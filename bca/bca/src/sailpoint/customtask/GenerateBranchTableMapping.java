package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class GenerateBranchTableMapping extends AbstractTaskExecutor{
	public static String CLASS_NAME = "::BCAUpdateBranchTableMapping::";
	public static Logger logger = Logger.getLogger("sailpoint.customtask.GenerateBranchTableMapping");
	public static String customObjectName = "BCA_Branch_Table_Mapping";
	public static String EntryKey = "branchTable";
	
	@Override
	public void execute(SailPointContext context, TaskSchedule taskSchedule, TaskResult taskResult, Attributes<String, Object> attributes)
			throws Exception {
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		
		String filePath = (String) attributes.get("filePath");
		logger.debug(CLASS_NAME + METHOD_NAME + "will be processed " + filePath);
	
		List tempList = new ArrayList();
		HashMap map = new HashMap();
		HashMap rootmap = new HashMap();
		Attributes attribute = new Attributes();
		Attributes rootattribute = new Attributes();
		map = (HashMap) attribute.getMap();
		rootmap = (HashMap) attribute.getMap();
		
		Custom custom = CommonUtil.getCustomObject(context, customObjectName);
		Attributes attrCustom = custom.getAttributes();
		List attrList = (List)attrCustom.get(EntryKey);
		
		if(attrList != null){
			logger.debug(CLASS_NAME + METHOD_NAME + " custom object branchTableMapping is not null");
			rootmap.put(EntryKey, tempList);
			rootattribute.setMap(rootmap);
			custom.setAttributes(rootattribute);
			context.startTransaction();
			context.saveObject(custom);
			context.commitTransaction();
			context.decache();
		}
	
		logger.debug(CLASS_NAME + METHOD_NAME + " Attribute in Map :" + map);
		
		if(filePath != null){
			
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				 String line;
				 String branchCode;
				 String branchName;
				 String initial;
				 String regionCode;
				 String subBranchCode;
				 
				    while ((line = br.readLine()) != null) {
				    	
				    	HashMap childMap = new HashMap();
				    	line = line.replace("\"", "");
				    	String[] temp = line.split(",");
						
				    	branchCode = temp[0].substring(temp[0].indexOf("=") + 1, temp[0].length());
				    	initial = temp[1].substring(temp[1].indexOf("=") + 1, temp[1].length());
				    	branchName = temp[2].substring(temp[2].indexOf("=") + 1, temp[2].length());
				    	regionCode = temp[3].substring(temp[3].indexOf("=") + 1, temp[3].length());
				    	subBranchCode = temp[4].substring(temp[4].indexOf("=") + 1, temp[4].length());
				    	
				    	logger.debug(CLASS_NAME + METHOD_NAME + " Data file : " + branchCode + " " + branchName + " " + initial + " " + regionCode + " " + subBranchCode);
		
				    	childMap.put("branchCode", branchCode);
				    	childMap.put("initial", initial);
				    	childMap.put("branchName", branchName);
				    	childMap.put("regionCode", regionCode);
				    	childMap.put("subBranchCode", subBranchCode);
							
						tempList.add(childMap);
				    }
				   
					rootmap.put(EntryKey, tempList);
					
					rootattribute.setMap(rootmap);
					custom.setAttributes(rootattribute);
					context.startTransaction();
					context.saveObject(custom);
					context.commitTransaction();
					context.decache();
					
					br.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			logger.debug(CLASS_NAME + METHOD_NAME + "File Path Tidak Ditemukan");
		}
	}

	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		return false;
	}
	
	/*public static void main(String[]args){
		String filePath = "C:\\nasbca\\cabang_table.csv";
		
		if(filePath != null){
			
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				 String line;
				 String branchCode;
				 String branchName;
				 String initial;
				 String regionCode;
				 String subBranchCode;
				    while ((line = br.readLine()) != null) {
				    	line = line.replace("\"", "");
				    	String[] temp = line.split(",");
						List tempList = new ArrayList();
						HashMap myMap = new HashMap();
						
						//System.out.println(temp[0] + temp[1] + temp[2] + temp[3] + temp[4]);
						
						temp[0].indexOf("=");
				    	branchCode = temp[0].substring(temp[0].indexOf("=") + 1, temp[0].length());  // Rubah Nanti sesuai Csv nya
				    	branchName = temp[2].substring(temp[2].indexOf("=") + 1, temp[2].length());
				    	initial = temp[1].substring(temp[1].indexOf("=") + 1, temp[1].length());
				    	regionCode = temp[3].substring(temp[3].indexOf("=") + 1, temp[3].length());
				    	subBranchCode = temp[4].substring(temp[4].indexOf("=") + 1, temp[4].length());
				    	//System.out.println(branchCode + "  " + branchName + "  "  + initial + "  " + regionCode + "  " + subBranchCode);
				    	
				    	myMap.put("BranchCode", branchCode);
				    	myMap.put("BranchName", branchName);
				    	myMap.put("Initial", initial);
				    	myMap.put("RegionCode", regionCode);
				    	myMap.put("SubBranchCode", subBranchCode);
				    	
				    	//System.out.println(myMap);
				    	
				    	tempList.add(myMap);
				    	
				    	//System.out.println(tempList);
				
				    }
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("File Path Tidak Ditemukan");
		}
	}*/

}
