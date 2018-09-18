package sailpoint.bca.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.api.Workflower;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityUtil;
import sailpoint.common.MainframeUtil;
import sailpoint.common.RACFAttribute;
import sailpoint.common.WorkflowUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowLaunch;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import sailpoint.workflow.rule.BCAApproval;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class LimitAccountBean extends AccountsRequestBean{
	
String className = "::LimitAccountBean::";
	
	public static Logger logger = Logger
			.getLogger("sailpoint.bca.web.LimitAccountBean");
	
	public List<LinkApplication> links;
	
		
	/**
	 * @return the links
	 */
	public List<LinkApplication> getLinks() {
		return links;
	}



	/**
	 * @param links the links to set
	 */
	public void setLinks(List<LinkApplication> links) {
		this.links = links;
	}



	public LimitAccountBean() throws GeneralException{
		
		super();
		
		Identity identity = getIdentity();
		
		logger.debug(className + " identity with id " + identity.getDisplayName());
		
		List links = identity.getLinks();
		
		Iterator it = links.iterator();
		List<LinkApplication> linksApp = new ArrayList();
		
		while(it.hasNext()){
			Link link = (Link)it.next();
			//Reset password tidak untuk SAP HR, reset password untuk AD, hanya dari login page
			if(!CommonUtil.HR_APPLICATION.equalsIgnoreCase(link.getApplicationName()) 
					&& !CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName()) 
					&& !CommonUtil.BASE24_FILE_FEED_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
				
				String userId = link.getNativeIdentity();
				String bcaApplicationName = link.getApplicationName();
				
				logger.debug(className + " processed " + userId);
				
				if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName())){
					
					String roleName = IdentityUtil.getAssignedRoleFromAccountId(identity, userId);
					if(roleName != null && !"".equalsIgnoreCase(roleName.trim())){
						bcaApplicationName = MainframeUtil.getBcaMainframeApplication(getContext(), roleName);
					}
					
				}	
				
				if(!link.isDisabled()){
					LinkApplication linkApps = new LinkApplication(userId, link.getInstance(), link.getApplicationName());
					linkApps.setNativeIdentity(link.getNativeIdentity());
					linkApps.setBcaApplicationName(bcaApplicationName);
					linkApps.setDebitLimit((String)link.getAttribute(RACFAttribute.LIMIT_DEBIT));
					linkApps.setCreditLimit((String)link.getAttribute(RACFAttribute.LIMIT_CREDIT));
					linksApp.add(linkApps);
					
					logger.debug(className + " add " + userId + " to list");
				}		
				
			}
			
		}
		
		if(getLinks() == null){
			this.setLinks(linksApp);
		}
		
		
	}
	
public String submitRequest(){
	
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
		
		
		if(targetList != null && targetList.size() > 0){
			try {
				invokeWorkflow(context, getIdentity(), targetList);
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return retVal;
	}

private void invokeWorkflow(SailPointContext context, Identity employee, List targetApplication) throws GeneralException{
	
	String methodName = "::invokeWorkflow::";
	
	final String WORKFLOW_NAME = "BCA Base LCM Provisioning";
	
	/*ProvisioningPlan plan = new ProvisioningPlan();
	
	Map limitMap = (Map)employee.getAttribute(IdentityAttribute.LIMIT_TELLER);
	
	if(limitMap == null){
		limitMap = new HashMap();
	}
	
	AccountRequest accReq = new AccountRequest();
	
	accReq.setApplication("IIQ");
	
	accReq.setNativeIdentity(employee.getName());
	
	accReq.setOperation(AccountRequest.Operation.Modify);
	
	
	AttributeRequest limitAttr = null;
	
	Iterator it = targetApplication.iterator();
	
	Map tempLimitMap = new HashMap();
	
	while(it.hasNext()){
		
		LinkApplication link = (LinkApplication)it.next();
		
		tempLimitMap.put(link.getNativeIdentity() + " debit", link.getDebitLimit());
		tempLimitMap.put(link.getNativeIdentity() + " credit", link.getCreditLimit());
	}*/
	
	ProvisioningPlan plan = new ProvisioningPlan();
	
	Iterator it = targetApplication.iterator();
	
	while(it.hasNext()){
		
		LinkApplication link = (LinkApplication)it.next();
		
		AccountRequest accReq = new AccountRequest();
		
		accReq.setApplication(link.getApplicationName());
		
		accReq.setInstance(link.getInstance());
		
		accReq.setNativeIdentity(link.getNativeIdentity());
		
		accReq.setOperation(AccountRequest.Operation.Modify);
		
		AttributeRequest limitDebit = null;
		
		AttributeRequest limitCredit = null;
		
		if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName())){
			
			limitDebit = new AttributeRequest();

			limitDebit.setName(RACFAttribute.LIMIT_DEBIT);

			limitDebit.setValue(link.getDebitLimit());

			limitDebit.setOp(ProvisioningPlan.Operation.Set);
			
			limitCredit = new AttributeRequest();

			limitCredit.setName(RACFAttribute.LIMIT_CREDIT);

			limitCredit.setValue(link.getCreditLimit());

			limitCredit.setOp(ProvisioningPlan.Operation.Set);
			
		}
		
		List attributeRequestList = new ArrayList();
		
		if(limitDebit != null){
			attributeRequestList.add(limitDebit);
		}
		
		if(limitCredit != null){
			attributeRequestList.add(limitCredit);
		}

		if(attributeRequestList != null && attributeRequestList.size() > 0){
			
			accReq.setAttributeRequests(attributeRequestList);
			
			if(accReq != null){
				plan.add(accReq);
			}
		}	
	}
	
	logger.debug(className + methodName + "The Provisioning Plan after adding this account request looks this: " + plan.toXml());
	
	// Sort out all the variables required for workflow invocation....
	
	Workflow baseWf = new Workflow();
	
	Attributes attributes = new Attributes();

	HashMap launchArgsMap = WorkflowUtil.getLaunchArgsMap(employee, getLoggedInUser(), plan, BCAApproval.FLOW_TYPE_LIMIT_TELER);

	attributes.putAll(launchArgsMap);

	baseWf.setVariables(attributes);

	WorkflowLaunch wflaunch = new WorkflowLaunch();

	Workflow wf = (Workflow) context.getObjectByName(Workflow.class, WORKFLOW_NAME);

	wflaunch.setWorkflowName(wf.getName());

	wflaunch.setWorkflowRef(wf.getName());

	wflaunch.setCaseName("Reset password for "

	+ employee.getDisplayName() + " :: " + new Date());

	wflaunch.setVariables(launchArgsMap);

	Workflower workflower = new Workflower(context);

	WorkflowLaunch launch = workflower.launch(wflaunch);

	String workFlowId = launch.getWorkflowCase().getId();

	logger.debug(className + methodName + "Workflow got launched with workflow id: " + workFlowId);
	
	
}
	

}
