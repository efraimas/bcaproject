package sailpoint.provisioningpolicy.rule;

import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.BCAApproverMatrixConstant;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.ApprovalItem;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class EmailEscalation {
	public static String CLASS_NAME = "::EmailEscalation::";
	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.EmailEscalation");
	
	public static  boolean GetAppCh1(SailPointContext context, ApprovalItem item) throws GeneralException{
		
		Map approverMap = null;
		boolean newOwner=item.getApprover() != null ;
			
		Attributes attrItem = item.getAttributes();
		Map attrMap = attrItem.getMap();
		
		String operation = (String)attrMap.get("flow");
		
		logger.debug("flow operation : " + operation);
		
		if (approverMap(approverMap).get(BCAApproverMatrixConstant.APPROVER2_KEY) != null) {
			if (!approverMap(approverMap).get(BCAApproverMatrixConstant.APPROVER2_KEY)
					.toString().equalsIgnoreCase("")) {
				
				if(operation.equalsIgnoreCase("AccessRequest")){
					logger.debug("approver2 empid : " + item.getOwner().toString());
					Identity ident = CommonUtil.searchIdentity(context, IdentityAttribute.EMPLOYEE_ID, item.getOwner().toString());
					logger.debug("identity manager : " + ident.getManager() + ident.getManager().toXml());
					
					Identity identity = ident.getManager();
					
					newOwner = identity.getName() != null;
					
				} else {
					Identity identity = (Identity) approverMap(approverMap)
							.get(BCAApproverMatrixConstant.CHECKER1_KEY);
					
					newOwner = identity.getName() != null;
				}
				

				//newOwner = identity.getDisplayName().toString().equalsIgnoreCase("19914267");
			}
		}
		
		if(newOwner != true || newOwner == false){
			 
			 newOwner =item.getOwner().toString().equalsIgnoreCase("19914267");
					  // && item.getOwner() == null;
		}
		return newOwner;
		}


	private static Map approverMap(Map approverMap) {
		return approverMap;
	}
	}