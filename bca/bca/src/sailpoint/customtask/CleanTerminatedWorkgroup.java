package sailpoint.customtask;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.BranchUtil;
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

@SuppressWarnings({ "unused", "rawtypes" })
public class CleanTerminatedWorkgroup extends AbstractTaskExecutor{

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
		
		QueryOptions qo = new QueryOptions();
		Identity identity = null;
		
		qo.addFilter(Filter.eq(IdentityAttribute.STATUS, IdentityStatus.TERMINATED_EMPLOYEE));
		qo.addFilter(Filter.eq(IdentityAttribute.BRANCH_CODE, branchCode));
		
		Iterator localIterator = context.search(Identity.class, qo);
		while(localIterator.hasNext()){
			identity = (Identity) localIterator.next();
			logger.debug("identitynya : " + identity.toXml());
			identity.setWorkgroups(null);
			
			context.saveObject(identity);
			context.commitTransaction();
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
