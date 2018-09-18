package sailpoint.workflow.rule;

import static sailpoint.service.IdentityResetService.Consts.Flows.UNLOCK_ACCOUNT_FLOW;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sailpoint.api.SailPointContext;
import sailpoint.api.Workflower;
import sailpoint.object.ApprovalItem;
import sailpoint.object.ApprovalSet;
import sailpoint.object.Attributes;
import sailpoint.object.AuditEvent;
import sailpoint.object.Bundle;
import sailpoint.object.Comment;
import sailpoint.object.Configuration;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.TaskResult;
import sailpoint.object.WorkflowSummary;
import sailpoint.object.Identity;
import sailpoint.object.IdentityRequest;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningPlan.Operation;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.Source;
import sailpoint.object.WorkItem;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowSummary.ApprovalSummary;
import sailpoint.object.WorkflowCase;
import sailpoint.server.Auditor;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;
import sailpoint.web.lcm.AccessRequestBean;
import sailpoint.web.lcm.PasswordsRequestBean;
import sailpoint.workflow.WorkflowContext;

import org.apache.log4j.Logger;

/**
 * @author joydeep.mondal
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BCAProcessPlanApprovalDecision {

	public static String CLASS_NAME = "::BCAProcessPlanApprovalDecision::";
	public static Logger logger = Logger
			.getLogger("sailpoint.workflow.rule.BCAProcessPlanApprovalDecision");

	public static final String ARG_DONT_UPDATE_PLAN = "dontUpdatePlan";
	public static final String ARG_DISABLE_AUDIT = "disableAudit";
	public static final String ARG_APPROVAL_SET = "approvalSet";
	public static final String VAR_FLOW = "flow";
	public static final String VAR_NULL_DECISION_APPROVED = "nullDecisionApproved";
	public static final String VAR_IDENTITY_NAME = "identityName";
	public static final String VAR_APPROVAL_SCHEME = "approvalScheme";
	public static final String VAR_PROJECT = "project";
	public static final String ARG_IDENTITY_DISABLE_IDENTITY_REQUESTS = "disableIdentityRequests";
	public static final String ARG_IDENTITY_REQUEST_ID = "identityRequestId";
	public static final String ARG_STATE = "state";
	public static final String VAR_IDENTITY_REQUEST_ID = ARG_IDENTITY_REQUEST_ID;

	/**
	 * @param wfc
	 * @return
	 * @throws GeneralException
	 */
	public static Object processPlanApprovalDecisions(WorkflowContext wfc)
			throws GeneralException {

		Attributes<String, Object> args = wfc.getArguments();
		ProvisioningPlan plan = (ProvisioningPlan) args.get("plan");
		if (plan == null)
			throw new GeneralException("Provisioning plan required.");

		processDecisions(wfc, plan);
		return plan;
	}

	/**
	 * @param wfc
	 * @param plan
	 * @return
	 * @throws GeneralException
	 */
	public static ProvisioningPlan processDecisions(WorkflowContext wfc,
			ProvisioningPlan plan) throws GeneralException {

		Attributes<String, Object> args = wfc.getArguments();
		boolean dontModifyPlan = args.getBoolean(ARG_DONT_UPDATE_PLAN);
		boolean disableAudit = args.getBoolean(ARG_DISABLE_AUDIT);

		ApprovalSet set = (ApprovalSet) args.get(ARG_APPROVAL_SET);
		if (set == null)
			throw new GeneralException("ApprovalSet arg required.");

		if (set != null) {
			List<ApprovalItem> items = set.getItems();
			if (Util.size(items) > 0) {
				for (ApprovalItem item : items) {
					if (item == null)
						continue;
					if (!dontModifyPlan) {
						boolean approved = isApproved(wfc, item);
						if (!approved) {
							String flowName = args.getString(VAR_FLOW);
							if (AccessRequestBean.FLOW_CONFIG_NAME
									.equals(flowName)) {
								checkAssignedRejection(item, set, plan, wfc);
							}
							removeFromPlan(plan, item);
						} else {
							updatePlan(plan, item);
						}
					}
					if (!disableAudit)
						auditDecision(wfc, item);
				}
			}

			assimilateWorkItemApprovalSetToIdentityRequest(wfc, set);
		}
		return plan;
	}

	/**
	 * @param wfc
	 * @param set
	 * @return
	 * @throws GeneralException
	 */
	public static Object assimilateWorkItemApprovalSetToIdentityRequest(
			WorkflowContext wfc, ApprovalSet set) throws GeneralException {

		if (disableRequests(wfc))
			return null;
		if (set == null)
			return null;
		SailPointContext ctx = wfc.getSailPointContext();

		IdentityRequest ir = null;
		List<ApprovalItem> approvalItems = set.getItems();
		if (Util.size(approvalItems) > 0) {
			ir = getIdentityRequest(wfc);
			if (ir != null) {
				updateTaskResultArtifacts(wfc, ir);
				refreshApprovalStates(ir, set);
				ir.setState(getState(wfc));
				ctx.saveObject(ir);
				ctx.commitTransaction();
			}
		}
		return ir;
	}

	public static IdentityRequest getIdentityRequest(WorkflowContext wfc)
			throws GeneralException {

		Attributes<String, Object> args = wfc.getArguments();

		String irId = Util.getString(args, ARG_IDENTITY_REQUEST_ID);
		if (irId == null) {
			WorkflowContext top = wfc.getRootContext();
			irId = (String) top.getVariable(VAR_IDENTITY_REQUEST_ID);
		}
		IdentityRequest ir = null;
		if (irId != null) {
			ir = wfc.getSailPointContext().getObject(IdentityRequest.class,
					irId);
		}
		return ir;
	}

	/**
	 * @param wfc
	 * @return
	 * @throws GeneralException
	 */
	public static String getState(WorkflowContext wfc) throws GeneralException {

		Attributes<String, Object> args = wfc.getArguments();
		String state = Util.getString(args, ARG_STATE);
		if (state == null) {
			WorkflowContext top = wfc.getRootContext();
			state = top.getStep().getName();
		}
		return state;
	}

	/**
	 * @param ir
	 * @param set
	 */
	public static void refreshApprovalStates(IdentityRequest ir, ApprovalSet set) {

		if (set != null) {
			List<ApprovalItem> approvalItems = set.getItems();
			if (approvalItems != null) {
				for (ApprovalItem approvalItem : approvalItems) {
					List<IdentityRequestItem> items = ir
							.findItems(approvalItem);
					if (items != null) {
						WorkItem.State state = approvalItem.getState();
						String ownerName = approvalItem.getOwner();
						for (IdentityRequestItem item : items) {
							if (item == null)
								continue;
							if (ir.isTerminated()) {
								if (state == null) {
									item.setApprovalState(WorkItem.State.Canceled);
								}
							} else {
								item.setApprovalState(state);
							}
							item.setOwnerName(ownerName);
							item.setApproverName(approvalItem.getApprover());
						}
					}
				}
			}
		}
	}

	/**
	 * @param wfc
	 * @param ir
	 * @throws GeneralException
	 */
	public static void updateTaskResultArtifacts(WorkflowContext wfc,
			IdentityRequest ir) throws GeneralException {
		TaskResult result = null;
		Workflower workflower = wfc.getRootContext().getWorkflower();
		if (workflower != null) {
			result = wfc.getRootContext().getTaskResult();
			if (result != null)
				workflower.updateTaskResult(wfc, result);
		}
		if (ir.getTaskResultId() == null) {
			String id = (result != null) ? result.getId() : null;
			ir.setTaskResultId(id);
		}
		refreshWorkflowSummaries(ir, result);
	}

	/**
	 * @param ir
	 * @param result
	 * @throws GeneralException
	 */
	public static void refreshWorkflowSummaries(IdentityRequest ir,
			TaskResult result) throws GeneralException {

		if (result != null) {
			WorkflowSummary summary = (WorkflowSummary) result
					.getAttribute(WorkflowCase.RES_WORKFLOW_SUMMARY);
			if (summary != null) {
				List<ApprovalSummary> approvalSummaries = summary
						.getInteractions();
				ir.setApprovalSummaries(approvalSummaries);

				if (ir.isTerminated()) {
					if (approvalSummaries != null) {
						for (ApprovalSummary sum : approvalSummaries) {
							WorkItem.State current = sum.getState();
							if (current == null)
								sum.setState(WorkItem.State.Canceled);
						}
					}
				}
				ApprovalSet set = summary.getApprovalSet();
				if (set != null)
					refreshApprovalStates(ir, set);
			}
		}
	}

	/**
	 * @param wfc
	 * @return
	 * @throws GeneralException
	 */
	public static IdentityRequest getIdentistyRequest(WorkflowContext wfc)
			throws GeneralException {

		Attributes<String, Object> args = wfc.getArguments();

		String irId = Util.getString(args, ARG_IDENTITY_REQUEST_ID);
		if (irId == null) {
			WorkflowContext top = wfc.getRootContext();
			irId = (String) top.getVariable(VAR_IDENTITY_REQUEST_ID);
		}
		IdentityRequest ir = null;
		if (irId != null) {
			ir = wfc.getSailPointContext().getObject(IdentityRequest.class,
					irId);
		}
		return ir;
	}

	/**
	 * @param wfc
	 * @return
	 */
	public static boolean disableRequests(WorkflowContext wfc) {
		boolean disabled = false;
		if ((wfc.getRootContext()
				.getBoolean(ARG_IDENTITY_DISABLE_IDENTITY_REQUESTS))
				|| (Util.getBoolean(wfc.getStepArguments(),
						ARG_IDENTITY_DISABLE_IDENTITY_REQUESTS))) {
			disabled = true;
		}
		return disabled;
	}

	public static boolean isApproved(WorkflowContext wfc, ApprovalItem item)
			throws GeneralException {

		if (item == null)
			return false;

		WorkItem.State itemState = item.getState();

		if (Util.nullSafeEq(WorkItem.State.Finished, itemState)) {
			return true;
		} else if (Util.nullSafeEq(WorkItem.State.Rejected, itemState)) {
			return false;
		}

		if (itemState == null) {
			Attributes<String, Object> args = wfc.getArguments();
			String flowName = args.getString(VAR_FLOW);
			if (Util.nullSafeEq(PasswordsRequestBean.PASSWORD_REQUEST_FLOW,
					flowName)
					|| Util.nullSafeEq(
							PasswordsRequestBean.EXPIRED_PASSWORD_FLOW,
							flowName)
					|| Util.nullSafeEq(UNLOCK_ACCOUNT_FLOW.value(), flowName)
					|| Util.nullSafeEq(
							PasswordsRequestBean.FORGOT_PASSWORD_FLOW, flowName)) {
				return true;
			}

			String approvalScheme = getApprovalScheme(wfc);
			if (Util.nullSafeEq("none", approvalScheme)) {
				return true;
			}

			String nullDecisionApproved = null;
			Configuration config = wfc.getSailPointContext().getConfiguration();
			if (config != null) {
				nullDecisionApproved = config
						.getString(VAR_NULL_DECISION_APPROVED);
			}
			if (Util.otob(nullDecisionApproved)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param rejectedAssignmentApproval
	 * @param set
	 * @param plan
	 * @param wfc
	 */
	public static void checkAssignedRejection(
			ApprovalItem rejectedAssignmentApproval, ApprovalSet set,
			ProvisioningPlan plan, WorkflowContext wfc) {
		if (rejectedAssignmentApproval.getName().equals(
				ProvisioningPlan.ATT_IIQ_ASSIGNED_ROLES)) {
			Object name = rejectedAssignmentApproval.getValue();
			String approvalRoleName = null;
			if (name instanceof String) {
				approvalRoleName = (String) name;
			} else if (name instanceof List) {
				approvalRoleName = Util.listToCsv((List) name);
			}

			SailPointContext context = wfc.getSailPointContext();
			try {
				if (approvalRoleName != null) {
					Bundle approvalRole = context.getObject(Bundle.class,
							approvalRoleName);
					if (approvalRole != null
							&& approvalRole.getRequirements() != null) {
						String rejecter = rejectedAssignmentApproval.getOwner();
						Set<String> permitNames = getPermitNames(approvalRole);

						for (ApprovalItem potentialSubApproval : set.getItems()) {
							Object potentialSubApprovalValue = potentialSubApproval
									.getValue();
							String subApprovalRoleName = null;
							if (potentialSubApprovalValue instanceof String) {
								subApprovalRoleName = (String) potentialSubApprovalValue;
							} else if (potentialSubApprovalValue instanceof List) {
								subApprovalRoleName = Util
										.listToCsv((List) potentialSubApprovalValue);
							}
							if (permitNames.contains(subApprovalRoleName)) {
								if (WorkItem.State.Rejected != potentialSubApproval
										.getState()) {
									String comment = "";
									potentialSubApproval
											.setState(WorkItem.State.Rejected);
									potentialSubApproval.add(new Comment(
											comment, rejecter));
									// potentialSubApproval.addRejecter(rejecter);
								}

								removeFromPlan(plan, potentialSubApproval);
							}
						}
					}
				}
			} catch (GeneralException ge) {
				logger.warn("Unable to fetch role with name: "
						+ approvalRoleName + ". Exception: " + ge.getMessage());
			}
		}
	}

	/**
	 * @param plan
	 * @param item
	 * @throws GeneralException
	 */
	public static void removeFromPlan(ProvisioningPlan plan, ApprovalItem item)
			throws GeneralException {

		String attrName = item.getName();
		List<String> itemValues = item.getValueList();

		List<AccountRequest> planRequests = plan.getAccountRequests();
		if (Util.size(planRequests) > 0) {

			List<AccountRequest> acctRequestCopy = new ArrayList<AccountRequest>(
					planRequests);
			for (AccountRequest planAcct : acctRequestCopy) {
				if (!matchesAccountRequest(planAcct, item)) {
					continue;
				}
				if ((attrName == null) && (Util.size(itemValues) == 0)) {
					plan.remove(planAcct);
					break;
				}

				List<AttributeRequest> requests = planAcct
						.getAttributeRequests();
				if (Util.size(requests) > 0) {
					List<AttributeRequest> attrReqsCopy = new ArrayList<AttributeRequest>();
					for (AttributeRequest attrReq : requests) {
						if (attrReq == null) {
							continue;
						}
						Operation op = attrReq.getOperation();
						String attrReqOp = (op != null) ? op.toString() : null;

						if ((Util.nullSafeEq(attrName, attrReq.getName()))
								&& (Util.nullSafeEq(item.getOperation(),
										attrReqOp))
								&& (Util.nullSafeEq(item.getAssignmentId(),
										attrReq.getAssignmentId(), true))) {

							List vals = Util.asList(attrReq.getValue());
							if (Util.size(vals) > 0) {
								for (String toRemove : itemValues) {
									vals.remove(toRemove);
								}
								if (Util.size(vals) > 0) {
									attrReq.setValue(vals);
									attrReqsCopy.add(attrReq);
								}
							}
						} else {
							attrReqsCopy.add(attrReq);
						}
					}

					if (Util.size(attrReqsCopy) > 0) {
						planAcct.setAttributeRequests(attrReqsCopy);
					} else {
						plan.remove(planAcct);
					}
				}
			}
		}
	}

	/**
	 * @param plan
	 * @param item
	 * @throws GeneralException
	 */
	public static void updatePlan(ProvisioningPlan plan, ApprovalItem item)
			throws GeneralException {

		List<AccountRequest> planRequests = plan.getAccountRequests();
		if (Util.size(planRequests) > 0) {
			for (AccountRequest planAcct : planRequests) {
				if (!matchesAccountRequest(planAcct, item))
					continue;

				List<AttributeRequest> attrReqs = planAcct
						.getAttributeRequests();
				if (Util.size(attrReqs) > 0) {
					for (AttributeRequest attrReq : attrReqs) {
						if (matchesAttributeRequest(attrReq, item)) {
							attrReq.setAddDate(item.getStartDate());
							attrReq.setRemoveDate(item.getEndDate());
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * @param accountRequest
	 * @param item
	 * @return
	 * @throws GeneralException
	 */
	public static boolean matchesAccountRequest(AccountRequest accountRequest,
			ApprovalItem item) throws GeneralException {

		if ((accountRequest == null) || (item == null))
			return false;

		if (Util.nullSafeEq(item.getApplication(),
				accountRequest.getApplication())) {
			if (!ProvisioningPlan.APP_IIQ.equals(accountRequest
					.getApplication())) {
				String accountId = item.getNativeIdentity();
				String instance = item.getInstance();
				if ((Util.nullSafeEq(accountId,
						accountRequest.getNativeIdentity(), true))
						&& (Util.nullSafeEq(instance,
								accountRequest.getInstance(), true))) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param attrReq
	 * @param item
	 * @return
	 * @throws GeneralException
	 */
	public static boolean matchesAttributeRequest(AttributeRequest attrReq,
			ApprovalItem item) throws GeneralException {

		if ((attrReq == null) || (item == null))
			return false;

		String itemAttr = item.getName();
		String attrName = attrReq.getName();
		Operation operation = attrReq.getOperation();
		String op = (operation != null) ? operation.toString() : null;

		if ((Util.nullSafeEq(itemAttr, attrName))
				&& (Util.nullSafeEq(op, item.getOperation()))
				&& (Util.nullSafeEq(item.getAssignmentId(),
						attrReq.getAssignmentId(), true))) {
			List attrReqVal = Util.asList(attrReq.getValue());
			List itemVal = Util.asList(item.getValue());
			if (Util.orderInsensitiveEquals(attrReqVal, itemVal)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param ctx
	 * @param item
	 * @throws GeneralException
	 */
	public static void auditDecision(WorkflowContext ctx, ApprovalItem item)
			throws GeneralException {

		if (item == null)
			return;

		AuditEvent event = buildBaseEvent(ctx, item);
		event.setAttribute("operation", item.getOperation());
		event.setAttribute("requester", ctx.getString("launcher"));

		List<Comment> comments = item.getComments();
		if (Util.size(comments) > 0) {
			event.setAttribute("completionComments", comments);
		}

		String requesterComments = item.getRequesterComments();
		if (Util.getString(requesterComments) != null)
			event.setAttribute("requesterComments", requesterComments);

		SailPointContext spcon = ctx.getSailPointContext();
		spcon.decache();

		String actor = spcon.getUserName();
		Identity ident = spcon.getObject(Identity.class, actor);
		if (ident != null)
			actor = ident.getDisplayName();
		else {
			actor = item.getOwner();
		}

		if (actor != null)
			event.setSource(actor);

		if (item.isApproved())
			event.setAction(AuditEvent.ActionApproveLineItem);
		else
			event.setAction(AuditEvent.ActionRejectLineItem);

		if (Auditor.isEnabled(event.getAction())) {
			Auditor.log(event);
			spcon.commitTransaction();
		}
	}

	/**
	 * @param wfc
	 * @param item
	 * @return
	 */
	public static AuditEvent buildBaseEvent(WorkflowContext wfc,
			ApprovalItem item) {

		Attributes<String, Object> args = wfc.getArguments();
		if (args == null)
			args = new Attributes<String, Object>();
		AuditEvent event = new AuditEvent();

		event.setSource(args.getString(Workflow.VAR_LAUNCHER));
		event.setTarget(args.getString(VAR_IDENTITY_NAME));

		event.setApplication(item.getApplication());

		if (item.getNativeIdentity() == null) {
			ProvisioningProject project = (ProvisioningProject) Util.get(args,
					VAR_PROJECT);
			if (project != null) {
				event.setAccountName(project.getIdentity());
			}
		} else {
			event.setAccountName(item.getNativeIdentity());
		}

		event.setInstance(item.getInstance());
		event.setAction(item.getOperation());
		event.setAttributeName(item.getName());
		event.setAttributeValue(item.getCsv());

		event.setAttributes(item.getAttributes());
		if (event.getAttributes() != null) {
			for (String key : event.getAttributes().keySet()) {
				if (key.startsWith("password:")) {
					event.getAttributes().put("password:********",
							event.getAttribute(key));
					event.getAttributes().remove(key);
					break;
				}
			}
		}

		event.setTrackingId(wfc.getWorkflow().getProcessLogId());
		String interfaceName = wfc.getString("interface");
		if (interfaceName == null)
			interfaceName = Source.LCM.toString();

		event.setAttribute("interface", interfaceName);
		event.setInterface(interfaceName);

		if (isPasswordItem(item)) {
			String requesterComment = item.getRequesterComments();
			if (requesterComment != null) {
				List<Comment> comments = (List<Comment>) event
						.getAttribute("completionComments");
				if (comments == null) {
					comments = new ArrayList<Comment>();
					event.setAttribute("completionComments", comments);
				}

				Comment comment = new Comment();
				comment.setComment(requesterComment);
				comment.setAuthor(item.getOwner());
				comment.setDate(new Date());

				comments.add(comment);
			}
		}

		String taskResultId = wfc.getString(Workflow.VAR_TASK_RESULT);
		if (taskResultId != null) {
			event.setAttribute(Workflow.VAR_TASK_RESULT, taskResultId);
		}

		String flow = args.getString("flow");
		if (flow != null)
			event.setAttribute("flow", flow);
		return event;
	}

	/**
	 * @param wfc
	 * @return
	 */
	public static String getApprovalScheme(WorkflowContext wfc) {
		Attributes<String, Object> args = wfc.getArguments();
		String approvalScheme = args.getString(VAR_APPROVAL_SCHEME);
		if (approvalScheme == null && !args.containsKey(VAR_APPROVAL_SCHEME)) {
			WorkflowContext parent = wfc.getParent();
			while (parent != null) {
				Attributes<String, Object> parentArgs = parent.getArguments();
				if (parentArgs != null
						&& parentArgs.containsKey(VAR_APPROVAL_SCHEME)) {
					approvalScheme = parentArgs.getString(VAR_APPROVAL_SCHEME);
					break;
				}
				parent = parent.getParent();
			}
		}
		return approvalScheme;
	}

	/**
	 * @param role
	 * @return
	 */
	public static Set<String> getPermitNames(Bundle role) {
		Set<String> permitNames = new HashSet<String>();
		List<Bundle> permits = role.getPermits();
		if (!Util.isEmpty(permits)) {
			for (Bundle permit : permits) {
				permitNames.add(permit.getName());
			}
		}
		return permitNames;
	}

	/**
	 * @param item
	 * @return
	 */
	public static boolean isPasswordItem(ApprovalItem item) {
		if (item.getName() != null) {
			return item.getName().equals("password");
		}
		return false;
	}
}
