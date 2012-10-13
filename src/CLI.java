import java.util.List;
import java.util.Scanner;

class CLI {
	private Logic logicHandler;
	private TypeTaskPair taskResult;
	private Scanner userInput = new Scanner(System.in);

	public CLI() {
		logicHandler = new Logic();
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
			System.out.println("[MousTask User Guide]");
		} else if (isInvalidCommand) {
			System.out.println("Invalid Command. Please try again.");
		}
	}

	private static void displayTaskList(List<AbstractTask> taskList) {
		int totalTask = taskList.size();
		System.out.println("[Displaying " + totalTask + " task(s)]");

		for (int i = 0; i < totalTask; i++) {
			boolean isTimedTask = taskList.get(i).getType()
					.equals(AbstractTask.Type.TIMED);
			boolean isFloatingTask = taskList.get(i).getType()
					.equals(AbstractTask.Type.FLOAT);
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
		System.out.println(displayNumber + ". "
				+ timedTaskDisplay.getDescription() + " | Venue: "
				+ timedTaskDisplay.getVenue());
		System.out.println("Category: " + timedTaskDisplay.getType()
				+ " | Status: " + timedTaskDisplay.getStatus());
		System.out.println("Start: " + timedTaskDisplay.getStartDate()
				+ " | End: " + timedTaskDisplay.getEndDate() + "\n");
	}

	private static void displayDeadlineTask(DeadlineTask deadlineTaskDisplay,
			int displayNumber) {
		System.out.println(displayNumber + ". "
				+ deadlineTaskDisplay.getDescription() + " | Venue: "
				+ deadlineTaskDisplay.getVenue());
		System.out.println("Category: " + deadlineTaskDisplay.getType()
				+ " | Status: " + deadlineTaskDisplay.getStatus());
		System.out.println("End: " + deadlineTaskDisplay.getEndDate() + "\n");
	}

	private static void displayFloatingTask(FloatingTask floatingTaskDisplay,
			int displayNumber) {
		System.out.println(displayNumber + ". "
				+ floatingTaskDisplay.getDescription() + " | Venue: "
				+ floatingTaskDisplay.getVenue());
		System.out.println("Category: " + floatingTaskDisplay.getType()
				+ " | Status: " + floatingTaskDisplay.getStatus() + "\n");
	}
}