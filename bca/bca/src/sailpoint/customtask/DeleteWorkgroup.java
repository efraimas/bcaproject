package sailpoint.customtask;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.BranchUtil;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityStatus;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

@SuppressWarnings({"rawtypes"})
public class DeleteWorkgroup extends AbstractTaskExecutor{

	public static String CLASS_NAME = "::DeleteWorkgroup::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.DeleteWorkgroup");
	
	@Override
	public void execute(SailPointContext context, TaskSchedule arg1, TaskResult arg2, Attributes<String, Object> arg3)
			throws Exception {
		// TODO Auto-generated method stub
		if(arg3.get("branchCode") != null){
			String input = (String)arg3.get("branchCode");
			logger.debug(CLASS_NAME + " branchcode that will be processed " + input);
			
			String branchCode = "";
			if("all".equalsIgnoreCase(input)){
				
				logger.debug(CLASS_NAME + " will process all branch");
				
					List branches = BranchUtil.getAllBranchCode(context);
					
					if(branches != null && branches.size() > 0){
						logger.debug("branch tidak kosong");
						
						int listLength = branches.size();
						for(int bob=0; bob<listLength; bob++){
							branchCode  = (String)branches.get(bob);
							logger.debug(CLASS_NAME + " process branch" + branchCode);
							
							executeCleaner(context, branchCode);
						}
					}
			}else{
				String[] arrBranchCode = input.split(",");
				int len = arrBranchCode.length;
				for(int i=0; i<len; i++){
					branchCode = arrBranchCode[i].trim();
					logger.debug(CLASS_NAME + " Branch Code : " + branchCode);
					
					executeCleaner(context, branchCode);
				}
			}
		}else{
			logger.debug(CLASS_NAME + " branchCode argument's not found");
		}
	}
	
	private void executeCleaner(SailPointContext context, String branchCode) throws GeneralException{
		String METHOD_NAME = "::executeCleaner::";
		
		List<Identity> wgList = null;
		Identity identity = null;
		String workGroupNameHeadApprover = "";
		String workGroupNameSecurityAdministrator = "";
		
		if(CommonUtil.isRegionalBranchCode(context, branchCode)){
			workGroupNameHeadApprover = "KANWIL " + branchCode + " Head Approvers";
			workGroupNameSecurityAdministrator = "KANWIL " + branchCode + " Security Administrators";
		}else if(CommonUtil.isMainBranchCode(context, branchCode)){
			workGroupNameHeadApprover = "KCU " + branchCode + " Head Approvers";
			workGroupNameSecurityAdministrator = "KCU " + branchCode + " Security Administrators";
		}else if(CommonUtil.isSubBranchCode(context, branchCode)){
			workGroupNameHeadApprover = "KCP " + branchCode + " Head Approvers";
			workGroupNameSecurityAdministrator = "KCP " + branchCode + " Security Administrators";
		}
		logger.debug(CLASS_NAME + METHOD_NAME + "Workgroup Name Head Approver : " + workGroupNameHeadApprover + "Workgroup Name Security Administrator : " + workGroupNameSecurityAdministrator);
		
		String workGroupManagerCadangan = "Manager Cadangan " + branchCode;
		logger.debug(CLASS_NAME + METHOD_NAME + "Workgroup Name Manager Cadangan : " + workGroupManagerCadangan);
		
		QueryOptions qo = new QueryOptions();
		qo.addFilter(Filter.eq(IdentityAttribute.BRANCH_CODE, branchCode));
		qo.addFilter(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.TERMINATED_EMPLOYEE));
		Iterator it = context.search(Identity.class, qo);
		
		while(it.hasNext()){
			identity = (Identity) it.next();
			logger.debug(CLASS_NAME + METHOD_NAME + "Try delete workgroup in Identity Name : " + identity.getDisplayName());
			try{
				logger.debug(CLASS_NAME + METHOD_NAME + "Inside clean Identity ");
				try{
					wgList = identity.getWorkgroups();
				}catch (Exception e) {
					// TODO: handle exception
				}
				
				for(int i = 0; i<wgList.size(); i++){
					wgList.get(i).toString();;
					logger.debug(CLASS_NAME + METHOD_NAME + "wglistOri " + wgList.toString());
					try{
						if(wgList.toString().contains(workGroupNameHeadApprover)){
							logger.debug(CLASS_NAME + METHOD_NAME + "Inside clear workGroupNameHeadApprover : " + wgList.toString());
							wgList.subList(i, i + 1).clear();
						}else if(wgList.toString().contains(workGroupNameSecurityAdministrator)){
							logger.debug(CLASS_NAME + METHOD_NAME + "Inside clear workGroupNameSecurityAdministrator : " + wgList.toString());
							wgList.subList(i, i + 1).clear();
						}else if(wgList.toString().contains(workGroupManagerCadangan)){
							logger.debug(CLASS_NAME + METHOD_NAME + "Inside clear workGroupManagerCadangan : " + wgList.toString());
							wgList.subList(i, i + 1).clear();
						}
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
				identity.setWorkgroups(wgList);
				context.saveObject(identity);
				context.commitTransaction();
		
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
		
		if(workGroupNameHeadApprover != null){
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside.. penghapusan workgroup Head Approver");
			Identity wg = null;
			try{
				wg = CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupNameHeadApprover);
				context.removeObject(wg);
				context.startTransaction();
				context.commitTransaction();
				context.decache();
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(workGroupNameSecurityAdministrator != null){
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside.. penghapusan workgroup Security Administrator");
			Identity wg = null;
			try{
				wg = CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupNameSecurityAdministrator);
				context.removeObject(wg);
				context.startTransaction();
				context.commitTransaction();
				context.decache();
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(workGroupManagerCadangan != null){
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside.. penghapusan workgroup Manager Cadangan");
			Identity wg = null;
			try{
				wg = CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupManagerCadangan);
				context.removeObject(wg);
				context.startTransaction();
				context.commitTransaction();
				context.decache();
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
		

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		// TODO Auto-generated method stub
		return false;
	}

}
