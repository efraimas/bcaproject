package sailpoint.password;

import sailpoint.api.SailPointContext;
import sailpoint.tools.GeneralException;

public class DecryptPassword {

	public static Object decryptPassword(SailPointContext context) throws GeneralException {
		
		String password = "1:343NJoAeVMhQhdDnZZsgFA==";
		
		password = context.decrypt(password);
		
		return password;
	}
	
	
	
	
	public static void main(String[] args) throws GeneralException {
		
		//System.out.println(decryptPassword());

	}

}
