package sailpoint.customtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class UpdateRegionCode extends AbstractTaskExecutor {
	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.UpdateRegionCode");

	public static String CLASS_NAME = "::UpdateRegionCode::";

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

		List regionCodeList = new ArrayList();

		// Query is intentionally left blank as we wish to search through all
		// identities and find out the entire set of region code....
		QueryOptions qo = new QueryOptions();

		// Getting an iterator to iterate through all identities present in
		// IIQ...
		Iterator iterator = context.search(Identity.class, qo);

		while (iterator.hasNext()) {
			Identity localIdentity = (Identity) iterator.next();
			if (localIdentity.getAttribute(IdentityAttribute.REGION_CODE) != null) {
				regionCodeList.add(localIdentity
						.getAttribute(IdentityAttribute.REGION_CODE));
			}

			// As we are iterating through huge number of records... it is
			// essential that we release memory often....
			context.decache();
		}

		logger.debug(METHOD_NAME + "regionCodeList.size(): "
				+ regionCodeList.size());

		// Getting the unique list from the list of all .....
		List uniqueRegionCode = CommonUtil.getUniqueValueList(regionCodeList);

		// Now going for creating or updating the custom object.....
		if (uniqueRegionCode.size() > 0) {
			boolean isRegionCodeUpdated = updateCustomObject(context,
					CustomObject.REGION_CODE_CUSTOM_OBJECT,
					IdentityAttribute.REGION_CODE, uniqueRegionCode);
			logger.debug(METHOD_NAME
					+ "Is Region Code Custom Object Successfully Updated: "
					+ isRegionCodeUpdated);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sailpoint.object.TaskExecutor#terminate()
	 */
	@Override
	public boolean terminate() {
		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");
		return false;
	}

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
	private boolean updateCustomObject(SailPointContext context,
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

			Iterator it = valueList.iterator();
			while (it.hasNext()) {
				String value = (String) it.next();
				existingValueList.add(value);
			}

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

}
