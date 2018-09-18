package sailpoint.correlationconfig.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Link;
import sailpoint.object.ResourceObject;
import sailpoint.api.SailPointContext;
import sailpoint.tools.GeneralException;


@SuppressWarnings({ "rawtypes" })
public class ActiveDirectoryCorrelation {
	
	public static Logger logger = Logger.getLogger("sailpoint.correlationconfig.rule.ActiveDirectoryCorrelation");
	
	@SuppressWarnings("unchecked")
	public static Map activeDirectoryCorrelationRule(SailPointContext internalContext,
			Map environment, Application application, ResourceObject account,
			Link link) throws GeneralException {
		
		String METHOD_NAME = "::ActiveDirectoryCorrelationRule::";
		logger.debug(METHOD_NAME + "Inside...");
		
		Map correlatedMap = new HashMap();
		Attributes localAccountAttribute = account.getAttributes();
		
		String employeeId = "";
				
		if(localAccountAttribute.containsKey(ActiveDirectoryAttribute.EMPLOYEE_ID)){
			String employeeID = localAccountAttribute.getString(ActiveDirectoryAttribute.EMPLOYEE_ID).trim();
			logger.debug(METHOD_NAME + " attribute employeeID ditemukan, dengan nilai ::" + employeeID + "::");
			employeeId = employeeID;
			
			if(employeeID!=null && employeeID.length()>0 && isNumeric(employeeID)==true){
				if(employeeId.length()==5){
					if(employeeId.startsWith("5") || employeeId.startsWith("6") || 
							employeeId.startsWith("7")){
						employeeId = "000" + employeeId;
					}
					else{
						logger.debug(METHOD_NAME + "Model NIP Lima digit belum terdaftar");
					}
				}
				else if(employeeId.length()==6){
					if(employeeId.startsWith("00") || employeeId.startsWith("01") || 
							employeeId.startsWith("02") || employeeId.startsWith("03")){
						employeeId = "20" + employeeId;
					}
					else if(employeeId.startsWith("05") || employeeId.startsWith("06") || 
							employeeId.startsWith("07")){
						employeeId = "00" + employeeId;
					}
					else if(employeeId.startsWith("7") || employeeId.startsWith("8") || 
							employeeId.startsWith("9")){
						employeeId = "19" + employeeId;
					}
					else if(employeeId.startsWith("6")){
						employeeId = "80" + employeeId;
					}
					else{
						logger.debug(METHOD_NAME + "Model NIP Enam digit belum terdaftar");
					}
				}
				else{
					logger.debug(METHOD_NAME + "Old NIP: " + employeeId);
				}
				
				logger.debug(METHOD_NAME + "New NIP: " + employeeId);
				
				if(CommonUtil.isNotEmptyString(employeeId)){
					logger.debug(METHOD_NAME + "Returning Identity Map with employeeid: "+employeeId);
					correlatedMap.put("identityAttributeName",IdentityAttribute.EMPLOYEE_ID);
					correlatedMap.put("identityAttributeValue",employeeId);
					return correlatedMap;
				}
			}		
		}
		else if(localAccountAttribute.containsKey(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME)){
			String sAMAccountName = localAccountAttribute.getString(ActiveDirectoryAttribute.SAM_ACCOUNT_NAME).toUpperCase().trim();
			logger.debug(METHOD_NAME + "attribute sAMAccountName ditemukan, dengan nilai ::" + sAMAccountName + "::");
			employeeId =  sAMAccountName.toUpperCase();
			
			if(sAMAccountName!=null && sAMAccountName.length()>0){
				if(employeeId.startsWith("U00") || employeeId.startsWith("U01") || employeeId.startsWith("U02") || 
						employeeId.startsWith("U03") || employeeId.startsWith("U04")){
					employeeId = employeeId.replace("U", "20");
				}
				else if(employeeId.startsWith("U05") || employeeId.startsWith("U06") || employeeId.startsWith("U07") || 
						employeeId.startsWith("U5") || employeeId.startsWith("U6")){
					employeeId = employeeId.replace("U", "00");
				}				
				else if(employeeId.startsWith("U7") || employeeId.startsWith("U8") || employeeId.startsWith("U9")){
					employeeId = employeeId.replace("U", "19");
				}
				else if(employeeId.startsWith("U5")){
					employeeId = employeeId.replace("U", "");
				}
				else{
					logger.debug(METHOD_NAME + "ID Tidak dapat di convert");
				}
				
				logger.debug(METHOD_NAME + "New NIP: " + employeeId);
				
				if(CommonUtil.isNotEmptyString(employeeId)){
					logger.debug(METHOD_NAME + "Returning Identity Map with employeeid: "+employeeId);
					correlatedMap.put("identityAttributeName",IdentityAttribute.EMPLOYEE_ID);
					correlatedMap.put("identityAttributeValue",employeeId);
					return correlatedMap;
				}
			}
			else{
				logger.debug(METHOD_NAME + "Attribute sAMAccountName tidak ada atau NULL");
			}
			
		}
		else{
			logger.debug(METHOD_NAME + "Attribute EmployeeID atau sAMAccountName tidak ada atau NULL");
			return correlatedMap;
		}
		
		return correlatedMap;
	}
	
	public static boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  
	
}
