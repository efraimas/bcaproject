package sailpoint.common;

public class ActiveDirectoryAttribute {

	// Active Directory Default Connector Attributes....
	public static String ACCOUNT_FLAGS = "accountFlags";
	public static String BUSINESS_CATAGORY = "businessCategory";
	public static String CAR_LICENSE = "carLicense";
	public static String COMMON_NAME = "cn";
	public static String DEPARTMENT = "department";
	public static String DEPARTMENT_NUMBER = "departmentNumber";
	public static String DESCRIPTION = "description";
	public static String DESTINATION_INDICATOR = "destinationIndicator";
	public static String DISPLAY_NAME = "displayName";
	public static String DISTINGUISHED_NAME = "distinguishedName";
	public static String EMPLOYEE_ID = "employeeID";
	public static String EMPLOYEE_TYPE = "employeeType";
	public static String FACIMILE_TELEPHONE_NUMBER = "facsimileTelephoneNumber";
	public static String GIVEN_NAME = "givenName";
	public static String EXCHANGE_DATABASE = "homeMDB";
	public static String HOME_TELEPHONE_NUMBER = "homePhone";
	public static String HOME_POSTAL_ADDRESS = "homePostalAddress";
	public static String INITIALS = "initials";
	public static String INTERNATIONAL_ISDN_NUMBER = "internationaliSDNNumber";
	public static String LOCALTION = "l";
	public static String MAIL = "mail";
	public static String MAIL_NICK_NAME = "mailNickname";
	public static String MANAGER = "manager";
	public static String MEMBER_OF = "memberOf";
	public static String MOBILE = "mobile";
	public static String MS_EXCHANGE_HIDE_FROM_ADDRESS_LISTS = "msExchHideFromAddressLists";
	public static String IS_DIAL_IN_ALLOWED = "msNPAllowDialin";
	public static String CALLING_STATION_ID = "msNPCallingStationID";
	public static String CALL_BACK_NUMBER = "msRADIUSCallbackNumber";
	public static String MS_RADIUS_FRAMED_IP_ADDRESS = "msRADIUSFramedIPAddress";
	public static String MS_RADIUS_FRAMED_ROUTE = "msRADIUSFramedRoute";
	public static String ORGANIZATION = "o";
	public static String OBJECT_CLASS = "objectClass";
	public static String OBJECT_SID = "objectSid";
	public static String ORGANIZATIONAL_UNIT = "ou";
	public static String PAGER = "pager";
	public static String PHYSICAL_DELIVERY_OFFICE_NAME = "physicalDeliveryOfficeName";
	public static String POST_OFFICE_BOX = "postOfficeBox";
	public static String POSTAL_ADDRESS = "postalAddress";
	public static String POSTAL_CODE = "postalCode";
	public static String PREFERRED_DELIVEREY_METHOD = "preferredDeliveryMethod";
	public static String PREFERRED_LANGUAGE = "preferredLanguage";
	public static String PRIMARY_GROUP_DN = "primaryGroupDN";
	public static String PRIMARY_GROUP_ID = "primaryGroupID";
	public static String REGISTERED_ADDRESS = "registeredAddress";
	public static String ROOM_NUMBER = "roomNumber";
	public static String SAM_ACCOUNT_NAME = "sAMAccountName";
	public static String SECRETARY = "secretary";
	public static String SEEL_ALSO = "seeAlso";
	public static String SURNAME = "sn";
	public static String STATE = "st";
	public static String STREET = "street";
	public static String TELEPHONE_NUMBER = "telephoneNumber";
	public static String TELETEX_TERMINAL_IDENTIFIER = "teletexTerminalIdentifier";
	public static String TELEX_NUMBER = "telexNumber";
	public static String TITLE = "title";
	public static String USER_IDENTIFIER = "uid";
	public static String USER_ACCOUNT_CONTROL = "userAccountControl";
	public static String ACCOUNT_EXPIRES = "accountExpires";
	public static String PASSWORD = "*password*";

	// Active Directory Attributes as described in Technical Design Document...
	// public static String EMPLOYEE_ID = "employeeid";
	public static String IS_HR_MANAGER = "isHRManaged";
	public static String END_DATE = "enddate";
	public static String SALUTATION = "salutationname";
	public static String DATE_OF_BIRTH = "dob";
	public static String STATUS = "status";
	public static String NAME = "name";
	public static String CITY = "city";
	public static String PROVINCE = "province";

	// public static String TELEPHONE_NUMBER = "telephonenumber";
	public static String EMAIL = "mail";
	public static String SUBDIVISION_CODE = "subdivisioncode";
	public static String SUBDIVISION_NAME = "subdivisionname";
	public static String DATE_OF_JOINING = "doj";
	public static String JOB_CODE = "jobcode";
	public static String JOB_DESCRIPTION = "jobdescription";
	public static String BRANCH_CODE = "branchcode";
	public static String BRANCH_NAME = "branchname";
	public static String REGION_CODE = "regioncode";
	public static String POSITION_CODE = "positioncode";
	public static String POSITION_NAME = "positionname";
	public static String MANAGER_ID = "managerid";
	public static String ECHELON = "echelon";
	public static String DIVISION_CODE = "divisioncode";
	public static String DIVISION_NAME = "divisionname";
	public static String COST_CENTER = "costcenter";
	public static String PWD_LAST_SET = "pwdLastSet";

	// Exchange Specific attributes as for IIQ for AD and Exchange, its the same
	// connector...
	// TODO... define the attributes below....
	
	public static String HOME_MDB = "homeMDB";
	
	public static String DATABASE_INTERNAL = "database internal";
	public static String DATABASE_EKSTERNAL = "database eksternal";
	public static String INTERNAL_DOMAIN = "internal domain";
	public static String EKSTERNAL_DOMAIN = "eksternal domain";
	public static String VIP_DOMAIN = "vip domain";
	
	public static String EMAIL_DOMAIN_INTERNAL = "@dti.co.id";
	public static String EMAIL_DOMAIN_EKSTERNAL = "@bca.co.id";
	public static String EMAIL_DOMAIN_INTRA = "@intra.bca";
	public static String EMAIL_DOMAIN_VIP = "@bca.co.id";
}
