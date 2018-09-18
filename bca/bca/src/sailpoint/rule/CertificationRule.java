package sailpoint.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.object.CertificationEntity;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CertificationRule {
	
	static String className = "::CertificationRule::";
	
	public static Logger logger = Logger
			.getLogger("sailpoint.rule.CertificationRule");
	
	public static Map preDelegationRule(SailPointContext ctx, CertificationEntity entity) throws GeneralException{
		
		String methodName = "::preDelegationRule::";
		
		Map results = new HashMap();
		
		logger.debug(className + methodName + " preparation to get entity");
		
		String empId = entity.getIdentity();
		
		logger.debug(className + methodName + " preparation to get manager with employee id : " + empId);
		
		String delegatedId = "spadmin";
			
		logger.debug(className + methodName + " get employee");
		
		Identity employee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.DISPLAY_NAME, empId);
		
		logger.debug(className + methodName + " get manager");
		
		Identity manager = employee.getManager();
		
		if(manager != null){
			
			logger.debug(className + methodName + " set manager with display name " + manager.getDisplayName());
			
			delegatedId = manager.getDisplayName();
		}
		
		logger.debug(className + methodName + " put recipient name");
		
		results.put("recipientName", delegatedId);
		
		logger.debug(className + methodName + " certification for employee " + empId + " will certified by " + delegatedId);
		
		results.put("description", "Please certify your direct subordinate");
		
		results.put("comments", "Berikut adalah account yang dimiliki oleh subordinat anda. Terima Kasih");
		
		return results;
		
	}
	
	

	
}
