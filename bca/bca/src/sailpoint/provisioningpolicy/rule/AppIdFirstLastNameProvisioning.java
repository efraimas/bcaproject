package sailpoint.provisioningpolicy.rule;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.RACFAttribute;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class AppIdFirstLastNameProvisioning {
	
	public static String CLASS_NAME = "::AppIdFirstLastNameProvisioning::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.AppIdFirstLastNameProvisioning");
	
	public static String firstName = "";
	public static String middleName = "";
	public static String lastName = "";
	public static String userId = "";
	
	public static String getUser_ID_FirstLastName(SailPointContext context, Identity identity)throws GeneralException{
		
		String METHOD_NAME = "getUser_ID_FirstLastName";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
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
					
					boolean name;
					name = AccountSelectorRule.isAlpha(userId);
					
					if(name == true){
						return link.getNativeIdentity();
					}
				}
			}
		}
		
		if((firstName = (String) identity.getAttribute(IdentityAttribute.FIRST_NAME))!=null ){
			firstName = (String) identity.getAttribute(IdentityAttribute.FIRST_NAME);
			middleName = (String) identity.getAttribute(IdentityAttribute.MIDDLE_NAME);
			lastName = (String) identity.getAttribute(IdentityAttribute.LAST_NAME);
			
			String temp = "";
			int lengthLastName;
			boolean isUnique = false;
			int counter = 0;
			
			while(!isUnique){
				if(firstName.length()>= 7 && middleName == null && lastName == null){
					userId = firstName.substring(0, 7);	
				}
				else if(middleName == null && lastName.length()>=6){
					if (counter > 0){
						userId = firstName.substring(0, 2) + lastName;
						userId = userId.substring(0, 7);
					} else {
						userId = firstName.substring(0, 1) + lastName;
						userId = userId.substring(0, 7);
					}
				}
				else if(middleName == null && lastName != null && lastName.length()<6){
					lengthLastName = lastName.length();
					userId = firstName.substring(0, 7-lengthLastName) + lastName;
				}
				else if(lastName.length()>=5){
					userId = firstName.substring(0, 1) + middleName.substring(0, 1) + lastName;
					userId = userId.substring(0, 7);
				}
				else if(lastName.length()>1 && lastName.length()<5){
					lengthLastName = lastName.length();
					userId = firstName.substring(0, 1) + middleName.substring(0, 6 - lengthLastName) + lastName;
				}
				else if(lastName.length()==1){
					temp = middleName + lastName;
					int lengthTemp = temp.length();
					
					userId = firstName.substring(0, 7-lengthTemp) + temp;	
				}
				else{
					userId = "";
					logger.debug(CLASS_NAME + METHOD_NAME + "First Name, Middle Name and Last Name is Either empty or wrong format....");
				}
				
				logger.debug(CLASS_NAME + METHOD_NAME + "userId will be : " + userId);
				isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, userId);
				logger.debug(CLASS_NAME + METHOD_NAME + "userId is unique : " + isUnique);
				counter++;
			}
			
			return userId.toUpperCase();
		}
		else{
			return "";
		}
	}
}
