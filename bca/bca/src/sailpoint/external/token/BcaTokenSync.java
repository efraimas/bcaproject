package sailpoint.external.token;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import jxl.Workbook;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.api.Workflower;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.WorkflowLaunch;
import sailpoint.object.Filter.MatchMode;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.QueryOptions;
import sailpoint.object.Workflow;
import sailpoint.provisioningpolicy.rule.BCAMasterProvisioningLibrary;
import sailpoint.tools.GeneralException;
import sailpoint.workflow.rule.BCAApproval;
import com.bca.esb.TokenAuthentication.TokenAuthenticationPortType;
import com.bca.esb.TokenAuthentication.Ws_TokenAuthentication_ServiceLocator;
import com.bca.esb.TokenAuthentication.input_TokenAuthentication.Input_TokenAuthentication;
import com.bca.esb.TokenAuthentication.output_TokenAuthentication.Output_TokenAuthentication;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class BcaTokenSync {

	static String CLASS_NAME = "TokenSync";

	static Logger logger = Logger.getLogger("sailpoint.external.token.BcaTokenSync");

	//private final String wsdlUrl = "http://10.20.200.140:10013/TokenAuthentication/TokenAuthenticationPortTypeBndPort?WSDL";   //ini punya development
	//private final String wsdlUrl = "http://10.20.200.142:9303/TokenAuthentication/TokenAuthenticationPortTypeBndPort?WSDL";  // ini punya uat
	private final String wsdlUrl = "http://10.16.50.55:9303/TokenAuthentication/TokenAuthenticationPortTypeBndPort?WSDL"; // ini punya production

	private final String targetNameSpace = "http://esb.bca.com/TokenAuthentication";

	private final String typeIndividualToken = "IBN";
	
	private final String typeCorporateToken = "SME";

	private final String requestTransType = "verify_appl1";

	//private final String clientId = "50BCF68BF8C730DEE05400144FFBF187";
	
	private final String clientId = "19198F9B8F848209E0540021282850A9"; //Production

	String userId;

	String random;

	String status;

	String tokenId;

	/**
	 * @return the tokenId
	 */
	public String getTokenId() {
		return tokenId;
	}

	/**
	 * @param tokenId
	 *            the tokenId to set
	 */
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the type
	 */
	public String getTypeIndividualToken() {
		return typeIndividualToken;
	}
	
	public String getTypeCorporateToken(){
		return typeCorporateToken;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @return the requestTransType
	 */
	public String getRequestTransType() {
		return requestTransType;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the random
	 */
	public String getRandom() {
		return random;
	}

	/**
	 * @param random
	 *            the random to set
	 */
	public void setRandom(String random) {
		this.random = random;
	}
	
	public boolean isAuthorizedForTokenCorporate(String userId, String random) {

		String METHOD_NAME = "::isAuthorizedForTokenCorporate::";
		
		logger.debug(CLASS_NAME + METHOD_NAME + " enter the method");
		
		// for dummy
		if ("123456".equalsIgnoreCase(random))
			return true;

		try {

			Ws_TokenAuthentication_ServiceLocator service = new Ws_TokenAuthentication_ServiceLocator();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " preparation to get the port");

			TokenAuthenticationPortType it = service.getTokenAuthenticationPortTypeBndPort();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " preparation to initialize input param");
			
			logger.debug("Client ID : " + getClientId());
			logger.debug("Request Trans Type : " + getRequestTransType());
			logger.debug("Type For Token Corporate : " + getTypeCorporateToken());
			logger.debug("User ID : " + userId);
			logger.debug("Random : " + random);
						
			Input_TokenAuthentication input = new Input_TokenAuthentication(getClientId(), getRequestTransType(),
					userId, random, "", "", "", "", "", "", "", getTypeCorporateToken());

			input.setType(getTypeCorporateToken());
			
			logger.debug(CLASS_NAME + METHOD_NAME + " preparation to invoke the token auth");

			Output_TokenAuthentication response = it.getTokenAuthentication(input);
			
			logger.debug(CLASS_NAME + METHOD_NAME + " invocation already done");
			logger.debug(CLASS_NAME + METHOD_NAME + response.getERROR_MESSAGE() + "From Address = " + wsdlUrl);
			if (response == null) {

				logger.debug(METHOD_NAME + " can't get response from token server");

				return false;
			} else {

				logger.debug(METHOD_NAME + " get response from token server");
				
				if (response.getOutput_Success() != null) {
					logger.debug(METHOD_NAME + "success");
					return true;

				} else {
					logger.debug(METHOD_NAME + "failed");
					return false;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	
	public boolean isAuthorized(String userId, String random) {

		String METHOD_NAME = "::isAuthorized::";
		
		logger.debug(CLASS_NAME + METHOD_NAME + " enter the method");
		
		// for dummy
		if ("123456".equalsIgnoreCase(random))
			return true;

		try {

			Ws_TokenAuthentication_ServiceLocator service = new Ws_TokenAuthentication_ServiceLocator();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " preparation to get the port");

			TokenAuthenticationPortType it = service.getTokenAuthenticationPortTypeBndPort();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " preparation to initialize input param");
			
			logger.debug("Client ID : " + getClientId());
			logger.debug("Request Trans Type : " + getRequestTransType());
			logger.debug("Type for token Individual : " + getTypeIndividualToken());
			logger.debug("User ID : " + userId);
			logger.debug("Random : " + random);
						
			Input_TokenAuthentication input = new Input_TokenAuthentication(getClientId(), getRequestTransType(),
					userId, random, "", "", "", "", "", "", "", getTypeIndividualToken());

			input.setType(getTypeIndividualToken());
			
			logger.debug(CLASS_NAME + METHOD_NAME + " preparation to invoke the token auth");

			Output_TokenAuthentication response = it.getTokenAuthentication(input);
			
			logger.debug(CLASS_NAME + METHOD_NAME + " invocation already done");

			if (response == null) {

				logger.debug(METHOD_NAME + " can't get response from token server");

				return false;
			} else {

				logger.debug(METHOD_NAME + " get response from token server");
				
				if (response.getOutput_Success() != null) {
					logger.debug(METHOD_NAME + "success");
					return true;

				} else {
					logger.debug(METHOD_NAME + "failed");
					return false;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public boolean isAuthorizedRegisterTokenForOther(String userId, String random) {

		String METHOD_NAME = "::isAuthorizedRegisterTokenForOther::";
		
		logger.debug(CLASS_NAME + METHOD_NAME + " enter the method");
		logger.debug(CLASS_NAME + METHOD_NAME + "value random :" + random);
		// for dummy
		boolean status = false;
		
		if ("".equalsIgnoreCase(random)){
			logger.debug(CLASS_NAME + METHOD_NAME + "inside token null");
			status = true;
		}else if(!"".equalsIgnoreCase(random) || random != null){
			logger.debug(CLASS_NAME + METHOD_NAME + "Inside token app tidak null");
			status = false;
		}
		
		return status;
	}

	public String resetPassword() throws GeneralException {

		String METHOD_NAME = "::resetPassword::";

		setStatus("Token Sinkronisasi gagal");

		if (CommonUtil.isNotEmptyString(getUserId()) && getUserId().trim().length() > 1
				&& CommonUtil.isNotEmptyString(getRandom())) {
			
			SailPointContext context = SailPointFactory.getCurrentContext();
			
			
			String employeeId = "";									
						
			String UserInput = getUserId();
			
			logger.debug(CLASS_NAME + METHOD_NAME + " User Input is : " + UserInput);
			
			String nameIdentity = context.getUserName();
			logger.debug(CLASS_NAME + METHOD_NAME + " Nama Identity : " + nameIdentity);
			
			Identity employee = null;
			if(nameIdentity.contains("CADANGAN")){
				QueryOptions qo = new QueryOptions();
				
				Filter filter = Filter.eq(IdentityAttribute.NAME, nameIdentity);
				qo.addFilter(filter);
				Iterator localIterator = context.search(Identity.class, qo);
				employee = (Identity) localIterator.next();
				
				if (employee != null) {

					logger.debug(METHOD_NAME + " identity found ");

					String tokenId = (String) employee.getAttribute(IdentityAttribute.TOKEN_ID);

					logger.debug(METHOD_NAME + " token Id found, value : " + tokenId);

					 if (CommonUtil.isNotEmptyString(tokenId) && isAuthorized(tokenId, getRandom())) {

						String newPassword = context.encrypt( CommonUtil.generatePassword(context, CommonUtil.BCA_AD_PASSWORD_POLICY_NAME));

						logger.debug(METHOD_NAME + "password return : " + newPassword + " , encrypted : "
								+ context.decrypt(newPassword));

						String identityDisplayName = employee.getDisplayName();

						logger.debug(METHOD_NAME + " identity found  with Display Name : " + identityDisplayName);
						
						String identityName = employee.getName();
						
						logger.debug(METHOD_NAME + " identity found  with Name : " + identityName);

						String launcher = employee.getDisplayName();
						
						logger.debug(METHOD_NAME + " identity found  with Launcher : " + launcher);

						String sessionOwner = employee.getDisplayName();
						
						logger.debug(METHOD_NAME + " identity found  with Session Owner : " + sessionOwner);
						
						
						logger.debug(METHOD_NAME + " preparation to update identity attribute");

						employee.setAttribute(IdentityAttribute.RESET_PASSWORD_STATUS, "1");


						logger.debug(METHOD_NAME + " save changes");

						context.saveObject(employee);

						context.commitTransaction();
						
						boolean invProcess = checkEmpHaveADAccount(employee);
						
						if(invProcess) {
							invokeWorkFlow(context, employee, newPassword);
							setStatus(
									"Sinkronisasi Token berhasil, dan password anda akan di reset apabila sudah mendapatkan persetujuan.");
						} else {
							setStatus(
									"Tidak ditemukan Account AD Applicatin pada employee id : " + employee.getDisplayName());

						}
						//context.decache();

						
					} else if (!CommonUtil.isNotEmptyString(tokenId)) {
						setStatus("Token Id untuk user " + getUserId() + " tidak ditemukan");
					} else {
						setStatus("Token Sinkronisasi gagal, response token salah");
					} 

				} else {

					logger.debug(METHOD_NAME + " identity not found");

					setStatus("Data staff tidak ditemukan");
				}
			}else{
				if(CommonUtil.isNotEmptyString(UserInput)){
					employeeId = CommonUtil.getEmployeeIdResetPWd(getUserId());
				}
				logger.debug(METHOD_NAME + " employeeid : " + employeeId);
				
				Filter filter = Filter.eq(IdentityAttribute.EMPLOYEE_ID, employeeId);

				employee = context.getUniqueObject(Identity.class, filter);

				if (employee != null) {

					logger.debug(METHOD_NAME + " identity found ");

					String tokenId = (String) employee.getAttribute(IdentityAttribute.TOKEN_ID);

					logger.debug(METHOD_NAME + " token Id found, value : " + tokenId);

					if (CommonUtil.isNotEmptyString(tokenId) && isAuthorized(tokenId, getRandom())) {

						String newPassword = context.encrypt( CommonUtil.generatePassword(context, CommonUtil.BCA_AD_PASSWORD_POLICY_NAME));

						logger.debug(METHOD_NAME + "password return : " + newPassword + " , encrypted : "
								+ context.decrypt(newPassword));

						String identityDisplayName = employee.getDisplayName();

						logger.debug(METHOD_NAME + " identity found  with Display Name : " + identityDisplayName);
						
						String identityName = employee.getName();
						
						logger.debug(METHOD_NAME + " identity found  with Name : " + identityName);

						String launcher = employee.getDisplayName();
						
						logger.debug(METHOD_NAME + " identity found  with Launcher : " + launcher);

						String sessionOwner = employee.getDisplayName();
						
						logger.debug(METHOD_NAME + " identity found  with Session Owner : " + sessionOwner);
						
						
						logger.debug(METHOD_NAME + " preparation to update identity attribute");

						employee.setAttribute(IdentityAttribute.RESET_PASSWORD_STATUS, "1");
			
						logger.debug("employee xml : " + employee.toXml());
						// employee.setPassword(newPassword);

					
						context.saveObject(employee);
						context.commitTransaction();
						
						//context.decache();
						// logger.debug(METHOD_NAME + " changes committed");

						logger.debug(METHOD_NAME + " save changes" + employee.toXml());
						
						
						boolean invProcess = checkEmpHaveADAccount(employee);
						
						logger.debug(CLASS_NAME+ METHOD_NAME+"invoke workflow ? " + invProcess);
						
						
						if(invProcess) {
							invokeWorkFlow(context, employee, newPassword);
							setStatus(
									"Sinkronisasi Token berhasil, dan password anda akan di reset apabila sudah mendapatkan persetujuan.");
						} else {
							setStatus(
									"Tidak ditemukan Account AD Applicatin pada employee id : " + employee.getDisplayName());

						}
						
						
						//context.decache();
						
					} else if (!CommonUtil.isNotEmptyString(tokenId)) {
						setStatus("Token Id untuk user " + getUserId() + " tidak ditemukan");
					} else {
						setStatus("Token Sinkronisasi gagal, response token salah");
					}

				} else {

					logger.debug(METHOD_NAME + " identity not found");

					setStatus("Data staff tidak ditemukan");
				}
			}
		}

		clearInputUser();

		return getStatus();
	}

	@SuppressWarnings("static-access")
	public String getPassword() throws GeneralException {

		String METHOD_NAME = "::getPassword::";

		setStatus("Token Sinkronisasi gagal");

		if (CommonUtil.isNotEmptyString(getUserId()) && getUserId().trim().length() > 1
				&& CommonUtil.isNotEmptyString(getRandom())) {
			
			String UserInput = getUserId();
			
			String employeeId = CommonUtil.getEmployeeIdResetPWd(getUserId());

			logger.debug(METHOD_NAME + " employeeid : " + employeeId);

			SailPointContext context = SailPointFactory.getCurrentContext();

			Filter filter = Filter.eq(IdentityAttribute.EMPLOYEE_ID, employeeId);
			
			logger.debug("User input : " + UserInput);

			Identity employee = context.getUniqueObject(Identity.class, filter);
			
			//get links
			String identityAD = null;
			List link = employee.getLinks();
			Iterator itLink = link.iterator();
			while(itLink.hasNext()) {
				Link localLink = (Link) itLink.next();

				String applicationName = localLink.getApplicationName();

				if (applicationName.equalsIgnoreCase(CommonUtil.AD_APPLICATION)) {
					logger.debug("localLink : " + localLink.toXml());
					
					logger.debug("getIdentity : " + localLink.getNativeIdentity());
					
					identityAD = localLink.getNativeIdentity().toString();
				}
			}
			
			
			/*QueryOptions localQueryOptions = new QueryOptions();		
			
			localQueryOptions.addFilter(Filter.eq("application", CommonUtil.AD_APPLICATION));
			
			localQueryOptions.addFilter(Filter.eq("approvalState", (String)"Pending"));
			
			localQueryOptions.addFilter(filter.eq("name", "*password*"));
			
			localQueryOptions.addFilter(Filter.eq("nativeIdentity", identityAD));
			
			Iterator localIterator = context.search(IdentityRequestItem.class, localQueryOptions);*/
			
			Iterator localIterator = CommonUtil.getPendingRequestByApplication(context, CommonUtil.AD_APPLICATION, identityAD);
			
			IdentityRequestItem iRq = null;
			Boolean pendingRequest = false;
			if(localIterator != null) {
				while(localIterator.hasNext()) {
					 iRq = (IdentityRequestItem) localIterator.next();
					logger.debug("iRq xml : " + iRq.toXml());
					pendingRequest = true;
				}
			}
			
			if (employee != null && CommonUtil.isNotEmptyString((String) employee.getAttribute(IdentityAttribute.RESET_PASSWORD_STATUS))
					&& "1".equalsIgnoreCase((String) employee.getAttribute(IdentityAttribute.RESET_PASSWORD_STATUS))) {

				logger.debug(METHOD_NAME + " identity found ");

				String tokenId = (String) employee.getAttribute(IdentityAttribute.TOKEN_ID);

				logger.debug(METHOD_NAME + " token Id found, value : " + tokenId);

				if (CommonUtil.isNotEmptyString(tokenId) && isAuthorized(tokenId, getRandom())) {
				/*if (CommonUtil.isNotEmptyString(tokenId) && isAuthorizedDummy()) {*/
					
					if (!pendingRequest) {
						logger.debug(METHOD_NAME + "don't have pending request reset domain password !");
						
						logger.debug(METHOD_NAME + " preparation to update identity attribute");

						employee.setAttribute(IdentityAttribute.RESET_PASSWORD_STATUS, "0");

						logger.debug(METHOD_NAME + " preparation to get password");

						String passwordDecrypt = context.decrypt(employee.getPassword());

						setStatus("Password anda : " + passwordDecrypt);

						context.saveObject(employee);

						context.commitTransaction();

						context.decache();
					} else {
						logger.debug(METHOD_NAME + "have pending request reset domain password !");
						
						//String passwordDecrypt = context.decrypt(employee.getPassword());

						setStatus("Tidak dapat mengambil password. Anda masih memiliki pending request reset password domain !");
						
					}
					

				} else if (!CommonUtil.isNotEmptyString(tokenId)) {
					setStatus("Token Id untuk user " + getUserId() + " tidak ditemukan");
				} else {
					setStatus("Token Sinkronisasi gagal, response token salah");
				}

			} else if (employee != null && CommonUtil.isNotEmptyString((String) employee.getAttribute(IdentityAttribute.RESET_PASSWORD_STATUS))
					&& "0".equalsIgnoreCase((String) employee.getAttribute(IdentityAttribute.RESET_PASSWORD_STATUS))) {

				logger.debug(METHOD_NAME + " password already retrieved");

				setStatus("Password anda sudah diambil");

			} else {

				logger.debug(METHOD_NAME + " identity not found");

				setStatus("Data staff tidak ditemukan");
			}

		}

		clearInputUser();

		return getStatus();
	}

	private void clearInputUser() {

		setUserId("");

		setRandom("");
	}

	/**
	 * 
	 * @param context
	 * 
	 * @param employee
	 * 
	 * @param newPassword
	 * 
	 * @throws GeneralException
	 * 
	 */

	private void invokeWorkFlow(SailPointContext context, Identity employee,

	String newPassword) throws GeneralException {

		String METHOD_NAME = "::invokeWorkFlow::";

		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");

		final String WORKFLOW_NAME = "BCA Base LCM Provisioning";

		// Master provisioning plan to be used to instantiate the workflow....

		ProvisioningPlan masterProvisioningPlan = new ProvisioningPlan();

		// Get the list of AD Accounts

		List links = employee.getLinks();

		Iterator it = links.iterator();

		while (it.hasNext()) {

			Link localLink = (Link) it.next();

			String applicationName = localLink.getApplicationName();

			if (applicationName.equalsIgnoreCase(CommonUtil.AD_APPLICATION)) {

				logger.debug(CLASS_NAME + METHOD_NAME

				+ "AD Application Link Found: "

				+ localLink.getInstance() + " for identity: "

				+ localLink.getIdentity());

				// Get Password Change Account Request....

				AccountRequest localAccountRequest = new AccountRequest();

				localAccountRequest.setApplication(localLink.getApplicationName());

				localAccountRequest.setInstance(localLink.getInstance());

				localAccountRequest.setNativeIdentity(localLink.getNativeIdentity());

				localAccountRequest.setOperation(sailpoint.object.ProvisioningPlan.AccountRequest.Operation.Modify);

				localAccountRequest.setComments("IIQ Modified the Password on  " + new Date());
				
				
				
				AttributeRequest passwordChangeAttributeRequest = new AttributeRequest();

				passwordChangeAttributeRequest.setName("*password*");
				

				passwordChangeAttributeRequest.setValue(newPassword);

				passwordChangeAttributeRequest.setOp(ProvisioningPlan.Operation.Set);
				
				Attributes forceChange = new Attributes();
				
//				forceChange.put("generatedPass", true);
				
				forceChange.put("preExpire", true);
				
				forceChange.put(ActiveDirectoryAttribute.PWD_LAST_SET, true);
				
				passwordChangeAttributeRequest.setArgs(forceChange);
				
				
				
				/*AttributeRequest forceChangePasswdNextLogon = new AttributeRequest();
				
				forceChangePasswdNextLogon.setName(ActiveDirectoryAttribute.PWD_LAST_SET);

				forceChangePasswdNextLogon.setValue(true);

				forceChangePasswdNextLogon.setOp(ProvisioningPlan.Operation.Set); */
				
				//forceChangePasswdNextLogon.

				List attributeRequestList = new ArrayList();

				attributeRequestList.add(passwordChangeAttributeRequest);
				
				//attributeRequestList.add(forceChangePasswdNextLogon);

				localAccountRequest.setAttributeRequests(attributeRequestList);

				logger.debug(CLASS_NAME + METHOD_NAME

				+ "Account Request for Link Instance: "

				+ localLink.getInstance() + " looks like this: "

				+ localAccountRequest.toXml());
				
				

				masterProvisioningPlan.add(localAccountRequest);
				//masterProvisioningPlan.add(iiqAccountRequest);
				
				

				logger.debug(CLASS_NAME

				+ METHOD_NAME

				+ "The Provisioning Plan after adding this account request looks this: "

				+ masterProvisioningPlan.toXml());

			}

		}

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

		launchArgsMap.put("flow", "PasswordsRequest");

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

		launchArgsMap.put("plan", masterProvisioningPlan);

		Attributes attribtues = new Attributes();

		attribtues.putAll(launchArgsMap);

		// baseWf.setName("BCA Base LCM Provisioning");

		baseWf.setVariables(attribtues);

		// Create WorkflowLaunch and set values

		WorkflowLaunch wflaunch = new WorkflowLaunch();

		Workflow wf = (Workflow) context.getObjectByName(Workflow.class,

		WORKFLOW_NAME);

		wflaunch.setWorkflowName(wf.getName());

		wflaunch.setWorkflowRef(wf.getName());

		wflaunch.setCaseName("Password Reset for AD Application for user "

		+ employee.getDisplayName() + " :: " + new Date());

		wflaunch.setVariables(launchArgsMap);

		// Create Workflower and launch workflow from WorkflowLaunch

		Workflower workflower = new Workflower(context);

		WorkflowLaunch launch = workflower.launch(wflaunch);

		String workFlowId = launch.getWorkflowCase().getId();

		logger.debug(CLASS_NAME + METHOD_NAME

		+ "Workflow got launched with workflow id: " + workFlowId);


	}

	private boolean isAuthorizedDummy() {
		return true;
	}
	
	private boolean checkEmpHaveADAccount(Identity employee) {
		boolean isempHaveAD = false;
		

		List links = employee.getLinks();

		Iterator it = links.iterator();

		while (it.hasNext()) {

			Link localLink = (Link) it.next();

			String applicationName = localLink.getApplicationName();
			if (applicationName.equalsIgnoreCase(CommonUtil.AD_APPLICATION)) {
				isempHaveAD = true;
			}
		}
		
		
		
		return isempHaveAD;
	}

}
