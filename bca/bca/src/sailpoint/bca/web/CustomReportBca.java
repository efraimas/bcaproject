package sailpoint.bca.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.common.BcaCalendar;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.IdentityUtil;
import sailpoint.custom.report.BcaCommonUserReport;
import sailpoint.custom.report.CustomReportGenerator;
import sailpoint.object.ApprovalItem;
import sailpoint.object.ApprovalSet;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.ExpansionItem;
import sailpoint.object.Filter;
import sailpoint.object.Filter.MatchMode;
import sailpoint.object.Identity;
import sailpoint.object.IdentityRequest;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.ProvisioningProject;
import sailpoint.object.QueryOptions;
import sailpoint.object.WorkflowSummary.ApprovalSummary;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class CustomReportBca extends AccountsRequestBean{
	
	Logger logger = Logger.getLogger("sailpoint.bca.web.CustomReportBca");
	
	String className = "::CustomReportBca::";
	
	List<String> lstStatus;
	
	List<String> reports;
	
	String status;
	
    String selectedReport;
	
	String tanggal;
	
	String bulan;
	
	String tahun;
	
	List<String> lstTanggal;
	
	List<String> lstBulan;
	
	List<String> lstTahun;
	
	String noRecordFound = "Tidak ada data";
	
	String message;
	
	String downloadFile;
	
	String reportFolder = "report folder";
	
	String whereapp = ""; //for sa app, query where
	
	/**
	 * @return the downloadFiile
	 */
	public String getDownloadFile() {
		return downloadFile;
	}

	/**
	 * @param downloadFile the downloadFile to set
	 */
	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the selectedReport
	 */
	public String getSelectedReport() {
		return selectedReport;
	}

	/**
	 * @param selectedReport the selectedReport to set
	 */
	public void setSelectedReport(String selectedReport) {
		this.selectedReport = selectedReport;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the lstStatus
	 */
	public List<String> getLstStatus() {
		lstStatus = new ArrayList<String>();
		lstStatus.add((String)"All ");
		lstStatus.add((String)"Completed");
		lstStatus.add((String)"Pending");
		lstStatus.add((String)"Reject");
		return lstStatus;
	}

	/**
	 * @return the reports
	 */
	public List<String> getReports() {
		return reports;
	}
	
	

	/**
	 * @param reports the reports to set
	 */
	public void setReports(List<String> reports) {
		this.reports = reports;
	}

	/**
	 * @return the tanggal
	 */
	public String getTanggal() {
		return tanggal;
	}

	/**
	 * @param tanggal the tanggal to set
	 */
	public void setTanggal(String tanggal) {
		this.tanggal = tanggal;
	}

	/**
	 * @return the bulan
	 */
	public String getBulan() {
		return bulan;
	}

	/**
	 * @param bulan the bulan to set
	 */
	public void setBulan(String bulan) {
		this.bulan = bulan;
	}

	/**
	 * @return the tahun
	 */
	public String getTahun() {
		return tahun;
	}

	/**
	 * @param tahun the tahun to set
	 */
	public void setTahun(String tahun) {
		this.tahun = tahun;
	}

	/**
	 * @param lstTanggal the lstTanggal to set
	 */
	public void setLstTanggal(List<String> lstTanggal) {
		this.lstTanggal = lstTanggal;
	}

	/**
	 * @param lstBulan the lstBulan to set
	 */
	public void setLstBulan(List<String> lstBulan) {
		this.lstBulan = lstBulan;
	}

	/**
	 * @param lstTahun the lstTahun to set
	 */
	public void setLstTahun(List<String> lstTahun) {
		this.lstTahun = lstTahun;
	}
	
	
	/**
	 * @return the lstTanggal
	 */
	public List<String> getLstTanggal() {
		return lstTanggal;
	}

	/**
	 * @return the lstBulan
	 */
	public List<String> getLstBulan() {
		return lstBulan;
	}

	/**
	 * @return the lstTahun
	 */
	public List<String> getLstTahun() {
		return lstTahun;
	}

	public String getReportDate() {
		return BcaCalendar.get2Digit(getTanggal()) + "/" + BcaCalendar.getMonthInt(getBulan()) + "/" + getTahun();
	}

	@SuppressWarnings("static-access")
	public CustomReportBca() throws GeneralException {
		
		super();
		
		Identity emp = getIdentity();
		
		boolean isKpEmp = CommonUtil.isHQEmployee(emp);
		
		String branchCode = (String)emp.getAttribute(IdentityAttribute.BRANCH_CODE);
		
		logger.debug(className + " logged in username " + getLoggedInUserName());
		
		if("spadmin".equalsIgnoreCase(getLoggedInUserName())){
			isKpEmp = true;
			branchCode = "0998";
		}
		
		logger.debug("branchCode is : " + branchCode);
		boolean isKcuEmp = CommonUtil.isMainBranchCode(getContext(), branchCode);
		
		
		BcaCalendar cal = new BcaCalendar();
		setLstTanggal(cal.getLstTanggal());
		setLstBulan(cal.getLstBulan());
		setLstTahun(cal.getLstTahun());
		
		if(getTanggal() == null){
			setTanggal(cal.getTanggal());
		}
		
		if(getBulan() == null){
			setBulan(cal.getBulan());
		}
		
		if(getTahun() == null){
			setTahun(cal.getTahun());
		}
		
		logger.debug(className + " is KCU Emp " + isKcuEmp);
		
		/*if(isKcuEmp){
			Map subBranchMap = getBranchKcuMap(branchCode);
		}*/
		
		List<String> lstReport = new ArrayList<String>();
		lstReport.add("--Pilih Laporan--");
		if(isChecker1(getLoggedInUser())){
			lstReport.add("Laporan Pengelolaan User Id"); //Checker1
		}
		else if(isChecker2(getLoggedInUser()) || "spadmin".equalsIgnoreCase(getLoggedInUserName())){
			lstReport.add("Laporan Pembuatan User Id Baru Base 24"); //SA Skes
			lstReport.add("Laporan Penonaktifan User Id Base 24"); //SA Skes
			lstReport.add("Laporan Pengaktifan Kembali User Id Base 24"); //SA Skes
			lstReport.add("Laporan User Id Base 24 Yang Harus diaktifkan Keesokan Harinya"); //SA Skes
			lstReport.add("Laporan Reset Password Base 24"); //SA Skes
			lstReport.add("Laporan Penghapusan User Id Base 24"); //SA Skes
			lstReport.add("Rekapitulasi Pengelolaan User Id"); //SA Skes
			lstReport.add("Rekapitulasi Pengelolaan User Id - Pending dan Reject"); //SA Skes
			lstReport.add("Laporan Pengelolaan User Id Aplikasi"); //spadmin
		}
		else if(isCheckerApp(getLoggedInUser())){
			lstReport.add("Laporan Pengelolaan User Id Aplikasi"); //SA App)
		}
		
		setReports(lstReport);
		
	}
	
	public CustomReportBca(String test){
		super();
	}
	
	private long getStartTimeDate(Date date){
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		return cal.getTimeInMillis();
		
	}
	
	private long getEndTimeDate(Date date){
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, 24);
		
		return cal.getTimeInMillis();
	}
	
	public void generateReport(){
		
		String methodName = "::submitRequest::";
		
		logger.debug(className + methodName + " generate report start at " + new Date());
		
		Identity identity = null;
		
		String reportDate = getReportDate();
		
		try {
			
			identity = getLoggedInUser();
			
		} catch (GeneralException e) {
			
			e.printStackTrace();
			
		//	return "failed";
		}
		
		String branchCode = identity.getAttribute(IdentityAttribute.BRANCH_CODE) == null ? "" : (String)identity.getAttribute(IdentityAttribute.BRANCH_CODE);
		
		String branchName = identity.getAttribute(IdentityAttribute.BRANCH_NAME) == null ? "" : (String)identity.getAttribute(IdentityAttribute.BRANCH_NAME);
		
		logger.debug(className + methodName + " branch code " + branchCode);
		
		boolean isKpEmp = CommonUtil.isHQEmployee(identity);
		
		logger.debug(className + methodName + " is KP employee " + isKpEmp);
		
		boolean isKcuEmp = false;
		
		try {
			isKcuEmp = CommonUtil.isMainBranchCode(getContext(), branchCode);
		} catch (GeneralException e) {
			
			e.printStackTrace();
			
			//return "failed";
		}
		
		logger.debug(className + methodName + " is KCU employee " + isKcuEmp);
			
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		Date date = new Date();
		try {
			date = df.parse(reportDate);
			
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		logger.debug("date pencarian : " + date);
		long timeBeginDate = getStartTimeDate(date);
		long timeEndDate = getEndTimeDate(date);
		
		logger.debug(className + methodName + " begin " + timeBeginDate + " , end " + timeEndDate);
		
		try {
			if("Laporan Pengelolaan User Id".equalsIgnoreCase(getSelectedReport())){
				logger.debug("masuk cabang pengelolaan user id");
				generateReportPengelolaanUserId("cabang", branchCode, branchName, "all", getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate);
//				generatePengelolaanUserIdReportData(timeEndDate);
			}else if("Laporan Pembuatan User Id Baru Base 24".equalsIgnoreCase(getSelectedReport())){
				logger.debug(className + methodName + " laporan yang dipilih : Laporan Pembuatan User Id Baru Base 24");
				try {
					generateReportNewBase24(getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate);
				} catch (SQLException e) {
					logger.debug(className + methodName + " Laporan Pembuatan User Id Baru Base 24 :failed ");
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
			}else if("Laporan Penonaktifan User Id Base 24".equalsIgnoreCase(getSelectedReport())){
				try {
					logger.debug(className + methodName + " Laporan Penonaktifan User Id Base 24 :start ");
					generateReportDisableBase24(getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate, false);
				} catch (FileNotFoundException e) {
					logger.debug(className + methodName + " Laporan Penonaktifan User Id Base 24 :failed ");
					e.printStackTrace();
				} catch (SQLException e) {
					logger.debug(className + methodName + " Laporan Penonaktifan User Id Base 24 :failed ");
					e.printStackTrace();
				} catch (DocumentException e) {
					logger.debug(className + methodName + " Laporan Penonaktifan User Id Base 24 :failed ");
					e.printStackTrace();
				}
			}else if("Laporan Pengaktifan Kembali User Id Base 24".equalsIgnoreCase(getSelectedReport())){
				try {
					logger.debug(className + methodName + " Laporan Pengaktifan Kembali User Id Base 24:start ");
					generateReportEnableBase24(getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate);
				} catch (FileNotFoundException e) {
					logger.debug(className + methodName + " Laporan Pengaktifan Kembali User Id Base 24 :failed ");
					e.printStackTrace();
				} catch (SQLException e) {
					logger.debug(className + methodName + " Laporan Pengaktifan Kembali User Id Base 24 :failed ");
					e.printStackTrace();
				} catch (DocumentException e) {
					logger.debug(className + methodName + " Laporan Pengaktifan Kembali User Id Base 24 :failed ");
					e.printStackTrace();
				}
			}else if("Laporan Reset Password Base 24".equalsIgnoreCase(getSelectedReport())){
				try {
					logger.debug(className + methodName + " Laporan Reset Password Base 24:start ");
					generateReportResetPasswordBase24(getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate);
				} catch (FileNotFoundException e) {
					logger.debug(className + methodName + " Laporan Reset Password Base 24:failed ");
					e.printStackTrace();
				} catch (SQLException e) {
					logger.debug(className + methodName + " Laporan Reset Password Base 24:failed ");
					e.printStackTrace();
				} catch (DocumentException e) {
					logger.debug(className + methodName + " Laporan Reset Password Base 24:failed ");
					e.printStackTrace();
				}
			}else if("Laporan Penghapusan User Id Base 24".equalsIgnoreCase(getSelectedReport())){
				try {
					logger.debug(className + methodName + " Laporan Delete User Base 24:start ");
					generateReportDeleteBase24(getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate);
				} catch (FileNotFoundException e) {
					logger.debug(className + methodName + " Laporan Delete User Base 24:failed ");
					e.printStackTrace();
				} catch (SQLException e) {
					logger.debug(className + methodName + " Laporan Delete User Base 24:failed ");
					e.printStackTrace();
				} catch (DocumentException e) {
					logger.debug(className + methodName + " Laporan Delete User Base 24:failed ");
					e.printStackTrace();
				}
			}else if("Rekapitulasi Pengelolaan User Id".equalsIgnoreCase(getSelectedReport())){
				generateReportPengelolaanUserId("pusat", branchCode, branchName, "complete", getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate);
			}else if("Rekapitulasi Pengelolaan User Id - Pending dan Reject".equalsIgnoreCase(getSelectedReport())){
				generateReportPengelolaanUserId("pusat", branchCode, branchName, "pending-reject", getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate);
			}else if("Laporan User Id Base 24 Yang Harus diaktifkan Keesokan Harinya".equalsIgnoreCase(getSelectedReport())){
				try {
					logger.debug(className + methodName + " Laporan Penonaktifan User Id Base 24 :start ");
					generateReportDisableBase24(getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate, true);
				} catch (FileNotFoundException e) {
					logger.debug(className + methodName + " Laporan Penonaktifan User Id Base 24 :failed ");
					e.printStackTrace();
				} catch (SQLException e) {
					logger.debug(className + methodName + " Laporan Penonaktifan User Id Base 24 :failed ");
					e.printStackTrace();
				} catch (DocumentException e) {
					logger.debug(className + methodName + " Laporan Penonaktifan User Id Base 24 :failed ");
					e.printStackTrace();
				}
			}else if("Laporan Pengelolaan User Id Aplikasi".equalsIgnoreCase(getSelectedReport())){
				generateReportPengelolaanUserId("pusat", branchCode, branchName, "aplikasi", getTanggal() + "-" + getBulan() + "-" + getTahun(), timeBeginDate, timeEndDate);
			}
			
		} catch (GeneralException e) {
			
			e.printStackTrace();
		}
		
		logger.debug(className + methodName + " generate report end at " + new Date() + " with file " + getDownloadFile());
		
		/*try {
			downloadReport(getDownloadFile());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
	//	return "success";
 }	

	private boolean generateReportNewBase24(String date, long beginTime, long endTime) throws GeneralException, SQLException, FileNotFoundException, DocumentException{
		
		String methodName = "::generateReportNewBase24::";
		
		logger.debug(className + methodName + " started");
		
		List<BcaCommonUserReport> lstData = generateBase24ReportData(beginTime, endTime);
		
		String fileName = "PembuatanUserBaruBase24_" + date + ".pdf";
		
		String FILE = getReportPath() + fileName;
		
		setDownloadFile(FILE);
		
		logger.debug(className + "file name " + fileName);
		
		CustomReportGenerator custom = new CustomReportGenerator();
		
		Document document = new Document();
		document.setPageSize(PageSize.A4.rotate());
		
		PdfWriter.getInstance(document, 
			new FileOutputStream(FILE));
		document.open();
			
		document.add(custom.getReportHeader("Laporan Pembuatan User Id Baru Base 24", "RA. 1B/6B/2T", "IM0002", "0996 - ETS Kantor Pusat", "Harian", date));
		
		String headers[] = new String[]{"REQUESTER", "NAMA REQUESTER", "TANGGAL PERMOHONAN", "ID PERMOHONAN", "ACCOUNT ID", "NAMA ACCOUNT ID", "TANGGAL COMPLETE"};
		
		document.add(custom.getTableHeader(headers)); //set tabel header
		
		document.add(getTableContentNewBase24(lstData, headers.length));
		
		document.close();
		
		return true;
	}
	
	private boolean generateReportDisableBase24(String date, long beginTime, long endTime, boolean isReminder) throws GeneralException, SQLException, FileNotFoundException, DocumentException{
		
		String methodName = "::generateReportDisableBase24::";
		
		logger.debug(className + methodName + " started");
		
		List<BcaCommonUserReport> lstData = generateBase24DisableReportData(beginTime, endTime, isReminder);
		
		String fileName = "PenonaktifanUserBase24_" + date + ".pdf";
		
		if(isReminder){
			fileName = "PengaktifanKembaliUserBase24_" + date + ".pdf";
		}
		
		String FILE = getReportPath() + fileName;
		
		setDownloadFile(FILE);
		
		logger.debug(className + "file name " + fileName);
		
		CustomReportGenerator custom = new CustomReportGenerator();
		
		Document document = new Document();
		document.setPageSize(PageSize.A4.rotate());
		
		PdfWriter.getInstance(document, 
			new FileOutputStream(FILE));
		document.open();
		
		if(isReminder){
			document.add(custom.getReportHeader("Laporan User Id Base 24 Yang Harus Diaktifkan Keesokan Harinya", "RA. 1B/6B/2T", "IM0007", "0996 - ETS Kantor Pusat", "Harian", date));
		}else{
			document.add(custom.getReportHeader("Laporan Penonaktifan User Id Base 24", "RA. 1B/6B/2T", "IM0004", "0996 - ETS Kantor Pusat", "Harian", date));
		}
		
		
		String headers[] = new String[]{"REQUESTER", "NAMA REQUESTER", "TANGGAL PERMOHONAN", "ID PERMOHONAN", "ACCOUNT ID", "NAMA ACCOUNT ID", "TANGGAL MULAI", "TANGGAL SELESAI", "TANGGAL COMPLETE"};
		
		document.add(custom.getTableHeader(headers)); //set tabel header
		
		document.add(getTableContentDisableBase24(lstData, headers.length));
		
		document.close();
		
		return true;
	}
	
private boolean generateReportEnableBase24(String date, long beginTime, long endTime) throws GeneralException, SQLException, FileNotFoundException, DocumentException{
		
		String methodName = "::generateReportEnableBase24::";
		
		logger.debug(className + methodName + " started");
		
		List<BcaCommonUserReport> lstData = generateBase24EnableReportData(beginTime, endTime);
		
		String fileName = "PengaktifanUserBase24_" + date + ".pdf";
		
		String FILE = getReportPath() + fileName;
		
		setDownloadFile(FILE);
		
		logger.debug(className + "file name " + fileName);
		
		CustomReportGenerator custom = new CustomReportGenerator();
		
		Document document = new Document();
		document.setPageSize(PageSize.A4.rotate());
		
		PdfWriter.getInstance(document, 
			new FileOutputStream(FILE));
		document.open();
			
		document.add(custom.getReportHeader("Laporan Pengaktifan Kembali User Id Base 24", "RA. 1B/6B/2T", "IM0005", "0996 - ETS Kantor Pusat", "Harian", date));
		
		String headers[] = new String[]{"REQUESTER", "NAMA REQUESTER", "TANGGAL PERMOHONAN", "ID PERMOHONAN", "ACCOUNT ID", "NAMA ACCOUNT ID", "TANGGAL COMPLETE"};
		
		document.add(custom.getTableHeader(headers)); //set tabel header
		
		document.add(getTableContentEnableBase24(lstData, headers.length));
		
		document.close();
		
		return true;
	}

private boolean generateReportResetPasswordBase24(String date, long beginTime, long endTime) throws GeneralException, SQLException, FileNotFoundException, DocumentException{
	
	String methodName = "::generateReportResetPasswordBase24::";
	
	logger.debug(className + methodName + " started");
	
	List<BcaCommonUserReport> lstData = generateResetPasswordBase24ReportData(beginTime, endTime);
	
	String fileName = "ResetPasswordBase24_" + date + ".pdf";
	
	String FILE = getReportPath() + fileName;
	
	setDownloadFile(FILE);
	
	logger.debug(className + "file name " + fileName);
	
	CustomReportGenerator custom = new CustomReportGenerator();
	
	Document document = new Document();
	document.setPageSize(PageSize.A4.rotate());
	
	PdfWriter.getInstance(document, 
		new FileOutputStream(FILE));
	document.open();
		
	document.add(custom.getReportHeader("Laporan Reset Password Base 24", "RA. 1B/3B/-", "IM0006", "0996 - ETS Kantor Pusat", "Harian", date));
	
	String headers[] = new String[]{"REQUESTER", "NAMA REQUESTER", "TANGGAL PERMOHONAN", "ID PERMOHONAN", "ACCOUNT ID", "NAMA ACCOUNT ID", "TANGGAL COMPLETE"};
	
	document.add(custom.getTableHeader(headers)); //set tabel header
	
	document.add(getTableContentResetPasswordBase24(lstData, headers.length));
	
	document.close();
	
	return true;
}

private boolean generateReportDeleteBase24(String date, long beginTime, long endTime) throws GeneralException, SQLException, FileNotFoundException, DocumentException{
	
	String methodName = "::generateReportDeleteBase24::";
	
	logger.debug(className + methodName + " started");
	
	List<BcaCommonUserReport> lstData = generateDeleteBase24ReportData(beginTime, endTime);
	
	String fileName = "DeleteBase24_" + date + ".pdf";
	
	String FILE = getReportPath() + fileName;
	
	setDownloadFile(FILE);
	
	logger.debug(className + "file name " + fileName);
	
	CustomReportGenerator custom = new CustomReportGenerator();
	
	Document document = new Document();
	document.setPageSize(PageSize.A4.rotate());
	
	PdfWriter.getInstance(document, 
		new FileOutputStream(FILE));
	document.open();
		
	document.add(custom.getReportHeader("Laporan Penghapusan User Id Base 24", "RA. 1B/6B/2T", "IM0003", "0996 - ETS Kantor Pusat", "Harian", date));
	
	String headers[] = new String[]{"REQUESTER", "NAMA REQUESTER", "TANGGAL PERMOHONAN", "ID PERMOHONAN", "ACCOUNT ID", "NAMA ACCOUNT ID", "TANGGAL COMPLETE"};
	
	document.add(custom.getTableHeader(headers)); //set tabel header
	
	document.add(getTableContentDeleteBase24(lstData, headers.length));
	
	document.close();
	
	return true;
}
	
	private boolean generateReportPengelolaanUserId(String typeReport, String branchCode, String branchName, String status, String date, long beginTime, long endTime) throws GeneralException{
		String KCU = branchCode;
		String methodName = "::generateReportPengelolaanUserId::";
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		logger.debug(className + methodName + "ini tanggal" + date);
		
		Date tmpDate = new Date();
		try {
			tmpDate = df.parse(date);
			
		} catch (ParseException e) {
			
			e.printStackTrace();
		}

		SailPointContext context = getContext();
		
		boolean isSuccess = true;
		
		logger.debug("branch code is : " + branchCode);
		boolean isKCU = CommonUtil.isMainBranchCode(getContext(), branchCode);
		
		QueryOptions localQueryOptions = new QueryOptions();
		
		localQueryOptions.addFilter(Filter.ge("modified", new Date(beginTime)));
		
		localQueryOptions.addFilter(Filter.lt("modified", new Date(endTime)));
		
		
		localQueryOptions.getQuery();
		//localQueryOptions.addFilter(Filter.eq("type", (String)"AccessRequest"));
		
//		if(status != null && !"".equalsIgnoreCase(status.trim())){
//			if("complete".equalsIgnoreCase(status)){
//				localQueryOptions.addFilter(Filter.eq("completionStatus", (String)"Success"));
//			}else if("pending-reject".equalsIgnoreCase(status)){
//				localQueryOptions.addFilter(Filter.eq("completionStatus", (String)"Finished"));
//			}
//			
//		} *dikomen untuk membedakan generate laporan yang completed dan pending-reject
		
		logger.debug(methodName + "Preparation to search");

		Iterator<IdentityRequest> it = context.search(IdentityRequest.class, localQueryOptions);
		
		
		logger.debug(className + methodName + " end query Data" + new Date());
		
		logger.debug(className + methodName + " preparation to create file");
		
		logger.debug(className + methodName + " get data report start at " + new Date());
		
		logger.debug("it should be : " + context.search(IdentityRequest.class, localQueryOptions));
		
//		List<BcaCommonUserReport> lst = getReportData(it, typeReport, status, beginTime, endTime); *diubah karena penambahan tabel untuk laporan pengelolaan user id
		List<BcaCommonUserReport> lst = new ArrayList<BcaCommonUserReport>();
		try {
			if("all".equalsIgnoreCase(status)){
				if(isKCU){
					Map<String, String> map = getBranchKcuMap(branchCode);
					int i = 1;
					branchCode = "";
					for (Map.Entry<String, String> entry : map.entrySet()){
						logger.debug("branch #" + i + " = " + entry.getKey());
						branchCode = branchCode + entry.getKey() + ",";
					}
					branchCode = branchCode.substring(0, branchCode.length()-1);
					lst = generatePengelolaanUserIdAllByBranchReportData(branchCode, beginTime);
					logger.debug("kondisi all");
				}
			}else if("pending-reject".equalsIgnoreCase(status)){
				lst = generatePengelolaanUserIdPendingRejectReportData(beginTime);
			}else if("complete".equalsIgnoreCase(status)){
				lst = generatePengelolaanUserIdAllBranchReportData(beginTime, tmpDate.getTime());
			}else if("aplikasi".equalsIgnoreCase(status)){
				lst = generatePengelolaanUserIdAplikasi(beginTime);
				logger.debug("kondisi aplikasi");
			}
				
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		logger.debug(className + methodName + " get data report end at " + new Date());
		
		if(lst != null && lst.size() > 0){
			logger.debug(className + methodName + " list size is " + lst.size());
		}else{
			logger.debug(className + methodName + " list is empty");
		}
		
		try {
			logger.debug(className + methodName + " generate pdf start at " + new Date());
			generateReport(lst, KCU, branchName, date, typeReport, status);
			logger.debug(className + methodName + " generate pdf end at " + new Date());
		} catch (Exception e) {
			logger.debug(className + methodName + " generate report failed");
			e.printStackTrace();
			return false;
		}
		
		logger.debug(className + methodName + " generate report success");
		return isSuccess;
	}
	
	public void generateReport(List<BcaCommonUserReport> lstData, String branchCode, String branchName, String date, String typeReport, String status) throws Exception{
		
		String fileName = "PengelolaanUserId" + branchCode + "_" + date + ".pdf";
		
		if("complete".equalsIgnoreCase(status)){
			fileName = "RekapitulasiComplete" + branchCode + "_" + date + ".pdf";
		}else if("pending-reject".equalsIgnoreCase(status)){
			fileName = "RekapitulasiPendingReject" + branchCode + "_" + date + ".pdf";
		}else if("aplikasi".equalsIgnoreCase(status)){
			fileName = "Laporan Pengelolaan User Id Aplikasi_" + date + ".pdf";
		}
		
		String FILE = getReportPath() + fileName;
		setDownloadFile(FILE);
		logger.debug(className + "file name " + fileName);
		
		CustomReportGenerator custom = new CustomReportGenerator();
		
		Document document = new Document();
		document.setPageSize(PageSize.A4.rotate());
		
		PdfWriter.getInstance(document, 
			new FileOutputStream(FILE));
		document.open();
		Font boldFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
		
		HeaderFooter header = new HeaderFooter(new Phrase("Laporan Pengelolaan User Id", boldFont), false);
		
		
		logger.debug("list data for report : " + lstData);
		if("complete".equalsIgnoreCase(status)){
			document.add(custom.getReportHeader("Rekapitulasi Pengelolaan User Id", "RA. 1B/6B/2T", "IM0008", branchCode + " " + branchName, "Harian", date));
		}else if("pending-reject".equalsIgnoreCase(status)){
			document.add(custom.getReportHeader("Rekapitulasi Pengelolaan User Id (Pending - Reject)", "RA. 1B/6B/2T", "IM0009", branchCode + " " + branchName, "Harian", date));
		}else if("aplikasi".equalsIgnoreCase(status)){
			document.add(custom.getReportHeader("Laporan Pengelolaan User Id Aplikasi", "RA. 1B/6B/2T", "IM0010", branchCode + " " + branchName, "Harian", date));
		}
		else{
			document.add(custom.getReportHeader("Laporan Pengelolaan User Id", "RA. 1B/6B/2T", "IM0001", branchCode + " " + branchName, "Harian", date));
		}
		
		String headers[] = new String[]{"ID Permohonan", "Tanggal Permohonan", "Permohonan", "Requestee", "Requestor"
				, "Kode Cabang", "Approver 1", "Approver 2", "Checker 1", "Checker 2", "SA Aplikasi", "Account Id", "Status Permohonan", "Status Provisioning"};
		
		document.add(custom.getTableHeader(headers)); //set tabel header
		
		document.add(getTableContent(lstData, headers.length));
		
		
		document.close();
			
	}
	
	private boolean isRequestExists(String requestId){
		String methodName = "::isRequestExists::";
		List lstRequest = new ArrayList();

		SailPointContext ctx = getContext();
		
		logger.debug(methodName + "Preparation to search");
		
		
		return false;
	}
	
private List<BcaCommonUserReport> generatePengelolaanUserIdAllByBranchReportData(String branchCode, long time) throws GeneralException, SQLException{
		
		String methodName = "::generatePengelolaanUserIdCompletedReportData::";
		List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

		SailPointContext ctx = getContext();
		logger.debug("branch code is : " + branchCode);
		logger.debug(methodName + "Preparation to search");
		String [] arrBranchCode = branchCode.split(",");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arrBranchCode.length; i++) {
			sb.append("?,");
		}
		String sql = 
				
				"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS AS LAST_ACTION, PROVISIONING_STAT AS PROVISIONING_STATUS, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+

 "From "+

 "( "+
//"-- QUERY  PASSWORD REQUEST -- "+
"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+
"FROM "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'PASSWORD'  "+
				       ") AS PERMOHONAN,  "+
				        
				      "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				       TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "				FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE "+
				"C.APPROVAL_STATE NOT IN ('Rejected')  "+
     				"AND C.PROVISIONING_STATE  not IN ('Failed') "+
				"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
				"or TYPE IN ('PasswordRequest')  "+
				"AND C.NAME IS NULL  "+
 ") "+
  


 " where  "+
  
 "Provisioning_STAT not in ('Finished') "+
 "and Provisioning_STAT not in ('Commited') "+
 "and Request_Complete_date is null "+

 
 "UNION ALL "+
//"--SELECT ALL FINISHED REQUEST "+
"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+
"FROM "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'PASSWORD'  "+
				       ") AS PERMOHONAN,  "+
				        
				      "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				       TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "				FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE "+
				"C.APPROVAL_STATE NOT IN ('Rejected')  "+
     				"AND C.PROVISIONING_STATE  not IN ('Failed') "+
				"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
				"or TYPE IN ('PasswordRequest')  "+
				"AND C.NAME IS NULL  "+
 ") "+
  
  

 " where  "+
  
 "Provisioning_STAT  in ('Finished') "+
  

 " UNION ALL "+
// "--Select All commited whit no finished request ID "+
 "Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+
"FROM "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'PASSWORD'  "+
				       ") AS PERMOHONAN,  "+
				        
				      "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				       TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "				FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE "+
				"C.APPROVAL_STATE NOT IN ('Rejected')  "+
     				"AND C.PROVISIONING_STATE  not IN ('Failed') "+
				"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
				"or TYPE IN ('PasswordRequest')  "+
				"AND C.NAME IS NULL  "+
 ") "+
  
  

 " where  "+
  
 "Provisioning_STAT  in ('Commited') "+
 "and request_id not in  "+
 "( "+
"Select distinct request_id  "+
"FROM "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'PASSWORD'  "+
				       ") AS PERMOHONAN,  "+
				        
				      "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				       TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "				FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE "+
				"C.APPROVAL_STATE NOT IN ('Rejected')  "+
     				"AND C.PROVISIONING_STATE  not IN ('Failed') "+
				"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
				"or TYPE IN ('PasswordRequest')  "+
				"AND C.NAME IS NULL  "+
 ") "+
 "where Provisioning_STAT  in ('Finished') "+
 ") "+
  
//"-- QUERY  PASSWORD REQUEST ENDS -- "+
"UNION ALL "+

// "-- QUERY ACCESS REQUEST -- "+
"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+

 "From "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
			       "B.NAME AS REQUEST_ID,  "+
			      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
			      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
			       "A.BRANCH_CODE,  "+
			       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
			       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
			       "AND E.NAME = 'assignedRoles'  "+
			       
			       ") AS PERMOHONAN,  "+

 "				  C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+

 "				  CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
			      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
			      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
  "C.PROVISIONING_STATE AS PROVISIONING_STAT, "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=requester_display_name) as requester,  "+
			       "(select firstname||' '||lastname from spt_identity  "+
			       "where name=target_display_name) as requestee,  "+
			       "(select firstname||' '||lastname  "+
			       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "					TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "					CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
			       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
			       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
			       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
			       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				   TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "			FROM SPT_IDENTITY_REQUEST B  "+
			"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
			"ON B.ID = C.IDENTITY_REQUEST_ID  "+
			"LEFT JOIN CUS_ACTIVITY_USER A  "+
			"ON A.REQUEST_ID = B.NAME  "+

 "			WHERE   "+
			"C.APPROVAL_STATE NOT IN ('Rejected')  "+
			"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest') "+
      "and c.Native_identity is not null  "+
      "and c.Provisioning_state  in ('Finished') "+
      "and c.Provisioning_state not  in ('Commited') "+
      
       
       
      "union all  "+
       
      "SELECT DISTINCT B.MODIFIED,  "+
			       "B.NAME AS REQUEST_ID,  "+
			      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
			      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
			       "A.BRANCH_CODE,  "+
			       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
			       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
			       "AND E.NAME = 'assignedRoles'  "+
			       
			       ") AS PERMOHONAN,  "+

 "				  C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+

 "				  CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
			      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
			      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
  "C.PROVISIONING_STATE AS PROVISIONING_STAT, "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=requester_display_name) as requester,  "+
			       "(select firstname||' '||lastname from spt_identity  "+
			       "where name=target_display_name) as requestee,  "+
			       "(select firstname||' '||lastname  "+
			       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "					TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "					CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
			       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
			       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
			       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
			       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				   TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "			FROM SPT_IDENTITY_REQUEST B  "+
			"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
			"ON B.ID = C.IDENTITY_REQUEST_ID  "+
			"LEFT JOIN CUS_ACTIVITY_USER A  "+
			"ON A.REQUEST_ID = B.NAME  "+

 "			WHERE   "+
			"C.APPROVAL_STATE NOT IN ('Rejected')  "+
			"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest') "+
      "and c.Native_identity is not null  "+
        
      "and  c.Provisioning_state  in ('Commited') "+
      "and request_id not in  "+
      "(       select  distinct B.NAME AS REQUEST_ID  "+
              "from "+
              "SPT_IDENTITY_REQUEST B  "+
              "LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
              "ON B.ID = C.IDENTITY_REQUEST_ID  "+
              "LEFT JOIN CUS_ACTIVITY_USER A  "+
              "ON A.REQUEST_ID = B.NAME  "+
              "WHERE   "+
              "C.APPROVAL_STATE NOT IN ('Rejected')  "+
              "AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest') "+
              "and c.Native_identity is not null  "+
              "and c.Provisioning_state  in ('Finished') "+
      ") "+
      "OR c.Provisioning_state  in ('Failed') "+
       
       
      ") "+

// "-- QUERY ACCESS REQUEST ENDS -- "+
"UNION ALL "+

// "-- QUERY IIQ -- "+
"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+
"FROM "+
"( "+
				"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'assignedRoles'  "+
				        
				       ") AS PERMOHONAN,  "+

 "				       C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				        
				       "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+
				       "(select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+
				        
				"FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest')  "+
				"AND C.APPROVAL_STATE NOT IN ('Rejected') "+
				"AND  C.VALUE LIKE ('IdentityIQ - %')  "+
				"AND C.PROVISIONING_STATE NOT IN ('Failed')  "+
        ") "+
//"-- QUERY iiQ END-- "+
") "+
"WHERE  "+
				"TO_DATE(REQUEST_COMPLETE_DATE, 'DD/MM/RRRR') = TO_DATE(?, 'DD/MM/RRRR') "	+
				"AND branch_code in ("+ sb.deleteCharAt(sb.length()-1).toString() +") ORDER BY REQUEST_ID " 
				;



		
		logger.debug(className + methodName + " sql : " + sql);
		logger.debug(className + methodName + " time : " + time);
		logger.debug("test : " + sb.deleteCharAt(sb.length()-1).toString());
//			
		PreparedStatement ps = ctx.getJdbcConnection().prepareStatement(sql);
		ps.setDate(1, new java.sql.Date(time));
//		ps.setDate(2, new java.sql.Date(time));
//		ps.setDate(3, new java.sql.Date(time));
//		ps.setDate(4, new java.sql.Date(time));
//		ps.setDate(5, new java.sql.Date(time));
//		ps.setDate(6, new java.sql.Date(time));
//		ps.setDate(7, new java.sql.Date(time));
		
	
	
		logger.debug(className + methodName + " statement 1 update date : " + new java.sql.Date(time));
		int j = 0;
		for (int i = 0; i < arrBranchCode.length; i++) {
			ps.setString(i+2, arrBranchCode[i]);
			logger.debug(className + methodName + " statement " + i + " update date : " + arrBranchCode[i]);
		}
				
		ResultSet rs = ps.executeQuery();
		logger.debug("List size :::: " + rs.getRow());
		
		while(rs.next()){
			BcaCommonUserReport data = new BcaCommonUserReport();
			
			String requesteeId = rs.getString("requestee");
			String requesterId = rs.getString("requester");
			long modifiedDate = rs.getLong("modified");
			String requestId = String.valueOf(Long.parseLong(rs.getString("request_id")));
			String accountId = rs.getString("account_id");
			logger.debug(className+methodName+"tanggal Permohonan" + rs.getString("request_date"));
			SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
			data.setTanggalPermohonan(df.format(new Date(rs.getTimestamp("request_date").getTime())));
		
			data.setIdPermohonan(requestId);
			data.setAccountId(accountId);
			
			//ditambahin untuk Laporan Pengelolaan User ID
			data.setPermohonan(rs.getString("permohonan"));
			data.setUserId(rs.getString("requestee"));
			data.setRequestor(rs.getString("requester"));
			
//			data.setNipRequestee(requesteeId);
//			data.setNipRequestor(requesterId);
			data.setKodeCabang(rs.getString("branch_code"));
			logger.debug(className + " approver1_name " + rs.getString("approver1_name"));
			data.setApprover1(rs.getString("approver1_name"));
			logger.debug(className + " approver2_name " + rs.getString("approver2_name"));
			data.setApprover2(rs.getString("approver2_name"));
			logger.debug(className + " checker1_name " + rs.getString("checker1_name"));
			data.setChecker1(rs.getString("checker1_name"));
			logger.debug(className + " checker2_name " + rs.getString("checker2_name"));
			data.setChecker2(rs.getString("checker2_name"));
			logger.debug(className + " sa_aplikasi_name " + rs.getString("sa_aplikasi_name"));
			data.setSaAplikasi(rs.getString("sa_aplikasi_name"));
			logger.debug(className + " sa_aplikasi_name " + rs.getString("sa_aplikasi_name"));
			data.setStatusPermohonan(rs.getString("last_action"));
			logger.debug(className + " last_action " + rs.getString("last_action"));
			data.setProvisioningStatus(rs.getString("provisioning_status"));
			logger.debug(className + " provisioning_status " + rs.getString("provisioning_status"));
			lstReport.add(data);
		}
		rs.close();
		ps.close();
		
		logger.debug(className + methodName + " list size " + lstReport.size());
		return lstReport;
	}
	
private List<BcaCommonUserReport> generatePengelolaanUserIdAllBranchReportData(long beginTime, long endTime) throws GeneralException, SQLException{
	
	String methodName = "::generatePengelolaanUserIdCompletedReportData::";
	List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

	SailPointContext ctx = getContext();
	
	logger.debug(methodName + "Preparation to search");
	
	String sql = 
			
			"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS AS LAST_ACTION, PROVISIONING_STAT AS PROVISIONING_STATUS, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+

 "From "+

 "( "+
//"-- QUERY  PASSWORD REQUEST -- "+
"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+
"FROM "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'PASSWORD'  "+
				       ") AS PERMOHONAN,  "+
				        
				      "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				       TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "				FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE "+
				"C.APPROVAL_STATE NOT IN ('Rejected')  "+
     				"AND C.PROVISIONING_STATE  not IN ('Failed') "+
				"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
				"or TYPE IN ('PasswordRequest')  "+
				"AND C.NAME IS NULL  "+
 ") "+
  


 " where  "+
  
 "Provisioning_STAT not in ('Finished') "+
 "and Provisioning_STAT not in ('Commited') "+
 "and Request_Complete_date is null "+

 
 "UNION ALL "+
//"--SELECT ALL FINISHED REQUEST "+
"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+
"FROM "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'PASSWORD'  "+
				       ") AS PERMOHONAN,  "+
				        
				      "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				       TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "				FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE "+
				"C.APPROVAL_STATE NOT IN ('Rejected')  "+
     				"AND C.PROVISIONING_STATE  not IN ('Failed') "+
				"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
				"or TYPE IN ('PasswordRequest')  "+
				"AND C.NAME IS NULL  "+
 ") "+
  
  

 " where  "+
  
 "Provisioning_STAT  in ('Finished') "+
  

 " UNION ALL "+
// "--Select All commited whit no finished request ID "+
 "Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+
"FROM "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'PASSWORD'  "+
				       ") AS PERMOHONAN,  "+
				        
				      "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				       TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "				FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE "+
				"C.APPROVAL_STATE NOT IN ('Rejected')  "+
     				"AND C.PROVISIONING_STATE  not IN ('Failed') "+
				"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
				"or TYPE IN ('PasswordRequest')  "+
				"AND C.NAME IS NULL  "+
 ") "+
  
  

 " where  "+
  
 "Provisioning_STAT  in ('Commited') "+
 "and request_id not in  "+
 "( "+
"Select distinct request_id  "+
"FROM "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'PASSWORD'  "+
				       ") AS PERMOHONAN,  "+
				        
				      "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				       TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "				FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE "+
				"C.APPROVAL_STATE NOT IN ('Rejected')  "+
     				"AND C.PROVISIONING_STATE  not IN ('Failed') "+
				"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
				"or TYPE IN ('PasswordRequest')  "+
				"AND C.NAME IS NULL  "+
 ") "+
 "where Provisioning_STAT  in ('Finished') "+
 ") "+
  
//"-- QUERY  PASSWORD REQUEST ENDS -- "+
"UNION ALL "+

// "-- QUERY ACCESS REQUEST -- "+
"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+

 "From "+
"( "+
"SELECT DISTINCT B.MODIFIED,  "+
			       "B.NAME AS REQUEST_ID,  "+
			      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
			      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
			       "A.BRANCH_CODE,  "+
			       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
			       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
			       "AND E.NAME = 'assignedRoles'  "+
			       
			       ") AS PERMOHONAN,  "+

 "				  C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+

 "				  CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
			      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
			      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
  "C.PROVISIONING_STATE AS PROVISIONING_STAT, "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=requester_display_name) as requester,  "+
			       "(select firstname||' '||lastname from spt_identity  "+
			       "where name=target_display_name) as requestee,  "+
			       "(select firstname||' '||lastname  "+
			       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "					TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "					CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
			       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
			       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
			       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
			       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				   TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "			FROM SPT_IDENTITY_REQUEST B  "+
			"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
			"ON B.ID = C.IDENTITY_REQUEST_ID  "+
			"LEFT JOIN CUS_ACTIVITY_USER A  "+
			"ON A.REQUEST_ID = B.NAME  "+

 "			WHERE   "+
			"C.APPROVAL_STATE NOT IN ('Rejected')  "+
			"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest') "+
      "and c.Native_identity is not null  "+
      "and c.Provisioning_state  in ('Finished') "+
      "and c.Provisioning_state not  in ('Commited') "+
      
       
       
      "union all  "+
       
      "SELECT DISTINCT B.MODIFIED,  "+
			       "B.NAME AS REQUEST_ID,  "+
			      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
			      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
			       "A.BRANCH_CODE,  "+
			       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
			       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
			       "AND E.NAME = 'assignedRoles'  "+
			       
			       ") AS PERMOHONAN,  "+

 "				  C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+

 "				  CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
			      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
			      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
  "C.PROVISIONING_STATE AS PROVISIONING_STAT, "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=requester_display_name) as requester,  "+
			       "(select firstname||' '||lastname from spt_identity  "+
			       "where name=target_display_name) as requestee,  "+
			       "(select firstname||' '||lastname  "+
			       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "					TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "					CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
			       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
			       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
			       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "					(select firstname||' '||lastname  "+
			       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
			       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				   TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
			      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

 "			FROM SPT_IDENTITY_REQUEST B  "+
			"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
			"ON B.ID = C.IDENTITY_REQUEST_ID  "+
			"LEFT JOIN CUS_ACTIVITY_USER A  "+
			"ON A.REQUEST_ID = B.NAME  "+

 "			WHERE   "+
			"C.APPROVAL_STATE NOT IN ('Rejected')  "+
			"AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest') "+
      "and c.Native_identity is not null  "+
        
      "and  c.Provisioning_state  in ('Commited') "+
      "and request_id not in  "+
      "(       select  distinct B.NAME AS REQUEST_ID  "+
              "from "+
              "SPT_IDENTITY_REQUEST B  "+
              "LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
              "ON B.ID = C.IDENTITY_REQUEST_ID  "+
              "LEFT JOIN CUS_ACTIVITY_USER A  "+
              "ON A.REQUEST_ID = B.NAME  "+
              "WHERE   "+
              "C.APPROVAL_STATE NOT IN ('Rejected')  "+
              "AND B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest') "+
              "and c.Native_identity is not null  "+
              "and c.Provisioning_state  in ('Finished') "+
      ") "+
      "OR c.Provisioning_state  in ('Failed') "+
       
       
      ") "+

// "-- QUERY ACCESS REQUEST ENDS -- "+
"UNION ALL "+

// "-- QUERY IIQ -- "+
"Select  "+

 "MODIFIED, REQUEST_ID, REQUEST_DATE, REQUEST_COMPLETE_DATE, BRANCH_CODE, PERMOHONAN, "+
"ACCOUNT_ID, STATUS, PROVISIONING_STAT, REQUESTER, REQUESTEE, APPROVER1_NAME, APPROVER1_DATE, "+
"APPROVER2_NAME, APPROVER2_DATE, CHECKER1_NAME, CHECKER1_DATE, CHECKER2_NAME, CHECKER2_DATE, "+
"SA_APLIKASI_NAME, SA_APLIKASI_DATE, TGL_AKTIVITAS1 "+
"FROM "+
"( "+
				"SELECT DISTINCT B.MODIFIED,  "+
				       "B.NAME AS REQUEST_ID,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
				      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
				       "A.BRANCH_CODE,  "+
				       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
				       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
				       "AND E.NAME = 'assignedRoles'  "+
				        
				       ") AS PERMOHONAN,  "+

 "				       C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
				        
				       "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
				      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
				      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
				      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=requester_display_name) as requester,  "+
				       "(select firstname||' '||lastname from spt_identity  "+
				       "where name=target_display_name) as requestee,  "+
				       "(select firstname||' '||lastname  "+
				       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

 "				       TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

 "				       CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
  "ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
				       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
				       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
				       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

 "				       (select firstname||' '||lastname  "+
				       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
				       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

 "				TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
				      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+
				        
				"FROM SPT_IDENTITY_REQUEST B  "+
				"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
				"ON B.ID = C.IDENTITY_REQUEST_ID  "+
				"LEFT JOIN CUS_ACTIVITY_USER A  "+
				"ON A.REQUEST_ID = B.NAME  "+

 "				WHERE B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest')  "+
				"AND C.APPROVAL_STATE NOT IN ('Rejected') "+
				"AND  C.VALUE LIKE ('IdentityIQ - %')  "+
				"AND C.PROVISIONING_STATE NOT IN ('Failed')  "+
        ") "+
//"-- QUERY iiQ END-- "+
") "+
"WHERE  PROVISIONING_STAT NOT IN ('Pending') AND "+
				"TO_DATE(REQUEST_COMPLETE_DATE, 'DD/MM/RRRR') = TO_DATE(?, 'DD/MM/RRRR') order by Request_id"	;
	
	logger.debug(className + methodName + " sql : " + sql);
	logger.debug(className + methodName + " date in long : " + beginTime);
	PreparedStatement ps = ctx.getJdbcConnection().prepareStatement(sql);
	ps.setDate(1, new java.sql.Date(beginTime));
	
	
	logger.debug(className + methodName + " statement 1 update begin date : " + new java.sql.Date(beginTime) + "end :" + new java.sql.Date(endTime));
		
	ResultSet rs = ps.executeQuery();
	logger.debug("List size :::: " + rs.getRow());
	
	while(rs.next()){
		BcaCommonUserReport data = new BcaCommonUserReport();
		
		String requesteeId = rs.getString("requestee");
		String requesterId = rs.getString("requester");
		long modifiedDate = rs.getLong("modified");
		String requestId = String.valueOf(Long.parseLong(rs.getString("request_id")));
		String accountId = rs.getString("account_id");
		logger.debug(className+methodName+"tanggal Permohonan" + rs.getString("request_date"));
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		data.setTanggalPermohonan(df.format(new Date(rs.getTimestamp("request_date").getTime())));
	
	
		data.setIdPermohonan(requestId);
		data.setAccountId(accountId);
		
		//ditambahin untuk Laporan Pengelolaan User ID
		data.setPermohonan(rs.getString("permohonan"));
		data.setUserId(rs.getString("requestee"));
		data.setRequestor(rs.getString("requester"));
//		data.setNipRequestee(requesteeId);
//		data.setNipRequestor(requesterId);
		data.setKodeCabang(rs.getString("branch_code"));
		logger.debug(className + " approver1_name " + rs.getString("approver1_name"));
		data.setApprover1(rs.getString("approver1_name"));
		logger.debug(className + " approver2_name " + rs.getString("approver2_name"));
		data.setApprover2(rs.getString("approver2_name"));
		logger.debug(className + " checker1_name " + rs.getString("checker1_name"));
		data.setChecker1(rs.getString("checker1_name"));
		logger.debug(className + " checker2_name " + rs.getString("checker2_name"));
		data.setChecker2(rs.getString("checker2_name"));
		logger.debug(className + " sa_aplikasi_name " + rs.getString("sa_aplikasi_name"));
		data.setSaAplikasi(rs.getString("sa_aplikasi_name"));
		logger.debug(className + " sa_aplikasi_name " + rs.getString("sa_aplikasi_name"));
		data.setStatusPermohonan(rs.getString("last_action"));
		logger.debug(className + " last_action " + rs.getString("last_action"));
		data.setProvisioningStatus(rs.getString("provisioning_status"));
		logger.debug(className + " status provisioning " + rs.getString("provisioning_status"));
		lstReport.add(data);
	}
	rs.close();
	ps.close();
	
	logger.debug(className + methodName + " list size " + lstReport.size());
	return lstReport;
}
	
private List<BcaCommonUserReport> generatePengelolaanUserIdAplikasi(long time) throws GeneralException, SQLException{
	
	String methodName = "::generatePengelolaanUserForSAAplikasiReportData::";
	List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

	SailPointContext ctx = getContext();
	
	//for spadmin
	if ("spadmin".equalsIgnoreCase(getLoggedInUserName())){
		whereapp = "TABLE_1.PERMOHONAN LIKE '%BNO%' OR TABLE_1.PERMOHONAN LIKE '%PDNUSER%' OR TABLE_1.PERMOHONAN LIKE '%PDNUSER1%' OR TABLE_1.PERMOHONAN LIKE '%BNUSER%' " +
				"OR TABLE_1.PERMOHONAN LIKE '%BTR%' OR TABLE_1.PERMOHONAN LIKE '%INTLUSR%' OR TABLE_1.PERMOHONAN LIKE '%INTLCO%' OR TABLE_1.PERMOHONAN LIKE '%INTLCO1%' OR TABLE_1.PERMOHONAN LIKE '%INTLSADM%' OR TABLE_1.PERMOHONAN LIKE '%INTLTABL%' " +
				"OR TABLE_1.PERMOHONAN LIKE '%CCR%' OR TABLE_1.PERMOHONAN LIKE '%CARDINQ%' OR TABLE_1.PERMOHONAN LIKE '%CCUSERA%' OR TABLE_1.PERMOHONAN LIKE '%CCUSERB%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC4%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC5%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC6%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%GLM%' OR TABLE_1.PERMOHONAN LIKE '%FISUSR%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%IRT%' OR TABLE_1.PERMOHONAN LIKE '%IRPUSAT%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER1%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%ITS%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER1%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%LBU%' OR TABLE_1.PERMOHONAN LIKE '%LBUSER%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%ORT%' OR TABLE_1.PERMOHONAN LIKE '%ORUSER%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%RIM%' OR TABLE_1.PERMOHONAN LIKE '%RIMOUSER%' OR TABLE_1.PERMOHONAN LIKE '%RIMOKP%' ";
	}
		
	logger.debug(methodName + "Preparation to search");
	String sql = 
			
			"SELECT * FROM " +
			"( " +
			"SELECT " +
			"B.MODIFIED  AS MODIFIED, B.NAME AS REQUEST_ID, " +
			"TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') + (B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE, " +
			"NVL(A.BRANCH_CODE, (SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E WHERE E.IDENTITY_REQUEST_ID = B.ID AND E.NAME = 'branchCode')) BRANCH_CODE, " +
        	"case when c.operation = 'Set' then 'Reset Password' " +
        	"when c.operation = 'Enable' then 'Pengaktifan' " +
        	"when c.operation = 'Disable' then 'Penonaktifan' " +
        	"when c.operation = 'Delete' then C.OPERATION || ' ' || A.OWN_DESCRIPTION " +
        	"when c.operation = 'Create' then C.OPERATION || ' ' || NVL((SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E WHERE E.IDENTITY_REQUEST_ID = B.ID AND E.NAME = 'assignedRoles'),'Non Karyawan') " +
        	"when c.operation = 'Add' and " +
        		"(select count(*) from spt_identity_request_item e where e.identity_request_id = b.id and e.operation = 'Create')=0  "+
        		"then 'Penambahan ' || NVL((SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E WHERE E.IDENTITY_REQUEST_ID = B.ID AND E.NAME = 'assignedRoles'), " +
        		"(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E WHERE E.IDENTITY_REQUEST_ID = B.ID AND E.NAME = 'displayName')) " +
        		"when c.operation = 'Remove' then 'Pengurangan ' || (SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E WHERE E.IDENTITY_REQUEST_ID = B.ID AND E.NAME = 'groups') " + 
        		"else '' end AS PERMOHONAN, "+
        	"C.NATIVE_IDENTITY AS ACCOUNT_ID, " +
        	"(select firstname||' '||lastname from spt_identity where name=requester_display_name) as requester, "+
        	"(select firstname||' '||lastname from spt_identity where name=target_display_name) as requestee, "+
        	"(select firstname||' '||lastname from spt_identity where name=approver1_user) as APPROVER1_NAME, " +
        	"CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)' ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME, " +
        	"(select firstname||' '||lastname from spt_identity where name=checker1_user) as CHECKER1_NAME, " +
        	"(select firstname||' '||lastname from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+ 
        	"(select firstname||' '||lastname from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME, " +
        	"CASE WHEN B.MODIFIED IS NULL THEN 'Pending' WHEN B.MODIFIED IS NOT NULL THEN 'Completed' ELSE TO_CHAR(B.MODIFIED) END AS LAST_ACTION, " +
        	"C.PROVISIONING_STATE AS PROVISIONING_STATUS " +
        	"FROM " +
			"SPT_IDENTITY_REQUEST B LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C ON B.ID = C.IDENTITY_REQUEST_ID " +
			"LEFT JOIN CUS_ACTIVITY_USER A ON A.REQUEST_ID = B.NAME  " +
            "WHERE C.APPROVAL_STATE NOT IN ('Rejected') AND C.PROVISIONING_STATE NOT IN ('Failed') " +
			"AND B.EXECUTION_STATUS NOT IN ('Terminated') " +
            "AND (C.NAME IS NULL OR C.NAME IN ('PASSWORD','groups')) " +
            ")table_1 " +
				"WHERE "+
				"TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') + (MODIFIED/1000/60/60/24),'DD/MM/RRRR') = TO_DATE(?, 'DD/MM/RRRR') " +
			"AND  (" + this.whereapp + ") ORDER BY REQUEST_ID DESC "
	;
	logger.debug(className + methodName + " sql : " + sql);
//	

	PreparedStatement ps = ctx.getJdbcConnection().prepareStatement(sql);
	ps.setDate(1, new java.sql.Date(time));
	
	logger.debug(className + methodName + " statement 1 update date : " + new java.sql.Date(time));
		
	ResultSet rs = ps.executeQuery();
	logger.debug("List size :::: " + rs.getRow());
	
	while(rs.next()){
		BcaCommonUserReport data = new BcaCommonUserReport();
		
		String requesteeId = rs.getString("requestee");
		String requesterId = rs.getString("requester");
		long modifiedDate = rs.getLong("modified");
		String requestId = String.valueOf(Long.parseLong(rs.getString("request_id")));
		String accountId = rs.getString("account_id");
		logger.debug(className+methodName+"tanggal Permohonan" + rs.getString("request_date"));
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		data.setTanggalPermohonan(df.format(new Date(rs.getTimestamp("request_date").getTime())));
	
	
		data.setIdPermohonan(requestId);
		data.setAccountId(accountId);
		
		//ditambahin untuk Laporan Pengelolaan User ID
		data.setPermohonan(rs.getString("permohonan"));
		data.setUserId(rs.getString("requestee"));
		data.setRequestor(rs.getString("requester"));
//		data.setNipRequestee(requesteeId);
//		data.setNipRequestor(requesterId);
		data.setKodeCabang(rs.getString("branch_code"));
		logger.debug(className + " approver1_name " + rs.getString("approver1_name"));
		data.setApprover1(rs.getString("approver1_name"));
		logger.debug(className + " approver2_name " + rs.getString("approver2_name"));
		data.setApprover2(rs.getString("approver2_name"));
		logger.debug(className + " checker1_name " + rs.getString("checker1_name"));
		data.setChecker1(rs.getString("checker1_name"));
		logger.debug(className + " checker2_name " + rs.getString("checker2_name"));
		data.setChecker2(rs.getString("checker2_name"));
		logger.debug(className + " sa_aplikasi_name " + rs.getString("sa_aplikasi_name"));
		data.setSaAplikasi(rs.getString("sa_aplikasi_name"));
		logger.debug(className + " sa_aplikasi_name " + rs.getString("sa_aplikasi_name"));
		data.setStatusPermohonan(rs.getString("last_action"));
		logger.debug(className + " last_action " + rs.getString("last_action"));
		data.setProvisioningStatus(rs.getString("provisioning_status"));
		logger.debug(className + " last_action " + rs.getString("provisioning_status"));
		lstReport.add(data);
	}
	rs.close();
	ps.close();
	
	logger.debug(className + methodName + " list size " + lstReport.size());
	return lstReport;
}


	private List<BcaCommonUserReport> generatePengelolaanUserIdPendingRejectReportData(long time) throws GeneralException, SQLException{
		
		String methodName = "::generatePengelolaanUserIdPendingRejectReportData::";
		List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

		SailPointContext ctx = getContext();
		
		logger.debug(methodName + "Preparation to search");
		
		String sql = 
				
				"SELECT TABLE_2.MODIFIED,  "+
					       "TABLE_2.REQUEST_ID,  "+
					       "TO_DATE (REQ_DATE, 'DD/MM/RRRR') AS REQUEST_DATE,  "+
					       "TABLE_2.BRANCH_CODE,  "+
					       "TABLE_2.PERMOHONAN,  "+
					       "TABLE_2.ACCOUNT_ID,  "+
					       "TABLE_2.REQUESTER,  "+
					       "TABLE_2.REQUESTEE,  "+
					       "TABLE_2.APPROVER1_NAME,  "+
					       "TABLE_2.APPROVER2_NAME,  "+
					       "TABLE_2.CHECKER1_NAME,  "+
					       "TABLE_2.CHECKER2_NAME,  "+
					       "TABLE_2.SA_APLIKASI_NAME,  "+
					       "TABLE_2.LAST_ACTION,  "+
					       "TABLE_2.PROVISIONING_STATUS  "+

						"FROM  "+

						"(SELECT TABLE_1.MODIFIED,  "+
							       "TABLE_1.REQUEST_ID,  "+
							       "TO_CHAR(TABLE_1.REQUEST_DATE, 'DD/MM/RRRR') AS REQ_DATE,  "+
							       "TABLE_1.BRANCH_CODE,  "+
							       "TABLE_1.PERMOHONAN,  "+
							       "TABLE_1.ACCOUNT_ID,  "+
							       "TABLE_1.REQUESTER,  "+
							       "TABLE_1.REQUESTEE,  "+
							       "TABLE_1.APPROVER1_NAME,  "+
							       "TABLE_1.APPROVER2_NAME,  "+
							       "TABLE_1.CHECKER1_NAME,  "+
							       "TABLE_1.CHECKER2_NAME,  "+
							       "TABLE_1.SA_APLIKASI_NAME,  "+
							       "TABLE_1.STATUS AS LAST_ACTION,  "+
							        "TABLE_1.PROVISIONING_STAT AS PROVISIONING_STATUS,  "+
							       "TO_DATE(TABLE_1.REQUEST_DATE, 'DD/MM/RRRR') AS REQUEST_DATE,  "+
		                 "TO_DATE(TABLE_1.REQUEST_COMPLETE_DATE, 'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE  "+
							 "FROM  "+
							"(  "+
							"SELECT DISTINCT B.MODIFIED,  "+
							       "B.NAME AS REQUEST_ID,  "+
							       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
							      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
							       "A.BRANCH_CODE,  "+
							       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
							       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
							       "AND E.NAME = 'PASSWORD'  "+
							       ") AS PERMOHONAN,  "+

								"C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
							      "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
							      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
							      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
							      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

								"(select firstname||' '||lastname  "+
							       "from spt_identity where name=requester_display_name) as requester,  "+
							       "(select firstname||' '||lastname from spt_identity  "+
							       "where name=target_display_name) as requestee,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

							"TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

							"CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
							"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
							       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

								"(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
							       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

								  "(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
							       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

								"(select firstname||' '||lastname  "+
							       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
							       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

								 "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

							"FROM SPT_IDENTITY_REQUEST B  "+
							"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
							"ON B.ID = C.IDENTITY_REQUEST_ID  "+
							"LEFT JOIN CUS_ACTIVITY_USER A  "+
							"ON A.REQUEST_ID = B.NAME  "+

							"WHERE  "+

							"B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE NOT IN ('AccessRequest')  "+
							"OR TYPE IN ('PasswordRequest')  "+
							"AND C.NAME IS NULL  "+

							"UNION ALL  "+

								"SELECT DISTINCT B.MODIFIED,  "+
							       "B.NAME AS REQUEST_ID,  "+
							      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
							      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
							       "A.BRANCH_CODE,  "+
							       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
							       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
							       "AND E.NAME = 'assignedRoles'  "+

									") AS PERMOHONAN,  "+

								  "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+

								 "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
							      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
							      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
							      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=requester_display_name) as requester,  "+
							       "(select firstname||' '||lastname from spt_identity  "+
							       "where name=target_display_name) as requestee,  "+
							       "(select firstname||' '||lastname  "+
							       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

							"TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

							"CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
							"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
							       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

							"(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
							       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

							"(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
							       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

							"(select firstname||' '||lastname  "+
							       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
							       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

							"TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

							"FROM SPT_IDENTITY_REQUEST B  "+
							"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
							"ON B.ID = C.IDENTITY_REQUEST_ID  "+
							"LEFT JOIN CUS_ACTIVITY_USER A  "+
							"ON A.REQUEST_ID = B.NAME  "+

							"WHERE B.EXECUTION_STATUS NOT IN ('Terminated')  AND B.TYPE IN ('AccessRequest')  "+
							"AND C.APPROVAL_STATE = 'Pending'  "+

							"UNION ALL  "+

							"SELECT DISTINCT B.MODIFIED,  "+
							       "B.NAME AS REQUEST_ID,  "+
							       "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
							      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
							       "A.BRANCH_CODE,  "+

									"B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
							       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
							       "AND E.NAME = 'assignedRoles'  "+

									") AS PERMOHONAN,  "+

								  "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+

								  "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
							      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
							      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
							      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=requester_display_name) as requester,  "+
							       "(select firstname||' '||lastname from spt_identity  "+
							       "where name=target_display_name) as requestee,  "+
							       "(select firstname||' '||lastname  "+
							       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

							"TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

							"CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
							"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
							       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

							"(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
							       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

							"(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
							       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

							"(select firstname||' '||lastname  "+
							       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
							       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

							"TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

							"FROM SPT_IDENTITY_REQUEST  B  "+
							"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
							"ON B.ID = C.IDENTITY_REQUEST_ID  "+
							"LEFT JOIN CUS_ACTIVITY_USER  A  "+
							"ON A.REQUEST_ID = B.NAME  "+

							"WHERE B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest')  "+
							"AND C.APPROVAL_STATE IN ('Finished','Rejected')  "+
							"AND C.NATIVE_IDENTITY IS NOT NULL  "+

							"UNION ALL  "+

							"SELECT DISTINCT B.MODIFIED,  "+
							       "B.NAME AS REQUEST_ID,  "+
							      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
							      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
							      "A.BRANCH_CODE,  "+
							       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
							       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
							       "AND E.NAME = 'assignedRoles'  "+

								   ") AS PERMOHONAN,  "+

								  "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+

								  "CASE WHEN B.MODIFIED IS NULL THEN 'Pending'  "+
							      "WHEN B.MODIFIED IS NOT NULL THEN 'Completed'  "+
							      "ELSE TO_CHAR(B.MODIFIED) END AS STATUS,  "+
							      "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=requester_display_name) as requester,  "+
							       "(select firstname||' '||lastname from spt_identity  "+
							       "where name=target_display_name) as requestee,  "+
							       "(select firstname||' '||lastname  "+
							       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

							"TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

							"CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
							"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
							       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
							       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
							       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
							       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

							"TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+

							"FROM SPT_IDENTITY_REQUEST B  "+
							"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
							"ON B.ID = C.IDENTITY_REQUEST_ID  "+
							"LEFT JOIN CUS_ACTIVITY_USER A  "+
							"ON A.REQUEST_ID = B.NAME  "+

							"WHERE B.EXECUTION_STATUS NOT IN ('Terminated') AND B.TYPE IN ('AccessRequest')  "+
							"AND C.APPROVAL_STATE IN ('Finished','Rejected')  "+
							"AND  C.VALUE LIKE ('IdentityIQ - %')  "+

							"UNION ALL  "+

							"SELECT DISTINCT B.MODIFIED,  "+
							       "B.NAME AS REQUEST_ID,  "+
							      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.CREATED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_DATE,  "+
							      "TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS REQUEST_COMPLETE_DATE,  "+
							       "A.BRANCH_CODE,  "+
							       "B.TYPE ||' '||(SELECT E.VALUE FROM SPT_IDENTITY_REQUEST_ITEM E  "+
							       "WHERE E.IDENTITY_REQUEST_ID = B.ID  "+
							       "AND E.NAME = 'assignedRoles'  "+
							       ") AS PERMOHONAN,  "+
							       "C.NATIVE_IDENTITY AS ACCOUNT_ID,  "+
							       "C.APPROVAL_STATE AS STATUS,  "+
							       "C.PROVISIONING_STATE AS PROVISIONING_STAT,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=requester_display_name) as requester,  "+
							       "(select firstname||' '||lastname from spt_identity  "+
							       "where name=target_display_name) as requestee,  "+
							       "(select firstname||' '||lastname  "+
							       "from spt_identity where name=approver1_user) as APPROVER1_NAME,  "+

									"TO_DATE(A.APPROVER1_DATE, 'DD/MM/RRRR') as APPROVER1_DATE,  "+

							"CASE WHEN APPROVER2_USER ='Scheduler' THEN '(ESKALASI)'   "+
							"ELSE (SELECT firstname||' '||lastname from spt_identity where name=approver2_user) END AS APPROVER2_NAME,  "+
							       "TO_DATE(A.APPROVER2_DATE,'DD/MM/RRRR') as APPROVER2_DATE,  "+

								   "(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker1_user) as CHECKER1_NAME,  "+
							       "TO_DATE(A.CHECKER1_DATE,'DD/MM/RRRR') as CHECKER1_DATE,  "+

								   "(select firstname||' '||lastname  "+
							       "from spt_identity where name=checker2_user) as CHECKER2_NAME,  "+
							       "TO_DATE(A.CHECKER2_DATE,'DD/MM/RRRR') as CHECKER2_DATE,  "+

									"(select firstname||' '||lastname  "+
							       "from spt_identity where name=sa_aplikasi_user) as SA_APLIKASI_NAME,  "+
							       "TO_DATE(A.SA_APLIKASI_DATE,'DD/MM/RRRR') as SA_APLIKASI_DATE,  "+

								"TO_DATE(TO_DATE('01/01/1970 00:00:00', 'DD/MM/RRRR HH24:MI:SS') +  "+
							      "(B.MODIFIED/1000/60/60/24),'DD/MM/RRRR') AS TGL_AKTIVITAS1  "+


							"FROM SPT_IDENTITY_REQUEST B  "+
							"LEFT JOIN SPT_IDENTITY_REQUEST_ITEM C  "+
							"ON B.ID = C.IDENTITY_REQUEST_ID  "+
							"LEFT JOIN CUS_ACTIVITY_USER A  "+
							"ON A.REQUEST_ID = B.NAME  "+

							"WHERE B.EXECUTION_STATUS NOT IN ('Terminated')"+
							"AND C.APPROVAL_STATE IN ('Rejected')  "+
							")TABLE_1  "+

							"WHERE  "+
							"TABLE_1.STATUS IN ('Pending','Rejected'))TABLE_2  "+
		          			"WHERE TO_DATE (REQUEST_COMPLETE_DATE, 'DD/MM/RRRR')= TO_DATE(?, 'DD/MM/RRRR')  "+
							"OR TO_DATE (TABLE_2.REQUEST_DATE, 'DD/MM/RRRR') = TO_DATE(?, 'DD/MM/RRRR')  "+
							"ORDER BY REQUEST_ID  " 
		;
		logger.debug(className + methodName + " sql : " + sql);
//		
		PreparedStatement ps = ctx.getJdbcConnection().prepareStatement(sql);
		ps.setDate(1, new java.sql.Date(time));
		ps.setDate(2, new java.sql.Date(time));
		/*ps.setDate(3, new java.sql.Date(time));
		ps.setDate(4, new java.sql.Date(time));
		ps.setDate(5, new java.sql.Date(time));
		ps.setDate(6, new java.sql.Date(time));*/
		
		
		logger.debug(className + methodName + " statement 1 update date : " + new java.sql.Date(time));

				
		ResultSet rs = ps.executeQuery();
		logger.debug("List size :::: " + rs.getRow());
		
		while(rs.next()){
			BcaCommonUserReport data = new BcaCommonUserReport();
			
			String requesteeId = rs.getString("requestee");
			String requesterId = rs.getString("requester");
			long modifiedDate = rs.getLong("modified");
			String requestId = String.valueOf(Long.parseLong(rs.getString("request_id")));
			String accountId = rs.getString("account_id");
			logger.debug(className+methodName+"tanggal Permohonan" + rs.getString("request_date"));
			SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
			data.setTanggalPermohonan(df.format(new Date(rs.getTimestamp("request_date").getTime())));
		
			data.setIdPermohonan(requestId);
			data.setAccountId(accountId);
			
			//ditambahin untuk Laporan Pengelolaan User ID
			data.setPermohonan(rs.getString("permohonan"));
			data.setUserId(rs.getString("requestee"));
			data.setRequestor(rs.getString("requester"));
//			data.setNipRequestee(requesteeId);
//			data.setNipRequestor(requesterId);
			data.setKodeCabang(rs.getString("branch_code"));
			logger.debug(className + " approver1_name " + rs.getString("approver1_name"));
			data.setApprover1(rs.getString("approver1_name"));
			logger.debug(className + " approver2_name " + rs.getString("approver2_name"));
			data.setApprover2(rs.getString("approver2_name"));
			logger.debug(className + " checker1_name " + rs.getString("checker1_name"));
			data.setChecker1(rs.getString("checker1_name"));
			logger.debug(className + " checker2_name " + rs.getString("checker2_name"));
			data.setChecker2(rs.getString("checker2_name"));
			logger.debug(className + " sa_aplikasi_name " + rs.getString("sa_aplikasi_name"));
			data.setSaAplikasi(rs.getString("sa_aplikasi_name"));
			logger.debug(className + " sa_aplikasi_name " + rs.getString("sa_aplikasi_name"));
			data.setStatusPermohonan(rs.getString("last_action"));
			logger.debug(className + " last_action " + rs.getString("last_action"));
			data.setProvisioningStatus(rs.getString("provisioning_status"));
			logger.debug(className + " provisioning status " + rs.getString("provisioning_status"));
			lstReport.add(data);
		}
		rs.close();
		ps.close();
		
		logger.debug(className + methodName + " list size " + lstReport.size());
		return lstReport;
	}
	

private List<BcaCommonUserReport> generateBase24ReportData(long beginTime, long endTime) throws GeneralException, SQLException{
		
		String methodName = "::generateBase24ReportData::";
		List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

		SailPointContext ctx = getContext();
		
		logger.debug(methodName + "Preparation to search");
		
		String sql = "select r.target_display_name, r.requester_display_name, "+
			       "r.modified, r.created, r.name, i.native_identity, r.completion_status, "+
			       "i.approval_state, "+
			       "c.update_date "+
			"from spt_identity_request_item i, "+
			     "spt_identity_request r, "+
			     "cus_activity_user c "+
			"where i.identity_request_id=r.id "+
			"and r.name = c.request_id "+
			"and i.value like '%Base24%' "+
			"and i.approval_state='Finished' "+
			"and TO_DATE(c.UPDATE_DATE, 'DD/MM/RRRR') = TO_DATE(?, 'DD/MM/RRRR') "+
			"order by c.update_date ";
		
		logger.debug(className + methodName + " sql : " + sql + " begintime " + beginTime + " end time " + endTime);
		
		PreparedStatement ps = ctx.getJdbcConnection().prepareStatement(sql);
		ps.setDate(1, new java.sql.Date(beginTime));
							 
		//ps.setLong(2, endTime);
		
		ResultSet rs = ps.executeQuery();
		Identity requester = null;
		Identity requestee = null;
		while(rs.next()){
			BcaCommonUserReport data = new BcaCommonUserReport();
			
			String requesteeId = rs.getString("target_display_name").toString();
			String requesterId = rs.getString("requester_display_name").toString();
			long modifiedDate = rs.getLong("modified");
			long tanggalPermohononan = rs.getLong("created");
			String requestId = String.valueOf(Long.parseLong(rs.getString("name")));
			String accountId = rs.getString("native_identity");
			
			logger.debug("requesteee : " + requesteeId + "requester : " + requesterId);
			
			if (requesterId.contains("CADANGAN")) {
				 requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesterId);
			} else {
				 requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesterId);
			}
			
			logger.debug("requester men : " + requester);
			
			if(requester != null){
				if (requesterId.contains("CADANGAN")) {
					data.setRequestor((String)requester.getAttribute(IdentityAttribute.DISPLAY_NAME));
				} else {
					data.setRequestor((String)requester.getAttribute(IdentityAttribute.SALUTATION_NAME));
				}
				
				data.setNipRequestor(requesterId);
			}else if("The Administrator".equalsIgnoreCase(requesterId)){
				data.setRequestor("Admin");
			}else{
				logger.debug(className + " requestor with id " + requesterId + " not found");
			}
			
			 
			
			if (requesteeId.contains("CADANGAN")) {
				requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesteeId);
			} else {
				requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesteeId);
			}
			
			logger.debug("requestee men : " + requestee);
			
			if(requestee != null){
				if(requesteeId.contains("CADANGAN")) {
					data.setUserId((String)requestee.getAttribute(IdentityAttribute.DISPLAY_NAME));
				}else {
					data.setUserId((String)requestee.getAttribute(IdentityAttribute.SALUTATION_NAME));
				}
			
				data.setNipRequestee(requesteeId);
			}else if("The Administrator".equalsIgnoreCase(requesteeId)){
				data.setUserId("Admin");
			}else{
				logger.debug(className + " requestee with id " + requesteeId + " not found");
			}
			
			logger.debug("data " + data.getRequestor() + data.getUserId());
			
			
			
			data.setTanggalPermohonan(new SimpleDateFormat("dd/MM/yyyy").format(tanggalPermohononan));
			data.setIdPermohonan(requestId);
			data.setAccountId(accountId);
			data.setTanggalSelesaiPermohonan(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(rs.getDate("update_date").getTime())));
			lstReport.add(data);
			
		}
		rs.close();
		ps.close();
		
		logger.debug(className + methodName + " list size " + lstReport.size());
		return lstReport;
	}
	
