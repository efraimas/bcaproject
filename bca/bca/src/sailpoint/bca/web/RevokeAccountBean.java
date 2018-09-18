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
import sailpoint.common.BcaCalendar;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityUtil;
import sailpoint.common.MainframeUtil;
import sailpoint.common.RACFAttribute;
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

@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
public class RevokeAccountBean extends AccountsRequestBean {

	String className = "::RevokeAccountBean::";

	public static Logger logger = Logger.getLogger("sailpoint.bca.web.RevokeAccountBean");

	public String userId;

	public List<LinkApplication> links;

	List lstTanggal;

	List lstBulan;

	List lstTahun;

	public String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List getLstTanggal() {
		return lstTanggal;
	}

	public void setLstTanggal(List lstTanggal) {
		this.lstTanggal = lstTanggal;
	}

	public List getLstBulan() {
		return lstBulan;
	}

	public void setLstBulan(List lstBulan) {
		this.lstBulan = lstBulan;
	}

	public List getLstTahun() {
		return lstTahun;
	}

	public void setLstTahun(List lstTahun) {
		this.lstTahun = lstTahun;
	}

	public List<LinkApplication> getLinks() {
		return links;
	}

	public void setLinks(List<LinkApplication> links) {
		this.links = links;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public RevokeAccountBean() throws GeneralException{
		logger.debug("Inside BCA Revoke Account Bean");
		
		Identity identity = getIdentity();
		logger.debug(className + " identity with id " + identity.getDisplayName());
		
		List links = identity.getLinks();
		logger.debug(className + " Size link : " + links.size());
		
		BcaCalendar cal = new BcaCalendar();
		
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
					
					if (!link.isDisabled()) {
						LinkApplication linkApps = new LinkApplication(link.getApplicationName(), userId, "active",	link.getId(), link.getInstance());

						linkApps.setNativeIdentity(link.getNativeIdentity());

						if (linkApps.getRevokedDate() == null) {
							linkApps.setRevokedDate(cal.getTanggal());
						}

						if (linkApps.getRevokedMonth() == null) {
							linkApps.setRevokedMonth(cal.getBulan());
						}

						if (linkApps.getRevokedYear() == null) {
							linkApps.setRevokedYear(cal.getTahun());
						}

						if (linkApps.getResumedDate() == null) {
							linkApps.setResumedDate(cal.getTanggal());
						}

						if (linkApps.getResumedMonth() == null) {
							linkApps.setResumedMonth(cal.getBulan());
						}

						if (linkApps.getResumedYear() == null) {
							linkApps.setResumedYear(cal.getTahun());
						}

						linkApps.setBcaApplicationName(bcaApplicationName);
						linksApp.add(linkApps);
					}
				}
			}
		}
		
		if (getLinks() == null) {
			this.setLinks(linksApp);
		}

		setLstTanggal(cal.getLstTanggal());
		setLstBulan(cal.getLstBulan());
		setLstTahun(cal.getLstTahun());
	}

	public String submitRequest() {

		String retVal = "success";
		String methodName = "::submitRequest::";
		logger.debug(methodName + " called");

		List targetList = null;

		if (getLinks() != null) {
			Iterator i = getLinks().iterator();
			targetList = new ArrayList();
			while (i.hasNext()) {
				LinkApplication linkApps = (LinkApplication) i.next();
				if (linkApps.isChecked()) {
					int revokeDate = 0;
					int resumeDate = 0;

					if (linkApps.getFinalRevokeDate() != null) {
						revokeDate = Integer.parseInt(linkApps.getFinalRevokeDate());
					}

					if (linkApps.getFinalResumeDate() != null) {
						resumeDate = Integer.parseInt(linkApps.getFinalResumeDate());
					}

					logger.debug(className + methodName + " revokeDate:" + revokeDate + "::resumeDate:" + resumeDate);

					if (revokeDate > 0 && resumeDate > 0 && revokeDate >= resumeDate) {
						setErrorMessage("Tanggal resume date harus lebih besar dari revoke date");
						return "failed";
					}
					targetList.add(linkApps);
				}
			}
		}

		SailPointContext context = null;

		try {
			context = SailPointFactory.getCurrentContext();
			logger.debug(className + methodName + " Try to get Context" + context.toString());

		} catch (GeneralException e) {
			e.printStackTrace();
		}

		logger.debug(className + methodName + " Here is target list " + targetList.toString());
		logger.debug(className + methodName + " Here is target list size " + targetList.size());

		if (targetList != null && targetList.size() > 0) {

			try {
				logger.debug(className + methodName + " Try to Invoke WorkFlow NOW");
				invokeWorkflow(context, getIdentity(), targetList);
				
			} catch (GeneralException e) {
				retVal = "failed";
				e.printStackTrace();
				
			}
		}

		return retVal;

	}

	private void invokeWorkflow(SailPointContext context, Identity employee, List targetApplication)
			throws GeneralException {

		String methodName = "::invokeWorkflow::";
		
		final String WORKFLOW_NAME = "BCA Base LCM Provisioning";

		ProvisioningPlan plan = new ProvisioningPlan();

		Iterator it = targetApplication.iterator();

		while (it.hasNext()) {

			LinkApplication link = (LinkApplication) it.next();

			boolean isDisabledNow = false;

			int intToday = Integer.parseInt(BcaCalendar.getMainframeTodayDate());
			logger.debug(className + methodName + " mainframe today date format " + intToday);
			logger.debug(className + methodName + " revoke date " + link.getFinalRevokeDate());
			logger.debug(className + methodName + " resume date " + link.getFinalResumeDate());

			if (link.getFinalRevokeDate() != null && link.getFinalRevokeDate().trim().length() > 1) {
				int intRevokedDate = Integer.parseInt(link.getFinalRevokeDate());
				
				if(intRevokedDate <= intToday){
					isDisabledNow = true;
				}
			}
			logger.debug(className + methodName + " Disabled Now Status " + isDisabledNow);

			AccountRequest accReq = new AccountRequest();

			if (isDisabledNow) {
				accReq.setOperation(AccountRequest.Operation.Disable);
				logger.debug("Inside");
			} else {
				accReq.setOperation(AccountRequest.Operation.Modify);
				logger.debug("Inside");
			}

			List attrReqList = null;

			if (CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName())) {
				logger.debug("Inside");
				
				accReq.setApplication(link.getApplicationName());
				accReq.setInstance(link.getInstance());
				accReq.setNativeIdentity(link.getNativeIdentity());
				
				if (link.getFinalResumeDate() != null && link.getFinalResumeDate().trim().length() > 1) {
					logger.debug("Inside");
					int intResumeDate = Integer.parseInt(link.getFinalResumeDate());
					
					if(intResumeDate > intToday){
						logger.debug("Inside");
						accReq.addArgument("RESUME_DATE", link.getFinalResumeDate());
					}
				}
				
				if (!isDisabledNow) {
					logger.debug(className + methodName + "revoke to the future");

					attrReqList = new ArrayList();

					AttributeRequest attrReqRevoke = new AttributeRequest();
					attrReqRevoke.setName(RACFAttribute.REVOKE_DATE);
					attrReqRevoke.setValue(link.getFinalRevokeDate());
					attrReqRevoke.setOp(ProvisioningPlan.Operation.Set);
					
					attrReqList.add(attrReqRevoke);
				}
				logger.debug("Done for MF");
				
			}else if (CommonUtil.BASE24_FILE_FEED_APPLICATION.equalsIgnoreCase(link.getApplicationName())) {
				//Khusus untuk base24, operation tetap disabled, walau revoke date in the future
				accReq.setApplication(link.getApplicationName());
				accReq.setInstance(link.getInstance());
				accReq.setNativeIdentity(link.getNativeIdentity());
				accReq.setOperation(AccountRequest.Operation.Disable);
				accReq.addArgument("REVOKE_DATE", link.getFinalRevokeDate());
				accReq.addArgument("RESUME_DATE", link.getFinalResumeDate());
			}

			if (attrReqList != null && attrReqList.size() > 0) {
				accReq.setAttributeRequests(attrReqList);
			}

			if (accReq != null) {
				plan.add(accReq);
			}
		}

		logger.debug(className + methodName + "The Provisioning Plan after adding this account request looks this: "+ plan.toXml());

		// Sort out all the variables required for workflow invocation....
		String identityDisplayName = employee.getDisplayName();
		String identityName = employee.getName();
		// String launcher = employee.getDisplayName();
		//logger.debug("employee : " + employee.toXml());
		String sessionOwner = employee.getDisplayName();

		// Now go for the invocation of workflow....

		Workflow baseWf = new Workflow();
		HashMap launchArgsMap = new HashMap();

		launchArgsMap.put("allowRequestsWithViolations", "true");
		launchArgsMap.put("approvalMode", "parallelPoll");
		launchArgsMap.put("approvalScheme", "worldbank");
		launchArgsMap.put("approvalSet", "");
		launchArgsMap.put("doRefresh", "");
		launchArgsMap.put("enableRetryRequest", "false");
		launchArgsMap.put("fallbackApprover", "spadmin");
		launchArgsMap.put("flow", "Revoke");
		launchArgsMap.put("foregroundProvisioning", "true");
		launchArgsMap.put("identityDisplayName", identityDisplayName);
		launchArgsMap.put("identityName", identityName);
		launchArgsMap.put("identityRequestId", "");
		// launchArgsMap.put("launcher", launcher);
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

		Attributes attribtues = new Attributes();
		attribtues.putAll(launchArgsMap);

		baseWf.setVariables(attribtues);

		WorkflowLaunch wflaunch = new WorkflowLaunch();
		Workflow wf = (Workflow) context.getObjectByName(Workflow.class, WORKFLOW_NAME);

		wflaunch.setWorkflowName(wf.getName());
		wflaunch.setWorkflowRef(wf.getName());
		wflaunch.setCaseName("Account Activation / Deactivation for "+ employee.getDisplayName() + " :: " + new Date());
		wflaunch.setVariables(launchArgsMap);

		Workflower workflower = new Workflower(context);
		WorkflowLaunch launch = workflower.launch(wflaunch);

		String workFlowId = launch.getWorkflowCase().getId();
		logger.debug(className + methodName + "Workflow got launched with workflow id: " + workFlowId);
	}

	/*
	 * public static void main(String args[]) throws Exception{
	 * 
	 * DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); Date date = new
	 * Date();
	 * 
	 * long nowLong = (13150918800000000L - 116444736000000000L);
	 * 
	 * nowLong = nowLong / 10000;
	 * 
	 * date= new Date(20170927); System.out.println(df.format(date));
	 * System.out.println(new Date(nowLong));
	 * 
	 * }
	 */

}
