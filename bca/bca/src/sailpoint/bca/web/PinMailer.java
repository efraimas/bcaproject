package sailpoint.bca.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointFactory;
import sailpoint.common.BcaCalendar;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.external.token.BcaTokenSync;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;
import sailpoint.web.lcm.AccountsRequestBean;

@SuppressWarnings({ "unused", "rawtypes", "static-access" })
public class PinMailer extends AccountsRequestBean{
	
	String className = "::PinMailer::";
	
	static Logger logger = Logger.getLogger("sailpoint.bca.web.PinMailer");
	
	String tokenResponse;
	
	String tanggal;
	
	String bulan;
	
	String tahun;
	
	List lstTanggal;
	
	List lstBulan;
	
	List lstTahun;
	
	String calendar;
	
	String message;
		
	
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
	 * @return the lstBulan
	 */
	public List getLstBulan() {
		return lstBulan;
	}

	/**
	 * @param lstBulan the lstBulan to set
	 */
	public void setLstBulan(List lstBulan) {
		this.lstBulan = lstBulan;
	}

	/**
	 * @return the lstTahun
	 */
	public List getLstTahun() {
		return lstTahun;
	}

	/**
	 * @param lstTahun the lstTahun to set
	 */
	public void setLstTahun(List lstTahun) {
		this.lstTahun = lstTahun;
	}

	/**
	 * @return the calendar
	 */
	public String getCalendar() {
		return calendar;
	}

	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	/**
	 * @return the lstTanggal
	 */
	public List getLstTanggal() {
		return lstTanggal;
	}

	/**
	 * @param lstTanggal the lstTanggal to set
	 */
	public void setLstTanggal(List lstTanggal) {
		this.lstTanggal = lstTanggal;
	}

	/**
	 * @return the tokenResponse
	 */
	public String getTokenResponse() {
		return tokenResponse;
	}

	/**
	 * @param tokenResponse the tokenResponse to set
	 */
	public void setTokenResponse(String tokenResponse) {
		this.tokenResponse = tokenResponse;
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

	public PinMailer() {
		super();
		
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
		
	}
	
	public String submitRequest(){
		
		String methodName = "::submitRequest::";
		
		Identity identity = null;
		
		try {
			identity = getIdentity();			
			logger.debug(className + methodName + "get identity with username " + identity.getDisplayName());
			
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String tokenId = (String)identity.getAttribute(IdentityAttribute.TOKEN_ID);
		
		logger.debug(className + methodName + "get token id " + tokenId);
		
		if(getTokenResponse() == null || "".equalsIgnoreCase(getTokenResponse())){
			setMessage("Silahkan isi respon key BCA anda (appl 1)");
			return "";
		}
		
		logger.debug(className + methodName + " token response : " + getTokenResponse());
		
		BcaTokenSync tokenSync = new BcaTokenSync();
		
		if(!tokenSync.isAuthorized(tokenId, getTokenResponse())){
			setMessage("Response token yang anda masukkan salah");
			logger.debug(className + methodName + " authentication ke token server gagal");
			return "";
		}
		
		BcaCalendar cal = new BcaCalendar();
		
		String hariIni = cal.getTanggal() + "-" + cal.getBulan() + "-" + cal.getTahun();
		
		String filePath = "";
		
		logger.debug(className + methodName + " preparation to get system config");
		
		try {
			filePath = CommonUtil.getBcaSystemConfig(SailPointFactory.getCurrentContext(), "pin mailer prefix") + hariIni + ".txt";
			
			logger.debug(className + methodName + " file path " + filePath);
			
		} catch (GeneralException e) {
			
			e.printStackTrace();
		}
		
		setMessage(filePath);
		
		return "";
	}
	
	public void downloadFile() throws IOException, GeneralException {

		String methodName = "::downloadFile::";
		
		Identity identity = null;
		
		try {
			
			identity = getIdentity();
			
			logger.debug(className + methodName + "get identity with username " + identity.getDisplayName());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String tokenId = (String)identity.getAttribute(IdentityAttribute.TOKEN_ID);
		
		logger.debug(className + methodName + "get token id " + tokenId);
		
		if(getTokenResponse() == null || "".equalsIgnoreCase(getTokenResponse())){
			setMessage("Silahkan isi respon key BCA anda (appl 1)");
			return;
		}
		
		logger.debug(className + methodName + " token response : " + getTokenResponse());
		
		BcaTokenSync tokenSync = new BcaTokenSync();
		
		if(!tokenSync.isAuthorized(tokenId, getTokenResponse())){
			setMessage("Response token yang anda masukkan salah");
			logger.debug(className + methodName + " authentication ke token server gagal");
			return;
		}
		
		BcaCalendar cal = new BcaCalendar();
		
		/*String hariIni = cal.getTanggal() + "-" + cal.getBulan() + "-" + cal.getTahun();*/
		
		String selectedTanggal = getTanggal() + "-" + getBulan() + "-" + getTahun();
		
		logger.debug(className + methodName + getTanggal() + " " + getBulan() + " " + getTahun());
		
		String filePath = "";
		
		String fileName = "BCA_pinmailer_pembuatan_";
		
		logger.debug(className + methodName + " preparation to get system config");
		
		try {
			
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
			
			HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			
			filePath = CommonUtil.getBcaSystemConfig(SailPointFactory.getCurrentContext(), "pin mailer prefix") + selectedTanggal + ".txt";
			
			fileName = fileName + selectedTanggal + ".txt";
			
			logger.debug(className + methodName + " file path " + filePath);
			
			/*File file = new File(filePath, URLDecoder.decode(requestedFile, "UTF-8"));*/
			File file = new File(filePath);
			
			 // Check if file actually exists in filesystem.
	        if (!file.exists()) {
	            // Do your thing if the file appears to be non-existing.
	            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
	           // response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
	            setMessage("File tanggal " + selectedTanggal +" tidak ditemukan");
	            return;
	        }

	        
	        // Get content type by filename.
	        String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(file.getName());

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
	        response.setHeader("Content-Length", String.valueOf(file.length()));
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

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
	        }
	    }finally{
	    	
	    }
		
		return;
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

}
