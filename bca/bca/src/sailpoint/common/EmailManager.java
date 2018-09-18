package sailpoint.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.object.EmailFileAttachment;
import sailpoint.object.EmailOptions;
import sailpoint.object.EmailTemplate;
import sailpoint.tools.GeneralException;

public class EmailManager{
	public static String CLASS_NAME = "::EmailManager::";
	public static Logger logger = Logger
			.getLogger("sailpoint.common.EmailManager");
	private EmailOptions emailOptions = null;
	private EmailTemplate emailTemplate = null;
	private EmailFileAttachment emailFileAttachment = null;
	private SailPointContext ctx = null;
	
	public EmailManager(String emailTemplateName) throws GeneralException{
		emailOptions = new EmailOptions();	
		emailTemplate = ctx.getObjectByName(EmailTemplate.class, emailTemplateName);
	}
	
	public void prepareEmailHeader(String emailSource, String emailDestination, String emailCC, String emailBCC)throws Exception{
		if("".equals(emailSource) || emailSource == null)
			throw new Exception("Email Source Is Empty");
		emailTemplate.setFrom(emailSource);
		if("".equals(emailDestination) || emailDestination == null)
			throw new Exception("Email From Is Empty");
		emailTemplate.setTo(emailDestination);
		if("".equals(emailCC) || emailCC == null)
			throw new Exception("Email Source Is Empty");
		emailTemplate.setCc(emailCC);
		if("".equals(emailBCC) || emailBCC == null)
			throw new Exception("Email Source Is Empty");
		emailTemplate.setBcc(emailBCC);
	}
	
	public void prepareAttachment(String fileLocation, EmailFileAttachment.MimeType fileType, byte[] fileData){
		emailFileAttachment = new EmailFileAttachment(fileLocation, fileType, fileData);
		List<EmailFileAttachment> lstAttachments = new ArrayList<EmailFileAttachment>();
		lstAttachments.add(emailFileAttachment);
		emailOptions.setAttachments(lstAttachments);
	}

	public void prepareAttachments(ArrayList<EmailFileAttachment> lstAttachments){
		emailOptions.setAttachments(lstAttachments);
	}
	
	public byte[] convertFileToByte(File file){
		byte[] byteFile = new byte[(int)file.length()];
		try{
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(byteFile);
			fileInputStream.close();
		}catch(Exception ex){
			logger.error("ERROR : " + ex.getMessage());
			ex.printStackTrace();
		}
		return byteFile;
	}
	
	public void sendEmail(){
		try {
			ctx.sendEmailNotification(emailTemplate, emailOptions);
		} catch (GeneralException ex) {
			ex.printStackTrace();
		}
	}
}
