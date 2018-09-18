package sailpoint.bca.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.object.Link;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.api.Workflower;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.BCAPasswordPolicyName;
import sailpoint.common.Base24Attribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityUtil;
import sailpoint.common.MainframeUtil;
import sailpoint.common.RACFAttribute;
import sailpoint.common.WorkflowUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.Workflow;
import sailpoint.object.WorkflowLaunch;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import sailpoint.workflow.rule.BCAApproval;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ResetAppsPasswordBean extends AccountsRequestBean{
	
String className = "::ResetPasswordBean::";
	
	public static Logger logger = Logger
			.getLogger("sailpoint.bca.web.ResetAppsPasswordBean");
	
	public List<LinkApplication> links;

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

	public ResetAppsPasswordBean() throws GeneralException{
		
		super();
		
		Identity identity = getIdentity();
		
		logger.debug(className + " identity with id " + identity.getDisplayName());
		
		List links = identity.getLinks();
		
		Iterator it = links.iterator();
		List<LinkApplication> linksApp = new ArrayList();
		
		while(it.hasNext()){
			Link link = (Link)it.next();
			//Reset password tidak untuk SAP HR, reset password untuk AD, hanya dari login page
			if(!CommonUtil.HR_APPLICATION.equalsIgnoreCase(link.getApplicationName()) 
					&& !CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
				
				String userId = link.getNativeIdentity();
				String bcaApplicationName = link.getApplicationName();
				
				if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
					userId = (String)link.getAttribute(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME);
				}
				
				if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName())){
					Attributes att = null;
					
					//search by assigned role
					String roleName = IdentityUtil.getAssignedRoleFromAccountId(identity, userId);
					if(roleName != null && !"".equalsIgnoreCase(roleName.trim())){
						bcaApplicationName = MainframeUtil.getBcaMainframeApplication(getContext(), roleName);
					}
					
					//search by detected role
					/*if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(roleName)){
						roleName = IdentityUtil.getDetectionRoleFromAccountId(identity, userId);
						if(roleName != null && !"".equalsIgnoreCase(roleName.trim())){
							bcaApplicationName = MainframeUtil.getBcaMainframeApplication(getContext(), roleName);
						}	
					}*/
					
					//search by entitlement attribute if from assigned and detected not get the application name
					if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(roleName)){
						//att = (Attributes) link.getAttributes();
						if(userId.equalsIgnoreCase(link.getDisplayName())){
							att = (Attributes) link.getAttributes();
							try{
								att.getList("groups");
								bcaApplicationName = IdentityUtil.getRoleNameFromEntitlementAccountId(getContext(), att);
							}catch(Exception e){
								roleName = IdentityUtil.getDetectionRoleFromAccountId(identity, userId);
								
								if(roleName != null && !"".equalsIgnoreCase(roleName.trim())){
									bcaApplicationName = MainframeUtil.getBcaMainframeApplication(getContext(), roleName);
								}	
								logger.debug("group entitlements is empty");
							}
							
						}else{
								
						}
						
					}
					
				}	
				
				/*if(!link.isDisabled()){*/
					LinkApplication linkApps = new LinkApplication(userId, link.getInstance(), link.getApplicationName());
					linkApps.setNativeIdentity(link.getNativeIdentity());
					linkApps.setBcaApplicationName(bcaApplicationName);
					linksApp.add(linkApps);
				/*}	*/	
				
			}
			
		}
		
		if(getLinks() == null){
			this.setLinks(linksApp);
		}

	}
	
	public String submitRequest(){
	
		String retVal = "success";
		
		String methodName = "::submitRequest::";
		
		logger.debug(methodName + " called");
		
		SailPointContext context = null;
		
		try {
			context = SailPointFactory.getCurrentContext();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		List targetList = null;
		
		if(getLinks() != null){
			Iterator i = getLinks().iterator();
			targetList = new ArrayList();
			while(i.hasNext()){
				LinkApplication linkApps = (LinkApplication)i.next();
				logger.debug(className + methodName + "Link application : " + linkApps.toString());
				
				Identity identity = null;
				try {
					identity = getIdentity();
				} catch (GeneralException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				logger.debug(className + " identity with id " + identity.getDisplayName());
				
				List links = identity.getLinks();
				int size = links.size();
				Link link = null;
				Attributes att = null;

				if(linkApps.isChecked()){
					boolean isBdsApps = false;
					String passwdPolicy = "";
				
					if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(linkApps.getApplicationName())){
						passwdPolicy = BCAPasswordPolicyName.AD_PASSWORD_POLICY_NAME;
					}else if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(linkApps.getApplicationName())){
						passwdPolicy = BCAPasswordPolicyName.RACF_PASSWORD_POLICY_NAME;
						boolean isGroupsAtt = false;
						for(int j=0; j<size; j++){
							 link = (Link) links.get(j);
							 if(link != null){
								 try {
									logger.debug(className + methodName + "link xml : " + link.toXml());
									
									if(linkApps.getNativeIdentity().equalsIgnoreCase(link.getDisplayName())){
										att = (Attributes) link.getAttributes();
										try{
											logger.debug("groupnya adalah : " + att.getList("groups"));
											isGroupsAtt = true;
										}catch(Exception e){
											logger.debug("group entitlements is empty");
											
										}
										break;
									}
								} catch (GeneralException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							 }
						}
						
						
						//trying to get from Name Application first  ..
						if(!"".equalsIgnoreCase(linkApps.getBcaApplicationName()) && !CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(linkApps.getBcaApplicationName())){
							if("IDS".equalsIgnoreCase(linkApps.getBcaApplicationName())
									|| "ILS".equalsIgnoreCase(linkApps.getBcaApplicationName())
									|| "IDS/ILS".equalsIgnoreCase(linkApps.getBcaApplicationName())
									|| "OR".equalsIgnoreCase(linkApps.getBcaApplicationName()))
							{
								passwdPolicy = BCAPasswordPolicyName.IDS_MAINFRAME_POLICY;
								isBdsApps = true;
								
							}
						}else if(isGroupsAtt){
							//if no Name Application trying to get from group Entitlements
							if(att.getList("groups") != null) {
								if(att.getList("groups").toString().contains("IBSWIL") || att.getList("groups").toString().contains("IBSOROPR") || att.getList("groups").toString().contains("IBSORSPV")) {
									passwdPolicy = BCAPasswordPolicyName.IDS_MAINFRAME_POLICY;
									isBdsApps = true;
									
								}
							}
							
						} else{
							passwdPolicy = BCAPasswordPolicyName.EXCLUDEBDS_MAINFRAME_POLICY;
							isBdsApps = true;
						}
						
					}else if(CommonUtil.BASE24_FILE_FEED_APPLICATION.equalsIgnoreCase(linkApps.getApplicationName())){
						passwdPolicy = BCAPasswordPolicyName.BASE24_PASSWORD_POLICY_NAME;
					}
					logger.debug(className + methodName + "password policy is : " + passwdPolicy);
					
					String password = "";
					try {
						
						String passwordPlain = CommonUtil.generatePassword(context, passwdPolicy);
						logger.debug(className + methodName + " plain password for " + linkApps.getUserId() + " apps : " + linkApps.getApplicationName() + " is " + passwordPlain);
						
						if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(linkApps.getApplicationName())) {
							if(isBdsApps && BCAPasswordPolicyName.IDS_MAINFRAME_POLICY.equals(passwdPolicy)){
								//String suffix = CommonUtil.getBcaSystemConfig(context, "BDS_SUFFIX");
								String suffix = "IS";
								logger.debug(className + methodName + " this is bds apps, hence add " + suffix);
								passwordPlain = passwordPlain.substring(0, passwordPlain.length() - 2) + suffix;
								logger.debug(className + methodName + " plainPass after add suffix bds " + passwordPlain);
							}else if(!isBdsApps && BCAPasswordPolicyName.EXCLUDEBDS_MAINFRAME_POLICY.equals(passwdPolicy)){
								logger.debug(className + methodName + " this is bds apps, hence add ");
								passwordPlain = passwordPlain.substring(0, passwordPlain.length() - 2);
								logger.debug(className + methodName + " plainPass after add suffix bds " + passwordPlain);
							}
							else{
								passwordPlain = passwordPlain.substring(0, 6);
								logger.debug(className + methodName + " Password other IDS " + passwordPlain);
								
							}
						}
						
						password = context.encrypt(passwordPlain);
						
					} catch (GeneralException e) {
						e.printStackTrace();
					}
					
					linkApps.setPassword(password);
					
					targetList.add(linkApps);
				}
					
			}
			
		}
	
		if(targetList != null && targetList.size() > 0){
			try {
				invokeWorkflow(context, getIdentity(), targetList);
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return retVal;
	}

	@SuppressWarnings("unused")
	private void invokeWorkflow(SailPointContext context, Identity employee, List targetApplication) throws GeneralException{
		
		String methodName = "::invokeWorkflow::";
		
		final String WORKFLOW_NAME = "BCA Base LCM Provisioning";
		
		ProvisioningPlan plan = new ProvisioningPlan();
		
		Iterator it = targetApplication.iterator();
		
		while(it.hasNext()){
			
			LinkApplication link = (LinkApplication)it.next();
			
			AccountRequest enableRacfReq = null;
			
			AccountRequest accReq = new AccountRequest();
			//for email template
			accReq.addArgument("APP_NAME", link.getBcaApplicationName());
			
			accReq.setApplication(link.getApplicationName());
			
			accReq.setInstance(link.getInstance());
			
			accReq.setNativeIdentity(link.getNativeIdentity());
			
			accReq.setOperation(AccountRequest.Operation.Modify);
			
			AttributeRequest passwordChangeAttributeRequest = null;
			
			AttributeRequest forceChangePasswdNextLogon = null;
			
			if(CommonUtil.RACF_APPLICATION_NAME.equalsIgnoreCase(link.getApplicationName())){
				
				passwordChangeAttributeRequest = new AttributeRequest();
	
				passwordChangeAttributeRequest.setName(RACFAttribute.PASSWORD);
	
				passwordChangeAttributeRequest.setValue(link.getPassword());
	
				passwordChangeAttributeRequest.setOp(ProvisioningPlan.Operation.Set);
				
				Attributes forceChange = new Attributes();
				
				forceChange.put("generatedPass", true);
				
				forceChange.put("preExpire", true);
				
				passwordChangeAttributeRequest.setArgs(forceChange);
				
				
				Calendar cal = Calendar.getInstance();
				
				cal.add(Calendar.MONTH, -12);
				
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				
				String mfFormat = dateFormat.format(cal.getTime());
				
				System.out.println("Tanggal Reset Mf " + mfFormat);
				
				
				//Langsung enable user
				
				//enableRacfReq = new AccountRequest();
				
				//enableRacfReq.setApplication(link.getApplicationName());
				
				//enableRacfReq.setInstance(link.getInstance());
				
				//enableRacfReq.setNativeIdentity(link.getNativeIdentity());
				
				//enableRacfReq.setOperation(AccountRequest.Operation.Enable);
				
			}else if(CommonUtil.AD_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
				
				passwordChangeAttributeRequest = new AttributeRequest();
	
				passwordChangeAttributeRequest.setName(ActiveDirectoryAttribute.PASSWORD);
	
				passwordChangeAttributeRequest.setValue(link.getPassword());
	
				passwordChangeAttributeRequest.setOp(ProvisioningPlan.Operation.Set);
				
				
				forceChangePasswdNextLogon = new AttributeRequest();
				
				forceChangePasswdNextLogon.setName(ActiveDirectoryAttribute.PWD_LAST_SET);
	
				forceChangePasswdNextLogon.setValue(true);
	
				forceChangePasswdNextLogon.setOp(ProvisioningPlan.Operation.Set);
			}else if(CommonUtil.BASE24_FILE_FEED_APPLICATION.equalsIgnoreCase(link.getApplicationName())){
				logger.debug(className + methodName + "masuk reset base24 + update temp passord");
				Calendar cal = Calendar.getInstance();
				
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				
				String base24date = dateFormat.format(cal.getTime());
				try{
					IdentityUtil.updateTempPasswordIdentityForResetPassword(context, employee, link.getApplicationName(), link.getNativeIdentity(), base24date, link.getPassword());
				}catch(Exception e){
					logger.debug(className + methodName + "failed to update temp password");
					e.printStackTrace();
				}
				
				passwordChangeAttributeRequest = new AttributeRequest();
	
				passwordChangeAttributeRequest.setName(Base24Attribute.PASSWORD);
	
				passwordChangeAttributeRequest.setValue(link.getPassword());
	
				passwordChangeAttributeRequest.setOp(ProvisioningPlan.Operation.Set);
			}
			
			List attributeRequestList = new ArrayList();
			
			if(passwordChangeAttributeRequest != null){
				attributeRequestList.add(passwordChangeAttributeRequest);
			}
			
			if(forceChangePasswdNextLogon != null){
				attributeRequestList.add(forceChangePasswdNextLogon);
			}
	
			if(attributeRequestList != null && attributeRequestList.size() > 0){
				
				accReq.setAttributeRequests(attributeRequestList);
				
				if(accReq != null){
					plan.add(accReq);
					
					if(enableRacfReq != null){
						plan.add(enableRacfReq);
					}
				}
			}	
		}
		
		logger.debug(className + methodName + "The Provisioning Plan after adding this account request looks this: " + plan.toXml());
		
		// Sort out all the variables required for workflow invocation....
		
		Workflow baseWf = new Workflow();
		
		Attributes attributes = new Attributes();
		
		/*logger.debug(className + methodName + " launcher xml " + getLoggedInUser().toXml());
		
		logger.debug(className + methodName + " launcher name " + getLoggedInUser().getName());*/
	
		HashMap launchArgsMap = WorkflowUtil.getLaunchArgsMap(employee, getLoggedInUser(), plan, BCAApproval.FLOW_TYPE_PASSWORD_REQUEST);
	
		attributes.putAll(launchArgsMap);
	
		baseWf.setVariables(attributes);
	
		WorkflowLaunch wflaunch = new WorkflowLaunch();
	
		Workflow wf = (Workflow) context.getObjectByName(Workflow.class, WORKFLOW_NAME);
	
		wflaunch.setWorkflowName(wf.getName());
	
		wflaunch.setWorkflowRef(wf.getName());
	
		wflaunch.setCaseName("Reset password for "
	
		+ employee.getDisplayName() + " :: " + new Date());
	
		wflaunch.setVariables(launchArgsMap);
	
		Workflower workflower = new Workflower(context);
	
		WorkflowLaunch launch = workflower.launch(wflaunch);
	
		String workFlowId = launch.getWorkflowCase().getId();
	
		logger.debug(className + methodName + "Workflow got launched with workflow id: " + workFlowId);

	}

	/*public static void main(String args[]){
		String passwordPlain = "UIEYRIEH56";
		String suffix = "JJ";
		
		System.out.println("Plain : " + passwordPlain);
		
		passwordPlain = passwordPlain.substring(0, passwordPlain.length() - 2) + suffix;
		
		System.out.println("After : " +passwordPlain);
	}*/
	

}
