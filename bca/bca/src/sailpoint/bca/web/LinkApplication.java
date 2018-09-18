package sailpoint.bca.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import sailpoint.common.BcaCalendar;

@SuppressWarnings({ "rawtypes" })
public class LinkApplication {

	public static Logger logger = Logger.getLogger("sailpoint.bca.web.LinkApplication");

	public static String CLASS_NAME = "::LinkApplication::";

	public String applicationName;

	public String userId;

	public String status;

	public String revokedDate;

	public String revokedMonth;

	public String revokedYear;

	public String resumedDate;

	public String resumedMonth;

	public String resumedYear;

	public String extendDate;

	public String extendMonth;

	public String extendYear;

	public String checkedId;

	public String instance;

	public boolean checked;

	public String finalRevokeDate;

	public String finalResumeDate;

	public String bcaApplicationName;

	public String password;

	public String nativeIdentity;

	public String debitLimit;

	public String creditLimit;

	public String functionName;

	public List lstRoles;
	
	public String group;
	
	public List lstEntGroup;

	public int roleSize;

	public String endDate;

	public String newEndDate;

	public String nonEmployeeId;

	public String message;

	/**
	 * @return the newEndDate
	 */
	public String getNewEndDate() {
		return newEndDate;
	}

	/**
	 * @param newEndDate
	 *            the newEndDate to set
	 */
	public void setNewEndDate(String newEndDate) {
		this.newEndDate = newEndDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public LinkApplication() {
		super();
	}

	public LinkApplication(String applicationName, String userId, String status, String checkedId, String instance) {
		super();
		this.applicationName = applicationName;
		this.userId = userId;
		this.status = status;
		this.checkedId = checkedId;
		this.instance = instance;
	}

	public LinkApplication(String functionName, String userId) {
		super();
		this.functionName = functionName;
		this.userId = userId;
	}

	public LinkApplication(String applicationName, String userId, String finalRevokeDate, String finalResumeDate) {
		super();
		this.applicationName = applicationName;
		this.userId = userId;
		this.finalRevokeDate = finalRevokeDate;
		this.finalResumeDate = finalResumeDate;
	}

	public LinkApplication(String userId, String instance, String applicationName) {
		super();
		this.userId = userId;
		this.instance = instance;
		this.applicationName = applicationName;
	}

	/**
	 * @return the roleSize
	 */
	public int getRoleSize() {
		return roleSize;
	}

	/**
	 * @param roleSize
	 *            the roleSize to set
	 */
	public void setRoleSize(int roleSize) {
		this.roleSize = roleSize;
	}

	/**
	 * @return the lstRoles
	 */
	public List getLstRoles() {
		return lstRoles;
	}

	/**
	 * @param lstRoles
	 *            the lstRoles to set
	 */
	public void setLstRoles(List lstRoles) {
		this.lstRoles = lstRoles;
	}
	
	public List getLstEntGroup() {
		return lstEntGroup;
	}

	public void setLstEntGroup(List lstEntGroup) {
		this.lstEntGroup = lstEntGroup;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the debitLimit
	 */
	public String getDebitLimit() {
		return debitLimit;
	}

	/**
	 * @param debitLimit
	 *            the debitLimit to set
	 */
	public void setDebitLimit(String debitLimit) {
		this.debitLimit = debitLimit;
	}

	/**
	 * @return the creditLimit
	 */
	public String getCreditLimit() {
		return creditLimit;
	}

	/**
	 * @param creditLimit
	 *            the creditLimit to set
	 */
	public void setCreditLimit(String creditLimit) {
		this.creditLimit = creditLimit;
	}

	/**
	 * @return the nativeIdentity
	 */
	public String getNativeIdentity() {
		return nativeIdentity;
	}

	/**
	 * @param nativeIdentity
	 *            the nativeIdentity to set
	 */
	public void setNativeIdentity(String nativeIdentity) {
		this.nativeIdentity = nativeIdentity;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the bcaApplicationName
	 */
	public String getBcaApplicationName() {
		return bcaApplicationName;
	}

	/**
	 * @param bcaApplicationName
	 *            the bcaApplicationName to set
	 */
	public void setBcaApplicationName(String bcaApplicationName) {
		this.bcaApplicationName = bcaApplicationName;
	}

	/**
	 * @return the finalRevokeDate
	 */
	public String getFinalRevokeDate() {
		int resumeDate = Integer.parseInt(BcaCalendar.get2Digit(getRevokedDate()));
		int resumeMonth = Integer.parseInt(BcaCalendar.getMonthInt(getRevokedMonth()));
		int resumeYear = Integer.parseInt(getRevokedYear());
		
		final Set<Integer> bulan30 = new HashSet<Integer>(Arrays.asList(4, 6, 9, 11));

		String date = "";
		String month = "";
		String year = "";
		
		date = String.valueOf(resumeDate);
		month = String.valueOf(resumeMonth);
		year = String.valueOf(resumeYear);
		
		if(resumeMonth == 2) {
			logger.debug("Bulan 2");
			if(resumeYear%400==0 || resumeYear%4==0) {
				logger.debug("Kabisat");
				if(resumeDate>=30 && resumeDate<=31) {
					logger.debug("Masuk");
					date = "29";;
				}
			}
			
			else {
				if(resumeDate >= 29 && resumeDate <=31) {
					date = "28";
				}
			}
		}
		
		if(bulan30.contains(resumeMonth)) {
			logger.debug("inside 2");

			if(resumeDate == 31) {
				date = "30";
			}
		}
		
		logger.debug(CLASS_NAME + " Tanggal dari linknya : " + year + month + BcaCalendar.get2Digit(date));
		return year + BcaCalendar.get2DigitMonth(month) + BcaCalendar.get2Digit(date);
	}

	/**
	 * @return the finalResumeDate
	 */
	public String getFinalResumeDate() {
		int resumeDate = Integer.parseInt(BcaCalendar.get2Digit(getResumedDate()));
		int resumeMonth = Integer.parseInt(BcaCalendar.getMonthInt(getResumedMonth()));
		int resumeYear = Integer.parseInt(getResumedYear());

		final Set<Integer> bulan30 = new HashSet<Integer>(Arrays.asList(4, 6, 9, 11));

		String date = "";
		String month = "";
		String year = "";
		
		date = String.valueOf(resumeDate);
		month = String.valueOf(resumeMonth);
		year = String.valueOf(resumeYear);
		
		if(resumeDate == 31) {
			logger.debug("inside 1");
			date = "1";
			
			if(resumeMonth == 12) {
				month = "1";
				resumeYear = resumeYear + 1;
				year = String.valueOf(resumeYear);
			}
			else {
				resumeMonth = resumeMonth + 1;
				month = String.valueOf(resumeMonth);
			}
		}
		else if(bulan30.contains(resumeMonth) && resumeDate == 30) {
			logger.debug("inside 2");
			date = "1";
			resumeMonth = resumeMonth + 1;
			month = String.valueOf(resumeMonth);
		}
		else if(resumeMonth == 2) {
			logger.debug("inside 3");
			
			//Tahun kabisat
			if(resumeYear%400==0 || resumeYear%4==0) {
				logger.debug("Kabisat");
				if(resumeDate>=29 && resumeDate<=31) {
					logger.debug("Masuk");
					date = "1";
					resumeMonth = resumeMonth + 1;
					month = String.valueOf(resumeMonth);
				}
				else {
					resumeDate = resumeDate + 1;
					date = String.valueOf(resumeDate);
				}
			}
			else {
				if(resumeDate>=28 && resumeDate<=31) {
					logger.debug("Masuk");
					date = "1";
					resumeMonth = resumeMonth + 1;
					month = String.valueOf(resumeMonth);
				}
				else {
					resumeDate = resumeDate + 1;
					date = String.valueOf(resumeDate);
				}
			}
		}
		else {
			logger.debug("inside 4");
			resumeDate = resumeDate + 1;
			date = String.valueOf(resumeDate);
		}
		
		
		logger.debug(CLASS_NAME + " Tanggal dari linknya : " + year + month + BcaCalendar.get2Digit(date));
		return year + BcaCalendar.get2DigitMonth(month) + BcaCalendar.get2Digit(date);
	}

	public String getFinalExtendDate() {
		logger.debug(getExtendYear() + getExtendMonth() + getExtendDate());
		return getExtendYear() + BcaCalendar.getMonthInt(getExtendMonth()) + BcaCalendar.get2Digit(getExtendDate());
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked
	 *            the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the revokedMonth
	 */
	public String getRevokedMonth() {
		return revokedMonth;
	}

	/**
	 * @param revokedMonth
	 *            the revokedMonth to set
	 */
	public void setRevokedMonth(String revokedMonth) {
		this.revokedMonth = revokedMonth;
	}

	/**
	 * @return the revokedYear
	 */
	public String getRevokedYear() {
		return revokedYear;
	}

	/**
	 * @param revokedYear
	 *            the revokedYear to set
	 */
	public void setRevokedYear(String revokedYear) {
		this.revokedYear = revokedYear;
	}

	/**
	 * @return the resumedDate
	 */
	public String getResumedDate() {
		return resumedDate;
	}

	/**
	 * @param resumedDate
	 *            the resumedDate to set
	 */
	public void setResumedDate(String resumedDate) {
		this.resumedDate = resumedDate;
	}

	/**
	 * @return the resumedMonth
	 */
	public String getResumedMonth() {
		return resumedMonth;
	}

	/**
	 * @param resumedMonth
	 *            the resumedMonth to set
	 */
	public void setResumedMonth(String resumedMonth) {
		this.resumedMonth = resumedMonth;
	}

	/**
	 * @return the resumedYear
	 */
	public String getResumedYear() {
		return resumedYear;
	}

	/**
	 * @param resumedYear
	 *            the resumedYear to set
	 */
	public void setResumedYear(String resumedYear) {
		this.resumedYear = resumedYear;
	}

	public String getExtendDate() {
		return extendDate;
	}

	public void setExtendDate(String extendDate) {
		this.extendDate = extendDate;
	}

	public String getExtendMonth() {
		return extendMonth;
	}

	public void setExtendMonth(String extendMonth) {
		this.extendMonth = extendMonth;
	}

	public String getExtendYear() {
		return extendYear;
	}

	public void setExtendYear(String extendYear) {
		this.extendYear = extendYear;
	}

	public String getnonEmployeeId() {
		return nonEmployeeId;
	}

	public void setnonEmployeeId(String nonEmployeeId) {
		this.nonEmployeeId = nonEmployeeId;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName
	 *            the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * @return the applicationName
	 */
	public String getfunctionName() {
		return functionName;
	}

	/**
	 * @param applicationName
	 *            the applicationName to set
	 */
	public void setfunctionName(String functionName) {
		this.functionName = functionName;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the revokedDate
	 */
	public String getRevokedDate() {
		return revokedDate;
	}

	/**
	 * @param revokedDate
	 *            the revokedDate to set
	 */
	public void setRevokedDate(String revokedDate) {
		this.revokedDate = revokedDate;
	}

	/**
	 * @return the checkedId
	 */
	public String getCheckedId() {
		return checkedId;
	}

	/**
	 * @param checkedId
	 *            the checkedId to set
	 */
	public void setCheckedId(String checkedId) {
		this.checkedId = checkedId;
	}

	/**
	 * @return the instance
	 */
	public String getInstance() {
		return instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public void setInstance(String instance) {
		this.instance = instance;
	}

}
