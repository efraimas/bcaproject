package sailpoint.workflow.rule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.AccountSelection;
import sailpoint.object.ApprovalItem;
import sailpoint.object.ApprovalSet;
import sailpoint.object.Attributes;
import sailpoint.object.AuditEvent;
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.ProvisioningTarget;
import sailpoint.object.SignOffHistory;
import sailpoint.object.Source;
import sailpoint.object.WorkItem;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowCase;
import sailpoint.object.Workflow.Approval;
import sailpoint.object.Workflow.Step;
import sailpoint.tools.GeneralException;
import sailpoint.workflow.WorkflowContext;

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class BCAAuditEvent {
	public static String CLASS_NAME = "::BCAAuditEvent::";
	public static Logger logger = Logger.getLogger("sailpoint.workflow.rule.BCAAuditEvent");

	/**
	 * We are setting all the items in a map and is placing it inside the
	 * atttributes filed... we can retrive these values per row and can use it
	 * later.....
	 * 
	 */
	public static class AuditEventAttributes {
		public static final String REQUEST_ID = "requestId";
		public static final String REQUEST_DATE = "requestDate";
		public static final String APPLICATOIN_NAME = "applicationName";
		// public static final String OPERATION = "operation";
		public static final String USER_ID = "userID";
		public static final String REQUESTER = "requester";
		public static final String REQUESTER_BRANCH_CODE = "requesterBranchCode";
		public static final String APPROVER1 = "approver1";
		public static final String APPROVER2 = "approver2";
		public static final String CHECKER1 = "checker1";
		public static final String CHECKER2 = "checker2";
		public static final String SA_APPLIKASI = "saAplikasi";
		public static final String ACCOUNT_ID = "accountID";
		public static final String REQUEST_STATUS = "state";
		public static final String IS_REJECTED = "isRejected";
	}

	/**
	 * This steps should match the list of approval step mentioned in workflow
	 * "BCA Provisioning Approval SubProcess"
	 *
	 */
	public static class BCAApprovalStepName {
		public static final String STEP_APPROVER_1 = "Approver 1";
		public static final String STEP_APPROVER_2 = "Approver 2";
		public static final String STEP_CHECKER_1 = "Checker 1";
		public static final String STEP_CHECKER_2 = "Checker 2";
		public static final String STEP_SA_APLIKASI = "SA Aplikasi";

	}

	/**
	 * 
	 * This method will be called from the workflow which will be creating BCA
	 * specific audit events.... this audit events will be read by custom report
	 * to generate the desired result....
	 * 
	 * @param context
	 * @param workflowContext
	 * @param workItem
	 * @throws GeneralException 
	 * @throws GeneralException
	 * @throws ParseException
	 */
	
	
	public static void updateTracking(String step,SailPointContext ctx, WorkflowContext wfCtx, String stepName, String identityName) throws GeneralException{
		PreparedStatement stmt=null;

		logger.debug("Update Tracking called");		
		
		Connection conn = ctx.getJdbcConnection();
		try{
			String username = ctx.getUserName();
			
			logger.debug("Username found with value : " + username);
			
			if(username == null){
				return;
			}else if(username == "SailPointContextRequestFilter"){
				if(identityName != null){
					username = identityName;
				}
			}
			
			if("init".equalsIgnoreCase(step)){
				Identity id = CommonUtil.searchIdentity(ctx, IdentityAttribute.NAME, username);
				
				
				String branchCode = (String) id.getAttribute(IdentityAttribute.BRANCH_CODE);
				
				if("0998".equalsIgnoreCase(branchCode) || "0960".equalsIgnoreCase(branchCode)){
					branchCode = (String) id.getAttribute(IdentityAttribute.DIVISION_CODE);
				}
				String requestId = wfCtx.getWorkflow().getVariables().getString("identityRequestId");
				
				logger.debug("Update Tracking called with reqId" + requestId);
				stmt = conn.prepareStatement("insert into cus_activity_user(request_id,branch_code,request_date,update_date,last_action,own_description) " + 
											 "values(?,?,sysdate,sysdate,?,"+
											 "CASE WHEN (SELECT distinct concat(sirt.operation,sirt.application) " +
											 "FROM spt_identity_request sir " +
											 "JOIN spt_identity_request_item sirt ON sir.id = sirt.identity_request_id " +
											 "WHERE sir.name = ?) IN ('DeleteIBM MAINFRAME RACF','DeleteIBM MAINFRAME PROD') " +
											 "THEN " +
											 "(SELECT LISTAGG(sie.value,',') WITHIN GROUP (ORDER BY sie.value) " +
											 "FROM spt_identity_request sir " +
											 "JOIN spt_identity_request_item sirt ON sir.id = sirt.identity_request_id " +
											 "JOIN spt_identity_entitlement sie ON sir.target_id = sie.identity_id and sirt.native_identity = sie.native_identity " +
											 "WHERE sir.name = ?) " +
											 "ELSE null " +
											 "END)");
				stmt.setString(1,requestId);
				stmt.setString(2,branchCode);
				stmt.setString(3,"initiate");
				stmt.setString(4,requestId);
				stmt.setString(5,requestId);
				
			}else{
				String requestId = wfCtx.getWorkflow().getVariables().getString("identityRequestId");
				
				System.out.println("stmt::" + stepName);
				String action = step;
				logger.debug("Update Tracking called with reqId" + requestId);
				if(stepName.equalsIgnoreCase(BCAApprovalStepName.STEP_APPROVER_1)){
					stmt = conn.prepareStatement("update cus_activity_user set update_date=sysdate,approver1_date=sysdate,approver1_user=?,approver1_action=?,last_action=? where REQUEST_ID=?");
				}else if(stepName.equalsIgnoreCase(BCAApprovalStepName.STEP_APPROVER_2)){
					stmt = conn.prepareStatement("update cus_activity_user set update_date=sysdate,approver2_date=sysdate,approver2_user=?,approver2_action=?,last_action=? where REQUEST_ID=?");							
				}else if(stepName.equalsIgnoreCase(BCAApprovalStepName.STEP_CHECKER_1)){
					stmt = conn.prepareStatement("update cus_activity_user set update_date=sysdate,checker1_date=sysdate,checker1_user=?,checker1_action=?,last_action=? where REQUEST_ID=?");							
				}else if(stepName.equalsIgnoreCase(BCAApprovalStepName.STEP_CHECKER_2)){
					stmt = conn.prepareStatement("update cus_activity_user set update_date=sysdate,checker2_date=sysdate,checker2_user=?,checker2_action=?,last_action=? where REQUEST_ID=?");							
				}else if(stepName.equalsIgnoreCase(BCAApprovalStepName.STEP_SA_APLIKASI)){
					stmt = conn.prepareStatement("update cus_activity_user set update_date=sysdate,sa_aplikasi_date=sysdate,sa_aplikasi_user=?,sa_aplikasi_action=?,last_action=? where REQUEST_ID=?");							
				}					
				stmt.setString(1,username);
				stmt.setString(2,action);
				stmt.setString(3,action);
				stmt.setString(4,requestId);

			}
			stmt.execute();
			ctx.commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			throw new GeneralException(e);
		}
		
				
	}

	public static void createCustomAuditsEvent(SailPointContext context, WorkflowContext workflowContext, WorkItem item)
			throws GeneralException, ParseException {
		String METHOD_NAME = "::createCustomAuditsEvent::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		logger.debug(CLASS_NAME + METHOD_NAME + "Workflow Context: " + workflowContext.toString());

		// Variables declaration to be used for Audit Event generation...
		AuditEvent bcaAuditEvent = new AuditEvent();
		Attributes eventAttributes = new Attributes();
		Map eventMap = new HashMap();
		String requestID = "";
		Date requestDate = new Date();
		String applicationName = "";
		String userID = "";
		String requester = "";
		String requesterBranchCode = "";
		String approver1 = "";
		String approver2 = "";
		String checker1 = "";
		String checker2 = "";
		String saApplikasi = "";
		String accountID = "";
		String requestStatus = "";
		String currentApprover = "";
		String bcaAction = ""; // This will hold the workflow approval step...
		String auditInterface = "";
		String baseIdentityUserID = ""; // This will hold the user id for the
										// user the request is made...

		// as the audit event is always getting generated from the LCM
		// provisioning workflow hence setting it as a constrant value.... make
		// change to this code if you want to use this audit class somewhere
		// else....
		String applicationSource = Source.LCM.toString();

		String requestedItemsTargetApplication = ""; // this will hold the value
														// from the target
														// application
														// corrosponding to the
														// role and entitlement
														// requested...

		String requestedRoleName = ""; // this will hold the role requested
										// for....

		// String isRequested = "false";

		WorkflowCase wfcase = workflowContext.getWorkflowCase();
		logger.debug(CLASS_NAME + METHOD_NAME + "Workflow Case: " + wfcase.toXml());

		// Getting the Workflow object...
		Workflow workflow = wfcase.getWorkflow();
		// logger.debug(CLASS_NAME + METHOD_NAME + "Workflow : " +
		// wfcase.toXml());

		String workflowCurrentStepString = workflow.getCurrentStep();
		logger.debug(CLASS_NAME + METHOD_NAME + "Workflow Current Step ID : " + workflowCurrentStepString);

		Attributes workflowAttributes = workflow.getVariables();

		// 2. Requester...
		requester = workflowAttributes.getString("launcher");
		logger.debug(CLASS_NAME + METHOD_NAME + "requester: " + requester);

		// Get interface which is nothing by the flow from workflow
		// attributes...
		auditInterface = Source.LCM.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "requestID: " + requestID);

		// 3. Request ID...
		requestID = (String) workflowAttributes.get("identityRequestId");
		logger.debug(CLASS_NAME + METHOD_NAME + "requestID: " + requestID);

		// 5. Get action....
		bcaAction = AuditEvent.ActionApproveLineItem;
		logger.debug(CLASS_NAME + METHOD_NAME + "bcaAction: " + bcaAction);

		// 6. Get Requestee Details.....
		baseIdentityUserID = workflowAttributes.getString("identityName");
		logger.debug(CLASS_NAME + METHOD_NAME + "baseIdentityUserID: " + baseIdentityUserID);

		// 7. Get Target Application Details...
		requestedItemsTargetApplication = BCAAuditEvent.getTargetApplicationDetails(workflow);

		// 8. Requested role name....
		requestedRoleName = BCAAuditEvent.getRequestedRoleName(workflow);

		// 9. Get Application Name...
		applicationName = BCAAuditEvent.getApplicationName(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "applicationName: " + applicationName);
		// 10. Get User ID
		userID = baseIdentityUserID;
		logger.debug(CLASS_NAME + METHOD_NAME + "userID: " + userID);

		// 11. Get Requestee's Branch Code....
		Identity baseIdentity = context.getObjectByName(Identity.class, baseIdentityUserID);
		if (baseIdentity != null) {
			requesterBranchCode = (String) baseIdentity.getAttribute(IdentityAttribute.BRANCH_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "requesterBranchCode: " + requesterBranchCode);
		}

		// 12. Get Account ID....
		ProvisioningProject provisioningProject = (ProvisioningProject) workflowAttributes.get("project");
		accountID = BCAAuditEvent.getAccountID(provisioningProject);
		logger.debug(CLASS_NAME + METHOD_NAME + "accountID: " + accountID);

		// we will start from the end approval to start...... getting values in
		// reverse order to make sure we do not get NPE...
		boolean shouldEvaluateOtherIfCondition = true;

		if (BCAAuditEvent.isApprovalFinished(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_SA_APLIKASI)) {

			logger.debug(CLASS_NAME + METHOD_NAME + "Currently Executing Step: "
					+ BCAAuditEvent.BCAApprovalStepName.STEP_SA_APLIKASI);
			// 12. Get Approver 1....
			approver1 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1);

			// 13. Get Approver 2....
			approver2 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_2);

			// 14. Get Checker 1.....
			checker1 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_1);

			// 15. Get Checker 2.....
			checker2 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_2);

			// 16. Get SA Applikasi....
			saApplikasi = BCAAuditEvent.getApproverFromStep(workflow,
					BCAAuditEvent.BCAApprovalStepName.STEP_SA_APLIKASI);

			workflowCurrentStepString = BCAAuditEvent.BCAApprovalStepName.STEP_SA_APLIKASI;
			currentApprover = saApplikasi;
			shouldEvaluateOtherIfCondition = false;

		} else if (BCAAuditEvent.isApprovalFinished(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_2)
				&& shouldEvaluateOtherIfCondition) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Currently Executing Step: "
					+ BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_2);

			// 12. Get Approver 1....
			approver1 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1);

			// 13. Get Approver 2....
			approver2 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_2);

			// 14. Get Checker 1.....
			checker1 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_1);

			// 15. Get Checker 2.....
			checker2 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_2);

			// 16. Get SA Applikasi....
			saApplikasi = "";

			workflowCurrentStepString = BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_2;
			currentApprover = checker2;
			shouldEvaluateOtherIfCondition = false;

		} else if (BCAAuditEvent.isApprovalFinished(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_1)
				&& shouldEvaluateOtherIfCondition) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Currently Executing Step: "
					+ BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_1);

			// 12. Get Approver 1....
			approver1 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1);

			// 13. Get Approver 2....
			approver2 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_2);

			// 14. Get Checker 1.....
			checker1 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_1);

			// 15. Get Checker 2.....
			checker2 = "";

			// 16. Get SA Applikasi....
			saApplikasi = "";
			workflowCurrentStepString = BCAAuditEvent.BCAApprovalStepName.STEP_CHECKER_1;
			currentApprover = checker1;
			shouldEvaluateOtherIfCondition = false;

		} else if (BCAAuditEvent.isApprovalFinished(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_2)
				&& shouldEvaluateOtherIfCondition) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Currently Executing Step: "
					+ BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_2);

			// 12. Get Approver 1....
			approver1 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1);

			// 13. Get Approver 2....
			approver2 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_2);

			// 14. Get Checker 1.....
			checker1 = "";

			// 15. Get Checker 2.....
			checker2 = "";

			// 16. Get SA Applikasi....
			saApplikasi = "";
			workflowCurrentStepString = BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_2;
			currentApprover = approver2;
			shouldEvaluateOtherIfCondition = false;

		} else if (BCAAuditEvent.isApprovalFinished(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1)
				&& shouldEvaluateOtherIfCondition) {

			logger.debug(CLASS_NAME + METHOD_NAME + "Currently Executing Step: "
					+ BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1);

			// 12. Get Approver 1....
			approver1 = BCAAuditEvent.getApproverFromStep(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1);

			// 13. Get Approver 2....
			approver2 = "";

			// 14. Get Checker 1.....
			checker1 = "";

			// 15. Get Checker 2.....
			checker2 = "";

			// 16. Get SA Applikasi....
			saApplikasi = "";
			workflowCurrentStepString = BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1;
			currentApprover = approver1;

		}

		// 1. Get Request Creation Date...

		if (BCAAuditEvent.isApprovalFinished(workflow, BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1)) {
			requestDate = BCAAuditEvent.getWorkflowStartDate(workflow);

			logger.debug(CLASS_NAME + METHOD_NAME + "requestDate: " + requestDate);
		}

		String auditSource = BCAAuditEvent.getAuditSource(context, item, requester);
		logger.debug(CLASS_NAME + METHOD_NAME + "auditSource: " + auditSource);
		// Using one extended attribute to set the request status... this value
		// will be then used for the filter condition...
		bcaAuditEvent.setAttributeName("assignedRoles");
		bcaAuditEvent.setAttributeValue(requestedRoleName);
		bcaAuditEvent.setTrackingId(requestID);
		bcaAuditEvent.setInterface(auditInterface);
		bcaAuditEvent.setSource(auditSource);
		bcaAuditEvent.setAccountName(requestedItemsTargetApplication);
		bcaAuditEvent.setTarget(baseIdentityUserID);
		bcaAuditEvent.setAction(bcaAction);
		bcaAuditEvent.setApplication(applicationSource);

		// This are the filters to be used by the report.....
		// bcaAuditEvent.setString1(requestStatus);
		// bcaAuditEvent.setString2(workflowCurrentStepString);

		// Setting the filter to be used for the report generation by using the
		// extra columns present in the audit event table...
		Date today = new Date();
		// String todayDate = sailpoint.tools.Util.dateToString(today);

		String todayDateString = BCAAuditEvent.getBCAFormatedDateString(today);
		logger.debug(CLASS_NAME + METHOD_NAME + "todayDateString: " + todayDateString);

		bcaAuditEvent.setString1(todayDateString);
		bcaAuditEvent.setString2(applicationName);

		if (workflowCurrentStepString.equalsIgnoreCase(BCAAuditEvent.BCAApprovalStepName.STEP_SA_APLIKASI)) {
			if (isAnyApprovalItemRejected(workflow)) {
				requestStatus = WorkItem.State.Rejected.toString().toUpperCase();
			} else {
				requestStatus = WorkItem.State.Finished.toString().toUpperCase();
			}

		} else if (workflowCurrentStepString.equalsIgnoreCase(BCAAuditEvent.BCAApprovalStepName.STEP_SA_APLIKASI)
				&& isApprovalFinished(workflow, workflowCurrentStepString) && !isAnyApprovalItemRejected(workflow)) {
			requestStatus = WorkItem.State.Finished.toString().toUpperCase();
		} else if (!workflowCurrentStepString.equalsIgnoreCase(BCAAuditEvent.BCAApprovalStepName.STEP_SA_APLIKASI)
				&& !isAnyApprovalItemRejected(workflow)) {
			requestStatus = WorkItem.State.Pending.toString().toUpperCase();
		} else {
			requestStatus = WorkItem.State.Finished.toString().toUpperCase();
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "requestStatus: " + requestStatus);
		bcaAuditEvent.setString3(requestStatus);
		bcaAuditEvent.setString4(workflowCurrentStepString);

		logger.debug(CLASS_NAME + METHOD_NAME + "Audit Event Going to be Commited: " + bcaAuditEvent.toXml());

		// Setting the audit value inside the map....
		eventMap.put(BCAAuditEvent.AuditEventAttributes.REQUEST_ID, requestID); // 3.
																				// Done...
		eventMap.put(BCAAuditEvent.AuditEventAttributes.REQUEST_DATE, // 1. Done
				requestDate);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.APPLICATOIN_NAME, // Done
				applicationName);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.USER_ID, userID); // Done
		eventMap.put(BCAAuditEvent.AuditEventAttributes.REQUESTER, requester); // 2.
																				// Done
		eventMap.put(BCAAuditEvent.AuditEventAttributes.REQUESTER_BRANCH_CODE, // Done
				requesterBranchCode);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.APPROVER1, approver1);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.APPROVER2, approver2);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.CHECKER1, checker1);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.CHECKER2, checker2);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.SA_APPLIKASI, saApplikasi);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.ACCOUNT_ID, accountID);
		eventMap.put(BCAAuditEvent.AuditEventAttributes.REQUEST_STATUS, requestStatus);

		// Setting the map inside the attribute....
		eventAttributes.putAll(eventMap);

		// Setting the attribute inside the actual audit event...
		bcaAuditEvent.setAttributes(eventAttributes);

		context.startTransaction();
		context.saveObject(bcaAuditEvent);
		context.commitTransaction();
		context.decache();
		logger.debug(CLASS_NAME + METHOD_NAME + "Audit Event Created....");

	}

	/**
	 * @param provisioningProject
	 * @return
	 */
	public static String getAccountID(ProvisioningProject provisioningProject) {
		String METHOD_NAME = "::getAccountID::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String accountID = "";
		List provisioningTargetList = provisioningProject.getProvisioningTargets();

		Iterator it = provisioningTargetList.iterator();
		while (it.hasNext()) {
			ProvisioningTarget localProvisioningProject = (ProvisioningTarget) it.next();

			List accountSelectorList = localProvisioningProject.getAccountSelections();
			Iterator it2 = accountSelectorList.iterator();
			while (it2.hasNext()) {
				AccountSelection localAccountSelection = (AccountSelection) it2.next();
				if (!localAccountSelection.getApplicationName().equalsIgnoreCase("")) {
					accountID = localAccountSelection.getApplicationName();
					logger.debug(CLASS_NAME + METHOD_NAME + "accountID: " + accountID);
					return accountID;

				}

			}

		}
		logger.debug(CLASS_NAME + METHOD_NAME + "accountID: " + accountID);

		return accountID;
	}

	/**
	 * @param workflow
	 * @param stepName
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isRequestPending(Workflow workflow, String stepName) throws GeneralException {

		String METHOD_NAME = "::isRequestPending::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		logger.debug(CLASS_NAME + METHOD_NAME + "Step Name: " + stepName);

		boolean isRequestPending = false;

		Workflow.Step approvalStep = null;

		List stepList = workflow.getSteps();
		Iterator it = stepList.iterator();
		while (it.hasNext()) {
			Workflow.Step localStep = (Step) it.next();
			if (localStep.getName().equalsIgnoreCase(stepName)) {
				approvalStep = localStep;
				logger.debug(CLASS_NAME + METHOD_NAME + "Found Step: " + localStep.getName());
			}

		}

		if (approvalStep != null) {
			Approval workflowApproval = approvalStep.getApproval();
			logger.debug(CLASS_NAME + METHOD_NAME + "WorkflowApproval: " + workflowApproval.toXml());

			if (workflowApproval.isStarted()) {
				if (!workflowApproval.isComplete()) {
					isRequestPending = true;
					logger.debug(CLASS_NAME + METHOD_NAME + "isRequestPending: " + isRequestPending);
					return isRequestPending;
				}
			}
		}

		return isRequestPending;
	}

	/**
	 * @param workflow
	 * @return
	 * @throws ParseException
	 */
	public static Date getWorkflowStartDate(Workflow workflow) throws ParseException {
		String METHOD_NAME = "::getWorkflowStartDate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Workflow.Step approvalStep = workflow.getStep(BCAAuditEvent.BCAApprovalStepName.STEP_APPROVER_1);
		Long dateString = approvalStep.getStartTime();
		logger.debug(CLASS_NAME + METHOD_NAME + "dateString: " + dateString);

		Date createdDate = new Date(dateString);
		logger.debug(CLASS_NAME + METHOD_NAME + "createdDate: " + createdDate);
		return createdDate;
	}

	/**
	 * @param workflow
	 * @return
	 */
	public static String getTargetApplicationDetails(Workflow workflow) {
		String METHOD_NAME = "::getTargetApplicationDetails::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");
		String targetApplication = "";

		Attributes workflowAttributes = workflow.getVariables();
		ProvisioningPlan localProvisioningPlan = (ProvisioningPlan) workflowAttributes.get("plan");

		if (localProvisioningPlan != null) {
			List provisioningTargetList = localProvisioningPlan.getProvisioningTargets();

			if (provisioningTargetList != null) {
				Iterator it = provisioningTargetList.iterator();
				while (it.hasNext()) {
					ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it.next();

					if (localProvisioningTarget.getRole() != null) {

						targetApplication = localProvisioningTarget.getApplication();
						logger.debug(CLASS_NAME + METHOD_NAME + "requestedRoleName: " + targetApplication);
						return targetApplication;
					}

				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "targetApplication: " + targetApplication);

		return targetApplication;
	}

	/**
	 * @param workflow
	 * @return
	 */
	public static String getRequestedRoleName(Workflow workflow) {
		String METHOD_NAME = "::getRequestedRoleName::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");

		String requestedRoleName = "";

		Attributes workflowAttributes = workflow.getVariables();
		ProvisioningPlan localProvisioningPlan = (ProvisioningPlan) workflowAttributes.get("plan");

		if (localProvisioningPlan != null) {
			List provisioningTargetList = localProvisioningPlan.getProvisioningTargets();

			if (provisioningTargetList != null) {
				Iterator it = provisioningTargetList.iterator();
				while (it.hasNext()) {
					ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it.next();

					if (localProvisioningTarget.getRole() != null) {
						requestedRoleName = localProvisioningTarget.getRole();
						logger.debug(CLASS_NAME + METHOD_NAME + "requestedRoleName: " + requestedRoleName);
						return requestedRoleName;
					}

				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "requestedRoleName: " + requestedRoleName);
		return requestedRoleName;
	}

	/**
	 * @param workflow
	 * @return
	 */
	public static String getApplicationName(Workflow workflow) {
		String METHOD_NAME = "::getApplicationName::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");

		String applicationName = "";

		Attributes workflowAttributes = workflow.getVariables();
		ProvisioningPlan localProvisioningPlan = (ProvisioningPlan) workflowAttributes.get("plan");
		
		if (localProvisioningPlan != null) {
			List provisioningTargetList = localProvisioningPlan.getProvisioningTargets();

			if (provisioningTargetList != null) {
				Iterator it = provisioningTargetList.iterator();
				while (it.hasNext()) {
					ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it.next();

					List localAccountSelectionList = localProvisioningTarget.getAccountSelections();
					Iterator it2 = localAccountSelectionList.iterator();
					while (it2.hasNext()) {

						AccountSelection localAccountSelection = (AccountSelection) it2.next();
						applicationName = localAccountSelection.getApplicationName();
						logger.debug(CLASS_NAME + METHOD_NAME + "applicationName: " + applicationName);
						return applicationName;
					}
				}
			}
		}
		else
			logger.debug(CLASS_NAME + METHOD_NAME + "NULL....");
		return applicationName;
	}

	/**
	 * @param workflow
	 * @param stepName
	 * @return
	 */
	public static String getApproverFromStep(Workflow workflow, String stepName) {
		String METHOD_NAME = "::getApproverFromStep::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String approver = "";

		logger.debug(CLASS_NAME + METHOD_NAME + "stepName: " + stepName);

		Workflow.Step currentWorkflowStep = workflow.getStep(stepName);
		

		if (currentWorkflowStep != null) {
			Approval currentlyExecutedApproval = currentWorkflowStep.getApproval();

			if (currentlyExecutedApproval != null) {
				if (currentlyExecutedApproval.isComplete()) {
					approver = currentlyExecutedApproval.getOwner();
					logger.debug(CLASS_NAME + METHOD_NAME + "Approver: " + approver);
				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "Approver: " + approver);
		return approver;
	}

	/**
	 * @param workflow
	 * @param stepName
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isApprovalFinished(Workflow workflow, String stepName) throws GeneralException {
		String METHOD_NAME = "::isApprovalFinished::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		logger.debug(CLASS_NAME + METHOD_NAME + "Step Name: " + stepName);

		boolean isApprovalFinished = false;
		Workflow.Step approvalStep = null;

		List stepList = workflow.getSteps();
		Iterator it = stepList.iterator();
		while (it.hasNext()) {
			Workflow.Step localStep = (Step) it.next();
			logger.debug(CLASS_NAME + METHOD_NAME + "Step Name: " + localStep.getName());

			if (localStep.getName().equalsIgnoreCase(stepName)) {
				approvalStep = localStep;
				logger.debug(CLASS_NAME + METHOD_NAME + "Found Step: " + localStep.getName());
			}

		}

		logger.debug(CLASS_NAME + METHOD_NAME + "approvalStep.toXml(): " + approvalStep.toXml());

		if (approvalStep != null) {
			Approval workflowApproval = approvalStep.getApproval();
			logger.debug(CLASS_NAME + METHOD_NAME + "WorkflowApproval: " + workflowApproval.toXml());
			if (workflowApproval.isComplete()) {
				isApprovalFinished = true;
				logger.debug(CLASS_NAME + METHOD_NAME + "isApprovalFinished: " + isApprovalFinished);
				return isApprovalFinished;
			}
		}

		return isApprovalFinished;
	}

	/**
	 * This method will check if any of the approval item is rejected. This is
	 * called from Intercepter script.. if rejected is true.. intercepter will
	 * get the list of open workitem and will close it one by one...
	 *
	 * 
	 * @param workflow
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isAnyApprovalItemRejected(Workflow workflow) throws GeneralException {
		String METHOD_NAME = "::isAnyApprovalItemRejected::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		boolean isRejected = false;

		String currentStepName = workflow.getCurrentStep();
		Workflow.Step currentStep = null;
		// Getting the list of workflow step.. then iterate over it and get the
		// current step object...
		List workflowStepList = workflow.getSteps();
		Iterator it4 = workflowStepList.iterator();
		while (it4.hasNext()) {
			Workflow.Step workflowStep = (Workflow.Step) it4.next();

			if (workflowStep != null) {
				if (workflowStep.getId().equalsIgnoreCase(currentStepName)) {
					currentStep = workflowStep;
					logger.debug(CLASS_NAME + METHOD_NAME + "Current Step Found: " + currentStep.getName());

				}
			}

		}

		logger.debug(CLASS_NAME + METHOD_NAME + "Current Step: " + currentStep.toXml());

		// Getting the current workflow step.....
		Approval masterApproval = currentStep.getApproval();
		logger.debug(CLASS_NAME + METHOD_NAME + "Master Approval: " + masterApproval.toXml());

		if (masterApproval.isRejected()) {
			isRejected = true;
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "isRejected: " + isRejected);
		return isRejected;

	}

	/**
	 * @param workflow
	 * @param item
	 * @return
	 * @throws GeneralException
	 */
	public static String getRequestState(WorkItem item) throws GeneralException {
		String METHOD_NAME = "::getRequestState::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String requestState = "";
		SignOffHistory signoff = null;

		if (item != null) {
			List signoffs = item.getSignOffs();
			if (signoffs != null && signoffs.size() > 0) {
				signoff = (SignOffHistory) signoffs.get(0);
				logger.debug(CLASS_NAME + METHOD_NAME + "signoff: " + signoff.toXml());
			}

			ApprovalSet approvalSet = (ApprovalSet) item.get("approvalSet");
			if ((approvalSet != null) && (!approvalSet.isEmpty())) {
				for (ApprovalItem it : approvalSet.getItems()) {

					if (!it.isApproved()) {
						requestState = WorkItem.State.Rejected.toString().toUpperCase();
					} else {
						requestState = WorkItem.State.Pending.toString().toUpperCase();
					}

				}
			}
		}

		return requestState;
	}

	/**
	 * @param context
	 * @param item
	 * @param launcher
	 * @return
	 * @throws GeneralException
	 */
	public static String getAuditSource(SailPointContext context, WorkItem item, String launcher)
			throws GeneralException {

		String actor = context.getUserName();
		Identity ident = context.getObjectByName(Identity.class, actor);
		if (ident != null)
			actor = ident.getDisplayName();
		else {
			ident = item.getOwner();
			if (ident != null)
				actor = ident.getDisplayName();
			else
				actor = launcher;
		}

		return actor;
	}

	/**
	 * @param date
	 * @return
	 */
	public static String getBCAFormatedDateString(Date date) {

		String METHOD_NAME = "::getDateBCAFormatedDateString::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String dateString = "";

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
		dateString = sdfDate.format(date);
		logger.debug(CLASS_NAME + METHOD_NAME + "Date String: " + dateString);
		return dateString;

	}

}
