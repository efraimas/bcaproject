package sailpoint.customtask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

/**
 * @author joydeep.mondal
 *
 */
@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class GenerateRegionCodeXML extends AbstractTaskExecutor {

	public static String CLASS_NAME = "::GenerateRegionCodeXML::";

	public static String POSITION1 = "region";
	public static String POSITION2 = "branchCode";
	public static String POSITION3 = "subBranch";
	public static String POSITION4 = "initial";
	public static String POSITION5 = "officeType";
	public static String POSITION6 = "branchName";

	public static Logger logger = Logger
			.getLogger("sailpoint.common.GenerateRegionCodeXML");

	/**
	 * Method will search for all identities with specific result option and
	 * then will update the custom object with updated data...
	 * 
	 * @param context
	 * @param customObjectName
	 * @param identityAttributeName
	 * @param valueList
	 * @return
	 * @throws GeneralException
	 */

	public static boolean updateCustomObject1(SailPointContext context,
			String customObjectName, String identityAttributeName,
			List valueList) throws GeneralException {
		String METHOD_NAME = "::updateCustomObject::";
		boolean isUpdated = false;

		Custom customObject = CommonUtil.getCustomObject(context,
				customObjectName);
		if (customObject == null) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "Custom Object: "
					+ customObjectName
					+ " Not Found.. hence trying to create a custom object.......");

			// Trying to create custom object...

			customObject = new Custom();
			customObject.setName(customObjectName);
			// Setting the custom object...
			customObject.put(identityAttributeName, valueList);

		} else {

			Attributes attributes = customObject.getAttributes();
			Map attributeMap = attributes.getMap();
			List existingValueList = (List) attributeMap
					.get(identityAttributeName);

			// logger.error(CLASS_NAME + METHOD_NAME + "existingValueList: "
			// + existingValueList);
			// logger.error(CLASS_NAME + METHOD_NAME + "valueList: " +
			// valueList);
			Iterator it = valueList.iterator();
			while (it.hasNext()) {
				String value = (String) it.next();
				existingValueList.add(value);
			}
			// logger.error(CLASS_NAME + METHOD_NAME + "existingValueList: "
			// + existingValueList);

			// existingValueList.add(valueList);
			Collections.copy(existingValueList, valueList);
			List uniqueValueToUpdate = CommonUtil
					.getUniqueValueList(existingValueList);
			logger.debug(METHOD_NAME + "uniqueValueToUpdate: "
					+ uniqueValueToUpdate);
			// Setting the custom object...
			customObject.put(identityAttributeName, uniqueValueToUpdate);

		}

		// Updating custom object into IIQ....
		context.startTransaction();
		context.saveObject(customObject);
		context.commitTransaction();
		context.decache();

		logger.debug(CLASS_NAME + METHOD_NAME
				+ " Custom Object update successfully... : " + customObjectName);
		isUpdated = true;

		return isUpdated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sailpoint.object.TaskExecutor#execute(sailpoint.api.SailPointContext,
	 * sailpoint.object.TaskSchedule, sailpoint.object.TaskResult,
	 * sailpoint.object.Attributes)
	 */
	@Override
	public void execute(SailPointContext arg0, TaskSchedule arg1,
			TaskResult arg2, Attributes<String, Object> arg3) throws Exception {
		String METHOD_NAME = "::execute::";

		// Name of the file
		String fileName = "/Users/joydeep.mondal/Documents/CBS.txt";
		String cbsXML = "/Users/joydeep.mondal/Documents/CBS.xml";
		logger.debug(METHOD_NAME + "Inside...");
		// Create object of FileReader
		try {
			FileReader inputFile = new FileReader(fileName);

			// Instantiate the BufferedReader Class
			BufferedReader bufferReader = new BufferedReader(inputFile);

			// Variable to hold the one line data
			String line;
			List finalList = new ArrayList();

			// Read file line by line and print on the console
			while ((line = bufferReader.readLine()) != null) {

				System.out.println(line);
				String[] temp = line.split(",");
				List tempList = new ArrayList();
				HashMap myMap = new HashMap();
				// myMap.put(POSITION1, temp[1]);
				myMap.put(POSITION2, temp[2]);
				myMap.put(POSITION3, temp[3]);
				myMap.put(POSITION4, temp[4]);
				myMap.put(POSITION5, temp[5]);
				myMap.put(POSITION6, temp[6]);

				tempList.add(myMap);

				// boolean isUpdated = updateCustomObject(context,
				// customObjectName, identityAttributeName, valueList);

			}
			// Close the buffer reader
			bufferReader.close();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {
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
		logger.debug(METHOD_NAME + "Inside...");

		return false;
	}

}
