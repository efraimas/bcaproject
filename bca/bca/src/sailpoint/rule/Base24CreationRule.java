package sailpoint.rule;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.object.Identity;

public class Base24CreationRule {

	public static Logger logger = Logger
			.getLogger("sailpoint.rule.Base24CreationRule");
	
	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static String generateBase24UserID(SailPointContext context,Identity identity) {

		String METHOD_NAME = "::generateBase24UserID::";
		String userID = "";
		logger.debug(METHOD_NAME + "Inside...");

		return userID;

	}

}
