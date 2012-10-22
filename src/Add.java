import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Date;

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
						generateDateTime(startDate), generateDateTime(endDate),
						venue);
				taskAdded = timedTaskObject;
				return generateReturnList(timedTaskObject);
			} else {
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
						generateDateTime(endDate), venue);
				taskAdded = deadlineTaskObject;
				return generateReturnList(deadlineTaskObject);
			} else {
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

	private static List<String> splitStringIntoTokens(String messageToAdd) {
		String[] addTokenArray;
		List<String> addTokenList = new Vector<String>();

		addTokenArray = messageToAdd.replaceAll("(\\s\\.\\w+)",
				"DELIMITER$1DELIMITER").split("DELIMITER");

		for (int i = 0; i < addTokenArray.length; i++) {
			addTokenList.add(addTokenArray[i].trim());
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
			if (!addTokenList.get(i).isEmpty()) {
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

	private static Vector<String> splitDateTime(String dateTime) {
		Vector<String> dateTimeToken = new Vector<String>();
		StringTokenizer st = new StringTokenizer(dateTime, " ");
		while (st.hasMoreTokens()) {
			dateTimeToken.add(st.nextToken());
		}
		return dateTimeToken;
	}

	private static boolean DateTimeValidator(String dateTime) {
		Vector<String> dateTimeToken = new Vector<String>();
		dateTimeToken = splitDateTime(dateTime);
		boolean isDateTimeTokenSizeOne = dateTimeToken.size() == 1;
		boolean isDateTimeTokenSizeTwo = dateTimeToken.size() == 2;

		if (isDateTimeTokenSizeOne) {
			SimpleDateFormat dayFormat = new SimpleDateFormat("E");
			dayFormat.setLenient(false);
			try {
				dayFormat.parse(dateTime);

			} catch (ParseException e) {
				return false;
			}
		} else if (isDateTimeTokenSizeTwo) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			dateFormat.setLenient(false);
			try {
				dateFormat.parse(dateTime);
			} catch (ParseException e) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	private static String generateDateTime(String dateTime) {
		Vector<String> dateTimeToken = new Vector<String>();
		dateTimeToken = splitDateTime(dateTime);
		boolean isDateTimeTokenSizeOne = dateTimeToken.size() == 1;

		if (isDateTimeTokenSizeOne) {
			SimpleDateFormat dayFormat = new SimpleDateFormat("E");
			dayFormat.setLenient(false);
			try {
				Date date1 = dayFormat.parse(dateTime);
				Calendar calendar = Calendar.getInstance();
				Calendar nowCalendar = Calendar.getInstance();
				nowCalendar.setTime(date1);
				while (calendar.get(Calendar.DAY_OF_WEEK) != nowCalendar
						.get(Calendar.DAY_OF_WEEK)) {
					calendar.add(Calendar.DAY_OF_MONTH, 1);
				}
				String formattedDateTime = new SimpleDateFormat(
						"yyyy-MM-dd 00:00").format(calendar.getTime());
				return formattedDateTime;

			} catch (ParseException e) {
				System.out.println("Error - " + e);
			}
		}
		return dateTime;
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