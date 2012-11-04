import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Logic {
	private static Logging logicLog = new Logging("Logic Handler");
	private static Logic logicHandler;
	private List<AbstractTask> taskList;
	private List<AbstractTask> searchOrDisplayTaskList;
	private TypeTaskPair taskResult;
	private Stack<UndoableCommand> undoStack;
	private Storage storageObject;

	private Logic() {
		taskList = new LinkedList<AbstractTask>();
		searchOrDisplayTaskList = new LinkedList<AbstractTask>();
		undoStack = new Stack<UndoableCommand>();
		storageObject = new Storage();
		try {
			taskList = storageObject.loadTaskList();
		} catch (IOException e) {
			System.out.println("Unable to load tasks from text file.");
		}
	}

	public static Logic getInstance() {
		if (logicHandler == null) {
			logicHandler = new Logic();
		}
		return logicHandler;
	}

	public TypeTaskPair processCommand(String taskMessages) {
		String commandTypeString = getFirstWord(taskMessages);
		String commandMessage = getMessage(taskMessages);
		checkIsTaskListEmpty(commandTypeString, commandMessage);
		return taskResult;
	}

	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		logicLog.addLog(Logging.LoggingLevel.INFO, "Command that user input: "
				+ commandTypeString);
		return commandTypeString;
	}

	private static String getMessage(String userCommand) {
		String arr[] = userCommand.split(" ", 2);

		if (arr.length == 1) {
			logicLog.addLog(Logging.LoggingLevel.INFO,
					"Message that user input: " + arr[0]);
			return arr[0];
		} else {
			logicLog.addLog(Logging.LoggingLevel.INFO,
					"Message that user input: " + arr[1]);
			return arr[1];
		}
	}

	private void checkIsTaskListEmpty(String commandTypeString,
			String commandMessage) {
		boolean isTaskListEmpty = taskList.isEmpty();
		boolean isCommandAdd = commandTypeString.equalsIgnoreCase(".add");
		boolean isCommandDelete = commandTypeString.equalsIgnoreCase(".delete");
		boolean isCommandEdit = commandTypeString.equalsIgnoreCase(".edit");
		boolean isCommandDisplay = commandTypeString
				.equalsIgnoreCase(".display");
		boolean isCommandSearch = commandTypeString.equalsIgnoreCase(".search");
		boolean isCommandUndo = commandTypeString.equalsIgnoreCase(".undo");
		boolean isCommandClear = commandTypeString.equalsIgnoreCase(".clear");
		boolean isCommandHelp = commandTypeString.equalsIgnoreCase(".help");
		boolean isCommandExit = commandTypeString.equalsIgnoreCase(".exit");

		// if there are no task, we only allow certain commands to work
		if (isTaskListEmpty) {
			if ((isCommandAdd) || (isCommandUndo) || (isCommandHelp)
					|| (isCommandExit)) {
				executeCommand(commandTypeString, commandMessage);
			} else if ((isCommandDelete) || (isCommandEdit)
					|| (isCommandSearch) || (isCommandDisplay)
					|| (isCommandClear)) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.EMPTY, null);
			} else {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.INVALID, null);
			}
		} else if (!isTaskListEmpty) {
			if ((isCommandAdd) || (isCommandUndo) || (isCommandHelp)
					|| (isCommandExit) || (isCommandDelete) || (isCommandEdit)
					|| (isCommandSearch) || (isCommandDisplay)
					|| (isCommandClear)) {
				checkIsSearchOrDisplayTaskListEmpty(commandTypeString,
						commandMessage);
			} else {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.INVALID, null);
			}
		}
	}

	private void checkIsSearchOrDisplayTaskListEmpty(String commandTypeString,
			String commandMessage) {
		boolean isSearchOrDisplayTaskListEmpty = searchOrDisplayTaskList
				.isEmpty();
		boolean isCommandAdd = commandTypeString.equalsIgnoreCase(".add");
		boolean isCommandDelete = commandTypeString.equalsIgnoreCase(".delete");
		boolean isCommandEdit = commandTypeString.equalsIgnoreCase(".edit");
		boolean isCommandDisplay = commandTypeString
				.equalsIgnoreCase(".display");
		boolean isCommandSearch = commandTypeString.equalsIgnoreCase(".search");
		boolean isCommandUndo = commandTypeString.equalsIgnoreCase(".undo");
		boolean isCommandClear = commandTypeString.equalsIgnoreCase(".clear");
		boolean isCommandHelp = commandTypeString.equalsIgnoreCase(".help");
		boolean isCommandExit = commandTypeString.equalsIgnoreCase(".exit");

		if (isSearchOrDisplayTaskListEmpty) {
			if ((isCommandEdit) || (isCommandDelete)) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.ERROR, null);
			} else {
				executeCommand(commandTypeString, commandMessage);
			}
		} else if (!isSearchOrDisplayTaskListEmpty) {
			if ((isCommandAdd) || (isCommandSearch) || (isCommandDisplay)
					|| (isCommandClear) || (isCommandUndo) || (isCommandHelp)
					|| (isCommandExit)) {
				// Clear the SearchOrDisplay List since it is not edit or delete
				clearSearchOrDisplayTaskList();
			}
			executeCommand(commandTypeString, commandMessage);
		}
	}

	public void clearSearchOrDisplayTaskList() {
		searchOrDisplayTaskList = new LinkedList<AbstractTask>();
	}

	private void executeCommand(String commandTypeString, String commandMessage) {
		boolean isCommandAdd = commandTypeString.equalsIgnoreCase(".add");
		boolean isCommandDelete = commandTypeString.equalsIgnoreCase(".delete");
		boolean isCommandEdit = commandTypeString.equalsIgnoreCase(".edit");
		boolean isCommandDisplay = commandTypeString
				.equalsIgnoreCase(".display");
		boolean isCommandSearch = commandTypeString.equalsIgnoreCase(".search");
		boolean isCommandUndo = commandTypeString.equalsIgnoreCase(".undo");
		boolean isCommandClear = commandTypeString.equalsIgnoreCase(".clear");
		boolean isCommandHelp = commandTypeString.equalsIgnoreCase(".help");
		boolean isCommandExit = commandTypeString.equalsIgnoreCase(".exit");

		logicLog.addLog(Logging.LoggingLevel.INFO, "Execute "
				+ commandTypeString + " function");

		if (isCommandAdd) {
			processAddCommand(commandMessage);
		} else if (isCommandDelete) {
			prcoessDeleteCommand(commandMessage);
		} else if (isCommandEdit) {
			processEditCommand(commandMessage);
		} else if (isCommandDisplay) {
			searchOrDisplayTaskList = taskList;
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DISPLAY, taskList);
		} else if (isCommandSearch) {
			processSearchCommand(commandMessage);
		} else if (isCommandUndo) {
			processUndoCommand();
		} else if (isCommandClear) {
			processClearCommand();
		} else if (isCommandHelp) {
			processHelpCommand();
		} else if (isCommandExit) {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.EXIT, null);
		} else {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.INVALID, null);
		}
	}

	private void processAddCommand(String commandMessage) {
		try {
			Add addObject = new Add(commandMessage);
			List<AbstractTask> addResult = addObject.execute(taskList);
			undoStack.push(addObject);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.ADD, addResult);
			storageObject.writeTaskList(taskList);
		} catch (IOException e) {
			System.out
					.println("MousTask Error: Unable to write task to the text file.");
		}
	}

	private void prcoessDeleteCommand(String commandMessage) {
		try {
			int index = Integer.parseInt(commandMessage);
			Delete deleteObject = new Delete(searchOrDisplayTaskList, index);
			List<AbstractTask> deleteResult = deleteObject.execute(taskList);
			undoStack.push(deleteObject);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE,
					deleteResult);
			storageObject.writeTaskList(taskList);
		} catch (NumberFormatException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE, null);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE, null);
		} catch (IllegalArgumentException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE, null);
		} catch (IOException e) {
			System.out
					.println("MousTask Error: Unable to remove task from the text file.");
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE, null);
		}
	}

	private void processEditCommand(String commandMessage) {
		try {
			String indexString = getFirstWord(commandMessage);
			String editParameter = getMessage(commandMessage);
			int index = Integer.parseInt(indexString);
			Edit editObject = new Edit(searchOrDisplayTaskList, index,
					editParameter);
			List<AbstractTask> returnEditedTask = editObject.execute(taskList);
			undoStack.push(editObject);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT,
					returnEditedTask);
			storageObject.writeTaskList(taskList);
		} catch (NumberFormatException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT, null);
		} catch (IllegalArgumentException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT, null);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT, null);
		} catch (IOException e) {
			System.out.println("MousTask Error: Unable to update text file.");
			taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT, null);
		}
	}

	private void processSearchCommand(String commandMessage) {
		try {
			Search searchObject = new Search(commandMessage);
			searchOrDisplayTaskList = searchObject.execute(taskList);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH,
					searchOrDisplayTaskList);
		} catch (NullPointerException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH, null);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH, null);
		} catch (IllegalArgumentException e) {
			System.out.println("MousTask Error: " + e.getMessage());
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH, null);
		}
	}

	private void processUndoCommand() {
		if (undoStack.isEmpty()) {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDONULL, null);
		} else {
			UndoableCommand undoableCommandObject = undoStack.peek();
			List<AbstractTask> undoResult = undoStack.pop().undo();
			if (undoableCommandObject instanceof Add) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDOADD,
						undoResult);
			} else if (undoableCommandObject instanceof Delete) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDODELETE,
						undoResult);
			} else if (undoableCommandObject instanceof Edit) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDOEDIT,
						undoResult);
			} else if (undoableCommandObject instanceof Clear) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDOCLEAR, null);
			}
			try {
				storageObject.writeTaskList(taskList);
			} catch (IOException e) {
				System.out
						.println("MousTask Error: Unable to update text file.");
			}
		}
	}

	private void processClearCommand() {
		Clear clearObject = new Clear();
		clearObject.execute(taskList);
		undoStack.push(clearObject);
		taskResult = new TypeTaskPair(TypeTaskPair.Type.CLEAR, null);
		try {
			storageObject.writeTaskList(taskList);
		} catch (IOException e) {
			System.out.println("MousTask Error: Unable to clear text file.");
		}
	}

	private void processHelpCommand() {
		Help helpObject = new Help();
		helpObject.execute();
		taskResult = new TypeTaskPair(TypeTaskPair.Type.HELP, null);
	}
}