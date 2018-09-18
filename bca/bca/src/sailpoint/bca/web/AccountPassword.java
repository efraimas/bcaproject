package sailpoint.bca.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import sailpoint.api.IdentityService;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.common.BCAPasswordPolicyName;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.external.token.BcaTokenSync;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.Link;
import sailpoint.object.QueryOptions;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import sailpoint.api.IdentityService;
import sailpoint.object.Application;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class AccountPassword extends AccountsRequestBean{
	
	String className = "::AccountPassword::";
	static Logger logger = Logger.getLogger("sailpoint.bca.web.AccountPassword");
	
	String userId;
	
	String tokenResponse;
	
	Map<String,String> listApplication;
	
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

	/**
	 * @return the listApplication
	 */
	public Map<String, String> getListApplication() {
		return listApplication;
	}

	public AccountPassword() throws GeneralException {
		
		super();
	}
	
	public String submitRequest(){
		String methodName = "::submitRequest::";
		
		logger.debug(className + methodName + "userid : " + getUserId());
		logger.debug(className + methodName + "response : " + getTokenResponse());
		
		String namaIdentity = "";
		
		try {
			namaIdentity = (String)getIdentity().getAttribute(IdentityAttribute.DISPLAY_NAME);
			logger.debug(className + methodName + "Name Identity : " + namaIdentity);
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String tokenId = "";
		
		try {
			tokenId = (String)getIdentity().getAttribute(IdentityAttribute.TOKEN_ID);
			logger.debug(className + methodName + "tokenId : " + tokenId);
		} catch (GeneralException e) {
			e.printStackTrace();
			
			setMessage("Token anda tidak tersimpan dalam database. Silahkan lakukan pendaftarakan token terlebih dahulu");
			
			return getMessage();
		}
		
		if(tokenId == null || "".equalsIgnoreCase(tokenId)){
			setMessage("Token anda tidak tersimpan dalam database. Silahkan lakukan pendaftarakan token terlebih dahulu");
			return getMessage();
		}
		
		BcaTokenSync token = new BcaTokenSync();
		
		boolean isCadangan = false;
		boolean isNormalEmp = false;

		if(namaIdentity.contains("CADANGAN")){
			isCadangan = token.isAuthorizedForTokenCorporate(tokenId, this.tokenResponse);
		}else{
			isNormalEmp = token.isAuthorized(tokenId, this.tokenResponse);
		}
		
		//salah satu true then process search the password
		if(isCadangan || isNormalEmp) {
			logger.debug(className + methodName + " Sync token berhasil");
			
			Iterator localIterator = null;
			//check any pending request resetpassword for this account
			
			try {
				if(this.getUserId().contains(".")) {
					localIterator = CommonUtil.getPendingRequestByApplication(getContext(), CommonUtil.BASE24_FILE_FEED_APPLICATION, this.getUserId());
				}else {
					localIterator = CommonUtil.getPendingRequestByApplication(getContext(), CommonUtil.RACF_APPLICATION_NAME, this.getUserId());
				}
			} catch (GeneralException e2) {
				logger.debug(methodName + "failed to get identityRequestItem");
			}
			
			IdentityRequestItem iRq = null;
			Boolean pendingRequest = false;
			if(localIterator != null) {
				while(localIterator.hasNext()) {
					 iRq = (IdentityRequestItem) localIterator.next();
					try {
						logger.debug("iRq xml : " + iRq.toXml());
					} catch (GeneralException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pendingRequest = true;
				}
			}
			
			
			if(pendingRequest) {
				setMessage("Tidak dapat mengambil password untuk User ID : " + this.userId + ". anda masih memiliki pending request reset password untuk User ID tersebut");
			}else {
				try {
					
					List <Map> listMap = null;
					Map <Map, Map> tokenMap = new HashMap<>();
					
					//base24 temp password
					if(this.getUserId().contains(".")){
						try{
							listMap = (List) getIdentity().getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
							for (Map nm : listMap) {
								tokenMap.putAll(nm);
							}
						}catch (Exception e) {
							logger.debug(className + methodName + " not list");
							tokenMap = (Map)getIdentity().getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
						}
					}else{
						try{
							listMap = (List) getIdentity().getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
							for (Map nm : listMap) {
								tokenMap.putAll(nm);
							}
						}catch (Exception e) {
							logger.debug(className + methodName + " not list");
							tokenMap = (Map)getIdentity().getAttribute(IdentityAttribute.TEMP_PASSWORD_APPS);
						}
					}
	
					 
					logger.debug(className + methodName + "token map value : " + tokenMap);
					if(tokenMap == null){
						setMessage("Password untuk User ID " + this.userId + "tidak ditemukan");
					}else{
							//Mainframe
							String keyMap = null;
						    if(this.userId.contains(".")) {
						    	keyMap = this.userId;
						    }else {
						    	keyMap = this.userId.toUpperCase();
						    }
							 
							logger.debug(className + methodName + " key : " + keyMap);
							
							Map passwordMap = tokenMap.get((String)keyMap);
							String password = "";
							
							if(tokenMap.get((String)keyMap) == null){
								setMessage("Password untuk User ID " + this.userId + "tidak ditemukan");   
							}else{
								
								password = getContext().decrypt((String)passwordMap.get((String)"password"));
								
								
								 String bcaApplicationName = (String)passwordMap.get("aplikasi");
								
								Identity identity = null;
								try {
									identity = getIdentity();
								} catch (GeneralException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								List links = identity.getLinks();
								int size = links.size();
								Link link = null;
								Attributes att = null;
								
								for(int j=0; j<size; j++){
									 link = (Link) links.get(j);
									 if(link != null){
										 try {
											logger.debug(className + methodName + "link xml : " + link.toXml());
											
											if(this.userId.equalsIgnoreCase(link.getDisplayName())){
												att = (Attributes) link.getAttributes();
												logger.debug("groupnya adalah : " + att.getList("groups"));
												break;
											}
										} catch (GeneralException e) {
											e.printStackTrace();
										}
									 }
								}
								
								try{
									logger.debug("groupnya adalah : " + att.getList("groups"));
									if(bcaApplicationName != null && !"".equalsIgnoreCase(bcaApplicationName)){
										if(bcaApplicationName.contains("IDS")
											|| "ILS".equalsIgnoreCase(bcaApplicationName)
											|| "OR".equalsIgnoreCase(bcaApplicationName)){
											password = password.substring(0, password.length() - 2);
											logger.debug(className + methodName + "Is BDIDS");
											
										}
									}else if(att.getList("groups").toString().contains("IBSWIL") || att.getList("groups").toString().contains("IBSOROPR") 
											|| att.getList("groups").toString().contains("IBSORSPV") || att.getList("groups").toString().contains("ENDUSW3")){
										password = password.substring(0, password.length() - 2);
										logger.debug(className + methodName + "Is IDS");
										
									}
									
								}catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}
								
								setMessage("Password untuk User ID " + this.userId + " ditemukan dengan password : " + password);  
							}
							
						}
				} catch (GeneralException e) {
					
					e.printStackTrace();
					
					setMessage("Password untuk User ID " + this.userId + "tidak ditemukan");
				}
			}
		} else if(!isCadangan && !isNormalEmp) {
			setMessage("Authorized Token Failed");
		}

		
		return this.message;
	}
	
	private static String getApplicationName(String selectedApps){
		
		String[] arrApps = selectedApps.split(" ");
		
		return arrApps != null && arrApps.length > 0 ? arrApps[0] : selectedApps;
	}
	
	public static void main(String args[]){
		//System.out.println(":" + getApplicationName("KOT") + ":");
		String abc = "opr2.wil601";
		System.out.println(abc.toUpperCase());
	}
	
}
