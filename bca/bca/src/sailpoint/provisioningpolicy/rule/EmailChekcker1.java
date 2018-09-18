package sailpoint.provisioningpolicy.rule;

import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.object.ApprovalSet;
import sailpoint.object.EmailOptions;
import sailpoint.object.EmailTemplate;
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningProject;
import sailpoint.tools.GeneralException;

public class EmailChekcker1{

	public static String CLASSNAME = "::EmailChekcker1::";
	
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.EmailChekcker1");
	
	public static void sendMail(ProvisioningProject project, String workgroup, String flow, ApprovalSet approvalSet, String USER_ID, String identityName, ProvisioningPlan plan, String launcher) throws GeneralException{
		String METHOD_NAME = "::sendMail::";
		
		SailPointContext ctx = SailPointFactory.getCurrentContext();
		
		Identity wg = ctx.getObjectByName(Identity.class, workgroup);
		String emailWg = wg.getEmail();
		String[] listmail = emailWg.split(";");
		String emailAdd = "";
		
		for(int i=0; i<listmail.length; i++){
			emailAdd = listmail[i].trim();
			logger.debug(CLASSNAME + METHOD_NAME + "try to send mail to checker 1");
			
			EmailOptions options = new EmailOptions();
			EmailTemplate et = ctx.getObjectByName(EmailTemplate.class, "BCA LCM User Notification");
			
	    		options.setSendImmediate(true);
				options.setNoRetry(true);
				options.setVariable("flow", flow);
				options.setVariable("approvalSet", approvalSet);
				options.setVariable("USER_ID", USER_ID);
				options.setVariable("identityName", identityName);
				options.setVariable("plan", plan);
				options.setVariable("launcher", launcher);
				options.setTo(emailAdd);
				ctx.sendEmailNotification(et, options);
				ctx.commitTransaction();
		}
	}
}
