package sailpoint.provisioningpolicy.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class IDSNextSequence {
	
	public static String CLASS_NAME = "::MUTServiceTypeIDSProvisioning::";
	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.MUTServiceTypeIDSProvisioning");
	
	public static String getNextSequence(SailPointContext context, String customObjectName, String prefix, String suffixId, boolean isIncrement) throws GeneralException{
		
		String METHOD_NAME = "getNextSequence";
		
		String id = "";
		
		boolean noSpaceId = true;
		
		Custom custom = CommonUtil.getCustomObject(context, customObjectName);
		
		Attributes attr = custom.getAttributes();
		
		Map seqMap = new HashMap();
		
		seqMap = attr.getMap();
		
		logger.debug(CLASS_NAME + METHOD_NAME + " prefix " + prefix + " and suffix " + suffixId);
		
		String branchCode = prefix.substring(1, 4);
		
		logger.debug(CLASS_NAME + METHOD_NAME + " branch code " + branchCode);
		
		int rangeType = 1; 
		
		String keyNextVal = branchCode + "_" + suffixId + "next";
		
		while(rangeType <=100){//assume the max range type is 100
			
			int minVal = getMinValue(seqMap, suffixId, rangeType);
			int maxVal = getMaxValue(seqMap, suffixId, rangeType);
			
			int nextVal = minVal;
			
			if(seqMap.get((String)keyNextVal) != null){
				nextVal = Integer.parseInt((String)seqMap.get((String)keyNextVal));
			}
			
			logger.debug(CLASS_NAME + METHOD_NAME + "minVal:" + minVal + ", maxVal:" + maxVal + ", nextVal:" + nextVal);
			
			id = prefix + paddingSeq(nextVal) + suffixId;
			
			logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id);
			
			boolean isUnique = false;
			
			isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, "IBM MAINFRAME RACF", "USER_ID", id);
			
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
				
				id = prefix + paddingSeq(i) + suffixId;
				
				logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id);
				
				isUnique = CommonUtil.isUniqueApplicationAttributeValue(context, "IBM MAINFRAME RACF", "USER_ID", id);
				
				if(isUnique){
					
					if(isIncrement){
						if((nextVal + 1) > maxVal)
							seqMap.put(keyNextVal, String.valueOf(minVal));
						else
							seqMap.put(keyNextVal, String.valueOf((nextVal + 1)));
					}
					
					attr.putAll(seqMap);
					
					custom.setAttributes(attr);
					
					logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id + " is unique");
					
					noSpaceId = false;
					
					return id;
				}else{
					logger.debug(CLASS_NAME + METHOD_NAME + " temp id " + id + " is already used");
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
	
	private static int getMinValue(Map map, String type, int rangeType){
		String keyMin = "minseq" + type + rangeType;
		return Integer.parseInt((String)map.get((String)keyMin));
	}
	
	private static int getMaxValue(Map map, String type, int rangeType){
		String keyMax = "maxseq" + type + rangeType;
		
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
	
	private static String paddingSeq(String seq){
		return paddingSeq(Integer.parseInt(seq));
	}

}
