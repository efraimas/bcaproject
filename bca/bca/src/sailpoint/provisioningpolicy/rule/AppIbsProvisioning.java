package sailpoint.provisioningpolicy.rule;

import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class AppIbsProvisioning {

	public static String CLASS_NAME = "::AppIbsProvisioning::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.AppIbsProvisioning");

	public static Object getUser_ID_IbsOperator(SailPointContext context, Identity identity, ProvisioningProject project) throws GeneralException{
		
		String METHOD_NAME = "::getUser_ID_IbsOperator::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		/*
		 * Ini untuk cek, apakah identity ini punya account yang sejenis
		 * */
		
		/*List links  = identity.getLinks();
		
		if(links != null && links.size() > 0){
			
			Iterator it = links.iterator();
			
			while(it.hasNext()){
				Link link = (Link)it.next();
				
				if("IBM MAINFRAME RACF".equalsIgnoreCase(link.getApplicationName())){
					String userId = link.getNativeIdentity();
					logger.debug(CLASS_NAME + METHOD_NAME + "MAINFRAME link's found, with userid  : " + userId);
					
					if(userId.substring(4, 5).equalsIgnoreCase("6") && userId.substring(7, 8).equalsIgnoreCase("G") && AccountSelectorRule.isNumber(userId.substring(1,7))==true){
						logger.debug(CLASS_NAME + METHOD_NAME + "match with userid  ::" + link.getNativeIdentity() + "::");
						return link.getNativeIdentity();
					}
				}	
			}
		}*/
		
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
		
		String sequenceObject = "BCA_OPE_Sequence";
		
		String val = "";
		
		String prefix = "OPE";
		
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
	
	
	public static Object getUser_ID_IbsIls(SailPointContext context, Identity identity, ProvisioningProject project) throws GeneralException{
		
		String METHOD_NAME = "::getUser_ID_IbsIls::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
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
		
		String sequenceObject = "BCA_CxxxyyyT_Sequence";
		
		String val = "";
		
		String prefix = "ILS";
		
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
	
	
	public static Object getUser_ID_IbsOr(SailPointContext context, Identity identity, ProvisioningProject project) throws GeneralException{
		
		String METHOD_NAME = "::getUser_ID_IbsOr::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
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
		
		String sequenceObject = "IDS Service Type - Final Letter S";
		
		String val = "";
		
		String prefix = "OR";
		
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
