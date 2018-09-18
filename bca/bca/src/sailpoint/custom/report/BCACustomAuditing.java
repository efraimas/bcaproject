package sailpoint.custom.report;

public class BCACustomAuditing {

	/**
	 * Input Arguments from Report....
	 * 
	 * @author joydeep.mondal
	 *
	 */
	public static class ReportInputArgument {
		public static final String INPUT_APPLICATION = "applications";
		public static final String INPUT_STATUS = "state";
		public static final String REQUEST_DATE_RANGE = "requestDateRange";
		public static final String INPUT_OPERATION = "operation";
		public static final String INPUT_RETENTION = "Retention";
		public static final String INPUT_LAPRON = "Laporan";
	}

	/**
	 * This should be superset or same set as "ReportOutputColumnConfig" report
	 * output column config...
	 * 
	 * @author joydeep.mondal
	 *
	 */
	public static class AuditEventAttributes {
		public static final String REQUEST_ID = "requestId";
		public static final String REQUEST_DATE = "requestDate";
		public static final String APPLICATOIN_NAME = "applicationName";
		public static final String USER_ID = "userID";
		public static final String REQUESTER = "requester";
		public static final String REQUESTER_BRANCH_CODE = "requesterBranchCode";
		public static final String APPROVER1 = "approver1";
		public static final String APPROVER2 = "approver2";
		public static final String CHECKER1 = "checker1";
		public static final String CHECKER2 = "checker2";
		public static final String SA_APPLIKASI = "saAplikasi";
		public static final String ACCOUNT_ID = "accountID";
		public static final String OPERATION = "operation";
		public static final String WORKITEM_OUTCOME = "workitemOutcome";
		public static final String REQUEST_STATUS = "state";
		public static final String IS_REJECTED = "isRejected";

	}

	/**
	 * Audit Event Filters......
	 * 
	 * @author joydeep.mondal
	 *
	 */
	public static class ReportAuditEventFilter {
		public static final String FILTER_ACTION = "action";
		public static final String AUDIT_EVENT_FILTER_APPLICATION = "application";
		public static final String REQUEST_DATE_RANGE = "requestDateRange";
		public static final String COMPLETION_DATE_RANGE = "completionDateRange";
	}

	/**
	 * List of Output arguments as defined in the ReportColumnConfig
	 * Definition...
	 * 
	 * @author joydeep.mondal
	 *
	 */
	public static class ReportOutputColumnConfig {

		public static final String REQUEST_ID = "requestId";
		public static final String REQUEST_DATE = "requestDate";
		public static final String APPLICATOIN_NAME = "applicationName";
		public static final String USER_ID = "userID";
		public static final String REQUESTER = "requester";
		public static final String REQUESTER_BRANCH_CODE = "requesterBranchCode";
		public static final String APPROVER1 = "approver1";
		public static final String APPROVER2 = "approver2";
		public static final String CHECKER1 = "checker1";
		public static final String CHECKER2 = "checker2";
		public static final String SA_APPLIKASI = "saAplikasi";
		public static final String ACCOUNT_ID = "accountID";
		public static final String ACCOUNT_REQUEST_OPERATION = "operation";
		public static final String WORKITEM_OUTCOME = "outcome";
		public static final String REQUEST_STATUS = "state";
	}

	/**
	 * This steps should match the list of approval step mentioned in workflow
	 * "BCA Provisioning Approval SubProcess" If there is a change in the
	 * workflow steps... make sure to add it out here....
	 * 
	 * @author joydeep.mondal
	 *
	 */
	public static class WorkflowStep {
		public static final String START = "Start";
		public static final String APPROVER_1 = "Approver 1";
		public static final String APPROVER_2 = "Approver 2";
		public static final String CHECKER_1 = "Checker 1";
		public static final String CHECKER_2 = "Checker 2";
		public static final String SA_APLIKASI = "SA Aplikasi";
		public static final String PROCESS_APPROVAL_DECISION = "Process Approval Decisions";
		public static final String STOP = "Stop";

	}

	/**
	 * If column config got changed.... then add those columns here...
	 * 
	 * @author joydeep.mondal
	 *
	 */
	public static class ReportSummaryColumn {

		public static final String RETENSI = "Retensi";
		public static final String LAPORAN = "Laporan";
		public static final String BRANCH_CODE = "BranchCode";
		public static final String FREKUENSI = "Frekuensi";
		public static final String CURRENT_DATE = "CurrentDate";

	}

}
