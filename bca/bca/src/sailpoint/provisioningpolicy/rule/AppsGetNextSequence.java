package sailpoint.provisioningpolicy.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.BcaBranchCode;
import sailpoint.common.CommonUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class AppsGetNextSequence {
	
	public static String CLASS_NAME = "::AppsGetNextSequence::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.AppsGetNextSequence");
	
	public static String getNextSequence(SailPointContext context, String sequenceObject, String prefix, String branchCode, boolean isIncrement) 
   		 throws GeneralException{
		
		String METHOD_NAME = "getNextSequence";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String userId = "";
		
		Custom custom = CommonUtil.getCustomObject(context, sequenceObject);
		
		Attributes attr = custom.getAttributes();
		
		Map seqMap = new HashMap();
		
		seqMap = attr.getMap();
		
		logger.debug(CLASS_NAME + METHOD_NAME + " prefix " + prefix);
		logger.debug(CLASS_NAME + METHOD_NAME + " seqMap : " + seqMap);
		logger.debug(CLASS_NAME + METHOD_NAME + " branch code " + branchCode);
		
		int rangeType = 1; 
		String keyNextVal = "";
		
		if(prefix=="ATMREK" || prefix=="BTITS"){
			keyNextVal = prefix + "_next";
		}else if(prefix=="ILS"){
			keyNextVal = RACFProvisioning.getUserIDFromBranchCode(context, branchCode) + "_Tnext";
		}else if(prefix=="OR"){
			keyNextVal = RACFProvisioning.getUserIDFromBranchCode(context, branchCode) + "_Snext";
		}else if(prefix=="OPE"){
			keyNextVal = RACFProvisioning.getUserIDFromBranchCode(context, branchCode) + "_next";
		}else{
			keyNextVal = prefix + "_" + branchCode + "_next";
		}		
		
		while(rangeType <100){//assume the max range type is 100
			
			int minVal = getMinValue(prefix, seqMap);
			int maxVal = getMaxValue(prefix, seqMap);
			int nextVal = minVal;
			
			if(seqMap.get((String)keyNextVal) != null){
				nextVal = Integer.parseInt((String)seqMap.get((String)keyNextVal));
			}
			logger.debug(CLASS_NAME + METHOD_NAME + "minVal:" + minVal + ", maxVal:" + maxVal + ", nextVal:" + nextVal);
			
			//untuk menentukan branchCode 4 digit atau 3 digit NB: Didalam if adalah 4 digit branchCode
			if(prefix!="BT" && prefix!="BG" && prefix!="BP" && prefix!="CC" 
					&& prefix!="T" && prefix!="GL" && prefix !="K" && prefix !="L"
					&& prefix!="R" && prefix!="S" && prefix!="SD"){
				branchCode = BcaBranchCode.getUserIDFromBranchCode(context, branchCode);
			}
			
			if(prefix=="OPE"){
				branchCode = keyNextVal.substring(0, keyNextVal.length()-5);
				logger.debug(CLASS_NAME + METHOD_NAME + "branchCode: " + branchCode);
			}
			
			if(prefix=="ILS" || prefix=="OR"){
				branchCode = keyNextVal.substring(0, keyNextVal.length()-6);
				logger.debug(CLASS_NAME + METHOD_NAME + "branchCode: " + branchCode);
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + "branchCode: " + branchCode);
			
			if(prefix=="ATMREK" || prefix=="BTITS"){
				userId = prefix + paddingSeq(nextVal);
			}else if(prefix=="BTTEMP" || prefix=="BTTABLE"){
				userId = prefix + paddingOneSeq(nextVal);
			}else if(prefix=="OPE"){
				userId = branchCode + "6" + paddingSeq(nextVal) + "G";
			}else if(prefix=="OR"){
				userId = branchCode + paddingThreeSeq(nextVal) + "S";
			}else if(prefix=="ILS"){
				userId = branchCode + paddingThreeSeq(nextVal) + "T";
			}else{
				userId = prefix + branchCode + paddingSeq(nextVal);
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + "temp userId: " + userId);
			
			boolean isUnique = false;
			
			isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, "IBM MAINFRAME RACF", "USER_ID", userId);
			
			if(isUnique){
				if(isIncrement){
					if((nextVal + 1) > maxVal)
						seqMap.put(keyNextVal, String.valueOf(minVal));
					else
						seqMap.put(keyNextVal, String.valueOf((nextVal + 1)));
				}
				
				Map seqMapToSave = new HashMap();
				attr.putAll(seqMap);
				custom.setAttributes(attr);
				logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + userId + " is unique");
				
				return userId;
				
			}else{
				logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + userId + " is already used");
			}
			
			nextVal++;
			
			logger.debug(CLASS_NAME + METHOD_NAME + "minVal:" + minVal + ", maxVal:" + maxVal + ", nextVal:" + nextVal);
			
			if(nextVal > maxVal)
				nextVal = minVal;
			
			logger.debug(CLASS_NAME + METHOD_NAME + "minVal:" + minVal + ", maxVal:" + maxVal + ", nextVal:" + nextVal);
			
			boolean achieveMax = false;
			
			for(int i = nextVal; i<=maxVal; i++){
				
				if(prefix=="ATMREK" || prefix=="BTITS"){
					userId = prefix + paddingSeq(i);
				}else if(prefix=="BTTEMP" || prefix=="BTTABLE"){
					userId = prefix + paddingOneSeq(i);
				}else if(prefix=="OPE"){
					userId = branchCode + "6" + paddingSeq(i) + "G";
				}else if(prefix=="OR"){
					userId = branchCode + paddingThreeSeq(i) + "S";
				}else if(prefix=="ILS"){
					userId = branchCode + paddingThreeSeq(i) + "T";
				}else{
					userId = prefix + branchCode + paddingSeq(i);
				}
				
				logger.debug(CLASS_NAME + METHOD_NAME + "New temp userId: " + userId);
				
				isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, "IBM MAINFRAME RACF", "USER_ID", userId);
				
				if(isUnique){
					nextVal = i;
					if(isIncrement){
						if((nextVal + 1) > maxVal)
							seqMap.put(keyNextVal, String.valueOf(minVal));
						else
							seqMap.put(keyNextVal, String.valueOf((nextVal + 1)));
					}
					
					attr.putAll(seqMap);
					custom.setAttributes(attr);
					
					logger.debug(CLASS_NAME + METHOD_NAME + "New temp userId: " + userId + " is unique");
					
					return userId;
					
				}else{
					logger.debug(CLASS_NAME + METHOD_NAME + "New temp userId: " + userId + " is already used");
				}
				
				if(!achieveMax && i == maxVal){
					i = minVal;
					achieveMax = true;
				}		
			}		
		}
		
		logger.debug(CLASS_NAME + METHOD_NAME + " tidak ada id yang tersedia");
		
		return "";
	}
	
	private static int getMinValue(String prefix, Map map){
		String keyMin = prefix + "_minseq";
		return Integer.parseInt((String)map.get((String)keyMin));
	}
	
	private static int getMaxValue(String prefix, Map map){
		String keyMax = prefix + "_maxseq";

		
		return Integer.parseInt((String)map.get((String)keyMax));
	}
	
	private static String paddingSeq(int seq){
		if(seq < 10)
			return "0" + seq; 
		else if(seq < 100)
			return "" + seq;
		else 
			return String.valueOf(seq);
	}
	
	private static String paddingOneSeq(int seq){
		if(seq < 10)
			return "" + seq; 
		else 
			return String.valueOf(seq);
	}
	
	private static String paddingThreeSeq(int seq){
		if(seq < 10)
			return "00" + seq; 
		else if(seq < 100)
			return "0" + seq;
		else 
			return String.valueOf(seq);
	}
   

}
