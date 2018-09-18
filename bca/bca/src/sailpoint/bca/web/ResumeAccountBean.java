package sailpoint.bca.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.api.Workflower;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityUtil;
import sailpoint.common.MainframeUtil;
import sailpoint.common.WorkflowUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowLaunch;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import sailpoint.workflow.rule.BCAApproval;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ResumeAccountBean extends AccountsRequestBean {

	String className = "::ResumeAccountBean::";
	public static Logger logger = Logger.getLogger("sailpoint.bca.web.ResumeAccountBean");

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
	
	public String getUserId() {
		return userId;
	}

	public ResumeAccountBean() throws GeneralException{
		logger.debug("Inside BCA Resume Account Bean");
		
		Identity identity = getIdentity();
		logger.debug(className + " identity with id " + identity.getDisplayName());

		List links = identity.getLinks();
		logger.debug(className + " Size link : " + links.size());

		Iterator it = links.iterator();
		List<LinkApplication> linksApp = new ArrayList();
		
		while (it.hasNext()) 
		{
			Link link = (Link) it.next();
			
			if (!CommonUtil.HR_APPLICATION.equalsIgnoreCase(link.getApplicationName()) && !CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName()))
			{
				String userId = link.getNativeIdentity();
				String bcaApplicationName = link.getApplicationName();
				logger.debug("UserId: " +userId + " BCA Application Name: " +bcaApplicationName);
				
				//For Mainframe
				if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName()))
				{
					logger.debug("Inside BCA Mainframe RACF");
					Attributes att = null;
					
					String appName = IdentityUtil.getAssignedRoleFromAccountId(identity, userId);
					logger.debug("App Name: " +appName);
					
					if (appName != null && !"".equalsIgnoreCase(appName.trim())) {
						bcaApplicationName = MainframeUtil.getBcaMainframeApplication(getContext(), appName);
						logger.debug("BCA App Name: " +bcaApplicationName);
					}
					
					if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(appName))
					{
						logger.debug("Inside");
						
						if (userId.equalsIgnoreCase(link.getDisplayName())){
							att = (Attributes) link.getAttributes();
							
							if(att.getList("groups")!=null){
								bcaApplicationName = IdentityUtil.getRoleNameFromEntitlementAccountId(getContext(), att);
								logger.debug("Inside Group, BCA Application Name: "+bcaApplicationName);
							}
							else if(att.getList("UG_DEF")!=null){
								bcaApplicationName = IdentityUtil.getRoleNameFromEntitlementAccountId(getContext(), att);
								logger.debug("Inside UG_DEF, BCA Application Name: "+bcaApplicationName);
							}
							else if(att.getList("OWNER")!=null){
								bcaApplicationName = IdentityUtil.getRoleNameFromEntitlementAccountId(getContext(), att);
								logger.debug("Inside OWNER, BCA Application Name: "+bcaApplicationName);
							}
							else{
								appName = IdentityUtil.getDetectionRoleFromAccountId(identity, userId);
								appName = appName.trim();
								
								bcaApplicationName = MainframeUtil.getBcaMainframeApplication(getContext(), appName);
								logger.debug("Inside ELSE, BCA Application Name: "+bcaApplicationName);
							}
						}
						
						if(bcaApplicationName == null || bcaApplicationName.length()==0){
							bcaApplicationName = "CALL HALO SKES";
						}
					}
				}
				
				String linkStatus = link.isDisabled() ? "inactive" : "active";

				LinkApplication linkApps = new LinkApplication(link.getApplicationName(), userId, linkStatus, link.getId(), link.getInstance());
				linkApps.setNativeIdentity(link.getNativeIdentity());
				linkApps.setBcaApplicationName(bcaApplicationName);

				Map linkMap = link.getAttributes().getMap();

				//Status revoke/disable in iiq
				String statusAcc = "";

				try {
					statusAcc = linkMap.get("IIQDisabled").toString();
				} catch (Exception e) {
					logger.debug("failed to get iiqdisabled status");
				}

				logger.debug("Account : " + link.getNativeIdentity() + " status : " + statusAcc);

				//For Mainframe only IIQDisabled=true
				if(link.getApplicationName().equalsIgnoreCase(CommonUtil.RACF_APPLICATION_NAME))
				{
					if (statusAcc.equalsIgnoreCase("true"))
					{
						linksApp.add(linkApps);
					}
				}else
				{
					linksApp.add(linkApps);
				}
			}
		}
		
		if (getLinks() == null) {
			this.setLinks(linksApp);
		}
	}

	public String submitRequest() {

		String retVal = "success";
		String methodName = "::submitRequest::";
		logger.debug(methodName + " called");

		SailPointContext context = null;

		try {
			context = SailPointFactory.getCurrentContext();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		List targetList = null;

		if (getLinks() != null) {
			Iterator i = getLinks().iterator();
			targetList = new ArrayList();
			while (i.hasNext()) {
				LinkApplication linkApps = (LinkApplication) i.next();
				logger.debug(className + methodName + "Link application : " + linkApps.toString());

				if (linkApps.isChecked()) {
					targetList.add(linkApps);
				}
			}
		}
		
		if (targetList != null && targetList.size() > 0) {
			try {
				invokeWorkflow(context, getIdentity(), targetList);
				
			} catch (GeneralException e) {
				retVal = "failed";
				e.printStackTrace();
			}
		}

		return retVal;
	}

	private void invokeWorkflow(SailPointContext context, Identity employee, List targetApplication) throws GeneralException {

		String methodName = "::invokeWorkflow::";
		
		final String WORKFLOW_NAME = "BCA Base LCM Provisioning";

		ProvisioningPlan plan = new ProvisioningPlan();
		
		Iterator it = targetApplication.iterator();
		logger.debug(className + methodName + " Size of app to invoke " + targetApplication.size());

		while (it.hasNext())
		{
			LinkApplication link = (LinkApplication) it.next();
			logger.debug(className + methodName + "User Id include to workflow is:" + link.userId);
			
			AccountRequest accReq = new AccountRequest();
//			AttributeRequest attReq = new AttributeRequest();
//			List attrReqList = new ArrayList();
			
			accReq.setOperation(AccountRequest.Operation.Enable);
			accReq.setApplication(link.getApplicationName());
			accReq.setNativeIdentity(link.getNativeIdentity());
			accReq.setInstance(link.getInstance());
			
//			attReq.setName("RESUME_DATE");
//			attReq.setOp(ProvisioningPlan.Operation.Set);
//			attReq.setValue("NORESUME");
//			
//			attrReqList.add(attReq);							
//			accReq.setAttributeRequests(attrReqList);
			logger.debug("Enter the workflow with " + accReq.toXml());
			
			plan.add(accReq);
			logger.debug("The Provisioning Plan after adding this account request looks this: "+ plan.toXml());
		}
		
		// Sort out all the variables required for workflow invocation....
		Workflow baseWf = new Workflow();
		Attributes attributes = new Attributes();

		HashMap launchArgsMap = WorkflowUtil.getLaunchArgsMap(employee, getLoggedInUser(), plan, BCAApproval.FLOW_TYPE_PENGAKTIFAN);

		attributes.putAll(launchArgsMap);

		baseWf.setVariables(attributes);

		WorkflowLaunch wflaunch = new WorkflowLaunch();

		Workflow wf = (Workflow) context.getObjectByName(Workflow.class, WORKFLOW_NAME);

		wflaunch.setWorkflowName(wf.getName());
		wflaunch.setWorkflowRef(wf.getName());
		wflaunch.setCaseName("Account Resumed for "+ employee.getDisplayName() + " :: " + new Date());
		wflaunch.setVariables(launchArgsMap);

		Workflower workflower = new Workflower(context);

		WorkflowLaunch launch = workflower.launch(wflaunch);

		String workFlowId = launch.getWorkflowCase().getId();
		logger.debug(className + methodName + "Workflow got launched with workflow id: " + workFlowId);
	}
}
