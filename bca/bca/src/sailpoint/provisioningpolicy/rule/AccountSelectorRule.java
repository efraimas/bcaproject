package sailpoint.provisioningpolicy.rule;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.object.Link;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class AccountSelectorRule {
	public static String CLASS_NAME = "::AccountSelectorRule::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.AccountSelectorRule");
	
	public static Link getUserIdIbsOperatorFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdIbsOperatorFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(userId.substring(4, 5).equalsIgnoreCase("6") && AccountSelectorRule.isNumber(userId.substring(1,7))==true && userId.substring(7, 8).equalsIgnoreCase("G")){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIdIbsIlsFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdIbsIlsFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(AccountSelectorRule.isNumber(userId.substring(1,7))==true && userId.substring(7, 8).equalsIgnoreCase("T")){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIdIbsOrFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdIbsOrFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(userId.substring(4, 5).equalsIgnoreCase("8") && AccountSelectorRule.isNumber(userId.substring(1,7))==true && userId.substring(7, 8).equalsIgnoreCase("S")){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIdDomainFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdDomainFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(userId.substring(0, 1).equalsIgnoreCase("U") && AccountSelectorRule.isNumber(userId.substring(1,7))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIdBnoFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdBnoFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(userId.substring(0, 3).equalsIgnoreCase("PDN")){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
				else if(userId.substring(0, 1).equalsIgnoreCase("T") && AccountSelectorRule.isNumber(userId.substring(1,2))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIdBtrFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdBtrFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(userId.substring(0, 2).equalsIgnoreCase("BT") && AccountSelectorRule.isNumber(userId.substring(2,3))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
				else if(userId.substring(0, 1).equalsIgnoreCase("T") && AccountSelectorRule.isNumber(userId.substring(1,2))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIdLldFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdLldFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(userId.substring(0, 1).equalsIgnoreCase("L") && AccountSelectorRule.isNumber(userId.substring(1,2))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
				else if(userId.substring(0, 1).equalsIgnoreCase("T") && AccountSelectorRule.isNumber(userId.substring(1,2))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIdMutFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdMutFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(userId.substring(0, 1).equalsIgnoreCase("s") && AccountSelectorRule.isNumber(userId.substring(1,2))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIdRimFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getUserIdRimFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				if(userId.substring(0, 1).equalsIgnoreCase("R") && AccountSelectorRule.isNumber(userId.substring(1,2))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
				else if(userId.substring(0, 1).equalsIgnoreCase("T") && AccountSelectorRule.isNumber(userId.substring(1,2))==true){
					logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
					return link;
				}
			}
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	public static Link getUserIDFromMainframe(String prefix, List links) throws GeneralException{
		String METHOD_NAME = "::getIDFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		logger.debug(CLASS_NAME + METHOD_NAME + "prefix  ::" + prefix + "::");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			int lengthPrefix = prefix.length();
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
			
				
				if(userId.substring(0, 2).equalsIgnoreCase("TL")){
					logger.debug(CLASS_NAME + METHOD_NAME + "userid TL : " + userId);
					returnLink = new Link();
					returnLink.setNativeIdentity(null);
					return returnLink;					
				}
				else{
				
					String tempPrefix = userId.substring(0, lengthPrefix);
					
					logger.debug(CLASS_NAME + METHOD_NAME + "tempPrefix  ::" + tempPrefix + "::");
					
					if(prefix.equalsIgnoreCase(tempPrefix.trim())){
						logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
						return link;
					}	
				}
			}
			
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
	
	
	public static boolean isAlpha(String userId) {
	    return userId.matches("[a-zA-Z]+");
	}
	
	public static boolean isNumber(String userId) {
	    return userId.matches("-?\\d+(\\.\\d+)?");
	}
	
	
	public static Link getUserIDCharacterFromMainframe(List links) throws GeneralException{
		String METHOD_NAME = "::getIDFromMainframe::";
		logger.debug(CLASS_NAME + METHOD_NAME +"Inside ...");
		
		Link returnLink = null;
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				String userId = link.getNativeIdentity();
				logger.debug(CLASS_NAME + METHOD_NAME + "userid  : " + userId);
				
				boolean name;
				name = AccountSelectorRule.isAlpha(userId);
				
				if(name == true){
					return link;
				}				
			}
			
		}
		
		returnLink = new Link();
		returnLink.setNativeIdentity(null);
		return returnLink;	
		
	}
		
}
