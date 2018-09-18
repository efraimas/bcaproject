package sailpoint.common;

import java.util.HashMap;

import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class WorkflowUtil {

	public static HashMap getLaunchArgsMap(Identity employee, Identity launcherId, ProvisioningPlan plan, String flowType){
		
		String identityDisplayName = employee.getDisplayName();

		String identityName = employee.getName();

		String launcher = launcherId.getName();

		String sessionOwner = launcherId.getName();
		
		HashMap launchArgsMap = new HashMap();

		launchArgsMap.put("allowRequestsWithViolations", "true");

		launchArgsMap.put("approvalMode", "parallelPoll");

		launchArgsMap.put("approvalScheme", "worldbank");

		launchArgsMap.put("approvalSet", "");

		launchArgsMap.put("doRefresh", "");

		launchArgsMap.put("enableRetryRequest", "false");

		launchArgsMap.put("fallbackApprover", "spadmin");
		
		launchArgsMap.put("flow", flowType);

		launchArgsMap.put("foregroundProvisioning", "true");

		launchArgsMap.put("identityDisplayName", identityDisplayName);

		launchArgsMap.put("identityName", identityName);

		launchArgsMap.put("identityRequestId", "");

		launchArgsMap.put("launcher", launcher);

		launchArgsMap.put("notificationScheme", "user,requester");

		launchArgsMap.put("optimisticProvisioning", "false");

		launchArgsMap.put("policiesToCheck", "");

		launchArgsMap.put("policyScheme", "continue");

		launchArgsMap.put("policyViolations", "");

		launchArgsMap.put("project", "");

		launchArgsMap.put("requireViolationReviewComments", "true");

		launchArgsMap.put("securityOfficerName", "");

		launchArgsMap.put("sessionOwner", sessionOwner);

		launchArgsMap.put("source", "LCM");

		launchArgsMap.put("trace", "true");

		launchArgsMap.put("violationReviewDecision", "");

		launchArgsMap.put("workItemComments", "");

		launchArgsMap.put("ticketManagementApplication", "");

		launchArgsMap.put("identity", employee);

		launchArgsMap.put("plan", plan);
		
		return launchArgsMap;
	}
}
