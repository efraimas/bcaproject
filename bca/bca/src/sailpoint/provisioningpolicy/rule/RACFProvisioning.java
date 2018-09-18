package sailpoint.provisioningpolicy.rule;

import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityRequesterType;
import sailpoint.common.RACFProfile;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class RACFProvisioning {

	public static String CLASS_NAME = "::RACFProvisioning::";

	public static Logger logger = Logger
			.getLogger("sailpoint.provisioningpolicy.rule.RACFProvisioning");

	/**
	 * @param context
	 * @param branchCode
	 * @return
	 */
	public static String getUserIDFromBranchCode(SailPointContext context,
			String branchCode) {
		String METHOD_NAME = "::getUserIDFromBranchCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String val = "";
		String prefix = "";

		if (branchCode == null || branchCode.equalsIgnoreCase("")) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Branch Code is Either empty or null....");
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "branchCode: " + branchCode);

		if (branchCode.length() > 4) {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Branch Code Length More than 4 hence cannot process... ");
			return val;
		}

		int branchCodeInt = Integer.parseInt(branchCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code: " + branchCodeInt);

		if (branchCodeInt >= 1 && branchCodeInt < 499) {
			val = "B"
					+ branchCode.substring((branchCode.length() - 3),
							branchCode.length());
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "
					+ val);
			return val;
		} else if (branchCodeInt >= 5000 && branchCodeInt <= 5999) {
			val = "C"
					+ branchCode.substring((branchCode.length() - 3),
							branchCode.length());
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "
					+ val);
			return val;
		} else if (branchCodeInt >= 6000 && branchCodeInt <= 6999) {
			val = "D"
					+ branchCode.substring((branchCode.length() - 3),
							branchCode.length());
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "
					+ val);
			return val;
		} else if (branchCodeInt >= 7000 && branchCodeInt <= 7999) {
			val = "E"
					+ branchCode.substring((branchCode.length() - 3),
							branchCode.length());
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "
					+ val);
			return val;
		} else if (branchCodeInt >= 8000 && branchCodeInt <= 8999) {
			val = "F"
					+ branchCode.substring((branchCode.length() - 3),
							branchCode.length());
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "
					+ val);
			return val;
		} else if (branchCodeInt >= 900 && branchCodeInt <= 999) {
			val = "B"
					+ branchCode.substring((branchCode.length() - 3),
							branchCode.length());
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning UserID Prefix: "
					+ val);
			return val;
		}

		// Getting only the last 3 character of branch code...
		prefix = branchCode.substring((branchCode.length() - 3),
				branchCode.length());
		logger.debug(CLASS_NAME + METHOD_NAME + "Prefix Found: " + prefix);

		return val;

	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static String getRequesterType(SailPointContext context,
			Identity identity) {
		String METHOD_NAME = "::getServiceType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String requesterType = "";

		return requesterType;
	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static String getUserType(SailPointContext context, Identity identity) {

		String METHOD_NAME = "::getServiceType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String userType = "";
		return userType;
	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static String getServiceType(SailPointContext context,
			Identity identity) {
		String METHOD_NAME = "::getServiceType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String serviceType = "";

		return serviceType;
	}

	/**
	 * Read the Region Code to MF Group Mapping custom object and figure out the
	 * MF Group for the user....
	 * 
	 * @param context
	 * @param regionCode
	 * @return
	 * @throws GeneralException
	 */
	public static String getMFGroupFromRegionCode(SailPointContext context,
			String regionCode) throws GeneralException {
		String METHOD_NAME = "::getMFGroupFromRegionCode::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String mfGroupName = "";

		Custom mfGroupCustomObject = CommonUtil.getCustomObject(context,
				CustomObject.BCA_REGION_CODE_TO_MF_GROUP_MAPPING);

		Attributes localAttributes = mfGroupCustomObject.getAttributes();
		Map groupMap = localAttributes.getMap();
		logger.debug(CLASS_NAME + METHOD_NAME + "Map Size: " + groupMap.size()
				+ " Map String: " + groupMap.toString());

		if (groupMap.containsKey(regionCode)) {
			mfGroupName = (String) groupMap.get(regionCode);

			if (mfGroupName.equalsIgnoreCase("")) {
				logger.debug(CLASS_NAME
						+ METHOD_NAME
						+ "mfGroupName is blank.... it can be a Head Office region code...");
			} else {
				logger.debug(CLASS_NAME + METHOD_NAME + "mfGroupName found: "
						+ mfGroupName
						+ " hence returning this MF Group Name.....");
				return mfGroupName;
			}
		}

		logger.debug(CLASS_NAME + METHOD_NAME
				+ "Returning mfGroupName as blank... ");
		return mfGroupName;

	}

	/**
	 * @param context
	 * @param identity
	 * @return
	 */
	public static String getFinalLetterFromRequesterType(
			SailPointContext context, String profileName, String positionCode) {
		String METHOD_NAME = "::getFinalLetterFromRequesterType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");

		String finalLetter = "";

		logger.debug(CLASS_NAME + METHOD_NAME + "profileName: " + profileName);

		// profileNameString profileName = ""; // TODO get the type...
		
		if(IdentityRequesterType.POSITION_CODE_KALAY_KCU.equalsIgnoreCase(positionCode)){
			if (profileName.equalsIgnoreCase(RACFProfile.INQUIRY_INQ_REKENING_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_ST_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STG_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STD_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGD_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGDC_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGC_PRIORITAS)
					|| profileName
					.equalsIgnoreCase(RACFProfile.SUPERVISOR_ST_INQ_REK_GAJI_KARYAWAN)
					|| profileName
							.equalsIgnoreCase(RACFProfile.SUPERVISOR_STG_INQ_REK_GAJI_KARYAWAN)
					|| profileName
							.equalsIgnoreCase(RACFProfile.SUPERVISOR_STD_INQ_REK_GAJI_KARYAWAN)
					|| profileName
							.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGD_INQ_REK_GAJI_KARYAWAN)
					|| profileName
							.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGDC_INQ_REK_GAJI_KARYAWAN)
					|| profileName
							.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGC_INQ_REK_GAJI_KARYAWAN)
					|| profileName.equalsIgnoreCase(RACFProfile.INQUIRY_INQ_REKENING_GAJI_KARYAWAN)		
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_INQUIRY_INQ_PRIORITAS)){
				
				finalLetter = "M";
				logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: " + finalLetter);
				return finalLetter;
				
			}else if (profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGDC_INQ_REQ_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGC_INQ_REQ_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGD_INQ_REQ_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STD_INQ_REQ_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STG_INQ_REQ_NASABAH_PRIORITAS)){
				
				finalLetter = "L";
				logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: " + finalLetter);
				return finalLetter;
			}else if (profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGDC_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGC_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGD_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STD_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)
					|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STG_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)){
				finalLetter = "K";
				logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: " + finalLetter);
				return finalLetter;
			}
		}else if (profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGDC_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGC_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGD_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STD_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STG_INQ_REQ_NASABAH_PRIORITAS)){
			
			finalLetter = "L";
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: " + finalLetter);
			return finalLetter;
		}else if (profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGDC_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGC_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STGD_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STD_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_STG_INQ_REQ_GAJI_KARYAWAN_NASABAH_PRIORITAS)){
			finalLetter = "K";
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: " + finalLetter);
			return finalLetter;
		}

		if (profileName.equalsIgnoreCase(RACFProfile.TELLER_GFO)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_TELLER_TFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_TFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_DFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_GDFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_TGFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_TGDFO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_DBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_DFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GDBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GDFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGDBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGDFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TDBO)
				|| profileName.equalsIgnoreCase(RACFProfile.CASH_VAULT_CV)
				|| profileName.equalsIgnoreCase(RACFProfile.CUSTOMER_SERVICE_CSO)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_INQUIRY_INQ)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_USER_PROFILE_INQUIRY_INQ)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_WEEKEND_BANKING_USER_PROFILE_INQUIRY_INQ)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_VIDEO_BANKING_USER_PROFILE_INQUIRY_INQ)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_PRIORITAS_USER_PROFILE_INQUIRY_INQ)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_TELLER_GFO)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_TELLER_TFO)){
			finalLetter = "T";
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: "
					+ finalLetter);
			return finalLetter;

		}
		
		if (profileName.equalsIgnoreCase(RACFProfile.IDS_SOLITAIRE_SUPERVISOR_ST_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STD_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STG_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STGD_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STGC_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_SOLITAIRE_SUPERVISOR_STGDC_INQ_REQ_NASABAH_PRIORITAS)
				
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_PRIORITAS_SUPERVISOR_ST_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_PRIORITAS_SUPERVISOR_STD_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_PRIORITAS_SUPERVISOR_STG_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_PRIORITAS_SUPERVISOR_STGD_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_PRIORITAS_SUPERVISOR_STGC_INQ_REQ_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_PRIORITAS_SUPERVISOR_STGDC_INQ_REQ_NASABAH_PRIORITAS)
				
				){
			finalLetter = "L"; //tambahkan sesuai layanan
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: " + finalLetter);
			return finalLetter;
		}
		
		if (profileName
				.equalsIgnoreCase(RACFProfile.INQUIRY_INQ_REKENING_NASABAH_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_ST_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STG_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STD_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGD_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGDC_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGC_PRIORITAS)){
			finalLetter = "L";
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: "
					+ finalLetter);
			return finalLetter;
		}
		
		

		if (profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SG)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_ST)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STG)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SCSO)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGDC)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGC)
				
				//ditambahkan baru
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SG)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_ST)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SD)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STG)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SGD)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STD)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGD)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_SCSO)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGDC)
				|| profileName.equalsIgnoreCase(RACFProfile.WEEKEND_BANKING_SUPERVISOR_STGC)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SG)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_ST)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SD)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STG)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SGD)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STD)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGD)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_SCSO)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGDC)
				|| profileName.equalsIgnoreCase(RACFProfile.PRIORITAS_SUPERVISOR_STGC)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SG)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_ST)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SD)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STG)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SGD)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STD)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGD)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_SCSO)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGDC)
				|| profileName.equalsIgnoreCase(RACFProfile.VIDEO_BANKING_SUPERVISOR_STGC)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_REGULER_SUPERVISOR_SG)
				//
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110)) {
			finalLetter = "S";
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: "
					+ finalLetter);
			return finalLetter;
		}
		
		if (profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STGDC)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STGC)  // 102
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STG)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STD)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_ST)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_SGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_SG)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_SD)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_SCSO)
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_BRANCH_SIGN_ON_7110)) {
			finalLetter = "M";
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: "
					+ finalLetter);
			return finalLetter;
		}

		if (profileName
				.equalsIgnoreCase(RACFProfile.SUPERVISOR_ST_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STG_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STD_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGD_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGDC_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGC_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_ST_INQ_PRIORITAS)
				|| profileName
						.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STD_INQ_PRIORITAS)
				|| profileName
						.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STG_INQ_PRIORITAS)
				|| profileName
						.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGC_INQ_PRIORITAS)
				|| profileName
						.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGD_INQ_PRIORITAS)
				|| profileName
						.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_SUPERVISOR_STGDC_INQ_PRIORITAS)
				|| profileName.equalsIgnoreCase(RACFProfile.INQUIRY_INQ_REKENING_GAJI_KARYAWAN)		
				|| profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_INQUIRY_INQ_PRIORITAS)) {
			finalLetter = "K";
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: "
					+ finalLetter);
			return finalLetter;
		}
		
		// Ditambahkan Untuk IDS KP 
		
		if(profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_9650)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0986)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0987)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0973)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0970)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0969)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0968)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0965)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0958)
				||profileName.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_KANTOR_PUSAT_0959)){
			finalLetter = "T";
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning finalLetter: " + finalLetter);
		}

		return finalLetter;
	}

	/**
	 * @param prefixConstant
	 * @param prefix
	 * @param sequencePrefix
	 * @param sequence
	 * @param finalLetter
	 * @return
	 */
	public static String generateRACFUserIDString(String prefix,
			String sequencePrefix, String sequence, String finalLetter) {
		String METHOD_NAME = "::generateRACFUserIDString::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		String val = "";
		val = prefix + sequencePrefix + sequence + finalLetter;
		logger.debug(CLASS_NAME + METHOD_NAME + "Value: " + val);

		return val;

	}

	/**
	 * If there is any addition or deletion of profile please make change to
	 * RACFProfile first... and then put the final letter return logic into this
	 * method....
	 * 
	 * Context is sent just like that and is never used... you can make change
	 * if you would like to....
	 * 
	 * @param context
	 * @param profileName
	 * @return
	 */

	public static String getSequenceNumberFromRequesterType(
			SailPointContext context, String profileName) {
		String METHOD_NAME = "::getFinalLetterFromRequesterType::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside....");

		String sequenceNumber = "";
		logger.debug(CLASS_NAME + METHOD_NAME + "Profile Name: " + profileName);

		if (profileName.equalsIgnoreCase(RACFProfile.TELLER_GFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_TFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_DFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_GDFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_TGFO)
				|| profileName.equalsIgnoreCase(RACFProfile.TELLER_TGDFO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_DBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_DFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GDBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GDFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_GBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGDBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TGDFBO)
				|| profileName.equalsIgnoreCase(RACFProfile.OPERATOR_TDBO)
				|| profileName.equalsIgnoreCase(RACFProfile.CASH_VAULT_CV)
				|| profileName
						.equalsIgnoreCase(RACFProfile.CUSTOMER_SERVICE_CSO)
				|| profileName
						.equalsIgnoreCase(RACFProfile.IDS_USER_PROFILE_INQUIRY_INQ)) {
			sequenceNumber = "T";// TODO Not sure what this one will be... need
									// to
									// clarify....
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Returning sequenceNumber: " + sequenceNumber);
			return sequenceNumber;

		}

		if (profileName
				.equalsIgnoreCase(RACFProfile.INQUIRY_INQ_REKENING_GAJI_KARYAWAN)) {
			sequenceNumber = "7";
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Returning sequenceNumber: " + sequenceNumber);
			return sequenceNumber;
		}
		if (profileName
				.equalsIgnoreCase(RACFProfile.INQUIRY_INQ_REKENING_NASABAH_PRIORITAS)) {
			sequenceNumber = "7";
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Returning sequenceNumber: " + sequenceNumber);
			return sequenceNumber;
		}

		if (profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SG)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_ST)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STG)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_SCSO)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGDC)
				|| profileName.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGC)) {
			sequenceNumber = "8";
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Returning sequenceNumber: " + sequenceNumber);
			return sequenceNumber;
		}
		
		if (profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STGDC)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STGC)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STG)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_STD)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_ST)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_SGD)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_SG)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_SD)
				|| profileName.equalsIgnoreCase(RACFProfile.SOLITAIRE_SUPERVISOR_SCSO)) {
			sequenceNumber = "7";
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Returning sequenceNumber: " + sequenceNumber);
			return sequenceNumber;
		}

		if (profileName
				.equalsIgnoreCase(RACFProfile.SUPERVISOR_ST_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STG_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STD_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGD_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGDC_INQ_REK_GAJI_KARYAWAN)
				|| profileName
						.equalsIgnoreCase(RACFProfile.SUPERVISOR_STGC_INQ_REK_GAJI_KARYAWAN)) {
			sequenceNumber = "7";
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Returning sequenceNumber: " + sequenceNumber);
			return sequenceNumber;
		}

		return sequenceNumber;
	}

}
