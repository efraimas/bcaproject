package sailpoint.customtask;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Capability;
import sailpoint.object.Identity;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class GenerateWorkGroupSecurityAdministrator extends AbstractTaskExecutor{

	public static String CLASS_NAME = "::GenerateWorkGroupSecurityAdministrator::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.GenerateWorkGroupSecurityAdministrator");
	
	@Override
	public void execute(SailPointContext context, TaskSchedule arg1, TaskResult arg2, Attributes<String, Object> arg3) throws Exception {
		
		if(arg3.get("fileInputPath") != null){
			
			String filePath = (String)arg3.get("fileInputPath");
			
			String input = (String)arg3.get("branchCode");
			
			logger.debug(CLASS_NAME + " branchcode that will be processed " + input);
			
			Workbook book = Workbook.getWorkbook(new File(filePath));
			
			Sheet s = book.getSheet(0);
			
			int rows = s.getRows();
			
			
			if("all".equalsIgnoreCase(input)){
				
				logger.debug(CLASS_NAME + " will process all branch");
				
				for(int i=1; i<rows; i++){
					
					String branchCode = s.getCell(0, i).getContents();
					
					String nipSA1 = CommonUtil.getEmployeeIdRacf(s.getCell(2, i).getContents());
					String groupMail = s.getCell(3, i).getContents();
		
					Capability capability = null;
					
					if(CommonUtil.isNotEmptyString(nipSA1)){
						executeWorkGroupByBranch(context, branchCode, CommonUtil.searchIteratorIdentity(context, IdentityAttribute.EMPLOYEE_ID, nipSA1), groupMail, capability);
					}
					
					String nipSA2 = ""; // CommonUtil.getEmployeeIdRacf(s.getCell(4, i).getContents());
					
					if(CommonUtil.isNotEmptyString(nipSA2)){
						executeWorkGroupByBranch(context, branchCode, CommonUtil.searchIteratorIdentity(context, IdentityAttribute.EMPLOYEE_ID, nipSA2), groupMail, capability);
					}
					
					logger.debug(CLASS_NAME + " process branch" + branchCode + " SA1 : " + nipSA1 + ", SA2 : " + nipSA2);
				}
				
			}else{
				String[] arrBranchCode = input.split(",");
				int len = arrBranchCode.length;
				String branchCode = "";
				
				for(int i=0; i<len; i++){
					branchCode = arrBranchCode[i].trim();
					logger.debug(CLASS_NAME + " Branch Code : " + branchCode);
					
					for(int j=1; j<rows; j++){
						
						if(branchCode.equalsIgnoreCase(s.getCell(0, j).getContents())){
							
							String nipSA1 = CommonUtil.getEmployeeIdRacf(s.getCell(2, j).getContents());
							String groupMail = s.getCell(3, j).getContents();
							Capability capability = null;
							
							if(CommonUtil.isNotEmptyString(nipSA1)){
								
								executeWorkGroupByBranch(context, branchCode, CommonUtil.searchIteratorIdentity(context, IdentityAttribute.EMPLOYEE_ID, nipSA1), groupMail, capability);
							}
							
							String nipSA2 = "";  // CommonUtil.getEmployeeIdRacf(s.getCell(4, j).getContents());
							
							if(CommonUtil.isNotEmptyString(nipSA2)){
								executeWorkGroupByBranch(context, branchCode, CommonUtil.searchIteratorIdentity(context, IdentityAttribute.EMPLOYEE_ID, nipSA2), groupMail, capability);
							}
							logger.debug(CLASS_NAME + " process branch" + branchCode + " SA1 : " + nipSA1 + ", SA2 : " + nipSA2);
							
						}
					}
					
				//	executeWorkGroupByBranch(context, branchCode, getListSA(context, nipSA1, nipSA2));
				}
			}
			
			
		}else{
			logger.debug(CLASS_NAME + " branchCode argument's not found");
		}
		
	}
	
	private void executeWorkGroupByBranch(SailPointContext context, String branchCode, Iterator it, String groupMail, Capability capability) throws GeneralException{
		String workGroupName = "";
		String workGroupDescription = "";
		
		if(CommonUtil.isHQBranchCode(branchCode)){
			logger.debug(CLASS_NAME + " branch type KP hence not processed");
		}else if(CommonUtil.isKFCCBranchCode(branchCode)){
			logger.debug(CLASS_NAME + " branch type KFCC hence not processed");
		}else if(CommonUtil.isSOABranchCode(branchCode)){
			logger.debug(CLASS_NAME + " branch type SOA hence not processed");
		}else if(CommonUtil.isRegionalBranchCode(context, branchCode)){
			logger.debug(CLASS_NAME + " branch type region");
			workGroupName = "KANWIL " + branchCode + " Security Administrators";
			workGroupDescription = "Group Security Administrator Kanwil dengan kode " +  branchCode;
		}else if(CommonUtil.isMainBranchCode(context, branchCode)){
			logger.debug(CLASS_NAME + " branch type KCU");
			workGroupName = "KCU " + branchCode + " Security Administrators";
			workGroupDescription = "Group Security Administrator KCU dengan kode cabang " +  branchCode;
		}else if(CommonUtil.isSubBranchCode(context, branchCode)){
			logger.debug(CLASS_NAME + " branch type KCP");
			workGroupName = "KCP " + branchCode + " Security Administrators";
			workGroupDescription = "Group Security Administrator KCP dengan kode cabang " +  branchCode;
		}
		
		if(it != null && CommonUtil.isNotEmptyString(workGroupName)){
			logger.debug(CLASS_NAME + "preparation to create or update workgroup " + workGroupName);
			WorkGroupCreator.createUpdateWorkGroup(it, context, workGroupName, workGroupDescription, groupMail, capability);
		}

	}
	

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		return false;
	}
	
	private static boolean isEmptyIterator(Iterator<?> it){
		return it == null || !it.hasNext();
	}
	
	private static List<Identity> getListSA(SailPointContext context, String nip1, String nip2) throws GeneralException{
		List lst = null;
		
		Identity sa1 = CommonUtil.searchIdentity(context, IdentityAttribute.EMPLOYEE_ID, nip1);
		
		if(sa1 != null){
			lst = new ArrayList<Identity>();
			lst.add(sa1);
		}
		
		Identity sa2 = CommonUtil.searchIdentity(context, IdentityAttribute.EMPLOYEE_ID, nip2);	
		
		if(sa2 != null){
			if(lst == null)
				lst = new ArrayList<Identity>();			
			lst.add(sa2);			
		}
		
		return lst;
	}
//	public static void main(String args[]){
//		
//		String filePath = "C:\\Users\\aspadmin\\Desktop\\mastersa.xls";
//		
//		Workbook book;
//		
//		try {
//			book = Workbook.getWorkbook(new File(filePath));
//			Sheet s = book.getSheet(0);
//			
//			int rows = s.getRows();
//			
//			for(int i=1; i<rows; i++){
//				/*System.out.println("Branch Code : " + s.getCell(0, i).getContents() + ", nip SA1 : " + s.getCell(2, i).getContents() + ", nip SA2 : " + s.getCell(4, i).getContents() 
//						+ "Alamat Email : " + s.getCell(3, i).getContents());*/
//				System.out.println("Alamat Email : " + s.getCell(3, i).getContents());
//			}
//			
//		} catch (BiffException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//	}
}
