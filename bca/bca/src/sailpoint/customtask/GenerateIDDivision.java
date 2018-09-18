package sailpoint.customtask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GenerateIDDivision extends AbstractTaskExecutor {
	public static String CLASS_NAME = "::GenerateIDDivision::";
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.GenerateIDDivision");

	/**
	 * @param context
	 * @param taskSchedule
	 * @param taskResult
	 * @param attributes
	 * @throws Exception
	 */
	public void execute(SailPointContext context, TaskSchedule taskSchedule,
			TaskResult taskResult, Attributes<String, Object> attributes)
			throws Exception {
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		if (attributes.get("fileInputPath") != null) {

			String filePath = (String) attributes.get("fileInputPath");
			logger.debug(CLASS_NAME + METHOD_NAME + "will be processed "
					+ filePath);

			Workbook book = Workbook.getWorkbook(new File(filePath));
			logger.debug(CLASS_NAME + "::excel will be processed::" + book);

			Sheet s = book.getSheet(0);

			final String customObjectName = "BCA_GenerateIDDivision";

			if (s != null) {

				Custom customObject = context.getObjectByName(Custom.class,
						customObjectName);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "::custom will be processed::" + customObject);

				Attributes localAttributes = null;

				if (customObject == null) {
					logger.debug(CLASS_NAME
							+ METHOD_NAME
							+ "Custom Object: "
							+ customObjectName
							+ " Not Found.. hence trying to create a custom object.......");

					// Trying to create custom object...
					customObject = new Custom();
					customObject.setName(customObjectName);
					localAttributes = new Attributes();

				} else {

					localAttributes = customObject.getAttributes();
				}

				Map map = new HashMap();
				map = localAttributes.getMap();
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "Custom Objects Current Map: " + map);

				for (int i = 1; i < s.getRows(); i++) {
					Cell a = s.getCell(0, i);
					Cell b = s.getCell(1, i);
					String entry = a.getContents();
					String value = b.getContents();
					logger.debug(CLASS_NAME + METHOD_NAME + "entry: " + entry);
					logger.debug(CLASS_NAME + METHOD_NAME + "Value:" + value);

					if (!map.containsKey(entry)) {

						map.put(entry, value);
					} else {
						map.remove(entry);
						map.put(entry, value);
					}

					localAttributes.setMap(map);
				}
				customObject.setAttributes(localAttributes);
				logger.debug(CLASS_NAME + METHOD_NAME + "Custom Object XML: "
						+ customObject.toXml());
				CommonUtil.updateCustomObject(context, customObject);

			} else {
				logger.debug(CLASS_NAME + METHOD_NAME
						+ "Cannot found file on sheet");
			}

		} else {
			logger.debug(CLASS_NAME + METHOD_NAME
					+ " Division argument's not found");
		}

	}

	public static void main(String args[]) throws Exception {
		String inputPathFile = "C://nasbca//idm//input//Generate_ID_Division.xls";
		Workbook book = Workbook.getWorkbook(new File(inputPathFile));

		Sheet s = book.getSheet(0);

		for (int i = 1; i < s.getRows(); i++) {
			Cell entry = s.getCell(0, i);
			Cell value = s.getCell(1, i);
			String spasi = "";
			System.out.print(spasi);

			System.out.print(entry.getContents() + " ");
			System.out.println(value.getContents());

		}
	}

	@Override
	public boolean terminate() {
		String METHOD_NAME = "::terminate::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		return false;
	}

}