private List<BcaCommonUserReport> generateResetPasswordBase24ReportData(long beginTime, long endTime) throws GeneralException, SQLException{
		
		String methodName = "::generateResetPasswordBase24ReportData::";
		List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

		SailPointContext ctx = getContext();
		
		logger.debug(methodName + "Preparation to search");
		
		String sql = "select r.target_display_name, r.requester_display_name, r.modified, r.created, r.name, i.native_identity, r.completion_status, i.approval_state, c.update_date "+
				"from spt_identity_request_item i, spt_identity_request r, cus_activity_user c "+
				"where i.identity_request_id=r.id "+
				"and c.request_id = r.name "+
				"and i.application like '%Base24%' and i.approval_state='Finished' "+
				"and i.provisioning_state='Finished' "+
				"and i.name='PASSWORD' and i.operation='Set' "+
				"and TO_DATE(c.UPDATE_DATE, 'DD/MM/RRRR') = TO_DATE(?, 'DD/MM/RRRR') "+
				"order by c.update_date ";
		
		logger.debug(className + methodName + " sql : " + sql + " begintime " + beginTime + " end time " + endTime);
		
		PreparedStatement ps = ctx.getJdbcConnection().prepareStatement(sql);
		ps.setDate(1, new java.sql.Date(beginTime));
		//ps.setLong(1, beginTime);
		//ps.setLong(2, endTime);
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()){
			BcaCommonUserReport data = new BcaCommonUserReport();
			
			String requesteeId = rs.getString("target_display_name");
			String requesterId = rs.getString("requester_display_name");
			long modifiedDate = rs.getLong("modified");
			long tanggalPermohononan = rs.getLong("created");
			String requestId = String.valueOf(Long.parseLong(rs.getString("name")));
			String accountId = rs.getString("native_identity");
			
			

			Identity requester = null;
			if(requesterId.contains("CADANGAN")) {
				requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesterId);
				
			} else {
				requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesterId);
				
			}	
			
			if(requester != null){
				if (requesterId.contains("CADANGAN")){
					data.setRequestor((String)requester.getAttribute(IdentityAttribute.DISPLAY_NAME));
				}else {
					data.setRequestor((String)requester.getAttribute(IdentityAttribute.SALUTATION_NAME));
				}
				data.setNipRequestor(requesterId);
			}else if("The Administrator".equalsIgnoreCase(requesterId)){
				data.setRequestor("Admin");
			}else{
				logger.debug(className + " requestor with id " + requesterId + " not found");
			}
			
			Identity requestee =  null;
			if(requesteeId.contains("CADANGAN")) {
				requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesteeId);				
			} else {
				 requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesteeId);	
			}
			
			if(requestee != null){
				if (requesteeId.contains("CADANGAN")){
					data.setUserId((String)requestee.getAttribute(IdentityAttribute.DISPLAY_NAME));
				}else {
					data.setUserId((String)requestee.getAttribute(IdentityAttribute.SALUTATION_NAME));
				}
				data.setNipRequestee(requesteeId);
			}else if("The Administrator".equalsIgnoreCase(requesteeId)){
				data.setUserId("Admin");
			}else{
				logger.debug(className + " requestee with id " + requesteeId + " not found");
			}
			
			data.setTanggalPermohonan(new SimpleDateFormat("dd/MM/yyyy").format(tanggalPermohononan));
			data.setIdPermohonan(requestId);
			data.setAccountId(accountId);
			data.setTanggalSelesaiPermohonan(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(rs.getDate("update_date").getTime())));
			
			lstReport.add(data);
			
		}
		rs.close();
		ps.close();
		
		logger.debug(className + methodName + " list size " + lstReport.size());
		return lstReport;
	}

