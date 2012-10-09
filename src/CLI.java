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
		if (taskResult.getType() == TypeTaskPair.Type.EMPTY) {
			System.out.println("Your task list is currently empty.");
		} else if (taskResult.getType() == TypeTaskPair.Type.ERROR) {
			System.out
					.println("You must use a search or display command first.");
		} else if (taskResult.getType() == TypeTaskPair.Type.ADD) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been add.");
		} else if (taskResult.getType() == TypeTaskPair.Type.DELETE) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been deleted.");
			displayTaskList(taskResult.getTasks());
		} else if (taskResult.getType() == TypeTaskPair.Type.EDIT) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been update.");
			displayTaskList(taskResult.getTasks());
		} else if (taskResult.getType() == TypeTaskPair.Type.SEARCH) {
			if (taskResult.getTasks().isEmpty()) {
				System.out.println("Your search has returned no result.");
			} else {
				displayTaskList(taskResult.getTasks());
			}
		} else if (taskResult.getType() == TypeTaskPair.Type.DISPLAY) {
			displayTaskList(taskResult.getTasks());
		} else if (taskResult.getType() == TypeTaskPair.Type.UNDONULL) {
			System.out.println("There is nothing to undo.");
		} else if (taskResult.getType() == TypeTaskPair.Type.CLEAR) {
			System.out.println("All tasks are cleared.");
		} else if (taskResult.getType() == TypeTaskPair.Type.HELP) {
			System.out.println("PRINT HELP INFORMATION...");
		} else if (taskResult.getType() == TypeTaskPair.Type.INVALID) {
			System.out.println("Invalid Command. Please try again.");
		}
	}

	private static void displayTaskList(List<AbstractTask> taskList) {
		/*
		 * CURRENT FORMAT: System.out.println(
		 * "123. Online Assignment afj jsfjdjfsdjja | Venue: Orchard Road" );
		 * System.out.println("Category: Floating | Status: Undone"); System
		 * .out.println("Start: 2012-12-20 13:00 | End: 2012-12-30 00:00" );
		 */
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getType().equals(AbstractTask.TIMED)) {
				/**
				 * System.out.println((i + 1) + ". " +
				 * taskList.get(i).getDescription() + " | Venue: " +
				 * taskList.get(i).getVenue());
				 * System.out.println("Category: Floating | Status: " +
				 * getStatus(taskList.get(i).getStatus()));
				 * System.out.println("Start: " + taskList.get(i).getStartDate()
				 * + " | End: " + taskList.get(i).getEndDate());
				 */
			} else if (taskList.get(i).getType().equals(AbstractTask.FLOAT)) {
				System.out.println((i + 1) + ". "
						+ taskList.get(i).getDescription() + " | Venue: "
						+ taskList.get(i).getVenue());
				System.out.println("Category: Floating | Status: "
						+ getStatus(taskList.get(i).getStatus()));
			} else if (taskList.get(i).getType().equals(AbstractTask.DEADLINE)) {
				/**
				 * System.out.println((i + 1) + ". " +
				 * taskList.get(i).getDescription() + " | Venue: " +
				 * taskList.get(i).getVenue());
				 * System.out.println("Category: Floating | Status: " +
				 * getStatus(taskList.get(i).getStatus()));
				 * System.out.println("End: " + taskList.get(i).getEndDate());
				 */
			}
		}
	}

	private static String getStatus(int taskStatusNumber) {
		switch (taskStatusNumber) {
		case 0:
			return "Undone";
		case 1:
			return "Done";
		case 2:
			return "Impossible";
		default:
			return "Unknown";
		}
	}
}