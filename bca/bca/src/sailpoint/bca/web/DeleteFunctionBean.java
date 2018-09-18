package sailpoint.bca.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.api.Workflower;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.WorkflowUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.RoleAssignment;
import sailpoint.object.RoleTarget;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowLaunch;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import sailpoint.workflow.rule.BCAApproval;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DeleteFunctionBean extends AccountsRequestBean {

	String className = "::DeleteFunctionBean::";
	public static Logger logger = Logger.getLogger("sailpoint.bca.web.DeleteFunctionBean");

	public List<LinkApplication> accountFunctionList;

	public String userId;

	/**
	 * @return the accountFunctionList
	 */
	public List<LinkApplication> getAccountFunctionList() {
		return accountFunctionList;
	}

	/**
	 * @param accountFunctionList the accountFunctionList to set
	 */
	public void setAccountFunctionList(List<LinkApplication> accountFunctionList) {
		this.accountFunctionList = accountFunctionList;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public DeleteFunctionBean() {

		try {

			Identity identity = getIdentity();

			List lstAccounts = new ArrayList();

			List lstTemp = getAdMainframeLinks();

			List lstRole = null;

			Iterator tempLinkIterator = null;

			RoleAssignment rAssigment = null;

			List lstRoleTarget = null;

			Iterator roleTargetIterator = null;

			RoleTarget roleTarget = null;

			logger.debug(className + " identity with id " + identity.getDisplayName());

			Map preferenceMap = identity.getPreferences();

			if (lstTemp != null && preferenceMap != null) {
				lstRole = (List) preferenceMap.get("roleAssignments");

				tempLinkIterator = lstTemp.iterator();

				while (tempLinkIterator.hasNext() && lstRole != null) {
					logger.debug(className + "total loop app");
					LinkApplication linkApp = (LinkApplication) tempLinkIterator.next();

					List tempList = new ArrayList();
					List tempList2 = new ArrayList();

					logger.debug(className + " process userid " + linkApp.getUserId());

					Iterator iteratorRole = lstRole.iterator();

					while (iteratorRole.hasNext()) {
						logger.debug(className + "total loop assigment");
						rAssigment = (RoleAssignment) iteratorRole.next();

						lstRoleTarget = rAssigment.getTargets();

						if (lstRoleTarget != null) {
							roleTargetIterator = lstRoleTarget.iterator();

							while (roleTargetIterator.hasNext()) {
								logger.debug(className + "total loop target");
								roleTarget = (RoleTarget) roleTargetIterator.next();

								if (roleTarget.getApplicationName().equalsIgnoreCase("IBM MAINFRAME RACF")
										&& roleTarget.getNativeIdentity().equalsIgnoreCase(linkApp.getUserId())) {
									logger.debug(className + "matched user id " + linkApp.getUserId() + " with role "
											+ rAssigment.getRoleName());
									LinkRole linkRole = new LinkRole(rAssigment.getRoleName(),
											rAssigment.getAssignmentId());
									tempList.add(linkRole);
									List links = identity.getLinks();
									Iterator it = links.iterator();
									while (it.hasNext()) {
										Link link = (Link) it.next();
										if (link.getApplicationName().equalsIgnoreCase("IBM MAINFRAME RACF") && link
												.getDisplayName().equalsIgnoreCase(roleTarget.getNativeIdentity())) {
											Attributes att = link.getAttributes();
											List<String> groupList = att.getList("groups");
											// String defaultGroup = (String) att.get("UG_DEF");
											for (String groupLink : groupList) {
												LinkGroup linkGroup = new LinkGroup(roleTarget.getNativeIdentity(), "",
														groupLink);
												tempList2.add(linkGroup);
											}

										}
									}
								}

							}
						}
					}

					if (tempList != null && tempList.size() >= 1) {
						logger.debug(className + " fungsi lebih dari 1, dengan account " + linkApp.getUserId());
						linkApp.setLstEntGroup(tempList2);
						linkApp.setLstRoles(tempList);
						linkApp.setRoleSize(tempList.size());
						lstAccounts.add(linkApp);
					} else {
						logger.debug(className + " fungsi kurang dari 2, dengan account " + linkApp.getUserId());
					}
				}

			}

			setAccountFunctionList(lstAccounts);

		} catch (GeneralException e) {

			e.printStackTrace();
		}
	}

	private List getAdMainframeLinks() throws GeneralException {

		List lstRoles = new ArrayList();

		Identity emp = getIdentity();

		List links = emp.getLinks();

		Iterator it = links.iterator();

		while (it.hasNext()) {
			Link link = (Link) it.next();
			String appsName = link.getApplicationName();
			if (CommonUtil.AD_APPLICATION.equalsIgnoreCase(appsName)
					|| CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(appsName)) {
				LinkApplication linkApps = new LinkApplication();
				linkApps.setApplicationName(appsName);
				linkApps.setNativeIdentity(link.getNativeIdentity());
				linkApps.setInstance(link.getInstance());
				if (CommonUtil.AD_APPLICATION.equalsIgnoreCase(appsName)) {
					linkApps.setUserId((String) link.getAttribute(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME));
				} else {
					linkApps.setUserId(link.getNativeIdentity());
				}
				lstRoles.add(linkApps);
			}
		}

		return lstRoles;
	}

	public String submitRequest() {

		String methodName = "::submitRequest::";

		String retVal = "success";

		logger.debug(methodName + " called");

		List targetList = null;

		if (getAccountFunctionList() != null) {
			Iterator i = getAccountFunctionList().iterator();
			targetList = new ArrayList();
			while (i.hasNext()) {
				LinkApplication linkApps = (LinkApplication) i.next();
				targetList.add(linkApps);
			}
		}

		if (targetList != null && targetList.size() > 0) {

			try {
				invokeWorkflow(getContext(), getIdentity(), targetList);
			} catch (GeneralException e) {

				retVal = "failed";
				e.printStackTrace();
			}
		}

		return retVal;

	}

	private void invokeWorkflow(SailPointContext context, Identity employee, List targetApplication)
			throws GeneralException {

		String methodName = "::invokeWorkflow::";

		final String WORKFLOW_NAME = "BCA Base LCM Provisioning";

		ProvisioningPlan plan = new ProvisioningPlan();

		Iterator it = targetApplication.iterator();

		logger.debug(className + methodName + " Size of app to invoke " + targetApplication.size());

		while (it.hasNext()) {

			LinkApplication linkApps = (LinkApplication) it.next();

			// mainframe . -> clean iiq after provisioning
			logger.debug(className + methodName + "User Id include to workflow is:" + linkApps.getUserId());
			logger.debug(className + methodName + "nativeIdentity is : " + linkApps.getNativeIdentity());

			boolean isAddToPlan = false;

			AccountRequest accReq = new AccountRequest();

			accReq.setApplication(linkApps.getApplicationName());

			accReq.setInstance(linkApps.getInstance());

			accReq.setNativeIdentity(linkApps.getNativeIdentity());

			accReq.setOperation(AccountRequest.Operation.Modify);

			List roleList = linkApps.getLstRoles();
			List groupList = linkApps.getLstEntGroup();

			if (roleList != null) {

				Iterator roleListIterator = roleList.iterator();

				List attrReqList = new ArrayList();

				while (roleListIterator.hasNext()) {
					LinkRole linkRole = (LinkRole) roleListIterator.next();

					if (linkRole.isChecked()) {
						logger.debug(className + methodName + " role checked " + linkRole.getRoleName());

						AttributeRequest attReq = new AttributeRequest();
						attReq.setAssignmentId(linkRole.getAssignmentId());
						attReq.setName(ProvisioningPlan.ATT_IIQ_ASSIGNED_ROLES);
						attReq.setValue(linkRole.getRoleName());
						attReq.setOp(ProvisioningPlan.Operation.Remove);

						if (groupList != null) {
							Iterator groupListIterator = groupList.iterator();
							while (groupListIterator.hasNext()) {
								LinkGroup linkGroup = (LinkGroup) groupListIterator.next();
								if (linkApps.getNativeIdentity().equalsIgnoreCase(linkGroup.getGroupIdentity())) {
									logger.debug(className + "Insert penghapusa");
									AttributeRequest attReqEntitlement = new AttributeRequest();
									attReqEntitlement.setName("EntitlementGroup");
									attReqEntitlement.setTrackingId(linkGroup.getGroupId());
									attReqEntitlement.setValue(linkGroup.getGroupIdentity());
									attReqEntitlement.setOp(ProvisioningPlan.Operation.Remove);
									attrReqList.add(attReqEntitlement);
								}

							}

						}
						attrReqList.add(attReq);
					}
				}

				if (attrReqList != null && attrReqList.size() > 0) {
					isAddToPlan = true;
					accReq.setAttributeRequests(attrReqList);
				}
			}

			if (isAddToPlan) {
				plan.add(accReq);
			}

			logger.debug(className + methodName + "Enter the workflow with " + accReq.toXml());

		}

		logger.debug(className + methodName + "The Provisioning Plan after adding this account request looks this: "
				+ plan.toXml());

		// Sort out all the variables required for workflow invocation....

		if (plan.getAccountRequests() != null && plan.getAccountRequests().size() > 0) {

			logger.debug(className + methodName + " invoke workflows");

			Workflow baseWf = new Workflow();

			Attributes attributes = new Attributes();

			HashMap launchArgsMap = WorkflowUtil.getLaunchArgsMap(employee, getLoggedInUser(), plan,
					BCAApproval.FLOW_TYPE_ACCOUNTS_REQUEST);

			logger.debug(className + methodName + launchArgsMap);
			attributes.putAll(launchArgsMap);

			baseWf.setVariables(attributes);

			WorkflowLaunch wflaunch = new WorkflowLaunch();

			Workflow wf = (Workflow) context.getObjectByName(Workflow.class, WORKFLOW_NAME);

			wflaunch.setWorkflowName(wf.getName());

			wflaunch.setWorkflowRef(wf.getName());

			logger.debug(className + methodName + "Check point WF 1 " + launchArgsMap.size());

			wflaunch.setCaseName("Account Deactivation for "

					+ employee.getDisplayName() + " :: " + new Date());

			wflaunch.setVariables(launchArgsMap);

			Workflower workflower = new Workflower(context);

			logger.debug(className + methodName + "");

			WorkflowLaunch launch = workflower.launch(wflaunch);

			String workFlowId = launch.getWorkflowCase().getId();

			logger.debug(className + methodName + "Workflow got launched with workflow id: " + workFlowId);
		}
	}
}

/*
 * package sailpoint.bca.web;
 * 
 * import java.util.ArrayList; import java.util.Date; import java.util.HashMap;
 * import java.util.Iterator; import java.util.List; import java.util.Map;
 * import org.apache.log4j.Logger; import sailpoint.api.SailPointContext; import
 * sailpoint.api.Workflower; import sailpoint.common.ActiveDirectoryAttribute;
 * import sailpoint.common.CommonUtil; import sailpoint.common.WorkflowUtil;
 * import sailpoint.object.Attributes; import sailpoint.object.EntitlementGroup;
 * import sailpoint.object.Identity; import sailpoint.object.Link; import
 * sailpoint.object.ProvisioningPlan; import
 * sailpoint.object.ProvisioningPlan.AccountRequest; import
 * sailpoint.object.ProvisioningPlan.AttributeRequest; import
 * sailpoint.object.RoleAssignment; import sailpoint.object.RoleTarget; import
 * sailpoint.object.Workflow; import sailpoint.object.WorkflowLaunch; import
 * sailpoint.tools.GeneralException; import
 * sailpoint.web.lcm.AccountsRequestBean; import
 * sailpoint.workflow.rule.BCAApproval;
 * 
 * @SuppressWarnings({ "rawtypes", "unchecked" }) public class
 * DeleteFunctionBean extends AccountsRequestBean{
 * 
 * String className = "::DeleteFunctionBean::"; public static Logger logger =
 * Logger.getLogger("sailpoint.bca.web.DeleteFunctionBean");
 * 
 * public List<LinkApplication> accountFunctionList;
 * 
 * public String userId;
 * 
 * public List<LinkApplication> getAccountFunctionList() { return
 * accountFunctionList; }
 * 
 * public void setAccountFunctionList(List<LinkApplication> accountFunctionList)
 * { this.accountFunctionList = accountFunctionList; }
 * 
 * public void setUserId(String userId) { this.userId = userId; }
 * 
 * public DeleteFunctionBean() {
 * 
 * try {
 * 
 * Identity identity = getIdentity();
 * 
 * List lstAccounts = new ArrayList();
 * 
 * List lstTemp = getAdMainframeLinks();
 * 
 * logger.debug(className + " identity with id " + identity.getDisplayName());
 * 
 * Map preferenceMap = identity.getPreferences(); List exceptions =
 * identity.getExceptions(); Iterator EntitlementGroupIterator =
 * exceptions.iterator(); List lstRole = null; Iterator tempLinkIterator = null;
 * EntitlementGroup enttGroup = null; Attributes attrGroup = null; Map
 * attrMapGroup = null; List lstGroup = null; LinkApplication linkApp = null;
 * List tempList = new ArrayList(); List tempList2 = new ArrayList(); Iterator
 * iteratorRole = null; RoleAssignment rAssignment = null; List lstRoleTarget =
 * null; RoleTarget roleTarget = null;
 * 
 * while(EntitlementGroupIterator.hasNext()) { enttGroup =
 * (EntitlementGroup)EntitlementGroupIterator.next(); attrGroup = (Attributes)
 * enttGroup.getAttributes(); attrMapGroup = attrGroup.getMap(); lstGroup =
 * (List) attrMapGroup.get("groups");
 * 
 * } String group = lstGroup.toString().replace("[", "").replace("]", "");
 * 
 * if(lstTemp != null && preferenceMap != null){ lstRole = (List)
 * preferenceMap.get("roleAssignments"); iteratorRole = lstRole.iterator();
 * tempLinkIterator = lstTemp.iterator();
 * 
 * while(tempLinkIterator.hasNext() && lstRole != null){
 * 
 * linkApp = (LinkApplication)tempLinkIterator.next();
 * 
 * while(iteratorRole.hasNext()){
 * 
 * rAssignment = (RoleAssignment)iteratorRole.next();
 * 
 * lstRoleTarget = rAssignment.getTargets();
 * 
 * if(lstRoleTarget != null){ Iterator roleTargetIterator =
 * lstRoleTarget.iterator();
 * 
 * while(roleTargetIterator.hasNext()){
 * 
 * roleTarget = (RoleTarget)roleTargetIterator.next();
 * if(roleTarget.getApplicationName().equalsIgnoreCase("IBM MAINFRAME RACF") &&
 * roleTarget.getNativeIdentity().equalsIgnoreCase(linkApp.getUserId())){
 * 
 * logger.debug(className + "matched user id " + linkApp.getUserId() +
 * " with role " + rAssignment.getRoleName()); LinkRole linkRole = new
 * LinkRole(rAssignment.getRoleName(), rAssignment.getAssignmentId());
 * tempList.add(linkRole); } } } } logger.debug(className + "total list user : "
 * + tempList.size()); if(tempList != null && tempList.size() >= 1){
 * logger.debug(className + " fungsi lebih dari 1, dengan account " +
 * linkApp.getUserId()); linkApp.setLstEntGroup(tempList2);
 * linkApp.setLstRoles(tempList); linkApp.setRoleSize(tempList.size());
 * lstAccounts.add(linkApp);
 * 
 * }else{ logger.debug(className + " fungsi kurang dari 2, dengan account " +
 * linkApp.getUserId()); } } logger.debug(className + "entry map entitlement :"
 * + attrMapGroup.toString()); }
 * 
 * if(enttGroup.getNativeIdentity().equalsIgnoreCase(roleTarget.
 * getNativeIdentity())) { LinkGroup linkGroup = new
 * LinkGroup(enttGroup.getNativeIdentity(), enttGroup.getId(), group);
 * tempList2.add(linkGroup); } logger.debug(className +
 * " list yang ditampilkan : " + lstAccounts.size());
 * setAccountFunctionList(lstAccounts);
 * 
 * } catch (GeneralException e) {
 * 
 * e.printStackTrace(); } }
 * 
 * private List getAdMainframeLinks() throws GeneralException{
 * 
 * List lstRoles = new ArrayList();
 * 
 * Identity emp = getIdentity();
 * 
 * List links = emp.getLinks();
 * 
 * Iterator it = links.iterator();
 * 
 * while(it.hasNext()){ Link link = (Link)it.next(); logger.debug(className +
 * "link identity : " + link.getNativeIdentity()); String appsName =
 * link.getApplicationName();
 * if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(appsName) ||
 * CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(appsName)){ LinkApplication
 * linkApps = new LinkApplication(); linkApps.setApplicationName(appsName);
 * linkApps.setNativeIdentity(link.getNativeIdentity());
 * linkApps.setInstance(link.getInstance());
 * if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(appsName)){
 * linkApps.setUserId((String)link.getAttribute(ActiveDirectoryAttribute.
 * SAM_ACCOUNT_NAME)); }else{ linkApps.setUserId(link.getNativeIdentity()); }
 * lstRoles.add(linkApps); } }
 * 
 * return lstRoles; }
 * 
 * public String submitRequest(){
 * 
 * String methodName = "::submitRequest::";
 * 
 * String retVal = "success";
 * 
 * logger.debug(methodName + " called");
 * 
 * List targetList = null;
 * 
 * if(getAccountFunctionList() != null){ Iterator i =
 * getAccountFunctionList().iterator(); targetList = new ArrayList();
 * while(i.hasNext()){ LinkApplication linkApps = (LinkApplication)i.next();
 * 
 * logger.debug(className + methodName + " account checked " +
 * linkApps.getUserId());
 * 
 * targetList.add(linkApps);
 * 
 * }
 * 
 * }
 * 
 * if(targetList != null && targetList.size() > 0){
 * 
 * try { invokeWorkflow(getContext(), getIdentity(), targetList); } catch
 * (GeneralException e) {
 * 
 * retVal = "failed"; e.printStackTrace(); } }
 * 
 * return retVal;
 * 
 * }
 * 
 * private void invokeWorkflow(SailPointContext context, Identity employee, List
 * targetApplication) throws GeneralException{
 * 
 * String methodName = "::invokeWorkflow::";
 * 
 * final String WORKFLOW_NAME = "BCA Base LCM Provisioning";
 * 
 * ProvisioningPlan plan = new ProvisioningPlan();
 * 
 * Iterator it = targetApplication.iterator();
 * 
 * logger.debug(className + methodName + " Size of app to invoke "+
 * targetApplication.size());
 * 
 * while(it.hasNext()){
 * 
 * LinkApplication linkApps = (LinkApplication)it.next();
 * 
 * logger.debug(className + methodName + "User Id include to workflow is:"
 * +linkApps.getUserId()); logger.debug(className + methodName +
 * "nativeIdentity is : " + linkApps.getNativeIdentity());
 * 
 * boolean isAddToPlan = false;
 * 
 * AccountRequest accReq = new AccountRequest();
 * 
 * accReq.setApplication(linkApps.getApplicationName());
 * 
 * accReq.setInstance(linkApps.getInstance());
 * 
 * accReq.setNativeIdentity(linkApps.getNativeIdentity());
 * 
 * accReq.setOperation(AccountRequest.Operation.Modify);
 * 
 * List roleList = linkApps.getLstRoles(); List groupList =
 * linkApps.getLstEntGroup();
 * 
 * if(roleList != null){
 * 
 * Iterator roleListIterator = roleList.iterator();
 * 
 * List attrReqList = new ArrayList();
 * 
 * while(roleListIterator.hasNext()){ LinkRole linkRole = (LinkRole)
 * roleListIterator.next();
 * 
 * if(linkRole.isChecked()){ logger.debug(className + methodName +
 * " role checked " + linkRole.getRoleName());
 * 
 * AttributeRequest attReq = new AttributeRequest();
 * attReq.setAssignmentId(linkRole.getAssignmentId());
 * attReq.setName(ProvisioningPlan.ATT_IIQ_ASSIGNED_ROLES);
 * attReq.setValue(linkRole.getRoleName());
 * attReq.setOp(ProvisioningPlan.Operation.Remove);
 * 
 * if(groupList != null) { Iterator groupListIterator = groupList.iterator();
 * while(groupListIterator.hasNext()) { LinkGroup linkGroup = (LinkGroup)
 * groupListIterator.next();
 * if(linkApps.getNativeIdentity().equalsIgnoreCase(linkGroup.getGroupIdentity()
 * )) { logger.debug(className + "Insert penghapusa"); AttributeRequest
 * attReqEntitlement = new AttributeRequest();
 * attReqEntitlement.setName("EntitlementGroup");
 * attReqEntitlement.setTrackingId(linkGroup.getGroupId());
 * attReqEntitlement.setValue(linkGroup.getGroupIdentity());
 * attReqEntitlement.setOp(ProvisioningPlan.Operation.Remove);
 * attrReqList.add(attReqEntitlement); }
 * 
 * }
 * 
 * } attrReqList.add(attReq); } }
 * 
 * if(attrReqList != null && attrReqList.size() > 0){ isAddToPlan = true;
 * accReq.setAttributeRequests(attrReqList); } }
 * 
 * if(isAddToPlan){ plan.add(accReq); }
 * 
 * 
 * logger.debug(className + methodName + "Enter the workflow with " +
 * accReq.toXml());
 * 
 * }
 * 
 * 
 * logger.debug(className + methodName +
 * "The Provisioning Plan after adding this account request looks this: " +
 * plan.toXml());
 * 
 * 
 * logger.debug(className + methodName + " invoke workflows");
 * 
 * Workflow baseWf = new Workflow();
 * 
 * Attributes attributes = new Attributes();
 * 
 * HashMap launchArgsMap = WorkflowUtil.getLaunchArgsMap(employee,
 * getLoggedInUser(), plan, BCAApproval.FLOW_TYPE_PENGURANGAN);
 * 
 * logger.debug(className + methodName + launchArgsMap);
 * attributes.putAll(launchArgsMap);
 * 
 * baseWf.setVariables(attributes);
 * 
 * WorkflowLaunch wflaunch = new WorkflowLaunch();
 * 
 * Workflow wf = (Workflow) context.getObjectByName(Workflow.class,
 * WORKFLOW_NAME);
 * 
 * wflaunch.setWorkflowName(wf.getName());
 * 
 * wflaunch.setWorkflowRef(wf.getName());
 * 
 * logger.debug(className + methodName + "Check point WF 1 " +
 * launchArgsMap.size());
 * 
 * wflaunch.setCaseName("Account Deactivation for "
 * 
 * + employee.getDisplayName() + " :: " + new Date());
 * 
 * wflaunch.setVariables(launchArgsMap);
 * 
 * Workflower workflower = new Workflower(context);
 * 
 * logger.debug(className + methodName + "");
 * 
 * WorkflowLaunch launch = workflower.launch(wflaunch);
 * 
 * String workFlowId = launch.getWorkflowCase().getId();
 * 
 * logger.debug(className + methodName +
 * "Workflow got launched with workflow id: " + workFlowId); } }
 */