private List<BcaCommonUserReport> generateDeleteBase24ReportData(long beginTime, long endTime) throws GeneralException, SQLException{
	
	String methodName = "::generateDeleteBase24ReportData::";
	List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

	SailPointContext ctx = getContext();
	
	logger.debug(methodName + "Preparation to search");
	
	String sql = "select r.target_display_name, r.requester_display_name, r.modified, r.created, r.name, i.native_identity, r.completion_status, i.approval_state, c.update_date "+
			"from spt_identity_request_item i, spt_identity_request r, cus_activity_user c "+
			"where i.identity_request_id=r.id "+
			"and c.request_id = r.name "+
			"and i.application like 'Base24%' and i.approval_state='Finished' and i.provisioning_state='Finished' "+
			"and i.operation='Delete' "+
			"and TO_DATE(c.UPDATE_DATE, 'DD/MM/RRRR') = TO_DATE(?, 'DD/MM/RRRR') "+
			"order by c.update_date ";
	
	logger.debug(className + methodName + " sql : " + sql + " begintime " + beginTime + " end time " + endTime);
	
	PreparedStatement ps = ctx.getJdbcConnection().prepareStatement(sql);
	ps.setDate(1, new java.sql.Date(beginTime));
	//ps.setLong(1, beginTime);
	//ps.setLong(2, endTime);
	
	ResultSet rs = ps.executeQuery();
	
	while(rs.next()){
		BcaCommonUserReport data = new BcaCommonUserReport();
		
		String requesteeId = rs.getString("target_display_name");
		String requesterId = rs.getString("requester_display_name");
		long modifiedDate = rs.getLong("modified");
		long tanggalPermohononan = rs.getLong("created");
		String requestId = String.valueOf(Long.parseLong(rs.getString("name")));
		String accountId = rs.getString("native_identity");
		
		
		Identity requester = null;
		if(requesterId.contains("CADANGAN")) {
			requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesterId);
			
		} else {
			requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesterId);
			
		}	
		
		if(requester != null){
			if (requesterId.contains("CADANGAN")){
				data.setRequestor((String)requester.getAttribute(IdentityAttribute.DISPLAY_NAME));
			}else {
				data.setRequestor((String)requester.getAttribute(IdentityAttribute.SALUTATION_NAME));
			}
			data.setNipRequestor(requesterId);
		}else if("The Administrator".equalsIgnoreCase(requesterId)){
			data.setRequestor("Admin");
		}else{
			logger.debug(className + " requestor with id " + requesterId + " not found");
		}
		
		Identity requestee =  null;
		if(requesteeId.contains("CADANGAN")) {
			requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesteeId);				
		} else {
			 requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesteeId);	
		}
		
		if(requestee != null){
			if (requesteeId.contains("CADANGAN")){
				data.setUserId((String)requestee.getAttribute(IdentityAttribute.DISPLAY_NAME));
			}else {
				data.setUserId((String)requestee.getAttribute(IdentityAttribute.SALUTATION_NAME));
			}
			data.setNipRequestee(requesteeId);
		}else if("The Administrator".equalsIgnoreCase(requesteeId)){
			data.setUserId("Admin");
		}else{
			logger.debug(className + " requestee with id " + requesteeId + " not found");
		}
		
		data.setTanggalPermohonan(new SimpleDateFormat("dd/MM/yyyy").format(tanggalPermohononan));
		data.setIdPermohonan(requestId);
		data.setAccountId(accountId);
		data.setTanggalSelesaiPermohonan(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(rs.getDate("update_date").getTime())));
		
		lstReport.add(data);
		
	}
	rs.close();
	ps.close();
	
	logger.debug(className + methodName + " list size " + lstReport.size());
	return lstReport;
}
	
