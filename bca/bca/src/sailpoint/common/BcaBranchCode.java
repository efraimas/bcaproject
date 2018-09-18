package sailpoint.common;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;

public class BcaBranchCode {
	
	public static String CLASS_NAME = "::BcaBranchCode::";
	public static Logger logger = Logger.getLogger("sailpoint.provisioningpolicy.rule.BcaBranchCode");
	
	public static String getUserIDFromBranchCode(SailPointContext context, String branchCode) {
		String METHOD_NAME = "::getUserIDFromBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String val = "";
		String prefix = "";

		if (branchCode == null || branchCode.equalsIgnoreCase("")) {
			logger.error(CLASS_NAME + METHOD_NAME + "Branch Code is Either empty or null....");
		}

		if (branchCode.length() > 4) {
			logger.error(CLASS_NAME + METHOD_NAME + "Branch Code Length More than 4 hence cannot process... ");
			return val;
		}

		int branchCodeInt = Integer.parseInt(branchCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code: " + branchCodeInt);

		if (branchCodeInt >= 1 && branchCodeInt <= 499) {
			val = branchCode.substring((branchCode.length() - 3), branchCode.length());
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: " + val);
			return val;
			
		} else if (branchCodeInt >= 5000 && branchCodeInt <= 5999) {
			val = branchCode.substring((branchCode.length() - 4), branchCode.length() - 1);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "	+ val);
			return val;
			
		} else if (branchCodeInt >= 6000 && branchCodeInt <= 6999) {
			val = branchCode.substring((branchCode.length() - 4), branchCode.length() - 1);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "	+ val);
			return val;
			
		} else if (branchCodeInt >= 7000 && branchCodeInt <= 7999) {
			val = branchCode.substring((branchCode.length() - 4), branchCode.length() - 1);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "	+ val);
			return val;
			
		} else if (branchCodeInt >= 8000 && branchCodeInt <= 8999) {
			val = branchCode.substring((branchCode.length() - 4), branchCode.length() - 1);
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "	+ val);
			return val;
			
		} else if (branchCodeInt >= 900 && branchCodeInt <= 999) {
			val = branchCode.substring((branchCode.length() - 3), branchCode.length());
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "	+ val);
			return val;
		}

		// Getting only the last 3 character of branch code...
		prefix = branchCode.substring((branchCode.length() - 3), branchCode.length());
		logger.debug(CLASS_NAME + METHOD_NAME + "Prefix Found: " + prefix);

		return val;

	}

}
