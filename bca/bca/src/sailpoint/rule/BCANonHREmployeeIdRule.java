package sailpoint.rule;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.google.common.collect.Iterators;

import sailpoint.api.ObjectUtil;
import sailpoint.api.PersistenceManager;
import sailpoint.api.SailPointContext;
import sailpoint.common.IdentityAttribute;
import sailpoint.object.Attributes;
import sailpoint.object.Custom;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.server.InternalContext;
import sailpoint.tools.GeneralException;

@SuppressWarnings({ "rawtypes" })
public class BCANonHREmployeeIdRule {

	public static Logger logger = Logger
			.getLogger("sailpoint.rule.BCAEmployeeIdRule");

	static String CLASS_NAME = "BCANonHREmployeeIdRule";

	/**
	 * 
	 * Generate NIP (Employee ID)... For staff it is received from
	 * 
	 * HR SAP, for non-staff, it is generated in IIQ... -
	 * 
	 * , IIQ will retrieve it from a sequence created for this purpose in
	 * 
	 * the IIQ database and completed with leading zeros as necessary. - Before
	 * 
	 * effectively saving the new non-staff identity, IIQ will perform a
	 * 
	 * uniqueness verification inside its own list of Identities’ NIP. If any
	 * 
	 * Identity is found with the same NIP, it will recursively re-query the
	 * 
	 * sequence for the next ID number until it finds one that is not used
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param internalContext
	 * 
	 * 
	 * 
	 * @return
	 * 
	 * @throws GeneralException
	 */

	public static String generateNIP(InternalContext internalContext)

	throws GeneralException {

		String METHOD_NAME = "::generateNIP::";

		String nip = "";

		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		SailPointContext context = internalContext.getContext();

		String IIQ_MANAGED_USER_PREFIX = "";

		logger.debug("Prepared to call method getNextSequence");

		nip = IIQ_MANAGED_USER_PREFIX + getNextSequence(internalContext);

		boolean isUniqueNip = false;

		logger.debug("Check Uniqueness of new NIP");

		isUniqueNip = isUniqueNip(context, nip);

		logger.debug("Prepared enter loop to check uniqueness");

		for (; !isUniqueNip;) {

			nip = IIQ_MANAGED_USER_PREFIX + getNextSequence(internalContext);

			isUniqueNip = isUniqueNip(context, nip);

		}

		logger.debug(CLASS_NAME + METHOD_NAME + "Unique ERN Found: " + nip);

		return nip;

	}

	private static boolean isUniqueNip(SailPointContext context, String nip)
			throws GeneralException {

		boolean isUnique = false;

		String METHOD_NAME = "::isUniqueNip::";

		QueryOptions qo = new QueryOptions();
		Filter f = Filter.eq(IdentityAttribute.EMPLOYEE_ID, nip);
		qo.add(f);

		Iterator identities = context.search(Identity.class, qo);

		int size = Iterators.size(identities);

		if (size > 0) {
			isUnique = false;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning isUnique nip: "
					+ isUnique);
			return isUnique;
		} else {
			isUnique = true;
			logger.debug(CLASS_NAME + METHOD_NAME + "Returning isUnique nip: "
					+ isUnique);
			return isUnique;

		}

	}

	/**
	 * 
	 * @param internalContext
	 * 
	 * @return
	 * 
	 * @throws GeneralException
	 */

	public static String getNextSequence(InternalContext internalContext)

	throws GeneralException {

		String METHOD_NAME = "::getNextSequence::";

		String nextUniqueSequence = "";

		logger.debug(CLASS_NAME + METHOD_NAME + "Inside...");

		SailPointContext context = internalContext.getContext();

		String SEQUENCE_OBJECT = "BCA_Non_Employee_Id_Sequence";

		Custom sequenceObject = context.getObjectByName(Custom.class,
				SEQUENCE_OBJECT);

		if (sequenceObject == null) {

			logger.debug(CLASS_NAME + METHOD_NAME + "Sequence Object "
					+ SEQUENCE_OBJECT
					+ " not found... hence returning blank value...");

			return nextUniqueSequence;

		} else {

			Attributes attributes = sequenceObject.getAttributes();

			nextUniqueSequence = attributes.getString("nextUniqueSequence");

			logger.debug(CLASS_NAME + METHOD_NAME
					+ "Next Unique Sequence Found: " + nextUniqueSequence);

			// Try to increment the sequence by one

			boolean isIncrementSuccessful = incrementSequence(context,

			SEQUENCE_OBJECT, nextUniqueSequence);

			logger.debug(CLASS_NAME + METHOD_NAME + "isIncrementSuccessful: "
					+ isIncrementSuccessful);

		}

		return nextUniqueSequence;

	}

	/**
	 * 
	 * Getting lock on the sequence and then increment it by one...
	 * 
	 * 
	 * 
	 * @param context
	 * 
	 * @param sequenceObjectString
	 * 
	 * @param nextUniqueSequence
	 * 
	 * @return
	 * 
	 * @throws GeneralException
	 */

	public static boolean incrementSequence(SailPointContext context,

	String sequenceObjectString, String nextUniqueSequence1)

	throws GeneralException {

		String METHOD_NAME = "::incrementSequence::";

		int baseSequence = Integer.parseInt(nextUniqueSequence1);

		baseSequence = baseSequence + 1;

		if (baseSequence > 99999) {

			logger.debug(CLASS_NAME
					+ METHOD_NAME
					+ "sequence value had gone beyond the specified limit.. cant continue... reset the sequence....");

			return false;

		}

		nextUniqueSequence1 = Integer.toString(baseSequence);

		// See if there is already a custom object, and lock it if there is.

		Custom sequenceObject = (Custom) ObjectUtil.lockObject(context,

		Custom.class, null, sequenceObjectString,

		PersistenceManager.LOCK_TYPE_TRANSACTION);

		logger.debug(CLASS_NAME + METHOD_NAME + "nextUniqueSequence: "
				+ nextUniqueSequence1);

		nextUniqueSequence1 = getformatedSequenceString(nextUniqueSequence1);

		sequenceObject.put( "nextUniqueSequence",nextUniqueSequence1);

		// Trying to update the sequence....

		context.startTransaction();

		context.saveObject(sequenceObject);

		context.commitTransaction();

		context.decache();

		logger.trace(CLASS_NAME + METHOD_NAME + sequenceObject.toXml());

		return true;

	}

	/**
	 * 
	 * @param nextUniqueSequence
	 * 
	 * @return
	 */

	public static String getformatedSequenceString(String nextUniqueSequence) {

		String METHOD_NAME = "::getformatedSequenceString::";

		logger.debug(CLASS_NAME + METHOD_NAME + "Inside..");

		String formatedString = "00000";

		int nextUniqueSequenceLength = nextUniqueSequence.length();

		int formatedStringLength = formatedString.length();

		logger.debug(CLASS_NAME + METHOD_NAME + "formatedStringLength: "
				+ formatedStringLength);

		logger.debug(CLASS_NAME + METHOD_NAME + "nextUniqueSequenceLength: "
				+ nextUniqueSequenceLength);

		formatedString = formatedString.substring(0,
				(formatedStringLength - nextUniqueSequenceLength))
				+ nextUniqueSequence;

		logger.debug(CLASS_NAME + METHOD_NAME + "formatedString: "
				+ formatedString);

		return formatedString;

	}

}