private List<BcaCommonUserReport> generateBase24DisableReportData(long beginTime, long endTime, boolean isReminder) throws GeneralException, SQLException{
		
		String methodName = "::generateBase24DisableReportData::";
		List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

		SailPointContext ctx = getContext();
		
		QueryOptions localQueryOptions = new QueryOptions();
		
		if(isReminder){
			
			Date date = new Date(endTime);
			
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(date);
			
			cal.add(Calendar.MONTH, -1);
			
			Date newDate = new Date(cal.getTimeInMillis());
			
			localQueryOptions.addFilter(Filter.ge("modified", newDate));
			
		}else{
			
			localQueryOptions.addFilter(Filter.ge("modified", new Date(beginTime)));
			
			logger.debug(className + methodName + " set beginTime with " + new Date(beginTime));
			
			localQueryOptions.addFilter(Filter.lt("modified", new Date(endTime)));
			
			logger.debug(className + methodName + " set endTime with " + new Date(endTime));
			
			
		}
		
		localQueryOptions.addFilter(Filter.or(Filter.eq("application", (String)"Base24 File Feed"), Filter.like("value", "Base24", MatchMode.START)));
		
		localQueryOptions.addFilter(Filter.eq("operation", (String)"Disable"));
		
		localQueryOptions.addFilter(Filter.eq("approvalState", (String)"Finished"));
		
		localQueryOptions.addFilter(Filter.eq("provisioningState", (String)"Finished"));
		
		localQueryOptions.addOrdering("modified", true);
		
		logger.debug(methodName + "Preparation to search");

		Iterator<IdentityRequestItem> it = ctx.search(IdentityRequestItem.class, localQueryOptions);
		
		
		
		while(it.hasNext()){
			
			boolean willAdd = true;
			
			BcaCommonUserReport data = new BcaCommonUserReport();
			
			IdentityRequestItem item = (IdentityRequestItem)it.next();
			
			IdentityRequest req = item.getIdentityRequest();
			
			String requesteeId = req.getTargetDisplayName();
			String requesterId = req.getRequesterDisplayName();
			
			String requestId = String.valueOf(Long.parseLong(req.getName()));
			String accountId = item.getNativeIdentity();
			

			String resumeDate = (String)item.getAttributes().get("RESUME_DATE");
			
			//isReminder = menentukan apakah report ini termasuk pengaktifan kembali keesokan harinya
			if(isReminder){
				
				if(resumeDate != null && !"".equalsIgnoreCase(resumeDate)){
					
					String lastRevoke = String.valueOf(Integer.parseInt(resumeDate) - 1);
					SimpleDateFormat baseFormat = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
					
					Date dt = new Date();
					try {
						dt = df1.parse(getReportDate());
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					
					String newDt = baseFormat.format(dt);
					willAdd = newDt.equalsIgnoreCase(lastRevoke);
					logger.debug(className + methodName + " pengaktifan kembali " + newDt + " will add " + willAdd);
						
				}
				
			}else{
				willAdd = true;
			}
			
		if(willAdd){
				
				Identity requester = null;
				if(requesterId.contains("CADANGAN")) {
					requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesterId);
					
				} else {
					requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesterId);
					
				}
				 
				if(requester != null){
					if(requesterId.contains("CADANGAN")) {
						data.setRequestor((String)requester.getAttribute(IdentityAttribute.DISPLAY_NAME));
					} else {
						data.setRequestor((String)requester.getAttribute(IdentityAttribute.SALUTATION_NAME));
					}
					
					data.setNipRequestor(requesterId);
				}else if("The Administrator".equalsIgnoreCase(requesterId)){
					data.setRequestor("Admin");
				}else{
					logger.debug(className + " requestor with id " + requesterId + " not found");
				}
				
				Identity requestee =  null;
				if(requesteeId.contains("CADANGAN")) {
					requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesteeId);				
				} else {
					 requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesteeId);
						
					
				}
				
				if(requestee != null){
					
					if(requesteeId.contains("CADANGAN")) {
						data.setUserId((String)requestee.getAttribute(IdentityAttribute.DISPLAY_NAME));
					}else {
						data.setUserId((String)requestee.getAttribute(IdentityAttribute.SALUTATION_NAME));
					}
					data.setNipRequestee(requesteeId);
				}else if("The Administrator".equalsIgnoreCase(requesteeId)){
					data.setUserId("Admin");
				}else{
					logger.debug(className + " requestee with id " + requesteeId + " not found");
				}
				
				data.setTanggalPermohonan(new SimpleDateFormat("dd/MM/yyyy").format(req.getCreated()));
				logger.debug(className + methodName + "tanggal Permohonan " + req.getCreated());
				data.setIdPermohonan(requestId);
				data.setAccountId(accountId);
				data.setTanggalSelesaiPermohonan(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(req.getModified()));
				logger.debug(className + methodName + "tanggal selesai permohonan " + req.getModified());
				
				String revokeDate = (String)item.getAttributes().get("REVOKE_DATE");
				
				if(revokeDate != null && !"".equalsIgnoreCase(revokeDate)){
					SimpleDateFormat baseFormat = new SimpleDateFormat("yyyyMMdd");
					try {
						Date revokeBase = baseFormat.parse(revokeDate);
						
						data.setTanggalMulaiRevoke(new SimpleDateFormat("dd/MM/yyyy").format(revokeBase));
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				
				if(resumeDate != null && !"".equalsIgnoreCase(resumeDate)){
					
					String resumeDateMin1 = String.valueOf(Integer.parseInt(resumeDate) - 1);
					SimpleDateFormat baseFormat = new SimpleDateFormat("yyyyMMdd");
					try {
						Date resumeBase = baseFormat.parse(resumeDateMin1);
						
						data.setTanggalSelesaiRevoke(new SimpleDateFormat("dd/MM/yyyy").format(resumeBase));
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				
				
				
				lstReport.add(data);
				
			}	
			
		}	
		
		logger.debug(className + methodName + " list size " + lstReport.size());
		
		return lstReport;
	}

private List<BcaCommonUserReport> generateBase24EnableReportData(long beginTime, long endTime) throws GeneralException, SQLException{
	
	String methodName = "::generateBase24EnableReportData::";
	List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();

	SailPointContext ctx = getContext();
	
	QueryOptions localQueryOptions = new QueryOptions();
	
	localQueryOptions.addFilter(Filter.ge("modified", new Date(beginTime)));
	
	logger.debug(className + methodName + " set beginTime with " + beginTime);
	
	localQueryOptions.addFilter(Filter.lt("modified", new Date(endTime)));
	
	logger.debug(className + methodName + " set endTime with " + endTime);
	
	localQueryOptions.addFilter(Filter.eq("operation", (String)"Enable"));
	
	localQueryOptions.addFilter(Filter.eq("approvalState", (String)"Finished"));
	
	localQueryOptions.addFilter(Filter.eq("provisioningState", (String)"Finished"));
	
	localQueryOptions.addFilter(Filter.eq("application", (String)"Base24 File Feed"));
	
	
	
	localQueryOptions.addOrdering("modified", true);
	
	logger.debug(methodName + "Preparation to search");

	Iterator<IdentityRequestItem> it = ctx.search(IdentityRequestItem.class, localQueryOptions);
	
	
	while(it.hasNext()){
		
		BcaCommonUserReport data = new BcaCommonUserReport();
		
		IdentityRequestItem item = (IdentityRequestItem)it.next();
		
		IdentityRequest req = item.getIdentityRequest();
		
		String requesteeId = req.getTargetDisplayName();
		String requesterId = req.getRequesterDisplayName();
		
		String requestId = String.valueOf(Long.parseLong(req.getName()));
		String accountId = item.getNativeIdentity();
		
		Identity requester = null;
		if(requesterId.contains("CADANGAN")) {
			requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesterId);
			
		} else {
			requester = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesterId);
			
		}
		
		
		if(requester != null){
			if(requesterId.contains("CADANGAN")) {
				data.setRequestor((String)requester.getAttribute(IdentityAttribute.DISPLAY_NAME));
			} else {
				data.setRequestor((String)requester.getAttribute(IdentityAttribute.SALUTATION_NAME));
			}
			
			data.setNipRequestor(requesterId);
		}else if("The Administrator".equalsIgnoreCase(requesterId)){
			data.setRequestor("Admin");
		}else{
			logger.debug(className + " requestor with id " + requesterId + " not found");
		}
		
		Identity requestee =  null;
		if(requesteeId.contains("CADANGAN")) {
			requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.NAME, requesteeId);				
		} else {
			 requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, requesteeId);
				
			
		}
		
		if(requestee != null){
			
			if(requesteeId.contains("CADANGAN")) {
				data.setUserId((String)requestee.getAttribute(IdentityAttribute.DISPLAY_NAME));
			}else {
				data.setUserId((String)requestee.getAttribute(IdentityAttribute.SALUTATION_NAME));
			}
			
			data.setNipRequestee(requesteeId);
		}else if("The Administrator".equalsIgnoreCase(requesteeId)){
			data.setUserId("Admin");
		}else{
			logger.debug(className + " requestee with id " + requesteeId + " not found");
		}
		
		data.setTanggalPermohonan(new SimpleDateFormat("dd/MM/yyyy").format(req.getCreated()));
		data.setIdPermohonan(requestId);
		data.setAccountId(accountId);
		data.setTanggalSelesaiPermohonan(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(req.getModified()));
		
		lstReport.add(data);
		
	}
	
	
	logger.debug(className + methodName + " list size " + lstReport.size());
	
	return lstReport;
}
	

	
	private List<BcaCommonUserReport> getReportData(Iterator<IdentityRequest> it, String typeReport, String status, long start, long end) throws GeneralException{
		
		String methodName = "::getReportData::";
		
		SailPointContext ctx = getContext();
		
		logger.debug(className + methodName + " preparation to proceed");
		
		List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();
		
		int i = 1;
		
		while(it.hasNext()){
			
			boolean willBeProcessed = true;
			
			BcaCommonUserReport data = new BcaCommonUserReport();
			
			IdentityRequest req = it.next();
			
			logger.debug(className + methodName + " process req id " + req.getName());
			
			Identity requestee = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, req.getTargetDisplayName());
			
			if(requestee == null){
				willBeProcessed = false;
			}
			
			if(willBeProcessed){
				
				String requesteeBranchCode = (String)requestee.getAttribute(IdentityAttribute.BRANCH_CODE);
				
				if("cabang".equalsIgnoreCase(typeReport)){
					
					if(isChecker1Branch(getLoggedInUser())){
						Map branchMapping = getBranchKcuMap((String)getLoggedInUser().getAttribute(IdentityAttribute.BRANCH_CODE));
						
						if(branchMapping.containsKey(requesteeBranchCode)){
							willBeProcessed = true;
						}else{
							willBeProcessed = false;
						}
					}else{
						String divisionCode = (String)getLoggedInUser().getAttribute(IdentityAttribute.DIVISION_CODE);
						
						willBeProcessed = divisionCode.equalsIgnoreCase((String)requestee.getAttribute(IdentityAttribute.DIVISION_CODE));
					}
					
				}
				
				logger.debug(className + methodName + " is branch " + isChecker1Branch(getLoggedInUser()));
				
				logger.debug(className + methodName + " target branch code " + requesteeBranchCode);
			}
			
			
			if(willBeProcessed){	
				
				if("complete".equalsIgnoreCase(status) || "pending-reject".equalsIgnoreCase(status)){
					
					List<IdentityRequestItem> listItem = req.getItems();
					
					Iterator<IdentityRequestItem> itListItem = listItem.iterator();
					
					while(itListItem.hasNext()){
						IdentityRequestItem reqItemChecked = (IdentityRequestItem)itListItem.next();
						
						if(reqItemChecked.getApprovalState() != null && !"Finished".equalsIgnoreCase(reqItemChecked.getApprovalState().toString()) && "complete".equalsIgnoreCase(status)){
							willBeProcessed = false;
						}else if(reqItemChecked.getApprovalState() != null && "Finished".equalsIgnoreCase(reqItemChecked.getApprovalState().toString()) && "pending-reject".equalsIgnoreCase(status)){
							willBeProcessed = false;
						}
					}
					
				}
			}
			
			if(willBeProcessed){
				Attributes<String, Object> at = req.getAttributes();
				
				List<ApprovalSummary> lstApproval = (List<ApprovalSummary>)at.get("approvalSummaries");
				
				if(lstApproval != null){
					Iterator<ApprovalSummary> itApproval = lstApproval.iterator();
					
					while(itApproval.hasNext() && lstApproval != null && lstApproval.size() > 1){
						
						ApprovalSummary app = itApproval.next();
						
						if(app.getEndDate() != null && app.getEndDate().before(new Date(end)) && app.getEndDate().after(new Date(start))){
							setApproverChecker(data, app);
						}
						
						if(data.isValid() && ("".equalsIgnoreCase(data.getPermohonan()) || data.getPermohonan() == null)){
							data.setPermohonan(getDisplayValueFromApprovalSummary(app));
						}
						
						if(data.isValid() && ("".equalsIgnoreCase(data.getAccountId()) || data.getAccountId() == null) && data.getPermohonan() != null){
							data.setAccountId(getAccountIdFromIdentityRequest(req, data.getPermohonan()));
						}
						
					}
					
					if(data.isValid()){
						
						data.setIdPermohonan(String.valueOf(Long.parseLong(req.getName())));
						data.setTanggalPermohonan(new SimpleDateFormat("dd/MM/yyyy").format(req.getCreated()));
						
						if(req.getCompletionStatus() != null){
							data.setStatusPermohonan(req.getCompletionStatus().toString());
						}else{
							data.setStatusPermohonan("Pending");
						}
						
						
						
						Identity requestor = IdentityUtil.searchIdentity(ctx, IdentityAttribute.EMPLOYEE_ID, req.getRequesterDisplayName());
						if(requestor != null){
							data.setRequestor((String)requestor.getAttribute(IdentityAttribute.SALUTATION_NAME));
						}else if("The Administrator".equalsIgnoreCase(req.getRequesterDisplayName())){
							data.setRequestor("Admin");
						}else{
							logger.debug(className + methodName + " requestor with id " + req.getRequesterDisplayName() + " not found");
						}
						
						if(requestee != null){
							data.setUserId((String)requestee.getAttribute(IdentityAttribute.SALUTATION_NAME));
							if(isChecker1Branch(getLoggedInUser())){
								data.setKodeCabang((String)requestee.getAttribute(IdentityAttribute.BRANCH_CODE));
							}else{
								data.setKodeCabang((String)requestee.getAttribute(IdentityAttribute.DIVISION_CODE));
							}
							
						}else{
							logger.debug(className + methodName + " requestee with id " + req.getTargetDisplayName() + " not found");
						}
						
						lstReport.add(data);
					}else{
						logger.debug(className + methodName + " req id " + req.getName() + " not valid");
					}
				}
				
				
			}
		
		}
		
		return lstReport;
	}
	
	private String getDisplayValueFromApprovalSummary(ApprovalSummary summary){
		
		String methodName = "::getDisplayValueFromApprovalSummary::";
		String retVal = "";
		
		if(summary != null){
			ApprovalSet appSet = summary.getApprovalSet();
			
			if(appSet != null){
				List<ApprovalItem> lstItem = appSet.getItems();
				
				Iterator<ApprovalItem> itItem = lstItem.iterator();
				
				while(itItem.hasNext()){
					ApprovalItem appItem = itItem.next();
					if(CommonUtil.IIQ_APPLICATION.equalsIgnoreCase(appItem.getApplication())){
						retVal = appItem.getDisplayValue();
						logger.debug(className + methodName + " retVal : " + retVal);
					}
				}
			}
		}
		
		return retVal;
	}
	
	private String getAccountIdFromIdentityRequest(IdentityRequest req, String type){
		String methodName = "::getAccountIdFromIdentityRequest::";
		String accountId = "";
		
		logger.debug(className + methodName + "type " + type);
		
		if(req != null){
			List<IdentityRequestItem> lstItem = req.getItems();
			Iterator<IdentityRequestItem> itReqItem = lstItem.iterator();
			
			if("Exchange".indexOf(type) >= 0){
				while(itReqItem.hasNext()){
					IdentityRequestItem item = itReqItem.next();
					if("mail".equalsIgnoreCase(item.getName())){
						accountId = (String)item.getValue();
						logger.debug(className + methodName + " req id " + req.getName() + " accountid " + accountId);
					}
				}
			}else if(type.indexOf("Base24") >= 0){
				
				while(itReqItem.hasNext()){
					IdentityRequestItem item = itReqItem.next();
					accountId = item.getNativeIdentity();
					logger.debug(className + methodName + " req id " + req.getName() + " accountid " + accountId);
				}
			}
			else{
				
				ProvisioningProject p = req.getProvisionedProject();
				
				if(p != null){
					
					List<ExpansionItem> lstExp = p.getExpansionItems();
					
					if(lstExp != null){
						
						Iterator<ExpansionItem> itExp = lstExp.iterator();
						
						while(itExp.hasNext() && ("".equalsIgnoreCase(accountId) || accountId == null)){
							ExpansionItem item = itExp.next();
							accountId = item.getNativeIdentity();
							logger.debug(className + methodName + " req id " + req.getName() + " accountid " + accountId);
						}
					}
					
				}
				
			}
			
		}
		
		logger.debug(className + methodName + " return accountId " + accountId);
		return accountId;
	}
	
	private void setApproverChecker(BcaCommonUserReport rep, ApprovalSummary appSummary) throws GeneralException{

		String approver1 = "Approver 1";
		String approval1 = "Approval 1";
		String approver2 = "Approver 2";
		String checker1 = "Checker 1";
		String checker2 = "Checker 2";
		String saAplickasi = "SA Aplikasi";
		
		String requestName = appSummary.getRequest();
		String empId = appSummary.getCompleter();
		
		Identity approver = IdentityUtil.searchIdentity(getContext(), IdentityAttribute.EMPLOYEE_ID, empId);
		String approverName = "";
		
		if(approver != null){
			approverName = (String)approver.getAttribute(IdentityAttribute.SALUTATION_NAME);
		}
		
		if(requestName != null){
			if(requestName.indexOf(approver1) >= 0 || requestName.indexOf(approval1) >= 0){
				rep.setApprover1(approverName);
			}else if(requestName.indexOf(approver2) >= 0){
				rep.setApprover2(approverName);
			}else if(requestName.indexOf(checker1) >= 0){
				rep.setChecker1(approverName);
			}else if(requestName.indexOf(checker2) >= 0){
				rep.setChecker2(approverName);
			}else if(requestName.indexOf(saAplickasi) >= 0){
				rep.setSaAplikasi(approverName);
			}
		}
		
		rep.setValid(true);
		
		
	}
	
