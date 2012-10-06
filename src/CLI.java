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
		if (taskResult.getType() == taskResult.type.ADD) {
			System.out.println("Task "
					+ taskResult.getTasks().get(0).getDescription()
					+ " has been add.");
		} else if (taskResult.getType() == taskResult.type.DISPLAY) {
			if (taskResult.getTasks().isEmpty()) {
				System.out.println("There is no task.");
			} else {
				/*
				 * SUGGESTED FORMAT: System.out.println(
				 * "123. Online Assignment afj jsfjdjfsdjja | Venue: Orchard Road"
				 * );
				 * System.out.println("Categories: Floating | Status: Undone");
				 * System
				 * .out.println("Start: 2012-12-20 13:00 | End: 2012-12-20 13:00"
				 * );
				 */
				for (int i = 0; i < taskResult.getTasks().size(); i++) {
					if (taskResult.getTasks().get(i).getType() == 0) {

					} else if (taskResult.getTasks().get(i).getType() == 1) {
						System.out.println((i + 1) + ". "
								+ taskResult.getTasks().get(i).getDescription()
								+ " | Venue: "
								+ taskResult.getTasks().get(i).getVenue());
						System.out.println("Category: Floating | Status: "
								+ getStatus(taskResult.getTasks().get(i)
										.getStatus()));
					}

				}
			}
		} else if (taskResult.getType() == taskResult.type.UNDONULL) {
			System.out.println("There is nothing to undo.");
		} else if (taskResult.getType() == taskResult.type.INVALID) {
			System.out.println("Invalid Command.");
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