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
public class Generate_SA_EMail_Table_Mapping extends AbstractTaskExecutor {
	public static String CLASS_NAME = "::Generate_SA_EMail_Table::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.Generate_SA_EMail_Table");

	public static String POSITION1 = "userID";
	public static String POSITION2 = "emailID";

	public static String ENTRY_KEY_VALUE = "SAEmailTable";

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

			try {

				FileReader inputFile = new FileReader(fileName);

				// Instantiate the BufferedReader Class
				BufferedReader bufferReader = new BufferedReader(inputFile);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "File Read Successfully....");

				// Variable to hold the one line data
				String line;

				List tempList = new ArrayList();
				HashMap myMap = new HashMap();

				// Read file line by line and print on the console
				while ((line = bufferReader.readLine()) != null) {
					if (line.equalsIgnoreCase(""))
						break;

					logger.debug(CLASS_NAME + METHOD_NAME + "line: " + line);
					String[] temp = line.split(",");

					int tempSize = temp.length;
					logger.debug(CLASS_NAME + METHOD_NAME + "temp size: "
							+ tempSize);

					if (tempSize > 1)
						myMap.put(temp[0], temp[1]);
					else
						logger.debug(CLASS_NAME + METHOD_NAME + "Either "
								+ POSITION1 + " or " + POSITION2
								+ " is not present....");

				}
				tempList.add(myMap);
				boolean isUpdated = CommonUtil.updateCustomObject(context,
						CustomObject.BCA_SA_EMAIL_TABLE_CUSTOM_OBJECT,
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
