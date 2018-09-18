package sailpoint.bca.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.api.Workflower;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.BcaCalendar;
import sailpoint.common.CommonUtil;
import sailpoint.common.WorkflowUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowLaunch;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import sailpoint.workflow.rule.BCAApproval;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class EnableRole extends AccountsRequestBean{
		
		String className = "::EnableRole::";
		
		public static Logger logger = Logger
				.getLogger("sailpoint.bca.web.EnableRole");
		
		public String userId;
		
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



		/**
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}



		/**
		 * @param userId the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		
		public void ResumeAccountBean() {
			try {
				
				Identity identity = getIdentity();
				
				logger.debug(className + " identity with id " + identity.getDisplayName());
				
				List links = identity.getLinks();
				
				BcaCalendar cal = new BcaCalendar();
				
				translateToLinkApplication(links, cal);		
				
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void translateToLinkApplication(List links, BcaCalendar cal){
			Iterator it = links.iterator();
			List<LinkApplication> linksApp = new ArrayList();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				
				if(!CommonUtil.HR_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
					
					String userId = link.getNativeIdentity();
					
					if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
						userId = (String)link.getAttribute(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME);
					}
					String linkStatus = link.isDisabled() ? "inactive" : "active";
					//if(link.isDisabled()){
						LinkApplication linkApps = new LinkApplication(link.getApplicationName(), userId, linkStatus, link.getId(), link.getInstance());
						linkApps.setNativeIdentity(link.getNativeIdentity());
						linksApp.add(linkApps);
				//	}
					
					
				}
				
			}
			
			if(getLinks() == null){
				this.setLinks(linksApp);
			}
			
			
		}
		
		public static void submitRequest(SailPointContext context, ProvisioningPlan plan){
			
			
			List reqList = plan.getAccountRequests();
			
			boolean isDisabledOp = false;
			
			Iterator it = reqList.iterator();
			
			try {
				logger.debug("Before Provisioning Policy::" + plan.toXml());
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			
			while(it.hasNext()){
				AccountRequest accReqIt = (AccountRequest)it.next();
				logger.debug("AccReqOp::" + accReqIt.getOperation());
				logger.debug("AccReqOpString::" + accReqIt.getOperation().toString());
				
				if("Disable".equalsIgnoreCase(accReqIt.getOperation().toString())){
					logger.debug("The operation is disabled");
					isDisabledOp = true;
				}
			}
			
			
			if(!isDisabledOp){
				AccountRequest accReqOri = (AccountRequest)reqList.get(0);
				
				AccountRequest accReq = new AccountRequest();
				
				accReq.setApplication(accReqOri.getApplicationName());
				
				accReq.setInstance(accReqOri.getInstance());
				
				accReq.setNativeIdentity(accReqOri.getNativeIdentity());
				
				accReq.setOperation(AccountRequest.Operation.Enable);
				
				reqList.add(accReq);
				
				plan.setAccountRequests(reqList);
			}
				
			
		}
		
		private void invokeWorkflow(SailPointContext context, Identity employee, List targetApplication) throws GeneralException{
			
			String methodName = "::invokeWorkflow::";
			
			final String WORKFLOW_NAME = "BCA Base LCM Provisioning";
			
			ProvisioningPlan plan = new ProvisioningPlan();
			
			Iterator it = targetApplication.iterator();
			
			while(it.hasNext()){
				
				LinkApplication link = (LinkApplication)it.next();
				
				if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName())
						|| CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName())
						|| CommonUtil.BASE24_FILE_FEED_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
					
					AccountRequest accReq = new AccountRequest();
					
					accReq.setApplication(link.getApplicationName());
					
					accReq.setInstance(link.getInstance());
					
					accReq.setNativeIdentity(link.getNativeIdentity());
					
					accReq.setOperation(AccountRequest.Operation.Enable);
					
					plan.add(accReq);
					
				}
				
			}
			
			logger.debug(className + methodName + "The Provisioning Plan after adding this account request looks this: " + plan.toXml());
			
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

			wflaunch.setCaseName("Account Activation / Deactivation for "

			+ employee.getDisplayName() + " :: " + new Date());

			wflaunch.setVariables(launchArgsMap);

			Workflower workflower = new Workflower(context);

			WorkflowLaunch launch = workflower.launch(wflaunch);

			String workFlowId = launch.getWorkflowCase().getId();

			logger.debug(className + methodName + "Workflow got launched with workflow id: " + workFlowId);
			
			
		}

	}

