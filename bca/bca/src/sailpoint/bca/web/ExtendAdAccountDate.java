package sailpoint.bca.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.support.RequestContext;

import com.sun.research.ws.wadl.Request;

import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.api.Workflower;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.BcaCalendar;
import sailpoint.common.CalendarBean;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.QueryOptions;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowLaunch;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import sailpoint.workflow.rule.BCAApproval;

//test



@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class ExtendAdAccountDate extends AccountsRequestBean{
	
	String className = "::ExtendAdAccountDate::";
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	public static Logger logger = Logger
			.getLogger("sailpoint.bca.web.ExtendAdAccountDate");
	
	public String userId;
	
	public String endDate;
	
	public String applicationName;
	
	public String status;
	
	public List<LinkApplication> links;
	
	
	List lstTanggal;
	
	List lstBulan;
	
	List lstTahun;
	
	public String message;
	
	public String errorMessage;
	
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
	

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}



	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	/**
	 * @return the lstTanggal
	 */
	public List getLstTanggal() {
		return lstTanggal;
	}



	/**
	 * @param lstTanggal the lstTanggal to set
	 */
	public void setLstTanggal(List lstTanggal) {
		this.lstTanggal = lstTanggal;
	}



	/**
	 * @return the lstBulan
	 */
	public List getLstBulan() {
		return lstBulan;
	}


	
	/**
	 * @param lstBulan the lstBulan to set
	 */
	public void setLstBulan(List lstBulan) {
		this.lstBulan = lstBulan;
	}
	
	
	
	/**
	 * @return the lstTahun
	 */
	public List getLstTahun() {
		return lstTahun;
	}
	
	
	
	/**
	 * @param lstTahun the lstTahun to set
	 */
	public void setLstTahun(List lstTahun) {
		this.lstTahun = lstTahun;
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
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	
	@SuppressWarnings("static-access")
	public ExtendAdAccountDate() throws GeneralException, ParseException{
		super();
		
		//List<LinkApplication> linksApp = new ArrayList();
		TreeMap<String,LinkApplication> srtMap = new TreeMap();
		
		
		
		try {
			
			//List<LinkApplication> linksApp = new ArrayList();
			
			Identity loggedInUser = getLoggedInUser();
			
			BcaCalendar cal = new BcaCalendar();
			
			setLstTanggal(cal.getLstTanggal());
			
			setLstBulan(cal.getLstBulan());
			
			setLstTahun(cal.getLstTahun());	
			
			String branchCode = (String)loggedInUser.getAttribute(IdentityAttribute.BRANCH_CODE);
			
			Iterator nonEmployeeIterator = IdentityUtil.getNonEmployeeBranchIdentity(getContext(), branchCode/*, loggedInUser.getDisplayName()*/);					
			
			logger.debug("iterator : " + nonEmployeeIterator);
			
			
			
			DateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy"); //SimpleDateFormat("yyyy-MMM-dd'T'HH:mm:ss.SSSX");
			SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
			
			while(nonEmployeeIterator.hasNext()){
				
				Identity identity = (Identity)nonEmployeeIterator.next();						
				
				logger.debug(className + " [RAW]Identity End Date: " + identity.getAttribute(IdentityAttribute.DISPLAY_NAME));
				
				logger.debug(className + " [RAW]Identity End Date: " + identity.getAttribute(IdentityAttribute.END_DATE));
				
				String endDate = null;
				try {
					endDate = identity.getAttribute(IdentityAttribute.END_DATE).toString();
				}catch(Exception e) {
					logger.debug("failed to get endDate attribute !");
				}
				
				String inputText = null;
				if (endDate != null) {
					if(endDate.matches("[+-]?\\d*(\\.\\d+)?")) {
						if(endDate.length() > 1) {
							Date date = originalFormat.parse(endDate);
							inputText = inputFormat.format(date);
						}
					}else {
						inputText = "Never";
					}
				}
				
				String outputText = "date";
				
				
				if(inputText != null){
					outputText = inputText;					
				}
				
				
				logger.debug(className + " identity with id " + identity.getName());
				
				Link link = IdentityUtil.getAdLink(identity);								
				
				if(link != null && CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName()) && !link.isDisabled()){

					LinkApplication linkApps = new LinkApplication(link.getApplicationName(), (String)link.getAttribute(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME), "Active", link.getId(), link.getInstance());
					
					logger.debug(className + " identity with id " + identity.getName() + " samaccountname " + link.getAttribute(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME) + " and enddate" + identity.getAttribute(IdentityAttribute.END_DATE));
					
					if(identity.getAttribute(IdentityAttribute.END_DATE) != null)
						
						logger.debug(className + "the date is : " + IdentityAttribute.END_DATE);
						linkApps.setEndDate(outputText);
						//linkApps.setEndDate(IdentityAttribute.END_DATE);
					
						
					logger.debug(linkApps.getExtendDate() + linkApps.getExtendMonth() + linkApps.getExtendYear());
					if(linkApps.getExtendDate() == null){
						linkApps.setExtendDate(cal.getTanggal());				
					}
					
					if(linkApps.getExtendMonth() == null){
						linkApps.setExtendMonth(cal.getBulan());
					}
					
					if(linkApps.getExtendYear() == null){
						linkApps.setExtendYear(cal.getTahun());
					}
					
					if(linkApps.getnonEmployeeId() == null){
						linkApps.setnonEmployeeId(identity.getName());						
					}
					
					logger.debug(className + "get out extendDate List");
					srtMap.put(identity.getName(), linkApps);
					logger.debug("map sorted : " + srtMap);
					
					
					//linksApp.add(linkApps);
															
				}								 	 
				
			}
			
			if(srtMap != null){
				
				List<LinkApplication> list = new ArrayList<LinkApplication>(srtMap.values());
				logger.debug("list  : " + list);
				setLinks(list);
			}
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public String submitRequest(){
		
		String methodName = "::submitRequest::";
		
		String endDateParam = "endDate";
		
		String newEndDateParam = "newEndDate";
		
		String retVal = "retVal";
		
		logger.debug(methodName + " called");
		
		DateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
		
		ExternalContext ctx = getFacesContext().getExternalContext();
		
		List targetList = null;
		
		UIInput ui = new UIInput();
		
		

		
		if(getLinks() != null){
			Iterator i = getLinks().iterator();
			targetList = new ArrayList();
			while(i.hasNext()){
				LinkApplication linkApps = (LinkApplication)i.next();
				if(linkApps.isChecked()){
					int oldEndDate = 0;
					int newEndDate = 0;
					int dateNow = 0;
					
					String nonEmployeeId = "nonEmployeeID";
					Date date = null;
					String endDate = linkApps.getEndDate();
					logger.debug("endDate " + endDate);
					if(!endDate.equalsIgnoreCase("Never")) {
						try {
							date = inputFormat.parse(linkApps.getEndDate());
							logger.debug("endDate " + date);
							endDate = originalFormat.format(date);
							logger.debug("endDate " + endDate);
						} catch (ParseException e) {
							logger.debug("failed ");
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						date = new Date();
						
						dateNow = Integer.parseInt(originalFormat.format(date).toString());
						logger.debug("datenow : " + dateNow);
						
						if(linkApps.getEndDate() != null){
							oldEndDate = Integer.parseInt(endDate);
						}
						logger.debug(oldEndDate);
						
						if(linkApps.getFinalExtendDate() != null){
							newEndDate = Integer.parseInt(linkApps.getFinalExtendDate());
						}
						
						if(linkApps.getnonEmployeeId() != null){
							nonEmployeeId = linkApps.getnonEmployeeId();
						}
						
						logger.debug(className + methodName + " oldEndDate:" + oldEndDate 
								+ "::ExtendedNewEndDate:" + newEndDate + " and User Id " + nonEmployeeId);
						
						if(dateNow > 0 && dateNow >= newEndDate) {
							setErrorMessage("Invalid Extended Date");
							
							retVal = "failed";
							
							return null;
							
						}
						
						if(oldEndDate > 0  && newEndDate > 0 && oldEndDate >= newEndDate){
							setErrorMessage("Invalid Extended Date");
							
							retVal = "failed";
							
							return null;
							
							
						}else if(newEndDate - oldEndDate > 10000){
							setErrorMessage("Perpanjangan User ID tidak boleh lebih dari 1 Tahun");
							retVal = "failed";
							
							return null;
						}
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
		
		retVal = "success";
		
		logger.debug(className + methodName + " Here is target list " + targetList.toString());
		
		logger.debug(className + methodName + " Here is target list size " + targetList.size());
		
		if(targetList != null && targetList.size() > 0){
	
			try {
				logger.debug(className + methodName + " Try to Invoke WorkFlow NOW");
				
				invokeWorkflow(context, getIdentity(), targetList);
			} catch (GeneralException e) {
				
				retVal = "failed";
				e.printStackTrace();
			}
		}
		
		/*ExternalContext ctx = getFacesContext().getExternalContext();
		
		SailPointContext context = null;
		
		try {
			context = SailPointFactory.getCurrentContext();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug(methodName + " called");
		
		String retVal = "";
		
		Map reqMap = ctx.getRequestParameterValuesMap();
		
		Set<String> keys = reqMap.keySet();
		
		for(String key : keys){
			logger.debug(methodName + " key :" + key + ": with value :" + ((String[])reqMap.get(key))[0]);
		}
		
		logger.debug(className + methodName + " resuming process to invoke workflow..." + endDateParam);
		
		if(reqMap.get(endDateParam) != null && !"".equalsIgnoreCase(((String[])reqMap.get(endDateParam))[0])){
			
			logger.debug(className + methodName + " Entering invoking workflow...");
			
			String endDate = ((String[])reqMap.get(endDateParam))[0];
			
			logger.debug(className + methodName + " going to call invoke workflow...");
			
			try {
				logger.debug(className + methodName + " call invoke workflow");
				invokeWorkflow(context, getIdentity(), endDate);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			
		}*/
		
		return retVal;
		
	}
	
	private void invokeWorkflow(SailPointContext context, Identity employee, List targetApplication) throws GeneralException{
		
		String methodName = "::invokeWorkflow::";
		
		final String WORKFLOW_NAME = "BCA Base LCM Provisioning";
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		ProvisioningPlan plan = new ProvisioningPlan();
		
		Iterator it = targetApplication.iterator();
		
		while(it.hasNext()){
			LinkApplication link = (LinkApplication)it.next();
			
			logger.debug(methodName + "for "+ link.getnonEmployeeId() +" for extend account Date in 3..2...1... "
					+ "with old End Date " + link.getEndDate() + " Extended to " + link.getFinalExtendDate() );
		
			
			try {
				date = df.parse(link.getFinalExtendDate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			AccountRequest accReq = new AccountRequest();
			
			accReq.setApplication(CommonUtil.AD_APPLICATION);						
				
			List attrReqList = new ArrayList();
			
			accReq.setInstance(link.getInstance());
			
			//Epoch date
			long nowLong = date.getTime();
			 nowLong = (nowLong * 10000L) + 116444736000000000L;
			 
			 //native identity target need to get from link
			 QueryOptions qo = new QueryOptions();
			 qo.add(Filter.eq(IdentityAttribute.NAME, link.getnonEmployeeId()));
			 
			 String nativeIdentity = null;
			
			Iterator identityIt = context.search(Identity.class, qo);
			
			while(identityIt.hasNext()) {
				Identity nonEmp = (Identity) identityIt.next();
				Iterator LinkIt = nonEmp.getLinks().iterator();
				while(LinkIt.hasNext()) {
					Link lnkApps = (Link)LinkIt.next();
					if(lnkApps.getApplicationName().equalsIgnoreCase(CommonUtil.AD_APPLICATION)) {
						
						nativeIdentity = lnkApps.getNativeIdentity();
						break;
					}
				}
			}
			
			accReq.setNativeIdentity(nativeIdentity);
			
			accReq.setOperation(AccountRequest.Operation.Modify);
			
			AttributeRequest attrReqEndDate = new AttributeRequest();
			
			attrReqEndDate.setName(ActiveDirectoryAttribute.ACCOUNT_EXPIRES);			
			attrReqEndDate.setValue(String.valueOf(nowLong));			
			attrReqEndDate.setOp(ProvisioningPlan.Operation.Set);

			attrReqList.add(attrReqEndDate);
			
			accReq.addArgument("EXTEND_DATE", sdf.format(date));
			accReq.setAttributeRequests(attrReqList);
			
			plan.add(accReq);
			
			if(isDomainUserShouldEnabled(endDate)){
				
				AccountRequest adAccReq = new AccountRequest();
				
				adAccReq.setInstance(link.getInstance());
				
				adAccReq.setNativeIdentity(link.getNativeIdentity());
				
				adAccReq.setOperation(AccountRequest.Operation.Enable);
				
				plan.add(adAccReq);
				
			}
			
			logger.debug(className + methodName + "The Provisioning Plan after adding this account request looks this: " + plan.toXml());
			
			
			// Sort out all the variables required for workflow invocation....

			String identityDisplayName = link.getnonEmployeeId();

			String identityName = link.getnonEmployeeId();

			String launcher = employee.getDisplayName();

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

			launchArgsMap.put("flow", BCAApproval.FLOW_TYPE_EXTEND_ACCOUNT);

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

			launchArgsMap.put("identity", link.getnonEmployeeId());

			launchArgsMap.put("plan", plan);
			
			Attributes attribtues = new Attributes();

			attribtues.putAll(launchArgsMap);

			baseWf.setVariables(attribtues);

			WorkflowLaunch wflaunch = new WorkflowLaunch();

			Workflow wf = (Workflow) context.getObjectByName(Workflow.class, WORKFLOW_NAME);

			wflaunch.setWorkflowName(wf.getName());

			wflaunch.setWorkflowRef(wf.getName());

			wflaunch.setCaseName("Account Activation / Deactivation for "

			+ link.getnonEmployeeId() + " :: " + new Date());

			wflaunch.setVariables(launchArgsMap);

			Workflower workflower = new Workflower(context);

			WorkflowLaunch launch = workflower.launch(wflaunch);

			String workFlowId = launch.getWorkflowCase().getId();

			logger.debug(className + methodName + "Workflow got launched with workflow id: " + workFlowId);
			
		
		}
		/*ProvisioningPlan plan = new ProvisioningPlan();
		
		AccountRequest accReq = new AccountRequest();
		
		accReq.setApplication("IIQ");		
		
		accReq.setInstance(employee.getDisplayName());
		
		accReq.setNativeIdentity(employee.getDisplayName());
		
		accReq.setOperation(AccountRequest.Operation.Modify);
		
		
		attrReqEndDate.setName(IdentityAttribute.END_DATE);
		attrReqEndDate.setValue(endDate);
		attrReqEndDate.setOp(ProvisioningPlan.Operation.Set);
		
		List attrReqList = new ArrayList();
		
		attrReqList.add(attrReqEndDate);
		
		accReq.setAttributeRequests(attrReqList);
		
		plan.add(accReq);
		
		if(isDomainUserShouldEnabled(endDate)){
			
			AccountRequest adAccReq = new AccountRequest();
			
			Link link = IdentityUtil.getAdLink(employee);
			
			adAccReq.setApplication(link.getApplicationName());		
			
			adAccReq.setInstance(link.getInstance());
			
			adAccReq.setNativeIdentity(link.getNativeIdentity());
			
			adAccReq.setOperation(AccountRequest.Operation.Enable);
			
			plan.add(adAccReq);
			
		}
						
		logger.debug(className + methodName + "The Provisioning Plan after adding this account request looks this: " + plan.toXml());
		
		// Sort out all the variables required for workflow invocation....

		String identityDisplayName = employee.getDisplayName();

		String identityName = employee.getName();

		String launcher = employee.getDisplayName();

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

		launchArgsMap.put("flow", BCAApproval.FLOW_TYPE_ACCOUNTS_REQUEST);

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

		Attributes attribtues = new Attributes();

		attribtues.putAll(launchArgsMap);

		baseWf.setVariables(attribtues);

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

		logger.debug(className + methodName + "Workflow got launched with workflow id: " + workFlowId);*/
		
	}
	
	private boolean isDomainUserShouldEnabled(String endDate){
		
		Date now = new Date();
		
		SimpleDateFormat df = new SimpleDateFormat("YYYYMMdd");
		
		int todayLong = Integer.parseInt(df.format(now));
		
		int endDateInt = endDate == null || "".equalsIgnoreCase(endDate.trim()) ? 0 : Integer.parseInt(endDate);
		
		if("Disabled".equalsIgnoreCase(getStatus()) && endDateInt > todayLong){
			return true;
		}
		
		return false;
	}
	
	

}
