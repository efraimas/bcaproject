package sailpoint.provisioningpolicy.rule;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class AppIdDomainProvisioning {
	
	public static String CLASS_NAME = "::AppIdDomainProvisioning::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.AppIdDomainProvisioning");	

	public static String userId = "";
	public static String employeeId = "";
	
	public static String getUser_ID_Domain(SailPointContext context,Identity identity)
			throws GeneralException
		{
			String METHOD_NAME = "::getUser_ID_CCR::";
			logger.debug(METHOD_NAME + "Inside...");
			
			/*
			 * Ini untuk cek, apakah identity ini punya account yang sejenis
			 * */
			
			List links  = identity.getLinks();
			
			if(links != null && links.size() > 0){
				
				Iterator it = links.iterator();
				
				while(it.hasNext()){
					Link link = (Link)it.next();
					
					if("IBM MAINFRAME RACF".equalsIgnoreCase(link.getApplicationName())){
						String userId = link.getNativeIdentity();
						logger.debug(CLASS_NAME + METHOD_NAME + "MAINFRAME link's found, with userid  : " + userId);
						
						if(userId.substring(0, 1).equalsIgnoreCase("U") && AccountSelectorRule.isNumber(userId.substring(1,7))==true){
							logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
							return link.getNativeIdentity();
						}
					}	
				}
			}
			
			employeeId = (String) identity.getAttribute(IdentityAttribute.EMPLOYEE_ID);
			
			if(employeeId.length()==8){
				userId = 'U' + employeeId.substring(2);
			}
			else if(employeeId.length()==6){
				userId = 'U' + employeeId;
			}
			else{
				logger.debug(CLASS_NAME + METHOD_NAME + "EmployeeID is Either empty or wrong format....");
			}

			return userId;		
		}
}
