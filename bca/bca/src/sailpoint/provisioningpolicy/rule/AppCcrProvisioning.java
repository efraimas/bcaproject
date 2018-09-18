package sailpoint.provisioningpolicy.rule;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class AppCcrProvisioning {

	public static String CLASS_NAME = "::AppCcrProvisioning::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.AppCcrProvisioning");

	public static Object getUser_ID_CCR(SailPointContext context, Identity identity, String oldPrefix, ProvisioningProject project) throws GeneralException{
		
		String METHOD_NAME = "::getUser_ID_CCR::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		/*
		 * Ini untuk cek, apakah identity ini punya account yang sejenis
		 * */
		
		List links  = identity.getLinks();
		
		if(links != null && links.size() > 0){
					
			int lengthPrefix = oldPrefix.length();
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				
				if("IBM MAINFRAME RACF".equalsIgnoreCase(link.getApplicationName())){
					String userId = link.getNativeIdentity();
					logger.debug(CLASS_NAME + METHOD_NAME + "MAINFRAME link's found, with userid  : " + userId);
					
					if(userId.substring(0, 2).equalsIgnoreCase("CC")){
					
						String tempPrefix = userId.substring(0, lengthPrefix);
						
						logger.debug(CLASS_NAME + METHOD_NAME + "tempPrefix  ::" + tempPrefix + "::");
						
						if(oldPrefix.equalsIgnoreCase(tempPrefix.trim())){
							logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
							return link.getNativeIdentity();
						}
					}
				}	
			}
		}
		
		/**
		 * To check provisioning project from lcm or no, if not from lcm, this method should not called, start
		 * */
		
		boolean isIncrement = false;
		
		logger.debug(METHOD_NAME + CLASS_NAME + project.toXml());
		
		ProvisioningPlan plan = project.getMasterPlan();
		
		if(plan != null){
			
			@SuppressWarnings("deprecation")
			AccountRequest planAccReq = plan.getAccountRequest("IIQ");
			
			logger.debug(CLASS_NAME + METHOD_NAME + " get Master Plan IIQ Account Request");
			
			if(planAccReq != null){
				
				Attributes attrAccReq = planAccReq.getArguments();
				
				logger.debug(CLASS_NAME + METHOD_NAME + " get IIQ Account Request Arguments");
				
				if(attrAccReq != null){
					
					Map attrAccReqMap = attrAccReq.getMap();
					
					logger.debug(CLASS_NAME + METHOD_NAME + " get IIQ Account Request Arguments Map");
					
					if(attrAccReq != null && attrAccReqMap.get("interface") != null && "LCM".equalsIgnoreCase((String)attrAccReqMap.get("interface"))){
						isIncrement = true;
					}
				}
			}
		}
		
		String sequenceObject = "BCA_CCR_Sequence";
		
		String val = "";
		
		String prefix = "CC";
		
		logger.debug(METHOD_NAME + " preparation to get Role Request");
		
		String branchCode = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code: " + branchCode);
		
		// Checking if the branch code value is null or blank...
		if (branchCode == null || branchCode.equalsIgnoreCase("")){
			logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code for this identity is empty.. Please provide branchCode to generate  USER ID....");
			return val;
		}
		
		val = AppsGetNextSequence.getNextSequence(context, sequenceObject, prefix, branchCode, isIncrement);
		logger.debug(CLASS_NAME + METHOD_NAME + "Unique USER ID Found: " + val);
		
		return val;
	}
	
}
