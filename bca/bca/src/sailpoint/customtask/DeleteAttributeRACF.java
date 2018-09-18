package sailpoint.customtask;

import org.apache.log4j.Logger;

import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.TaskResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

public class DeleteAttributeRACF extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::DeleteAttributeRACF::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.DeleteAttributeRACF");
	
	@Override
	public void execute(SailPointContext ctx, TaskSchedule scl, TaskResult trs, Attributes<String, Object> attr)
			throws Exception {
		// TODO Auto-generated method stub
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String USER_ID = (String) attr.get("UserId");
		String launcher = (String) attr.get("launcher_Workflow");
		
		if(USER_ID != null){
			executeDeleteAttr(ctx, USER_ID, launcher);
		}
		
		
	}
	
	@SuppressWarnings("deprecation")
	private void executeDeleteAttr(SailPointContext ctx, String USER_ID, String launcher) throws GeneralException{
		String METHOD_NAME = "::executeDeleteAttr::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside..." + USER_ID + launcher);
		
		Identity identity = ctx.getObjectByName(Identity.class, launcher.trim());
		List links = identity.getLinks();
		Iterator it = links.iterator();
		
		while(it.hasNext()){
			Link link = (Link)it.next();
			
			String userIdLink = link.getDisplayName();
			if(USER_ID.equalsIgnoreCase(userIdLink)){
				logger.debug(CLASS_NAME + METHOD_NAME + "rlink xml : " + link.toXml());
				String resumeDate = (String)link.getAttribute("RESUME_DATE");
				logger.debug(CLASS_NAME + METHOD_NAME + "resume date yang didapat : " + resumeDate);
				
				String accountId = userIdLink;
				AccountRequest accReq = new AccountRequest();
				ProvisioningPlan newplan = new ProvisioningPlan();
				AttributeRequest attReq = new AttributeRequest();
				List attrReqList = new ArrayList();
				
				accReq.setOperation(AccountRequest.Operation.Modify);
				
				accReq.setApplication("IBM MAINFRAME");
				
				accReq.setInstance(link.getInstance());
				
				accReq.setNativeIdentity(userIdLink);
				
				attReq.setName("RESUME_DATE");
				attReq.setOp(ProvisioningPlan.Operation.Remove);
				
				attrReqList.add(attReq);							
				accReq.setAttributeRequests(attrReqList);
			
				if(accReq != null){
					try{
						logger.debug(CLASS_NAME + METHOD_NAME + "AccessRequest in plan inteceptor" + accReq.toXml());
					}catch (Exception e) {
						// TODO: handle exception
					}
					newplan.add(accReq);
				}
				
				newplan.setIdentity(identity);
				
				try {
					logger.debug(CLASS_NAME + METHOD_NAME + " plan has been setup, plan should be like this " + newplan.toXml());
				} catch (GeneralException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Provisioner p = new Provisioner(ctx);
				
				logger.debug( CLASS_NAME + METHOD_NAME + " preparation to compile the plan");
				
				ProvisioningProject project;
				try {
					
					project = p.compile(newplan);
					
					logger.debug(CLASS_NAME + METHOD_NAME + " preparation to execute the project");
					
					p.execute(project);
					
				} catch (GeneralException e) {

					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		return false;
	}

}
