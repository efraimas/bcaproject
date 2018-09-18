package sailpoint.provisioningpolicy.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.RACFAttribute;
import sailpoint.common.RACFProfile;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class ServiceTypeIDSProvisioning {
	
	public static String CLASS_NAME = "::ServiceTypeIDSProvisioning::";
	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.ServiceTypeIDSProvisioning");
	
	public static String getNextSequence(SailPointContext context, String customObjectName, String prefix, String suffixId, boolean isIncrement, String profileName) throws GeneralException{
		
		int counter = 1;
		
		String METHOD_NAME = "getNextSequence";
		
		String id = "";
		
		boolean noSpaceId = true;
		
		logger.debug(CLASS_NAME + METHOD_NAME + " entering the method");
		
		Custom custom = CommonUtil.getCustomObject(context, customObjectName);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " get custom object with name : " + customObjectName);
		
		Attributes attr = custom.getAttributes();
		
		logger.debug(CLASS_NAME + METHOD_NAME + " get attributes");
		
		Map seqMap = new HashMap();
		
		seqMap = attr.getMap();
		
		logger.debug(CLASS_NAME + METHOD_NAME + " prefix " + prefix + " and suffix " + suffixId);
		
		String branchCode = prefix.substring(0, 4);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " branch code " + branchCode);
		
		int rangeType = 1; 
		
		String keyNextVal = branchCode + "_" + suffixId + "next";
		//String keyNextKL = branchCode + "_" + "next";
		logger.debug(CLASS_NAME + METHOD_NAME + "range type : " + rangeType);
		logger.debug(CLASS_NAME + METHOD_NAME + "seqmap :" + seqMap);
		
			logger.debug(CLASS_NAME + METHOD_NAME + ">>> Starting to processing sequence ... ");
	
			int minVal = getMinValue(seqMap, suffixId, rangeType);
			int maxVal = getMaxValue(seqMap, suffixId, rangeType);
			
			int nextVal = minVal;
			
			if(seqMap.get((String)keyNextVal) != null){
				nextVal = Integer.parseInt((String)seqMap.get((String)keyNextVal));
			}
			
			//while(rangeType <= 100)
			for(int j = nextVal; j<=maxVal; j++){//assume the max range type is 100
				
			logger.debug(CLASS_NAME + METHOD_NAME + "minVal:" + minVal + ", maxVal:" + maxVal + ", nextVal:" + nextVal);
			boolean isUnique = false;
			
			if("K".equalsIgnoreCase(suffixId) || "L".equalsIgnoreCase(suffixId) || "M".equalsIgnoreCase(suffixId)){
				
				String otherSuffix = "K".equalsIgnoreCase(suffixId) ? "L" : "K";
				
				id = prefix + paddingSeq(nextVal) + suffixId;
				
				String idOther = prefix + paddingSeq(nextVal) + otherSuffix;
				
				logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id + " and other " + idOther);
				
				isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id) && 
						CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, idOther);
				
			}
			else if("S".equalsIgnoreCase(suffixId)){
					if(profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SG)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_ST)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SD)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STG)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SGD)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STD)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGD)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SCSO)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGDC)
						|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGC)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SG)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_ST)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SD)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STG)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SGD)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STD)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGD)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SCSO)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGDC)
						|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGC)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SG)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_ST)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SD)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STG)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SGD)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STD)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGD)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SCSO)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGDC)
						|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGC)
						|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110)
						
						|| RACFProfile.SUPERVISOR_ST.equalsIgnoreCase(profileName) 
						|| RACFProfile.SUPERVISOR_SD.equalsIgnoreCase(profileName) 
						|| RACFProfile.SUPERVISOR_STG.equalsIgnoreCase(profileName) 
						|| RACFProfile.SUPERVISOR_SGD.equalsIgnoreCase(profileName) 
						|| RACFProfile.SUPERVISOR_STD.equalsIgnoreCase(profileName) 
						|| RACFProfile.SUPERVISOR_STGD.equalsIgnoreCase(profileName) 
						|| RACFProfile.SUPERVISOR_SCSO.equalsIgnoreCase(profileName) 
						|| RACFProfile.SUPERVISOR_STGDC.equalsIgnoreCase(profileName) 
						|| RACFProfile.SUPERVISOR_STGC.equalsIgnoreCase(profileName)
						|| RACFProfile.SUPERVISOR_SG.equalsIgnoreCase(profileName)
						|| RACFProfile.IDS_REGULER_SUPERVISOR_SG.equalsIgnoreCase(profileName)
						){
						
							id = prefix + paddingSeq(nextVal) + suffixId;
							logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id);
							
					}
					else
							id = prefix + "8" + paddingSeqSupervisor(nextVal) + suffixId;
							
							logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id);
							
							isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id);
			}
			// Ditambahkan untuk id T satu sequence
			else if ("T".contains(suffixId)){
				id = prefix + paddingSeq(nextVal) + suffixId;
				
				logger.debug(CLASS_NAME + METHOD_NAME + "temp id " + id);
				
				isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id);
			}			
						//end
			else if(profileName.equalsIgnoreCase(RACFProfile.OPERATOR_DBO) 
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_DFBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GDBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GDFBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GFBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TDBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TFBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGDBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGDFBO)
					|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGFBO)
					|| profileName.equalsIgnoreCase(RACFProfile.CASH_VAULT_CV)){
				id = prefix + paddingseq2digit(nextVal) + suffixId;
				
				logger.debug(CLASS_NAME + METHOD_NAME + "temp id " + id);
				
				isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id);
			}
			else{
				id = prefix + paddingSeq(nextVal) + suffixId; // test
				
				logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id);
				
				isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id);
			}
			
			
			
			
			if(isUnique){
				if(isIncrement){
					if((nextVal + 1) > maxVal){
						seqMap.put(keyNextVal, String.valueOf(minVal));
						counter++; // ini sebelumnya counter
						if(counter> 2) throw new GeneralException("SEQUENCE IS ALREADY FULL");
					}
					else if (id.contains("K") || id.contains("L") || id.contains("M")){ // this for Increment user ID K and L in one sequence
						seqMap.put(branchCode + "_K" + "next", String.valueOf(nextVal + 1));
						seqMap.put(branchCode + "_L" + "next", String.valueOf(nextVal + 1));	
						seqMap.put(branchCode + "_M" + "next", String.valueOf(nextVal + 1));
					}
					else
						seqMap.put(keyNextVal, String.valueOf((nextVal + 1)));
				}
			
			
					
				
				
				Map seqMapToSave = new HashMap();
				
				attr.putAll(seqMap);
				
				custom.setAttributes(attr);
				
				//CommonUtil.updateCustomObject(context, custom);
				
				logger.debug(CLASS_NAME + METHOD_NAME + " Map : " + seqMap);
				
				logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id + " is unique");
				
				noSpaceId = false;
				
				return id;
				
			}else{
				logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id + " is already used");
			}
			
			nextVal++;
			
			logger.debug(CLASS_NAME + METHOD_NAME + "minVal:" + minVal + ", maxVal:" + maxVal + ", nextVal:" + nextVal);
			
			if(nextVal > maxVal)
				nextVal = minVal;
			
			logger.debug(CLASS_NAME + METHOD_NAME + "minVal:" + minVal + ", maxVal:" + maxVal + ", nextVal:" + nextVal);
			
			boolean achieveMax = false;
			
			for(int i = nextVal; i<=maxVal; i++){
				
				nextVal = i;
				
				if("K".equalsIgnoreCase(suffixId) || "L".equalsIgnoreCase(suffixId)){
					
					String otherSuffix = "K".equalsIgnoreCase(suffixId) ? "L" : "K";
					
					id = prefix + paddingSeq(nextVal) + suffixId;
					
					String idOther = prefix + paddingSeq(nextVal) + otherSuffix;
					
					logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id + " and other " + idOther);
					
					isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id) && 
							CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, idOther);
				}
				else if("S".equalsIgnoreCase(suffixId)){
					if(profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SG)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_ST)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SD)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STG)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SGD)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STD)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGD)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SCSO)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGDC)
							|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGC)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SG)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_ST)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SD)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STG)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SGD)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STD)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGD)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SCSO)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGDC)
							|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGC)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SG)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_ST)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SD)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STG)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SGD)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STD)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGD)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SCSO)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGDC)
							|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGC)
							|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110)
							
							|| RACFProfile.SUPERVISOR_ST.equalsIgnoreCase(profileName) 
							|| RACFProfile.SUPERVISOR_SD.equalsIgnoreCase(profileName) 
							|| RACFProfile.SUPERVISOR_STG.equalsIgnoreCase(profileName) 
							|| RACFProfile.SUPERVISOR_SGD.equalsIgnoreCase(profileName) 
							|| RACFProfile.SUPERVISOR_STD.equalsIgnoreCase(profileName) 
							|| RACFProfile.SUPERVISOR_STGD.equalsIgnoreCase(profileName) 
							|| RACFProfile.SUPERVISOR_SCSO.equalsIgnoreCase(profileName) 
							|| RACFProfile.SUPERVISOR_STGDC.equalsIgnoreCase(profileName) 
							|| RACFProfile.SUPERVISOR_STGC.equalsIgnoreCase(profileName)
							|| RACFProfile.SUPERVISOR_SG.equalsIgnoreCase(profileName)
							|| RACFProfile.IDS_REGULER_SUPERVISOR_SG.equalsIgnoreCase(profileName)
							){
							
								id = prefix + paddingSeq(nextVal) + suffixId;
					}
					else
					id = prefix + "8" + paddingSeqSupervisor(nextVal) + suffixId;
					
					logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id);
					
					isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id);
				}
				// Ditambahkan untuk id T satu sequence
				else if (suffixId.contains("T")){
					id = prefix + paddingSeq(nextVal) + suffixId;
					
					logger.debug(CLASS_NAME + METHOD_NAME + "temp id " + id);
					
					isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id);
				}
				//end
				else if(profileName.equalsIgnoreCase(RACFProfile.OPERATOR_DBO) 
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_DFBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GDBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GDFBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GFBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TDBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TFBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGDBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGDFBO)
						|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGFBO)
						|| profileName.equalsIgnoreCase(RACFProfile.CASH_VAULT_CV)){
					id = prefix + paddingseq2digit(nextVal) + suffixId;
					
					logger.debug(CLASS_NAME + METHOD_NAME + "temp id " + id);
					
					isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id);
				}
				else{
					id = prefix + paddingSeq(nextVal) + suffixId;  //test
					
					logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id);
					
					isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, CommonUtil.RACF_APPLICATION_NAME, RACFAttribute.USER_ID, id);
				}
				
						
				if(isUnique){
					if(isIncrement){
						if((nextVal + 1) > maxVal)
							seqMap.put(keyNextVal, String.valueOf(minVal));
						counter++;
						if(counter> 2) throw new GeneralException("SEQUENCE IS ALREADY FULL");
						else if (id.contains("K") || id.contains("L") || id.contains("M")){ // this for Increment user ID K and L in one sequence
							seqMap.put(branchCode + "_K" + "next", String.valueOf(nextVal + 1));
							seqMap.put(branchCode + "_L" + "next", String.valueOf(nextVal + 1));	
							seqMap.put(branchCode + "_M" + "next", String.valueOf(nextVal + 1));
						}
						else
							seqMap.put(keyNextVal, String.valueOf((nextVal + 1)));
					}
					
					attr.putAll(seqMap);
					
					custom.setAttributes(attr);
					
					logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id + " is unique");
					
					noSpaceId = false;
					
					
					return id;
				}else 
				{
					logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id + " is already used");
				
				}
				
				if(!achieveMax && i == maxVal){
					logger.debug(CLASS_NAME + METHOD_NAME + "Inside Perulangan .... pertama");
					int otherminVal = getMinValue(seqMap, suffixId, rangeType + 1);
					int othermaxVal = getMaxValue(seqMap, suffixId, rangeType + 1);
					logger.debug(CLASS_NAME + METHOD_NAME + "other minimum : " + otherminVal + " other maximun : " + othermaxVal);
					i = otherminVal;
					maxVal = othermaxVal;
					achieveMax = true;
				}
							
			}	
			if(j == maxVal){
				logger.debug(CLASS_NAME + METHOD_NAME + "Inside Perulangan .... pertama");
				int otherminVal = getMinValue(seqMap, suffixId, rangeType + 1);
				int othermaxVal = getMaxValue(seqMap, suffixId, rangeType + 1);
				logger.debug(CLASS_NAME + METHOD_NAME + "other minimum : " + otherminVal + " other maximun : " + othermaxVal);
				j = otherminVal;
				maxVal = othermaxVal;
			}
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + " tidak ada id yang tersedia");
		
		return "";
	}
	
	private static int getMinValue(Map map, String type, int rangeType){
		String keyMin = "minseq" + type + rangeType;
		return Integer.parseInt((String)map.get((String)keyMin));
	}
	
	private static int getMaxValue(Map map, String type, int rangeType){
		String keyMax = "maxseq" + type + rangeType;
		
		return Integer.parseInt((String)map.get((String)keyMax));
	}
	
	private static String paddingseq2digit(int seq){
		if(seq < 10)
			return "0" + seq ;
		else
			return String.valueOf(seq);
	}
	
	private static String paddingseq2digit(String seq){
		return paddingseq2digit(Integer.parseInt(seq));
	}
	
	private static String paddingSeqSupervisor(int seq){
		if(seq < 10)
			return "0" + seq;
		else 
			return String.valueOf(seq);
	}
	
	private static String paddingSeqSupervisor(String seq){
		return paddingSeqSupervisor(Integer.parseInt(seq));
	}
	
	private static String paddingSeq(int seq){
		if(seq < 10)
			return "00" + seq;
		else if(seq < 100)
			return "0" + seq;
		else 
			return String.valueOf(seq);
	}
	
	private static String paddingSeq(String seq){
		return paddingSeq(Integer.parseInt(seq));
	}
	
}
