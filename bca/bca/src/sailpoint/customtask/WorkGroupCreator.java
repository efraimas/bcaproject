package sailpoint.customtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import sailpoint.api.AggregationLogger.LogEntry;
import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.object.Capability;
import sailpoint.object.Identity;
import sailpoint.object.Identity.WorkgroupNotificationOption;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class WorkGroupCreator {

	public static String CLASS_NAME = "::WorkGroupCreator::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.WorkGroupCreator");

	public static Identity createWorkGroup(SailPointContext context,String name, String desc) throws GeneralException {
		String Method_Name = "::createWorkGroup::";
		Identity identity = null;

		identity = context.getObjectByName(Identity.class, name);

		if (identity == null) {

			identity = new Identity();

			identity.setName(name);

			identity.setDisplayName(name);

			identity.setDescription(desc);

			identity.setOwner(context.getObjectByName(Identity.class, "spadmin"));

			identity.setWorkgroup(true);

			Map<String, Object> preferenceMap = new HashMap<String, Object>();

			preferenceMap.put("workgroupNotificationOption", WorkgroupNotificationOption.Both);
			identity.setPreferences(preferenceMap);

			context.saveObject(identity);
			context.commitTransaction();

		}

		return identity;

	}

	public static void createUpdateWorkGroup(Iterator<?> it, SailPointContext context, String workGroupName,
			String workGroupDescription, String groupMail, Capability capability) throws GeneralException {
		
		String Method_Name = "::createUpdateWorkGroup::";

		while (it.hasNext()) {
			Identity identity = (Identity) it.next();
			createUpdateWorkGroup(identity, context, workGroupName, workGroupDescription, groupMail, capability);
		}
	}
	
//	public static void cleanWorkgroup(Identity identity, SailPointContext context, String workGroupName, String workGroupDescription, String groupMail, Capability capability) throws GeneralException {
//		String METHOD_NAME = "::cleanWorkgroup::";
//		
//		logger.debug(CLASS_NAME + METHOD_NAME + "Try delete workgroup in Identity Name : " + identity.getDisplayName());
//		List<Identity> wgList = null;
//		try{
//			logger.debug(CLASS_NAME + METHOD_NAME + "Inside clean Identity ");
//				wgList = identity.getWorkgroups();
//		}catch (Exception e) {
//			// TODO: handle exception
//		}
//		try {
//			logger.debug(CLASS_NAME + METHOD_NAME + "wglistOri " + wgList.toString());
//				if(wgList.toString().contains(workGroupName)){
//					logger.debug(CLASS_NAME + METHOD_NAME + "Inside clear workGroupName : " + wgList.toString());
//					for(int i = 0; i<wgList.size(); i++){
//						wgList.get(i).toString();
//						wgList.subList(i, i + 1).clear();
//					}
//				}
//	}catch (Exception e) {
//		logger.debug("tidak bisa mengenali workgroup");
//	}
//		context.saveObject(identity);
//		context.commitTransaction();
//		context.decache();
//	}

	public static void createUpdateWorkGroup(Identity identity, SailPointContext context, String workGroupName, String workGroupDescription, String groupMail, Capability capability) throws GeneralException {
		String Method_Name = "::createUpdateWorkGroup::";
		logger.debug(CLASS_NAME  + Method_Name + " workgroup name : " + workGroupName);
	   	
		Map<String, Object> preferenceMap = new HashMap<String, Object>();
		preferenceMap.put("workgroupNotificationOption", WorkgroupNotificationOption.Both);
		Identity wg = CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupName);	
		try{
			capability = context.getObjectByName(Capability.class, "ReportViewer");
			logger.debug(CLASS_NAME + Method_Name + "capability :" + capability.toXml());
		}catch (Exception e) {
			capability = null;
			logger.debug(CLASS_NAME + Method_Name + "capability tida ditemukan");
		}
		
		List listCapability = new ArrayList<>();
		listCapability.add(capability);
	
		if (wg == null) {
			logger.debug(CLASS_NAME + Method_Name + " Create new WorkgroupName :" + workGroupName + " is not exists");
			wg = WorkGroupCreator.createWorkGroup(context, workGroupName, workGroupDescription);
			List<Identity> wgList = identity.getWorkgroups();
			

			if (!Hibernate.isInitialized(wgList)) {
				Hibernate.initialize(wgList);
			}
			if (wgList == null) {
				wgList = new ArrayList<Identity>();
			}
			wgList.add(wg);
			identity.setWorkgroups(wgList);
			wg.setEmail(groupMail);
			wg.setPreferences(preferenceMap);
			try{
				logger.debug(Method_Name + "nama group nya " + wg.getDisplayName());
				if(workGroupName.contains("Security Administrators")){
					logger.debug(CLASS_NAME + Method_Name + "Sukses Inside capability");
					wg.setCapabilities(listCapability);
				}else{
					logger.debug(CLASS_NAME + Method_Name + "TidAK Inside capability");
					wg.setCapabilities(null);
				}
			}catch (Exception e1) {
				logger.debug(CLASS_NAME + Method_Name + "Gagal Inside capability");
				// TODO: handle exception
			}
		}/*else{
			logger.debug(CLASS_NAME + Method_Name + " Update WorkgroupName :" + workGroupName);
			
			List<Identity> wgList = identity.getWorkgroups();
			if (!Hibernate.isInitialized(wgList)) {
				Hibernate.initialize(wgList);
			}

			if (wgList == null) {
				identity.setWorkgroups(wgList);
			} else {
				if(wgList.contains(wg)){
					logger.debug("inside");
				}else{
					wgList.add(wg);
					identity.setWorkgroups(wgList);
					logger.debug("inside");
				}
			}
			
			wg.setEmail(groupMail);
			wg.setPreferences(preferenceMap);
			try{
				logger.debug(Method_Name + "nama group nya " + wg.getDisplayName());
				if(workGroupName.contains("Security Administrators")){
					logger.debug(CLASS_NAME + Method_Name + "Sukses Inside capability");
					wg.setCapabilities(listCapability);
				}
			}catch (Exception e) {
				logger.debug(CLASS_NAME + Method_Name + "Gagal Inside capability");
				// TODO: handle exception
			}
		}*/
		context.saveObject(identity);
		context.commitTransaction();
		context.decache();
	}

	public static void createUpdateWorkGroup(List<?> lst,
			SailPointContext context, String workGroupName,
			String workGroupDescription, String groupMail, Capability capability) throws GeneralException {
		String Method_Name = "::createUpdateWorkGroup::";
		createUpdateWorkGroup(lst.iterator(), context, workGroupName,
				workGroupDescription, groupMail, capability);
	}

}
