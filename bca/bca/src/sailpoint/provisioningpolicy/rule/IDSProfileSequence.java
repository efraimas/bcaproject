package sailpoint.provisioningpolicy.rule;

import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes" })
public class IDSProfileSequence {
	
	public static String CLASS_NAME = "::IDSProfileSequence::";

	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.IDSProfileSequence");
	
	private String suffixSeq = " - SEQ";
	
	private String maxSeqKey = "maxseq";
	
	private String minSeqKey = "minSeq";
	
	private String suffixBranchSeq = "_nextseq";
	
	public String getNextSequence(SailPointContext context, String profile, String branchCode) throws GeneralException{
		
		String methodName = "getNextSequence";
		
		String customObjectName = profile + suffixSeq;
		
		Custom obj = CommonUtil.getCustomObject(context, customObjectName);
		
		if(obj == null){
			logger.debug(CLASS_NAME + methodName + " custom object with name" + customObjectName + " is not found");
			return "";
		}
		
		Attributes attribute = obj.getAttributes();
		
		if(attribute != null){
			Map seqMap = attribute.getMap();
			
			if(seqMap != null && seqMap.get((String)maxSeqKey) != null && seqMap.get((String)minSeqKey) != null){
				
				int defVal = Integer.parseInt(seqMap.get((String)minSeqKey).toString());
				
				int maxVal = Integer.parseInt(seqMap.get((String)maxSeqKey).toString());
				
				int nextVal = defVal;
				
				String branchKey = branchCode + suffixBranchSeq;
				
				if(seqMap.get((String)branchKey) != null){
					
					nextVal = Integer.parseInt(seqMap.get((String)branchKey).toString());			
				}
				
			}
		
		}
		
		
		
		return "";
	}

}
