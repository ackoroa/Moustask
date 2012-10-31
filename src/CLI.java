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
			processAddOperation(taskResult);
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
			processSearchOperation(taskResult);
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

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Add Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
	private static void processAddOperation(TypeTaskPair taskResult) {
		boolean isAddResultEmpty = taskResult.getTasks().isEmpty();
		if (isAddResultEmpty) {
			System.out.println("Invalid Syntax. Please try again.");
		} else {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been added.");
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Search Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
	private static void processSearchOperation(TypeTaskPair taskResult) {
		boolean isSearchResultInvalid = taskResult.getTasks() != null;
		if (isSearchResultInvalid) {
			boolean isSearchResultEmpty = taskResult.getTasks().isEmpty();
			if (isSearchResultEmpty) {
				System.out.println("Your search has returned no result.");
			} else {
				displayTaskList(taskResult.getTasks());
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Display Operations
	// ////////////////////////////////////////////////////////////////////////////////////////
	private static void displayTaskList(List<AbstractTask> taskList) {
		int totalNumberOfTask = taskList.size();
		System.out
				.println("*************************************************************");
		System.out.println("[Displaying " + totalNumberOfTask + " task(s)]\n");

		for (int i = 0; i < totalNumberOfTask; i++) {
			showDivider();
			showDetailedTaskInfo(taskList, i);
			showDivider();
			System.out.println("\n");
		}
	}

	private static void showDetailedTaskInfo(List<AbstractTask> taskList,
			int index) {
		boolean isTimedTask = taskList.get(index).getType()
				.equals(AbstractTask.Type.TIMED);
		boolean isDeadlineTask = taskList.get(index).getType()
				.equals(AbstractTask.Type.DEADLINE);
		int displayNumber = index + 1;

		System.out.println("Task #" + displayNumber);
		displayWrapText(taskList.get(index).getDescription());
		showLine();
		if (!taskList.get(index).getVenue().isEmpty()) {
			String venue = "Venue: " + taskList.get(index).getVenue();
			displayWrapText(venue);
			showLine();
		}
		if (isTimedTask) {
			System.out.println("From: "
					+ ((TimedTask) taskList.get(index)).getStartDate());
			showLine();
		}
		if (isTimedTask) {
			System.out.println("To: "
					+ ((TimedTask) taskList.get(index)).getEndDate());
			showLine();
		} else if (isDeadlineTask) {
			System.out.println("To: "
					+ ((DeadlineTask) taskList.get(index)).getEndDate());
			showLine();
		}
		System.out.println("Status: " + taskList.get(index).getStatus());
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