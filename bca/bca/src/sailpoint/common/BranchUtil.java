package sailpoint.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class BranchUtil {
	
	static String className = "::BranchUtil::";
	
	public static Logger logger = Logger.getLogger("sailpoint.common.BranchUtil");
	
	public static List getAllBranchCode(SailPointContext context) throws GeneralException{
		
		String methodName = "::getAllBranchCode::";
		
		List returnList = null;
		
		logger.debug(className + methodName + " get custom object with name " + CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT);
		
		Custom branchCustomObj = CommonUtil.getCustomObject(context, CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT);
		
		Attributes attrCustom = branchCustomObj.getAttributes();
		
		List attrList = (List)attrCustom.get("branchTable");
		
		if(attrList != null){
			
			logger.debug(className + methodName + " custom object branch is not null");
			
			returnList = new ArrayList();			
			Iterator it = attrList.iterator();
			
			while(it.hasNext()){
				
				Map branchMap = (Map)it.next();
				
				String branchCode = (String)branchMap.get("subBranchCode");
				
				returnList.add(branchCode);
				
			}
			
		}
		
		return returnList;
	}
	
	public static String getBranchType(SailPointContext context, String branchCode) throws GeneralException{
		
		String methodName = "::getBranchType::";
		
		String branchType = "";
		
		if(CommonUtil.HQ_BRANCH_CODE.equalsIgnoreCase(branchCode)){
			branchType = "KP";
		}else if(CommonUtil.isRegionalBranchCode(context, branchCode)){
			branchType = "Region";
		}else{
			branchType = CommonUtil.getBranchType(context, branchCode);
		}
		
		return branchType;
		
	}

}
