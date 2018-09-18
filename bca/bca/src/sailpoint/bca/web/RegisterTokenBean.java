package sailpoint.bca.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import sailpoint.api.Provisioner;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.external.token.BcaTokenSync;
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningProject;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class RegisterTokenBean extends AccountsRequestBean{
	
	static Logger logger = Logger.getLogger("sailpoint.bca.web.RegisterTokenBean");
	
	String className = "::RegisterTokenBean::";
	
	String employeeId;
	
	String employeeName;
	
	String tokenId;
	
	String tokenResponse;
	
	String message;
	
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the employeeId
	 */
	public String getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the employeeName
	 */
	public String getEmployeeName() {
		return employeeName;
	}

	/**
	 * @param employeeName the employeeName to set
	 */
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	/**
	 * @return the tokenId
	 */
	public String getTokenId() {
		return tokenId;
	}

	/**
	 * @param tokenId the tokenId to set
	 */
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	/**
	 * @return the tokenResponse
	 */
	public String getTokenResponse() {
		return tokenResponse;
	}

	/**
	 * @param tokenResponse the tokenResponse to set
	 */
	public void setTokenResponse(String tokenResponse) {
		this.tokenResponse = tokenResponse;
	}
	
	public RegisterTokenBean() throws GeneralException {
		
		super();
		
		System.out.println("Enter the constructor");
		
		logger.debug(className + " load the identity");
		
		Identity emp = getIdentity();
		
		setEmployeeId((String)emp.getAttribute(IdentityAttribute.EMPLOYEE_ID));
		
		logger.debug(className + " employee id : " + getEmployeeId());
		
		setEmployeeName((String)emp.getAttribute(IdentityAttribute.FIRST_NAME) + (String)(emp.getAttribute(IdentityAttribute.MIDDLE_NAME) == null ? "" : " " + emp.getAttribute(IdentityAttribute.MIDDLE_NAME))
				+ (String)(emp.getAttribute(IdentityAttribute.LAST_NAME) == null ? "" : " " + emp.getAttribute(IdentityAttribute.LAST_NAME)));
		
		setTokenId((String)emp.getAttribute(IdentityAttribute.TOKEN_ID));
		
	}
	
	public String submitRequest(){
		
		String methodName = "::submitRequest::";
		
		Identity identity = null;
		Identity loginIdentity = null;
		SailPointContext context = null;

		try {
			context = SailPointFactory.getCurrentContext();
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			loginIdentity = this.getLoggedInUser();
			logger.debug("search login identity :");
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		try {
			identity = getIdentity();
			logger.debug(className + methodName + "get identity with username " + identity.getDisplayName());
			
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Add save token id for Identity
		Identity tokenIdentity = null;
		try{
			logger.debug(className + methodName + "identity set get : " + this.employeeId);
			tokenIdentity = context.getObjectByName(Identity.class, this.employeeId);
		}catch (Exception e) {
			setMessage("Identity Tidak Ditemukan");
			e.printStackTrace();
			// TODO: handle exception
		}
		
		String tokenId = getTokenId();
		
		logger.debug(className + methodName + "get token id " + tokenId);
		
		BcaTokenSync tokenSync = new BcaTokenSync();
		List<Identity> wgList = null;
		try{
			wgList = loginIdentity.getWorkgroups();
		}catch (Exception e) {
			wgList = null;
			e.printStackTrace();
			// TODO: handle exception
		}
		logger.debug(className + methodName + "token response : "  + this.tokenResponse);
		for(int i = 0; i<wgList.size(); i++){
			wgList.get(i).toString();
			if(wgList.toString().contains("SKES Security Administrators")){
				logger.debug(className + methodName + "Inside token authorized for skes");
			
				tokenSync.isAuthorizedRegisterTokenForOther(tokenId, getTokenResponse());
				
			}else if(!wgList.toString().contains("SKES Security Administrators") || wgList == null){
				logger.debug(className + methodName + "Inside token authorized for normal karyawan");
				tokenSync.isAuthorized(tokenId, getTokenResponse());
				
				if(getTokenResponse() == null || "".equalsIgnoreCase(getTokenResponse())){
					setMessage("Silahkan isi respon key BCA anda (appl 1)");
					return "";
				}else if(!tokenSync.isAuthorized(tokenId, getTokenResponse())){
					setMessage("Response token yang anda masukkan salah");
					logger.debug(className + methodName + " authentication ke token server gagal");
					return "";
				}
			}
		}
		
		if(wgList == null || wgList.size() == 0 || "".equalsIgnoreCase(wgList.toString())){
			logger.debug(className + methodName + "Inside token authorized for normal karyawan");
			tokenSync.isAuthorized(tokenId, getTokenResponse());
			
			if(getTokenResponse() == null || "".equalsIgnoreCase(getTokenResponse())){
				setMessage("Silahkan isi respon key BCA anda (appl 1)");
				return "";
			}else if(!tokenSync.isAuthorized(tokenId, getTokenResponse())){
				setMessage("Response token yang anda masukkan salah");
				logger.debug(className + methodName + " authentication ke token server gagal");
				return "";
			}
		}
		
		logger.debug(className + methodName + " token response : " + getTokenResponse());
		
		Identity tempIdent = null;
		try {
			tempIdent = CommonUtil.searchIdentity(getContext(), IdentityAttribute.TOKEN_ID, getTokenId());
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		if(tempIdent != null && !tempIdent.getName().equalsIgnoreCase(identity.getName()) 
				&& !"CADANGAN".equalsIgnoreCase((String)identity.getAttribute(IdentityAttribute.DISPLAY_NAME))){
			setMessage("Token User Id " + getTokenId() + " sudah pernah di registrasi sebelumnya");
			logger.debug(className + methodName + " duplicate token id");
			return "";
		}
		
		ProvisioningPlan plan = new ProvisioningPlan();
		AccountRequest accReq = new AccountRequest();
		AttributeRequest attrToken = new AttributeRequest();
		
		accReq.setApplication(CommonUtil.IIQ_APPLICATION);
		accReq.setNativeIdentity(tokenIdentity.getName());
		accReq.setOperation(AccountRequest.Operation.Modify);
		
		
		plan.setIdentity(tokenIdentity);
		
		List attrReqList = new ArrayList();
		
		
		attrToken.setName(IdentityAttribute.TOKEN_ID);
		attrToken.setValue(getTokenId());
		attrToken.setOp(ProvisioningPlan.Operation.Set);
		attrReqList.add(attrToken);
		accReq.setAttributeRequests(attrReqList);
		plan.add(accReq);
		
		try {
			logger.debug(className + methodName + " plan should be like this " + plan.toXml());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Provisioner p = new Provisioner(getContext());
		
		logger.debug(className + methodName + " preparation to compile the plan");
		
		ProvisioningProject project = null;;
		try {
			project = p.compile(plan);
			logger.debug(className + methodName + "value project : " + project.toXml());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug(className + methodName + " preparation to execute the project");
		
		try {
			p.execute(project);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug(className + methodName + " project has been executed for link " + tokenIdentity.getDisplayName());
		
		return "success";
	}
	
	

}
