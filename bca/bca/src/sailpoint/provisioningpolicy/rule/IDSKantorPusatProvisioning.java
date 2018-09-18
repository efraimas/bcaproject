package sailpoint.provisioningpolicy.rule;

import java.util.Map;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.RACFProfile;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class IDSKantorPusatProvisioning {
	
	public static String CLASS_NAME = "::IDSKantorPusatProvisioning::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.IDSKantorPusatProvisioning");
	
	@SuppressWarnings("finally")
	public static Object getFV_IBM_MAINFRAME_RACF_USER_ID_Rule(
			SailPointContext context, Identity identity, String op,
			String profileName, ProvisioningProject project) throws GeneralException {

		String METHOD_NAME = "::getFV_IBM_MAINFRAME_RACF_USER_ID_Rule::";
		logger.trace(CLASS_NAME + METHOD_NAME + "Inside...");

		logger.debug(CLASS_NAME + METHOD_NAME + "Identity Name: "
				+ identity.getDisplayName() + "  profileName: " + profileName
				+ " Operation: " + op);
		
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
		
		String customObjectName = "";
		String branchCode = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code: " + branchCode);
		
		
		if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0986 == profileName.trim() && branchCode.contains("0986") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_9650 == profileName.trim() && branchCode.contains("9650") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0987 == profileName.trim() && branchCode.contains("0987") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0973 == profileName.trim() && branchCode.contains("0973") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0970 == profileName.trim() && branchCode.contains("0970") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0969 == profileName.trim() && branchCode.contains("0969") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0968 == profileName.trim() && branchCode.contains("0968") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0965 == profileName.trim() && branchCode.contains("0965") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0958 == profileName.trim() && branchCode.contains("0958") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0959 == profileName.trim() && branchCode.contains("0959") || branchCode.contains("0998")){
			customObjectName = "BCA Kantor Pusat Sequence";
		}
		else{
			throw new GeneralException("TIDAK DIPERBOLEHKAN REQUEST AKSES INI");
		}
		logger.debug(CLASS_NAME + METHOD_NAME + " customObjectName : " + customObjectName);
	
		String val = "";
		String prefix = "";
		String finalLetter = "";

		if (branchCode == null || branchCode.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "Branch Code for this identity is empty.. Please provide branchCode to generate RACF USER ID....");
			return val;
		}
		
		if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0986 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B986";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_9650 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B650";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0987 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B987";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0973 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B973";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0970 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B970";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0969 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B969";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0968 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B968";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0965 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B965";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0958 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B958";
		}
		else if(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0959 == profileName.trim() && branchCode.contains("0998")){
			prefix = "B959";
		}
		else{
			prefix = RACFProvisioning.getUserIDFromBranchCode(context, branchCode);
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + "Prefix: " + prefix);
		logger.debug(CLASS_NAME + METHOD_NAME + "profileName: " + profileName);
		
		String positionCode = (String)identity.getAttribute(IdentityAttribute.POSITION_CODE);
		logger.debug(CLASS_NAME + METHOD_NAME + " positionCode: " + positionCode);

		finalLetter = RACFProvisioning.getFinalLetterFromRequesterType(context,
				profileName, positionCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "finalLetter: " + finalLetter);
		
		try{
			val = ServiceTypeIDSProvisioning.getNextSequence(context, customObjectName, prefix, finalLetter, isIncrement, profileName);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			logger.debug(CLASS_NAME + METHOD_NAME + "Unique USER ID Found: " + val);

			return val;
		}
	}
}
