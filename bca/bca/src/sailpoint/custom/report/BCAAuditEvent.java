package sailpoint.custom.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.AccountSelection;
import sailpoint.object.ApprovalItem;
import sailpoint.object.ApprovalSet;
import sailpoint.object.Attributes;
import sailpoint.object.AuditEvent;
import sailpoint.object.Identity;
import sailpoint.object.IdentityRequest;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.ProvisioningTarget;
import sailpoint.object.Source;
import sailpoint.object.Workflow;
import sailpoint.object.Workflow.Approval;
import sailpoint.object.Workflow.Step;
import sailpoint.object.WorkflowCase;
import sailpoint.object.WorkItem;
import sailpoint.tools.GeneralException;
import sailpoint.workflow.WorkflowContext;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BCAAuditEvent {
	public static String CLASS_NAME = "::BCAAuditEvent::";
	public static Logger logger = Logger
			.getLogger("sailpoint.workflow.rule.BCAAuditEvent");

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
	 * @throws ParseException
	 */
	public static void createCustomAuditsEvent(SailPointContext context,
			WorkflowContext workflowContext, WorkItem item)
			throws GeneralException, ParseException {
		String METHOD_NAME = "::createCustomAuditsEvent::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		// Now start the actual work...
		WorkflowCase wfcase = workflowContext.getWorkflowCase();

		// Getting the Workflow object...
		Workflow workflow = wfcase.getWorkflow();

		String workflowCurrentStepString = workflow.getCurrentStep();
		logger.debug(CLASS_NAME + METHOD_NAME + "Workflow Current Step ID : "
				+ workflowCurrentStepString);
		String workflowCurrentStepName = BCAAuditEvent
				.getWorkflowStepNameFromStepID(workflow,
						workflowCurrentStepString);

		if (workflowCurrentStepName
				.equalsIgnoreCase(BCACustomAuditing.WorkflowStep.START)) {

			BCAAuditEvent.getStartAuditEvent(context, workflowContext, item);

		} else if (workflowCurrentStepName
				.equalsIgnoreCase(BCACustomAuditing.WorkflowStep.STOP)) {

			BCAAuditEvent.getStopAuditEvent(context, workflowContext, item);

		} else {
			BCAAuditEvent.getApprovalAuditEvent(context, workflowContext, item);
		}

		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Auditing Completed for Workflow Step: "
				+ workflowCurrentStepName);

	}

	/**
	 * @param context
	 * @param workflowContext
	 * @param item
	 * @throws GeneralException
	 */
	public static void getStartAuditEvent(SailPointContext context,
			WorkflowContext workflowContext, WorkItem item)
			throws GeneralException {

		String METHOD_NAME = "::getStartAuditEvent::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		// Variables declaration to be used for Audit Event generation...
		AuditEvent bcaAuditEvent = new AuditEvent();
		Attributes eventAttributes = new Attributes();
		Map eventMap = new HashMap();

		// Local Variables used to create the event map...
		String requestID = "";
		String requestDate = "";
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
		String requestedOperation = "";
		String workItemOutcome = "";
		String requestStatus = "";

		// Used for other activity...
		String bcaAction = "";
		String auditInterface = "";
		String baseIdentityUserID = "";
		String requestedRoleName = "";
		String auditSource = "";

		// Now start the actual work...
		WorkflowCase wfcase = workflowContext.getWorkflowCase();

		// Getting the Workflow object...
		Workflow workflow = wfcase.getWorkflow();

		// Getting workflow current attributes...
		Attributes workflowAttributes = workflow.getVariables();
		requester = workflowAttributes.getString("launcher");
		logger.debug(CLASS_NAME + METHOD_NAME + "requester: " + requester);

		auditInterface = Source.LCM.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "auditInterface: "
				+ auditInterface);

		requestID = (String) workflowAttributes.get("identityRequestId");
		logger.debug(CLASS_NAME + METHOD_NAME + "requestID: " + requestID);

		requestID = Integer.toString(Integer.parseInt(requestID));
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Trimming the prefix zero.... requestID: " + requestID);

		requestDate = BCAAuditEvent.getRequestDate(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "requestDate: " + requestDate);

		bcaAction = BCACustomAuditing.WorkflowStep.START;
		logger.debug(CLASS_NAME + METHOD_NAME + "bcaAction: " + bcaAction);

		baseIdentityUserID = workflowAttributes.getString("identityName");
		logger.debug(CLASS_NAME + METHOD_NAME + "baseIdentityUserID: "
				+ baseIdentityUserID);

		applicationName = BCAAuditEvent.getTargetApplicationDetails(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "applicationName: "
				+ applicationName);

		userID = baseIdentityUserID;
		logger.debug(CLASS_NAME + METHOD_NAME + "userID: " + userID);

		requesterBranchCode = BCAAuditEvent.getRequesteeBranchCode(context,
				baseIdentityUserID);
		logger.debug(CLASS_NAME + METHOD_NAME + "requesterBranchCode: "
				+ requesterBranchCode);

		requestedRoleName = BCAAuditEvent.getRequestedRoleName(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "requestedRoleName: "
				+ requestedRoleName);

		accountID = "";
		workItemOutcome = WorkItem.State.Finished.toString();
		approver1 = "";
		approver2 = "";
		checker1 = "";
		checker2 = "";
		saApplikasi = "";
		requestedOperation = BCAAuditEvent.getRequestedOperation(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "requestedOperation: "
				+ requestedOperation);
		requestStatus = IdentityRequest.ExecutionStatus.Executing.toString();

		String requestedItemsTargetApplicationAccountName = BCAAuditEvent
				.getTargetApplicationAccountName(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "requestedItemsTargetApplicationAccountName: "
				+ requestedItemsTargetApplicationAccountName);

		auditSource = Source.LCM.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "auditSource: " + auditSource);

		// ***************************************************
		// Below are the list of Audit Event Table column values getting
		// populated...
		// ***************************************************

		bcaAuditEvent.setInterface(auditInterface);
		bcaAuditEvent.setSource(auditSource);
		bcaAuditEvent.setAction(bcaAction);
		bcaAuditEvent.setTarget(baseIdentityUserID);
		bcaAuditEvent.setApplication(applicationName);
		bcaAuditEvent
				.setAccountName(requestedItemsTargetApplicationAccountName);
		bcaAuditEvent.setInstance(workflow.getName());
		bcaAuditEvent.setAttributeName("assignedRoles");
		bcaAuditEvent.setAttributeValue(requestedRoleName);
		bcaAuditEvent.setTrackingId(requestID);

		String baseIdentityDisplayName = "";
		Identity baseIdentity = context.getObjectByName(Identity.class,
				baseIdentityUserID);
		if (baseIdentity != null) {
			baseIdentityDisplayName = baseIdentity.getDisplayName();
			logger.debug(CLASS_NAME + METHOD_NAME + "baseIdentityDisplayName: "
					+ baseIdentityDisplayName);
		}

		// Setting the filter to be used for the report generation by using the
		// extra columns present in the audit event table...
		Date today = new Date();
		String todayDateString = BCAAuditEvent
				.getDateBCAFormatedDateString(today);
		logger.debug(CLASS_NAME + METHOD_NAME + "todayDateString: "
				+ todayDateString);

		// Now creating audit event map...
		eventMap = BCAAuditEvent.getAuditMap(requestID, requestDate,
				applicationName, baseIdentityDisplayName, requester,
				requesterBranchCode, approver1, approver2, checker1, checker2,
				saApplikasi, accountID, requestedOperation, workItemOutcome,
				requestStatus);

		eventAttributes.putAll(eventMap);

		// Setting the attribute inside the actual audit event...
		bcaAuditEvent.setAttributes(eventAttributes);

		// This are the DB Column based filter attributes which will be
		// minned...
		// 1. String 1 = createdDate = InputDate Filter in Report - We are not
		// using it anymore...
		// 2. String 2 = application Name = Applicatoin ID filter in report.
		// 3. String 3 = Status Filter (Can be approved, rejected,executing)
		// 4. String 4 = Operation
		bcaAuditEvent.setString1(todayDateString);
		bcaAuditEvent.setString2(applicationName);
		bcaAuditEvent.setString3(workItemOutcome);
		bcaAuditEvent.setString4(requestedOperation);

		// ***************************************************
		// Commiting on the transaction...
		// ***************************************************
		context.startTransaction();
		context.saveObject(bcaAuditEvent);
		context.commitTransaction();
		context.decache();
		logger.debug(CLASS_NAME + METHOD_NAME + "Audit Event Created....");

	}

	/**
	 * @param context
	 * @param workflowContext
	 * @param item
	 * @throws GeneralException
	 */
	public static void getStopAuditEvent(SailPointContext context,
			WorkflowContext workflowContext, WorkItem item)
			throws GeneralException {
		String METHOD_NAME = "::getStopAuditEvent::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		// Variables declaration to be used for Audit Event generation...
		AuditEvent bcaAuditEvent = new AuditEvent();
		Attributes eventAttributes = new Attributes();
		Map eventMap = new HashMap();

		// Local Variables used to create the event map...
		String requestID = "";
		String requestDate = "";
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
		String requestedOperation = "";
		String workItemOutcome = "";
		String requestStatus = "";
		String requestedItemsTargetApplicationAccountName = "";

		// Used for other activity...
		String bcaAction = "";
		String auditInterface = "";
		String baseIdentityUserID = "";
		String requestedItemsTargetApplicationName = "";
		String requestedRoleName = "";
		String auditSource = "";
		// String applicationSource = "";
		// String requestedItemsTargetApplication = "";
		// String currentApprover = "";

		// Now start the actual work...
		WorkflowCase wfcase = workflowContext.getWorkflowCase();

		// Getting the Workflow object...
		Workflow workflow = wfcase.getWorkflow();

		// Getting workflow current attributes...
		Attributes workflowAttributes = workflow.getVariables();
		requester = workflowAttributes.getString("launcher");
		logger.debug(CLASS_NAME + METHOD_NAME + "requester: " + requester);

		auditInterface = Source.LCM.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "auditInterface: "
				+ auditInterface);

		requestID = (String) workflowAttributes.get("identityRequestId");
		requestID = Integer.toString(Integer.parseInt(requestID));
		logger.debug(CLASS_NAME + METHOD_NAME + "requestID: " + requestID);

		requestDate = BCAAuditEvent.getRequestDate(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "requestDate: " + requestDate);

		bcaAction = BCACustomAuditing.WorkflowStep.STOP;
		logger.debug(CLASS_NAME + METHOD_NAME + "bcaAction: " + bcaAction);

		baseIdentityUserID = workflowAttributes.getString("identityName");
		logger.debug(CLASS_NAME + METHOD_NAME + "baseIdentityUserID: "
				+ baseIdentityUserID);

		requestedItemsTargetApplicationName = BCAAuditEvent
				.getTargetApplicationDetails(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "requestedItemsTargetApplicationName: "
				+ requestedItemsTargetApplicationName);

		applicationName = requestedItemsTargetApplicationName;
		logger.debug(CLASS_NAME + METHOD_NAME + "applicationName: "
				+ applicationName);

		userID = baseIdentityUserID;
		logger.debug(CLASS_NAME + METHOD_NAME + "userID: " + userID);

		requesterBranchCode = BCAAuditEvent.getRequesteeBranchCode(context,
				baseIdentityUserID);
		logger.debug(CLASS_NAME + METHOD_NAME + "requesterBranchCode: "
				+ requesterBranchCode);

		accountID = "";
		workItemOutcome = "";
		approver1 = "";
		approver2 = "";
		checker1 = "";
		checker2 = "";
		saApplikasi = "";

		requestedOperation = BCAAuditEvent.getRequestedOperation(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "requestedOperation: "
				+ requestedOperation);

		workItemOutcome = WorkItem.State.Finished.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "workItemOutcome: "
				+ workItemOutcome);

		requestStatus = IdentityRequest.ExecutionStatus.Executing.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "requestStatus: "
				+ requestStatus);

		auditSource = Source.LCM.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "auditSource: " + auditSource);

		// Setting the filter to be used for the report generation by using the
		// extra columns present in the audit event table...
		Date today = new Date();
		String todayDateString = BCAAuditEvent
				.getDateBCAFormatedDateString(today);
		logger.debug(CLASS_NAME + METHOD_NAME + "todayDateString: "
				+ todayDateString);

		// ***************************************************
		// Below are the list of Audit Event Table column values getting
		// populated...
		// ***************************************************

		bcaAuditEvent.setInterface(auditInterface);
		bcaAuditEvent.setSource(auditSource);
		bcaAuditEvent.setAction(bcaAction);
		bcaAuditEvent.setTarget(baseIdentityUserID);
		bcaAuditEvent.setApplication(applicationName);
		bcaAuditEvent
				.setAccountName(requestedItemsTargetApplicationAccountName);
		bcaAuditEvent.setInstance(workflow.getName());
		bcaAuditEvent.setAttributeName("assignedRoles");
		bcaAuditEvent.setAttributeValue(requestedRoleName);
		bcaAuditEvent.setTrackingId(requestID);

		// Now creating audit event map...
		eventMap = BCAAuditEvent.getAuditMap(requestID, requestDate,
				applicationName, baseIdentityUserID, requester,
				requesterBranchCode, approver1, approver2, checker1, checker2,
				saApplikasi, accountID, requestedOperation, workItemOutcome,
				requestStatus);

		eventAttributes.putAll(eventMap);

		// Setting the attribute inside the actual audit event...
		bcaAuditEvent.setAttributes(eventAttributes);

		// This are the DB Column based filter attributes which will be
		// minned...
		// 1. String 1 = createdDate = InputDate Filter in Report
		// 2. String 2 = application Name = Applicatoin ID filter in report.
		// 3. String 3 = Status Filter (Can be approved, rejected,executing)
		// 4. String 4 = Operation
		bcaAuditEvent.setString1(todayDateString);
		bcaAuditEvent.setString2(applicationName);
		bcaAuditEvent.setString3(workItemOutcome);
		bcaAuditEvent.setString4(requestedOperation);

		// ***************************************************
		// Commiting on the transaction...
		// ***************************************************
		context.startTransaction();
		context.saveObject(bcaAuditEvent);
		context.commitTransaction();
		context.decache();
		logger.debug(CLASS_NAME + METHOD_NAME + "Audit Event Created....");

	}

	/**
	 * @param context
	 * @param workflowContext
	 * @param item
	 * @throws GeneralException
	 * @throws ParseException
	 */
	public static void getApprovalAuditEvent(SailPointContext context,
			WorkflowContext workflowContext, WorkItem item)
			throws GeneralException, ParseException {
		String METHOD_NAME = "::getApprovalAuditEvent::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		// Variables declaration to be used for Audit Event generation...
		AuditEvent bcaAuditEvent = new AuditEvent();
		Attributes eventAttributes = new Attributes();
		Map eventMap = new HashMap();

		// Local Variables used to create the event map...
		String requestID = "";
		String requestDate = "";
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
		String requestedOperation = "";
		String workItemOutcome = "";
		String requestStatus = "";

		// Used for other activity...
		String currentApprover = "";
		String bcaAction = "";
		String auditInterface = "";
		String baseIdentityUserID = "";
		// String applicationSource = "";
		String requestedItemsTargetApplicationName = "";
		// String requestedItemsTargetApplication = "";
		String requestedRoleName = "";

		// Now start the actual work...
		WorkflowCase wfcase = workflowContext.getWorkflowCase();

		// Remove this logger...
		// logger.debug(CLASS_NAME + METHOD_NAME + "Workflow Case: " +
		// wfcase.toXml());

		// Getting the Workflow object...
		Workflow workflow = wfcase.getWorkflow();

		// Getting workflow current attributes...
		Attributes workflowAttributes = workflow.getVariables();

		String workflowCurrentStepString = workflow.getCurrentStep();
		logger.debug(CLASS_NAME + METHOD_NAME + "Workflow Current Step ID : "
				+ workflowCurrentStepString);

		// String workflowCurrentStepName =
		// BCAAuditEvent.getWorkflowStepNameFromStepID(workflow,
		// workflowCurrentStepString);

		requester = workflowAttributes.getString("launcher");
		logger.debug(CLASS_NAME + METHOD_NAME + "requester: " + requester);

		// auditInterface = workflowAttributes.getString("source");
		auditInterface = Source.LCM.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "auditInterface: "
				+ auditInterface);

		requestID = (String) workflowAttributes.get("identityRequestId");
		requestID = Integer.toString(Integer.parseInt(requestID));
		logger.debug(CLASS_NAME + METHOD_NAME + "requestID: " + requestID);

		requestDate = BCAAuditEvent.getRequestDate(workflow);

		bcaAction = BCAAuditEvent.getAuditAction(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "bcaAction: " + bcaAction);

		baseIdentityUserID = workflowAttributes.getString("identityName");
		logger.debug(CLASS_NAME + METHOD_NAME + "baseIdentityUserID: "
				+ baseIdentityUserID);

		requestedItemsTargetApplicationName = BCAAuditEvent
				.getTargetApplicationDetails(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "requestedItemsTargetApplicationName: "
				+ requestedItemsTargetApplicationName);

		applicationName = requestedItemsTargetApplicationName;

		requestedRoleName = BCAAuditEvent.getRequestedRoleName(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "requestedRoleName: "
				+ requestedRoleName);

		userID = baseIdentityUserID;
		logger.debug(CLASS_NAME + METHOD_NAME + "userID: " + userID);

		requesterBranchCode = BCAAuditEvent.getRequesteeBranchCode(context,
				baseIdentityUserID);

		ProvisioningProject provisioningProject = (ProvisioningProject) workflowAttributes
				.get("project");
		accountID = BCAAuditEvent.getAccountID(provisioningProject);
		logger.debug(CLASS_NAME + METHOD_NAME + "accountID: " + accountID);

		requestedOperation = BCAAuditEvent.getRequestedOperation(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "requestedOperation: "
				+ requestedOperation);

		workItemOutcome = BCAAuditEvent.getWorkItemOutcome(workflow);
		logger.debug(CLASS_NAME + METHOD_NAME + "workItemOutcome: "
				+ workItemOutcome);

		// we will start from the end approval to start...... getting values in
		// reverse order to make sure we do not get NPE...
		boolean shouldEvaluateOtherIfCondition = true;

		if (BCAAuditEvent.isApprovalFinished(workflow,
				BCACustomAuditing.WorkflowStep.SA_APLIKASI)) {

			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Currently Executing Step: "
					+ BCACustomAuditing.WorkflowStep.SA_APLIKASI);

			approver1 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_1);

			approver2 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_2);

			checker1 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.CHECKER_1);

			checker2 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.CHECKER_2);

			saApplikasi = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.SA_APLIKASI);

			workflowCurrentStepString = BCACustomAuditing.WorkflowStep.SA_APLIKASI;
			currentApprover = saApplikasi;
			shouldEvaluateOtherIfCondition = false;

			requestStatus = IdentityRequest.ExecutionStatus.Verifying
					.toString();

		} else if (BCAAuditEvent.isApprovalFinished(workflow,
				BCACustomAuditing.WorkflowStep.CHECKER_2)
				&& shouldEvaluateOtherIfCondition) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Currently Executing Step: "
					+ BCACustomAuditing.WorkflowStep.CHECKER_2);

			approver1 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_1);

			approver2 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_2);

			checker1 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.CHECKER_1);

			checker2 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.CHECKER_2);

			saApplikasi = "";

			workflowCurrentStepString = BCACustomAuditing.WorkflowStep.CHECKER_2;
			currentApprover = checker2;
			shouldEvaluateOtherIfCondition = false;

			requestStatus = IdentityRequest.ExecutionStatus.Executing
					.toString();

		} else if (BCAAuditEvent.isApprovalFinished(workflow,
				BCACustomAuditing.WorkflowStep.CHECKER_1)
				&& shouldEvaluateOtherIfCondition) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Currently Executing Step: "
					+ BCACustomAuditing.WorkflowStep.CHECKER_1);

			approver1 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_1);

			approver2 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_2);

			checker1 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.CHECKER_1);

			checker2 = "";
			saApplikasi = "";
			workflowCurrentStepString = BCACustomAuditing.WorkflowStep.CHECKER_1;
			currentApprover = checker1;
			shouldEvaluateOtherIfCondition = false;

			requestStatus = IdentityRequest.ExecutionStatus.Executing
					.toString();

		} else if (BCAAuditEvent.isApprovalFinished(workflow,
				BCACustomAuditing.WorkflowStep.APPROVER_2)
				&& shouldEvaluateOtherIfCondition) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Currently Executing Step: "
					+ BCACustomAuditing.WorkflowStep.APPROVER_2);

			approver1 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_1);

			approver2 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_2);

			checker1 = "";
			checker2 = "";
			saApplikasi = "";
			workflowCurrentStepString = BCACustomAuditing.WorkflowStep.APPROVER_2;
			currentApprover = approver2;
			shouldEvaluateOtherIfCondition = false;

			requestStatus = IdentityRequest.ExecutionStatus.Executing
					.toString();

		} else if (BCAAuditEvent.isApprovalFinished(workflow,
				BCACustomAuditing.WorkflowStep.APPROVER_1)
				&& shouldEvaluateOtherIfCondition) {

			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Currently Executing Step: "
					+ BCACustomAuditing.WorkflowStep.APPROVER_1);

			approver1 = BCAAuditEvent.getApproverFromStep(workflow,
					BCACustomAuditing.WorkflowStep.APPROVER_1);

			approver2 = "";
			checker1 = "";
			checker2 = "";
			saApplikasi = "";
			workflowCurrentStepString = BCACustomAuditing.WorkflowStep.APPROVER_1;
			currentApprover = approver1;
			requestStatus = IdentityRequest.ExecutionStatus.Executing
					.toString();

		} else {
			requestStatus = IdentityRequest.ExecutionStatus.Completed
					.toString();
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "currentApprover: "
				+ currentApprover);

		// 1. Get Request Creation Date...

		if (BCAAuditEvent.isApprovalFinished(workflow,
				BCACustomAuditing.WorkflowStep.APPROVER_1)) {
			Date requestDateDate = BCAAuditEvent.getWorkflowStartDate(workflow);
			logger.debug(CLASS_NAME + METHOD_NAME + "requestDateDate: "
					+ requestDateDate);

			requestDate = BCAAuditEvent
					.getDateBCAFormatedDateString(requestDateDate);
			logger.debug(CLASS_NAME + METHOD_NAME + "requestDate: "
					+ requestDate);
		}

		// String auditSource = BCAAuditEvent.getAuditSource(context, item,
		// requester);
		String auditSource = Source.LCM.toString();
		logger.debug(CLASS_NAME + METHOD_NAME + "auditSource: " + auditSource);

		String requestedItemsTargetApplicationAccountName = BCAAuditEvent
				.getTargetApplicationAccountName(workflow);

		// Setting the filter to be used for the report generation by using the
		// extra columns present in the audit event table...
		Date today = new Date();
		String todayDateString = BCAAuditEvent
				.getDateBCAFormatedDateString(today);
		logger.debug(CLASS_NAME + METHOD_NAME + "todayDateString: "
				+ todayDateString);

		// long dateLong = today.getTime();
		// logger.debug(CLASS_NAME + METHOD_NAME + "dateLong: " + dateLong);
		// todayDateString = String.valueOf(dateLong);

		// ***************************************************
		// Below are the list of Audit Event Table column values getting
		// populated...
		// ***************************************************

		logger.debug(CLASS_NAME + METHOD_NAME + "FinalApplicationName: "
				+ applicationName);

		bcaAuditEvent.setInterface(auditInterface);
		bcaAuditEvent.setSource(auditSource);
		bcaAuditEvent.setAction(bcaAction);
		bcaAuditEvent.setTarget(baseIdentityUserID);
		bcaAuditEvent.setApplication(applicationName);
		bcaAuditEvent
				.setAccountName(requestedItemsTargetApplicationAccountName);
		bcaAuditEvent.setInstance(workflow.getName());
		bcaAuditEvent.setAttributeName("assignedRoles");
		bcaAuditEvent.setAttributeValue(requestedRoleName);
		bcaAuditEvent.setTrackingId(requestID);

		// Now creating audit event map...
		eventMap = BCAAuditEvent.getAuditMap(requestID, requestDate,
				applicationName, baseIdentityUserID, requester,
				requesterBranchCode, approver1, approver2, checker1, checker2,
				saApplikasi, accountID, requestedOperation, workItemOutcome,
				requestStatus);

		eventAttributes.putAll(eventMap);

		// Setting the attribute inside the actual audit event...
		bcaAuditEvent.setAttributes(eventAttributes);

		// This are the DB Column based filter attributes which will be
		// minned...
		// 1. String 1 = createdDate = InputDate Filter in Report
		// 2. String 2 = application Name = Applicatoin ID filter in report.
		// 3. String 3 = Status Filter (Can be approved, rejected,executing)
		// 4. String 4 = Operation
		bcaAuditEvent.setString1(todayDateString);
		bcaAuditEvent.setString2(applicationName);
		bcaAuditEvent.setString3(workItemOutcome);
		bcaAuditEvent.setString4(requestedOperation);

		// ***************************************************
		// Commiting on the transaction...
		// ***************************************************
		context.startTransaction();
		context.saveObject(bcaAuditEvent);
		context.commitTransaction();
		context.decache();
		logger.debug(CLASS_NAME + METHOD_NAME + "Audit Event Created....");

	}

	/**
	 * @param requestID
	 * @param requestDate
	 * @param applicationName
	 * @param baseIdentityUserID
	 * @param requester
	 * @param requesterBranchCode
	 * @param approver1
	 * @param approver2
	 * @param checker1
	 * @param checker2
	 * @param saApplikasi
	 * @param accountID
	 * @param requestedOperation
	 * @param workItemOutcome
	 * @param requestStatus
	 * @return
	 */
	public static Map getAuditMap(String requestID, String requestDate,
			String applicationName, String baseIdentityUserID,
			String requester, String requesterBranchCode, String approver1,
			String approver2, String checker1, String checker2,
			String saApplikasi, String accountID, String requestedOperation,
			String workItemOutcome, String requestStatus) {
		String METHOD_NAME = "::getAuditMap::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Map eventMap = new HashMap();
		// Setting the audit value inside the map....
		eventMap.put(BCACustomAuditing.AuditEventAttributes.REQUEST_ID,
				requestID);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.REQUEST_DATE,
				requestDate);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.APPLICATOIN_NAME,
				applicationName);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.USER_ID,
				baseIdentityUserID);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.REQUESTER,
				requester);
		eventMap.put(
				BCACustomAuditing.AuditEventAttributes.REQUESTER_BRANCH_CODE,
				requesterBranchCode);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.APPROVER1,
				approver1);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.APPROVER2,
				approver2);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.CHECKER1, checker1);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.CHECKER2, checker2);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.SA_APPLIKASI,
				saApplikasi);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.ACCOUNT_ID,
				accountID);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.OPERATION,
				requestedOperation);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.WORKITEM_OUTCOME,
				workItemOutcome);
		eventMap.put(BCACustomAuditing.AuditEventAttributes.REQUEST_STATUS,
				requestStatus);
		logger.debug(CLASS_NAME + METHOD_NAME + "Event Map: " + eventMap);

		return eventMap;
	}

	/**
	 * Returning the workflow step name using the workflow step id as the
	 * identifying parameter....
	 * 
	 * @param workflow
	 * @param workflowCurrentStepString
	 * @return
	 */
	public static String getWorkflowStepNameFromStepID(Workflow workflow,
			String workflowCurrentStepString) {
		String METHOD_NAME = "::getWorkflowStepNameFromStepID::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String workflowStepName = "";

		List workflowAllStepList = workflow.getSteps();
		Iterator it = workflowAllStepList.iterator();
		while (it.hasNext()) {
			Workflow.Step localStep = (Workflow.Step) it.next();

			if (localStep.getId().equalsIgnoreCase(workflowCurrentStepString))
				workflowStepName = localStep.getName();

		}

		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Returning Workflow Step Name: " + workflowStepName);
		return workflowStepName;
	}

	/**
	 * This will send in the date the request is raised...
	 * 
	 * @param workflow
	 * @return
	 */
	public static String getRequestDate(Workflow workflow) {

		String METHOD_NAME = "::getRequestDate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Date workflowCreationDate = workflow.getCreated();
		String bcaForatedDate = BCAAuditEvent
				.getDateBCAFormatedDateString(workflowCreationDate);
		logger.debug(CLASS_NAME + METHOD_NAME + "bcaForatedDate: "
				+ bcaForatedDate);

		return bcaForatedDate;
	}

	/**
	 * 
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateBCAFormatedDateString(Date date) {

		String METHOD_NAME = "::getDateBCAFormatedDateString::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String dateString = "";
		final String DATE_FORMAT = "yyyyMMdd"; // Not using the global date
												// format. Most of the time the
												// team do not update the source
												// code into SVN hence creating
												// issue...

		SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);

		Date now = new Date();
		dateString = sdfDate.format(now);
		logger.debug(CLASS_NAME + METHOD_NAME + "Date String: " + dateString);
		return dateString;

	}

	/**
	 * Get the application name from the workflowcase....s
	 * 
	 * @param workflow
	 * @return
	 * @throws GeneralException
	 */
	public static String getTargetApplicationDetails_bak(Workflow workflow)
			throws GeneralException {
		String METHOD_NAME = "::getTargetApplicationDetails::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");
		String targetApplication = "";

		Attributes workflowAttributes = workflow.getVariables();
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Workflow Attribute Details: \n"
				+ workflowAttributes.getKeys() + "\n " + workflow.toXml());

		ProvisioningPlan localProvisioningPlan = (ProvisioningPlan) workflowAttributes
				.get("plan");
		logger.debug(CLASS_NAME + METHOD_NAME + "localProvisioningPlan: "
				+ localProvisioningPlan.toXml());
		List localProvisioningTargetList = localProvisioningPlan
				.getProvisioningTargets();
		Iterator it = localProvisioningTargetList.iterator();
		while (it.hasNext()) {
			ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it
					.next();
			List accountSelectionList = localProvisioningTarget
					.getAccountSelections();
			Iterator it2 = accountSelectionList.iterator();
			while (it2.hasNext()) {

				AccountSelection localAccountSelection = (AccountSelection) it2
						.next();
				logger.debug(CLASS_NAME + METHOD_NAME + "accountSelection: "
						+ localAccountSelection.toXml());

				if (localAccountSelection.getApplicationName() != null) {
					targetApplication = localAccountSelection
							.getApplicationName();
					logger.debug(CLASS_NAME + METHOD_NAME
							+ "targetApplicationFinal: " + targetApplication);
					return targetApplication;
				}

			}

		}

		logger.debug(CLASS_NAME + METHOD_NAME
				+ "defaultValue targetApplication: " + targetApplication);
		return targetApplication;
	}

	// Put the new method here....

	public static String getTargetApplicationDetails(Workflow workflow)
			throws GeneralException {
		String METHOD_NAME = "::getTargetApplicationDetails::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");
		String targetApplication = "";

		Attributes workflowAttributes = workflow.getVariables();
		// ProvisioningPlan localProvisioningPlan = (ProvisioningPlan)
		// workflowAttributes
		// .get("plan");

		ProvisioningProject localProvisioningProject = (ProvisioningProject) workflowAttributes
				.get("project");
		logger.debug(CLASS_NAME + METHOD_NAME + "Provisioning Project: "
				+ localProvisioningProject.toXml());

		// Joy - Added Recently....

		if (localProvisioningProject.getProvisioningTargets() != null) {
			List provisioningTargetList = localProvisioningProject
					.getProvisioningTargets();
			Iterator it1 = provisioningTargetList.iterator();
			while (it1.hasNext()) {

				ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it1
						.next();
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "localProvisioningTargetNew: "
						+ localProvisioningTarget.toXml());

				List accountSelectionList = localProvisioningTarget
						.getAccountSelections();
				Iterator it2 = accountSelectionList.iterator();
				while (it2.hasNext()) {

					AccountSelection localAccountSelection = (AccountSelection) it2
							.next();
					logger.debug(CLASS_NAME + METHOD_NAME
							+ "accountSelection: "
							+ localAccountSelection.toXml());
					

					if (localAccountSelection.getApplicationName() != null) {
						targetApplication = localAccountSelection
								.getApplicationName();
						logger.debug(CLASS_NAME + METHOD_NAME
								+ "targetApplicationFinal: "
								+ targetApplication);
						return targetApplication;
					}
				}
			}
		}

		// Joy - End...

		/*
		 * if (localProvisioningPlan != null) {
		 * 
		 * logger.debug(CLASS_NAME + METHOD_NAME + "Provisioning Plan: " +
		 * localProvisioningPlan.toXml()); List provisioningTargetList =
		 * localProvisioningPlan .getProvisioningTargets();
		 * 
		 * if (provisioningTargetList != null) { Iterator it =
		 * provisioningTargetList.iterator(); while (it.hasNext()) {
		 * ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it
		 * .next();
		 * 
		 * List accountSelectionList = localProvisioningTarget
		 * .getAccountSelections(); Iterator it2 =
		 * accountSelectionList.iterator(); while (it2.hasNext()) {
		 * AccountSelection localAccountSelection = (AccountSelection) it2
		 * .next();
		 * 
		 * targetApplication = localAccountSelection .getApplicationName();
		 * logger.debug(CLASS_NAME + METHOD_NAME + "targetApplication: " +
		 * targetApplication); return targetApplication;
		 * 
		 * }
		 * 
		 * } } } else { logger.debug(CLASS_NAME + METHOD_NAME +
		 * "Provisioning Plan is Null..."); }
		 */
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "defaultValue targetApplication: " + targetApplication);
		return targetApplication;
	}

	/**
	 * @param context
	 * @param baseIdentityUserID
	 * @return
	 * @throws GeneralException
	 */
	public static String getRequesteeBranchCode(SailPointContext context,
			String baseIdentityUserID) throws GeneralException {
		String METHOD_NAME = "::getRequesteeBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String requesteeBranchCode = "";

		if (baseIdentityUserID == null
				|| baseIdentityUserID.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "baseIdentityUserID value passed is either null or blank... : "
					+ baseIdentityUserID);
			return requesteeBranchCode;
		}

		Identity baseIdentity = context.getObjectByName(Identity.class,
				baseIdentityUserID);
		if (baseIdentity != null) {

			if ((String) baseIdentity
					.getAttribute(IdentityAttribute.BRANCH_CODE) != null) {
				requesteeBranchCode = (String) baseIdentity
						.getAttribute(IdentityAttribute.BRANCH_CODE);
				logger.debug(CLASS_NAME + METHOD_NAME + "requesteeBranchCode: "
						+ requesteeBranchCode);
			}
		}
		return requesteeBranchCode;
	}

	/**
	 * This method will return if its a Add/Delete operation requested for this
	 * identity request....
	 * 
	 * @param workflow
	 * @return
	 * @throws GeneralException
	 */
	public static String getRequestedOperation(Workflow workflow)
			throws GeneralException {
		String METHOD_NAME = "::getRequestedOperation::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String requestedOperation = "";

		String workflowCurrentStepString = workflow.getCurrentStep();
		logger.debug(CLASS_NAME + METHOD_NAME + "workflowCurrentStepString: "
				+ workflowCurrentStepString);

		String workflowCurrentStepName = BCAAuditEvent
				.getWorkflowStepNameFromStepID(workflow,
						workflowCurrentStepString);
		logger.debug(CLASS_NAME + METHOD_NAME + "workflowCurrentStepName: "
				+ workflowCurrentStepName);

		if (workflow.getStep(workflowCurrentStepName) != null) {
			Workflow.Step currentWorkflowStep = workflow
					.getStep(workflowCurrentStepName);

			if (workflowCurrentStepName
					.equalsIgnoreCase(BCACustomAuditing.WorkflowStep.START)) {

				Attributes workflowAttributes = workflow.getVariables();
				ApprovalSet localApprovalSet = (ApprovalSet) workflowAttributes
						.get("approvalSet");
				logger.debug(CLASS_NAME + METHOD_NAME + "localApprovalSet: "
						+ localApprovalSet.toXml());

				List approvalItemList = localApprovalSet.getItems();
				Iterator it = approvalItemList.iterator();
				while (it.hasNext()) {
					ApprovalItem localApprovalItem = (ApprovalItem) it.next();
					logger.debug(CLASS_NAME + METHOD_NAME
							+ "localApprovalItem: " + localApprovalItem.toXml());
					requestedOperation = localApprovalItem.getOperation();
					logger.debug(CLASS_NAME + METHOD_NAME
							+ "requestedOperation: " + requestedOperation);
					return requestedOperation;

				}

			} else {

				if (currentWorkflowStep.getApproval() != null) {
					Approval currentApprovalItemExecuted = currentWorkflowStep
							.getApproval();

					if (currentApprovalItemExecuted.getApprovalSet() != null) {
						ApprovalSet localApprovalSet = currentApprovalItemExecuted
								.getApprovalSet();

						if (localApprovalSet.getItems() != null) {
							List approvalItemList = localApprovalSet.getItems();

							Iterator it = approvalItemList.iterator();
							while (it.hasNext()) {
								ApprovalItem localApprovalItem = (ApprovalItem) it
										.next();
								if (localApprovalItem.getOperation() != null) {
									requestedOperation = localApprovalItem
											.getOperation();
									logger.debug(CLASS_NAME + METHOD_NAME
											+ "Requested Operation Returned: "
											+ requestedOperation);
									return requestedOperation;
								}
							}
						}

					}
				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Default Requested Operation Returned: " + requestedOperation);
		return requestedOperation;
	}

	/**
	 * It is assumed that all the time BCA will have request based on roles
	 * hence we are only populating the attributeValue with the role name ....
	 * 
	 * @param workflow
	 * @return
	 */
	public static String getRequestedRoleName(Workflow workflow) {
		String METHOD_NAME = "::getRequestedRoleName::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");

		String requestedRoleName = "";

		Attributes workflowAttributes = workflow.getVariables();
		ProvisioningPlan localProvisioningPlan = (ProvisioningPlan) workflowAttributes
				.get("plan");

		if (localProvisioningPlan != null) {
			List provisioningTargetList = localProvisioningPlan
					.getProvisioningTargets();

			if (provisioningTargetList != null) {
				Iterator it = provisioningTargetList.iterator();
				while (it.hasNext()) {
					ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it
							.next();

					if (localProvisioningTarget.getRole() != null) {
						requestedRoleName = localProvisioningTarget.getRole();
						logger.debug(CLASS_NAME + METHOD_NAME
								+ "requestedRoleName: " + requestedRoleName);
						return requestedRoleName;
					}

				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "requestedRoleName: "
				+ requestedRoleName);
		return requestedRoleName;
	}

	/**
	 * @param workflow
	 * @return
	 */
	public static String getTargetApplicationAccountName(Workflow workflow) {
		String METHOD_NAME = "::getTargetApplicationAccountName::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String accountName = "";
		Attributes workflowAttributes = workflow.getVariables();
		ProvisioningPlan localProvisioningPlan = (ProvisioningPlan) workflowAttributes
				.get("plan");

		if (localProvisioningPlan != null) {
			List provisioningTargetList = localProvisioningPlan
					.getProvisioningTargets();

			if (provisioningTargetList != null) {
				Iterator it = provisioningTargetList.iterator();
				while (it.hasNext()) {
					ProvisioningTarget localProvisioningTarget = (ProvisioningTarget) it
							.next();

					List accountSelectionList = localProvisioningTarget
							.getAccountSelections();
					Iterator it2 = accountSelectionList.iterator();
					while (it2.hasNext()) {
						AccountSelection localAccountSelection = (AccountSelection) it2
								.next();

						accountName = localAccountSelection
								.getApplicationName();
						logger.debug(CLASS_NAME + METHOD_NAME + "accountName: "
								+ accountName);
						return accountName;

					}

				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "accountName: " + accountName);

		return accountName;
	}

	/**
	 * We are making custom Audit Action and not using OOTB audit Actions.. this
	 * is to ensure we get the right audit data during our report...
	 * 
	 * @param workflow
	 * @return
	 */
	public static String getAuditAction(Workflow workflow) {

		String METHOD_NAME = "::getAuditAction::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String workflowCurrentStepString = workflow.getCurrentStep();
		logger.debug(CLASS_NAME + METHOD_NAME + "Audit Action Returned : "
				+ workflowCurrentStepString);

		String workflowCurrentStepName = BCAAuditEvent
				.getWorkflowStepNameFromStepID(workflow,
						workflowCurrentStepString);

		return workflowCurrentStepName;
	}

	/**
	 * @param provisioningProject
	 * @return
	 */
	public static String getAccountID(ProvisioningProject provisioningProject) {
		String METHOD_NAME = "::getAccountID::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String accountID = "";
		List provisioningTargetList = provisioningProject
				.getProvisioningTargets();

		Iterator it = provisioningTargetList.iterator();
		while (it.hasNext()) {
			ProvisioningTarget localProvisioningProject = (ProvisioningTarget) it
					.next();

			List accountSelectorList = localProvisioningProject
					.getAccountSelections();
			Iterator it2 = accountSelectorList.iterator();
			while (it2.hasNext()) {
				AccountSelection localAccountSelection = (AccountSelection) it2
						.next();
				if (!localAccountSelection.getApplicationName()
						.equalsIgnoreCase("")) {
					accountID = localAccountSelection.getApplicationName();
					logger.debug(CLASS_NAME + METHOD_NAME + "accountID: "
							+ accountID);
					return accountID;

				}

			}

		}
		logger.debug(CLASS_NAME + METHOD_NAME + "accountID: " + accountID);
		return accountID;
	}

	/**
	 * @param workflow
	 * @return
	 * @throws GeneralException
	 */
	public static String getWorkItemOutcome(Workflow workflow)
			throws GeneralException {
		String METHOD_NAME = "::getWorkItemOutcome::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String workItemState = WorkItem.State.Pending.toString();

		// Workflow.Step approvalStep = null;
		String workflowCurrentStepString = workflow.getCurrentStep();
		logger.debug(CLASS_NAME + METHOD_NAME + "workflowCurrentStepString: "
				+ workflowCurrentStepString);

		String workflowCurrentStepName = BCAAuditEvent
				.getWorkflowStepNameFromStepID(workflow,
						workflowCurrentStepString);
		logger.debug(CLASS_NAME + METHOD_NAME + "workflowCurrentStepName: "
				+ workflowCurrentStepString);

		Workflow.Step currentWorkflowStep = workflow
				.getStep(workflowCurrentStepName);
		logger.debug(CLASS_NAME + METHOD_NAME + "currentWorkflowStep: "
				+ currentWorkflowStep.toXml());

		logger.debug(CLASS_NAME + METHOD_NAME
				+ "currentWorkflowStep.isComplete(): "
				+ currentWorkflowStep.isComplete());

		Approval currentApprovalItemExecuted = currentWorkflowStep
				.getApproval();
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "currentApprovalItemExecuted.isComplete(): "
				+ currentApprovalItemExecuted.isComplete());

		if (currentApprovalItemExecuted.isComplete()) {
			ApprovalSet localApprovalSet = currentApprovalItemExecuted
					.getApprovalSet();

			List approvalItemList = localApprovalSet.getItems();
			logger.debug(CLASS_NAME + METHOD_NAME + "approvalItemList.size(): "
					+ approvalItemList.size());

			Iterator it = approvalItemList.iterator();
			while (it.hasNext()) {
				ApprovalItem localApprovalItem = (ApprovalItem) it.next();
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "localApprovalItem.getState(): "
						+ localApprovalItem.getState());

				workItemState = WorkItem.State.Finished.toString();

				if (localApprovalItem.getState() != null) {

					workItemState = localApprovalItem.getState().toString();
					logger.debug(CLASS_NAME + METHOD_NAME
							+ "Returning workItemState: " + workItemState);

					return workItemState;
				}
			}

		}

		logger.debug(CLASS_NAME + METHOD_NAME + "Returning workItemState: "
				+ workItemState);
		return workItemState;
	}

	/**
	 * @param workflow
	 * @param stepName
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isApprovalFinished(Workflow workflow, String stepName)
			throws GeneralException {
		String METHOD_NAME = "::isApprovalFinished::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		logger.debug(CLASS_NAME + METHOD_NAME + "Step Name: " + stepName);

		boolean isApprovalFinished = false;
		Workflow.Step approvalStep = null;

		List stepList = workflow.getSteps();
		Iterator it = stepList.iterator();
		while (it.hasNext()) {
			Workflow.Step localStep = (Step) it.next();
			logger.debug(CLASS_NAME + METHOD_NAME + "Step Name: "
					+ localStep.getName());

			if (localStep.getName().equalsIgnoreCase(stepName)) {
				approvalStep = localStep;
				logger.debug(CLASS_NAME + METHOD_NAME + "Found Step: "
						+ localStep.getName());
			}

		}

		logger.debug(CLASS_NAME + METHOD_NAME + "approvalStep.toXml(): "
				+ approvalStep.toXml());

		if (approvalStep != null) {
			Approval workflowApproval = approvalStep.getApproval();
			logger.debug(CLASS_NAME + METHOD_NAME + "WorkflowApproval: "
					+ workflowApproval.toXml());
			if (workflowApproval.isComplete()) {
				isApprovalFinished = true;
				logger.debug(CLASS_NAME + METHOD_NAME + "isApprovalFinished: "
						+ isApprovalFinished);
				return isApprovalFinished;
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "isApprovalFinished: "
				+ isApprovalFinished);
		return isApprovalFinished;
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
			Approval currentlyExecutedApproval = currentWorkflowStep
					.getApproval();

			if (currentlyExecutedApproval != null) {
				if (currentlyExecutedApproval.isComplete()) {
					approver = currentlyExecutedApproval.getOwner();
					logger.debug(CLASS_NAME + METHOD_NAME + "Approver: "
							+ approver);
				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "Approver: " + approver);
		return approver;
	}

	/**
	 * @param workflow
	 * @return
	 * @throws ParseException
	 */
	public static Date getWorkflowStartDate(Workflow workflow)
			throws ParseException {
		String METHOD_NAME = "::getWorkflowStartDate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Workflow.Step approvalStep = workflow
				.getStep(BCACustomAuditing.WorkflowStep.APPROVER_1);
		Long dateString = approvalStep.getStartTime();
		logger.debug(CLASS_NAME + METHOD_NAME + "dateString: " + dateString);

		Date createdDate = new Date(dateString);
		logger.debug(CLASS_NAME + METHOD_NAME + "createdDate: " + createdDate);
		return createdDate;
	}

	/**
	 * @param context
	 * @param item
	 * @param launcher
	 * @return
	 * @throws GeneralException
	 */
	public static String getAuditSource(SailPointContext context,
			WorkItem item, String launcher) throws GeneralException {
		String METHOD_NAME = "::getAuditSource::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String actor = context.getUserName();
		logger.debug(CLASS_NAME + METHOD_NAME + "actor: " + actor);

		Identity identity = context.getObjectByName(Identity.class, actor);

		if (identity != null) {
			actor = identity.getDisplayName();
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity: "
					+ identity.getDisplayName());
		} else {
			identity = item.getOwner();
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity: "
					+ identity.getDisplayName());

			actor = identity.getDisplayName();
			logger.debug(CLASS_NAME + METHOD_NAME + "actor: " + actor);
			logger.debug(CLASS_NAME + METHOD_NAME + "actor: " + actor);
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "actor: " + actor);
		return actor;
	}

}