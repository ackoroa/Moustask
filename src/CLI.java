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
		if (taskResult.getType() == taskResult.type.EMPTY) {
			System.out.println("Your task list is empty.");
		} else if (taskResult.getType() == taskResult.type.ERROR) {
			System.out
					.println("You must perform a search or display command first.");
		} else if (taskResult.getType() == taskResult.type.ADD) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been add.");
		} else if (taskResult.getType() == taskResult.type.DELETE) {
			System.out.println("Your task list is deleted.");
			displayTaskList(taskResult.getTasks());
		} else if (taskResult.getType() == taskResult.type.EDIT) {
			System.out.println("Your task list has been changed.");
			displayTaskList(taskResult.getTasks());
		} else if (taskResult.getType() == taskResult.type.SEARCH) {
			if (taskResult.getTasks().isEmpty()) {
				System.out.println("Your search has returned 0 result.");
			} else {
				displayTaskList(taskResult.getTasks());
			}
		} else if (taskResult.getType() == taskResult.type.DISPLAY) {
			displayTaskList(taskResult.getTasks());
		} else if (taskResult.getType() == taskResult.type.CLEAR) {
			System.out.println("All events are cleared.");
		} else if (taskResult.getType() == taskResult.type.UNDONULL) {
			System.out.println("There is nothing to undo.");
		} else if (taskResult.getType() == taskResult.type.HELP) {
			System.out.println("PRINT HELP INFORMATION");
		} else if (taskResult.getType() == taskResult.type.INVALID) {
			System.out.println("Invalid Command.");
		}
	}

	private static void displayTaskList(List<AbstractTask> taskList) {
		/*
		 * SUGGESTED FORMAT: System.out.println(
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