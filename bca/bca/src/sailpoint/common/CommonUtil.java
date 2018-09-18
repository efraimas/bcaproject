package sailpoint.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sailpoint.api.ObjectUtil;
import sailpoint.api.PasswordGenerator;
import sailpoint.api.PersistenceManager;
import sailpoint.api.SailPointContext;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Bundle;
import sailpoint.object.Custom;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.Link;
import sailpoint.object.PasswordPolicy;
import sailpoint.object.QueryOptions;
import sailpoint.server.InternalContext;
import sailpoint.tools.GeneralException;

import com.google.common.collect.Iterators;

@SuppressWarnings({ "unused", "rawtypes", "unchecked", "deprecation" })
public class CommonUtil {

	public static String CLASS_NAME = "::CommonUtil::";
	public static Logger logger = Logger.getLogger("sailpoint.common.CommonUtil");

	// Some of the commonly accepted values....

	public static String BCA_DATE_FORMAT = "yyyyMMdd";
	public static String BCA_AD_PASSWORD_POLICY_NAME = "BCA AD Password Policy";
	public static String RACF_PASSWORD_POLICY = "RACF Password Policy";
	public static String BCA_DEFALUT_PASSWORD_POLICY_NAME = "BCA Default Password Policy";

	public static String HR_APPLICATION = "SAP HR File Feed";
	public static String AD_APPLICATION = "AD BCA";
	public static final String RACF_APPLICATION_NAME = "IBM MAINFRAME RACF";
	public static final String BASE24_FILE_FEED_APPLICATION = "Base24 File Feed";
	public static final String IIQ_APPLICATION = "IIQ";

	// Task Definition Name for Base 24 which will be used as the base task....
	public static final String BCA_BASE24_AGGREGATION = "Aggregate Base24";

	// This will always remains constant unless BCA decide to harden its
	// environment and make SPADMIN invisible.....
	public static final String IIQ_ADMINISTRATOR = "spadmin";

	// HQ Branch Code is always constant... hence marking it as final...
	public static final String HQ_REGION_CODE = "0998";

	public static final String HQ_BRANCH_CODE = "0998";

	// BCA Branch Table column attributes defined here... to make sure everybody
	// is using the same thing....

	public static final String BRANCH_TABLE_POSITION1 = "subBranchCode";
	public static final String BRANCH_TABLE_POSITION2 = "initial";
	public static final String BRANCH_TABLE_POSITION3 = "branchName";
	public static final String BRANCH_TABLE_POSITION4 = "branchCode";
	public static final String BRANCH_TABLE_POSITION5 = "regionCode";
	public static final String BRANCH_TABLE_ENTRY_KEY_VALUE = "branchTable";

	/**
	 * If you wish to check unique attribute IDENTITY value use this common
	 * method...
	 * 
	 * @param context
	 * @param ERN
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isUniqueValue(SailPointContext context, String identityAttribute, String attributeValue)
			throws GeneralException {
		String METHOD_NAME = "::isUniqueValue::";
		boolean isUniqueValue = false;
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		QueryOptions qo = new QueryOptions();
		Filter f = Filter.eq(identityAttribute, attributeValue);
		qo.add(f);
		Iterator identities = context.search(Identity.class, qo);

		int size = Iterators.size(identities);

		if (size > 0) {
			isUniqueValue = false;
			logger.debug(CLASS_NAME + CLASS_NAME + METHOD_NAME + "Returning isUniqueValue: " + isUniqueValue);
			return isUniqueValue;
		} else {
			isUniqueValue = true;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning isUniqueValue: " + isUniqueValue);
			return isUniqueValue;
		}
	}

	/**
	 * This method will return the unique list of value from the non unique
	 * list....
	 * 
	 * @param nonUniqueList
	 * @return
	 */
	public static List getUniqueValueList(List nonUniqueList) {
		String METHOD_NAME = "::getUniqueValueList::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		Set uniqueSet = new HashSet(nonUniqueList);
		List uniqueList = new ArrayList(uniqueSet);
		logger.debug(CLASS_NAME + METHOD_NAME + "Sorted List Only: " + uniqueList);

		return uniqueList;
	}

	/**
	 * Pass the value of the custom object name and get the custom object loaded
	 * into memory...
	 * 
	 * @param context
	 * @param customObjectName
	 * @return
	 * @throws GeneralException
	 */
	public static Custom getCustomObject(SailPointContext context, String customObjectName) throws GeneralException {
		String METHOD_NAME = "::getCustomObject::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		QueryOptions qo = new QueryOptions();

		qo.add(Filter.eq("name", customObjectName));

		Iterator it = context.search(Custom.class, qo);

		if (it == null || !it.hasNext()) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Custom Object Not Found...");
			return null;
		}

		Custom customObject = (Custom) it.next();