private PdfPTable getTableContent(List<BcaCommonUserReport> data, int len) throws DocumentException{
		
		PdfPTable content = new PdfPTable(len);
		Font fontContent = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
		
		float[] widths = new float[len];
		
		for(int i=0; i<len; i++){
			widths[i] = 1f;
		}
		
		content.setWidths(widths);
		content.setWidthPercentage(102);
		
		int borderStyle = Rectangle.BOX;
		
		Iterator<BcaCommonUserReport> itData = data.iterator();
		
		int i=0;
		
		while(itData.hasNext()){
			
			i++;
			
			BcaCommonUserReport rep = itData.next();
			
			PdfPCell cell1 = new PdfPCell(new Phrase(rep.getIdPermohonan(), fontContent));
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBorder(borderStyle);
			
			content.addCell(cell1);
			
			PdfPCell cell2 = new PdfPCell(new Phrase(rep.getTanggalPermohonan(), fontContent));
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setBorder(borderStyle);
			
			content.addCell(cell2);
			
			PdfPCell cell3 = new PdfPCell(new Phrase(rep.getPermohonan(), fontContent));
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setBorder(borderStyle);
			
			content.addCell(cell3);
			
			PdfPCell cell4 = new PdfPCell(new Phrase(rep.getUserId(), fontContent));
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBorder(borderStyle);
			
			content.addCell(cell4);
			
			PdfPCell cell5 = new PdfPCell(new Phrase(rep.getRequestor(), fontContent));
			cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell5.setBorder(borderStyle);
			
			content.addCell(cell5);
			
			PdfPCell cell6 = new PdfPCell(new Phrase(rep.getKodeCabang(), fontContent));
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell6.setBorder(borderStyle);
			
			content.addCell(cell6);
			
			PdfPCell cell7 = new PdfPCell(new Phrase(rep.getApprover1(), fontContent));
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setBorder(borderStyle);
			
			content.addCell(cell7);
			
			PdfPCell cell8 = new PdfPCell(new Phrase(rep.getApprover2(), fontContent));
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell8.setBorder(borderStyle);
			
			content.addCell(cell8);
			
			PdfPCell cell9 = new PdfPCell(new Phrase(rep.getChecker1(), fontContent));
			cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell9.setBorder(borderStyle);
			
			content.addCell(cell9);
			
			PdfPCell cell10 = new PdfPCell(new Phrase(rep.getChecker2(), fontContent));
			cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell10.setBorder(borderStyle);
			
			content.addCell(cell10);
			
			PdfPCell cell11 = new PdfPCell(new Phrase(rep.getSaAplikasi(), fontContent));
			cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell11.setBorder(borderStyle);
			
			content.addCell(cell11);
			
			PdfPCell cell12 = new PdfPCell(new Phrase(rep.getAccountId(), fontContent));
			cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell12.setBorder(borderStyle);
			
			content.addCell(cell12);
			
			PdfPCell cell13 = new PdfPCell(new Phrase(rep.getStatusPermohonan(), fontContent));
			cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell13.setBorder(borderStyle);
			
			content.addCell(cell13);
			
			PdfPCell cell14 = new PdfPCell(new Phrase(rep.getProvisioningStatus(), fontContent));
			cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell13.setBorder(borderStyle);
			
			content.addCell(cell14);
		}
		
		if(i == 0){
			PdfPCell cell1 = new PdfPCell(new Phrase(noRecordFound, fontContent));
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBorder(borderStyle);
			cell1.setColspan(len);
			
			content.addCell(cell1);
		}
		
		return content;
		
	}

