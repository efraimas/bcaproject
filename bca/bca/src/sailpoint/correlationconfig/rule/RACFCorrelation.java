package sailpoint.correlationconfig.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.common.RACFAttribute;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.Link;
import sailpoint.object.ResourceObject;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class RACFCorrelation {
	public static Logger logger = Logger.getLogger("sailpoint.correlationconfig.rule.RACFCorrelation");

	@SuppressWarnings("unchecked")
//	public static Map racfCorrelationRule(SailPointContext context,
//			Map environment, Application application, ResourceObject account,
//			Link link) throws GeneralException {
//		String METHOD_NAME = "::RACFCorrelationRule::";
//		logger.debug(METHOD_NAME + "Inside...");
//
//		Map correlatedMap = new HashMap();
//		Attributes localAccountAttribute = account.getAttributes();
//
//		String employeeId = "";
//		String branchCode = "";
//	
//		if(localAccountAttribute.containsKey(RACFAttribute.DATA) && 
//				localAccountAttribute.containsKey(RACFAttribute.NAME)){
//			String data = localAccountAttribute.getString(RACFAttribute.DATA);
//			String name = localAccountAttribute.getString(RACFAttribute.NAME).toUpperCase();
//			logger.debug(METHOD_NAME + " attribute DATA ditemukan, dengan nilai ::" + data + "::");
//			logger.debug(METHOD_NAME + " attribute NAME ditemukan, dengan nilai ::" + name + "::");
//			
//			if(data == null || data.equalsIgnoreCase("") || name == null || name.equalsIgnoreCase("")) {
//				logger.debug(METHOD_NAME
//						+ "Data Field is either null or blank ... hence returning nothing....");
//				return correlatedMap;
//			}
//			else if(data!=null && data.length()>0 && name!=null && name.length()>0){
//				logger.debug(METHOD_NAME + "Data Value: " + data);
//				
//				StringBuffer sb = new StringBuffer(data);
//				
//				if(name.contains("CADANGAN")==false && data.contains("NIP:")==true){
//					
//					int beginIndex = sb.indexOf("NIP:");
//					int endIndex = sb.indexOf("LAHIR:");
//					
//					if(beginIndex>0 && endIndex>0){
//						employeeId = sb.substring(beginIndex+4, endIndex).trim();
//						
//						if(employeeId !=null && employeeId.length()>0){
//							if(employeeId.startsWith("7") || employeeId.startsWith("8") || 
//									employeeId.startsWith("9")){
//								employeeId = "19" + employeeId;
//							}
//							else if(employeeId.startsWith("6")){
//								employeeId = "80" + employeeId;
//							}
//							else if(employeeId.startsWith("00") || employeeId.startsWith("01") ||
//									employeeId.startsWith("02") || employeeId.startsWith("03") ||
//									employeeId.startsWith("04")){
//								employeeId = "20" + employeeId;
//							}
//							else if(employeeId.startsWith("05") || employeeId.startsWith("06") || 
//									employeeId.startsWith("07")){
//								employeeId = "00" + employeeId;
//							}
//							
//							logger.debug(METHOD_NAME + "New NIP: " + employeeId);
//							
//							if(CommonUtil.isNotEmptyString(employeeId)){
//								logger.debug(METHOD_NAME + "Returning Identity Map with employeeid: "+employeeId);
//								correlatedMap.put("identityAttributeName",IdentityAttribute.EMPLOYEE_ID);
//								correlatedMap.put("identityAttributeValue",employeeId);
//								return correlatedMap;
//							}
//							
//						}
//						else{
//							logger.debug(METHOD_NAME + "Result: NIP tidak ada yang cocok atau NULL "+employeeId);
//						}
//					}
//					else{
//						logger.debug(METHOD_NAME + "Attribute DATA tidak terdapat kata " + "NIP:" +beginIndex + "LAHIR:" +endIndex);
//					}
//				}
//				else if(localAccountAttribute.containsKey(RACFAttribute.NAME) && 
//						name.contains("CADANGAN")==true && data.contains("NIP:")==true){
//					int beginIndex = 0;
//					int endIndex = sb.indexOf("NIP:");
//					
//					branchCode = sb.substring(beginIndex, endIndex).trim();
//					logger.debug(METHOD_NAME + "New BranchCode: " + branchCode);
//					
//					if (CommonUtil.isNotEmptyString(branchCode)) {
//						logger.debug(METHOD_NAME + "Returning Identity Map with CADANGAN: "+branchCode);
//						correlatedMap.put("identityAttributeName",IdentityAttribute.NAME);
//						correlatedMap.put("identityAttributeValue","CADANGAN "+branchCode);
//						return correlatedMap;
//					}
//				}
//				else{
//					logger.debug(METHOD_NAME + "Returning Identity Orphan Account ");
//					return correlatedMap;
//				}
//			}
//		}
//		else{
//			logger.debug(METHOD_NAME + " attribute DATA atau NAME tidak ditemukan atau NULL");
//		}
//		
//		return correlatedMap;
//	}
	
	public static Map racfCorrelationRule(SailPointContext context, Map environment, Application application,
			ResourceObject account, Link link) throws GeneralException {
		String METHOD_NAME = "::RACFCorrelationRule::";
		logger.debug(METHOD_NAME + "Inside...");

		Map correlatedMap = new HashMap();
		Attributes localAccountAttribute = account.getAttributes();

		String employeeId = "";
		String branchCode = "";

		if (localAccountAttribute.containsKey(RACFAttribute.DATA)
				&& localAccountAttribute.containsKey(RACFAttribute.NAME)) {
			String data = localAccountAttribute.getString(RACFAttribute.DATA);
			String name = localAccountAttribute.getString(RACFAttribute.NAME).toUpperCase();
			logger.debug(METHOD_NAME + " attribute DATA ditemukan, dengan nilai ::" + data + "::");
			logger.debug(METHOD_NAME + " attribute NAME ditemukan, dengan nilai ::" + name + "::");

			if (data == null || data.equalsIgnoreCase("") || name == null || name.equalsIgnoreCase("")) {
				logger.debug(METHOD_NAME + "Data Field is either null or blank ... hence returning nothing....");
				return correlatedMap;
			} else if (data != null && data.length() > 0 && name != null && name.length() > 0) {
				logger.debug(METHOD_NAME + "Data Value: " + data);

				StringBuffer sb = new StringBuffer(data);

				if (name.contains("CADANGAN") == false && data.contains("NIP:") == true) {

					int beginIndex = sb.indexOf("NIP:");
					int endIndex = sb.indexOf("LAHIR:");

					if (beginIndex > 0 && endIndex > 0) {
						employeeId = sb.substring(beginIndex + 4, endIndex).trim();

						if (employeeId != null && employeeId.length() > 0) {
							if (employeeId.startsWith("7") || employeeId.startsWith("8")
									|| employeeId.startsWith("9")) {
								employeeId = "19" + employeeId;
							} else if (employeeId.startsWith("6")) {
								employeeId = "80" + employeeId;
							} else if (employeeId.startsWith("00") || employeeId.startsWith("01")
									|| employeeId.startsWith("02") || employeeId.startsWith("03")
									|| employeeId.startsWith("04")) {
								employeeId = "20" + employeeId;
							} else if (employeeId.startsWith("05") || employeeId.startsWith("06")
									|| employeeId.startsWith("07")) {
								employeeId = "00" + employeeId;
							}

							logger.debug(METHOD_NAME + "New NIP: " + employeeId);

							if (CommonUtil.isNotEmptyString(employeeId)) {
								logger.debug(METHOD_NAME + "Returning Identity Map with employeeid: " + employeeId);
								correlatedMap.put("identityAttributeName", IdentityAttribute.EMPLOYEE_ID);
								correlatedMap.put("identityAttributeValue", employeeId);
								return correlatedMap;
							}

						} else {
							logger.debug(METHOD_NAME + "Result: NIP tidak ada yang cocok atau NULL " + employeeId);
						}
					} else {
						logger.debug(METHOD_NAME + "Attribute DATA tidak terdapat kata " + "NIP:" + beginIndex
								+ "LAHIR:" + endIndex);
					}
				} else if (localAccountAttribute.containsKey(RACFAttribute.NAME) && name.contains("CADANGAN") == true
						&& data.contains("NIP:") == true) {
					int beginIndex = 0;
					int endIndex = sb.indexOf("NIP:");

					branchCode = sb.substring(beginIndex, endIndex).trim();
					logger.debug(METHOD_NAME + "New BranchCode: " + branchCode);

					if (CommonUtil.isNotEmptyString(branchCode)) {
						logger.debug(METHOD_NAME + "Returning Identity Map with CADANGAN: " + branchCode);
						correlatedMap.put("identityAttributeName", IdentityAttribute.NAME);
						correlatedMap.put("identityAttributeValue", "CADANGAN " + branchCode);
						return correlatedMap;
					}
				} else {
					logger.debug(METHOD_NAME + "Returning Identity Orphan Account ");
					return correlatedMap;
				}
			}
		} else {
			logger.debug(METHOD_NAME + " attribute DATA atau NAME tidak ditemukan atau NULL");
		}

		return correlatedMap;
	}

	public static void main(String[] args) {
		String employeeId = "";
		String branchCode = "";
		
		String name = "UNEKA WULAN ARMADANI";
		String data = "5855 NIP: 056772 LAHIR: 05-12-88 MASUK: 01-07-14  TELLER KCP AMPERA";
		StringBuffer sb = new StringBuffer(data);
		
		if(name.contains("CADANGAN")==false){
			
			int beginIndex = sb.indexOf("NIP:");
			int endIndex = sb.indexOf("LAHIR:");
						
			if(beginIndex>0 && endIndex>0){
				employeeId = sb.substring(beginIndex+4, endIndex).trim();
				
				if(employeeId !=null && employeeId.length()>0){
					if(employeeId.startsWith("7") || employeeId.startsWith("8") || 
							employeeId.startsWith("9")){
						employeeId = "19" + employeeId;
					}
					else if(employeeId.startsWith("6")){
						employeeId = "80" + employeeId;
					}
					else if(employeeId.startsWith("00") || employeeId.startsWith("01") ||
							employeeId.startsWith("02") || employeeId.startsWith("03") ||
							employeeId.startsWith("04")){
						employeeId = "20" + employeeId;
					}
					else if(employeeId.startsWith("05") || employeeId.startsWith("06") || 
							employeeId.startsWith("07")){
						employeeId = "00" + employeeId;
					}
					
					System.out.println("New NIP: " + employeeId);
					
				}
				else{
					System.out.println("Result: NIP tidak ada yang cocok atau NULL "+employeeId);
				}	
			}							
		}
		else if(name.contains("CADANGAN")==true){
			int beginIndex = 0;
			int endIndex = sb.indexOf("NIP:");
			
			branchCode = sb.substring(beginIndex, endIndex).trim();
			
			System.out.println("New NIP: " + "CADANGAN "+branchCode);
		}
		
	}

}
