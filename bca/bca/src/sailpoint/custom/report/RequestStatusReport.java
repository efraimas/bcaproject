package sailpoint.custom.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.AuditEvent;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.LiveReport;
import sailpoint.object.QueryOptions;
import sailpoint.object.ReportDataSource;
import sailpoint.object.ReportDataSource.Parameter;
import sailpoint.object.Sort;
import sailpoint.object.WorkItem;
import sailpoint.reporting.DateRange;
import sailpoint.reporting.datasource.JavaDataSource;
import sailpoint.server.InternalContext;
import sailpoint.task.Monitor;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class RequestStatusReport implements JavaDataSource {

	public static String CLASS_NAME = "::RequestStatusReport::";
	public static Logger logger = Logger
			.getLogger("sailpoint.custom.report.RequestStatusReport");

	// Variables used in the report...
	private QueryOptions baseQueryOptions;
	// private SailPointContext context;
	List<Map> reportData = new ArrayList();
	private boolean hasMore = true;
	private int recordCount = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sailpoint.reporting.datasource.JavaDataSource#initialize(sailpoint.api
	 * .SailPointContext, sailpoint.object.LiveReport,
	 * sailpoint.object.Attributes, java.lang.String, java.util.List)
	 */
	@Override
	public void initialize(SailPointContext sailPointContext,
			LiveReport report, Attributes<String, Object> attributes,
			String groupBy, List<Sort> sort) throws GeneralException {
		String METHOD_NAME = "::initialize::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside... ");

		logger.debug(CLASS_NAME + METHOD_NAME + " attributes xml are "
				+ attributes.getOriginalXml());

		// TODO Remove Later... the print statement...
		RequestStatusReport.toPrint(report, attributes, groupBy, sort);

		Map attributeMap = attributes.getMap();

		String launcher = (String) attributeMap.get("launcher");
		logger.debug(CLASS_NAME + METHOD_NAME + "launcher: " + launcher);
		Identity launcherIdentity = sailPointContext.getObjectByName(
				Identity.class, launcher);
		logger.debug(CLASS_NAME + METHOD_NAME + "launcherIdentity: "
				+ launcherIdentity.getName());
		String sessionOwnerBranchCode = (String) launcherIdentity
				.getAttribute(IdentityAttribute.BRANCH_CODE);
		logger.debug(CLASS_NAME + METHOD_NAME + "sessionOwnerBranchCode: "
				+ sessionOwnerBranchCode);

		// Variable Initialization ...
		this.baseQueryOptions = new QueryOptions();

		// Get Final Query...
		baseQueryOptions = RequestStatusReport.getActualReportFilteredQuery(
				sailPointContext, attributes, sort);

		// Get a map like this... Map<requestID,List<AuditEvent>>
		Map auditEventMap = new HashMap();

		auditEventMap = RequestStatusReport.groupAuditEventByRequestID(
				sailPointContext, baseQueryOptions);
		RequestStatusReport.printAuditEventMap(auditEventMap);

		// This will hold the value like List<Map<requestID,List<AuditEvent>>>
		List<Map> beforeStatusFilterMap = new ArrayList<Map>();

		// Getting the list of all unique Request ID Keys...
		Set requestIDKeySet = auditEventMap.keySet();
		Iterator it = requestIDKeySet.iterator();
		while (it.hasNext()) {
			String requestId = (String) it.next();

			if (auditEventMap.containsKey(requestId)) {
				List auditEventList = (List) auditEventMap.get(requestId);
				Map individualRequestIDMap;
				try {
					individualRequestIDMap = RequestStatusReport
							.getSingleRecordMapFromAuditEventList(requestId,
									auditEventList);
					beforeStatusFilterMap.add(individualRequestIDMap);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		List statusFilter = RequestStatusReport.getStatusFilter(attributes);
		logger.debug(CLASS_NAME + METHOD_NAME + "statusFilter: " + statusFilter);

		List branchFilter = RequestStatusReport.getBranchCodeFilter(
				sailPointContext, CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT,
				sessionOwnerBranchCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "branchFilter: " + branchFilter);

		if (statusFilter.size() > 0) {

			logger.debug(CLASS_NAME + METHOD_NAME
					+ "beforeStatusFilterMap Size: "
					+ beforeStatusFilterMap.size());

			List afterStatusFilterList = RequestStatusReport
					.filterDataBasedOnStatus(beforeStatusFilterMap,
							statusFilter);
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "afterStatusFilterMap Size: "
					+ afterStatusFilterList.size());
			List afterBranchCodeFilterList = RequestStatusReport
					.filterBasedOnBranchCode(afterStatusFilterList,
							branchFilter);
			Iterator it4 = afterBranchCodeFilterList.iterator();
			while (it4.hasNext()) {
				Map localMap = (Map) it4.next();
				reportData.add(localMap);
			}

		} else {

			List afterBranchCodeFilterList = RequestStatusReport
					.filterBasedOnBranchCode(beforeStatusFilterMap,
							branchFilter);

			Iterator it5 = afterBranchCodeFilterList.iterator();
			while (it5.hasNext()) {
				Map localMap = (Map) it5.next();
				reportData.add(localMap);
			}
		}

	}

	/**
	 * @param beforeStatusFilterMap
	 * @param statusFilter
	 * @return
	 */
	public static List filterBasedOnBranchCode(
			List<Map> beforeBranchCodeFilterMap, List branchCodeFilter) {
		String METHOD_NAME = "::filterBasedOnBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		List afterBranchCodeFilterList = new ArrayList();

		logger.debug(CLASS_NAME + METHOD_NAME
				+ "beforeBranchCodeFilterMap Size: "
				+ beforeBranchCodeFilterMap.size());

		Iterator it = beforeBranchCodeFilterMap.iterator();
		while (it.hasNext()) {
			boolean shouldAddToList = false;
			Map localSingleRecordMap = (Map) it.next();
			if (localSingleRecordMap
					.containsKey(BCACustomAuditing.AuditEventAttributes.REQUESTER_BRANCH_CODE)) {
				if (localSingleRecordMap
						.get(BCACustomAuditing.AuditEventAttributes.REQUESTER_BRANCH_CODE) != null) {

					String branchCode = (String) localSingleRecordMap
							.get(BCACustomAuditing.AuditEventAttributes.REQUESTER_BRANCH_CODE);
					logger.debug(CLASS_NAME + METHOD_NAME + "branchCode: "
							+ branchCode);

					if (branchCodeFilter.contains(branchCode)) {
						shouldAddToList = true;
					}

				}

			}
			if (shouldAddToList) {
				afterBranchCodeFilterList.add(localSingleRecordMap);
			}
		}
		logger.debug(CLASS_NAME + METHOD_NAME + "afterBranchCodeFilterList: "
				+ +afterBranchCodeFilterList.size());

		return afterBranchCodeFilterList;
	}

	/**
	 * @param beforeStatusFilterMap
	 * @param statusFilter
	 * @return
	 */
	public static List filterDataBasedOnStatus(List<Map> beforeStatusFilterMap,
			List statusFilter) {

		String METHOD_NAME = "::filterDataBasedOnStatus::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		List statusFilterUpperCase = new ArrayList();
		Iterator it2 = statusFilter.iterator();
		while (it2.hasNext()) {
			String localStatusFilter = (String) it2.next();
			statusFilterUpperCase.add(localStatusFilter.toUpperCase());
		}
		logger.debug(CLASS_NAME + METHOD_NAME + "statusFilterUpperCase: "
				+ statusFilterUpperCase);

		List filteredListToReturn = new ArrayList();

		Iterator it = beforeStatusFilterMap.iterator();
		while (it.hasNext()) {
			boolean shouldAddToList = false;
			Map localSingleRecordMap = (Map) it.next();

			if (localSingleRecordMap
					.containsKey(BCACustomAuditing.AuditEventAttributes.OPERATION)) {
				if (localSingleRecordMap
						.get(BCACustomAuditing.AuditEventAttributes.OPERATION) != null) {
					String localRequestStatus = (String) localSingleRecordMap
							.get(BCACustomAuditing.AuditEventAttributes.OPERATION);
					logger.debug(CLASS_NAME + METHOD_NAME
							+ "localRequestStatus: " + localRequestStatus);

					if (statusFilterUpperCase.contains(localRequestStatus
							.toUpperCase())) {
						logger.debug(CLASS_NAME
								+ METHOD_NAME
								+ "Request ID: "
								+ localSingleRecordMap
										.get(BCACustomAuditing.AuditEventAttributes.REQUEST_ID)
								+ " satisfies the filter... hence should be part of the report...");
						shouldAddToList = true;
					}

				}

			}
			if (shouldAddToList) {
				filteredListToReturn.add(localSingleRecordMap);
			}

		}
		logger.debug(CLASS_NAME + METHOD_NAME + "filteredListToReturn: "
				+ +filteredListToReturn.size());
		return filteredListToReturn;
	}

	/**
	 * Will get the status filter as a list which will be fed into the
	 * filterDataBasedOnStatus() method...
	 * 
	 * @param attributes
	 * @return
	 */
	public static List getStatusFilter(Attributes<String, Object> attributes) {

		String METHOD_NAME = "::getStatusFilter::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		List statusList = new ArrayList();
		List statusFilterList = new ArrayList();
		if (attributes
				.containsKey(BCACustomAuditing.ReportInputArgument.INPUT_OPERATION)) {
			statusList = attributes
					.getList(BCACustomAuditing.ReportInputArgument.INPUT_OPERATION);
			Iterator it = statusList.iterator();
			while (it.hasNext()) {
				String localStatusList = (String) it.next();
				logger.debug(CLASS_NAME + METHOD_NAME + "localStatusList: "
						+ localStatusList);
				statusFilterList.add(localStatusList);

				// Filter filter = Filter.eq("string3", localStatusList);
				// logger.debug(CLASS_NAME + METHOD_NAME
				// + "Report Status Filter: " + filter.toXml());
				// bcaReportQueryOption.addFilter(filter);

			}
		}
		logger.debug(CLASS_NAME + METHOD_NAME + "Operation Filter List: "
				+ statusFilterList);
		return statusFilterList;
	}

	/**
	 * @param auditEventMap
	 */
	public static void printAuditEventMap(Map auditEventMap) {
		String METHOD_NAME = "::printAuditEventMap::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Set keyList = auditEventMap.keySet();
		Iterator it = keyList.iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			List auditEventList = (List) auditEventMap.get(keyName);
			Iterator it2 = auditEventList.iterator();
			while (it2.hasNext()) {
				AuditEvent localAuditEvent = (AuditEvent) it2.next();
				logger.debug(CLASS_NAME + METHOD_NAME + ""
						+ localAuditEvent.getTrackingId() + " :: "
						+ localAuditEvent.getId());
			}
		}

	}

	/**
	 * @param context
	 * @param baseQueryOptions
	 * @return
	 * @throws GeneralException
	 */
	public static Map groupAuditEventByRequestID(SailPointContext context,
			QueryOptions baseQueryOptions) throws GeneralException {
		String METHOD_NAME = "::groupAuditEventByRequestID::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		Map groupedAuditEventMap = new HashMap();

		logger.debug(CLASS_NAME + METHOD_NAME + "baseQueryOptions: "
				+ baseQueryOptions);
		List requestIDList = new ArrayList();

		// Search.....
		Iterator searchResultIterator = context.search(AuditEvent.class,
				baseQueryOptions);
		while (searchResultIterator.hasNext()) {
			AuditEvent localAuditEvent = (AuditEvent) searchResultIterator
					.next();
			String requestID = localAuditEvent.getTrackingId();
			logger.debug(CLASS_NAME + METHOD_NAME + "requestID: " + requestID);
			if (requestIDList != null) {
				requestIDList.add(requestID);
			}

		}

		Set uniqueRequestSet = new HashSet(requestIDList);
		List uniqueRequestIDList = new ArrayList(uniqueRequestSet);
		logger.debug(CLASS_NAME + METHOD_NAME + "uniqueRequestIDList: "
				+ uniqueRequestIDList);

		Iterator secondTimeSearchIterator = context.search(AuditEvent.class,
				baseQueryOptions);

		while (secondTimeSearchIterator.hasNext()) {
			AuditEvent localAuditEvent = (AuditEvent) secondTimeSearchIterator
					.next();
			String requestID = localAuditEvent.getTrackingId();

			List localRequestIDList = (List) groupedAuditEventMap
					.get(requestID);
			if (localRequestIDList == null) {
				localRequestIDList = new ArrayList();
				localRequestIDList.add(localAuditEvent);
			} else {
				localRequestIDList.add(localAuditEvent);
			}
			groupedAuditEventMap.put(requestID, localRequestIDList);

		}

		logger.debug(CLASS_NAME + METHOD_NAME + "groupedAuditEventMap: "
				+ groupedAuditEventMap);

		return groupedAuditEventMap;

	}

	/**
	 * @param requestId
	 * @param requestDate
	 * @param applicationName
	 * @param userID
	 * @param requester
	 * @param requestBranchCode
	 * @param approver1
	 * @param approver2
	 * @param checker1
	 * @param checker2
	 * @param saAplikasi
	 * @param accountID
	 * @param requestStatus
	 * @param requestStatus2
	 * @param workItemOutcome
	 * @return
	 */

	public static Map createSingleRecordMap(String requestId,
			String requestDate, String applicationName, String userID,
			String requester, String requestBranchCode, String approver1,
			String approver2, String checker1, String checker2,
			String saAplikasi, String accountID,
			String accountRequestOperation, String workItemOutcome,
			String requestStatus) {
		String METHOD_NAME = "::createSingleRecordMap::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Map singleRecordMap = new HashMap();

		if (requestId == null)
			requestId = "";

		if (requestDate == null)
			requestDate = "";

		if (applicationName == null)
			applicationName = "";

		if (userID == null)
			userID = "";

		if (requester == null)
			requester = "";

		if (requestBranchCode == null)
			requestBranchCode = "";

		if (approver1 == null)
			approver1 = "";

		if (approver2 == null)
			approver2 = "";

		if (checker1 == null)
			checker1 = "";

		if (checker2 == null)
			checker2 = "";

		if (saAplikasi == null)
			saAplikasi = "";

		if (accountID == null)
			accountID = "";

		if (accountRequestOperation == null)
			accountRequestOperation = "";

		if (workItemOutcome == null)
			workItemOutcome = "";

		if (requestStatus == null)
			requestStatus = "";

		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.REQUEST_ID,
				requestId);
		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.REQUEST_DATE,
				requestDate);
		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.APPLICATOIN_NAME,
				applicationName);
		singleRecordMap.put(BCACustomAuditing.ReportOutputColumnConfig.USER_ID,
				userID);
		singleRecordMap
				.put(BCACustomAuditing.ReportOutputColumnConfig.REQUESTER,
						requester);
		singleRecordMap
				.put(BCACustomAuditing.ReportOutputColumnConfig.REQUESTER_BRANCH_CODE,
						requestBranchCode);
		singleRecordMap
				.put(BCACustomAuditing.ReportOutputColumnConfig.APPROVER1,
						approver1);
		singleRecordMap
				.put(BCACustomAuditing.ReportOutputColumnConfig.APPROVER2,
						approver2);
		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.CHECKER1, checker1);
		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.CHECKER2, checker2);
		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.SA_APPLIKASI,
				saAplikasi);
		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.ACCOUNT_ID,
				accountID);
		singleRecordMap
				.put(BCACustomAuditing.ReportOutputColumnConfig.ACCOUNT_REQUEST_OPERATION,
						accountRequestOperation);
		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.WORKITEM_OUTCOME,
				workItemOutcome);
		singleRecordMap.put(
				BCACustomAuditing.ReportOutputColumnConfig.REQUEST_STATUS,
				requestStatus);

		logger.debug(CLASS_NAME + METHOD_NAME + "singleRecordMap: "
				+ singleRecordMap);
		return singleRecordMap;
	}

	/**
	 * @param auditEventList
	 *            - List<AuditEvent>
	 * @return
	 * @throws GeneralException
	 * @throws ParseException
	 */
	public static Map getSingleRecordMapFromAuditEventList(String requestId,
			List auditEventList) throws GeneralException, ParseException {
		String METHOD_NAME = "::getSingleRecordMapFromAuditEventList::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String requestDate = "";
		String applicationName = "";
		String userID = "";
		String requester = "";
		String requestBranchCode = "";
		String approver1 = "";
		String approver2 = "";
		String checker1 = "";
		String checker2 = "";
		String saAplikasi = "";
		String accountID = "";
		String accountRequestOperation = "";
		String workItemOutcome = "";
		String requestStatus = "";

		// Variable which will hold the list of all Request Status for a
		// particular Identity Request ID...
		List requestStatusList = new ArrayList();
		List actionList = new ArrayList();
		List workItemOutComeList = new ArrayList();

		Iterator it = auditEventList.iterator();
		while (it.hasNext()) {
			AuditEvent localAuditEvent = (AuditEvent) it.next();
			logger.debug(CLASS_NAME + METHOD_NAME + "Local Audit Event XML : "
					+ localAuditEvent.toXml());

			if (localAuditEvent.getAttributes() != null) {
				Attributes localAuditEventAttribute = localAuditEvent
						.getAttributes();
				Map localMap = localAuditEventAttribute.getMap();

				// This step is to ensure it do not fail in successive steps...
				if (localMap == null) {
					localMap = new HashMap();
				}

				String approvalStepString = localAuditEvent.getAction();
				logger.debug(CLASS_NAME + METHOD_NAME + "approvalStepString: "
						+ approvalStepString);

				if (approvalStepString == null) {
					approvalStepString = "";
				}

				actionList.add(approvalStepString);

				// Getting the request status....
				requestStatusList.add(localAuditEvent.getString3());

				// If this event is from start step in the workflow.. then get
				// the
				// request
				// date...

				if (localAuditEvent.getAction().equalsIgnoreCase(
						BCACustomAuditing.WorkflowStep.START)) {
					Date requestDateDate = localAuditEvent.getCreated();
					logger.debug(CLASS_NAME + METHOD_NAME + "requestDateDate: "
							+ requestDateDate);

					requestDate = BCAAuditEvent
							.getDateBCAFormatedDateString(requestDateDate);
					logger.debug(CLASS_NAME + METHOD_NAME
							+ "For Identity Request "
							+ localAuditEvent.getTrackingId()
							+ " the requested date is " + requestDate);
					Date toShowDate = CommonUtil.getBCADateFormat(requestDate);

					final String REPORT_DATE_FORMAT = "dd-MMM-yyyy";

					SimpleDateFormat sdfDate = new SimpleDateFormat(
							REPORT_DATE_FORMAT);

					requestDate = sdfDate.format(toShowDate);
					logger.debug(CLASS_NAME + METHOD_NAME + "Date String: "
							+ requestDate);

				}

				// Getting application Name...
				if (applicationName == null
						|| applicationName.equalsIgnoreCase("")) {
					applicationName = localAuditEvent.getApplication();
				}

				// Getting User ID...
				if (localMap
						.containsKey(BCACustomAuditing.AuditEventAttributes.USER_ID)) {
					if (localMap
							.get(BCACustomAuditing.AuditEventAttributes.USER_ID) != null) {
						userID = (String) localMap
								.get(BCACustomAuditing.AuditEventAttributes.USER_ID);
						logger.debug(CLASS_NAME + METHOD_NAME + "userID: "
								+ userID);
					}
				}

				// Getting requester.....
				if (localMap
						.containsKey(BCACustomAuditing.AuditEventAttributes.REQUESTER)) {
					if (localMap
							.get(BCACustomAuditing.AuditEventAttributes.REQUESTER) != null) {
						requester = (String) localMap
								.get(BCACustomAuditing.AuditEventAttributes.REQUESTER);
						logger.debug(CLASS_NAME + METHOD_NAME + "requester: "
								+ requester);
					}
				}

				// Get Requester Branch Code....
				if (localMap
						.containsKey(BCACustomAuditing.AuditEventAttributes.REQUESTER_BRANCH_CODE)) {
					if (localMap
							.get(BCACustomAuditing.AuditEventAttributes.REQUESTER_BRANCH_CODE) != null) {
						requestBranchCode = (String) localMap
								.get(BCACustomAuditing.AuditEventAttributes.REQUESTER_BRANCH_CODE);
						logger.debug(CLASS_NAME + METHOD_NAME
								+ "requestBranchCode: " + requestBranchCode);
					}
				}

				// Get Approver 1
				if (approvalStepString
						.equalsIgnoreCase(BCACustomAuditing.WorkflowStep.APPROVER_1)) {
					approver1 = localAuditEvent.getSource();
					logger.debug(CLASS_NAME + METHOD_NAME + "approver1: "
							+ approver1);
				}

				// Get Approver 2
				if (approvalStepString
						.equalsIgnoreCase(BCACustomAuditing.WorkflowStep.APPROVER_2)) {
					approver2 = localAuditEvent.getSource();
					logger.debug(CLASS_NAME + METHOD_NAME + "approver2: "
							+ approver2);
				}

				// Get Checker 1...

				if (approvalStepString
						.equalsIgnoreCase(BCACustomAuditing.WorkflowStep.CHECKER_1)) {
					checker1 = localAuditEvent.getSource();
					logger.debug(CLASS_NAME + METHOD_NAME + "checker2: "
							+ checker2);
				}

				// Get Checker 2
				if (approvalStepString
						.equalsIgnoreCase(BCACustomAuditing.WorkflowStep.CHECKER_2)) {
					checker2 = localAuditEvent.getSource();
					logger.debug(CLASS_NAME + METHOD_NAME + "checker2: "
							+ checker2);
				}

				// Get SA Aplikasi
				if (approvalStepString
						.equalsIgnoreCase(BCACustomAuditing.WorkflowStep.SA_APLIKASI)) {
					saAplikasi = localAuditEvent.getSource();
					logger.debug(CLASS_NAME + METHOD_NAME + "saAplikasi: "
							+ saAplikasi);
				}

				// Getting Account ID...
				if (localMap
						.containsKey(BCACustomAuditing.AuditEventAttributes.ACCOUNT_ID)) {
					if (localMap
							.get(BCACustomAuditing.AuditEventAttributes.ACCOUNT_ID) != null) {
						accountID = (String) localMap
								.get(BCACustomAuditing.AuditEventAttributes.ACCOUNT_ID);
						logger.debug(CLASS_NAME + METHOD_NAME + "accountID: "
								+ accountID);
					}
				}

				// Get Account Request Operation...

				// Getting Account ID...
				if (localMap
						.containsKey(BCACustomAuditing.AuditEventAttributes.OPERATION)) {
					if (localMap
							.get(BCACustomAuditing.AuditEventAttributes.OPERATION) != null) {
						accountRequestOperation = (String) localMap
								.get(BCACustomAuditing.AuditEventAttributes.OPERATION);
						logger.debug(CLASS_NAME + METHOD_NAME
								+ "accountRequestOperation: "
								+ accountRequestOperation);
					}
				}

				// Get Work Item Outcome.....
				if (localMap
						.containsKey(BCACustomAuditing.AuditEventAttributes.WORKITEM_OUTCOME)) {
					if (localMap
							.get(BCACustomAuditing.AuditEventAttributes.WORKITEM_OUTCOME) != null) {
						workItemOutcome = (String) localMap
								.get(BCACustomAuditing.AuditEventAttributes.WORKITEM_OUTCOME);
						logger.debug(CLASS_NAME + METHOD_NAME
								+ "workItemOutcome: " + workItemOutcome);
						workItemOutComeList.add(workItemOutcome);
					}
				}
			}
		}

		// Get Request Status....
		logger.debug(CLASS_NAME + METHOD_NAME + "requestStatusList: "
				+ requestStatusList);

		// Now get the final status....
		if (requestStatusList.contains(WorkItem.State.Finished.toString())) {
			requestStatus = WorkItem.State.Finished.toString();
			logger.debug(CLASS_NAME + METHOD_NAME + "requestStatus: "
					+ requestStatus);
		} else {
			requestStatus = WorkItem.State.Pending.toString();
			logger.debug(CLASS_NAME + METHOD_NAME + "requestStatus: "
					+ requestStatus);

		}

		if (!(actionList.contains(BCACustomAuditing.WorkflowStep.STOP))) {
			if ((actionList.contains(BCACustomAuditing.WorkflowStep.START))) {
				requestStatus = WorkItem.State.Pending.toString();
				logger.debug(CLASS_NAME + METHOD_NAME + "requestStatus: "
						+ requestStatus);
			}
		} else {
			requestStatus = WorkItem.State.Finished.toString();
			logger.debug(CLASS_NAME + METHOD_NAME + "requestStatus: "
					+ requestStatus);
		}

		if (workItemOutComeList.contains(WorkItem.State.Rejected.toString())) {
			workItemOutcome = WorkItem.State.Rejected.toString();
			logger.debug(CLASS_NAME + METHOD_NAME + "workItemOutcome: "
					+ workItemOutcome);
		} else if (actionList.contains(BCACustomAuditing.WorkflowStep.STOP)
				&& !workItemOutComeList.contains(WorkItem.State.Rejected
						.toString())) {
			workItemOutcome = "Approved";
			logger.debug(CLASS_NAME + METHOD_NAME + "workItemOutcome: "
					+ workItemOutcome);

		} else {
			workItemOutcome = WorkItem.State.Pending.toString();
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "requestStatus: "
				+ requestStatus);

		// *******************************************************
		// Getting the single record map per request....
		// *******************************************************
		Map singleRecordMap = RequestStatusReport.createSingleRecordMap(
				requestId, requestDate, applicationName, userID, requester,
				requestBranchCode, approver1, approver2, checker1, checker2,
				saAplikasi, accountID, accountRequestOperation,
				workItemOutcome, requestStatus);
		return singleRecordMap;
	}

	/**
	 * @param context
	 * @param attributes
	 * @param sort
	 * @return
	 * @throws GeneralException
	 */
	public static QueryOptions getActualReportFilteredQuery(
			SailPointContext context, Attributes<String, Object> attributes,
			List<Sort> sort) throws GeneralException {
		String METHOD_NAME = "::getActualReportFilteredQuery::";

		// Variable Declarations...
		QueryOptions bcaReportQueryOption = new QueryOptions();

		// Filter Parameters defined in the report definition...
		List<String> applicationIds = null;
		List<String> statusList = null;
		List<String> requestOperationList = null;

		// Adding Sorting Options...
		if (sort != null) {
			for (Sort sortItem : sort) {
				logger.debug(CLASS_NAME + METHOD_NAME + "Sort Item: "
						+ sort.toString());
				bcaReportQueryOption.addOrdering(sortItem.getField(),
						sortItem.isAscending());
			}
		}

		// 1. Adding Application Query....

		if (attributes
				.containsKey(BCACustomAuditing.ReportInputArgument.INPUT_APPLICATION)) {
			applicationIds = attributes
					.getList(BCACustomAuditing.ReportInputArgument.INPUT_APPLICATION);

			if (applicationIds != null) {
				logger.debug(CLASS_NAME
						+ METHOD_NAME
						+ "Number Of Applications Filter Passed into the Report Filter: "
						+ applicationIds.size());

				// Iterating through list of all applications selected from
				Iterator it = applicationIds.iterator();
				while (it.hasNext()) {

					String applicationID = (String) it.next();
					logger.debug(CLASS_NAME + METHOD_NAME + "Application ID: "
							+ applicationID);
					if (applicationID != null) {
						if (!applicationID.equalsIgnoreCase("")) {
							// Getting the application object...
							Application application = (Application) context
									.getObjectById(Application.class,
											applicationID);
							if (application != null) {

								Filter applicationFilter = Filter.eq("string2",
										application.getName());
								logger.debug(CLASS_NAME + METHOD_NAME
										+ "Adding Application Filter: "
										+ applicationFilter.toXml());
								bcaReportQueryOption
										.addFilter(applicationFilter);

							}
						}
					}
				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "bcaReportQueryOption: "
				+ bcaReportQueryOption.toString());

		// 2. There can not be a input status filter from data base... we need
		// to iterate over each row to identify if the request is completed or
		// not...s
		if (attributes
				.containsKey(BCACustomAuditing.ReportInputArgument.INPUT_STATUS)) {
			statusList = attributes
					.getList(BCACustomAuditing.ReportInputArgument.INPUT_STATUS);
			logger.debug(CLASS_NAME + METHOD_NAME + "statusList: " + statusList);

			Iterator it = statusList.iterator();
			while (it.hasNext()) {
				String localStatusList = (String) it.next();
				logger.debug(CLASS_NAME + METHOD_NAME + "localStatusList: "
						+ localStatusList);

				Filter filter = Filter.eq("string3", localStatusList);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "Report Status Filter: " + filter.toXml());
				// bcaReportQueryOption.addFilter(filter);

			}
		}

		// 3. Operation Filter...
		if (attributes
				.containsKey(BCACustomAuditing.ReportInputArgument.INPUT_OPERATION)) {
			requestOperationList = attributes
					.getList(BCACustomAuditing.ReportInputArgument.INPUT_OPERATION);

			logger.debug(CLASS_NAME + METHOD_NAME + "requestOperationList: "
					+ requestOperationList);

			Iterator it = requestOperationList.iterator();
			while (it.hasNext()) {
				String localRequestOperation = (String) it.next();
				Filter filter = Filter.eq("string4", localRequestOperation);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "Report Operation Filter: " + filter.toXml());
				bcaReportQueryOption.addFilter(filter);
			}

		}

		// 4. Get only those audit events whose created date is in the date
		// range.....
		if (attributes
				.containsKey(BCACustomAuditing.ReportInputArgument.REQUEST_DATE_RANGE)) {
			Map requestDateRange = (Map) attributes
					.get(BCACustomAuditing.ReportInputArgument.REQUEST_DATE_RANGE);
			logger.debug(CLASS_NAME + METHOD_NAME + "requestDateRange: "
					+ requestDateRange);

			Long startDateLong = (Long) requestDateRange.get("start");
			logger.debug(CLASS_NAME + METHOD_NAME + "startDateLong: "
					+ startDateLong);

			Long endDateLong = (Long) requestDateRange.get("end");
			logger.debug(CLASS_NAME + METHOD_NAME + "endDateLong: "
					+ endDateLong);

			DateRange dateRangeObject = new DateRange("created", startDateLong,
					endDateLong);

			Filter dateRangeFilter = dateRangeObject.getFilter();
			bcaReportQueryOption.addFilter(dateRangeFilter);

		}

		logger.debug(CLASS_NAME + METHOD_NAME + "bcaReportQueryOption: "
				+ bcaReportQueryOption.toString());
		return bcaReportQueryOption;

	}

	@Override
	public String getBaseHql() {
		String METHOD_NAME = "::getBaseHql::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		return null;
	}

	@Override
	public QueryOptions getBaseQueryOptions() {
		String METHOD_NAME = "::getBaseQueryOptions::";
		logger.debug(CLASS_NAME + METHOD_NAME + "baseQueryOptions: "
				+ baseQueryOptions.toString());
		return baseQueryOptions;

	}

	@Override
	public Object getFieldValue(String jrField) throws GeneralException {
		String METHOD_NAME = "::getFieldValue::";

		// These are execisive logs can be removed later...
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Enter getFieldValue for record " + recordCount
				+ " and field " + jrField);
		Map recordMap = reportData.get(recordCount);
		logger.debug(CLASS_NAME + METHOD_NAME + "Record Map: "
				+ recordMap.toString());
		String returnvalue = (String) recordMap.get(jrField);
		logger.debug(CLASS_NAME + METHOD_NAME + "Field Name: " + jrField
				+ "  Field Value: " + returnvalue);
		return returnvalue;

	}

	@Override
	public int getSizeEstimate() throws GeneralException {
		String METHOD_NAME = "::getSizeEstimate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		int size = -1;
		size = reportData.size();
		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Enter getSizeEstimate returning: " + size);

		return size;
	}

	@Override
	public void close() {
		String METHOD_NAME = "::close::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

	}

	@Override
	public void setMonitor(Monitor arg0) {
		String METHOD_NAME = "::setMonitor::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		String METHOD_NAME = "::getFieldValue::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String name = jrField.getName();
		try {
			return getFieldValue(name);
		} catch (GeneralException e) {
			throw new JRException(e);
		}
	}

	@Override
	public boolean next() throws JRException {
		String METHOD_NAME = "::next::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		recordCount++;
		logger.debug(CLASS_NAME + METHOD_NAME + "Enter next Record Count is: "
				+ recordCount);
		if (recordCount < reportData.size()) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Method Returning True...");
			return hasMore;
		} else {
			hasMore = false;
		}
		logger.debug(CLASS_NAME + METHOD_NAME + "Method Returning False...");

		return hasMore;
	}

	@Override
	public void setLimit(int startRow, int pageSize) {

		String METHOD_NAME = "::setLimit::";

		logger.debug(CLASS_NAME + METHOD_NAME + "StartRow: " + startRow);
		logger.debug(CLASS_NAME + METHOD_NAME + "Page Size: " + pageSize);
	}

	/**
	 * This method will get called from the report summary section.... Report
	 * Summary Section have its own data source which is not equal to the
	 * reporting data source...
	 * 
	 * @param context
	 * @return
	 * @throws GeneralException
	 */
	public static Map getReportSummaryMap(InternalContext context,
			LiveReport report, Attributes reportArgs) throws GeneralException {
		String METHOD_NAME = "::getReportSummaryMap::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Map myMap = new HashMap();

		Date currentDate = new Date();
		String retensi = "";
		String laporan = "";
		String branchCode = "";
		String frekuensi = "";

		Map attributeMap = reportArgs.getMap();
		if (attributeMap
				.containsKey(BCACustomAuditing.ReportSummaryColumn.RETENSI)) {
			if (attributeMap.get(BCACustomAuditing.ReportSummaryColumn.RETENSI) != null) {
				retensi = (String) attributeMap
						.get(BCACustomAuditing.ReportSummaryColumn.RETENSI);
				logger.debug(CLASS_NAME + METHOD_NAME + "retensi: " + retensi);
			}
		}

		if (attributeMap
				.containsKey(BCACustomAuditing.ReportSummaryColumn.LAPORAN)) {
			if (attributeMap.get(BCACustomAuditing.ReportSummaryColumn.LAPORAN) != null) {
				laporan = (String) attributeMap
						.get(BCACustomAuditing.ReportSummaryColumn.LAPORAN);
				logger.debug(CLASS_NAME + METHOD_NAME + "laporan: " + laporan);
			}
		}

		if (attributeMap
				.containsKey(BCACustomAuditing.ReportSummaryColumn.FREKUENSI)) {
			if (attributeMap
					.get(BCACustomAuditing.ReportSummaryColumn.FREKUENSI) != null) {
				frekuensi = (String) attributeMap
						.get(BCACustomAuditing.ReportSummaryColumn.FREKUENSI);
				logger.debug(CLASS_NAME + METHOD_NAME + "FREKUENSI: "
						+ frekuensi);
			}
		}

		String launcher = "";
		if (attributeMap.containsKey("launcher")) {
			if (attributeMap.get("launcher") != null) {
				launcher = (String) attributeMap.get("launcher");
				logger.debug(CLASS_NAME + METHOD_NAME + "launcher: " + launcher);
			}
		}
		SailPointContext sailpointContext = context.getContext();
		Identity launcherIdentity = sailpointContext.getObjectByName(
				Identity.class, launcher);

		if (launcherIdentity.getAttribute(IdentityAttribute.BRANCH_CODE) != null) {
			branchCode = (String) launcherIdentity
					.getAttribute(IdentityAttribute.BRANCH_CODE);
			logger.debug(CLASS_NAME + METHOD_NAME + "branchCode: " + branchCode);
		}

		myMap.put(BCACustomAuditing.ReportSummaryColumn.RETENSI, retensi);
		myMap.put(BCACustomAuditing.ReportSummaryColumn.LAPORAN, laporan);
		myMap.put(BCACustomAuditing.ReportSummaryColumn.BRANCH_CODE, branchCode);
		myMap.put(BCACustomAuditing.ReportSummaryColumn.FREKUENSI, frekuensi);
		myMap.put(BCACustomAuditing.ReportSummaryColumn.CURRENT_DATE,
				currentDate.toString());

		return myMap;
	}

	/**
	 * @param report
	 * @param attributes
	 * @param groupBy
	 * @param sort
	 * @throws GeneralException
	 */
	public static void toPrint(LiveReport report,
			Attributes<String, Object> attributes, String groupBy,
			List<Sort> sort) throws GeneralException {
		String METHOD_NAME = "::toPrint::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		ReportDataSource reportDataSource = report.getDataSource();
		logger.debug(CLASS_NAME + METHOD_NAME + "Report Data Source: "
				+ reportDataSource.toXml());
		List<Parameter> queryParametersList = reportDataSource
				.getQueryParameters();
		Iterator it1 = queryParametersList.iterator();
		while (it1.hasNext()) {
			Parameter localParameter = (Parameter) it1.next();
			String argument = localParameter.getArgument();
			String property = localParameter.getProperty();
			logger.debug(CLASS_NAME + METHOD_NAME + "argument: " + argument);
			logger.debug(CLASS_NAME + METHOD_NAME + "property: " + property);

		}
		logger.debug(CLASS_NAME + METHOD_NAME + "Report XML: " + report.toXml());
		logger.debug(CLASS_NAME + METHOD_NAME + "Group By: " + groupBy);
		logger.debug(CLASS_NAME + METHOD_NAME + "Sort List: " + sort);
		Map attributeMapToPrint = attributes.getMap();
		logger.debug(CLASS_NAME + METHOD_NAME + "Attribute Map: "
				+ attributeMapToPrint);

		if (null != attributes) {
			List<String> keys = attributes.getKeys();
			Map attributeMap = attributes.getMap();

			for (String key : keys) {
				logger.debug(CLASS_NAME + METHOD_NAME + "Attributes Key: "
						+ key);
				logger.debug(CLASS_NAME + METHOD_NAME + "Value: "
						+ attributeMap.get(key));

			}
		}
		// Printing the sorting order....
		if (null != sort) {
			Iterator it = sort.iterator();
			while (it.hasNext()) {
				Sort localSort = (Sort) it.next();
				logger.debug(CLASS_NAME + METHOD_NAME + "Local Sort List: "
						+ localSort.toXml());
			}
		}
	}

	/**
	 * @param epochString
	 * @return
	 */
	public static Date getDateFromEpoch(Long epoch) {
		String METHOD_NAME = "::getDateFromEpoch::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		// long epoch = Long.parseLong(epochString);

		Date date = new Date(epoch * 1000);
		logger.debug(CLASS_NAME + METHOD_NAME + "date: " + date);
		return date;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public static List getBranchCodeFilter(SailPointContext context,
			String branchCodeMappingCustomObjectName,
			String sessionOwnerBranchCode) {

		String METHOD_NAME = "::getBranchCodeFilter::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		List branchCodeFilterList = new ArrayList();

		// TODO remove the below lines and call the custom object to get the
		// branch code filter...
		// branchCodeFilterList.add("0385");
		// branchCodeFilterList.add("0960");
		branchCodeFilterList.add("0001");
		branchCodeFilterList.add("0002");
		branchCodeFilterList.add("0003");
		branchCodeFilterList.add("0004");
		branchCodeFilterList.add("0005");
		branchCodeFilterList.add("0006");
		branchCodeFilterList.add("0007");
		branchCodeFilterList.add("0008");
		branchCodeFilterList.add("0009");
		branchCodeFilterList.add("0010");
		branchCodeFilterList.add("0011");
		branchCodeFilterList.add("0012");
		branchCodeFilterList.add("0014");
		branchCodeFilterList.add("0018");
		branchCodeFilterList.add("0019");
		branchCodeFilterList.add("0021");
		branchCodeFilterList.add("0022");
		branchCodeFilterList.add("0024");
		branchCodeFilterList.add("0025");
		branchCodeFilterList.add("0026");
		branchCodeFilterList.add("0027");
		branchCodeFilterList.add("0028");
		branchCodeFilterList.add("0029");
		branchCodeFilterList.add("0033");
		branchCodeFilterList.add("0034");
		branchCodeFilterList.add("0035");
		branchCodeFilterList.add("0036");
		branchCodeFilterList.add("0037");
		branchCodeFilterList.add("0038");
		branchCodeFilterList.add("0040");
		branchCodeFilterList.add("0041");
		branchCodeFilterList.add("0044");
		branchCodeFilterList.add("0047");
		branchCodeFilterList.add("0048");
		branchCodeFilterList.add("0050");
		branchCodeFilterList.add("0051");
		branchCodeFilterList.add("0054");
		branchCodeFilterList.add("0055");
		branchCodeFilterList.add("0061");
		branchCodeFilterList.add("0064");
		branchCodeFilterList.add("0065");
		branchCodeFilterList.add("0066");
		branchCodeFilterList.add("0068");
		branchCodeFilterList.add("0069");
		branchCodeFilterList.add("0070");
		branchCodeFilterList.add("0071");
		branchCodeFilterList.add("0073");
		branchCodeFilterList.add("0075");
		branchCodeFilterList.add("0076");
		branchCodeFilterList.add("0082");
		branchCodeFilterList.add("0083");
		branchCodeFilterList.add("0084");
		branchCodeFilterList.add("0085");
		branchCodeFilterList.add("0087");
		branchCodeFilterList.add("0088");
		branchCodeFilterList.add("0090");
		branchCodeFilterList.add("0092");
		branchCodeFilterList.add("0093");
		branchCodeFilterList.add("0094");
		branchCodeFilterList.add("0095");
		branchCodeFilterList.add("0096");
		branchCodeFilterList.add("0101");
		branchCodeFilterList.add("0102");
		branchCodeFilterList.add("0103");
		branchCodeFilterList.add("0105");
		branchCodeFilterList.add("0107");
		branchCodeFilterList.add("0108");
		branchCodeFilterList.add("0110");
		branchCodeFilterList.add("0111");
		branchCodeFilterList.add("0118");
		branchCodeFilterList.add("0119");
		branchCodeFilterList.add("0120");
		branchCodeFilterList.add("0121");
		branchCodeFilterList.add("0127");
		branchCodeFilterList.add("0128");
		branchCodeFilterList.add("0130");
		branchCodeFilterList.add("0134");
		branchCodeFilterList.add("0146");
		branchCodeFilterList.add("0148");
		branchCodeFilterList.add("0152");
		branchCodeFilterList.add("0158");
		branchCodeFilterList.add("0159");
		branchCodeFilterList.add("0164");
		branchCodeFilterList.add("0166");
		branchCodeFilterList.add("0167");
		branchCodeFilterList.add("0168");
		branchCodeFilterList.add("0175");
		branchCodeFilterList.add("0177");
		branchCodeFilterList.add("0183");
		branchCodeFilterList.add("0185");
		branchCodeFilterList.add("0188");
		branchCodeFilterList.add("0190");
		branchCodeFilterList.add("0191");
		branchCodeFilterList.add("0192");
		branchCodeFilterList.add("0194");
		branchCodeFilterList.add("0195");
		branchCodeFilterList.add("0198");
		branchCodeFilterList.add("0199");
		branchCodeFilterList.add("0200");
		branchCodeFilterList.add("0203");
		branchCodeFilterList.add("0205");
		branchCodeFilterList.add("0206");
		branchCodeFilterList.add("0209");
		branchCodeFilterList.add("0211");
		branchCodeFilterList.add("0212");
		branchCodeFilterList.add("0214");
		branchCodeFilterList.add("0215");
		branchCodeFilterList.add("0216");
		branchCodeFilterList.add("0217");
		branchCodeFilterList.add("0218");
		branchCodeFilterList.add("0219");
		branchCodeFilterList.add("0223");
		branchCodeFilterList.add("0224");
		branchCodeFilterList.add("0228");
		branchCodeFilterList.add("0230");
		branchCodeFilterList.add("0231");
		branchCodeFilterList.add("0232");
		branchCodeFilterList.add("0237");
		branchCodeFilterList.add("0242");
		branchCodeFilterList.add("0244");
		branchCodeFilterList.add("0245");
		branchCodeFilterList.add("0248");
		branchCodeFilterList.add("0253");
		branchCodeFilterList.add("0256");
		branchCodeFilterList.add("0258");
		branchCodeFilterList.add("0260");
		branchCodeFilterList.add("0261");
		branchCodeFilterList.add("0262");
		branchCodeFilterList.add("0267");
		branchCodeFilterList.add("0270");
		branchCodeFilterList.add("0271");
		branchCodeFilterList.add("0272");
		branchCodeFilterList.add("0273");
		branchCodeFilterList.add("0276");
		branchCodeFilterList.add("0277");
		branchCodeFilterList.add("0279");
		branchCodeFilterList.add("0285");
		branchCodeFilterList.add("0286");
		branchCodeFilterList.add("0287");
		branchCodeFilterList.add("0288");
		branchCodeFilterList.add("0291");
		branchCodeFilterList.add("0301");
		branchCodeFilterList.add("0303");
		branchCodeFilterList.add("0308");
		branchCodeFilterList.add("0309");
		branchCodeFilterList.add("0310");
		branchCodeFilterList.add("0319");
		branchCodeFilterList.add("0321");
		branchCodeFilterList.add("0324");
		branchCodeFilterList.add("0335");
		branchCodeFilterList.add("0340");
		branchCodeFilterList.add("0341");
		branchCodeFilterList.add("0342");
		branchCodeFilterList.add("0343");
		branchCodeFilterList.add("0345");
		branchCodeFilterList.add("0349");
		branchCodeFilterList.add("0363");
		branchCodeFilterList.add("0365");
		branchCodeFilterList.add("0366");
		branchCodeFilterList.add("0368");
		branchCodeFilterList.add("0369");
		branchCodeFilterList.add("0372");
		branchCodeFilterList.add("0375");
		branchCodeFilterList.add("0379");
		branchCodeFilterList.add("0380");
		branchCodeFilterList.add("0383");
		branchCodeFilterList.add("0384");
		branchCodeFilterList.add("0385");
		branchCodeFilterList.add("0386");
		branchCodeFilterList.add("0388");
		branchCodeFilterList.add("0389");
		branchCodeFilterList.add("0391");
		branchCodeFilterList.add("0397");
		branchCodeFilterList.add("0399");
		branchCodeFilterList.add("0401");
		branchCodeFilterList.add("0405");
		branchCodeFilterList.add("0408");
		branchCodeFilterList.add("0412");
		branchCodeFilterList.add("0413");
		branchCodeFilterList.add("0414");
		branchCodeFilterList.add("0418");
		branchCodeFilterList.add("0419");
		branchCodeFilterList.add("0427");
		branchCodeFilterList.add("0428");
		branchCodeFilterList.add("0429");
		branchCodeFilterList.add("0436");
		branchCodeFilterList.add("0441");
		branchCodeFilterList.add("0448");
		branchCodeFilterList.add("0450");
		branchCodeFilterList.add("0454");
		branchCodeFilterList.add("0455");
		branchCodeFilterList.add("0458");
		branchCodeFilterList.add("0459");
		branchCodeFilterList.add("0464");
		branchCodeFilterList.add("0465");
		branchCodeFilterList.add("0467");
		branchCodeFilterList.add("0468");
		branchCodeFilterList.add("0474");
		branchCodeFilterList.add("0478");
		branchCodeFilterList.add("0481");
		branchCodeFilterList.add("0485");
		branchCodeFilterList.add("0487");
		branchCodeFilterList.add("0489");
		branchCodeFilterList.add("0494");
		branchCodeFilterList.add("0497");
		branchCodeFilterList.add("0958");
		branchCodeFilterList.add("0960");
		branchCodeFilterList.add("0969");
		branchCodeFilterList.add("0970");
		branchCodeFilterList.add("0971");
		branchCodeFilterList.add("0972");
		branchCodeFilterList.add("0973");
		branchCodeFilterList.add("0974");
		branchCodeFilterList.add("0975");
		branchCodeFilterList.add("0977");
		branchCodeFilterList.add("0978");
		branchCodeFilterList.add("0979");
		branchCodeFilterList.add("0980");
		branchCodeFilterList.add("0981");
		branchCodeFilterList.add("0982");
		branchCodeFilterList.add("0998");
		branchCodeFilterList.add("5000");
		branchCodeFilterList.add("5005");
		branchCodeFilterList.add("5010");
		branchCodeFilterList.add("5025");
		branchCodeFilterList.add("5050");
		branchCodeFilterList.add("5065");
		branchCodeFilterList.add("5075");
		branchCodeFilterList.add("5110");
		branchCodeFilterList.add("5120");
		branchCodeFilterList.add("5125");
		branchCodeFilterList.add("5190");
		branchCodeFilterList.add("5200");
		branchCodeFilterList.add("5240");
		branchCodeFilterList.add("5245");
		branchCodeFilterList.add("5315");
		branchCodeFilterList.add("5325");
		branchCodeFilterList.add("5335");
		branchCodeFilterList.add("5375");
		branchCodeFilterList.add("5385");
		branchCodeFilterList.add("5410");
		branchCodeFilterList.add("5415");
		branchCodeFilterList.add("5450");
		branchCodeFilterList.add("5530");
		branchCodeFilterList.add("5540");
		branchCodeFilterList.add("5550");
		branchCodeFilterList.add("5600");
		branchCodeFilterList.add("5660");
		branchCodeFilterList.add("5680");
		branchCodeFilterList.add("5720");
		branchCodeFilterList.add("5725");
		branchCodeFilterList.add("5735");
		branchCodeFilterList.add("5740");
		branchCodeFilterList.add("5745");
		branchCodeFilterList.add("5750");
		branchCodeFilterList.add("5765");
		branchCodeFilterList.add("5770");
		branchCodeFilterList.add("5775");
		branchCodeFilterList.add("5810");
		branchCodeFilterList.add("5850");
		branchCodeFilterList.add("5860");
		branchCodeFilterList.add("5865");
		branchCodeFilterList.add("5870");
		branchCodeFilterList.add("5875");
		branchCodeFilterList.add("5890");
		branchCodeFilterList.add("5910");
		branchCodeFilterList.add("5930");
		branchCodeFilterList.add("5940");
		branchCodeFilterList.add("6000");
		branchCodeFilterList.add("6030");
		branchCodeFilterList.add("6040");
		branchCodeFilterList.add("6050");
		branchCodeFilterList.add("6070");
		branchCodeFilterList.add("6080");
		branchCodeFilterList.add("6105");
		branchCodeFilterList.add("6120");
		branchCodeFilterList.add("6140");
		branchCodeFilterList.add("6150");
		branchCodeFilterList.add("6155");
		branchCodeFilterList.add("6220");
		branchCodeFilterList.add("6240");
		branchCodeFilterList.add("6265");
		branchCodeFilterList.add("6270");
		branchCodeFilterList.add("6280");
		branchCodeFilterList.add("6300");
		branchCodeFilterList.add("6330");
		branchCodeFilterList.add("6350");
		branchCodeFilterList.add("6380");
		branchCodeFilterList.add("6430");
		branchCodeFilterList.add("6450");
		branchCodeFilterList.add("6465");
		branchCodeFilterList.add("6540");
		branchCodeFilterList.add("6560");
		branchCodeFilterList.add("6565");
		branchCodeFilterList.add("6580");
		branchCodeFilterList.add("6590");
		branchCodeFilterList.add("6610");
		branchCodeFilterList.add("6670");
		branchCodeFilterList.add("6690");
		branchCodeFilterList.add("6720");
		branchCodeFilterList.add("6750");
		branchCodeFilterList.add("6790");
		branchCodeFilterList.add("6820");
		branchCodeFilterList.add("6830");
		branchCodeFilterList.add("6840");
		branchCodeFilterList.add("6850");
		branchCodeFilterList.add("6860");
		branchCodeFilterList.add("6870");
		branchCodeFilterList.add("6890");
		branchCodeFilterList.add("6920");
		branchCodeFilterList.add("6930");
		branchCodeFilterList.add("7010");
		branchCodeFilterList.add("7025");
		branchCodeFilterList.add("7040");
		branchCodeFilterList.add("7050");
		branchCodeFilterList.add("7060");
		branchCodeFilterList.add("7080");
		branchCodeFilterList.add("7160");
		branchCodeFilterList.add("7180");
		branchCodeFilterList.add("7210");
		branchCodeFilterList.add("7240");
		branchCodeFilterList.add("7330");
		branchCodeFilterList.add("7340");
		branchCodeFilterList.add("7370");
		branchCodeFilterList.add("7390");
		branchCodeFilterList.add("7420");
		branchCodeFilterList.add("7460");
		branchCodeFilterList.add("7480");
		branchCodeFilterList.add("7510");
		branchCodeFilterList.add("7550");
		branchCodeFilterList.add("7560");
		branchCodeFilterList.add("7600");
		branchCodeFilterList.add("7610");
		branchCodeFilterList.add("7640");
		branchCodeFilterList.add("7650");
		branchCodeFilterList.add("7660");
		branchCodeFilterList.add("7700");
		branchCodeFilterList.add("7710");
		branchCodeFilterList.add("7780");
		branchCodeFilterList.add("7790");
		branchCodeFilterList.add("7805");
		branchCodeFilterList.add("7815");
		branchCodeFilterList.add("7825");
		branchCodeFilterList.add("7830");
		branchCodeFilterList.add("7870");
		branchCodeFilterList.add("7890");
		branchCodeFilterList.add("7900");
		branchCodeFilterList.add("7910");
		branchCodeFilterList.add("7955");
		branchCodeFilterList.add("8050");
		branchCodeFilterList.add("8170");
		branchCodeFilterList.add("8195");
		branchCodeFilterList.add("8210");
		branchCodeFilterList.add("8220");
		branchCodeFilterList.add("8265");
		branchCodeFilterList.add("8275");
		branchCodeFilterList.add("8280");
		branchCodeFilterList.add("8290");
		branchCodeFilterList.add("8370");
		branchCodeFilterList.add("8390");
		branchCodeFilterList.add("8400");
		branchCodeFilterList.add("8460");
		branchCodeFilterList.add("8600");
		branchCodeFilterList.add("8620");
		branchCodeFilterList.add("8640");
		branchCodeFilterList.add("8650");
		branchCodeFilterList.add("8680");
		branchCodeFilterList.add("8690");
		branchCodeFilterList.add("8700");
		branchCodeFilterList.add("8710");
		branchCodeFilterList.add("8720");
		branchCodeFilterList.add("8725");
		branchCodeFilterList.add("8730");
		branchCodeFilterList.add("8770");
		branchCodeFilterList.add("8910");
		branchCodeFilterList.add("8945");

		return branchCodeFilterList;
	}
}
