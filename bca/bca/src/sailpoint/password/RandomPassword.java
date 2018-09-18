package sailpoint.password;

import java.util.Random;

public class RandomPassword {
	
	public static String password(int length) {
		String candidateChars = "abcdefghijkmnpqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
	    StringBuilder sb = new StringBuilder();
	    Random random = new Random();
	    for (int i = 0; i < length; i++) {
	        sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
	    }

	    return sb.toString();
	}

	/*public static void main(String[] args) {
		String resetPassword = "";
		resetPassword = password(8);
    	System.out.println(resetPassword);
	}*/

}
