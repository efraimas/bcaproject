package sailpoint.rule;

@SuppressWarnings({ "unused" })
public class SapHrRule {
	
	private static String invalidString = "^.*(Bpk|bpk|Ibu|ibu|Sdr|sdr|Sdri|sdri|Hj|hj|\\.).*$";
	
	public static String getFirstName(String[] arrayOfName){
		String firstname = "";
		int len = arrayOfName.length;
		for(int i=0; i<len; i++){
			if(!arrayOfName[i].matches(invalidString)){
				return arrayOfName[i];
			}
		}
		
		return firstname != null ? firstname.trim() : "";
	}
	
	public static String getLastName(String[] arrayOfName){
		String lastname = "";
		int len = arrayOfName.length;

		if(len == 1)
			return lastname;
		for(int i=len-1; i>=0; i--){
			if(!arrayOfName[i].matches(invalidString)){
				return arrayOfName[i];
			}
		}
		
		return lastname != null ? lastname.trim() : "";
	}
	
	public static String getMiddleName(String name){
		
		String middlename = "";
		
		String[] arrayOfName = name.split(" "); 
		String firstname = getFirstName(arrayOfName);
		String lastname = getLastName(arrayOfName);
		
		if(lastname == null || "".equalsIgnoreCase(lastname))
			return middlename;
		
		int firstnameIdx = name.indexOf(firstname);
		int lastnameIdx = name.lastIndexOf(lastname);
		
		String tempMiddlename = name.substring(firstnameIdx+firstname.length()+1, lastnameIdx);
		
		String[] arrayofMiddlename = tempMiddlename.split(" ");
		
		int len = arrayofMiddlename.length;
		for(int i=0; i<len; i++){
			if(!arrayofMiddlename[i].matches(invalidString)){
				middlename = middlename + arrayofMiddlename[i] + " ";
			}
		}
		
		
		return middlename != null ? middlename.trim() : "";
	}
	
	private static boolean isValidNameCha(String name){
		if(name == null || name.lastIndexOf(".") >=1 || name.trim().length() <=1 )
			return false;
		
		return true;
			
	}
	
	private static String getValidName(String originalName){
		String fullname = "";
		String[] arrayOfName = originalName.split(" "); 
		int len = arrayOfName.length;
		
		for(int i=0; i<len; i++){
			if(!arrayOfName[i].matches(invalidString) && arrayOfName[i].trim().length() > 1){
				fullname = fullname + arrayOfName[i] + " ";
			}
		}
		
		return fullname;
	}
	
	public static String computeName(String name, String type){
		String fullname = getValidName(name);
		String[] arrayOfName = fullname.split(" "); 
		if("first".equalsIgnoreCase(type)){
			return getFirstName(arrayOfName);
		}else if("last".equalsIgnoreCase(type)){
			return getLastName(arrayOfName);
		}else if("middle".equalsIgnoreCase(type)){
			return getMiddleName(fullname);
		}
		
		return "";
	}
	
	/*public static void main(String args[]){
		String nama = "Bpk Ibu bpk Sdr Sdri Hj. Komaruddin Saleh  Menjangan Hutabarat Ssi. SH.";
		
		System.out.println("First name : " + computeName(nama, "first"));
		
		System.out.println("Middle name : " + computeName(nama, "middle"));
		
		System.out.println("Last name : " + computeName(nama, "last"));
	}*/

}
