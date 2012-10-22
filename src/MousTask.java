class MousTask {
	private static CLI commandLine;

	private MousTask() {
		commandLine = CLI.getInstance();
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
		System.out.println("Type .help to view the user guide.");
		System.out.println("Use the search or display command first before edit or delete any tasks.");
		System.out.println("Type .exit to exit the program.\n");
	}
}