import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Add implements UndoableCommand {
	private Logging addLog = new Logging("Add Function");
	private List<AbstractTask> wholeTaskList;
	private String messageToAdd;
	private AbstractTask taskAdded;

	public Add(String commandMessage) {
		addLog.addLog(Logging.LoggingLevel.INFO, "Add(): User Input: "
				+ commandMessage);
		this.messageToAdd = commandMessage;
	}

	public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
		this.wholeTaskList = wholeTaskList;

		List<String> addTokenList = new Vector<String>();
		List<AbstractTask> errorReturn = new LinkedList<AbstractTask>();

		boolean isValidAddMessage = addMessageValidation(messageToAdd,
				addTokenList);
		if (isValidAddMessage) {
			addLog.addLog(Logging.LoggingLevel.INFO, "Add(): " + messageToAdd
					+ " passes validation.");
			return differentiateAndAddTask(addTokenList, errorReturn);
		} else {
			addLog.addLog(Logging.LoggingLevel.WARNING, "Add(): "
					+ messageToAdd + " fails validation.");
			return errorReturn;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Validations
	// ////////////////////////////////////////////////////////////////////////////////////////
	private boolean addMessageValidation(String messageToAdd,
			List<String> addTokenList) {
		// Validation #1 - Determine whether the input is empty
		if (checkMessageEmpty(messageToAdd) == true) {
			addLog.addLog(Logging.LoggingLevel.WARNING, "Add(): "
					+ messageToAdd + " is empty.");
			return false;
		}

		// Validation #2 - Determine whether the first token is keyword
		splitStringIntoTokens(messageToAdd, addTokenList);
		if (checkFirstTokenForKeyword(addTokenList.get(0)) == true) {
			addLog.addLog(Logging.LoggingLevel.WARNING, "Add(): "
					+ messageToAdd + " has a keyword in the first word.");
			return false;
		}

		// Validation #3 - Determine whether there is a syntax error
		if (checkSyntax(addTokenList) == false) {
			addLog.addLog(Logging.LoggingLevel.WARNING, "Add(): "
					+ messageToAdd + " has a syntax error.");
			return false;
		}

		return true;
	}

	// Validation #1
	private static boolean checkMessageEmpty(String messageToAdd) {
		boolean isMessageEmpty = messageToAdd.isEmpty();
		if (isMessageEmpty) {
			return true;
		}
		return false;
	}

	// Validation #2
	private static List<String> splitStringIntoTokens(String messageToAdd,
			List<String> addTokenList) {
		String[] addTokenArray;

		addTokenArray = messageToAdd.replaceAll("(\\s\\.\\w+)",
				"DELIMITER$1DELIMITER").split("DELIMITER");

		for (int i = 0; i < addTokenArray.length; i++) {
			addTokenList.add(addTokenArray[i].trim());
		}
		return addTokenList;
	}

	private boolean checkFirstTokenForKeyword(String firstToken) {
		if (firstToken.matches("\\.\\w+")) {
			return true;
		}
		return false;
	}

	// Validation #3
	private static boolean checkSyntax(List<String> addTokenList) {
		boolean isDuplicatedKeyword = checkDuplicatedKeywords(addTokenList);

		if (isDuplicatedKeyword) {
			return false;
		} else {
			boolean isAllowedKeyword = checkAllowedKeywords(addTokenList);

			if (!isAllowedKeyword) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkDuplicatedKeywords(List<String> addTokenList) {
		HashSet<String> set = new HashSet<String>();

		for (int i = 0; i < addTokenList.size(); i++) {
			boolean isTokenEmpty = addTokenList.get(i).isEmpty();
			if (!isTokenEmpty) {
				char firstCharacter = addTokenList.get(i).charAt(0);
				boolean isCommand = (firstCharacter == '.');
				if (isCommand) {
					if (!set.add(addTokenList.get(i))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean checkAllowedKeywords(List<String> addTokenList) {
		for (int i = 0; i < addTokenList.size(); i++) {
			boolean isTokenEmpty = addTokenList.get(i).isEmpty();
			if (!isTokenEmpty) {
				char firstCharacter = addTokenList.get(i).charAt(0);
				boolean isCommand = (firstCharacter == '.');
				if (isCommand) {
					if (!((addTokenList.get(i).equalsIgnoreCase(".from"))
							|| (addTokenList.get(i).equalsIgnoreCase(".to"))
							|| (addTokenList.get(i).equalsIgnoreCase(".by")) || (addTokenList
								.get(i).equalsIgnoreCase(".at")))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////// Differentiate and Add into Respective Task Categories
	// ////////////////////////////////////////////////////////////////////////////////////////
	private List<AbstractTask> differentiateAndAddTask(
			List<String> addTokenList, List<AbstractTask> errorReturn) {
		boolean isFromKeyword = addTokenList.contains(".from");
		boolean isToKeyword = addTokenList.contains(".to");
		boolean isByKeyword = addTokenList.contains(".by");

		// Timed Task
		if ((isFromKeyword) && (isToKeyword)) {
			boolean hasValidKeywords = checkValidKeywords("timed", addTokenList);

			if (hasValidKeywords) {
				return processTimedTask(addTokenList, errorReturn);
			} else {
				return errorReturn;
			}

		} // Deadline Task
		else if (isByKeyword) {
			boolean hasValidKeywords = checkValidKeywords("deadline",
					addTokenList);

			if (hasValidKeywords) {
				return processDeadlineTask(addTokenList, errorReturn);
			} else {
				return errorReturn;
			}
		} // Floating Task
		else {
			boolean hasValidKeywords = checkValidKeywords("floating",
					addTokenList);

			if (hasValidKeywords) {
				return processFloatingTask(addTokenList, errorReturn);
			} else {
				return errorReturn;
			}
		}

	}

	private List<AbstractTask> processTimedTask(List<String> addTokenList,
			List<AbstractTask> errorReturn) {
		String startDate = "", endDate = "", venue = "";
		DateTime timedTaskStartDate = null;
		DateTime timedTaskEndDate = null;
		String description = addTokenList.get(0);
		addTokenList.remove(0);

		if (addTokenList.size() % 2 != 0) {
			return errorReturn;
		} else {
			for (int i = 0; i < addTokenList.size(); i++) {
				if (addTokenList.get(i).equals(".at")) {
					if (addTokenList.get(i + 1).isEmpty()) {
						return errorReturn;
					} else {
						venue = addTokenList.get(i + 1);
						i = i + 1;
					}
				} else if (addTokenList.get(i).equals(".from")) {
					if (addTokenList.get(i + 1).isEmpty()) {
						return errorReturn;
					} else {
						startDate = addTokenList.get(i + 1);
						timedTaskStartDate = new DateTime(startDate);
						boolean isStartDateValid = timedTaskStartDate
								.validateDateTime();
						if (isStartDateValid) {
							i = i + 1;
						} else {
							return errorReturn;
						}
					}
				} else if (addTokenList.get(i).equals(".to")) {
					if (addTokenList.get(i + 1).isEmpty()) {
						return errorReturn;
					} else {
						endDate = addTokenList.get(i + 1);
						timedTaskEndDate = new DateTime(endDate);
						boolean isEndDateValid = timedTaskEndDate
								.validateDateTime();
						if (isEndDateValid) {
							i = i + 1;
						} else {
							return errorReturn;
						}
					}
				}
			}
		}
		timedTaskStartDate.generateDateTime(false);
		timedTaskEndDate.generateDateTime(true);
		if (timedTaskStartDate.compareTo(timedTaskEndDate.getDateTime()) < 0) {
			TimedTask timedTaskObject = new TimedTask(description,
					timedTaskStartDate.getDateTime(),
					timedTaskEndDate.getDateTime(), venue);
			taskAdded = timedTaskObject;
			addLog.addLog(Logging.LoggingLevel.INFO, taskAdded.getType()
					+ " TASK - " + taskAdded.getDescription() + " is added.");
			return generateReturnList(timedTaskObject);
		} else {
			return errorReturn;
		}
	}

	private List<AbstractTask> processDeadlineTask(List<String> addTokenList,
			List<AbstractTask> errorReturn) {
		String endDate = "", venue = "";
		DateTime deadlineTaskEndDate = null;
		String description = addTokenList.get(0);
		addTokenList.remove(0);

		if (addTokenList.size() % 2 != 0) {
			return errorReturn;
		} else {
			for (int i = 0; i < addTokenList.size(); i++) {
				if (addTokenList.get(i).equals(".at")) {
					if (addTokenList.get(i + 1).isEmpty()) {
						return errorReturn;
					} else {
						venue = addTokenList.get(i + 1);
						i = i + 1;
					}
				} else if (addTokenList.get(i).equals(".by")) {
					if (addTokenList.get(i + 1).isEmpty()) {
						return errorReturn;
					} else {
						endDate = addTokenList.get(i + 1);
						deadlineTaskEndDate = new DateTime(endDate);
						boolean isEndDateValid = deadlineTaskEndDate
								.validateDateTime();
						if (isEndDateValid) {
							i = i + 1;
						} else {
							return errorReturn;
						}
					}
				}
			}
		}
		DeadlineTask deadlineTaskObject = new DeadlineTask(description,
				deadlineTaskEndDate.generateDateTime(true), venue);
		taskAdded = deadlineTaskObject;
		addLog.addLog(Logging.LoggingLevel.INFO, taskAdded.getType()
				+ " TASK - " + taskAdded.getDescription() + " is added.");
		return generateReturnList(deadlineTaskObject);
	}

	private List<AbstractTask> processFloatingTask(List<String> addTokenList,
			List<AbstractTask> errorReturn) {
		String venue = "";
		String description = addTokenList.get(0);
		addTokenList.remove(0);

		if (addTokenList.size() % 2 != 0) {
			return errorReturn;
		} else {
			for (int i = 0; i < addTokenList.size(); i++) {
				if (addTokenList.get(i).equals(".at")) {
					if (addTokenList.get(i + 1).isEmpty()) {
						return errorReturn;
					} else {
						venue = addTokenList.get(i + 1);
						i = i + 1;
					}
				}
			}
		}
		FloatingTask floatingTaskObject = new FloatingTask(description, venue);
		taskAdded = floatingTaskObject;
		addLog.addLog(Logging.LoggingLevel.INFO, taskAdded.getType()
				+ " TASK - " + taskAdded.getDescription() + " is added.");
		return generateReturnList(floatingTaskObject);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Check for Valid Keywords
	// ////////////////////////////////////////////////////////////////////////////////////////
	private static boolean checkValidKeywords(String taskType,
			List<String> addTokenList) {
		boolean isTimedTask = taskType.equals("timed");
		boolean isDeadlineTask = taskType.equals("deadline");
		boolean isFloatingTask = taskType.equals("floating");

		for (int i = 0; i < addTokenList.size(); i++) {
			if (!addTokenList.get(i).isEmpty()) {
				char firstCharacter = addTokenList.get(i).charAt(0);
				boolean hasMoreKeyword = ((firstCharacter == '.') && (!addTokenList
						.get(i).equals(".at")));

				if (hasMoreKeyword) {
					if (isTimedTask) {
						if (!((addTokenList.get(i).equalsIgnoreCase(".from")) || (addTokenList
								.get(i).equalsIgnoreCase(".to")))) {
							return false;
						}
					} else if (isDeadlineTask) {
						if ((!addTokenList.get(i).equalsIgnoreCase(".by"))) {
							return false;
						}
					} else if (isFloatingTask) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Generate List for return
	// ////////////////////////////////////////////////////////////////////////////////////////
	private List<AbstractTask> generateReturnList(AbstractTask taskAdded) {
		List<AbstractTask> returnList = new Vector<AbstractTask>();
		wholeTaskList.add(taskAdded);
		returnList.add(taskAdded);
		return returnList;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// UNDO FUNCTION
	// ////////////////////////////////////////////////////////////////////////////////////////
	public List<AbstractTask> undo() {
		List<AbstractTask> returnList = new Vector<AbstractTask>();
		wholeTaskList.remove(taskAdded);
		returnList.add(taskAdded);
		addLog.addLog(Logging.LoggingLevel.INFO, "Add(): Task Undo - "
				+ taskAdded.getType() + ": " + taskAdded.getDescription());
		return returnList;
	}
}