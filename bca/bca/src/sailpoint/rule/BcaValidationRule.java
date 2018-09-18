package sailpoint.rule;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.ActiveDirectoryAttribute;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Identity;
import sailpoint.tools.GeneralException;

public class BcaValidationRule {
	
	static String className = "::BcaValidationRule::";
	
	public static Logger logger = Logger
			.getLogger("sailpoint.rule.BcaValidationRule");
	
	public static String validateEmailEksternal(SailPointContext ctx, Identity identity, String fullEmailAddress) throws GeneralException{
		
		String methodName = "::validateEmailEksternal::";
		
		String message = null;			
		
		if(fullEmailAddress == null || "".equalsIgnoreCase(fullEmailAddress)){
			logger.debug("alamat email kosong");
			return "Alamat email tidak boleh kosong, silahkan cek nama awal dan nama akhir anda";
		}else{
			String tempEmailAddress  = "";
			String regex = "[a-zA-Z_]+";
			
			logger.debug(className + methodName + "alamat email full : " + fullEmailAddress);
			String emailAddress = fullEmailAddress.substring(0, fullEmailAddress.lastIndexOf("@"));
			logger.debug(className + methodName + "alamat email address : " + emailAddress);
			
			tempEmailAddress = emailAddress + ActiveDirectoryAttribute.EMAIL_DOMAIN_EKSTERNAL;
			logger.debug(className + methodName + "Alamat email yang akan di cek : " + tempEmailAddress);
			
			if(emailAddress.equalsIgnoreCase(identity.getAttribute(IdentityAttribute.FIRST_NAME).toString())){
				message = "Alamat email " + tempEmailAddress + " tidak valid, silahkan tambahkan nama belakang atau marga anda";
			}	
			
			if(!emailAddress.matches(regex)){
				message = "Alamat email " + tempEmailAddress + " tidak valid, silahkan ubah alamat email anda sesuai kententuan";
			}
			
			if(!CommonUtil.isUniqueValue(ctx, IdentityAttribute.EMAIL, tempEmailAddress)){
				message = "Alamat email " + tempEmailAddress + " sudah digunakan, silahkan ubah nama belakang anda";
			}
		}
		
		logger.debug(className + methodName + "Pesan validasi : " + message);
		
		return message;
	}
	
	public static String validateEmailInternal(SailPointContext ctx, Identity identity, String fullEmailAddress) throws GeneralException{
		
		String methodName = "::validateEmailInternal::";
		
		String message = null;			
		
		if(fullEmailAddress == null || "".equalsIgnoreCase(fullEmailAddress)){
			logger.debug("alamat email kosong");
			return "Alamat email tidak boleh kosong, silahkan cek nama awal dan nama akhir anda";
		}else{
			String tempEmailAddress  = "";
			String regex = "[a-zA-Z_]+";
			
			logger.debug(className + methodName + "alamat email full : " + fullEmailAddress);
			String emailAddress = fullEmailAddress.substring(0, fullEmailAddress.lastIndexOf("@"));
			logger.debug(className + methodName + "alamat email address : " + emailAddress);
			
			tempEmailAddress = emailAddress + ActiveDirectoryAttribute.EMAIL_DOMAIN_INTERNAL;
			logger.debug(className + methodName + "Alamat email yang akan di cek : " + tempEmailAddress);
			
			if(emailAddress.equalsIgnoreCase(identity.getAttribute(IdentityAttribute.FIRST_NAME).toString())){
				message = "Alamat email " + tempEmailAddress + " tidak valid, silahkan tambahkan nama belakang atau marga anda";
			}	
			
			if(!emailAddress.matches(regex)){
				message = "Alamat email " + tempEmailAddress + " tidak valid, silahkan ubah alamat email anda sesuai kententuan";
			}
			
			if(!CommonUtil.isUniqueValue(ctx, IdentityAttribute.EMAIL, tempEmailAddress)){
				message = "Alamat email " + tempEmailAddress + " sudah digunakan, silahkan ubah nama belakang anda";
			}
		}
		
		logger.debug(className + methodName + "Pesan validasi : " + message);
		
		return message;
	}
	

	
	/*public static void main(String args[]) throws GeneralException{
		
		String fullEmailAddress = "";
		String regex = "[a-zA-Z_]+";
		
		if(!fullEmailAddress.matches(regex)){
			
			System.out.println("Error");
		}
		else{
			System.out.println(fullEmailAddress);
		}

	}*/

}
