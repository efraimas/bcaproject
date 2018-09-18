package sailpoint.customtask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.common.CommonUtil;
import sailpoint.custom.report.BcaCommonUserReport;
import sailpoint.custom.report.CustomReportGenerator;
import sailpoint.object.Attributes;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

public class GenerateUserExpiredReportTask extends AbstractTaskExecutor{
	
	public static String CLASS_NAME = "::GenerateUserExpiredReportTask::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.GenerateUserExpiredReportTask");
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	
	@Override
	public void execute(SailPointContext arg0, TaskSchedule arg1, TaskResult arg2, Attributes<String, Object> arg3)
			throws Exception {
		// TODO Auto-generated method stub
		String methodName = "::execute::";
		logger.debug(CLASS_NAME + methodName + " Inside .... ");
		Date currentDate = new Date();
		String folderLocation = CommonUtil.getBcaSystemConfig(SailPointFactory.getCurrentContext(), "report folder");
		String fileName = "UserExpiredReport" + sdf.format(currentDate) + ".pdf";
		String fileLocation = folderLocation + fileName;
		logger.debug(CLASS_NAME + methodName + "file location : " + fileLocation);
		generateReport(arg0, currentDate.getTime(), fileLocation);
	}

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		return false;
	}
	
	private List<BcaCommonUserReport> generateUserExpiredReport(SailPointContext ctx, long time) throws GeneralException, SQLException{
		String methodName = "::generateUserExpiredReport::";
		List<BcaCommonUserReport> lstReport = new ArrayList<BcaCommonUserReport>();
		String sql = "SELECT emp_id, nama_karyawan, report_date, end_date, branch_code, branch, Status "+
				"From "+
				"(select  emp_id, nama_karyawan, report_date, end_date, branch_code, branch, status, to_char(end_date,'mon') as Bulan, to_char(end_date, 'yyyy') as Tahun from "+
				"(select emp_id, case when nama_karyawan is null then 'No data' else nama_karyawan end nama_karyawan, report_date, end_date, "+
				"case when branch_code is null then 'No data' else branch_code end branch_code, case when branch is null then 'No data' else branch end branch,"+
				"case when  (sysdate-to_date('1-1-1970 00:00:00','MM-DD-YYYY HH24:Mi:SS'))*1000*60*60*24 > extended10 then 'expired' else 'active' end status"+
				"FROM"+
				"(select extended10, name as EMP_ID,extended6 as nama_karyawan, sysdate as  report_date, to_date(to_date('01/01/1970','DD/MM/YYYY') + (extended10/1000/60/60/24),'DD/MM/YYYY')  as END_DATE , "+
				"extended11 as Branch_Code,  extended7 as Branch  from spt_identity where name  like '5%' and extended10 is not null"+
				")"+
				")"+
				")";
		
		logger.debug(CLASS_NAME + methodName + " sql : " + sql);
		
		PreparedStatement ps = ctx.getJdbcConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		logger.debug("List size :::: " + rs.getRow());
		
		while(rs.next()){
			BcaCommonUserReport data = new BcaCommonUserReport();			
			data.setAccountId(rs.getString("emp_id"));
			data.setUserId(rs.getString("nama_karyawan"));
			data.setTanggalSelesaiPermohonan(sdf.format(new Date(rs.getDate("end_date").getTime())));
			data.setKodeCabang(rs.getString("branch_code"));
			data.setNamaCabang(rs.getString("branch"));
			lstReport.add(data);
		}
		rs.close();
		ps.close();
		
		return lstReport;
	}
	
	private PdfPTable getTableContentUserExpiredReport(List<BcaCommonUserReport> data, int len) throws DocumentException{
		String methodName = "::getTableContentUserExpiredReport::";
		logger.debug(CLASS_NAME + methodName + " started");
		
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
			
			logger.debug(CLASS_NAME + methodName + " loop " + i++);
			
			BcaCommonUserReport rep = itData.next();
			
			PdfPCell cell1 = new PdfPCell(new Phrase(rep.getAccountId(), fontContent));
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBorder(borderStyle);
			
			content.addCell(cell1);
			
			PdfPCell cell2 = new PdfPCell(new Phrase(rep.getUserId(), fontContent));
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setBorder(borderStyle);
			
			content.addCell(cell2);
			
			PdfPCell cell3 = new PdfPCell(new Phrase(rep.getTanggalSelesaiPermohonan(), fontContent));
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setBorder(borderStyle);
			
			content.addCell(cell3);
			
			PdfPCell cell4 = new PdfPCell(new Phrase(rep.getKodeCabang(), fontContent));
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBorder(borderStyle);
			
			content.addCell(cell4);
			
			PdfPCell cell5 = new PdfPCell(new Phrase(rep.getNamaCabang(), fontContent));
			cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell5.setBorder(borderStyle);
			
			content.addCell(cell5);
		}
		
		if(i == 0){
			PdfPCell cell1 = new PdfPCell(new Phrase("Tidak Ada Data", fontContent));
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBorder(borderStyle);
			cell1.setColspan(len);
			
			content.addCell(cell1);
		}
		return content;
	}
	
	private void generateReport(SailPointContext ctx, long time, String fileLocation){
		String methodName = "::generateReport::";
		logger.debug(CLASS_NAME + methodName + " started");
		try {
			CustomReportGenerator customReportGenerator = new CustomReportGenerator();
			Document document = new Document();
			document.setPageSize(PageSize.A4.rotate());
			PdfWriter.getInstance(document, new FileOutputStream(fileLocation));
			document.open();
			document.add(customReportGenerator.getReportHeader("Laporan User Expired", "RA. 1B/6B/2T", "IM0002", "0996 - ETS Kantor Pusat", "Harian", sdf.format(new Date())));
			String headers[] = new String[]{"EMPLOYEE ID", "NAMA KARYAWAN", "END DATE", "BRANCH CODE", "BRANCH"};
			document.add(customReportGenerator.getTableHeader(headers)); //set tabel header
			document.add(getTableContentUserExpiredReport(generateUserExpiredReport(ctx, time), headers.length));
			document.close();
			logger.debug(CLASS_NAME + methodName + " finished...");
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
