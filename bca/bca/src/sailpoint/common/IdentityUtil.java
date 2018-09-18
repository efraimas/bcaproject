package sailpoint.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.QueryOptions;
import sailpoint.object.RoleAssignment;
import sailpoint.object.RoleDetection;
import sailpoint.object.RoleTarget;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class IdentityUtil {

	public static String className = "::IdentityUtil::";
	public static Logger logger = Logger.getLogger("sailpoint.common.IdentityUtil");

	public static Identity searchIdentity(SailPointContext context, String attribute, String value)
			throws GeneralException {
		return CommonUtil.searchIdentity(context, attribute, value);
	}

	public static Identity searchActiveIdentityById(SailPointContext context, String id) throws GeneralException {

		Map args = new HashMap<String, String>();

		args.put(IdentityAttribute.EMPLOYEE_ID, id);

		Identity identity = (Identity) CommonUtil.getSingleObjectFromIterator(searchIdentity(context, args, true));

		return identity;
	}

	public static Iterator searchIdentity(SailPointContext context, Map args, boolean onlyActiveEmployee)
			throws GeneralException {

		QueryOptions qo = new QueryOptions();

		Iterator identityIterator = null;

		if (onlyActiveEmployee) {
			qo.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.INACTIVE_EMPLOYEE));
			qo.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.TERMINATED_EMPLOYEE));
		}

		if (args != null) {

			Set set = args.keySet();

			Iterator it = set.iterator();

			while (it.hasNext()) {
				String attribute = (String) it.next();
				String value = (String) args.get(attribute);

				qo.add(Filter.eq(attribute, value));

			}
		}

		CommonUtil.setIgnoreWorkgroupIndexes(qo);
		logger.debug("Qo will look like this : " + qo);
		identityIterator = context.search(Identity.class, qo);

		return identityIterator;

	}

	public static Iterator searchIdentityByQueryOptions(SailPointContext ctx, QueryOptions qo) throws GeneralException {

		Iterator identityIterator = null;

		CommonUtil.setIgnoreWorkgroupIndexes(qo);

		identityIterator = ctx.search(Identity.class, qo);

		return identityIterator;
	}

	public static Iterator<Identity> searchActiveIdentity(SailPointContext context, Map args) throws GeneralException {

		return searchIdentity(context, args, true);

	}

	public static boolean isMemberOfWorkGroup(Identity workgroup, Identity member) {

		List<Identity> wgList = member.getWorkgroups();
		if (wgList != null) {
			Iterator<Identity> it = wgList.iterator();
			while (it.hasNext()) {
				Identity identity = (Identity) it.next();
				if (identity.getId().equalsIgnoreCase(workgroup.getId())) {
					return true;
				}
			}
		}

		return false;
	}

	public static Identity getDirectManager(SailPointContext context, String employeeId) throws GeneralException {

		Identity manager = null;

		Identity employee = searchIdentity(context, IdentityAttribute.EMPLOYEE_ID, employeeId);

		manager = employee.getManager();

		return manager;

	}

	public static Iterator<Identity> getActiveBranchIdentityByPosition(SailPointContext context, String branchCode,
			String positionCode) throws GeneralException {

		Map filterMap = new HashMap();

		filterMap.put(IdentityAttribute.BRANCH_CODE, branchCode);
		filterMap.put(IdentityAttribute.POSITION_CODE, positionCode);

		return searchActiveIdentity(context, filterMap);

	}

	public static Identity getCadanganManager(SailPointContext context, String branchCode) throws GeneralException {

		Identity manager = null;

		return manager;
	}

	public static Link getAdLink(Identity identity) {

		String methodName = "::getAdLink::";

		Link link = null;

		List links = identity.getLinks();

		if (links != null) {
			Iterator it = links.iterator();

			while (it.hasNext()) {
				Link tempLink = (Link) it.next();
				logger.debug(className + methodName + " Application : " + tempLink.getApplicationName() + ", with id : "
						+ tempLink.getDisplayName());
				if (CommonUtil.AD_APPLICATION.equalsIgnoreCase(tempLink.getApplicationName())) {
					logger.debug(
							className + methodName + " AD application found, with id : " + tempLink.getDisplayName());
					return tempLink;
				}
			}
		}

		return link;
	}

	public static boolean isNeedBirthrightAdAccount(SailPointContext ctx, Identity identity) {

		return "Active Employee".equalsIgnoreCase((String) identity.getAttribute("status"))
				&& getAdLink(identity) == null;
	}

	public static String getAssignedRoleFromAccountId(Identity identity, String accountId) {

		String methodName = "::getAssignedRoleFromAccountId::";

		String roleName = "IBM MAINFRAME RACF";

		List lstRoleAssigned = (List) identity.getPreference("roleAssignments");

		if (lstRoleAssigned != null) {

			Iterator iteratorRoleAssigned = lstRoleAssigned.iterator();

			while (iteratorRoleAssigned.hasNext()) {

				logger.debug(className + methodName + " Enter roles iteratorRoleAssigned iterator with size "
						+ lstRoleAssigned.size());

				RoleAssignment roleAssigned = (RoleAssignment) iteratorRoleAssigned.next();

				logger.debug(
						className + methodName + " Enter roleAssigned with role name " + roleAssigned.getRoleName());

				List lstRoleTarget = roleAssigned.getTargets();

				if (lstRoleTarget != null && lstRoleTarget.size() > 0) {

					Iterator iteratorRoleTarget = lstRoleTarget.iterator();

					while (iteratorRoleTarget.hasNext()) {

						logger.debug(className + methodName + " Enter roles iteratorRoleTarget iterator");

						RoleTarget roleTarget = (RoleTarget) iteratorRoleTarget.next();

						logger.debug(className + methodName + roleTarget.getApplicationName() + "::"
								+ roleTarget.getNativeIdentity());

						if (CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(roleTarget.getApplicationName())
								&& accountId.equalsIgnoreCase(roleTarget.getNativeIdentity())) {

							roleName = roleAssigned.getRoleName();

							logger.debug(className + methodName + " detected role with id " + roleName);

							return roleName;
						}
					}

				}
			}
		}

		return roleName;
	}

	public static String getDetectionRoleFromAccountId(Identity identity, String accountId) {

		String methodName = "::getDetectionRoleFromAccountId::";

		String roleName = "IBM MAINFRAME RACF";

		List lstRoleDetected = (List) identity.getPreference("roleDetections");

		if (lstRoleDetected != null) {

			Iterator iteratorRoleDetected = lstRoleDetected.iterator();

			while (iteratorRoleDetected.hasNext()) {

				logger.debug(className + methodName + " Enter roles iteratorRoleDetected iterator with size "
						+ lstRoleDetected.size());

				RoleDetection roleDetected = (RoleDetection) iteratorRoleDetected.next();

				logger.debug(
						className + methodName + " Enter roleAssigned with role name " + roleDetected.getRoleName());

				List lstRoleTarget = roleDetected.getTargets();

				if (lstRoleTarget != null && lstRoleTarget.size() > 0) {

					Iterator iteratorRoleTarget = lstRoleTarget.iterator();

					while (iteratorRoleTarget.hasNext()) {

						logger.debug(className + methodName + " Enter roles iteratorRoleTarget iterator");

						RoleTarget roleTarget = (RoleTarget) iteratorRoleTarget.next();

						logger.debug(className + methodName + roleTarget.getApplicationName() + "::"
								+ roleTarget.getNativeIdentity());

						if (CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(roleTarget.getApplicationName())
								&& accountId.equalsIgnoreCase(roleTarget.getNativeIdentity())) {

							roleName = roleDetected.getRoleName();

							logger.debug(className + methodName + " detected role with id " + roleName);

							return roleName;
						}
					}

				}
			}
		}

		return roleName;
	}

	public static String getRoleNameFromEntitlementAccountId(SailPointContext context, Attributes att)
			throws GeneralException {
		String methodname = "::getRoleNameFromEntitlementAccountId::";
		String bcaApplicationName = null;
		Custom custom = CommonUtil.getCustomObject(context, CustomObject.BCA_APPLICATION_MAINFRAME);
		logger.debug(className + methodname + "Inside....");
		Map mapCustom = custom.getAttributes().getMap();

		// loop group from entitlement attributes

		String groupMap = null;
		List<String> listMap = null;
		
		if(att.getList("groups")!=null){
			listMap = att.getList("groups");
		}
		else if(att.getList("UG_DEF")!=null){
			listMap = att.getList("UG_DEF");
		}
		else if(att.getList("OWNER")!=null){
			listMap = att.getList("OWNER");
		}

		for (String nm : listMap) {
			groupMap = nm;
			if (mapCustom.get((String) groupMap) != null && groupMap != null) {
				bcaApplicationName = mapCustom.get((String) groupMap).toString();
				logger.debug("applicationName will be : " + bcaApplicationName);
				break;
			}
		}

		return bcaApplicationName;

	}

	public static boolean updateTempPasswordIdentityForGetPassword(SailPointContext context, Identity identity,
			String application, String accountId, String tanggal, String password) throws GeneralException {

		String methodName = "::updateTempPasswordIdentityForGetPassword::";
		accountId = accountId.toUpperCase();

		Map tempMap = null;// (Map)identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);

		Map detailMap = new HashMap();

		detailMap.put("date", tanggal);
		detailMap.put("accountid", accountId);
		detailMap.put("aplikasi", application);
		detailMap.put("password", context.encrypt(password));
		/* detailMap.put("password", password); */

		logger.debug(className + methodName + " detail sudah ditambahkan ke map");

		if (tempMap == null) {
			tempMap = new HashMap();
		}

		/* tempMap.put(application + "_" + accountId, detailMap); */

		tempMap.put(accountId, detailMap);

		logger.debug(className + methodName + " detail apps sudah ditambahkan dengan key " + accountId);

		// tempMap.put("create", createMap);

		logger.debug(className + methodName + " detail create apps sudah ditambahkan dengan key create");

		ProvisioningPlan planPass = new ProvisioningPlan();

		AccountRequest accReqPass = new AccountRequest();

		accReqPass.setApplication(CommonUtil.IIQ_APPLICATION);
		accReqPass.setNativeIdentity(identity.getName());
		accReqPass.setOperation(AccountRequest.Operation.Modify);

		AttributeRequest attrPass = new AttributeRequest();
		attrPass.setName(IdentityAttribute.TEMP_PASSWORD_APPS);
		attrPass.setValue(tempMap);
		attrPass.setOp(ProvisioningPlan.Operation.Add);

		List attrReqList = new ArrayList();

		attrReqList.add(attrPass);

		accReqPass.setAttributeRequests(attrReqList);

		planPass.setIdentity(identity);

		planPass.add(accReqPass);

		try {
			logger.debug(className + methodName + " plan untuk update temp password apps " + accReqPass.toXml());

			Provisioner p = new Provisioner(context);

			logger.debug(className + methodName + " preparation to compile the plan");

			ProvisioningProject project = p.compile(planPass);

			logger.debug(className + methodName + " preparation to execute the project");

			p.execute(project);

			logger.debug(className + methodName + " project has been executed for link " + identity.getDisplayName());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	// TEST

	public static boolean updateTempPasswordIdentityForResetPassword(SailPointContext context, Identity identity,
			String application, String accountId, String tanggal, String password) throws GeneralException {

		String methodName = "::updateTempPasswordIdentityForResetPassword::";
		accountId = accountId.toUpperCase();
		
		Map<String, Map> tempMap = new HashMap<>();
		List<Map> keylist = null;
		try {
			keylist = (List) identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
			for (Map key : keylist) {
				tempMap.putAll(key);
			}
		} catch (Exception e) {
			tempMap = (Map) identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
		}

		Map detailMap = new HashMap();

		detailMap.put("date", tanggal);
		detailMap.put("accountid", accountId);
		detailMap.put("aplikasi", application);
		detailMap.put("password", context.encrypt(password));
		/* detailMap.put("password", password); */

		logger.debug(className + methodName + " detail sudah ditambahkan ke map");

		if (tempMap == null) {
			tempMap = new HashMap();
		}

		/* tempMap.put(application + "_" + accountId, detailMap); */

		tempMap.put(accountId, detailMap);

		logger.debug(className + methodName + " detail apps sudah ditambahkan dengan key " + accountId);

		// tempMap.put("create", createMap);

		logger.debug(className + methodName + " detail create apps sudah ditambahkan dengan key create");

		ProvisioningPlan planPass = new ProvisioningPlan();

		AccountRequest accReqPass = new AccountRequest();

		accReqPass.setApplication(CommonUtil.IIQ_APPLICATION);
		accReqPass.setNativeIdentity(identity.getName());
		accReqPass.setOperation(AccountRequest.Operation.Modify);

		AttributeRequest attrPass = new AttributeRequest();
		attrPass.setName(IdentityAttribute.TEMP_PASSWORD_APPS);
		attrPass.setValue(tempMap);
		attrPass.setOp(ProvisioningPlan.Operation.Set);

		List attrReqList = new ArrayList();

		attrReqList.add(attrPass);

		accReqPass.setAttributeRequests(attrReqList);

		planPass.setIdentity(identity);

		planPass.add(accReqPass);

		try {
			logger.debug(className + methodName + " plan untuk update temp password apps " + accReqPass.toXml());

			Provisioner p = new Provisioner(context);

			logger.debug(className + methodName + " preparation to compile the plan");

			ProvisioningProject project = p.compile(planPass);

			logger.debug(className + methodName + " preparation to execute the project");

			p.execute(project);

			logger.debug(className + methodName + " project has been executed for link " + identity.getDisplayName());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean updateTempPasswordIdentityForDeletePassword(SailPointContext context, Identity identity,
			String accountId) throws GeneralException {

		String methodName = "::updateTempPasswordIdentityForDeletePassword::";

		Map<String, Map> tempMap = new HashMap<>();
		Map<String, Map> tempAttributeMap = new HashMap<>();
		List<Map> keylist = null;
		List<Map> keylistAttribute = null;
		try {
			keylist = (List) identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
			for (Map key : keylist) {
				tempMap.putAll(key);
			}
		} catch (Exception e) {
			tempMap = (Map) identity.getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
		}

		try {

			keylistAttribute = (List) identity.getAttributeMetaData(IdentityAttribute.TEMP_PASSWORD_APPS)
					.getLastValue();
			for (Map key : keylistAttribute) {
				tempAttributeMap.putAll(key);
			}
		} catch (Exception e) {
			tempAttributeMap = (Map) identity.getAttributeMetaData(IdentityAttribute.TEMP_PASSWORD_APPS).getLastValue();
			logger.debug("No lastValue from attribute meta data");
		}

		// logger.debug(className + methodName + "AttributeMetaData : " +
		// amd.toXml());
		logger.debug(className + methodName + "Map Before : " + tempMap);
		logger.debug(className + methodName + "List Before : " + keylistAttribute);
		logger.debug(className + methodName + "Map Att Before : " + tempAttributeMap);

		for (Iterator<Map.Entry<String, Map>> it = tempMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Map> entry = it.next();
			if (entry.getKey().equalsIgnoreCase(accountId)) {
				it.remove();
				break;
			}
		}

		for (Iterator<Map.Entry<String, Map>> it = tempAttributeMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Map> entry = it.next();
			if (entry.getKey().equalsIgnoreCase(accountId)) {
				it.remove();
				break;
			}
		}
		// tempAttributeMap.remove(accountId);

		// identity.remove(md);

		logger.debug(className + methodName + "Map After : " + tempMap);
		logger.debug(className + methodName + "Map Att After : " + tempAttributeMap);

		logger.debug(className + methodName + " detail apps dengan key " + accountId + " sudah di hapus");

		ProvisioningPlan planPass = new ProvisioningPlan();

		AccountRequest accReqPass = new AccountRequest();

		accReqPass.setApplication(CommonUtil.IIQ_APPLICATION);
		accReqPass.setNativeIdentity(identity.getName());
		accReqPass.setOperation(AccountRequest.Operation.Modify);

		AttributeRequest attrPass = new AttributeRequest();
		attrPass.setName(IdentityAttribute.TEMP_PASSWORD_APPS);
		attrPass.setValue(tempMap);
		attrPass.setOp(ProvisioningPlan.Operation.Set);

		List attrReqList = new ArrayList();

		attrReqList.add(attrPass);

		accReqPass.setAttributeRequests(attrReqList);

		planPass.setIdentity(identity);

		planPass.add(accReqPass);

		try {
			logger.debug(className + methodName + " plan untuk update temp password apps " + accReqPass.toXml());

			Provisioner p = new Provisioner(context);

			logger.debug(className + methodName + " preparation to compile the plan");

			ProvisioningProject project = p.compile(planPass);

			logger.debug(className + methodName + " preparation to execute the project");

			p.execute(project);

			logger.debug(className + methodName + " project has been executed for link " + identity.getDisplayName());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean updateIdentityAttribute(SailPointContext context, Identity identity, String key, String value)
			throws GeneralException {

		String methodName = "::updateIdentityAttribute::";

		logger.debug(className + methodName + " inside update identity attribute ..");

		ProvisioningPlan plan = new ProvisioningPlan();

		AccountRequest accReq = new AccountRequest();

		accReq.setApplication(CommonUtil.IIQ_APPLICATION);
		accReq.setNativeIdentity(identity.getName());
		accReq.setOperation(AccountRequest.Operation.Modify);

		AttributeRequest attrRq = new AttributeRequest();
		attrRq.setName(key);
		attrRq.setValue(value);
		attrRq.setOp(ProvisioningPlan.Operation.Set);

		List attrReqList = new ArrayList();

		attrReqList.add(attrRq);

		accReq.setAttributeRequests(attrReqList);

		plan.setIdentity(identity);

		plan.add(accReq);

		try {
			logger.debug(className + methodName + " plan untuk update temp password apps " + accReq.toXml());

			Provisioner p = new Provisioner(context);

			logger.debug(className + methodName + " preparation to compile the plan");

			ProvisioningProject project = p.compile(plan);

			logger.debug(className + methodName + " preparation to execute the project");

			p.execute(project);

			logger.debug(className + methodName + " project has been executed for link " + identity.getDisplayName());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// untuk perpanjangan id non karyawan
	public static Iterator<Identity> getNonEmployeeBranchIdentity(SailPointContext context, String branchCode)
			throws GeneralException {

		Map filterMap = new HashMap();

		filterMap.put(IdentityAttribute.BRANCH_CODE, branchCode);
		filterMap.put(IdentityAttribute.IS_HR_MANAGED, "false");

		return searchIdentity(context, filterMap, true);

	}

	// untuk penghapusan userid beserta fungsi
	public static boolean deleteAssignments(SailPointContext context, Identity identity, String accountId)
			throws GeneralException {

		String methodName = "::deleteAssignments::";
		ProvisioningPlan p = new ProvisioningPlan();
		List lstRoleAssigned = null;
		Iterator iteratorRoleAssigned = null;

		try {
			lstRoleAssigned = (List) identity.getPreference("roleAssignments");
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (lstRoleAssigned != null) {

			iteratorRoleAssigned = lstRoleAssigned.iterator();

			while (iteratorRoleAssigned.hasNext()) {

				RoleAssignment roleAssigned = (RoleAssignment) iteratorRoleAssigned.next();
				logger.debug(className + methodName + "Role Name : " + roleAssigned.getRoleName());
				List lstRoleTarget = null;
				try {
					lstRoleTarget = roleAssigned.getTargets();
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (lstRoleTarget != null && lstRoleTarget.size() > 0) {

					Iterator iteratorRoleTarget = lstRoleTarget.iterator();
					RoleTarget roleTarget = null;
					while (iteratorRoleTarget.hasNext()) {
						roleTarget = (RoleTarget) iteratorRoleTarget.next();
						logger.debug(className + methodName + "Role Target : " + roleTarget.getNativeIdentity());
					}
					if (accountId.trim().equalsIgnoreCase(roleTarget.getNativeIdentity().trim())) {
						logger.debug(className + methodName + "Inside penghapusan entitlements" + roleAssigned.toXml());

						List attrReqList = new ArrayList();
						AccountRequest accReq = new AccountRequest();
						accReq.setApplication(CommonUtil.IIQ_APPLICATION);
						accReq.setOperation(AccountRequest.Operation.Modify);
						accReq.addArgument("displayableName", roleAssigned.getRoleName());
						accReq.addArgument("name", roleAssigned.getRoleName());
						accReq.addArgument("roleId", roleAssigned.getRoleId());

						AttributeRequest attr = new AttributeRequest();
						attr.setName(ProvisioningPlan.ATT_IIQ_ASSIGNED_ROLES);
						attr.setValue(roleAssigned.getRoleName());
						attr.setOp(ProvisioningPlan.Operation.Remove);

						attrReqList.add(attr);
						accReq.setAttributeRequests(attrReqList);
						p.setIdentity(identity);
						p.add(accReq);
					}
				}
			}
		}

		try {
			logger.debug(className + methodName + " plan untuk delete Assignments " + p.toXml());

			Provisioner pr = new Provisioner(context);

			logger.debug(className + methodName + " preparation to compile the plan");

			ProvisioningProject project = pr.compile(p);

			logger.debug(className + methodName + " project to xml : " + project.toXml());

			logger.debug(className + methodName + " preparation to execute the project");

			pr.execute(project);

			logger.debug(className + methodName + " project has been executed for link " + identity.getDisplayName());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
