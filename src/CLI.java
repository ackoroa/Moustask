import java.util.List;
import java.util.Scanner;

class CLI {
	private static CLI commandLine;
	private Logic logicHandler;
	private TypeTaskPair taskResult;
	private Scanner userInput = new Scanner(System.in);

	private CLI() {
		logicHandler = Logic.getInstance();
	}

	public static CLI getInstance() {
		if (commandLine == null) {
			commandLine = new CLI();
		}
		return commandLine;
	}

	public void getUserInput() {
		while (true) {
			System.out.print("Command: ");
			taskResult = logicHandler.processCommand(userInput.nextLine());
			printMessage();
		}
	}

	private void printMessage() {
		boolean isTaskListEmpty = (taskResult.getType() == TypeTaskPair.Type.EMPTY);
		boolean isSearchOrDisplayCommandRequiredFirst = (taskResult.getType() == TypeTaskPair.Type.ERROR);
		boolean isAddOperation = (taskResult.getType() == TypeTaskPair.Type.ADD);
		boolean isDeleteOperation = (taskResult.getType() == TypeTaskPair.Type.DELETE);
		boolean isEditOperation = (taskResult.getType() == TypeTaskPair.Type.EDIT);
		boolean isSearchOperation = (taskResult.getType() == TypeTaskPair.Type.SEARCH);
		boolean isDisplayOperation = (taskResult.getType() == TypeTaskPair.Type.DISPLAY);
		boolean isUnableToUndo = (taskResult.getType() == TypeTaskPair.Type.UNDONULL);
		boolean isUndoAddOperation = (taskResult.getType() == TypeTaskPair.Type.UNDOADD);
		boolean isUndoDeleteOperation = (taskResult.getType() == TypeTaskPair.Type.UNDODELETE);
		boolean isUndoEditOperation = (taskResult.getType() == TypeTaskPair.Type.UNDOEDIT);
		boolean isUndoClearOperation = (taskResult.getType() == TypeTaskPair.Type.UNDOCLEAR);
		boolean isClearOperation = (taskResult.getType() == TypeTaskPair.Type.CLEAR);
		boolean isHelpOperation = (taskResult.getType() == TypeTaskPair.Type.HELP);
		boolean isInvalidCommand = (taskResult.getType() == TypeTaskPair.Type.INVALID);
		boolean isExitOperation = (taskResult.getType() == TypeTaskPair.Type.EXIT);

		if (isTaskListEmpty) {
			System.out.println("Your task list is currently empty.");
		} else if (isSearchOrDisplayCommandRequiredFirst) {
			System.out
					.println("You must use a search or display command first.");
		} else if (isAddOperation) {
			boolean isAddResultEmpty = taskResult.getTasks().isEmpty();
			if (isAddResultEmpty) {
				System.out.println("Invalid Syntax. Please try again.");
			} else {
				System.out.println("Task "
						+ taskResult.getTasks().get(0).getDescription()
						+ " has been added.");
			}
		} else if (isDeleteOperation) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been deleted.");
			logicHandler.clearSearchOrDisplayTaskList();
		} else if (isEditOperation) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been updated.");
			logicHandler.clearSearchOrDisplayTaskList();
		} else if (isSearchOperation) {
			if (taskResult.getTasks().isEmpty()) {
				System.out.println("Your search has returned no result.");
			} else {
				displayTaskList(taskResult.getTasks());
			}
		} else if (isDisplayOperation) {
			displayTaskList(taskResult.getTasks());
		} else if (isUnableToUndo) {
			System.out.println("There is nothing to undo.");
		} else if (isUndoAddOperation) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been deleted.");
		} else if (isUndoDeleteOperation) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been added.");
		} else if (isUndoEditOperation) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been updated to "
					+ taskResult.getTasks().get(1).getDescription() + ".");
		} else if (isUndoClearOperation) {
			System.out.println("All tasks are restored.");
		} else if (isClearOperation) {
			System.out.println("All tasks are cleared.");
		} else if (isHelpOperation) {
		} else if (isInvalidCommand) {
			System.out.println("Invalid Command. Please try again.");
		} else if (isExitOperation) {
			System.out.println("Thank you for using MousTask. Good Bye!");
			System.exit(0);
		}
	}

	private static void displayTaskList(List<AbstractTask> taskList) {
		int totalTask = taskList.size();
		System.out
				.println("*************************************************************");
		System.out.println("[Displaying " + totalTask + " task(s)]\n");

		for (int i = 0; i < totalTask; i++) {
			boolean isTimedTask = taskList.get(i).getType()
					.equals(AbstractTask.Type.TIMED);
			boolean isFloatingTask = taskList.get(i).getType()
					.equals(AbstractTask.Type.FLOATING);
			boolean isDeadlineTask = taskList.get(i).getType()
					.equals(AbstractTask.Type.DEADLINE);
			int displayNumber = i + 1;

			if (isTimedTask) {
				TimedTask timedTaskDisplay = (TimedTask) taskList.get(i);
				displayTimedTask(timedTaskDisplay, displayNumber);
			} else if (isDeadlineTask) {
				DeadlineTask deadlineTaskDisplay = (DeadlineTask) taskList
						.get(i);
				displayDeadlineTask(deadlineTaskDisplay, displayNumber);
			} else if (isFloatingTask) {
				FloatingTask floatingTaskDisplay = (FloatingTask) taskList
						.get(i);
				displayFloatingTask(floatingTaskDisplay, displayNumber);
			}
		}
	}

	private static void displayTimedTask(TimedTask timedTaskDisplay,
			int displayNumber) {
		showDivider();
		System.out.println("Task #" + displayNumber);
		displayWrapText(timedTaskDisplay.getDescription());
		showLine();
		if (!timedTaskDisplay.getVenue().isEmpty()) {
			String venue = "Venue: " + timedTaskDisplay.getVenue();
			displayWrapText(venue);
			showLine();
		}
		System.out.println("From: " + timedTaskDisplay.getStartDate());
		showLine();
		System.out.println("To: " + timedTaskDisplay.getEndDate());
		showLine();
		System.out.println("Status: " + timedTaskDisplay.getStatus());
		showDivider();
		System.out.println("\n");
	}

	private static void displayDeadlineTask(DeadlineTask deadlineTaskDisplay,
			int displayNumber) {
		showDivider();
		System.out.println("Task #" + displayNumber);
		displayWrapText(deadlineTaskDisplay.getDescription());
		showLine();
		if (!deadlineTaskDisplay.getVenue().isEmpty()) {
			String venue = "Venue: " + deadlineTaskDisplay.getVenue();
			displayWrapText(venue);
			showLine();
		}
		System.out.println("Deadline: " + deadlineTaskDisplay.getEndDate());
		showLine();
		System.out.println("Status: " + deadlineTaskDisplay.getStatus());
		showDivider();
		System.out.println("\n");
	}

	private static void displayFloatingTask(FloatingTask floatingTaskDisplay,
			int displayNumber) {
		showDivider();
		System.out.println("Task #" + displayNumber);
		displayWrapText(floatingTaskDisplay.getDescription());
		showLine();
		if (!floatingTaskDisplay.getVenue().isEmpty()) {
			String venue = "Venue: " + floatingTaskDisplay.getVenue();
			displayWrapText(venue);
			showLine();
		}
		System.out.println("Status: " + floatingTaskDisplay.getStatus());
		showDivider();
		System.out.println("\n");
	}

	private static void showLine() {
		System.out
				.println("-------------------------------------------------------------");
	}

	private static void showDivider() {
		System.out
				.println("=============================================================");
	}

	private static void displayWrapText(String message) {
		int initial = 0;
		int charLimit = 60;
		while (initial < message.length() - 1) {
			if (message.length() - 1 - initial > charLimit) {
				System.out.println(message.substring(initial, initial
						+ charLimit));
			} else {
				System.out
						.println(message.substring(initial, message.length()));
			}
			initial += charLimit;
		}
	}
}