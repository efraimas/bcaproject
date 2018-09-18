package sailpoint.lcm.event.rule;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Identity;

public class IdentitySelectorRule {
	
	public static Logger logger = Logger
			.getLogger("sailpoint.lcm.event.rule.IdentitySelectorRule");
	
	static String className = "::IdentitySelectorRule::";
	
	public static boolean isHrRegionOfficeEmployee(SailPointContext context, Identity employee){
		
		String methodName = "::isHrRegionOfficeEmployee::";
		
		logger.debug(className + methodName + " enter this method");
		
		logger.debug(className + methodName + " employee id " + employee.getAttribute(IdentityAttribute.EMPLOYEE_ID));
		
		logger.debug(className + methodName + " branch code " + employee.getAttribute(IdentityAttribute.BRANCH_CODE));
		
		logger.debug(className + methodName + " region code " + employee.getAttribute(IdentityAttribute.REGION_CODE));
		
		boolean isHrRegionEmployee = false;
		
		String positionCodeSdm = "00000985";
			
		if(CommonUtil.isRegionalBranchCode((String)employee.getAttribute(IdentityAttribute.BRANCH_CODE), (String)employee.getAttribute(IdentityAttribute.REGION_CODE))){
			isHrRegionEmployee = positionCodeSdm.equalsIgnoreCase((String)employee.getAttribute(IdentityAttribute.POSITION_CODE));
			
			logger.debug(className + methodName + " is region hr staff " + isHrRegionEmployee);
		}	
		
		return isHrRegionEmployee;
		
	}

}
