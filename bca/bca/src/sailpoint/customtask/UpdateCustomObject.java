package sailpoint.customtask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.common.CommonUtil;
import sailpoint.common.CustomObject;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.AbstractTaskExecutor;

/**
 * @author joydeep.mondal
 *
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class UpdateCustomObject extends AbstractTaskExecutor {

	public static Logger logger = Logger
			.getLogger("sailpoint.customtask.UpdateCustomObject");

	public static String CLASS_NAME = "::UpdateCustomObject::";

	/**
	 * (non-Javadoc)
	 * 
	 * @see sailpoint.object.TaskExecutor#execute(sailpoint.api.SailPointContext,
	 *      sailpoint.object.TaskSchedule, sailpoint.object.TaskResult,
	 *      sailpoint.object.Attributes)
	 */
	@Override
	public void execute(SailPointContext context, TaskSchedule taskSchedule,
			TaskResult taskResult, Attributes<String, Object> attributes)
			throws Exception {

		String METHOD_NAME = "::execute::";
		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		// Variable declaration...
		boolean shouldUpdateRegionCode = false;
		boolean shouldUpdateDivisionCode = false;
		boolean shouldUpdateBranchCode = false;
		boolean shouldUpdateJobCode = false;
		boolean shouldUpdatePositionCode = false;
		boolean shouldUpdateCostCenter = false;
		boolean shouldUpdateEchelon = false;

		List regionCode = new ArrayList();
		List divisionCode = new ArrayList();
		List branchCode = new ArrayList();
		List jobCode = new ArrayList();
		List positionCode = new ArrayList();
		List costCenter = new ArrayList();
		List echelon = new ArrayList();

		List inputAttributeListKey = attributes.getKeys();
		QueryOptions qo = new QueryOptions();
		// Filter f = new Filter();
		List props = new ArrayList();

		// TODO Remove this section later...
		Iterator it = inputAttributeListKey.iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			String keyValue = attributes.getString(keyName);
			logger.debug(CLASS_NAME + METHOD_NAME + "Key Name: " + keyName);
			logger.debug(CLASS_NAME + METHOD_NAME + "Key Value: " + keyValue);

			if (keyName
					.equalsIgnoreCase(CustomObject.REGION_CODE_CUSTOM_OBJECT)) {
				shouldUpdateRegionCode = true;
				props.add(IdentityAttribute.REGION_CODE);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ IdentityAttribute.REGION_CODE
						+ " added as a return attribute...");

			} else if (keyName
					.equalsIgnoreCase(CustomObject.DIVISION_CODE_CUSTOM_OBJECT)) {
				shouldUpdateDivisionCode = true;
				props.add(IdentityAttribute.DIVISION_CODE);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ IdentityAttribute.DIVISION_CODE
						+ " added as a return attribute...");

			} else if (keyName
					.equalsIgnoreCase(CustomObject.BRANCH_CODE_CUSTOM_OBJECT)) {
				shouldUpdateBranchCode = true;
				props.add(IdentityAttribute.BRANCH_CODE);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ IdentityAttribute.BRANCH_CODE
						+ " added as a return attribute...");

			} else if (keyName
					.equalsIgnoreCase(CustomObject.JOB_CODE_CUSTOM_OBJECT)) {
				shouldUpdateJobCode = true;
				props.add(IdentityAttribute.JOB_CODE);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ IdentityAttribute.JOB_CODE
						+ " added as a return attribute...");

			} else if (keyName
					.equalsIgnoreCase(CustomObject.POSITION_CODE_CUSTOM_OBJECT)) {
				shouldUpdatePositionCode = true;
				props.add(IdentityAttribute.POSITION_CODE);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ IdentityAttribute.POSITION_CODE
						+ " added as a return attribute...");

			} else if (keyName
					.equalsIgnoreCase(CustomObject.COST_CENTER_CUSTOM_OBJECT)) {
				shouldUpdateCostCenter = true;
				props.add(IdentityAttribute.COST_CENTER);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ IdentityAttribute.COST_CENTER
						+ " added as a return attribute...");

			} else if (keyName
					.equalsIgnoreCase(CustomObject.ECHELON_CUSTOM_OBJECT)) {
				shouldUpdateEchelon = true;
				props.add(IdentityAttribute.ECHELON);
				logger.debug(CLASS_NAME + METHOD_NAME
						+ IdentityAttribute.ECHELON
						+ " added as a return attribute...");

			}
			context.decache();
		}// end of while...

		logger.debug(CLASS_NAME + METHOD_NAME + "Properties: " + props);

		if (props.size() == 0) {
			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ " Not a single option selected... hence doing nothing....");
			return;
		}

		// Now generate query....

		Iterator iterator = context.search(Identity.class, qo);
		while (iterator.hasNext()) {
			Identity localIdentity = (Identity) iterator.next();

			// TODO Remove later...
			logger.debug(METHOD_NAME + "Evaluating Identity: "
					+ localIdentity.getDisplayName());

			if (shouldUpdateRegionCode) {

				if (localIdentity.getAttribute(IdentityAttribute.REGION_CODE) != null) {
					regionCode.add(localIdentity
							.getAttribute(IdentityAttribute.REGION_CODE));
				}
			}
			if (shouldUpdateDivisionCode) {

				if (localIdentity.getAttribute(IdentityAttribute.DIVISION_CODE) != null) {
					divisionCode.add(localIdentity
							.getAttribute(IdentityAttribute.DIVISION_CODE));
				}
			}
			if (shouldUpdateBranchCode) {

				if (localIdentity.getAttribute(IdentityAttribute.BRANCH_CODE) != null) {
					branchCode.add(localIdentity
							.getAttribute(IdentityAttribute.BRANCH_CODE));
				}
			}
			if (shouldUpdateJobCode) {

				if (localIdentity.getAttribute(IdentityAttribute.JOB_CODE) != null) {
					jobCode.add(localIdentity
							.getAttribute(IdentityAttribute.JOB_CODE));
				}
			}
			if (shouldUpdatePositionCode) {

				if (localIdentity.getAttribute(IdentityAttribute.POSITION_CODE) != null) {
					positionCode.add(localIdentity
							.getAttribute(IdentityAttribute.POSITION_CODE));
				}
			}
			if (shouldUpdateCostCenter) {

				if (localIdentity.getAttribute(IdentityAttribute.COST_CENTER) != null) {
					costCenter.add(localIdentity
							.getAttribute(IdentityAttribute.COST_CENTER));
				}
			}
			if (shouldUpdateEchelon) {

				if (localIdentity.getAttribute(IdentityAttribute.ECHELON) != null) {
					echelon.add(localIdentity
							.getAttribute(IdentityAttribute.ECHELON));
				}
			}
			context.decache();
		}

		// Getting the unique list from the list of all .....
		List uniqueRegionCode = CommonUtil.getUniqueValueList(regionCode);
		List uniqueDivisionCode = CommonUtil.getUniqueValueList(divisionCode);
		List uniqueBranchCode = CommonUtil.getUniqueValueList(branchCode);
		List uniqueJobCode = CommonUtil.getUniqueValueList(jobCode);
		List uniquePositionCode = CommonUtil.getUniqueValueList(positionCode);
		List uniqueCostCenter = CommonUtil.getUniqueValueList(costCenter);
		List uniqueEchelon = CommonUtil.getUniqueValueList(echelon);

		logger.debug(CLASS_NAME + METHOD_NAME + "Unique Region Code: "
				+ uniqueRegionCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Division Code: "
				+ uniqueDivisionCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Branch Code: "
				+ uniqueBranchCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Job Code: " + uniqueJobCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Position Code: "
				+ uniquePositionCode);
		logger.debug(CLASS_NAME + METHOD_NAME + "Cost Center: "
				+ uniqueCostCenter);
		logger.debug(CLASS_NAME + METHOD_NAME + "Echelon: " + uniqueEchelon);

		// now call and update all the custom objects...

		if (uniqueRegionCode.size() > 0) {
			CommonUtil.updateCustomObject(context,
					CustomObject.REGION_CODE_CUSTOM_OBJECT,
					IdentityAttribute.REGION_CODE, uniqueRegionCode);
		}

		if (uniqueDivisionCode.size() > 0) {
			CommonUtil.updateCustomObject(context,
					CustomObject.DIVISION_CODE_CUSTOM_OBJECT,
					IdentityAttribute.DIVISION_CODE, uniqueDivisionCode);
		}
		if (uniqueBranchCode.size() > 0) {
			CommonUtil.updateCustomObject(context,
					CustomObject.BRANCH_CODE_CUSTOM_OBJECT,
					IdentityAttribute.BRANCH_CODE, uniqueBranchCode);
		}
		if (uniqueJobCode.size() > 0) {
			CommonUtil.updateCustomObject(context,
					CustomObject.JOB_CODE_CUSTOM_OBJECT,
					IdentityAttribute.JOB_CODE, uniqueJobCode);
		}
		if (uniquePositionCode.size() > 0) {
			CommonUtil.updateCustomObject(context,
					CustomObject.POSITION_CODE_CUSTOM_OBJECT,
					IdentityAttribute.POSITION_CODE, uniquePositionCode);
		}
		if (uniqueCostCenter.size() > 0) {
			CommonUtil.updateCustomObject(context,
					CustomObject.COST_CENTER_CUSTOM_OBJECT,
					IdentityAttribute.COST_CENTER, uniqueCostCenter);
		}
		if (uniqueEchelon.size() > 0) {
			CommonUtil.updateCustomObject(context,
					CustomObject.ECHELON_CUSTOM_OBJECT,
					IdentityAttribute.ECHELON, uniqueEchelon);
		}

		logger.debug(CLASS_NAME + METHOD_NAME + "Ending Method...");

	}

	/**
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

}
