class MousTask {
	private CLI commandLine;

	private MousTask() {
		commandLine = new CLI();
	}

	private void startProgram() {
		commandLine.getUserInput();
	}

	public static void main(String[] args) {
		MousTask moustaskObject = new MousTask();
		moustaskObject.showWelcomeMessage();
		moustaskObject.startProgram();
	}

	private void showWelcomeMessage() {
		System.out.println("Welcome to MousTask.");
		System.out.println("Type .help to view all commands.");
		System.out.println("Please use the search or display command first before edit or delete any tasks.");
		System.out.println("Type .exit to end the program.\n");
	}
}