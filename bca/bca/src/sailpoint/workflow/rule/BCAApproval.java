package sailpoint.workflow.rule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import sailpoint.bca.web.LinkApplication;
import sailpoint.common.BCAApproverMatrixConstant;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.common.RACFAttribute;
import sailpoint.object.AccountSelection;
import sailpoint.object.ApprovalItem;
import sailpoint.object.ApprovalItem.ProvisioningState;
import sailpoint.object.ApprovalSet;
import sailpoint.object.Attributes;
import sailpoint.object.Bundle;
import sailpoint.object.Identity;
import sailpoint.object.IdentityRequest;
import sailpoint.object.IdentityRequest.CompletionStatus;
import sailpoint.object.IdentityRequest.ExecutionStatus;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.ProvisioningTarget;
import sailpoint.object.Question;
import sailpoint.object.TaskResult;
import sailpoint.object.WorkItem;
import sailpoint.object.WorkItem.State;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowLaunch;
import sailpoint.tools.GeneralException;
import sailpoint.workflow.WorkflowContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BCAApproval {
	public static String CLASS_NAME = "::BCAApproval::";
	public static Logger logger = Logger
			.getLogger("sailpoint.workflow.rule.BCAApproval");

	// Workflow Approver Constants.... If you change in the UI... make a change
	// here also...

	public static final String APPROVER_1 = "approver1";
	public static final String APPROVER_2 = "approver2";
	public static final String CHECKER_1 = "checker1";
	public static final String CHECKER_2 = "checker2";
	public static final String SA_APLIKASI = "saAplikasi";

	// List of variables which will be used for spawning the Approval
	// SubProcess
	public static final String APPROVAL_SCHEME = "identity";
	public static final String WF_VARIABLE_TEMPORARY_APPROVAL_IDENTITIES = "tempApprovalIdentities";
	public static final String WF_VARIABLE_TEMPORARY_APPROVAL_SET = "temporaryApprovalSet";

	public static final String WF_VARIABLE_NUMBER_OF_APPROVAL_SET_REMAINING = "numberOfApprovalSetRemaining";
	// public static final String WF_VARIABLE_SUB_PROCESS_APPROVAL_SET =
	// "subProcessApprovalSet";
	public static final String WF_VARIABLE_APPROVAL_SET = "approvalSet";
	public static final String WF_VARIABLE_IDENTITY_NAME = "identityName";

	public static final String ROLE_EXTENDED_ATTRIBUTE_1 = "applicationName";

	public static final String WF_VARIABLE_APPROVAL_MAP = "approvalMap";

	public static final String WF_VARIABLE_NUMBER_OF_PROVISIONING_PLAN = "numberOfPlan";

	public static final String WF_VARIABLE_TASK_LIST_ID = "taskIdLists";
	public static final String WF_VARIABLE_PROVISIONING_CHECK_STATUS_INTERVAL = "provisioningCheckStatusInterval";
	public static final String WF_VARIABLE_STATUS_CHECK = "statusChecks";
	public static final String WF_VARIABLE_NEXT_ACTION = "nextAction";
	public static final String WF_VARIABLE_PROVISIONING_PLAN = "plan";

	public static final String WORKFLOW_NAME_LCM_PROVISIONING = "BCA LCM Provisioning";
	public static final String WF_VARIABLE_FLOW = "flow";
	public static final String FLOW_TYPE_ACCESS_REQUEST = "AccessRequest";
	public static final String FLOW_TYPE_ACCOUNTS_REQUEST = "Delete";
	public static final String FLOW_TYPE_EXTEND_ACCOUNT = "ExtendAccount";
	public static final String FLOW_TYPE_PASSWORD_REQUEST = "PasswordsRequest";
	public static final String FLOW_TYPE_PENONAKTIFAN = "Revoke";
	public static final String FLOW_TYPE_PENGAKTIFAN = "Resume";
	public static final String FLOW_TYPE_PENGURANGAN = "Remove";
	public static final String FLOW_TYPE_CREATE_IDENTITY = "IdentityCreateRequest";
	public static final String FLOW_TYPE_LIMIT_TELER = "LimitTeler";

	/**
	 * @param workflow
	 * @param nextApprover
	 * @return
	 */
	public static boolean shouldStop(Workflow workflow) {
		String METHOD_NAME = "::shouldStop::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		boolean shouldStop = false;

		Attributes workflowVariableAttributes = workflow.getVariables();
		
		ApprovalSet approvalSet = (ApprovalSet) workflowVariableAttributes
				.get(WF_VARIABLE_APPROVAL_SET);

		List approvalItemList = approvalSet.getItems();
		Iterator it = approvalItemList.iterator();
		while (it.hasNext()) {
			ApprovalItem localApprovalItem = (ApprovalItem) it.next();

			// Check if the approval item state is
			// rejected...
			if (localApprovalItem.getState() == WorkItem.State.Rejected) {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "One of the work item found to be rejected... ");
				shouldStop = true;
			}

		}

		logger.debug(CLASS_NAME + METHOD_NAME + "Should Stop: " + shouldStop);
		return shouldStop;

	}
	
	/**
	 * @param workflow
	 * @param nextApprover
	 * @return
	 */
	public static boolean skipThisApproverChecker(Workflow workflow, String nextApprover) {
		String METHOD_NAME = "::shouldStop::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		boolean shouldSkip = false;

		Attributes workflowVariableAttributes = workflow.getVariables();
		String nextApproverString = workflowVariableAttributes
				.getString(nextApprover);
		logger.debug(CLASS_NAME + METHOD_NAME + "nextApproverString: "
				+ nextApproverString);

		if (nextApproverString == null || nextApproverString.equalsIgnoreCase("")) {
			shouldSkip = true;
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Returning shouldSkip value as : " + shouldSkip
					+ " for nextApprover: " + nextApprover);
			return shouldSkip;
		}

		return shouldSkip;

	}

	/**
	 * @param workflow
	 * @return
	 * @throws GeneralException
	 */
	@SuppressWarnings("deprecation")
	public static List callLCMProvisioning(Workflow workflow,
			SailPointContext context) throws GeneralException {
		String METHOD_NAME = "::callLCMProvisioning::";
		logger.info(METHOD_NAME + "Inside...");

		List taskIdLists = new ArrayList();

		// Getting the global variable of workflow....

		Attributes attributes = workflow.getVariables();

		// Get the Initial Provisioning Plan
		ProvisioningPlan masterProvisioningPlan = (ProvisioningPlan) attributes
				.get(WF_VARIABLE_PROVISIONING_PLAN);

		String requestFlow = (String) attributes.get(WF_VARIABLE_FLOW);
		
		logger.debug(METHOD_NAME + " request flow is " + requestFlow);

		if (requestFlow.equalsIgnoreCase(FLOW_TYPE_ACCESS_REQUEST)) {

			List accountRequestList = masterProvisioningPlan
					.getAccountRequests();
			Iterator it = accountRequestList.iterator();
			while (it.hasNext()) {
				AccountRequest localAccountRequest = (AccountRequest) it.next();

				Attributes a = localAccountRequest.getArgs();
			
				String displayableName = a.getString("displayableName");
				logger.info(METHOD_NAME + "displayableName: " +  displayableName);

				// Now create individual Provisioning Plan....
				ProvisioningPlan newProvisioningPlan = new ProvisioningPlan();
				logger.debug("localAccountRequest will be : " + localAccountRequest.toXml());
				newProvisioningPlan.add(localAccountRequest);
				
				List provisioningTargets = new ArrayList();
				
				List temporaryProvisioningTarget = null;
				try {
					temporaryProvisioningTarget = masterProvisioningPlan
							.getProvisioningTargets();
				}catch(Exception e) {
					logger.debug(METHOD_NAME + "the operation is remove entitlements ");
				}
				 
				Iterator it1 = null;
				try{
					it1 = temporaryProvisioningTarget.iterator();
				}catch (Exception e) {
					logger.debug(CLASS_NAME + METHOD_NAME + "Failed Iterator");
					// TODO: handle exception
				}
				
				if(it1 !=null) {
					while (it1.hasNext()) {
						ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it1
								.next();
						logger.info(METHOD_NAME + "displayableName: " + displayableName);
						String roleName = localProvisioningTarget.getRole();
						logger.info(METHOD_NAME + "displayableName: " + displayableName);
						if (roleName.equalsIgnoreCase(displayableName)) {
							provisioningTargets.add(localProvisioningTarget);
							newProvisioningPlan
									.setProvisioningTargets(provisioningTargets);
						}
						logger.info(METHOD_NAME + "displayableName: " + displayableName);
						//newProvisioningPlan.setComments("USER ID : " + userID);
					}
				} else {
					//not changing the plan
					newProvisioningPlan = masterProvisioningPlan;
				}
				

				logger.info(METHOD_NAME + "newProvisioningPlan: "
						+ newProvisioningPlan.toXml());
				// Now Call the LCM Workflow.....
				String currentTaskIDList = invokeLCMProvisioning(workflow,
						context, newProvisioningPlan, displayableName);
				logger.debug(CLASS_NAME + METHOD_NAME + "currentTaskIDList: "
						+ currentTaskIDList);

				taskIdLists.add(currentTaskIDList);

			}

		} else if (requestFlow.equalsIgnoreCase(FLOW_TYPE_ACCOUNTS_REQUEST) || FLOW_TYPE_PASSWORD_REQUEST.equalsIgnoreCase(requestFlow)
				|| FLOW_TYPE_PENGAKTIFAN.equalsIgnoreCase(requestFlow) || FLOW_TYPE_PENONAKTIFAN.equalsIgnoreCase(requestFlow) 
				|| FLOW_TYPE_CREATE_IDENTITY.equalsIgnoreCase(requestFlow)|| FLOW_TYPE_LIMIT_TELER.equalsIgnoreCase(requestFlow)
				|| FLOW_TYPE_EXTEND_ACCOUNT.equalsIgnoreCase(requestFlow) || requestFlow.equalsIgnoreCase(FLOW_TYPE_PENGURANGAN)){

			String displayableName = "";
			List accountRequestList = masterProvisioningPlan.getAccountRequests();
			
			logger.debug(METHOD_NAME + " get account request");
			
			Iterator it = accountRequestList.iterator();
			String comment = null;
			while (it.hasNext()) {
				AccountRequest localAccountRequest = (AccountRequest) it.next();
				// Now create individual Provisioning Plan....
				ProvisioningPlan newProvisioningPlan = new ProvisioningPlan();
				newProvisioningPlan.add(localAccountRequest);
				if (localAccountRequest.getApplication() != null) {
					displayableName = localAccountRequest.getApplication();
					logger.debug(METHOD_NAME + " application name " + displayableName);
				}
			//	newProvisioningPlan.setComments();
				logger.debug("localAccountRequest will be " + localAccountRequest.toXml());
				//added try when provisioning extend AD account the localAccountRequest don't have Native Identity will fail
				try {
					comment = "USER ID : " + localAccountRequest.getNativeIdentity().toString();
				}catch(Exception e) {
					
				}
				logger.debug("comment : " + comment);
				if (displayableName.equalsIgnoreCase(CommonUtil.AD_APPLICATION)) {
					if (FLOW_TYPE_PENONAKTIFAN.equalsIgnoreCase(requestFlow)){
						//AD
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						Date date = new Date();
						
						if(!"Disable".equalsIgnoreCase(localAccountRequest.getOp().toString())) {
							comment += System.lineSeparator() + "Disable Date : " + localAccountRequest.getArgument("REVOKE_DATE");
						} else {
							comment += System.lineSeparator() + "Disable Date : " + df.format(date);
						}
						
					} else if (FLOW_TYPE_EXTEND_ACCOUNT.equalsIgnoreCase(requestFlow)) {
						comment += System.lineSeparator() + "Extend Date : " + localAccountRequest.getArgument("EXTEND_DATE");
					}
				} else if (displayableName.equalsIgnoreCase(CommonUtil.BASE24_FILE_FEED_APPLICATION) || displayableName.equalsIgnoreCase(CommonUtil.RACF_APPLICATION_NAME)) {
					if (FLOW_TYPE_PENONAKTIFAN.equalsIgnoreCase(requestFlow)) {
						//mainframe //base24
						DateFormat df = new SimpleDateFormat("yyyyMMdd");
						Date date = new Date();
						
						if(!"Disable".equalsIgnoreCase(localAccountRequest.getOp().toString())) {
							comment += System.lineSeparator() + "REVOKE_DATE : " + localAccountRequest.getAttributeRequest(RACFAttribute.REVOKE_DATE).getValue();
						} else {
							comment += System.lineSeparator() + "REVOKE_DATE : " + df.format(date);
						}
						
						logger.debug("trying get argument : " + localAccountRequest.getArgument(RACFAttribute.RESUME_DATE));
						try {
							comment += System.lineSeparator() + "RESUME_DATE : " + localAccountRequest.getArgument(RACFAttribute.RESUME_DATE);
						}catch(Exception e) {
							logger.debug("no argument resume date found in account request!");
						}
					}
				}
				

				logger.debug("comment : " + comment);
				try{
					newProvisioningPlan.setComments(comment);
				}catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
				
				// Now Call the LCM Workflow.....
				String currentTaskIDList = invokeLCMProvisioning(workflow,
						context, newProvisioningPlan, displayableName);
				taskIdLists.add(currentTaskIDList);
			}

		} else {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "Flow is not Account or Access request... hence cant proceed... The flow is " + requestFlow);
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "taskIdLists: " + taskIdLists);
		return taskIdLists;
	}
	
	public static String getApp_Name(ProvisioningPlan plan, String flow) throws GeneralException{
		List lstAccRq = plan.getAccountRequests();
		Iterator it = lstAccRq.iterator();

		String result = null;

		if(!flow.equalsIgnoreCase("AccessRequest")){
		while(it.hasNext()){
		          AccountRequest accRq = (AccountRequest) it.next();
		          try{
		               result = accRq.getArguments().getMap().get("APP_NAME").toString();
		                System.out.println("APP_NAME from notify checker1: " + result);
		           }catch(Exception e) {
		                 System.out.println("failed get app_name");
		           }
		          
		}


		}

		return result;
	}
	
	public static String getUserId(Workflow workflow, String flow){
		String result = null;
		if(flow.equalsIgnoreCase("AccessRequest")){
			result = workflow.get("workItemComments").toString();
			
			result = result.substring(result.indexOf("USER_ID"), result.indexOf("]") - 1);
		         System.out.println("notify checker 1 " + result);
		}

		return result;
	}
	
	public static String getApplication(Workflow workflow, ProvisioningProject project, String flow)
		    throws GeneralException
		  {
		    String METHOD_NAME = "::getApplication::";
		    String application = null;
		    ProvisioningTarget pt = null;
		    
		    logger.debug(CLASS_NAME + METHOD_NAME + "isi project :" + project.toXml());
		    List<ProvisioningTarget> lpt = null;
		    
		    try{
		    	 lpt = project.getProvisioningTargets();
		    	 
		    	 Iterator lpti = lpt.iterator();
				    while (lpti.hasNext())
				    {
				      pt = (ProvisioningTarget)lpti.next();
				      logger.debug(CLASS_NAME + METHOD_NAME + "nama application yang didapat" + pt.getRole());
				      if (pt.getRole().contains("Base24")) {
				        application = "Base24";
				      } else {
				        application = null;
				      }
				    }
		    }catch (Exception e) {
		    	 logger.debug(CLASS_NAME + METHOD_NAME + "Inside loop master plan");
		    	 ProvisioningPlan lpp = project.getMasterPlan();
		    	 List <AccountRequest> acr = lpp.getAccountRequests();
		    	 AccountRequest acrr = null;
		    	 
		    	 Iterator acrl = acr.iterator();
		    	 while(acrl.hasNext()){
		    		 acrr = (AccountRequest) acrl.next();
		    		 logger.debug(CLASS_NAME + METHOD_NAME + "nama application yang didapat" + acrr.getApplication() + "op : " + acrr.getOp().toString());
		    		 if(acrr.getApplication().contains("Base24") && acrr.getOp().toString().contains("Disable")){
						 application = "Base24_Revoke";
					 }else if(acrr.getApplication().contains("Base24")){
						 application = "Base24";
					 }
		    	 }
			}
		    return application;
		  }
	
		  
	public static String setworkItemComments(Workflow workflow, ProvisioningProject project, String flow) throws GeneralException{
		String METHOD_NAME = "::setworkItemComments::";
		
		
		String comment = null;
		logger.debug(CLASS_NAME + METHOD_NAME + "flow : " + flow); 
		logger.debug(CLASS_NAME + METHOD_NAME + "isi project :" + project.toXml());
	
		if(flow.equalsIgnoreCase(FLOW_TYPE_ACCESS_REQUEST)) {
			String roleName = null;
			String applicationName = null;
			List lpt = null;
			SailPointContext context = SailPointFactory.getCurrentContext();
			logger.debug("inside access request .. . . .");
			try {
				 lpt = project.getProvisioningTargets();
				 logger.debug(METHOD_NAME + lpt);
			}catch(Exception e) {
				logger.debug("the plan don't have provisioning target");
				//not sure only racf application. need to check !
				//make sure is EntitlementRemove Operation
				List lAcR = project.getMasterPlan().getAccountRequests();
				Iterator IAcR = lAcR.iterator();
				while(IAcR.hasNext()) {
					AccountRequest AcR = (AccountRequest) IAcR.next();
					if(AcR.getArguments().getMap().get("operation").toString().equalsIgnoreCase("EntitlementRemove")) {
						applicationName = AcR.getApplication();
						logger.debug("application name : " + applicationName);
						break;
					}
				}
				
			}
			
			if(lpt != null) {
				Iterator it = lpt.iterator();
			
				while(it.hasNext()) {
					ProvisioningTarget pt = (ProvisioningTarget) it.next();
					roleName = pt.getRole();
					logger.debug(CLASS_NAME + METHOD_NAME + "roleName is : " + roleName);
				}
			}
			
			
			if(roleName != null) {
				applicationName = CommonUtil.getApplicationNameByRoleName(context, roleName);
				logger.debug(CLASS_NAME + METHOD_NAME + "applicationName is : " + applicationName);
			}
			
			if(applicationName != null) {
				
				if(applicationName.equalsIgnoreCase(CommonUtil.RACF_APPLICATION_NAME)) {
					List qh = null;
					try {
						 qh = project.getQuestionHistory();
					}catch(Exception e) {
						//failed then from pengurangan fungsi
						List lacS = project.getAccountSelections();
						Iterator IacS = lacS.iterator();
						while(IacS.hasNext()) {
							AccountSelection AcS = (AccountSelection) IacS.next();
							if(AcS.getApplicationName().equalsIgnoreCase(CommonUtil.RACF_APPLICATION_NAME)) {
								comment = "USER_ID : " + AcS.getSelection();
								break;
							}
						}
						
					}
					
					if(qh != null) {
						Iterator qhi = qh.iterator();
						
						while(qhi.hasNext()) {
							Question q = (Question)qhi.next();
							if (q.getFieldName().equalsIgnoreCase("IBM MAINFRAME RACF:USER_ID")) {
								comment = "USER_ID : " +  q.getField().getValue().toString();
								logger.debug(METHOD_NAME+ "comment : " + comment);
							}
						}
					}
					
				
				}else if(applicationName.equalsIgnoreCase(CommonUtil.AD_APPLICATION)) {
					
					try {
						List lsAc =  project.getPlan(applicationName).getAccountRequests();
						Iterator itAc = lsAc.iterator();
						while(itAc.hasNext()) {
							AccountRequest AcRq = (AccountRequest) itAc.next();
							
							comment = "USER_ID : " + AcRq.getNativeIdentity().toString();
						}
					}catch(Exception e) {
						comment = "none";
					}
					
					
				}else {
					
					comment = "none";
				}
				
			}
			
			
			
	    	 
		} else if (flow.equalsIgnoreCase(FLOW_TYPE_PENONAKTIFAN)){
			comment = workflow.get("workItemComments").toString();
		} else if (flow.equalsIgnoreCase(FLOW_TYPE_EXTEND_ACCOUNT)) {
			comment = workflow.get("workItemComments").toString();
		}
		
		return comment;
		
	}

	/**
	 * @param workflow
	 * @param context
	 * @param newProvisioningPlan
	 * @param displayableName
	 * @return
	 * @throws GeneralException
	 */
	public static String invokeLCMProvisioning(Workflow workflow,
			SailPointContext context, ProvisioningPlan newProvisioningPlan,
			String displayableName) throws GeneralException {
		String METHOD_NAME = "::invokeLCMProvisioning::";
		String currentTaskIDList = "";
		String removeFunction = null;

		// Getting the initial attribute...
		Attributes localAttributes = workflow.getVariables();
		logger.info(METHOD_NAME + "Got Local Attributes: ");

		// Setting the launch arguments....
		Map launchArgsMap = new HashMap();
		
		//change the flow type 
		if(localAttributes.get("flow").toString().equalsIgnoreCase(FLOW_TYPE_ACCESS_REQUEST)) {
			//check the plan attribute have attribute request to remove 
			List lst = newProvisioningPlan.getAccountRequests();
			Iterator itAr = lst.iterator();
			while(itAr.hasNext()) {
				AccountRequest acRq =(AccountRequest) itAr.next();
				List attRq = acRq.getAttributeRequests();
				Iterator itAtt = attRq.iterator();
				while(itAtt.hasNext()) {
					AttributeRequest attR = (AttributeRequest) itAtt.next();
					if(attR.getOp().toString().equalsIgnoreCase("Remove")) {
						removeFunction = attR.getOp().toString();
						logger.debug(METHOD_NAME + "operation nya : " +  removeFunction);
						break;
					}
				}
			}
		}

		launchArgsMap.put("allowRequestsWithViolations",localAttributes.get("allowRequestsWithViolations"));
		launchArgsMap.put("approvalEmailTemplate",localAttributes.get("approvalEmailTemplate"));
		launchArgsMap.put("approvalMode", localAttributes.get("approvalMode"));
		launchArgsMap.put("approvalScheme",localAttributes.get("approvalScheme"));
		launchArgsMap.put("doRefresh", localAttributes.get("doRefresh"));
		launchArgsMap.put("enableRetryRequest","true");
		launchArgsMap.put("retries", 5);
		launchArgsMap.put("provisioningMaxRetries", 5);
		launchArgsMap.put("provisioningRetryThreshold", 5);
		//launchArgsMap.put("enableRetryRequest",localAttributes.get("enableRetryRequest"));
		launchArgsMap.put("fallbackApprover",localAttributes.get("fallbackApprover"));
		
		if(removeFunction != null && removeFunction.equalsIgnoreCase("Remove")) {
			launchArgsMap.put("flow", FLOW_TYPE_PENGURANGAN);
		}else {
			launchArgsMap.put("flow", localAttributes.get("flow"));
		}
		
		launchArgsMap.put("foregroundProvisioning",
				localAttributes.get("foregroundProvisioning"));
		launchArgsMap.put("identityDisplayName",
				localAttributes.get("identityDisplayName"));
		launchArgsMap.put("identityName", localAttributes.get("identityName"));
		launchArgsMap.put("launcher", localAttributes.get("launcher"));
		launchArgsMap.put("managerEmailTemplate",
				localAttributes.get("managerEmailTemplate"));
		launchArgsMap.put("notificationScheme",
				localAttributes.get("notificationScheme"));
		launchArgsMap.put("numberOfPlan", localAttributes.get("numberOfPlan"));
		launchArgsMap.put("optimisticProvisioning", true);

		launchArgsMap.put("policyScheme", localAttributes.get("policyScheme"));
		launchArgsMap.put("requesterEmailTemplate",
				localAttributes.get("requesterEmailTemplate"));
		launchArgsMap.put("requireViolationReviewComments",
				localAttributes.get("requireViolationReviewComments"));
		launchArgsMap.put("sessionOwner", localAttributes.get("sessionOwner"));
		launchArgsMap.put("userEmailTemplate",
				localAttributes.get("userEmailTemplate"));
		launchArgsMap.put("workItemPriority",
				localAttributes.get("workItemPriority"));
		//try modified the workitemcomments
		
		if(FLOW_TYPE_PENONAKTIFAN.equalsIgnoreCase(localAttributes.get(WF_VARIABLE_FLOW).toString()) || FLOW_TYPE_PENGAKTIFAN.equalsIgnoreCase(localAttributes.get(WF_VARIABLE_FLOW).toString()) ||
		   FLOW_TYPE_ACCOUNTS_REQUEST.equalsIgnoreCase(localAttributes.get(WF_VARIABLE_FLOW).toString()) || FLOW_TYPE_PASSWORD_REQUEST.equalsIgnoreCase(localAttributes.get(WF_VARIABLE_FLOW).toString()) ||
		   FLOW_TYPE_EXTEND_ACCOUNT.equalsIgnoreCase(localAttributes.get(WF_VARIABLE_FLOW).toString()) || localAttributes.get("flow").toString().equalsIgnoreCase(FLOW_TYPE_PENGURANGAN)) {
			String comment = newProvisioningPlan.getComments();
			try{
				launchArgsMap.put("workItemComments", comment);
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
			
			
		}
		
		
		if(localAttributes.get(WF_VARIABLE_FLOW) != null 
				&& "IdentityCreateRequest".equalsIgnoreCase(localAttributes.get(WF_VARIABLE_FLOW).toString())){
			launchArgsMap.put("identity", new Identity());
		}else{
		launchArgsMap.put("identity",localAttributes.get("identity"));
		}

		// Only Item which is computed and then put across...
		launchArgsMap.put("plan", newProvisioningPlan);

		
		logger.info(METHOD_NAME + "Workflow Instantiation Map Created...." + launchArgsMap.toString());

		// creating the workflow launcher and then will go for workflow
		// instanciation...
		WorkflowLaunch workflowLauncher = new WorkflowLaunch();

		Workflow wf = (Workflow) context.getObjectByName(Workflow.class,
				WORKFLOW_NAME_LCM_PROVISIONING);

		logger.info(METHOD_NAME + "SubProcess Workflow found: " + wf.getName()
				+ " ID: " + wf.getId());

		workflowLauncher.setWorkflowName(wf.getName());
		workflowLauncher.setWorkflowRef(wf.getReferenceId());
		workflowLauncher.setCaseName("Approval for Role: " + displayableName);
		workflowLauncher.setVariables(launchArgsMap);
		
		String flow = launchArgsMap.get("flow") == null ? "" : (String)launchArgsMap.get("flow");
		
		
		if(FLOW_TYPE_PASSWORD_REQUEST.equalsIgnoreCase(flow)){
			workflowLauncher.setTargetClass(Link.class);
		//	workflowLauncher.setTargetId((String)launchArgsMap.get("identityDisplayName"));
		//	workflowLauncher.setTargetName((String)launchArgsMap.get("identityDisplayName"));
		//	workflowLauncher.set
		}else{
			Identity identity = context.getObjectByName(Identity.class,
					localAttributes.get("identityName").toString());

			workflowLauncher.setTargetClass(Identity.class);
			workflowLauncher.setTargetId(identity.getId());
			workflowLauncher.setTargetName(identity.getName());
		}
		
		workflowLauncher.setSessionOwner(localAttributes.get("launcher")
				.toString());

		Workflower workflower = new Workflower(context);
		logger.info(METHOD_NAME + "Trying to Launch the SubProcess Workflow: ");
		workflowLauncher = workflower.launch(workflowLauncher);
		logger.info(METHOD_NAME + "workflowLauncher.getTaskResult(): "
				+ workflowLauncher.getTaskResult());

		logger.info(METHOD_NAME
				+ "SubProcess Workflow successfully launched....");

		currentTaskIDList = workflowLauncher.getTaskResult().getId();

		return currentTaskIDList;
	}

	/**
	 * @param workflow
	 * @return
	 * @throws GeneralException
	 */
	public static String checkSubProcessStatusAndTakeAction(Workflow workflow,
			WorkflowContext wfContext, SailPointContext context)
			throws GeneralException {
		String METHOD_NAME = "::checkSubProcessStatusAndTakeAction::";
		String provisioningCheckStatusInterval = "0";

		logger.info(METHOD_NAME + "Inside...");

		// SailPointContext context = wfContext.getSailPointContext();

		Attributes workflowAttributes = workflow.getVariables();
		logger.info(METHOD_NAME + "Got workflow attributes...");
		List taskIdLists = new ArrayList();

		if (workflowAttributes.get(WF_VARIABLE_TASK_LIST_ID) != null) {
			taskIdLists = workflowAttributes.getList(WF_VARIABLE_TASK_LIST_ID);

			// Check all the invoked sub process status.. if the status is
			// completed.. then send "end" value to workflow...
			Iterator it = taskIdLists.iterator();
			while (it.hasNext()) {
				String taskIdString = (String) it.next();

				TaskResult localTaskResult = context.getObjectById(
						TaskResult.class, taskIdString);

				logger.info(METHOD_NAME + "Task ID: " + localTaskResult.getId()
						+ "  Name: " + localTaskResult.getName() + " State: "
						+ localTaskResult.isComplete());

				if (!localTaskResult.isComplete()) {

					provisioningCheckStatusInterval = "5";
					logger.info(METHOD_NAME
							+ "Returning provisioningCheckStatusInterval: "
							+ provisioningCheckStatusInterval);
					return provisioningCheckStatusInterval;

				} else {
					provisioningCheckStatusInterval = "0";
				}

			}

		}
		logger.info(METHOD_NAME + "Returning provisioningCheckStatusInterval: "
				+ provisioningCheckStatusInterval);
		return provisioningCheckStatusInterval;

	}
	
	public static void addCommentToWorkitem(WorkflowContext wfcontext, Workflow workflow) throws GeneralException{
		WorkItem wrkitm = wfcontext.getWorkItem();
		wrkitm.addComment("test set comment ");
		logger.debug("trying set comment to workitem");
		SailPointContext context = wfcontext.getSailPointContext();
		context.decache();
		context.saveObject(wrkitm);
		context.commitTransaction();
	}

	/**
	 * This method will close the master identity request as its copied to child
	 * and child will take care of the provisioning....
	 * 
	 * @param workflow
	 * @throws GeneralException
	 */
	public static void closeIdentityRequest(Workflow workflow,
			SailPointContext context) throws GeneralException {
		String METHOD_NAME = "::closeIdentityRequest::";
		logger.info(METHOD_NAME + "Inside...");

		// Getting the global variable of workflow....
		Attributes attributes = workflow.getVariables();

		// Getting the Identity Request ID....
		String identityRequestId = (String) attributes.get("identityRequestId");
		logger.info(METHOD_NAME + "Identity Request String : "
				+ identityRequestId);
		IdentityRequest masterIdentityRequest = context.getObjectByName(
				IdentityRequest.class, identityRequestId);

		if (masterIdentityRequest == null) {
			logger.info(METHOD_NAME + "No Identity Request Found...");
		}
		logger.info(METHOD_NAME + "Master Identity Request Name: "
				+ masterIdentityRequest.getName());

		// Getting the Identity Request Items from the master Identity
		// Request....
		List masterRequestItemsList = masterIdentityRequest.getItems();
		logger.info(METHOD_NAME + "Identity Request Size: "
				+ masterRequestItemsList.size());

		Iterator it = masterRequestItemsList.iterator();
		List updatableList = new ArrayList();

		// Iterating over each Identity Request Items and setting the status to
		// finished...
		while (it.hasNext()) {

			IdentityRequestItem localIdentityRequestItem = (IdentityRequestItem) it
					.next();
			logger.info(METHOD_NAME + "Identity Request Item: "
					+ localIdentityRequestItem.getName());
			localIdentityRequestItem.setApprovalState(State.Finished);
			localIdentityRequestItem.setApproverName("spadmin");
			localIdentityRequestItem
					.setProvisioningState(ProvisioningState.Finished);
			localIdentityRequestItem.setModified(new Date());
			updatableList.add(localIdentityRequestItem);

		}

		// Setting the IdentityRequestItems to complete status and the Master
		// Identity Request item to success status....
		masterIdentityRequest.setItems(updatableList);
		masterIdentityRequest.setState("End");
		masterIdentityRequest.setExecutionStatus(ExecutionStatus.Completed);
		masterIdentityRequest.setEndDate(new Date());
		masterIdentityRequest.setCompletionStatus(CompletionStatus.Success);

		// Commiting the transaction....
		context.startTransaction();
		context.saveObject(masterIdentityRequest);
		context.commitTransaction();
		context.decache();
		logger.info(METHOD_NAME + "Completed...");

	}

	/**
	 * @param workflow
	 * @param approverKey
	 * @return
	 */
	public static String getApproverNameFromApproverKey(Workflow workflow,
			String approverKey) {
		String METHOD_NAME = "::getApproverNameFromApproverKey::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		logger.debug(CLASS_NAME + METHOD_NAME + "approverKey: " + approverKey);

		String approverString = "";
		Attributes workflowVariableAttributes = workflow.getVariables();
		Map approverMap = (Map) workflowVariableAttributes
				.get(WF_VARIABLE_APPROVAL_MAP);

		if (approverMap == null) {
			logger.debug(CLASS_NAME + METHOD_NAME + "approverMap is null ");
			return approverString;
		}

		if (approverMap.get(approverKey) != null) {
			if (!approverMap.get(approverKey).toString().equalsIgnoreCase("")) {
				Identity identity = (Identity) approverMap.get(approverKey);
				approverString = identity.getName();
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "Returning approverString: " + approverString
						+ " for approverKey: " + approverKey);

				return approverString;
			}
		}
		logger.debug(CLASS_NAME + METHOD_NAME + "Returning approverString: "
				+ approverString
				+ " as null as none of the correct approver key is found...");
		return approverString;
	}

	/**
	 * @return
	 * @throws GeneralException
	 */
	public static Map getBCAApprovers(WorkflowContext wfcontext, Workflow workflow, String launcher, ProvisioningProject project) throws GeneralException {
		String METHOD_NAME = "::getBCAApprovers::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside..." + "Workflow : " + workflow.toXml() + "projectnya : " + project.toXml());

		// Variable Declarations...
		Map approverMap = new HashMap();

		// Getting workflow global variables...
		Attributes workflowVariableAttributes = workflow.getVariables();

		// Getting local sailpoint context...
		SailPointContext localSPContext = wfcontext.getSailPointContext();

		// Getting Target User Value.....
		String targetIdentityName = (String) workflowVariableAttributes
				.get("identityName");
		logger.debug(CLASS_NAME + METHOD_NAME + "	"
				+ targetIdentityName);
		Identity identity = localSPContext.getObjectByName(Identity.class,
				targetIdentityName);
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Person for whom request is made: " + identity.getName());

		// Getting requester identity...
		Identity requester = localSPContext.getObjectByName(Identity.class,
				launcher);
		logger.debug(CLASS_NAME + METHOD_NAME + "requester: "
				+ requester.getName());

		String applicationName = "";
		String mainframeSubApplicationName = "";
		String op = "Create"; //Operation by default
		String requestFlow = (String) workflowVariableAttributes
				.get(WF_VARIABLE_FLOW);
		
		if (requestFlow.equalsIgnoreCase(FLOW_TYPE_ACCESS_REQUEST)) {
			// Get sub process approval set....
			ApprovalSet subProcessApprovalSet = (ApprovalSet) workflowVariableAttributes
					.get(WF_VARIABLE_APPROVAL_SET);

			// Get Application Name ...
			//check if the plan is EntitlementRemove request
			boolean isEntitlementRemove = false;
			ProvisioningPlan plan = (ProvisioningPlan) workflowVariableAttributes.get(WF_VARIABLE_PROVISIONING_PLAN);
			List lAcR = plan.getAccountRequests();
			Iterator IAcR = lAcR.iterator();
			
			while(IAcR.hasNext()) {
				AccountRequest AcR = (AccountRequest) IAcR.next();
				if(AcR.getArguments().getMap().get("operation").toString().equalsIgnoreCase("EntitlementRemove")) {
					applicationName = AcR.getApplication();
					isEntitlementRemove = true;
					break;
				}
			}
			
			if(!isEntitlementRemove) {
				applicationName = getApplicatoinNameFromApprovalSet(localSPContext, subProcessApprovalSet);
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + "ApplicationName : " + applicationName);
			
			if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(applicationName)){
				if(!isEntitlementRemove) {
					mainframeSubApplicationName = getMainframeApplicatoinNameFromApprovalSet(localSPContext, subProcessApprovalSet);
				}else {
					mainframeSubApplicationName = applicationName;
				}
				
			}else{
				mainframeSubApplicationName = applicationName;
			}
			
		} else if (requestFlow.equalsIgnoreCase(FLOW_TYPE_ACCOUNTS_REQUEST) || FLOW_TYPE_PASSWORD_REQUEST.equalsIgnoreCase(requestFlow) || requestFlow.equalsIgnoreCase(FLOW_TYPE_PENGURANGAN)) {
           // for set approval set in provisioning plan
			ProvisioningPlan masterProvisioningPlan = (ProvisioningPlan) workflowVariableAttributes
					.get(WF_VARIABLE_PROVISIONING_PLAN);

			List accountRequestList = masterProvisioningPlan
					.getAccountRequests();
			Iterator it = accountRequestList.iterator();
			while (it.hasNext()) {
				AccountRequest localAccountRequest = (AccountRequest) it.next();
				// Now create individual Provisioning Plan....
				ProvisioningPlan newProvisioningPlan = new ProvisioningPlan();
				newProvisioningPlan.add(localAccountRequest);
				if (localAccountRequest.getApplication() != null) {
					applicationName = localAccountRequest.getApplication();
					logger.debug(CLASS_NAME + METHOD_NAME + "applicationName: "
							+ applicationName);
				}
				
				if(localAccountRequest.getOperation() != null){
					op = localAccountRequest.getOperation().toString();
					logger.debug(CLASS_NAME + METHOD_NAME + " operation : " + op);
				}
			}
		}
				if(requestFlow.equalsIgnoreCase(FLOW_TYPE_PENONAKTIFAN) || requestFlow.equalsIgnoreCase(FLOW_TYPE_PENGAKTIFAN)){
					approverMap = BCAApprovalMatrixRule.getApproverListWithOutApp2Checker2(localSPContext, identity, applicationName, mainframeSubApplicationName, requester, op);
					logger.debug(CLASS_NAME + METHOD_NAME + "Inside Map Without Approver 2 and Checker 2");
				}else if (requestFlow.equalsIgnoreCase(FLOW_TYPE_PASSWORD_REQUEST)){
					approverMap = BCAApprovalMatrixRule.getApproverListWithOutChecker2(localSPContext, identity, applicationName, mainframeSubApplicationName, requester, op);
					logger.debug(CLASS_NAME + METHOD_NAME + "Inside Map Without Checker 2");
				}else{
					//access request
					ProvisioningPlan plan = project.getMasterPlan();
					logger.debug(CLASS_NAME + METHOD_NAME + "Master plan nya : " + plan.toXml());
					List <AccountRequest> acr = plan.getAccountRequests();
					AccountRequest acrr = null;
					Iterator acrl = acr.iterator();
					while(acrl.hasNext()) {
						acrr = (AccountRequest) acrl.next();
						String USER_ID = acrr.getNativeIdentity();
						logger.debug(CLASS_NAME + METHOD_NAME + "user Id nya : " + USER_ID);
						approverMap = BCAApprovalMatrixRule.getApproverList(localSPContext, identity, applicationName, mainframeSubApplicationName, requester, op, USER_ID);
					}
					logger.debug(CLASS_NAME + METHOD_NAME + "Inside Normal Map");
				}
		logger.debug(CLASS_NAME + METHOD_NAME + "approverMap : " + approverMap);
		return approverMap;

	}

	/**
	 * @param localSPContext
	 * @param subProcessApprovalSet
	 * @return
	 * @throws GeneralException
	 */
	public static String getApplicatoinNameFromApprovalSet(
			SailPointContext localSPContext, ApprovalSet subProcessApprovalSet)
			throws GeneralException {
		String METHOD_NAME = "::getApplicatoinNameFromApprovalSet::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		List approvalItemList = subProcessApprovalSet.getItems();
		
		logger.debug(CLASS_NAME + METHOD_NAME + " get items");
		
		Iterator it = approvalItemList.iterator();
		
		logger.debug(CLASS_NAME + METHOD_NAME + "get the iterator");
		
		while (it.hasNext()) {
			ApprovalItem localApprovalItem = (ApprovalItem) it.next();

			logger.debug(CLASS_NAME + METHOD_NAME + localApprovalItem.getDisplayValue() + " value :" + localApprovalItem.getValue());
			/*String roleNameString = (String) localApprovalItem
					.getDisplayValue();*/
			
			
			Map appItem = localApprovalItem.getAttributes().getMap();
			
			String roleNameString = (String)appItem.get("name");
			
			logger.debug(CLASS_NAME + METHOD_NAME + "role name : " + roleNameString);
						
			Bundle role = localSPContext.getObjectByName(Bundle.class,
					roleNameString);
			
			String applicationName = (String) role
					.getAttribute(ROLE_EXTENDED_ATTRIBUTE_1);
			logger.debug(CLASS_NAME + METHOD_NAME + "applicationName: "
					+ applicationName);
			if (applicationName == null || applicationName.equalsIgnoreCase("")) {
				logger.debug(CLASS_NAME
						+ METHOD_NAME
						+ "Role Definition Not Proper please check the role definition and make changes to it. ApplicationName is null or blank...");
			}

			return applicationName;

		}

		return null;
	}
	
	/**
	 * @param localSPContext
	 * @param subProcessApprovalSet
	 * @return
	 * @throws GeneralException
	 */
	public static String getMainframeApplicatoinNameFromApprovalSet(
			SailPointContext localSPContext, ApprovalSet subProcessApprovalSet)
			throws GeneralException {
		String METHOD_NAME = "::getMainframeApplicatoinNameFromApprovalSet::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		List approvalItemList = subProcessApprovalSet.getItems();
		
		logger.debug(CLASS_NAME + METHOD_NAME + " get items");
		
		Iterator it = approvalItemList.iterator();
		
		logger.debug(CLASS_NAME + METHOD_NAME + "get the iterator");
		
		while (it.hasNext()) {
			ApprovalItem localApprovalItem = (ApprovalItem) it.next();
			
			String roleName = localApprovalItem.getDisplayValue();

			logger.debug(CLASS_NAME + METHOD_NAME + roleName);
			
			String delimiter = "-";
			
			int indexDelimiter = roleName.indexOf(delimiter) ;
			
			if(roleName != null && indexDelimiter > 0){
				
				String mainframeApplicationName =  roleName.substring(0, indexDelimiter).trim();
				
				logger.debug(CLASS_NAME + METHOD_NAME + " mainframe applicationName: "
						+ mainframeApplicationName);
				if (mainframeApplicationName == null || mainframeApplicationName.equalsIgnoreCase("")) {
					logger.debug(CLASS_NAME
							+ METHOD_NAME
							+ "Role Definition Not Proper please check the role definition and make changes to it. ApplicationName is null or blank...");
				}

				return mainframeApplicationName;
				
			}
		}

		return null;
	}

	/**
	 * This method will convert the approver map to CSV string which will be
	 * sent to the approval sub process...
	 *
	 * @param approverMap
	 * @return
	 */
	public static String getApproverListToString(Map approverMap) {
		String METHOD_NAME = "::getApproverListToString::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String approverString = "";
		final String COMMA = ",";
		// final String NOTHING = "";
		
		
		logger.debug(CLASS_NAME +METHOD_NAME + "all approver list : " + approverMap);
		
		if (approverMap.get(BCAApproverMatrixConstant.APPROVER1_KEY) != null) {
			if (!approverMap.get(BCAApproverMatrixConstant.APPROVER1_KEY)
					.toString().equalsIgnoreCase("")) {
				Identity identity = (Identity) approverMap
						.get(BCAApproverMatrixConstant.APPROVER2_KEY);
				approverString = COMMA + identity.getName();
			}
		}

		if (approverMap.get(BCAApproverMatrixConstant.APPROVER2_KEY) != null) {

			if (!approverMap.get(BCAApproverMatrixConstant.APPROVER2_KEY)
					.toString().equalsIgnoreCase("")) {
				Identity identity = (Identity) approverMap
						.get(BCAApproverMatrixConstant.APPROVER2_KEY);

				approverString = COMMA + identity.getName();
			}
		}

		if (approverMap.get(BCAApproverMatrixConstant.CHECKER1_KEY) != null) {

			if (!approverMap.get(BCAApproverMatrixConstant.CHECKER1_KEY)
					.toString().equalsIgnoreCase("")) {

				Identity identity = (Identity) approverMap
						.get(BCAApproverMatrixConstant.CHECKER1_KEY);

				approverString = COMMA + identity.getName();
			}
		}

		if (approverMap.get(BCAApproverMatrixConstant.CHECKER2_KEY) != null) {
			if (!approverMap.get(BCAApproverMatrixConstant.CHECKER2_KEY)
					.toString().equalsIgnoreCase("")) {
				Identity identity = (Identity) approverMap
						.get(BCAApproverMatrixConstant.CHECKER2_KEY);

				approverString = COMMA + identity.getName();
			}
		}

		if (approverMap.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY) != null)
			if (!approverMap.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY)
					.toString().equalsIgnoreCase("")) {

				Identity identity = (Identity) approverMap
						.get(BCAApproverMatrixConstant.SA_APLIKASI_KEY);
				approverString = COMMA + identity.getName();
			}

		// Trimming the first Comma...
		if (approverString.startsWith(",")) {
			approverString = approverString.substring(1,
					approverString.length());
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "approverString: "
				+ approverString);

		return approverString;

	}

	/**
	 * @param workflow
	 * @return
	 */
	public static String substractApprovalItemNumber(Workflow workflow) {
		String METHOD_NAME = "::substractApprovalItemNumber::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String numberOfApprovalSetRemaining = "";
		Attributes workflowVariableAttributes = workflow.getVariables();
		String temporaryString = (String) workflowVariableAttributes
				.get(WF_VARIABLE_NUMBER_OF_APPROVAL_SET_REMAINING);
		logger.debug(CLASS_NAME + METHOD_NAME + "temporaryString: "
				+ temporaryString);
		int i = Integer.parseInt(temporaryString);
		i = i - 1;
		numberOfApprovalSetRemaining = Integer.toString(i);
		logger.debug(CLASS_NAME + METHOD_NAME + "temporaryString new value: "
				+ temporaryString);

		return numberOfApprovalSetRemaining;

	}

	/**
	 * The method is used to update the variable in workflow used for
	 * looping....
	 * 
	 * @param workflow
	 * @return
	 */
	public static String getNumberOfApprovalItem(Workflow workflow) {
		String METHOD_NAME = "::getNumberOfApprovalItem::";

		String numberOfApprovalItem = "";

		Attributes workflowVariableAttributes = workflow.getVariables();

		ApprovalSet approvalSet = (ApprovalSet) workflowVariableAttributes
				.get(WF_VARIABLE_APPROVAL_SET);
		List approvalItemList = approvalSet.getItems();

		// List provisioningPlanList = provisioningProject.getPlans();

		numberOfApprovalItem = Integer.toString(approvalItemList.size());
		logger.debug(CLASS_NAME + METHOD_NAME + "Number of Items Requested: "
				+ numberOfApprovalItem);

		return numberOfApprovalItem;
	}

	/**
	 * @param workflow
	 * @return
	 * @throws GeneralException
	 */
	public static ApprovalSet removeSubProcessApprovalSet(Workflow workflow)
			throws GeneralException {
		String METHOD_NAME = "::removeSubProcessApprovalSet::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside..");
		ApprovalSet temporaryApprovalSet = new ApprovalSet();
		/*
		 * Attributes workflowVariableAttributes = workflow.getVariables();
		 * ApprovalSet temporaryApprovalSet = (ApprovalSet)
		 * workflowVariableAttributes .get(WF_VARIABLE_TEMPORARY_APPROVAL_SET);
		 * ApprovalSet subProcessApprovalSet = (ApprovalSet)
		 * workflowVariableAttributes
		 * .get(WF_VARIABLE_SUB_PROCESS_APPROVAL_SET); List approvalItemList =
		 * subProcessApprovalSet.getItems(); Iterator iterator =
		 * approvalItemList.iterator(); while (iterator.hasNext()) {
		 * ApprovalItem localApprovalItem = (ApprovalItem) iterator.next();
		 * temporaryApprovalSet.remove(localApprovalItem); }
		 */
		return temporaryApprovalSet;

	}

	/**
	 * Split the workflow approval set to pass it on to the sub process....
	 * 
	 * @param workflow
	 * @throws GeneralException
	 */
	public static ApprovalSet splitApprovalSet(Workflow workflow)
			throws GeneralException {
		String METHOD_NAME = "::splitApprovalSet::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		ApprovalSet subProcessApprovalSet = new ApprovalSet();

		// SailPointContext context = wfcontext.getSailPointContext();
		Attributes workflowVariableAttributes = workflow.getVariables();

		String identity = (String) workflowVariableAttributes
				.get(WF_VARIABLE_IDENTITY_NAME);
		if (identity == null || identity.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity Name is null...");
		} else {
			logger.debug(CLASS_NAME + METHOD_NAME + "identity: " + identity);
		}

		// Getting the value for number of approval item to process...
		String numberOfApprovalItemToProcess = (String) workflowVariableAttributes
				.get(WF_VARIABLE_NUMBER_OF_APPROVAL_SET_REMAINING);
		if (numberOfApprovalItemToProcess == null
				|| numberOfApprovalItemToProcess.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Number of Approval Item is null...");
			return null;
		} else {

			logger.debug(CLASS_NAME + METHOD_NAME
					+ "numberOfApprovalItemToProcess: "
					+ numberOfApprovalItemToProcess);
		}

		// Original Approval Set....
		ApprovalSet approvalSet = (ApprovalSet) workflowVariableAttributes
				.get(WF_VARIABLE_TEMPORARY_APPROVAL_SET);
		if (approvalSet == null) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Temporary Approval Set is null...");
		} else {

			logger.debug(CLASS_NAME + METHOD_NAME + "Temporary Approval Set: "
					+ approvalSet.toXml());
		}

		List approvalItemList = approvalSet.getItems();

		if (approvalItemList == null) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Approval Item List is Null...");
		}

		int numberOfOriginalWorkItem = approvalItemList.size();
		logger.debug(CLASS_NAME + METHOD_NAME + "Original Approval Set Size: "
				+ numberOfOriginalWorkItem);

		int i = Integer.parseInt(numberOfApprovalItemToProcess);

		if (i > 0) {

			// Get the temporary approval set...
			ApprovalSet temporaryApprovalSet = (ApprovalSet) workflowVariableAttributes
					.get(WF_VARIABLE_TEMPORARY_APPROVAL_SET);
			if (temporaryApprovalSet == null) {
				return null;
			}

			// Get the approval item list and iterate over it...
			List localApprovalItemList = approvalSet.getItems();
			Iterator it = localApprovalItemList.iterator();
			while (it.hasNext()) {
				ApprovalItem localApprovalItem = (ApprovalItem) it.next();
				logger.debug(CLASS_NAME + METHOD_NAME + "localApprovalItem: "
						+ localApprovalItem.toXml());

				// Create the sub process approval set... which will be updated
				// in the workflow..

				subProcessApprovalSet.add(localApprovalItem);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "subProcessApprovalSet: "
						+ subProcessApprovalSet.toXml());

				return subProcessApprovalSet;

				// Remove approvalItem from the approvalCopySet...
				// temporaryApprovalSet.remove(localApprovalItem);
				// logger.debug(CLASS_NAME
				// + METHOD_NAME
				// + "temporaryApprovalSet after removing the approval Item: "
				// + temporaryApprovalSet.toXml());
				/*
				 * // Setting variable attribute....
				 * workflowVariableAttributes.put("subProcessApprovalSet",
				 * subProcessApprovalSet);
				 * workflowVariableAttributes.put("approvalScheme",
				 * APPROVAL_SCHEME);
				 * workflowVariableAttributes.put("temporaryApprovalSet",
				 * temporaryApprovalSet);
				 * 
				 * // Updating workflow with variables...
				 * workflow.setVariables(workflowVariableAttributes);
				 * logger.debug(CLASS_NAME + METHOD_NAME +
				 * "Going for commiting the transaction..." + workflow.toXml());
				 * 
				 * context.startTransaction(); context.saveObject(workflow);
				 * context.commitTransaction(); context.decache();
				 * logger.debug(CLASS_NAME + METHOD_NAME +
				 * "Transaction commited...");
				 */
			}

		}

		// logger.debug(CLASS_NAME + METHOD_NAME + "Workflow: " +
		// workflow.toXml());
		return subProcessApprovalSet;

	}
	
	public static String getWorkItemDesc(Workflow workflow, WorkflowContext wfcontext, String stepApprover) throws GeneralException {
		SailPointContext context = wfcontext.getSailPointContext();
	
		Identity identityTarget = IdentityUtil.searchIdentity(context, IdentityAttribute.NAME, workflow.get("identityName").toString());
		
		String infoTarget = null;
		
		infoTarget = identityTarget.getAttribute(IdentityAttribute.DISPLAY_NAME).toString() ;
		
		try {
			infoTarget += " - " + identityTarget.getAttribute(IdentityAttribute.SALUTATION_NAME).toString();
		}catch(Exception e) {
			logger.debug("failed to get attribute salutation name");
		}
		
		try {
			infoTarget += " - " + identityTarget.getAttribute(IdentityAttribute.POSITION_NAME).toString();
		}catch(Exception e) {
			logger.debug("failed to get attribute position name");
		}
		
		
	/*	 infoTarget = identityTarget.getAttribute(IdentityAttribute.DISPLAY_NAME).toString() + " - " + identityTarget.getAttribute(IdentityAttribute.SALUTATION_NAME).toString() + " - " +
				identityTarget.getAttribute(IdentityAttribute.POSITION_NAME).toString();
		*/
		
		String wrkItemDesc = "BCA Approval for " + stepApprover + ". " +System.lineSeparator() + 
				"Target Information : " + infoTarget ;

		return wrkItemDesc;
	}
	
	
	//TESTING
	public static String getAttributeFromWorkflow(Workflow workflow, String req, ProvisioningPlan plan) {
		
		String result = null;

		List lstAccRq = plan.getAccountRequests();
		Iterator it = lstAccRq.iterator();


		while(it.hasNext()){
		          AccountRequest accRq = (AccountRequest) it.next();
		          try{
		               result = accRq.getArguments().getMap().get("APP_NAME").toString();
		                System.out.println("APP_NAME : " + result);
		          }catch(Exception e) {
		                 
		          }
		          
		}

		
		
		result = workflow.get("workItemComments").toString();
		result = result.substring(result.indexOf("USER_ID"), result.indexOf("]") - 1);
		
		return result;
	}
}
