//@author A0058657N
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

	private static final String MOUSTASK_IOEXCEPTION_MESSAGE = "MousTask has encountered a critical error: Unable to load from/update to text file.";

	private Logic() {
		taskList = new LinkedList<AbstractTask>();
		searchOrDisplayTaskList = new LinkedList<AbstractTask>();
		undoStack = new Stack<UndoableCommand>();
		storageObject = new Storage();
		try {
			taskList = storageObject.loadTaskList();
		} catch (IOException e) {
			processIOExceptionError();
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
		logicLog.addLog(Logging.LoggingLevel.INFO,
				"Logic(): Command that user input: " + commandTypeString);
		return commandTypeString;
	}

	private static String getMessage(String userCommand) {
		String arr[] = userCommand.split(" ", 2);
		boolean isArrayLengthOne = arr.length == 1;

		if (isArrayLengthOne) {
			logicLog.addLog(Logging.LoggingLevel.WARNING,
					"Logic(): Message that user input: \"\"");
			return arr[0];
		} else {
			logicLog.addLog(Logging.LoggingLevel.INFO,
					"Logic(): Message that user input: " + arr[1]);
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

		logicLog.addLog(Logging.LoggingLevel.INFO, "Logic(): Execute "
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

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Add Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
	private void processAddCommand(String commandMessage) {
		try {
			Add addObject = new Add(commandMessage);
			List<AbstractTask> addResult = addObject.execute(taskList);
			undoStack.push(addObject);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.ADD, addResult);
			storageObject.writeTaskList(taskList);
		} catch (IOException e) {
			processIOExceptionError();
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Delete Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
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
			prcoessDeleteCommandError();
		} catch (IndexOutOfBoundsException e) {
			prcoessDeleteCommandError();
		} catch (IllegalArgumentException e) {
			prcoessDeleteCommandError();
		} catch (IOException e) {
			processIOExceptionError();
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE, null);
		}
	}

	private void prcoessDeleteCommandError() {
		System.out
				.println("You have entered an invalid Task Number.\nPlease enter a Task Number within the Total Tasks Range:\n.delete <Task Number>\n[Total Tasks Range: 1 - "
						+ taskList.size() + "]");
		taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE, null);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Edit Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
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
			processEditCommandError();
		} catch (IndexOutOfBoundsException e) {
			processEditCommandError();
		} catch (IllegalArgumentException e) {
			processEditCommandError();
		} catch (IOException e) {
			processIOExceptionError();
			taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT, null);
		}
	}

	private void processEditCommandError() {
		System.out
				.println("You have entered an invalid Task Number.\nPlease enter a Task Number within the Total Tasks Range:\n.edit <Task Number> ...\n[Total Tasks Range: 1 - "
						+ taskList.size() + "]");
		taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT, null);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Search Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
	private void processSearchCommand(String commandMessage) {
		try {
			Search searchObject = new Search(commandMessage);
			searchOrDisplayTaskList = searchObject.execute(taskList);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH,
					searchOrDisplayTaskList);
		} catch (NullPointerException e) {
			System.out.println("Your search line cannot be empty.");
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH, null);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out
					.println("You have entered an invalid Day/Date.\nPlease enter a valid Day/Date.\nDay Format Eg: 7\nDate Format Eg: 2012-12-21 12:21");
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH, null);
		} catch (IllegalArgumentException e) {
			System.out
					.println("You have entered an invalid Date.\nPlease enter a valid Date.\nDate Format Eg: 2012-12-21 12:21");
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH, null);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Undo Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
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
				processIOExceptionError();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Clear Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
	private void processClearCommand() {
		Clear clearObject = new Clear();
		clearObject.execute(taskList);
		undoStack.push(clearObject);
		taskResult = new TypeTaskPair(TypeTaskPair.Type.CLEAR, null);
		try {
			storageObject.writeTaskList(taskList);
		} catch (IOException e) {
			processIOExceptionError();
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////// Help Operation
	// ////////////////////////////////////////////////////////////////////////////////////////
	private void processHelpCommand() {
		Help helpObject = new Help();
		helpObject.execute();
		taskResult = new TypeTaskPair(TypeTaskPair.Type.HELP, null);
	}

	private static void processIOExceptionError() {
		System.out.println(MOUSTASK_IOEXCEPTION_MESSAGE);
		logicLog.addLog(Logging.LoggingLevel.SEVERE,
				"Logic(): Unable to load from/update to text file.");
	}
}