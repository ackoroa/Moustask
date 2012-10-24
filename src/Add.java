import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Add implements UndoableCommand {
	private List<AbstractTask> wholeTaskList;
	private String messageToAdd;
	private AbstractTask taskAdded;

	public Add(String commandMessage) {
		this.messageToAdd = commandMessage;
	}

	public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
		this.wholeTaskList = wholeTaskList;

		List<String> addTokenList = new Vector<String>();
		List<AbstractTask> errorReturn = new LinkedList<AbstractTask>();

		boolean isValidAddMessage = addMessageValidation(messageToAdd,
				addTokenList);
		if (isValidAddMessage) {
			return differentiateAndAddTask(addTokenList, errorReturn);
		} else {
			return errorReturn;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Validation
	// ////////////////////////////////////////////////////////////////////////////////////////
	private boolean addMessageValidation(String messageToAdd,
			List<String> addTokenList) {
		// Validation #1 - Determine whether the input is empty
		if (checkMessageEmpty(messageToAdd) == true) {
			return false;
		}

		// Validation #2 - Determine whether the first token is keyword
		splitStringIntoTokens(messageToAdd, addTokenList);
		if (checkFirstTokenForKeyword(addTokenList.get(0)) == true) {
			return false;
		}

		// Validation #3 - Determine whether there is a syntax error in the
		// command
		if (checkSyntax(addTokenList) == false) {
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
		// Syntax Check Sequence:
		// After separated the message into string tokens,
		// 1. check for duplicated keywords
		// 2. check for allowed keywords
		// 3. identify the correct task based on the keywords
		// 3a. ensure only correct keywords are used for the right task
		// 3b. ensure that all respective fields are not empty
		// 3c. check for correct date and time
		// 3d. ensure that there are flexibility in the ordering of keywords
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
	// //////////////////////////// Differentiate and Add into Respective Task
	// Categories
	// ////////////////////////////////////////////////////////////////////////////////////////
	private List<AbstractTask> differentiateAndAddTask(
			List<String> addTokenList, List<AbstractTask> errorReturn) {
		boolean isFromKeyword = addTokenList.contains(".from");
		boolean isToKeyword = addTokenList.contains(".to");
		boolean isByKeyword = addTokenList.contains(".by");

		// For TimedTask
		if ((isFromKeyword) && (isToKeyword)) {
			boolean hasValidKeywords = checkValidKeywords("timed", addTokenList);

			if (hasValidKeywords) {
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
				timedTaskStartDate.generateDateTime();
				timedTaskEndDate.generateDateTime();
				if (timedTaskStartDate
						.compareTo(timedTaskEndDate.getDateTime()) < 0) {
					TimedTask timedTaskObject = new TimedTask(description,
							timedTaskStartDate.getDateTime(),
							timedTaskEndDate.getDateTime(), venue);
					taskAdded = timedTaskObject;
					return generateReturnList(timedTaskObject);
				} else {
					return errorReturn;
				}
			} else {
				return errorReturn;
			}

		} else if (isByKeyword) { // For Deadline Task
			boolean hasValidKeywords = checkValidKeywords("deadline",
					addTokenList);

			if (hasValidKeywords) {
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
						deadlineTaskEndDate.generateDateTime(), venue);
				taskAdded = deadlineTaskObject;
				return generateReturnList(deadlineTaskObject);
			} else {
				return errorReturn;
			}
		} else { // For Floating Task
			boolean hasValidKeywords = checkValidKeywords("floating",
					addTokenList);

			if (hasValidKeywords) {
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
				FloatingTask floatingTaskObject = new FloatingTask(description,
						venue);
				taskAdded = floatingTaskObject;
				return generateReturnList(floatingTaskObject);
			} else {
				return errorReturn;
			}
		}

	}

	private static boolean checkValidKeywords(String taskType,
			List<String> addTokenList) {
		boolean isTimedTask = taskType.equals("timed");
		boolean isDeadlineTask = taskType.equals("deadline");
		boolean isFloatingTask = taskType.equals("floating");

		if (isTimedTask) {
			for (int i = 0; i < addTokenList.size(); i++) {
				if (!addTokenList.get(i).isEmpty()) {
					char firstCharacter = addTokenList.get(i).charAt(0);
					boolean isCommand = ((firstCharacter == '.') && (!addTokenList
							.get(i).equals(".at")));

					if (isCommand) {
						if (!((addTokenList.get(i).equalsIgnoreCase(".from")) || (addTokenList
								.get(i).equalsIgnoreCase(".to")))) {
							return false;
						}
					}
				}
			}
		} else if (isDeadlineTask) {
			for (int i = 0; i < addTokenList.size(); i++) {
				if (!addTokenList.get(i).isEmpty()) {
					char firstCharacter = addTokenList.get(i).charAt(0);
					boolean isCommand = ((firstCharacter == '.') && (!addTokenList
							.get(i).equals(".at")));

					if (isCommand) {
						if ((!addTokenList.get(i).equalsIgnoreCase(".by"))) {
							return false;
						}
					}
				}
			}
		} else if (isFloatingTask) {
			for (int i = 0; i < addTokenList.size(); i++) {
				if (!addTokenList.get(i).isEmpty()) {
					char firstCharacter = addTokenList.get(i).charAt(0);
					boolean isCommand = ((firstCharacter == '.') && (!addTokenList
							.get(i).equals(".at")));

					if (isCommand) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private List<AbstractTask> generateReturnList(AbstractTask taskAdded) {
		List<AbstractTask> returnList = new Vector<AbstractTask>();
		wholeTaskList.add(taskAdded);
		returnList.add(taskAdded);
		return returnList;
	}

	public List<AbstractTask> undo() {
		List<AbstractTask> returnList = new Vector<AbstractTask>();
		wholeTaskList.remove(taskAdded);
		returnList.add(taskAdded);
		return returnList;
	}
}