package sailpoint.bca.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.api.Workflower;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.BcaCalendar;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityUtil;
import sailpoint.common.MainframeUtil;
import sailpoint.common.WorkflowUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.RoleAssignment;
import sailpoint.object.RoleTarget;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowLaunch;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import sailpoint.workflow.rule.BCAApproval;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class DeleteAccountBean extends AccountsRequestBean {
	
	String className = "::DeleteAccountBean::";
	
	public static Logger logger = Logger
			.getLogger("sailpoint.bca.web.DeleteAccountBean");

	public List<LinkApplication> links;
	
	public String userId;
	
	public List<LinkApplication> getLinks() {
		return links;
	}
	
	public void setLinks(List<LinkApplication> links) {
		this.links = links;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public DeleteAccountBean() {
		try {
			
			Identity identity = getIdentity();
			
			logger.debug(className + " identity with id " + identity.getDisplayName());
			
			List links = identity.getLinks();
			
			logger.debug(className + " Size link : " + links.size());
			
			BcaCalendar cal = new BcaCalendar();
			
			translateToLinkApplication(links, cal);			
			
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void translateToLinkApplication(List links, BcaCalendar cal) throws GeneralException{
		Iterator it = links.iterator();
		List<LinkApplication> linksApp = new ArrayList();
		
		while(it.hasNext()){
			Link link = (Link)it.next();
			
			if(!CommonUtil.HR_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
				
				String userId = link.getNativeIdentity();
				String bcaApplicationName = link.getApplicationName();
				
				if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
					userId = (String)link.getAttribute(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME);
				}
				
				if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName())){
					Attributes att = null;
					
					//search by assigned role
					String roleName = IdentityUtil.getAssignedRoleFromAccountId(getIdentity(), userId);
					if(roleName != null && !"".equalsIgnoreCase(roleName.trim())){
						bcaApplicationName = MainframeUtil.getBcaMainframeApplication(getContext(), roleName);
					}
					
					//search by detected role
					if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(roleName)){
						roleName = IdentityUtil.getDetectionRoleFromAccountId(getIdentity(), userId);
						if(roleName != null && !"".equalsIgnoreCase(roleName.trim())){
							bcaApplicationName = MainframeUtil.getBcaMainframeApplication(getContext(), roleName);
						}	
					}
					
					//search by entitlement attribute if from assigned and detected not get the application name
					if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(roleName)){
						//att = (Attributes) link.getAttributes();
						if(userId.equalsIgnoreCase(link.getDisplayName())){
							att = (Attributes) link.getAttributes();
							try{
								att.getList("groups");
								bcaApplicationName = IdentityUtil.getRoleNameFromEntitlementAccountId(getContext(), att);
							}catch(Exception e){
								logger.debug("group entitlements is empty");
							}
							
						}
						
					}
					
				}	

				String linkStatus = link.isDisabled() ? "inactive" : "active";

				LinkApplication linkApps = new LinkApplication(link.getApplicationName(), userId, linkStatus, link.getId(), link.getInstance());
				linkApps.setNativeIdentity(link.getNativeIdentity());
				linkApps.setBcaApplicationName(bcaApplicationName);
				linksApp.add(linkApps);
			}
		}
		
		if(getLinks() == null){
			this.setLinks(linksApp);
		}
	}

	public String submitRequest(){
		
		String methodName = "::submitRequest::";
		
		logger.debug(methodName + " called");
		
		ExternalContext ctx = getFacesContext().getExternalContext();
		
		List targetList = null;
		
		if(getLinks() != null){
			Iterator i = getLinks().iterator();
			targetList = new ArrayList();
			while(i.hasNext()){
				LinkApplication linkApps = (LinkApplication)i.next();
				if(linkApps.isChecked()){
					targetList.add(linkApps);
				}
			}
		}
		
		SailPointContext context = null;
		
		try {
			context = SailPointFactory.getCurrentContext();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		String retVal = "success";
		
		if(targetList != null && targetList.size() > 0){
	
			try {
				invokeWorkflow(context, getIdentity(), targetList);
			} catch (GeneralException e) {
				
				retVal = "failed";
				e.printStackTrace();
			}
		}

		return retVal;
		
	}
	
	private void invokeWorkflow(SailPointContext context, Identity employee, List targetApplication) throws GeneralException{
		
		String methodName = "::invokeWorkflow::";
		
		final String WORKFLOW_NAME = "BCA Base LCM Provisioning";
		
		ProvisioningPlan plan = new ProvisioningPlan();
		
		Iterator it = targetApplication.iterator();
		
		logger.debug(className + methodName + " Size of app to invoke "+ targetApplication.size());
		
		while(it.hasNext()){
			
			LinkApplication link = (LinkApplication)it.next();
			
			logger.debug(className + methodName + "User Id include to workflow is:"  + link.userId);

			Identity identity = getIdentity();
			logger.debug(className + methodName + "identity nya : " + identity.toXml());
			
			if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName())
					|| CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName())
					|| CommonUtil.BASE24_FILE_FEED_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
				
				AccountRequest accReq = new AccountRequest();
				
				//for email template
				accReq.addArgument("APP_NAME", link.getBcaApplicationName());
				
				accReq.setApplication(link.getApplicationName());
				
				accReq.setInstance(link.getInstance());
				
				accReq.setNativeIdentity(link.getNativeIdentity());
				
				accReq.setOperation(AccountRequest.Operation.Delete);
				
				plan.add(accReq);
				
				
				
				if(link.userId != null){
					try{
						logger.debug(className + methodName + "Inside.. penghapusan TempPassword ");
						IdentityUtil.updateTempPasswordIdentityForDeletePassword(context, identity, link.userId);
					}catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
					
					//
					
				}
				
			logger.debug(className + methodName + "Enter the workflow with " + accReq.toXml());
				
			}
			
		}
		
		logger.debug(className + methodName + "The Provisioning Plan after adding this account request looks this: " + plan.toXml());
		
		// Sort out all the variables required for workflow invocation....
		
		Workflow baseWf = new Workflow();
		
		Attributes attributes = new Attributes();

		HashMap launchArgsMap = WorkflowUtil.getLaunchArgsMap(employee, getLoggedInUser(), plan, BCAApproval.FLOW_TYPE_ACCOUNTS_REQUEST);

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
