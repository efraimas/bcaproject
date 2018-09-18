package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.object.Attributes;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

/**
 * @author joydeep.mondal
 * 
 * 
 *         Input file have following list of columns
 * 
 *         1. branch code (same 4 digits as in KCU-SA table file), note that the
 *         region in the first lines also have a branchcode (for the staff
 *         working at region level)
 * 
 * 
 *         2. branch 3-letter code (the regions, at the top, have no such
 *         3-letter codes)
 * 
 *         3. Name (Branch name or Region Name accordingly)
 * 
 *         4.Main Branch (or Parent Main Branch), this is the important
 *         information of this file: this is the only place where we have the
 *         hierarchy of Main Branches and sub branches. For the Branches that
 *         are Main Branch (and for Regions), this data is equal to the column
 *         1.
 * 
 *         5. Region Code (as part of the hierarchy, each Branch depends from 1
 *         Region)
 * 
 *
 */
@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class Generate_BranchTable_Mapping extends AbstractTaskExecutor {
	public static String CLASS_NAME = "::Generate_BranchTable_Mapping::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.Generate_BranchTable_Mapping");

	public static String POSITION1 = "branchCode";
	public static String POSITION2 = "branch3LetterCode";
	public static String POSITION3 = "branchName";
	public static String POSITION4 = "mainBranch";
	public static String POSITION5 = "regionCode";

	public static String ENTRY_KEY_VALUE = "branchTable";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sailpoint.object.TaskExecutor#execute(sailpoint.api.SailPointContext,
	 * sailpoint.object.TaskSchedule, sailpoint.object.TaskResult,
	 * sailpoint.object.Attributes)
	 */
	@Override
	public void execute(SailPointContext context, TaskSchedule taskSchedule,
			TaskResult taskResult, Attributes<String, Object> attributes)
			throws Exception {
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		// Name of the file
		String fileName = "";
		List inputAttributeListKey = attributes.getKeys();

		Iterator it = inputAttributeListKey.iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			String keyValue = attributes.getString(keyName);

			if (keyName.equalsIgnoreCase("inputFileLocation")) {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "File Location Found....");
				fileName = keyValue;
				logger.debug(CLASS_NAME + METHOD_NAME + "File Location: "
						+ fileName);
			}

		}

		// Create object of FileReader
		try {
			FileReader inputFile = new FileReader(fileName);

			// Instantiate the BufferedReader Class
			BufferedReader bufferReader = new BufferedReader(inputFile);
			logger.debug(CLASS_NAME + METHOD_NAME
					+ "File Read Successfully....");

			// Variable to hold the one line data
			String line;

			List tempList = new ArrayList();

			// Read file line by line and print on the console
			while ((line = bufferReader.readLine()) != null) {

				logger.debug(CLASS_NAME + METHOD_NAME + "line: " + line);
				String[] temp = line.split(",");
				logger.debug(CLASS_NAME + METHOD_NAME + "temp size: "
						+ temp.length);
				int tempSize = temp.length;
				HashMap myMap = new HashMap();

				if (tempSize > 1)
					myMap.put(POSITION1, temp[0]);

				if (tempSize > 2)
					myMap.put(POSITION2, temp[1]);

				if (tempSize > 3)
					myMap.put(POSITION3, temp[2]);

				if (tempSize > 4)
					myMap.put(POSITION4, temp[3]);

				if (tempSize > 5)
					myMap.put(POSITION5, temp[4]);

				logger.debug(CLASS_NAME + METHOD_NAME + "myMap: " + myMap);

				tempList.add(myMap);
				logger.debug(CLASS_NAME + METHOD_NAME + myMap);

			}

			boolean isUpdated = CommonUtil.updateCustomObject(context,
					CustomObject.BCA_BRANCH_TABLE_CUSTOM_OBJECT,
					ENTRY_KEY_VALUE, tempList);

			logger.debug(CLASS_NAME + METHOD_NAME + "TempList: " + tempList);

			// Close the buffer reader
			bufferReader.close();

		} catch (FileNotFoundException e) {
			logger.error(CLASS_NAME + METHOD_NAME + "Error Caught: "
					+ e.getStackTrace());
			e.printStackTrace();

		} catch (IOException e) {
			logger.error(CLASS_NAME + METHOD_NAME + "Error Caught: "
					+ e.getStackTrace());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(CLASS_NAME + METHOD_NAME + "Error Caught: "
					+ e.getStackTrace());
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sailpoint.object.TaskExecutor#terminate()
	 */
	@Override
	public boolean terminate() {

		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		return false;
	}
}
