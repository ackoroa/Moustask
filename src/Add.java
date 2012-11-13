//@author A0058657N
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
			addLog.addLog(Logging.LoggingLevel.INFO, "Add.execute(): "
					+ messageToAdd + " passes validation.");
			return differentiateAndAddTask(addTokenList, errorReturn);
		} else {
			addLog.addLog(Logging.LoggingLevel.WARNING, "Add.execute(): "
					+ messageToAdd + " fails validation.");
			return errorReturn;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Validations
	// ////////////////////////////////////////////////////////////////////////////////////////
	private boolean addMessageValidation(String messageToAdd,
			List<String> addTokenList) {
		boolean isMessageEmpty = checkMessageEmpty(messageToAdd) == true;
		
		// Validation #1 - Determine whether the input is empty
		if (isMessageEmpty) {
			addLog.addLog(Logging.LoggingLevel.WARNING, "Add(): "
					+ messageToAdd + " is empty.");
			return false;
		}

		// Validation #2 - Determine whether the first token is keyword
		splitStringIntoTokens(messageToAdd, addTokenList);
		boolean isFirstTokenKeyword = checkFirstTokenForKeyword(addTokenList.get(0)) == true;
		
		if (isFirstTokenKeyword) {
			addLog.addLog(Logging.LoggingLevel.WARNING, "Add(): "
					+ messageToAdd + " has a keyword in the first word.");
			return false;
		}

		// Validation #3 - Determine whether there is a syntax error
		boolean isNoSyntaxError = checkSyntax(addTokenList) == false;
		
		if (isNoSyntaxError) {
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
			System.out.println("Please enter your task name after .add");
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
		boolean isFirstWordKeyword = firstToken.matches("\\.\\w+");
		
		if (isFirstWordKeyword) {
			System.out.println("Please enter your task name after .add");
			return true;
		}
		return false;
	}

	// Validation #3
	private static boolean checkSyntax(List<String> addTokenList) {
		boolean isDuplicatedKeyword = checkDuplicatedKeywords(addTokenList);

		if (isDuplicatedKeyword) {
			System.out
					.println("You have entered some duplicated keywords.\nPlease enter according to the add format:\neg: .add <Task Name>...");
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
					boolean isFromKeyword = addTokenList.get(i)
							.equalsIgnoreCase(".from");
					boolean isToKeyword = addTokenList.get(i).equalsIgnoreCase(
							".to");
					boolean isByKeyword = addTokenList.get(i).equalsIgnoreCase(
							".by");
					boolean isAtKeyword = addTokenList.get(i).equalsIgnoreCase(
							".at");

					if (!((isFromKeyword) || (isToKeyword) || (isByKeyword) || (isAtKeyword))) {
						System.out
								.println("You have entered some invalid keywords.\nPlease enter according to the add format:\neg: .add <Task Name>...");
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
				System.out
						.println("You have entered an invalid command to add a timed task.\nPlease enter according to the adding timed task format:\neg: .add <Task Name> .at <Venue> .from <date time |day> .to <date time |day>");
				return errorReturn;
			}
		} // Deadline Task
		else if (isByKeyword) {
			boolean hasValidKeywords = checkValidKeywords("deadline",
					addTokenList);

			if (hasValidKeywords) {
				return processDeadlineTask(addTokenList, errorReturn);
			} else {
				System.out
						.println("You have entered an invalid command to add a deadline task.\nPlease enter according to the adding deadline task format:\neg: .add <Task Name> .at <Venue> .by <date time |day>");
				return errorReturn;
			}
		} // Floating Task
		else {
			boolean hasValidKeywords = checkValidKeywords("floating",
					addTokenList);

			if (hasValidKeywords) {
				return processFloatingTask(addTokenList, errorReturn);
			} else {
				System.out
						.println("You have entered an invalid command to add a floating task.\nPlease enter according to the adding floating task format:\neg: .add <Task Name> .at <Venue>");
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
		boolean isTokenSizeOdd = addTokenList.size() % 2 != 0;

		if (isTokenSizeOdd) {
			System.out
					.println("You have entered an invalid command to add a timed task.\nPlease enter according to the adding timed task format:\neg: .add <Task Name> .at <Venue> .from <date time |day> .to <date time|day>");
			return errorReturn;
		} else {
			for (int i = 0; i < addTokenList.size(); i++) {
				boolean isAtKeyword = addTokenList.get(i).equals(".at");
				boolean isFromKeyword = addTokenList.get(i).equals(".from");
				boolean isToKeyword = addTokenList.get(i).equals(".to");

				if (isAtKeyword) {
					boolean isNextTokenEmpty = addTokenList.get(i + 1)
							.isEmpty();

					if (isNextTokenEmpty) {
						System.out
								.println("You have entered an invalid command to add a timed task.\nPlease enter according to the adding timed task format:\neg: .add <Task Name> .at <Venue> .from <date time |day> .to <date time|day>");
						return errorReturn;
					} else {
						venue = addTokenList.get(i + 1);
						i = i + 1;
					}
				} else if (isFromKeyword) {
					boolean isNextTokenEmpty = addTokenList.get(i + 1)
							.isEmpty();

					if (isNextTokenEmpty) {
						System.out
								.println("You have entered an invalid date time OR day format.\nPlease enter according to the date time OR day format:\ndate time format eg: 2012-12-21 12:21\nday format eg: monday");
						return errorReturn;
					} else {
						startDate = addTokenList.get(i + 1);
						timedTaskStartDate = new DateTime(startDate);
						boolean isStartDateValid = timedTaskStartDate
								.validateDateTime();
						if (isStartDateValid) {
							i = i + 1;
						} else {
							System.out
									.println("You have entered an invalid date time OR day format.\nPlease enter according to the date time OR day format:\ndate time format eg: 2012-12-21 12:21\nday format eg: monday");
							return errorReturn;
						}
					}
				} else if (isToKeyword) {
					boolean isNextTokenEmpty = addTokenList.get(i + 1)
							.isEmpty();

					if (isNextTokenEmpty) {
						System.out
								.println("You have entered an invalid date time OR day format.\nPlease enter according to the date time OR day format:\ndate time format eg: 2012-12-21 12:21\nday format eg: monday");
						return errorReturn;
					} else {
						endDate = addTokenList.get(i + 1);
						timedTaskEndDate = new DateTime(endDate);
						boolean isEndDateValid = timedTaskEndDate
								.validateDateTime();

						if (isEndDateValid) {
							i = i + 1;
						} else {
							System.out
									.println("You have entered an invalid date time OR day format.\nPlease enter according to the date time OR day format:\ndate time format eg: 2012-12-21 12:21\nday format eg: monday");
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
			System.out
					.println("Your Start Date/Day is later than End Date/Day.\nPlease try again.");
			return errorReturn;
		}
	}

	private List<AbstractTask> processDeadlineTask(List<String> addTokenList,
			List<AbstractTask> errorReturn) {
		String endDate = "", venue = "";
		DateTime deadlineTaskEndDate = null;
		String description = addTokenList.get(0);
		addTokenList.remove(0);
		boolean isTokenSizeOdd = addTokenList.size() % 2 != 0;

		if (isTokenSizeOdd) {
			System.out
					.println("You have entered an invalid command to add a deadline task.\nPlease enter according to the adding deadline task format:\neg: .add <Task Name> .at <Venue> .by <date time|day>");
			return errorReturn;
		} else {
			for (int i = 0; i < addTokenList.size(); i++) {
				boolean isAtKeyword = addTokenList.get(i).equals(".at");
				boolean isByKeyword = addTokenList.get(i).equals(".by");

				if (isAtKeyword) {
					boolean isNextTokenEmpty = addTokenList.get(i + 1)
							.isEmpty();

					if (isNextTokenEmpty) {
						System.out
								.println("You have entered an invalid command to add a deadline task.\nPlease enter according to the adding deadline task format:\neg: .add <Task Name> .at <Venue> .by <date time|day>");
						return errorReturn;
					} else {
						venue = addTokenList.get(i + 1);
						i = i + 1;
					}
				} else if (isByKeyword) {
					boolean isNextTokenEmpty = addTokenList.get(i + 1)
							.isEmpty();

					if (isNextTokenEmpty) {
						System.out
								.println("You have entered an invalid date time OR day format.\nPlease enter according to the date time OR day format:\ndate time format eg: 2012-12-21 12:21\nday format eg: monday");
						return errorReturn;
					} else {
						endDate = addTokenList.get(i + 1);
						deadlineTaskEndDate = new DateTime(endDate);
						boolean isEndDateValid = deadlineTaskEndDate
								.validateDateTime();
						if (isEndDateValid) {
							i = i + 1;
						} else {
							System.out
									.println("You have entered an invalid date time OR day format.\nPlease enter according to the date time OR day format:\ndate time format eg: 2012-12-21 12:21\nday format eg: monday");
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
		boolean isTokenSizeOdd = addTokenList.size() % 2 != 0;

		if (isTokenSizeOdd) {
			System.out
					.println("You have entered an invalid command to add a floating task.\nPlease enter according to the adding floating task format:\neg: .add <Task Name> .at <Venue>");
			return errorReturn;
		} else {
			for (int i = 0; i < addTokenList.size(); i++) {
				boolean isAtKeyword = addTokenList.get(i).equals(".at");

				if (isAtKeyword) {
					boolean isNextTokenEmpty = addTokenList.get(i + 1)
							.isEmpty();

					if (isNextTokenEmpty) {
						System.out
								.println("You have entered an invalid command to add a floating task.\nPlease enter according to the adding floating task format:\neg: .add <Task Name> .at <Venue>");
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