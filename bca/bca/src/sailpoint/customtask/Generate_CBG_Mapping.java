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

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class Generate_CBG_Mapping extends AbstractTaskExecutor {
	public static String CLASS_NAME = "::Generate_CBG_Mapping::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.Generate_CBG_Mapping");

	public static String POSITION1 = "region";
	public static String POSITION2 = "branchCode";
	public static String POSITION3 = "subBranch";
	public static String POSITION4 = "initial";
	public static String POSITION5 = "officeType";
	public static String POSITION6 = "branchName";

	public static String ENTRY_KEY_VALUE = "regionCode";

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
			// List finalList = new ArrayList();
			List tempList = new ArrayList();
			line = bufferReader.readLine();
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

				if (tempSize > 6)
					myMap.put(POSITION6, temp[5]);

				logger.debug(CLASS_NAME + METHOD_NAME + "myMap: " + myMap);

				tempList.add(myMap);
				logger.debug(CLASS_NAME + METHOD_NAME + myMap);

			}

			boolean isUpdated = CommonUtil.updateCustomObject(context,
					CustomObject.BCA_CBG_CUSTOM_OBJECT, ENTRY_KEY_VALUE,
					tempList);

			logger.debug(CLASS_NAME + METHOD_NAME + "TempList: " + tempList);

			// Close the buffer reader
			bufferReader.close();

		} catch (FileNotFoundException e) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Error Caught: "
					+ e.getStackTrace());
			e.printStackTrace();

		} catch (IOException e) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Error Caught: "
					+ e.getStackTrace());
			e.printStackTrace();
		} catch (Exception e) {
			logger.debug(CLASS_NAME + METHOD_NAME + "Error Caught: "
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