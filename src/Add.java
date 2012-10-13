import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
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

		boolean isMessageEmpty = messageToAdd.isEmpty();
		if (isMessageEmpty) {
			return errorReturn;
		}

		addTokenList = splitStringIntoTokens(messageToAdd);

		boolean isError = errorCheck(addTokenList);
		if (isError) {
			return errorReturn;
		}

		if ((addTokenList.contains(".from")) && (addTokenList.contains(".to"))) {
			boolean hasValidKeywords = checkValidKeywords("timed", addTokenList);

			if (hasValidKeywords) {
				String startDate = "", endDate = "", venue = "";
				String description = addTokenList.get(0);
				addTokenList.remove(0);

				if (addTokenList.size() % 2 != 0) {
					System.out.println("Got empty fields in this timed tasks");
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
								boolean isStartDateValid = DateTimeValidator(startDate);
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
								boolean isEndDateValid = DateTimeValidator(endDate);
								if (isEndDateValid) {
									i = i + 1;
								} else {
									return errorReturn;
								}
							}
						}
					}
				}
				TimedTask timedTaskObject = new TimedTask(description,
						startDate, endDate, venue);
				taskAdded = timedTaskObject;
				return generateReturnList(timedTaskObject);
			} else {
				System.out
						.println("Got not allowed keyword in this timed tasks");
				return errorReturn;
			}

		} else if (addTokenList.contains(".by")) {
			boolean hasValidKeywords = checkValidKeywords("deadline",
					addTokenList);

			if (hasValidKeywords) {
				String endDate = "", venue = "";
				String description = addTokenList.get(0);
				addTokenList.remove(0);

				if (addTokenList.size() % 2 != 0) {
					System.out
							.println("Got empty fields in this deadline tasks");
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
								boolean isEndDateValid = DateTimeValidator(endDate);
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
						endDate, venue);
				taskAdded = deadlineTaskObject;
				return generateReturnList(deadlineTaskObject);
			} else {
				System.out
						.println("Got not allowed keyword in this deadline tasks");
				return errorReturn;
			}
		} else {
			boolean hasValidKeywords = checkValidKeywords("floating",
					addTokenList);

			if (hasValidKeywords) {
				String venue = "";
				String description = addTokenList.get(0);
				addTokenList.remove(0);

				if (addTokenList.size() % 2 != 0) {
					System.out
							.println("Got empty fields in this floating tasks");
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
				System.out
						.println("Got not allowed keyword in this floating tasks");
				return errorReturn;
			}
		}
	}

	private static List<String> splitStringIntoTokens(String messageToAdd) {
		String[] addTokenArray;
		List<String> addTokenList = new Vector<String>();

		addTokenArray = messageToAdd.replaceAll("(\\.\\w+)",
				"DELIMITER$1DELIMITER").split("DELIMITER");

		for (int i = 0; i < addTokenArray.length; i++) {
			addTokenList.add(addTokenArray[i].trim());
			System.out.println("Token " + addTokenList.get(i));
		}

		return addTokenList;
	}

	private boolean errorCheck(List<String> addTokenList) {
		boolean isFirstStringKeyword = checkFirstStringForKeyword(addTokenList
				.get(0));
		if (isFirstStringKeyword) {
			return true;
		}

		boolean isSyntaxPass = checkSyntax(addTokenList);
		if (!isSyntaxPass) {
			return true;
		}

		return false;
	}

	private boolean checkFirstStringForKeyword(String firstToken) {
		return (firstToken.isEmpty());
	}

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
			System.out.println("DUPLICATED KEYWORD");
			return false;
		} else {
			System.out.println("NO DUPLICATED KEYWORD, continue on...");
			boolean isAllowedKeyword = checkAllowedKeywords(addTokenList);

			if (!isAllowedKeyword) {
				System.out.println("Got fake keyword");
				return false;
			} else {
				System.out.println("All are allowed keywords, continue on...");
			}
		}
		return true;
	}

	private static boolean checkDuplicatedKeywords(List<String> addTokenList) {
		HashSet<String> set = new HashSet<String>();

		for (int i = 0; i < addTokenList.size(); i++) {
			if (!addTokenList.get(i).isEmpty()) {
				char firstCharacter = addTokenList.get(i).charAt(0);
				boolean isCommand = (firstCharacter == '.');
				if (isCommand) {
					if (!set.add(addTokenList.get(i))) {
						System.out.println(addTokenList.get(i));
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean checkAllowedKeywords(List<String> addTokenList) {
		for (int i = 0; i < addTokenList.size(); i++) {
			if (!addTokenList.get(i).isEmpty()) {
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

	private boolean checkValidKeywords(String taskType,
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

	private static boolean DateTimeValidator(String dateTime) {
		Vector<String> dateTimeToken = new Vector<String>();
		StringTokenizer st = new StringTokenizer(dateTime, " ");
		while (st.hasMoreTokens()) {
			dateTimeToken.add(st.nextToken());
		}

		if (dateTimeToken.size() != 2) {
			return false;
		}

		boolean isValidDate = dateValidator(dateTimeToken.get(0));
		if (!isValidDate) {
			return false;
		}

		boolean isValidTime = timeValidator(dateTimeToken.get(1));
		if (!isValidTime) {
			return false;
		}

		return true;
	}

	private static boolean dateValidator(String date) {
		int year, month, day;
		Vector<String> dateToken = new Vector<String>();
		StringTokenizer st = new StringTokenizer(date, "-");
		while (st.hasMoreTokens()) {
			dateToken.add(st.nextToken());
		}

		if (dateToken.size() != 3) {
			return false;
		}

		try {
			year = Integer.parseInt(dateToken.get(0));
			month = Integer.parseInt(dateToken.get(1));
			day = Integer.parseInt(dateToken.get(2));
		} catch (NumberFormatException e) {
			return false;
		}

		if ((year < 0) || (year > 3000)) {
			return false;
		}
		if ((month < 0) || (month > 12)) {
			return false;
		}
		if ((day < 0) || (day > 31)) {
			return false;
		}

		return true;
	}

	private static boolean timeValidator(String time) {
		int hour, minutes;
		Vector<String> timeToken = new Vector<String>();
		StringTokenizer st = new StringTokenizer(time, ":");
		while (st.hasMoreTokens()) {
			timeToken.add(st.nextToken());
		}

		if (timeToken.size() != 2) {
			return false;
		}

		try {
			hour = Integer.parseInt(timeToken.get(0));
			minutes = Integer.parseInt(timeToken.get(1));
		} catch (NumberFormatException e) {
			return false;
		}

		if ((hour < 0) || (hour > 23)) {
			return false;
		}
		if ((minutes < 0) || (minutes > 59)) {
			return false;
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