		return customObject;

	}

	/**
	 * Method will search for all identities with specific result option and
	 * then will update the custom object with updated data...
	 * 
	 * @param context
	 * @param customObjectName
	 * @param identityAttributeName
	 * @param valueList
	 * @return
	 * @throws GeneralException
	 */

	public static boolean updateCustomObject(SailPointContext context, String customObjectName,
			String identityAttributeName, List valueList) throws GeneralException {
		String METHOD_NAME = "::updateCustomObject::";
		boolean isUpdated = false;
		logger.debug(METHOD_NAME + "Inside...");
		logger.debug(METHOD_NAME + "customObjectName: " + customObjectName);
		logger.debug(METHOD_NAME + "identityAttributeName: " + identityAttributeName);
		logger.debug(METHOD_NAME + "valueList: " + valueList);

		Custom customObject = getCustomObject(context, customObjectName);
		if (customObject == null) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Custom Object: " + customObjectName
					+ " Not Found.. hence trying to create a custom object.......");

			// Trying to create custom object...

			customObject = new Custom();
			customObject.setName(customObjectName);
			// Setting the custom object...
			customObject.put(identityAttributeName, valueList);

		} else {

			Attributes attributes = customObject.getAttributes();
			Map attributeMap = attributes.getMap();
			List existingValueList = (List) attributeMap.get(identityAttributeName);

			Iterator it = valueList.iterator();
			while (it.hasNext()) {
				String value = (String) it.next();
				existingValueList.add(value);
			}

			Collections.copy(existingValueList, valueList);
			List uniqueValueToUpdate = getUniqueValueList(existingValueList);
			logger.debug(METHOD_NAME + "uniqueValueToUpdate: " + uniqueValueToUpdate);

			// Setting the custom object...
			customObject.put(identityAttributeName, uniqueValueToUpdate);

		}

		// Updating custom object into IIQ....
		context.startTransaction();
		context.saveObject(customObject);
		context.commitTransaction();
		context.decache();

		logger.debug(CLASS_NAME + METHOD_NAME + " Custom Object update successfully... : " + customObjectName);
		isUpdated = true;

		return isUpdated;
	}

	/**
	 * Method will search for all identities with specific result option and
	 * then will update the custom object with updated data...
	 * 
	 * @param context
	 * @param customObjectName
	 * @param identityAttributeName
	 * @param valueList
	 * @return
	 * @throws GeneralException
	 */

	public static boolean updateCustomObject(SailPointContext context, String customObjectName, Attributes attribute)
			throws GeneralException {
		String METHOD_NAME = "::updateCustomObject::";
		boolean isUpdated = false;
		logger.debug(METHOD_NAME + "Inside...");
		logger.debug(METHOD_NAME + "customObjectName: " + customObjectName);

		Custom customObject = getCustomObject(context, customObjectName);
		if (customObject == null) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Custom Object: " + customObjectName
					+ " Not Found.. hence trying to create a custom object.......");

			// Trying to create custom object...

			customObject = new Custom();
			customObject.setName(customObjectName);

		}

		customObject.setAttributes(attribute);

		// Updating custom object into IIQ....
		context.startTransaction();
		context.saveObject(customObject);
		context.commitTransaction();
		context.decache();

		logger.debug(CLASS_NAME + METHOD_NAME + " Custom Object update successfully... : " + customObjectName);
		isUpdated = true;

		return isUpdated;
	}

	public static boolean updateCustomObject(SailPointContext context, Custom customObject) throws GeneralException {
		String METHOD_NAME = "::updateCustomObject::";
		boolean isUpdated = false;
		logger.debug(METHOD_NAME + "Inside...");

		// Updating custom object into IIQ....
		context.startTransaction();
		context.saveObject(customObject);
		context.commitTransaction();
		context.decache();

		isUpdated = true;

		return isUpdated;
	}

	/**
	 * Read from a data source or a custom object and get the list of allowed
	 * values...
	 * 
	 * @param internalContext
	 * @param customObjectName
	 * @param identityAttributeName
	 * @return
	 * @throws GeneralException
	 */

	public static List getDropDownListFromCustomObject(InternalContext internalContext, String customObjectName,
			String identityAttributeName) throws GeneralException {
		String METHOD_NAME = "::getDropDownListFromCustomObject::";

		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		SailPointContext context = internalContext.getContext();
		List dropDown = new ArrayList();
		dropDown = getDropDownListFromCustomObject(context, customObjectName, identityAttributeName);
		// logger.debug(CLASS_NAME + METHOD_NAME + "Drop Down List: " +
		// dropDown);
		return dropDown;

	}

	/**
	 * @param context
	 * @param customObjectName
	 * @param identityAttributeName
	 * @return
	 * @throws GeneralException
	 */
	public static List getDropDownListFromCustomObject(SailPointContext context, String customObjectName,
			String identityAttributeName) throws GeneralException {
		String METHOD_NAME = "::getDropDownListFromCustomObject::";

		List dropDown = new ArrayList();
		logger.debug(METHOD_NAME + "Inside...");

		QueryOptions qo = new QueryOptions();

		qo.add(Filter.eq("name", customObjectName));

		Iterator it = context.search(Custom.class, qo);

		if (it == null || !it.hasNext()) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Custom Object Not Found....: " + customObjectName);
			return dropDown;
		}

		Custom customObject = (Custom) it.next();

		/*
		 * 
		 * Custom customObject = getCustomObject(context, customObjectName);
		 * 
		 * if (customObject == null) { logger.debug(CLASS_NAME + METHOD_NAME +
		 * "Custom Object Not Found....: " + customObjectName); return dropDown;
		 * }
		 * 
		 * 
		 */
		Attributes attribute = customObject.getAttributes();
		dropDown = (List) attribute.get(identityAttributeName);

		// logger.debug(CLASS_NAME + METHOD_NAME + "Drop Down List: " +
		// dropDown);

		context.decache();

		return dropDown;

	}

	/**
	 * Generate password based on the Password policy specified... If no
	 * password policy name is provided it send the password which is compliant
	 * with the default password policy.....
	 * 
	 * This method takes InternalContext as input argument....
	 * 
	 * @param context
	 * @param policyName
	 * @return
	 * @throws GeneralException
	 */
	public static String generatePassword(InternalContext internalContext, String policyName) throws GeneralException {
		String METHOD_NAME = "::generatePassword:: ";
		String password = "Welcome1";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		SailPointContext context = internalContext.getContext();
		password = generatePassword(context, policyName);
		return password;

	}

	/**
	 * This method takes SailpointContext as input argument...
	 * 
	 * @param context
	 * @param policyName
	 * @return
	 * @throws GeneralException
	 */
	public static String generatePassword(SailPointContext context, String policyName) throws GeneralException {
		String METHOD_NAME = "::generatePassword::";

		String password = "";

		logger.debug(METHOD_NAME + "Inside...");

		if (policyName != null) {
			if (!policyName.equalsIgnoreCase("")) {

				PasswordPolicy policy = context.getObjectByName(PasswordPolicy.class, policyName);
				password = new PasswordGenerator(context).generatePassword(policy);
				logger.debug(CLASS_NAME + METHOD_NAME + "Password Policy Name Found: " + policyName
						+ " hence returning compliant password....");

				// Special treatment for Mainframe password policy, start

				// password printed for temporarily
				logger.debug(CLASS_NAME + METHOD_NAME + password);

				// End

				return password;
			} else {

				PasswordPolicy policy = context.getObjectByName(PasswordPolicy.class,
						CommonUtil.BCA_DEFALUT_PASSWORD_POLICY_NAME);
				if (policy != null) {
					password = new PasswordGenerator(context).generatePassword(policy);

					return password;
				} else {
					logger.debug(CLASS_NAME + METHOD_NAME + "Default password policy not found...");
				}
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "Returning the default code password: " + password);

		return password;
	}

	/**
	 * Check to see if the Identity Status value is correct or not....
	 * 
	 * @param context
	 * @param identityStatus
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isValidIdentityStatus(SailPointContext context, String identityStatus)
			throws GeneralException {

		String METHOD_NAME = "::isValidIdentityStatus::";
		boolean isValidIdentityStatus = false;
		logger.debug(METHOD_NAME + "Inside...");

		List identityStatusList = CommonUtil.getDropDownListFromCustomObject(context,
				CustomObject.IDENTITY_STATUS_CUSTOM_OBJECT, IdentityAttribute.STATUS);
		logger.debug(METHOD_NAME + "identityStatusList: " + identityStatusList);

		if (identityStatusList.contains(identityStatus)) {
			isValidIdentityStatus = true;
			logger.debug(METHOD_NAME + "Returning isValidIdentityStatus: " + isValidIdentityStatus);
			return isValidIdentityStatus;
		} else {
			logger.debug(METHOD_NAME + "Returning isValidIdentityStatus: " + isValidIdentityStatus);
			return isValidIdentityStatus;
		}
	}

	/**
	 * This method reads the Identity Status Custom Object and list the values
	 * for Identity Statuss...
	 * 
	 * @param internalContext
	 * @return
	 * @throws GeneralException
	 */
	public static List getIdentityStatusList(InternalContext internalContext) throws GeneralException {

		String METHOD_NAME = "::getIdentityStatusList::";
		List identityStatusList = new ArrayList();
		logger.debug(METHOD_NAME + "Inside...");

		identityStatusList = CommonUtil.getDropDownListFromCustomObject(internalContext,
				CustomObject.IDENTITY_STATUS_CUSTOM_OBJECT, IdentityAttribute.STATUS);
		logger.debug(METHOD_NAME + "identityStatusList: " + identityStatusList);

		return identityStatusList;

	}

	/**
	 * @param context
	 * @param dateString
	 * @return
	 */
	public static boolean isDateFormatCorrect(InternalContext context, String dateString) {
		String METHOD_NAME = "::isDateFormatCorrect::";
		// boolean isDateFormatCorrect = false;
		logger.debug(METHOD_NAME + "Inside...");
		String dateFormat = "\\d{4}\\d{2}\\d{2}";
		if (dateString.matches(dateFormat))
			return true;
		else
			return false;
		// return isDateFormatCorrect;
	}

	/**
	 * To check if the identity display name is unique for the identity....
	 * 
	 * @param internalContext
	 * @param identityID
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isUniqueIdentityID(InternalContext internalContext, String identityID)
			throws GeneralException {
		String METHOD_NAME = "::isUniqueIdentityID::";
		boolean isUniqueIdentityID = false;
		logger.debug(METHOD_NAME + "Inside...");

		isUniqueIdentityID = CommonUtil.isUniqueValue(internalContext.getContext(), IdentityAttribute.DISPLAY_NAME,
				identityID);
		logger.debug(METHOD_NAME + "Value returned for isUniqueIdentityID: " + isUniqueIdentityID);

		return isUniqueIdentityID;
	}

	/**
	 * @param nextUniqueSequence
	 * @return
	 */
	public static String getformatedSequenceString(String nextUniqueSequence) {
		String METHOD_NAME = "::getformatedSequenceString::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside..");

		String formatedString = "000000";
		int nextUniqueSequenceLength = nextUniqueSequence.length();
		int formatedStringLength = formatedString.length();
		logger.debug(CLASS_NAME + METHOD_NAME + "formatedStringLength: " + formatedStringLength);
		logger.debug(CLASS_NAME + METHOD_NAME + "nextUniqueSequenceLength: " + nextUniqueSequenceLength);

		formatedString = formatedString.substring(0, (formatedStringLength - nextUniqueSequenceLength))
				+ nextUniqueSequence;
		logger.debug(CLASS_NAME + METHOD_NAME + "formatedString: " + formatedString);

		return formatedString;
	}

	/**
	 * Common Util Method to get the formatted date...
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date getBCADateFormat(String dateString) throws ParseException {

		String METHOD_NAME = "::getBCADateFormat::";
		logger.debug(METHOD_NAME + "Inside...");
		DateFormat format = new SimpleDateFormat(CommonUtil.BCA_DATE_FORMAT);
		Date date = format.parse(dateString);
		return date;
	}

	/*
	 * public static Date getBCAEndDateFormat(String dateString) throws
	 * ParseException {
	 * 
	 * String METHOD_NAME = "::getBCAEndDateFormat::"; logger.debug(METHOD_NAME
	 * + "Inside..."); String adDate = ActiveDirectoryAttribute.ACCOUNT_EXPIRES;
	 * // 9223372036854775807 DateFormat df = new SimpleDateFormat(adDate); Date
	 * d = df.parse(dateString); return d; }
	 */

	/**
	 * The Read only flag is located in the Provisioning Policy that is used by
	 * the IIQ engine to prompt the user certain information. This rule will
	 * retrieve the profile of the requester and verify whether he is a Helpdesk
	 * Representative (has Helpdesk Capability in IIQ), an IIQ Administrator
	 * (has System Administrator Capability in IIQ) or a manager (requesting on
	 * behalf of one of his direct report), or again an end-user requesting for
	 * himself. Then, the IIQ Implementation team will return the true/false
	 * permission as detailed in the respective workflows (4.4.2.3.2Identity
	 * Creation Policy and 4.4.2.4.2Identity Update Policy) to make the fields
	 * read-only or editable.
	 * 
	 * @param context
	 * @param identity
	 * @return
	 */
	public static boolean shouldAttributeBeReadOnly(InternalContext context, String identity) {
		String METHOD_NAME = "::shouldAttributeBeReadOnly::";
		boolean shouldAttributeBeReadOnly = true;
		logger.debug(METHOD_NAME + "Inside...");

		return shouldAttributeBeReadOnly;
	}

	/**
	 * Whenever a user fill a request for a new Base24 account, the Group,
	 * Modeled as Role in IIQ, will be selected by the end-user as part of the
	 * Request. Inside this IIQ Role there will be a list of Permissions (or
	 * Entitlements in IIQ). This Rule is essentially retrieving the list of IIQ
	 * Entitlements (which equals Base24 permissions) associated with the IIQ
	 * Role (which equals Base24 Group) requested, and returns it as a
	 * serialized list of value representing the Base24 permissions in format
	 * <FILE>-<HAL>-<AKSES> separated by a comma. Example: CAF-1-RU,CAF-2-RU
	 * ,CAF-3-R,CAF-8-R,CAF-10-R,CSRC-1-R,CSTT-1-RU,CSTT-2-R,CSTT
	 * -3-R,ITLF-1-R,PBF-1-R,PTD-1-R,PTLF-1-R,TLF-1-R
	 * 
	 * @param internalContext
	 * @param roleName
	 * @return
	 */
	public static List generateBase24Permissions(InternalContext internalContext, String roleName) {
		String METHOD_NAME = "::generateBase24Permissions::";
		List base24PermissionList = new ArrayList();
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		// TODO Implement it...
		return base24PermissionList;
	}

	/**
	 * 
	 * 
	 * @param internalContext
	 * @param identity
	 * @return
	 */
	public static String generateBase24UserID(InternalContext internalContext, String identity) {
		String METHOD_NAME = "::generateBase24UserID::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String userID = "";

		String base24GroupName = "";
		String branchCode = "";
		String sequenceNumber = "";
		String SEPERATOR = ".";

		// TODO Get base24 group name...

		// TODO get branchCode....

		// TODO Get Sequence Number...

		userID = base24GroupName + SEPERATOR + branchCode + sequenceNumber;
		logger.debug(METHOD_NAME + "Base 24 User ID: " + userID);

		return userID;
	}

	public static String generateBase24Group(InternalContext internalContext, String identity) {
		String METHOD_NAME = "::generateBase24Group::";
		String base24Group = "";

		// TODO Implement...

		return base24Group;

	}

	/**
	 * @param internalContext
	 * @param identity
	 * @return
	 */
	public static String generateBase24CABUT(InternalContext internalContext, String identity) {
		String METHOD_NAME = "::generateBase24CABUT::";
		String cabut = "";

		// TODO Retrieve Branch Code...
		String branchCode = "";

		return cabut;

	}

	/**
	 * Method to retrieve the main branch code from the sub branch code....
	 * 
	 * @param internalContext
	 * @param branchCode
	 * @return
	 * @throws GeneralException
	 */
	public static String getMainBranchCode(SailPointContext context, String branchCode) throws GeneralException {
		String METHOD_NAME = "::getMainBranchCode::";
		logger.debug(METHOD_NAME + "Inside...");

		String ATTRIBUTE_NAME = "RegionCode";

		Custom branchTableMappingCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT);
		Attributes localAttributes = branchTableMappingCustomObject.getAttributes();
		List branchTableList = localAttributes.getList(CommonUtil.BRANCH_TABLE_ENTRY_KEY_VALUE);

		Iterator it = branchTableList.iterator();
		while (it.hasNext()) {
			Map branchTableMap = (Map) it.next();
			String localBranchCode = (String) branchTableMap.get(CommonUtil.BRANCH_TABLE_POSITION4);
			String localSubBranchCode = (String) branchTableMap.get(CommonUtil.BRANCH_TABLE_POSITION1);

			logger.debug(CLASS_NAME + METHOD_NAME + "localBranchCode: " + localBranchCode);
			logger.debug(CLASS_NAME + METHOD_NAME + "localSubBranchCode: " + localSubBranchCode);

			if (branchCode.equalsIgnoreCase(localSubBranchCode)) {
				return localBranchCode;
			}

		}

		return "";

	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static String generateEmailID(SailPointContext context, String identity) {
		String METHOD_NAME = "::generateEmailID::";
		String email = "";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		return email;

	}

	/**
	 * This method will read corresponding OU from custom object based on region
	 * code...
	 * 
	 * @param context
	 * @param regionCode
	 * @return
	 * @throws GeneralException
	 */
	public static String getOUFromRegionCode(SailPointContext context, String regionCode) throws GeneralException {
		String METHOD_NAME = "::getOUFromRegionCode::";
		String ou = "";
		logger.debug(METHOD_NAME + "Inside...");

		logger.debug(METHOD_NAME + "RegionCode: " + regionCode);
		Custom regionCodeCustomObject = CommonUtil.getCustomObject(context, CustomObject.REGION_CODE_MAP_CUSTOM_OBJECT);

		if (regionCodeCustomObject == null) {
			logger.debug(METHOD_NAME + "Custom Object not found.... Hence returning null");
			return ou;
		}

		Attributes localAttribute = regionCodeCustomObject.getAttributes();
		if (!localAttribute.containsKey(regionCode)) {
			logger.debug(METHOD_NAME + "Region Code Not found in the custom object.... hence returning null...."
					+ regionCode);
		} else {

			HashMap localHashMap = (HashMap) localAttribute.getMap();
			List ouList = (List) localHashMap.get(regionCode);
			// TODO Complete this one....

			return ou;
		}

		return ou;
	}

	/**
	 * A convenience function that tells IdentityIQ to ignore workgroup indexes
	 * in queries issued against the backing relational database. This can be a
	 * performance boost for installations on relational engines with poor index
	 * performance for bit-wise/boolean data columns like used on
	 * "spt_identity.workgroup"
	 */
	public static QueryOptions setIgnoreWorkgroupIndexes(QueryOptions qo) {
		ArrayList trueAndFalse = new ArrayList();
		trueAndFalse.add(new java.lang.Boolean(true));
		trueAndFalse.add(new java.lang.Boolean(false));
		qo.addFilter(Filter.in("workgroup", trueAndFalse));
		return qo;
	}

	/**
	 * @param context
	 * @param identityAttributeName
	 * @param identityAttributeValue
	 * @return
	 * @throws GeneralException
	 */
	public static Identity searchIdentity(SailPointContext context, String identityAttributeName,
			String identityAttributeValue) throws GeneralException {

		String METHOD_NAME = "::searchIdentity::";

		logger.debug(METHOD_NAME + "Inside...");

		Identity localIdentity = null;

		QueryOptions localQueryOptions = new QueryOptions();

		Filter localFilter = Filter.eq(identityAttributeName, identityAttributeValue);

		localQueryOptions.addFilter(localFilter);

		Iterator localIterator = context.search(Identity.class, localQueryOptions);

		logger.debug(METHOD_NAME + "Performed Search....");

		if (localIterator.hasNext()) {

			localIdentity = (Identity) localIterator.next();

		}

		logger.debug(METHOD_NAME + "Your Local Identity will be " + localIdentity);
		// context.decache();

		return localIdentity;

	}

	/**
	 * @param context
	 * @param identityAttributeName
	 * @param identityAttributeValue
	 * @return
	 * @throws GeneralException
	 */
	public static Iterator searchIteratorIdentity(SailPointContext context, String identityAttributeName,
			String identityAttributeValue) throws GeneralException {

		String METHOD_NAME = "::searchIdentity::";

		logger.debug(METHOD_NAME + "Inside...");

		Identity localIdentity = null;

		QueryOptions localQueryOptions = new QueryOptions();

		Filter localFilter = Filter.eq(identityAttributeName, identityAttributeValue);

		localQueryOptions.addFilter(localFilter);

		Iterator localIterator = context.search(Identity.class, localQueryOptions);

		logger.debug(METHOD_NAME + "Performed Search....");

		return localIterator;

	}

	/**
	 * 
	 * This method only return id value from an identity. This method is better
	 * use for Correlation Rule
	 * 
	 * @param context
	 * @param identityAttributeName
	 * @param identityAttributeValue
	 * @return
	 * @throws GeneralException
	 */
	public static String searchIdentityId(SailPointContext context, String identityAttributeName,
			String identityAttributeValue) throws GeneralException {

		String METHOD_NAME = "::searchIdentityId::";

		logger.debug(METHOD_NAME + "Inside...");

		String idVal = "";

		Identity localIdentity = null;

		QueryOptions localQueryOptions = new QueryOptions();

		logger.debug(METHOD_NAME + " set to ignore workgroup index");

		setIgnoreWorkgroupIndexes(localQueryOptions);

		logger.debug(METHOD_NAME + " set filter");

		Filter localFilter = Filter.eq(identityAttributeName, identityAttributeValue);

		localQueryOptions.addFilter(localFilter);

		logger.debug(METHOD_NAME + " preparation to search identity");

		Iterator localIterator = context.search(Identity.class, localQueryOptions, identityAttributeName);

		logger.debug(METHOD_NAME + "Performed Search....");

		if (localIterator.hasNext()) {

			Object[] retObj = (Object[]) localIterator.next();
			idVal = (String) retObj[0];

			// context.close();

			return idVal;

		}

		return idVal;

	}

	public static Map getAllActiveIdentity(SailPointContext context) throws GeneralException {
		Map map = new HashMap();

		String METHOD_NAME = "::searchIdentityId::";

		logger.debug(METHOD_NAME + "Inside...");

		String idVal = "";

		Identity localIdentity = null;

		QueryOptions localQueryOptions = new QueryOptions();

		logger.debug(METHOD_NAME + " set to ignore workgroup index");

		setIgnoreWorkgroupIndexes(localQueryOptions);

		logger.debug(METHOD_NAME + " set filter");

		localQueryOptions.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.INACTIVE_EMPLOYEE));
		localQueryOptions.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.TERMINATED_EMPLOYEE));

		Iterator it = context.search(Identity.class, localQueryOptions, IdentityAttribute.EMPLOYEE_ID);

		context.decache();
		int i = 0;
		while (it.hasNext()) {
			Object[] retObj = (Object[]) it.next();
			map.put((String) retObj[0], (String) retObj[0]);
			i++;
		}

		logger.debug(CLASS_NAME + METHOD_NAME + " size list " + i);

		return map;
	}

	/**
	 * 
	 * This method only return id value from an identity. This method is better
	 * use for Correlation Rule
	 * 
	 * @param context
	 * @param identityAttributeName
	 * @param identityAttributeValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	public static String searchIdentityIdBySql(SailPointContext context, String identityAttributeName,
			String identityAttributeValue) throws GeneralException {

		String METHOD_NAME = "::searchIdentityId::";

		String sqlQuery = "select display_name from identityiq.spt_identity where display_name = '"
				+ identityAttributeValue + "'";

		logger.debug(METHOD_NAME + "Inside..., sql : " + sqlQuery);

		String idVal = "";

		try {

			Connection conn = context.getJdbcConnection();

			logger.debug(METHOD_NAME + " get connection");

			PreparedStatement ps = conn.prepareStatement(sqlQuery);

			logger.debug(METHOD_NAME + " prepared statement");

			ResultSet rs = ps.executeQuery();

			logger.debug(METHOD_NAME + " execute query");

			while (rs.next()) {
				idVal = rs.getString("display_name");
				rs.close();
				ps.close();
				return idVal;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return idVal;

	}

	public static boolean isNotEmptyString(String var) {
		return var != null && !"".equalsIgnoreCase(var.trim());

	}

	/**
	 * Common Util Method to get the formatted date...
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date getCPAFormattedDate(String dateString) throws ParseException {
		String METHOD_NAME = "::getCPAFormattedDate::";
		logger.debug(METHOD_NAME + "Inside...");

		DateFormat format = new SimpleDateFormat(CommonUtil.BCA_DATE_FORMAT);
		Date date = format.parse(dateString);
		logger.debug(METHOD_NAME + "Formated Date: " + date);
		return date;
	}

	/**
	 * @param context
	 * @param mfSequenceName
	 * @return
	 * @throws GeneralException
	 */
	public static String getNextSequence(SailPointContext context, String mfSequenceName) throws GeneralException {

		String METHOD_NAME = "::getNextSequence::";
		String nextUniqueSequence = "";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		String SEQUENCE_OBJECT = mfSequenceName;

		Custom sequenceObject = context.getObjectByName(Custom.class, SEQUENCE_OBJECT);
		if (sequenceObject == null) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Sequence Object " + SEQUENCE_OBJECT
					+ " not found... hence returning blank value...");
			return nextUniqueSequence;
		} else {
			Attributes attributes = sequenceObject.getAttributes();
			nextUniqueSequence = attributes.getString("nextUniqueSequence");

			logger.debug(CLASS_NAME + METHOD_NAME + "Next Unique Sequence Found: " + nextUniqueSequence);
			// Try to increment the sequence by one

			boolean isIncrementSuccessful = incrementSequence(context, SEQUENCE_OBJECT, nextUniqueSequence);
			logger.debug(CLASS_NAME + METHOD_NAME + "isIncrementSuccessful: " + isIncrementSuccessful);

		}

		return nextUniqueSequence;

	}

	/**
	 * Getting lock on the sequence and then increment it by one...
	 * 
	 * @param context
	 * @param sequenceObjectString
	 * @param nextUniqueSequence
	 * @return
	 * @throws GeneralException
	 */
	public static boolean incrementSequence(SailPointContext context, String sequenceObjectString,
			String nextUniqueSequence) throws GeneralException {
		String METHOD_NAME = "::incrementSequence::";
		int baseSequence = Integer.parseInt(nextUniqueSequence);
		baseSequence = baseSequence + 1;

		if (baseSequence > 99) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "sequence value had gone beyond the specified limit.. cant continue... reset the sequence....");
			return false;
		}

		nextUniqueSequence = Integer.toString(baseSequence);

		// See if there is already a custom object, and lock it if there is.
		Custom sequenceObject = (Custom) ObjectUtil.lockObject(context, Custom.class, null, sequenceObjectString,
				PersistenceManager.LOCK_TYPE_TRANSACTION);

		logger.debug(CLASS_NAME + METHOD_NAME + "nextUniqueSequence: " + nextUniqueSequence);
		nextUniqueSequence = getformatedSequenceString(nextUniqueSequence);
		sequenceObject.put("nextUniqueSequence", nextUniqueSequence);
		// sequenceObject.pu

		// Trying to update the sequence....

		context.startTransaction();
		context.saveObject(sequenceObject);
		context.commitTransaction();
		context.decache();

		logger.trace(CLASS_NAME + METHOD_NAME + sequenceObject.toXml());

		return true;
	}

	/**
	 * Logic to check if the user is working in branch or at division level....
	 * 
	 * @param context
	 * @param identity
	 * @return
	 */
	public static boolean isUserWorkingInBranch(SailPointContext context, Identity identity) {
		String METHOD_NAME = "::isUserWoringInBranch::";
		boolean isUserWoringInBranch = false;
		// TODO Implement it...

		return isUserWoringInBranch;
	}

	/**
	 * @param context
	 * @param applicationName
	 * @param attributeName
	 * @param attributeValue
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isUniqueApplicationAttributeValue(SailPointContext context, String applicationName,
			String attributeName, String attributeValue) throws GeneralException {
		String METHOD_NAME = "::isUniqueApplicationAttributeValue::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		boolean isUniqueValue = false;

		List listLink = new ArrayList();
		QueryOptions qo = new QueryOptions();

		qo.addFilter(Filter.eq("displayName", (java.lang.Object) attributeValue));

		listLink = context.getObjects(Link.class, qo);
		if (listLink.size() > 0) {

			Iterator it = listLink.iterator();

			while (it.hasNext()) {
				Link link = (Link) it.next();

				if (applicationName.equalsIgnoreCase(link.getApplicationName())) {
					return false;
				}

			}
			return true;
		} else {
			logger.debug(CLASS_NAME + METHOD_NAME + "This value is not already taken");
			return true;
		}

	}

	/*
	 * method ini digunakan untuk cek atribute dari link setiap identity. method
	 * ini beda dengan method isUniqueApplicationAttributeValue, karena method
	 * tersebut hardcode attribute ke displayName
	 */
	public static boolean isUniqueApplicationAttribute(SailPointContext ctx, String applicationName,
			String attributeName, String attributeValue) throws GeneralException {

		String METHOD_NAME = "::isUniqueApplicationAttributeValue::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		boolean isUniqueValue = false;

		List listLink = new ArrayList();
		QueryOptions qo = new QueryOptions();

		Filter linksFilter = Filter.and(Filter.eq("application.name", applicationName),
				Filter.eq(attributeName, attributeValue));
		Filter.collectionCondition("links", linksFilter);

		qo.addFilter(linksFilter);

		listLink = ctx.getObjects(Link.class, qo);
		if (listLink.size() > 0) {

			Link localLink = (Link) listLink.get(0);

			logger.debug(CLASS_NAME + METHOD_NAME + "Existing account found  with " + localLink.getDisplayName());

			return false;
		} else {
			logger.debug(CLASS_NAME + METHOD_NAME + "This value is not already taken");
			return true;
		}

	}
	//tambah testing
	public static String getEmployeeId(String shortEmpId) {
		String employeeId = "";
		
		if(isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 5)
			return "000" + shortEmpId.trim();
		
		if(isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 8)
			return shortEmpId;

		if (shortEmpId != null && shortEmpId.trim().length() > 1) {
			if (Base24Attribute.BASE24_MAGANG_PREFIX_NIP
					.equalsIgnoreCase(shortEmpId.substring(0, 1))) {
				employeeId = IdentityAttribute.PREFIX_MAGANG_NIP + shortEmpId;
			} else if (Base24Attribute.BASE24_NEW_EMP_PREFIX
					.equalsIgnoreCase(shortEmpId.substring(0, 2))
					|| IdentityAttribute.MANAGE_IIQ_EMP_PREFIX
							.equalsIgnoreCase(shortEmpId.substring(0, 2))) {
				employeeId = IdentityAttribute.PREFIX_NEW_EMP + shortEmpId;
			} else {
				if("0".equalsIgnoreCase(shortEmpId.substring(0, 1)))
					employeeId = "20" + shortEmpId;
				else
					employeeId = IdentityAttribute.PREFIX_OLD_EMP + shortEmpId;
			}
		}

		return employeeId;
	}
	//end

	public static String getEmployeeIdRacf(String shortEmpId) {

		String METHOD_NAME = "::getEmployeeIdRacf::";

		String employeeId = "";

		if (isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 5)
			return "000" + shortEmpId.trim();
		// xxxxx = 000xxxxx
		if (isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 8)
			return shortEmpId;
		// xxxxxxxx = xxxxxxxx
		if (shortEmpId != null && shortEmpId.trim().length() > 1) {

			if ("05".equals(shortEmpId.substring(0, 2))) {
				employeeId = "00" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 05xxxx = 0005xxxx
			else if ("8".equals(shortEmpId.substring(0, 1))) {
				employeeId = "19" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 8xxxxx = 198xxxxx
			else if ("9".equals(shortEmpId.substring(0, 1))) {
				employeeId = "19" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 9xxxxx = 199xxxxx
			else if ("00".equals(shortEmpId.substring(0, 2))) {
				employeeId = "20" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 00xxxx = 2000xxxx
			else if ("01".equals(shortEmpId.substring(0, 2))) {
				employeeId = "20" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 01xxxx = 2001xxxx
			else if ("02".equals(shortEmpId.substring(0, 2))) {
				employeeId = "20" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 02xxxx = 2002xxxx
			else if ("03".equals(shortEmpId.substring(0, 2))) {
				employeeId = "20" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 03xxxx = 2003xxxx
			else if ("04".equals(shortEmpId.substring(0, 2))) {
				employeeId = "20" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 04xxxx = 2004xxxx
			else if ("06".equals(shortEmpId.substring(0, 2))) {
				employeeId = "00" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 06xxxx = 0006xxxx
			else if ("07".equals(shortEmpId.substring(0, 2))) {
				employeeId = "00" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 07xxxx = 0007xxxx
			else if ("6".equals(shortEmpId.substring(0, 1))) {
				employeeId = "80" + shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 6xxxxx = 806xxxxx
			else if ("5".equals(shortEmpId.substring(0, 1))) {
				employeeId = shortEmpId.trim();
				logger.debug("Your Employee wil be " + employeeId);
			} // 5XXXX = 5XXXX
			else if ("0".equalsIgnoreCase(shortEmpId.substring(0, 1))) {
				employeeId = "20" + shortEmpId;
				logger.debug("Your Employee wil be " + employeeId);
			} // 0xxxxx = 200xxxxx
			else
				employeeId = IdentityAttribute.PREFIX_OLD_EMP + shortEmpId;
			logger.debug("Your Employee wil be " + employeeId);

		}

		// if(isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 6)
		// return shortEmpId;
		//
		// if(isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 6 &&
		// "0".equals(shortEmpId.substring(1))){
		// return "00" + shortEmpId.trim();
		// }

		/*
		 * String empId = employeeId.substring(6);
		 * 
		 * logger.debug(METHOD_NAME + " EmployeeID substring " + empId);
		 * 
		 * String getUId =
		 * ActiveDirectoryAttribute.SAM_ACCOUNT_NAME.substring(6);
		 * 
		 * logger.debug(METHOD_NAME + " U Id substring " + getUId);
		 * 
		 * if(empId == getUId){ return employeeId; } logger.debug(CLASS_NAME +
		 * METHOD_NAME + "Reset Passwword domain" + employeeId);
		 */

		return employeeId;
	}

	public static String getEmployeeIdBase24(String shortEmpId) {

		String METHOD_NAME = "::getEmployeeIdBase24::";

		String employeeId = "";

		if (isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 5)
			return "000" + shortEmpId.trim();
		// xxxxx = 000xxxxx
		if (isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 8)
			return shortEmpId;
		// xxxxxxxx = xxxxxxxx
		if (shortEmpId != null && shortEmpId.trim().length() > 1) {
			if (Base24Attribute.BASE24_MAGANG_PREFIX_NIP.equalsIgnoreCase(shortEmpId.substring(0, 1))) {
				employeeId = IdentityAttribute.PREFIX_MAGANG_NIP + shortEmpId;
			} else if (Base24Attribute.BASE24_NEW_EMP_PREFIX.equalsIgnoreCase(shortEmpId.substring(0, 2))
					|| IdentityAttribute.MANAGE_IIQ_EMP_PREFIX.equalsIgnoreCase(shortEmpId.substring(0, 2))) {
				employeeId = IdentityAttribute.PREFIX_NEW_EMP + shortEmpId;
			} else {
				logger.debug("testing :" + "05".equals(shortEmpId.substring(0, 2)));
				if ("05".equals(shortEmpId.substring(0, 2))) {
					employeeId = "00" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 05xxxx = 0005xxxx
				else if ("8".equals(shortEmpId.substring(0, 1))) {
					employeeId = "19" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 8xxxxx = 198xxxxx
				else if ("9".equals(shortEmpId.substring(0, 1))) {
					employeeId = "19" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 9xxxxx = 199xxxxx
				else if ("00".equals(shortEmpId.substring(0, 2))) {
					employeeId = "20" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 00xxxx = 2000xxxx
				else if ("01".equals(shortEmpId.substring(0, 2))) {
					employeeId = "20" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 01xxxx = 2001xxxx
				else if ("02".equals(shortEmpId.substring(0, 2))) {
					employeeId = "20" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 02xxxx = 2002xxxx
				else if ("03".equals(shortEmpId.substring(0, 2))) {
					employeeId = "20" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 03xxxx = 2003xxxx
				else if ("04".equals(shortEmpId.substring(0, 2))) {
					employeeId = "20" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 04xxxx = 2004xxxx
				else if ("06".equals(shortEmpId.substring(0, 2))) {
					employeeId = "00" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 06xxxx = 0006xxxx
				else if ("07".equals(shortEmpId.substring(0, 2))) {
					employeeId = "00" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 07xxxx = 0007xxxx
				else if ("6".equals(shortEmpId.substring(0, 1))) {
					employeeId = "80" + shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 6xxxxx = 806xxxxx
				else if ("5".equals(shortEmpId.substring(0, 1))) {
					employeeId = shortEmpId.trim();
					logger.debug("Your Employee wil be " + employeeId);
				} // 5XXXX = 5XXXX
				else if ("0".equalsIgnoreCase(shortEmpId.substring(0, 1))) {
					employeeId = "20" + shortEmpId;
					logger.debug("Your Employee wil be " + employeeId);
				} // 0xxxxx = 200xxxxx
				else
					employeeId = IdentityAttribute.PREFIX_OLD_EMP + shortEmpId;
				logger.debug("Your Employee wil be " + employeeId);
			}
		}

		// if(isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 6)
		// return shortEmpId;
		//
		// if(isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 6 &&
		// "0".equals(shortEmpId.substring(1))){
		// return "00" + shortEmpId.trim();
		// }

		/*
		 * String empId = employeeId.substring(6);
		 * 
		 * logger.debug(METHOD_NAME + " EmployeeID substring " + empId);
		 * 
		 * String getUId =
		 * ActiveDirectoryAttribute.SAM_ACCOUNT_NAME.substring(6);
		 * 
		 * logger.debug(METHOD_NAME + " U Id substring " + getUId);
		 * 
		 * if(empId == getUId){ return employeeId; } logger.debug(CLASS_NAME +
		 * METHOD_NAME + "Reset Passwword domain" + employeeId);
		 */

		return employeeId;
	}

	public static String getEmployeeIdResetPWd(String shortEmpId) {

		String METHOD_NAME = "::getEmployeeIdforResetPWD::";

		String UID = shortEmpId.substring(0, 1);

		String employeeId = "";

		if ("U".equals(UID) || "u".equals(UID)) {

			logger.debug(METHOD_NAME + " Got an " + UID + " Character");

			logger.debug(METHOD_NAME + " Retrieve User Input " + shortEmpId);

			logger.debug(METHOD_NAME + "Sample for substr Result " + shortEmpId.substring(1, 3));

			if ("05".equals(shortEmpId.substring(1, 3))) {
				employeeId = "00" + shortEmpId.trim().substring(1);
				logger.debug("Your Employee wil be " + employeeId);
			} // U05xxxx = 00051234

			else if ("8".equals(shortEmpId.substring(1, 2))) {
				employeeId = "198" + shortEmpId.trim().substring(2);
				logger.debug("Your Employee wil be " + employeeId);
			} // U8xxxxx = 19812345

			else if ("9".equals(shortEmpId.substring(1, 2))) {
				employeeId = "199" + shortEmpId.trim().substring(2);
				logger.debug("Your Employee wil be " + employeeId);
			} // U9xxxxx = 19912345

			else if ("00".equals(shortEmpId.substring(1, 3))) {
				employeeId = "2000" + shortEmpId.trim().substring(3);
				logger.debug("Your Employee wil be " + employeeId);
			} // U00xxxx = 20001234

			else if ("01".equals(shortEmpId.substring(1, 3))) {
				employeeId = "2001" + shortEmpId.trim().substring(3);
				logger.debug("Your Employee wil be " + employeeId);
			} // U01xxxx = 20012345

			else if ("02".equals(shortEmpId.substring(1, 3))) {
				employeeId = "2002" + shortEmpId.trim().substring(3);
				logger.debug("Your Employee wil be " + employeeId);
			} // U02xxxx = 20021234

			else if ("03".equals(shortEmpId.substring(1, 3))) {
				employeeId = "2003" + shortEmpId.trim().substring(3);
				logger.debug("Your Employee wil be " + employeeId);
			} // U03xxxx = 20031234

			else if ("04".equals(shortEmpId.substring(1, 3))) {
				employeeId = "2004" + shortEmpId.trim().substring(3);
				logger.debug("Your Employee wil be " + employeeId);
			} // U04xxxx = 20041234

			else if ("06".equals(shortEmpId.substring(1, 3))) {
				employeeId = "00" + shortEmpId.trim().substring(1);
				logger.debug("Your Employee wil be " + employeeId);
			} // U06xxxx = 00061234

			else if ("07".equals(shortEmpId.substring(1, 3))) {
				employeeId = "00" + shortEmpId.trim().substring(1);
				logger.debug("Your Employee wil be " + employeeId);
			} // U07xxxx = 00071234

			else if ("6".equals(shortEmpId.substring(1, 2))) {
				employeeId = "80" + shortEmpId.trim().substring(1);
				logger.debug("Your Employee wil be " + employeeId);
			} // U6xxxxxx = 80612345

			else if ("5".equals(shortEmpId.substring(1, 2))) {
				employeeId = shortEmpId.trim().substring(1);
				logger.debug("Your Employee wil be " + employeeId);
			} // U5xxxxx = 520670

		} else {

			if (isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 5)
				return "000" + shortEmpId.trim();

			if (isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 6
					&& "5".equals(shortEmpId.substring(0, 1)))
				return shortEmpId.trim();

			if (isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 8)
				return shortEmpId;

			if (isNotEmptyString(shortEmpId) && shortEmpId.trim().length() == 6)
				return "00" + shortEmpId;

			if (shortEmpId != null && shortEmpId.trim().length() > 1) {
				if (Base24Attribute.BASE24_MAGANG_PREFIX_NIP.equalsIgnoreCase(shortEmpId.substring(0, 1))) {
					employeeId = IdentityAttribute.PREFIX_MAGANG_NIP + shortEmpId;
				} else if (Base24Attribute.BASE24_NEW_EMP_PREFIX.equalsIgnoreCase(shortEmpId.substring(0, 2))
						|| IdentityAttribute.MANAGE_IIQ_EMP_PREFIX.equalsIgnoreCase(shortEmpId.substring(0, 2))) {
					employeeId = IdentityAttribute.PREFIX_NEW_EMP + shortEmpId;
				} else {
					if ("0".equalsIgnoreCase(shortEmpId.substring(0, 1)))
						employeeId = "20" + shortEmpId;
					else
						employeeId = IdentityAttribute.PREFIX_OLD_EMP + shortEmpId;
				}
			}
		}

		logger.debug("Your Employee ID is " + employeeId);

		return employeeId;
	}

	public static String getShortEmployeeId(String employeeId) {

		if (CommonUtil.isNotEmptyString(employeeId) && employeeId.length() == 8)
			return employeeId.substring(2);
		else if (CommonUtil.isNotEmptyString(employeeId) && employeeId.length() == 5)
			return "0" + employeeId;

		return employeeId;
	}

	/**
	 * This method will read corresponding region from custom object based on
	 * region code and type of region value...
	 * 
	 * @param context
	 * @param regionCode
	 * @return
	 * @throws GeneralException
	 */
	public static String getRegionTypeValueFromRegionCode(SailPointContext context, String regionCode, String type)
			throws GeneralException {
		String METHOD_NAME = "::getRegionTypeValueFromRegionCode::";
		String region = "";
		logger.debug(METHOD_NAME + "Inside...");

		logger.debug(METHOD_NAME + "RegionCode: " + regionCode);
		Custom regionCodeCustomObject = CommonUtil.getCustomObject(context, CustomObject.REGION_CODE_MAP_CUSTOM_OBJECT);

		if (regionCodeCustomObject == null) {
			logger.debug(METHOD_NAME + "Custom Object not found.... Hence returning null");
			return region;
		}

		//Attributes localAttribute = regionCodeCustomObject.getAttributes();
		Map <String, Object> localHashMap = regionCodeCustomObject.getAttributes().getMap();
		
		logger.debug("localHashMap will be : " + regionCodeCustomObject.getAttributes().getMap());
		if(localHashMap != null) {
			for(Map.Entry<String, Object> val : localHashMap.entrySet()){
				if(val.getKey().equalsIgnoreCase(regionCode)){
					
					logger.debug("region found " + val.getValue().toString());
					return val.getValue().toString();
				}
			}
		}

		return region;

	}

	/**
	 * This method will read corresponding value from BCA Branch Code Mapping
	 * 
	 * @param context
	 * @param regionCode
	 * @return
	 * @throws GeneralException
	 */
	public static String getBranchValueFromBranchCode(SailPointContext context, String branchCode, String type)
			throws GeneralException {
		String METHOD_NAME = "::getBranchValueFromBranchCode::";
		String branchValue = "";
		String branchTableAttribute = "branchTable";

		logger.debug(METHOD_NAME + "Inside...");

		logger.debug(METHOD_NAME + "branchCode: " + branchCode);
		Custom branchCodeCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT);

		if (branchCodeCustomObject == null) {
			logger.debug(METHOD_NAME + "Custom Object not found.... Hence returning null");
			return branchValue;
		}

		Attributes localAttribute = branchCodeCustomObject.getAttributes();
		if (!localAttribute.containsKey(branchTableAttribute)) {
			logger.debug(METHOD_NAME + "Branch Code Not found in the custom object.... hence returning null...."
					+ branchCode);
			return branchValue;
		} else {

			List localList = localAttribute.getList(branchTableAttribute);

			Iterator it = localList.iterator();

			while (it.hasNext()) {
				Map branchMap = (Map) it.next();
				if (branchCode.equalsIgnoreCase((String) branchMap.get("subBranchCode"))) {
					branchValue = (String) branchMap.get(type);
				}
			}

			return branchValue;
		}
	}

	/**
	 * This method will read corresponding value from Custom Object Map
	 * 
	 * Format of custom object :
	 * 
	 * <Map> <entry key="" value=""> </map>
	 * 
	 * @param context
	 * @param regionCode
	 * @return
	 * @throws GeneralException
	 */

	public static String getValueFromCustomObject(SailPointContext ctx, String customObjectName, String key)
			throws GeneralException {

		String METHOD_NAME = "::getValueFromCustomObject::";

		String val = "";

		logger.debug(METHOD_NAME + " get map from custom object");

		Custom obj = getCustomObject(ctx, customObjectName);

		Attributes attr = obj.getAttributes();

		Map objMap = attr.getMap();

		logger.debug(METHOD_NAME + " get value from custom object");

		val = (String) objMap.get(key);

		if (!isNotEmptyString(val))
			logger.debug(METHOD_NAME + " the mapping couldn't be found");

		return val;
	}

	/**
	 * Get a string and check of its work group...
	 * 
	 * @param context
	 * @param workGroupName
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isWorkGroup(SailPointContext context, String workGroupName) throws GeneralException {
		String METHOD_NAME = "::isWorkGroup::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		Identity identity = null;
		identity = context.getObjectByName(Identity.class, workGroupName);

		identity.getLink("IBM MAIN");

		if (identity.isWorkgroup()) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity: " + identity.getName() + " is a workgroup...");
			return true;
		} else {
			logger.debug(CLASS_NAME + METHOD_NAME + "Identity: " + identity.getName() + " is not a workgroup...");
			return false;
		}
	}

	/**
	 * @param identity
	 * @return
	 */
	public static boolean isRegionalHQEmployee(Identity identity) {
		String METHOD_NAME = "::isRegionalHQEmployee::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		boolean isRegionalHQEmployee = false;

		String branchCode = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		String regionCode = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);

		if (HQ_REGION_CODE.equalsIgnoreCase(branchCode)) {
			isRegionalHQEmployee = false;
			return isRegionalHQEmployee;
		} else if (branchCode.equalsIgnoreCase(regionCode)) {
			isRegionalHQEmployee = true;
			return isRegionalHQEmployee;

		}
		logger.debug(CLASS_NAME + METHOD_NAME + "No Logic Found... Hence returning false...");
		return isRegionalHQEmployee;

	}

	/**
	 * @param identity
	 * @return
	 */
	public static boolean isBranchEmployee(Identity identity) {
		String METHOD_NAME = "::isBranchEmployee::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		boolean isBranchEmployee = false;
		String divisionCode = (String) identity.getAttribute(IdentityAttribute.DIVISION_CODE);
		String branchCode = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		String regionCode = (String) identity.getAttribute(IdentityAttribute.REGION_CODE);

		if (HQ_REGION_CODE.equalsIgnoreCase(branchCode)) {

			isBranchEmployee = false;
			return isBranchEmployee;
		} else if (branchCode.equalsIgnoreCase(regionCode)) {

			isBranchEmployee = false;
			return isBranchEmployee;
		} else {
			isBranchEmployee = true;
			return isBranchEmployee;
		}
		// TODO
	}

	/**
	 * @param identity
	 * @return
	 */
	public static boolean isHQEmployee(Identity identity) {
		String METHOD_NAME = "::isBranchCodeHQType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		boolean isHQEmployee = false;

		String branchCode = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);

		if (HQ_REGION_CODE.equalsIgnoreCase(branchCode)) {
			isHQEmployee = true;
			logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code: " + branchCode + " for employee : "
					+ identity.getName() + " is of HQ type hence the employee is from HQ... returning true....");

			return isHQEmployee;

		}
		return isHQEmployee;

	}

	/**
	 * Keeping the sailpoint context to just make sure all the similar method
	 * looks consistent and may be used in future... if you wish other method...
	 * define your own....
	 * 
	 * @param branchCode
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isHQBranchCode(String branchCode) {
		String METHOD_NAME = "::isHQBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		boolean isHQBranchCode = false;

		// Identifying if a branchCode is of HQ is very simple... as HQ only
		// have a constant value...
		if (CommonUtil.HQ_REGION_CODE.equalsIgnoreCase(branchCode)) {

			isHQBranchCode = true;

			logger.debug(
					CLASS_NAME + METHOD_NAME + "Branch Code : " + branchCode + " is HQ.... hence returning true...");
			return isHQBranchCode;
		} else {
			logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code : " + branchCode
					+ " is not HQ.... hence returning false...");

			return isHQBranchCode;
		}
	}

	public static boolean isRegionalBranchCode(String branchCode, String regionCode) {
		String METHOD_NAME = "::isRegionalBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		if (isHQBranchCode(branchCode))
			return false;
		return branchCode != null && regionCode != null && branchCode.equalsIgnoreCase(regionCode);
	}

	public static boolean isRegionalBranchCode(SailPointContext context, String branchCode) throws GeneralException {
		String METHOD_NAME = "::isRegionalBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		if (isHQBranchCode(branchCode))
			return false;
		else if (isKFCCBranchCode(branchCode))
			return false;
		else {
			Custom custom = CommonUtil.getCustomObject(context, CustomObject.BCA_REGION_TABLE_MAPPING_CUSTOM_OBJECT);

			Attributes attr = custom.getAttributes();

			Map regionMap = attr.getMap();

			return regionMap.containsKey(branchCode);
		}
	}

	/**
	 * 
	 * @param context
	 * @param branchCode
	 * @return
	 * @throws GeneralException
	 */
	public static String getBranchType(SailPointContext context, String branchCode) throws GeneralException {
		String METHOD_NAME = "::getBranchType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		boolean isMainBranchCode = false;

		Custom branchTableMappingCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT);
		Attributes localAttributes = branchTableMappingCustomObject.getAttributes();
		List branchTableList = localAttributes.getList(CommonUtil.BRANCH_TABLE_ENTRY_KEY_VALUE);

		Iterator it = branchTableList.iterator();
		while (it.hasNext()) {
			Map branchTableMap = (Map) it.next();
			String localBranchCode = (String) branchTableMap.get(CommonUtil.BRANCH_TABLE_POSITION4);
			String localSubBranchCode = (String) branchTableMap.get(CommonUtil.BRANCH_TABLE_POSITION1);

			logger.debug(CLASS_NAME + METHOD_NAME + "localBranchCode: " + localBranchCode);
			logger.debug(CLASS_NAME + METHOD_NAME + "localSubBranchCode: " + localSubBranchCode);

			if (branchCode.equalsIgnoreCase(localBranchCode) && localBranchCode.equalsIgnoreCase(localSubBranchCode)) {
				return "KCU";
			} else if (branchCode.equalsIgnoreCase(localSubBranchCode)) {
				return "KCP";
			}

		}

		logger.debug(CLASS_NAME + METHOD_NAME + " Branch Code: " + branchCode
				+ " seems to be not in the list... please contact system administrator... returning false..");

		return "";

	}

	/**
	 * 
	 * @param context
	 * @param branchCode
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isMainBranchCode(SailPointContext context, String branchCode) throws GeneralException {
		String METHOD_NAME = "::isMainBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		return "KCU".equalsIgnoreCase(getBranchType(context, branchCode));

	}

	/**
	 * 
	 * 
	 * @param context
	 * @param branchCode
	 * @return
	 * @throws GeneralException
	 */
	public static boolean isSubBranchCode(SailPointContext context, String branchCode) throws GeneralException {
		String METHOD_NAME = "::isSubBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		return "KCP".equalsIgnoreCase(getBranchType(context, branchCode));

	}

	public static boolean isSOABranchCode(String branchCode) {
		return branchCode != null && BCAApproverMatrixConstant.SOA_BRANCH_CODE.equalsIgnoreCase(branchCode);
	}

	public static boolean isKFCCBranchCode(String branchCode) {
		return branchCode != null && (BCAApproverMatrixConstant.KFCC_MAKASSAR_BRANCH_CODE.equalsIgnoreCase(branchCode)
				|| BCAApproverMatrixConstant.KFCC_SOLO_BRANC_CODE.equalsIgnoreCase(branchCode));
	}

	public static Iterator searchActiveIdentityByBranchByPosition(SailPointContext context, String branchCode,
			String positionCode) throws GeneralException {

		String METHOD_NAME = "::searchActiveIdentityByBranchByPosition::";

		logger.debug(METHOD_NAME + "Inside...");

		QueryOptions localQueryOptions = new QueryOptions();

		/*
		 * localQueryOptions.add(Filter.eq(IdentityAttribute.STATUS,
		 * IdentityStatus.ACTIVE_EMPLOYEE));
		 */

		localQueryOptions.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.INACTIVE_EMPLOYEE));
		localQueryOptions.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.TERMINATED_EMPLOYEE));

		if (CommonUtil.isNotEmptyString(branchCode))
			localQueryOptions.addFilter(Filter.eq(IdentityAttribute.BRANCH_CODE, branchCode));

		if (CommonUtil.isNotEmptyString(positionCode)) {

			if (positionCode.split(",").length > 1) {
				localQueryOptions.addFilter(
						Filter.in(IdentityAttribute.POSITION_CODE, getListFromArray(positionCode.trim().split(","))));
			} else {
				localQueryOptions.addFilter(Filter.eq(IdentityAttribute.POSITION_CODE, positionCode.trim()));
			}
		}
		logger.debug(METHOD_NAME + "Performed Search...., with branch code " + branchCode);

		Iterator localIterator = context.search(Identity.class, localQueryOptions);

		return localIterator;
	}
	
	public static Identity testSearchActiveIdentityByBranchByPosition(SailPointContext context, String branchCode,
			String positionCode) throws GeneralException {

		String METHOD_NAME = "::searchActiveIdentityByBranchByPosition::";

		logger.debug(METHOD_NAME + "Inside...");

		QueryOptions localQueryOptions = new QueryOptions();
		Identity identity = null;
		/*
		 * localQueryOptions.add(Filter.eq(IdentityAttribute.STATUS,
		 * IdentityStatus.ACTIVE_EMPLOYEE));
		 */

		localQueryOptions.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.INACTIVE_EMPLOYEE));
		localQueryOptions.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.TERMINATED_EMPLOYEE));

		if (CommonUtil.isNotEmptyString(branchCode))
			localQueryOptions.addFilter(Filter.eq(IdentityAttribute.BRANCH_CODE, branchCode));

		if (CommonUtil.isNotEmptyString(positionCode)) {

			if (positionCode.split(",").length > 1) {
				localQueryOptions.addFilter(
						Filter.in(IdentityAttribute.POSITION_CODE, getListFromArray(positionCode.trim().split(","))));
			} else {
				localQueryOptions.addFilter(Filter.eq(IdentityAttribute.POSITION_CODE, positionCode.trim()));
			}
		}
		logger.debug(METHOD_NAME + "Performed Search...., with branch code " + branchCode);

		Iterator localIterator = context.search(Identity.class, localQueryOptions);
		identity = (Identity) localIterator.next();
		return identity;
	}

	/**
	 * 
	 * 
	 * @param context
	 * @param branchCode
	 * @return
	 * @throws GeneralException
	 */

	public static Iterator searchActiveIdentityByBranchByJobCode(SailPointContext context, String branchCode,
			String jobcode) throws GeneralException {
		// ini method baru
		String METHOD_NAME = "::searchActiveIdentityByBranchByJobCode::";

		logger.debug(METHOD_NAME + "Inside...");

		QueryOptions localQueryOptions = new QueryOptions();

		/*
		 * localQueryOptions.add(Filter.eq(IdentityAttribute.STATUS,
		 * IdentityStatus.ACTIVE_EMPLOYEE));
		 */

		localQueryOptions.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.INACTIVE_EMPLOYEE));
		localQueryOptions.add(Filter.ne(IdentityAttribute.STATUS, IdentityStatus.TERMINATED_EMPLOYEE));

		if (CommonUtil.isNotEmptyString(branchCode))
			localQueryOptions.addFilter(Filter.eq(IdentityAttribute.BRANCH_CODE, branchCode));

		if (CommonUtil.isNotEmptyString(jobcode)) {

			if (jobcode.split(",").length > 1) {
				localQueryOptions
						.addFilter(Filter.in(IdentityAttribute.JOB_CODE, getListFromArray(jobcode.split(","))));
			} else {
				localQueryOptions.addFilter(Filter.eq(IdentityAttribute.JOB_CODE, jobcode));
			}
		}

		/*
		 * if (CommonUtil.isNotEmptyString(jobcode)){
		 * if(jobcode.split(",").length > 1){
		 * localQueryOptions.addFilter(Filter.in(IdentityAttribute.JOB_CODE,
		 * getListFromArray(positionCode.split(",")))); }else{
		 * localQueryOptions.addFilter(Filter.eq(IdentityAttribute.JOB_CODE,
		 * jobcode)); }
		 * 
		 * }
		 */

		logger.debug(METHOD_NAME + "Performed Search...., with branch code " + branchCode);

		Iterator localIterator = context.search(Identity.class, localQueryOptions);

		return localIterator;
	}

	/**
	 * @param context
	 * @param workgroupName
	 * @return
	 * @throws GeneralException
	 */
	public static Identity getWorkgroupFromWorkGroupName(SailPointContext context, String workgroupName)
			throws GeneralException {
		String METHOD_NAME = "::getWorkgroupFromWorkGroupName::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside... with workgroupname " + workgroupName);
		Identity workgroup = null;
		workgroup = context.getObjectByName(Identity.class, workgroupName);

		if (workgroup == null) {
			logger.debug(CLASS_NAME + METHOD_NAME + "workgroupname :" + workgroupName + " tidak ditemukan");
			return workgroup;
		}

		if (workgroup.isWorkgroup()) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Workgroup Name: " + workgroupName
					+ " is a workgroup... hence returning the identity : " + workgroup.getName());
			return workgroup;
		} else {
			logger.debug(CLASS_NAME + METHOD_NAME + "Workgroup Name: " + workgroupName
					+ " is not a workgroup... and is a pure identity hence returning null");
			return workgroup;
		}

	}

	public static Object getSingleObjectFromIterator(Iterator it) {

		Object obj = null;

		while (it.hasNext()) {
			obj = it.next();
		}

		return obj;
	}

	private static Collection getListFromArray(String[] arr) {
		Collection lst = null;

		if (arr != null && arr.length >= 1) {
			lst = new ArrayList();
			int len = arr.length;
			for (int i = 0; i < len; i++) {
				lst.add(arr[i].trim());
			}
		}

		return lst;
	}

	public static void writeFile(String filePath, String content, String lineHeader) {
		File outputFile = new File(filePath);
		FileWriter fw = null;
		try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
				content = lineHeader + "\n" + content;
				fw = new FileWriter(outputFile.getAbsoluteFile());
			} else {
				fw = new FileWriter(outputFile.getAbsoluteFile(), true);
			}

			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getApplicationNameByRoleName(SailPointContext ctx, String roleName) throws GeneralException{
		
		String methodName = "::getApplicationNameByRoleName::";
		
		String applicationName = "";
		
		Bundle bundle = ctx.getObjectByName(Bundle.class, roleName);
		
		if(bundle != null){
			
			logger.debug("bundle : " + bundle.toXml());
			
			applicationName = bundle.getAttribute("applicationName").toString();
			
		}else{
			logger.debug(methodName + " tidak ada role dengan nama " + roleName);
		}
		
		logger.debug(methodName + " return value " + applicationName);
		
		return applicationName;
	}
	
	

	

	public static String getBcaSystemConfig(SailPointContext context, String key) throws GeneralException {

		String value = null;

		Custom custom = getCustomObject(context, CustomObject.BCA_SYSTEM_CONFIG);

		Attributes attr = custom.getAttributes();

		Map map = attr.getMap();

		value = (String) map.get(key);

		return value;
	}
	
	public static Iterator getPendingRequestByApplication(SailPointContext context, String applicationName, String nativeIdentity) throws GeneralException {
		Iterator it = null;
		
		QueryOptions localQueryOptions = new QueryOptions();
		
		if(applicationName.equalsIgnoreCase(CommonUtil.AD_APPLICATION)) {
						
			localQueryOptions.addFilter(Filter.eq("application", CommonUtil.AD_APPLICATION));
			
			localQueryOptions.addFilter(Filter.eq("name", "*password*"));
				
		}else if(applicationName.equalsIgnoreCase(CommonUtil.RACF_APPLICATION_NAME) ) {		
			
			localQueryOptions.addFilter(Filter.eq("application", CommonUtil.RACF_APPLICATION_NAME));
			
			localQueryOptions.addFilter(Filter.eq("name", "PASSWORD"));
			
		} else if( applicationName.equalsIgnoreCase(CommonUtil.BASE24_FILE_FEED_APPLICATION)) {		
			
			localQueryOptions.addFilter(Filter.eq("application", CommonUtil.BASE24_FILE_FEED_APPLICATION));
			
			localQueryOptions.addFilter(Filter.eq("name", "PASSWORD"));
			
		}
		
		localQueryOptions.addFilter(Filter.eq("approvalState", (String)"Pending"));
		
		localQueryOptions.addFilter(Filter.eq("nativeIdentity", nativeIdentity));
		
		it = context.search(IdentityRequestItem.class, localQueryOptions);

		return it;
	}

	// TODO Added by Joy

	public static QueryOptions getDelegationQuery(InternalContext context, String loggedInUser)
			throws GeneralException {
		String METHOD_NAME = "::getDelegationQuery::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		SailPointContext spContext = context.getContext();
		Identity identity = spContext.getObjectById(Identity.class, loggedInUser);
		QueryOptions qo = new QueryOptions();
		String branchCodeValue = (String) identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		logger.debug(CLASS_NAME + METHOD_NAME + "branchCodeValue: " + branchCodeValue);

		if (branchCodeValue != null) {
			if (!branchCodeValue.equalsIgnoreCase("")) {
				Filter f = Filter.eq(IdentityAttribute.BRANCH_CODE, branchCodeValue);
				logger.debug(CLASS_NAME + METHOD_NAME + "Final Filter: " + f.toXml());
				qo.add(f);
			}
		}
		// TODO Uncomment below line to take echelon into account if BCA
		// confirms later.
		/*
		 * 
		 * Filter f1 = null; String echelonValue = (String)
		 * identity.getAttribute(IdentityAttribute.ECHELON); if (echelonValue !=
		 * null) { if (!echelonValue.equalsIgnoreCase("")) {
		 * 
		 * f1 = Filter.eq(IdentityAttribute.ECHELON, echelonValue); } } Filter
		 * finalFilter =null; if(f1!=null){ finalFilter = Filter.and(f, f1); }
		 * qo.add(finalFilter);
		 */

		logger.debug(CLASS_NAME + METHOD_NAME + "Final Query: " + qo.toString());
		return qo;
	}

	/*public static void main(String args[]) {
		String positionCode = "00220260, 00005291, 00005199";

		System.out.println(positionCode.split(","));

		// String str1 = "05";
		// String str2 = "054";
		// System.out.println(str1.substring(0, 1));
		// // System.out.println(getEmployeeId("56304"));
	}*/
}
