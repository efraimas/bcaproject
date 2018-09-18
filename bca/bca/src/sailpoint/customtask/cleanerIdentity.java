package sailpoint.customtask;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.BranchUtil;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

@SuppressWarnings({"rawtypes"})
public class cleanerIdentity extends AbstractTaskExecutor{

	public static String CLASS_NAME = "::cleanerIdentity::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.cleanerIdentity");
	
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
		String workGroupName = "";
		
		if(CommonUtil.isRegionalBranchCode(context, branchCode)){
			workGroupName = "KANWIL " + branchCode + " Head Approvers";
		}else if(CommonUtil.isMainBranchCode(context, branchCode)){
			workGroupName = "KCU " + branchCode + " Head Approvers";
		}else if(CommonUtil.isSubBranchCode(context, branchCode)){
			workGroupName = "KCP " + branchCode + " Head Approvers";
		}
		logger.debug(CLASS_NAME + METHOD_NAME + "Workgroup Name : " + workGroupName);
		
		QueryOptions qo = new QueryOptions();
		qo.addFilter(Filter.eq(IdentityAttribute.BRANCH_CODE, branchCode));
		Iterator it = context.search(Identity.class, qo);
		
		while(it.hasNext()){
			identity = (Identity) it.next();
			
			try{
				logger.debug(CLASS_NAME + METHOD_NAME + "Inside clean Identity ");
				wgList = identity.getWorkgroups();
				for(int i = 0; i<wgList.size(); i++){
					wgList.get(i).toString();
					if(wgList.toString().contains(workGroupName)){
						logger.debug(CLASS_NAME + METHOD_NAME + "Inside clear wgList" + wgList.toString());
						wgList.subList(i, i + 1).clear();;
					}
				}
				
				identity.setWorkgroups(wgList);
				context.saveObject(identity);
				context.commitTransaction();
			
				logger.debug(CLASS_NAME + METHOD_NAME + "Wglist : " + wgList.toString());
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
		
		if(workGroupName != null){
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside.. penghapusan workgroup");
			Identity wg = null;
			try{
				wg = CommonUtil.getWorkgroupFromWorkGroupName(context, workGroupName);
			}catch (Exception e) {
				// TODO: handle exception
			}
			context.removeObject(wg);
			context.startTransaction();
			context.commitTransaction();
			context.decache();
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
