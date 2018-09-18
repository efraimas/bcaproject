package sailpoint.customtask;

import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.BranchUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Capability;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

public class GenerateWorkgroupHeadApproverNew extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::GenerateWorkgroupHeadApproverNew::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.GenerateWorkgroupHeadApproverNew");
	
	private static final String WAPIM_PIMPINAN_KCP = "00005205,00075671,00005198,00005204";	
	private static final String KOC_KPBC_KEPALA_KCU = "00005196,00005197,00005134,00005229,00005193,00005194";
	private static final String KOW_KEPALA_SLA_KANWIL = "00107657,00380271,00251216";
	@Override
	public void execute(SailPointContext ctx, TaskSchedule arg1, TaskResult arg2, Attributes<String, Object> attr)
			throws Exception {
		// TODO Auto-generated method stub
		if(attr.get("branchCode") != null){
			String input = (String)attr.get("branchCode");
			logger.debug(CLASS_NAME + " branchcode that will be processed " + input);
			
			String groupMail = null;
			String branchCode = "";
			Capability capability = null;
			if("all".equalsIgnoreCase(input)){
				
				logger.debug(CLASS_NAME + " will process all branch");
				
					List branches = BranchUtil.getAllBranchCode(ctx);
					
					if(branches != null && branches.size() > 0){
						logger.debug("branch tidak kosong");
						
						int listLength = branches.size();
						for(int bob=0; bob<listLength; bob++){
							branchCode  = (String)branches.get(bob);
							logger.debug(CLASS_NAME + " process branch" + branchCode);
							executeWorkGroupByBranch(ctx, branchCode, groupMail, capability);
						}
					}
				
			}else{
				String[] arrBranchCode = input.split(",");
				int len = arrBranchCode.length;
				for(int i=0; i<len; i++){
					branchCode = arrBranchCode[i].trim();
					logger.debug(CLASS_NAME + " Branch Code : " + branchCode);
					
					executeWorkGroupByBranch(ctx, branchCode, groupMail, capability);
				}
			}
			
			
		}else{
			logger.debug(CLASS_NAME + " branchCode argument's not found");
		}
	}
	private void executeWorkGroupByBranch(SailPointContext ctx, String branchCode, String groupMail, Capability capability) throws GeneralException{
		
	}
	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
