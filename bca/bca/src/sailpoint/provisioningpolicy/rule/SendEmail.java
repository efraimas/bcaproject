package sailpoint.provisioningpolicy.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.BCAApproverMatrixConstant;
import sailpoint.common.CommonUtil;
import sailpoint.object.Custom;
import sailpoint.object.EmailOptions;
import sailpoint.object.EmailTemplate;
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.Workflow.Step;
import sailpoint.tools.EmailException;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class SendEmail{
	public static String className = "::SendEmail::";
	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.SendEmail");
	
	static String methodName = "::SendEmail::";
	
	public static String SendEmail(SailPointContext context, ProvisioningPlan plan, Custom mapAttrCustom, String branchType, CharSequence mainBranchCode, boolean isKp, CharSequence divisionCode,  String requesterTypeKey, Map<String, Object> args, EmailTemplate template, Custom localAttributes, Object identityName) throws GeneralException{
		
	    Identity checker1 = null;
	    List<String> workgroup = null;
		Map approverMatriks = (Map)mapAttrCustom.get(requesterTypeKey);
		String varBranchCode = "$branchCode";
		String varMainBranch = "$mainBranch";
		String varDivisionCode = "$divisionCode";
		
		
		if(approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY) != null){
			
			String workgroupChecker1Name = "";
			
			if(isKp){
				workgroupChecker1Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY)).replace(varDivisionCode,divisionCode);
			}else{
				workgroupChecker1Name = ((String)approverMatriks.get(BCAApproverMatrixConstant.CHECKER1_KEY)).replace("KCP".equalsIgnoreCase(branchType) ? varMainBranch : varBranchCode,mainBranchCode);
			}
					
			logger.debug(className + methodName + " workgroupChecker1Name : " + workgroupChecker1Name);
			
			checker1 = CommonUtil.getWorkgroupFromWorkGroupName(context, workgroupChecker1Name);
			
			logger.debug(className + methodName + " workgroupChecker1Name : " + checker1);
			
			List emailDest = new ArrayList();
			
			((Custom) emailDest).put("userEmailTemplate",
	   				localAttributes.get("userEmailTemplate"));
			
			EmailOptions ops = new EmailOptions(emailDest, args);
			
			getUserEmail(identityName.equals(checker1), plan);
			
			context.sendEmailNotification(template, ops);

	        workgroup = (List<String>) CommonUtil.getWorkgroupFromWorkGroupName(context, workgroupChecker1Name);
	       
	        ops.setTo(workgroup);
	        
	        
	        logger.debug(className + methodName + " workgroupChecker1Name : " + workgroup);
		}
		
		return ((Identity) workgroup).getEmail();
	}


	
    private static void getUserEmail(Object identityName, ProvisioningPlan plan) {
		// TODO Auto-generated method stub
		
	    //List identityName = new ArrayList();
		
		identityName.notify();
		
		getUserEmail("checker1".equalsIgnoreCase((String) identityName), plan);
		
	}
	
	
	//===============================EMAIL NOTIFY REQUESTER FOR CHECKER 1===========================================//
	
	public static String SendEmailtoCh1(SailPointContext context, Step step, Identity approval) 
			throws EmailException, GeneralException{

		EmailOptions plan = null;
				
		EmailTemplate template =(EmailTemplate) SendEmail.step(context, plan);
		
		//plan.setVariable("userEmailTemplate", template);
		
		logger.debug(className + methodName + " workgroupChecker1Name : " + template);
		
		if(step.isComplete() ){
			
			context.sendEmailNotification((EmailTemplate) template, plan);
		}
		logger.debug(className + methodName + " workgroupChecker1Name : " + approval);
		
		return ((Identity) approval).getEmail();
		
	}
	public static Object step (SailPointContext context, EmailOptions plan){
		
		Object EmailTemplate = null;
		
		List template = new ArrayList();
		
		if(plan != null){
			
        plan.setVariable("userEmailTemplate", template);
        
		}
		
		EmailTemplate = plan.getFileName().equalsIgnoreCase("BCA LCM User Notification");
		
		return EmailTemplate;
		
	}
	
	public static Identity approval(SailPointContext context,Object identity, String workgroupChecker1Name, Object identityName, ProvisioningPlan plan) throws GeneralException{
		
		Identity checker1 =null;
		
		checker1=CommonUtil.getWorkgroupFromWorkGroupName(context, workgroupChecker1Name);
		
		logger.debug(className + methodName + " workgroupChecker1Name : " + checker1);
		
		if(checker1!=null){
			getUserEmail(identityName, plan);
		}
		
		return checker1;
		
	}

}