private PdfPTable getTableContentDisableBase24(List<BcaCommonUserReport> data, int len) throws DocumentException{
	
	String methodName = "::getTableContentDisableBase24::";
	
	logger.debug(className + methodName + " started");
	
	PdfPTable content = new PdfPTable(len);
	Font fontContent = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
	
	float[] widths = new float[len];
	
	for(int i=0; i<len; i++){
		widths[i] = 1f;
	}
	
	content.setWidths(widths);
	content.setWidthPercentage(100);
	
	int borderStyle = Rectangle.BOX;
	
	Iterator<BcaCommonUserReport> itData = data.iterator();
	
	int i = 0;
	
	while(itData.hasNext()){
		
		logger.debug(className + methodName + " loop " + i++);
		
		BcaCommonUserReport rep = itData.next();
		
		PdfPCell cell1 = new PdfPCell(new Phrase(rep.getNipRequestor(), fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		
		content.addCell(cell1);
		
		PdfPCell cell2 = new PdfPCell(new Phrase(rep.getRequestor(), fontContent));
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setBorder(borderStyle);
		
		content.addCell(cell2);
		
		PdfPCell cell3 = new PdfPCell(new Phrase(rep.getTanggalPermohonan(), fontContent));
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setBorder(borderStyle);
		
		content.addCell(cell3);
		
		PdfPCell cell4 = new PdfPCell(new Phrase(rep.getIdPermohonan(), fontContent));
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setBorder(borderStyle);
		
		content.addCell(cell4);
		
		PdfPCell cell6 = new PdfPCell(new Phrase(rep.getAccountId(), fontContent));
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setBorder(borderStyle);
		
		content.addCell(cell6);
		
		PdfPCell cell5 = new PdfPCell(new Phrase(rep.getUserId(), fontContent));
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setBorder(borderStyle);
		
		content.addCell(cell5);
		
		PdfPCell cell7 = new PdfPCell(new Phrase(rep.getTanggalMulaiRevoke(), fontContent));
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setBorder(borderStyle);
		
		content.addCell(cell7);
		
		PdfPCell cell8 = new PdfPCell(new Phrase(rep.getTanggalSelesaiRevoke(), fontContent));
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setBorder(borderStyle);
		
		content.addCell(cell8);
		
		PdfPCell cell9 = new PdfPCell(new Phrase(rep.getTanggalSelesaiPermohonan(), fontContent));
		cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell9.setBorder(borderStyle);
		
		content.addCell(cell9);
		
		
	}
	
	if(i == 0){
		PdfPCell cell1 = new PdfPCell(new Phrase(noRecordFound, fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		cell1.setColspan(len);
		
		content.addCell(cell1);
	}
	
	return content;
	
}

private PdfPTable getTableContentEnableBase24(List<BcaCommonUserReport> data, int len) throws DocumentException{
	
	String methodName = "::getTableContentEnableBase24::";
	
	logger.debug(className + methodName + " started");
	
	PdfPTable content = new PdfPTable(len);
	Font fontContent = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
	
	float[] widths = new float[len];
	
	for(int i=0; i<len; i++){
		widths[i] = 1f;
	}
	
	content.setWidths(widths);
	content.setWidthPercentage(100);
	
	int borderStyle = Rectangle.BOX;
	
	Iterator<BcaCommonUserReport> itData = data.iterator();
	
	int i = 0;
	
	while(itData.hasNext()){
		
		logger.debug(className + methodName + " loop " + i++);
		
		BcaCommonUserReport rep = itData.next();
		
		PdfPCell cell1 = new PdfPCell(new Phrase(rep.getNipRequestor(), fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		
		content.addCell(cell1);
		
		PdfPCell cell2 = new PdfPCell(new Phrase(rep.getRequestor(), fontContent));
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setBorder(borderStyle);
		
		content.addCell(cell2);
		
		PdfPCell cell3 = new PdfPCell(new Phrase(rep.getTanggalPermohonan(), fontContent));
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setBorder(borderStyle);
		
		content.addCell(cell3);
		
		PdfPCell cell4 = new PdfPCell(new Phrase(rep.getIdPermohonan(), fontContent));
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setBorder(borderStyle);
		
		content.addCell(cell4);
		
		PdfPCell cell6 = new PdfPCell(new Phrase(rep.getAccountId(), fontContent));
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setBorder(borderStyle);
		
		content.addCell(cell6);
		
		PdfPCell cell5 = new PdfPCell(new Phrase(rep.getUserId(), fontContent));
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setBorder(borderStyle);
		
		content.addCell(cell5);
		
		PdfPCell cell7 = new PdfPCell(new Phrase(rep.getTanggalSelesaiPermohonan(), fontContent));
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setBorder(borderStyle);
		
		content.addCell(cell7);
		
	}
	
	if(i == 0){
		PdfPCell cell1 = new PdfPCell(new Phrase(noRecordFound, fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		cell1.setColspan(len);
		
		content.addCell(cell1);
	}
	
	return content;
	
}

private PdfPTable getTableContentNewBase24(List<BcaCommonUserReport> data, int len) throws DocumentException{
	
	String methodName = "::getTableContentNewBase24::";
	
	logger.debug(className + methodName + " started");
	
	PdfPTable content = new PdfPTable(len);
	Font fontContent = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
	
	float[] widths = new float[len];
	
	for(int i=0; i<len; i++){
		widths[i] = 1f;
	}
	
	content.setWidths(widths);
	content.setWidthPercentage(100);
	
	int borderStyle = Rectangle.BOX;
	
	Iterator<BcaCommonUserReport> itData = data.iterator();
	
	int i = 0;
	
	while(itData.hasNext()){
		
		logger.debug(className + methodName + " loop " + i++);
		
		BcaCommonUserReport rep = itData.next();
		
		PdfPCell cell1 = new PdfPCell(new Phrase(rep.getNipRequestor(), fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		
		content.addCell(cell1);
		
		PdfPCell cell2 = new PdfPCell(new Phrase(rep.getRequestor(), fontContent));
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setBorder(borderStyle);
		
		content.addCell(cell2);
		
		PdfPCell cell3 = new PdfPCell(new Phrase(rep.getTanggalPermohonan(), fontContent));
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setBorder(borderStyle);
		
		content.addCell(cell3);
		
		PdfPCell cell4 = new PdfPCell(new Phrase(rep.getIdPermohonan(), fontContent));
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setBorder(borderStyle);
		
		content.addCell(cell4);
		
		PdfPCell cell5 = new PdfPCell(new Phrase(rep.getUserId(), fontContent));
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setBorder(borderStyle);
		
		content.addCell(cell5);
		
		PdfPCell cell6 = new PdfPCell(new Phrase(rep.getAccountId(), fontContent));
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setBorder(borderStyle);
		
		content.addCell(cell6);
		
		PdfPCell cell7 = new PdfPCell(new Phrase(rep.getTanggalSelesaiPermohonan(), fontContent));
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setBorder(borderStyle);
		
		content.addCell(cell7);
	}
	
	if(i == 0){
		PdfPCell cell1 = new PdfPCell(new Phrase(noRecordFound, fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		cell1.setColspan(len);
		
		content.addCell(cell1);
	}
	
	return content;
	
}

private PdfPTable getTableContentResetPasswordBase24(List<BcaCommonUserReport> data, int len) throws DocumentException{
	
	String methodName = "::getTableContentResetPasswordBase24::";
	
	logger.debug(className + methodName + " started");
	
	PdfPTable content = new PdfPTable(len);
	Font fontContent = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
	
	float[] widths = new float[len];
	
	for(int i=0; i<len; i++){
		widths[i] = 1f;
	}
	
	content.setWidths(widths);
	content.setWidthPercentage(100);
	
	int borderStyle = Rectangle.BOX;
	
	Iterator<BcaCommonUserReport> itData = data.iterator();
	
	int i = 0;
	
	while(itData.hasNext()){
		
		logger.debug(className + methodName + " loop " + i++);
		
		BcaCommonUserReport rep = itData.next();
		
		PdfPCell cell1 = new PdfPCell(new Phrase(rep.getNipRequestor(), fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		
		content.addCell(cell1);
		
		PdfPCell cell2 = new PdfPCell(new Phrase(rep.getRequestor(), fontContent));
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setBorder(borderStyle);
		
		content.addCell(cell2);
		
		PdfPCell cell3 = new PdfPCell(new Phrase(rep.getTanggalPermohonan(), fontContent));
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setBorder(borderStyle);
		
		content.addCell(cell3);
		
		PdfPCell cell4 = new PdfPCell(new Phrase(rep.getIdPermohonan(), fontContent));
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setBorder(borderStyle);
		
		content.addCell(cell4);
		
		PdfPCell cell5 = new PdfPCell(new Phrase(rep.getUserId(), fontContent));
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setBorder(borderStyle);
		
		content.addCell(cell5);
		
		PdfPCell cell6 = new PdfPCell(new Phrase(rep.getAccountId(), fontContent));
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setBorder(borderStyle);
		
		content.addCell(cell6);
		
		PdfPCell cell7 = new PdfPCell(new Phrase(rep.getTanggalSelesaiPermohonan(), fontContent));
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setBorder(borderStyle);
		
		content.addCell(cell7);
		
	}
	
	if(i == 0){
		PdfPCell cell1 = new PdfPCell(new Phrase(noRecordFound, fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		cell1.setColspan(len);
		
		content.addCell(cell1);
	}
	
	return content;
	
}

private PdfPTable getTableContentDeleteBase24(List<BcaCommonUserReport> data, int len) throws DocumentException{
	
	String methodName = "::getTableContentDeleteBase24::";
	
	logger.debug(className + methodName + " started");
	
	PdfPTable content = new PdfPTable(len);
	Font fontContent = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
	
	float[] widths = new float[len];
	
	for(int i=0; i<len; i++){
		widths[i] = 1f;
	}
	
	content.setWidths(widths);
	content.setWidthPercentage(100);
	
	int borderStyle = Rectangle.BOX;
	
	Iterator<BcaCommonUserReport> itData = data.iterator();
	
	int i = 0;
	
	while(itData.hasNext()){
		
		logger.debug(className + methodName + " loop " + i++);
		
		BcaCommonUserReport rep = itData.next();
		
		PdfPCell cell1 = new PdfPCell(new Phrase(rep.getNipRequestor(), fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		
		content.addCell(cell1);
		
		PdfPCell cell2 = new PdfPCell(new Phrase(rep.getRequestor(), fontContent));
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setBorder(borderStyle);
		
		content.addCell(cell2);
		
		PdfPCell cell3 = new PdfPCell(new Phrase(rep.getTanggalPermohonan(), fontContent));
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setBorder(borderStyle);
		
		content.addCell(cell3);
		
		PdfPCell cell4 = new PdfPCell(new Phrase(rep.getIdPermohonan(), fontContent));
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setBorder(borderStyle);
		
		content.addCell(cell4);
		
		PdfPCell cell5 = new PdfPCell(new Phrase(rep.getUserId(), fontContent));
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setBorder(borderStyle);
		
		content.addCell(cell5);
		
		PdfPCell cell6 = new PdfPCell(new Phrase(rep.getAccountId(), fontContent));
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setBorder(borderStyle);
		
		content.addCell(cell6);
		
		PdfPCell cell7 = new PdfPCell(new Phrase(rep.getTanggalSelesaiPermohonan(), fontContent));
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setBorder(borderStyle);
		
		content.addCell(cell7);
	}
	
	if(i == 0){
		PdfPCell cell1 = new PdfPCell(new Phrase(noRecordFound, fontContent));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBorder(borderStyle);
		cell1.setColspan(len);
		
		content.addCell(cell1);
	}
	
	return content;
	
}

public String getDownloadFileName(String branchCode, String date, String selectedReport){
	String fileName = "FileName";
	
	if("Laporan Pengelolaan User Id".equalsIgnoreCase(selectedReport)){
		fileName = "PengelolaanUserId"+branchCode+"_"+date+".pdf";
	}else if("Laporan Pembuatan User Id Baru Base 24".equalsIgnoreCase(selectedReport)){
		fileName = "PembuatanUserBaruBase24_"+date+".pdf";
	}else if("Laporan Penonaktifan User Id Base 24".equalsIgnoreCase(selectedReport)){
		fileName = "PenonaktifanUserBase24_"+date+".pdf";		
	}else if("Laporan Pengaktifan Kembali User Id Base 24".equalsIgnoreCase(selectedReport)){
		fileName = "PengaktifanKembaliUserBase24_"+date+".pdf";
	}else if("Laporan Reset Password Base 24".equalsIgnoreCase(selectedReport)){
		fileName = "ResetPasswordBase24_"+date+".pdf";
	}else if("Laporan Penghapusan User Id Base 24".equalsIgnoreCase(selectedReport)){
		fileName = "DeleteBase24_"+date+".pdf";
	}else if("Rekapitulasi Pengelolaan User Id".equalsIgnoreCase(selectedReport)){
		fileName = "RekapitulasiComplete"+branchCode+"_"+date+".pdf";
	}else if("Rekapitulasi Pengelolaan User Id - Pending dan Reject".equalsIgnoreCase(selectedReport)){
		fileName = "RekapitulasiPendingReject"+branchCode+"_"+date+".pdf";
	}else if("Laporan User Id Base 24 Yang Harus diaktifkan Keesokan Harinya".equalsIgnoreCase(selectedReport)){
		fileName = "PengaktifanKembaliUserBase24_"+date+".pdf";
	}else if("Laporan Pengelolaan User Id Aplikasi".equalsIgnoreCase(selectedReport))
		fileName = "PengelolaanUserIDAplikasi_"+date+".pdf";
	
	return fileName;
}


public void downloadReport() throws IOException, GeneralException{
	
	String methodName = "::downloadReport::";
	
	//String FILE = getReportPath() + getDownloadFileName(String branchCode, String date, getSelectedReport());
	
	//logger.debug(className + methodName + " complete file path " + FILE);
	
	//setDownloadFile(FILE);
	
	logger.debug(className + methodName + " complete file path " + getDownloadFile());
	
	HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
	
	HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	
	//File file = new File(URLDecoder.decode(getDownloadFile(), "UTF-8"));
	//File file = new File(getDownloadFile());
	generateReport();
	FacesContext.getCurrentInstance().getExternalContext().redirect("DownloadReportServlet?fileName="+ getDownloadFile());
	/*	File file = new File(getDownloadFile());
    
    logger.debug(className + methodName + " file name " + file.getName());
    // Get content type by filename.
    String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(file.getName());
    logger.debug(className + methodName + " content type " + contentType);

    // If content type is unknown, then set the default value.
    // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
    // To add new content types, add new mime-mapping entry in web.xml.
    if (contentType == null) {
        contentType = "application/octet-stream";
    }

    // Init servlet response.
    response.reset();
    response.setBufferSize(10240);
    response.setContentType(contentType);
    /*response.setHeader("Content-Length", String.valueOf(file.length()));*/
    /*response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
    response.setContentLength((int)file.length());
    // Prepare streams.
    BufferedInputStream input = null;
    BufferedOutputStream output = null;

    try {
        // Open streams.
        input = new BufferedInputStream(new FileInputStream(file), 10240);
        output = new BufferedOutputStream(response.getOutputStream(), 10240);

        // Write file contents to response.
        byte[] buffer = new byte[10240];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        
        setMessage("Silahkan download file " + file.getName());
    } finally {
        // Gently close streams.
        close(output);
        close(input);
    }*/
}

private static void close(Closeable resource) {
    if (resource != null) {
        try {
            resource.close();
        } catch (IOException e) {
            // Do your thing with the exception. Print it, log it or mail it.
            e.printStackTrace();
        }
    }
}

private boolean isChecker1(Identity emp){
	
	boolean isTrue = false;
	
	List<Identity> lst = emp.getWorkgroups();
	
	Iterator<Identity> workgroupIterator = lst.iterator();
	
	while(workgroupIterator.hasNext()){
		Identity identity = (Identity)workgroupIterator.next();
		
		if(identity.getName().indexOf("Security Administrators") >= 0 && identity.getName().indexOf("SKES") < 0){
			isTrue = true;
		}
	}
	
	return isTrue;
}

private boolean isChecker1Branch(Identity emp){
	
	if(isChecker1(emp)){
		
		String branchCode = (String)emp.getAttribute(IdentityAttribute.BRANCH_CODE);
		
		return !CommonUtil.HQ_BRANCH_CODE.equalsIgnoreCase(branchCode);
	}else{
		return false;
	}
}

private boolean isChecker2(Identity emp){
	
		boolean isTrue = false;
		
		List<Identity> lst = emp.getWorkgroups();
		
		Iterator<Identity> workgroupIterator = lst.iterator();
		
		while(workgroupIterator.hasNext()){
			Identity identity = (Identity)workgroupIterator.next();
			
			if("SKES Security Administrators".equalsIgnoreCase(identity.getName())){
				isTrue = true;
				whereapp = "TABLE_1.PERMOHONAN LIKE '%BNO%' OR TABLE_1.PERMOHONAN LIKE '%PDNUSER%' OR TABLE_1.PERMOHONAN LIKE '%PDNUSER1%' OR TABLE_1.PERMOHONAN LIKE '%BNUSER%' " +
				"OR TABLE_1.PERMOHONAN LIKE '%BTR%' OR TABLE_1.PERMOHONAN LIKE '%INTLUSR%' OR TABLE_1.PERMOHONAN LIKE '%INTLCO%' OR TABLE_1.PERMOHONAN LIKE '%INTLCO1%' OR TABLE_1.PERMOHONAN LIKE '%INTLSADM%' OR TABLE_1.PERMOHONAN LIKE '%INTLTABL%' " +
				"OR TABLE_1.PERMOHONAN LIKE '%CCR%' OR TABLE_1.PERMOHONAN LIKE '%CARDINQ%' OR TABLE_1.PERMOHONAN LIKE '%CCUSERA%' OR TABLE_1.PERMOHONAN LIKE '%CCUSERB%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC4%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC5%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC6%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%GLM%' OR TABLE_1.PERMOHONAN LIKE '%FISUSR%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%IRT%' OR TABLE_1.PERMOHONAN LIKE '%IRPUSAT%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER1%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%ITS%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER1%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%LBU%' OR TABLE_1.PERMOHONAN LIKE '%LBUSER%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%ORT%' OR TABLE_1.PERMOHONAN LIKE '%ORUSER%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' "+
				"OR TABLE_1.PERMOHONAN LIKE '%RIM%' OR TABLE_1.PERMOHONAN LIKE '%RIMOUSER%' OR TABLE_1.PERMOHONAN LIKE '%RIMOKP%' ";
		
			}
			
			
		}
		
		return isTrue;
}


private boolean isCheckerApp(Identity emp){
	
	boolean isTrue = false;

	List<Identity> lst = emp.getWorkgroups();
	
	Iterator<Identity> workgroupIterator = lst.iterator();
	
	while(workgroupIterator.hasNext()){
		Identity identity = (Identity)workgroupIterator.next();
		
		if("Security Administrator Mainframe BNO".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%BNO%' OR TABLE_1.PERMOHONAN LIKE '%PDNUSER%' OR TABLE_1.PERMOHONAN LIKE '%PDNUSER1%' OR TABLE_1.PERMOHONAN LIKE '%BNUSER%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%BNO%' OR TABLE_1.PERMOHONAN LIKE '%PDNUSER%' OR TABLE_1.PERMOHONAN LIKE '%PDNUSER1%' OR TABLE_1.PERMOHONAN LIKE '%BNUSER%' ";
		}
		if("Security Administrator Mainframe BTR".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%BTR%' OR TABLE_1.PERMOHONAN LIKE '%INTLUSR%' OR TABLE_1.PERMOHONAN LIKE '%INTLCO%' OR TABLE_1.PERMOHONAN LIKE '%INTLCO1%' OR TABLE_1.PERMOHONAN LIKE '%INTLSADM%' OR TABLE_1.PERMOHONAN LIKE '%INTLTABL%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%BTR%' OR TABLE_1.PERMOHONAN LIKE '%INTLUSR%' OR TABLE_1.PERMOHONAN LIKE '%INTLCO%' OR TABLE_1.PERMOHONAN LIKE '%INTLCO1%' OR TABLE_1.PERMOHONAN LIKE '%INTLSADM%' OR TABLE_1.PERMOHONAN LIKE '%INTLTABL%' ";
		}
		if("Security Administrator Mainframe CCR".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%CCR%' OR TABLE_1.PERMOHONAN LIKE '%CARDINQ%' OR TABLE_1.PERMOHONAN LIKE '%CCUSERA%' OR TABLE_1.PERMOHONAN LIKE '%CCUSERB%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC4%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC5%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC6%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%CCR%' OR TABLE_1.PERMOHONAN LIKE '%CARDINQ%' OR TABLE_1.PERMOHONAN LIKE '%CCUSERA%' OR TABLE_1.PERMOHONAN LIKE '%CCUSERB%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC4%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC5%' OR TABLE_1.PERMOHONAN LIKE '%CARDPC6%' ";
		}
		if("Security Administrator Mainframe GLM".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%GLM%' OR TABLE_1.PERMOHONAN LIKE '%FISUSR%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%GLM%' OR TABLE_1.PERMOHONAN LIKE '%FISUSR%' ";
		}
		if("Security Administrator Mainframe IRT".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%IRT%' OR TABLE_1.PERMOHONAN LIKE '%IRPUSAT%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER1%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%IRT%' OR TABLE_1.PERMOHONAN LIKE '%IRPUSAT%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER1%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' ";
		}
		if("Security Administrator Mainframe ITS".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%ITS%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER1%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%ITS%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER1%' ";
		}
		if("Security Administrator Mainframe LBU".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%LBU%' OR TABLE_1.PERMOHONAN LIKE '%LBUSER%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%LBU%' OR TABLE_1.PERMOHONAN LIKE '%LBUSER%' ";
		}
		if("Security Administrator Mainframe ORT".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%ORT%' OR TABLE_1.PERMOHONAN LIKE '%ORUSER%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%ORT%' OR TABLE_1.PERMOHONAN LIKE '%ORUSER%' OR TABLE_1.PERMOHONAN LIKE '%ITSUSER%' ";
		}
		if("Security Administrator Mainframe RIM".equalsIgnoreCase(identity.getName())  ){
			isTrue = true;
			if(whereapp.equalsIgnoreCase(""))
				whereapp = "TABLE_1.PERMOHONAN LIKE '%RIM%' OR TABLE_1.PERMOHONAN LIKE '%RIMOUSER%' OR TABLE_1.PERMOHONAN LIKE '%RIMOKP%' ";
			else
				whereapp += "OR TABLE_1.PERMOHONAN LIKE '%RIM%' OR TABLE_1.PERMOHONAN LIKE '%RIMOUSER%' OR TABLE_1.PERMOHONAN LIKE '%RIMOKP%' ";
		}
	}
	
	return isTrue;
}

private Map getBranchKcuMap(String kcuBranchCode){
	String methodName = "::getBranchKcuMap::";
	
	logger.debug(className + methodName + " branch code KCU " + kcuBranchCode);
	
	Map<String, String> map = new HashMap<String, String>();
	
	try {
		Custom branchCustom = CommonUtil.getCustomObject(getContext(), CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT);
		
		Attributes attr = branchCustom.getAttributes();
		
		Map<String, List> mapAttrCustom = attr.getMap();
		
		List<Map> lstMap = (List)mapAttrCustom.get("branchTable");
		
		Iterator itMap = lstMap.iterator();
		
		while(itMap.hasNext()){
			Map mapBranch = (Map)itMap.next();
			
			String branchCode = (String)mapBranch.get("branchCode");
			String subBranchCode = (String)mapBranch.get("subBranchCode");
			
			if(branchCode != null && branchCode.equalsIgnoreCase(kcuBranchCode) && subBranchCode != null){
				map.put(subBranchCode, branchCode);
			}
		}
	} catch (GeneralException e) {
		e.printStackTrace();
	}
	
	return map;
}

private String getReportPath() throws GeneralException{
	String folder = "";
	
	folder = CommonUtil.getBcaSystemConfig(SailPointFactory.getCurrentContext(), "report folder");
	
	return folder;
}


	/*public static void main(String args[]) throws Exception{
		
		long dt = Long.parseLong("1495472400000");
		
		System.out.println(new Date(dt));
		
		String a = "[From '19914266' on Tue Oct 24 16:57:33 ICT 2017 'USER_ID : U914266']";
		System.out.println("UID : " + a.substring(a.indexOf(":"), a.length() - 1));
		
	}*/
	

	
}
