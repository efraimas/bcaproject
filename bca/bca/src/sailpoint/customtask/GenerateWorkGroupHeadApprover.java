package sailpoint.customtask;


import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.api.Terminator;
import sailpoint.common.BranchUtil;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Capability;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;

@SuppressWarnings({ "rawtypes" })
public class GenerateWorkGroupHeadApprover extends AbstractTaskExecutor{

	public static String CLASS_NAME = "::GenerateWorkGroupHeadApprover::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.GenerateWorkGroupHeadApprover");
	
	private static final String WAPIM_PIMPINAN_KCP = "00005205,00075671,00005198,00005204";
	private static final String KOC_KPBC_KEPALA_KCU = "00005196,00005197,00005134,00005229,00005193,00005194";
	private static final String KOW_KEPALA_SLA_KANWIL = "00107657,00380271,00251216";
	
	@Override
	public void execute(SailPointContext context, TaskSchedule arg1,
			TaskResult arg2, Attributes<String, Object> arg3) throws Exception {
		
		if(arg3.get("branchCode") != null){
			String input = (String)arg3.get("branchCode");
			logger.debug(CLASS_NAME + " branchcode that will be processed " + input);
			
			String groupMail = null;
			String branchCode = "";
			Capability capability = null;
			if("all".equalsIgnoreCase(input)){
				
					logger.debug(CLASS_NAME + " will process all branch");
					List branches = BranchUtil.getAllBranchCode(context);
					
					if(branches != null && branches.size() > 0){
						logger.debug("branch tidak kosong");
						
						int listLength = branches.size();
						for(int bob=0; bob<listLength; bob++){
							branchCode  = (String)branches.get(bob);
							logger.debug(CLASS_NAME + " process branch" + branchCode);
							executeWorkGroupByBranch(context, branchCode, groupMail, capability);
						}
					}
			}else{
				String[] arrBranchCode = input.split(",");
				int len = arrBranchCode.length;
				for(int i=0; i<len; i++){
					branchCode = arrBranchCode[i].trim();
					logger.debug(CLASS_NAME + " Branch Code : " + branchCode);
					executeWorkGroupByBranch(context, branchCode, groupMail, capability);
				}
			}
		}else{
			logger.debug(CLASS_NAME + " branchCode argument's not found");
		}
	}
	
	private void executeWorkGroupByBranch(SailPointContext context, String branchCode, String groupMail, Capability capability) throws GeneralException{
	
		String workGroupName = "";
		String workGroupDescription = "";
		capability = null;
		Iterator<?> it = null;
		Identity identity = null;
		if(CommonUtil.isHQBranchCode(branchCode)){
			logger.debug(CLASS_NAME + " branch type KP hence not processed");
		}else if(CommonUtil.isKFCCBranchCode(branchCode)){
			logger.debug(CLASS_NAME + " branch type KFCC hence not processed");
		}else if(CommonUtil.isSOABranchCode(branchCode)){
			logger.debug(CLASS_NAME + " branch type SOA hence not processed");
		}else if(CommonUtil.isRegionalBranchCode(context, branchCode)){
			try{
				identity = CommonUtil.testSearchActiveIdentityByBranchByPosition(context, branchCode, KOW_KEPALA_SLA_KANWIL);
			}catch (Exception e) {
				logger.debug("Gagal Search");
				// TODO: handle exception
			}
			workGroupName = "KANWIL " + branchCode + " Head Approvers";
			workGroupDescription = "Group Approver untuk Kanwil dengan kode " +  branchCode;
			logger.debug(CLASS_NAME + " branch type kanwil headapprover : " + branchCode);
			
		}else if(CommonUtil.isMainBranchCode(context, branchCode)){
			try{
				identity = CommonUtil.testSearchActiveIdentityByBranchByPosition(context, branchCode, KOC_KPBC_KEPALA_KCU);
			}catch (Exception e) {
				logger.debug("Gagal Search");
				// TODO: handle exception
			}
			
			workGroupName = "KCU " + branchCode + " Head Approvers";
			workGroupDescription = "Group Approver untuk KCU dengan kode cabang " +  branchCode;
			logger.debug(CLASS_NAME + " branch type KCU headapprover : " + branchCode);
		}else if(CommonUtil.isSubBranchCode(context, branchCode)){
			try{
				identity = CommonUtil.testSearchActiveIdentityByBranchByPosition(context, branchCode, WAPIM_PIMPINAN_KCP);
			}catch (Exception e) {
				logger.debug("Gagal Search");
				// TODO: handle exception
			}
			workGroupName = "KCP " + branchCode + " Head Approvers";
			workGroupDescription = "Group Approver untuk KCP dengan kode cabang " +  branchCode;
			logger.debug(CLASS_NAME + " branch type KCP head approver " + branchCode);
		}
		
		if(identity != null && CommonUtil.isNotEmptyString(workGroupName)){
			logger.debug(CLASS_NAME + "preparation to create or update workgroup " + workGroupName);
			
			try {
				 Identity wgClean = context.getObjectByName(Identity.class, workGroupName);
		            logger.debug(CLASS_NAME + "name of group " + wgClean.getDisplayName());
					Terminator t = new Terminator(context);
					t.deleteObject(wgClean);
					context.commitTransaction();
			}catch (Exception e) {
				logger.debug(CLASS_NAME + "name of group gagal ditemukan");
				// TODO: handle exception
			}
           
			try{
				
					logger.debug("total loop : ");
					WorkGroupCreator.createUpdateWorkGroup(identity, context, workGroupName, workGroupDescription, groupMail, capability);
			
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
	}
	

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		return false;
	}
}
