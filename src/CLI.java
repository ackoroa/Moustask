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
			System.out.println("Task " + taskResult.getTasks().get(0).getDescription() + " has been add.");
		} else if (taskResult.getType() == taskResult.type.INVALID) {
			System.out.println("Invalid Command.");
		}
	}
}