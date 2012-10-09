import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Logic {
	private List<AbstractTask> taskList;
	private List<AbstractTask> searchOrDisplayTaskList;
	private TypeTaskPair taskResult;
	private Stack<UndoableCommand> undoStack;

	// private Storage storageObject;

	public Logic() {
		taskList = new LinkedList<AbstractTask>();
		searchOrDisplayTaskList = new LinkedList<AbstractTask>();
		undoStack = new Stack<UndoableCommand>();
		// storageObject = new Storage();
		// taskList = storageObject.loadTaskList();
	}

	public TypeTaskPair processCommand(String taskMessages) {
		String commandTypeString = getFirstWord(taskMessages);
		String commandMessage = getMessage(taskMessages);
		checkIsTaskListEmpty(commandTypeString, commandMessage);
		return taskResult;
	}

	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}

	private static String getMessage(String userCommand) {
		String checkCommandType = userCommand.replace(
				getFirstWord(userCommand), "").trim();
		return checkCommandType;
	}

	private void checkIsTaskListEmpty(String commandTypeString,
			String commandMessage) {
		boolean isTaskListEmpty = taskList.isEmpty();
		boolean isCommandAdd = commandTypeString.equalsIgnoreCase(".add");
		boolean isCommandDelete = commandTypeString.equalsIgnoreCase(".delete");
		boolean isCommandEdit = commandTypeString.equalsIgnoreCase(".edit");
		boolean isCommandDisplay = commandTypeString
				.equalsIgnoreCase(".display");
		boolean isCommandSearch = commandTypeString.equalsIgnoreCase(".serch");
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
			}
		} else if (!isTaskListEmpty) {
			checkIsSearchOrDisplayTaskListEmpty(commandTypeString,
					commandMessage);
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
		boolean isCommandSearch = commandTypeString.equalsIgnoreCase(".serch");
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
				searchOrDisplayTaskList = new LinkedList<AbstractTask>();
			}
			executeCommand(commandTypeString, commandMessage);
		}
	}

	private void executeCommand(String commandTypeString, String commandMessage) {
		if (commandTypeString.equalsIgnoreCase(".add")) {
			Add addObject = new Add(commandMessage);
			List<AbstractTask> addResult = addObject.execute(taskList);
			undoStack.push(addObject);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.ADD, addResult);
			// storageObject.writeTaskList();
		} else if (commandTypeString.equalsIgnoreCase(".delete")) {
			try {
				int index = Integer.parseInt(commandMessage);
				Delete deleteObject = new Delete(searchOrDisplayTaskList, index);
				List<AbstractTask> deleteResult = deleteObject
						.execute(taskList);
				taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE,
						deleteResult);
				// storageObject.writeTaskList();
			} catch (NumberFormatException e) {
				taskResult = new TypeTaskPair(null, null);
				System.out.println("Error - " + e.getMessage());
			}
		} else if (commandTypeString.equalsIgnoreCase(".edit")) {
			try {
				String indexString = getFirstWord(commandMessage);
				String editParameter = getMessage(commandMessage);
				int index = Integer.parseInt(indexString);
				Edit editObject = new Edit(searchOrDisplayTaskList, index,
						editParameter);
				List<AbstractTask> returnEditedTask = editObject
						.execute(taskList);
				taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT,
						returnEditedTask);
				// storageObject.writeTaskList();
			} catch (NumberFormatException e) {
				taskResult = new TypeTaskPair(null, null);
				System.out.println("Error - " + e.getMessage());
			}
		} else if (commandTypeString.equalsIgnoreCase(".display")) {
			searchOrDisplayTaskList = taskList;
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DISPLAY, taskList);
		} else if (commandTypeString.equalsIgnoreCase(".search")) {
			// Search searchObject = new Search();
			// searchOrDisplayTaskList = searchObject.execute(commandMessage);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH,
					searchOrDisplayTaskList);
		} else if (commandTypeString.equalsIgnoreCase(".undo")) {
			if (undoStack.isEmpty()) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDONULL, null);
			} else {
				UndoableCommand undoableCommandObject = undoStack.peek();
				undoStack.pop().undo();
				if (undoableCommandObject instanceof Add) {
					taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDOADD,
							null);
				} else if (undoableCommandObject instanceof Delete) {
					taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDODELETE,
							null);
				} else if (undoableCommandObject instanceof Edit) {
					taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDOEDIT,
							null);
				}/*
				 * else if (undoableCommandObject instanceof Clear) { taskResult
				 * = new TypeTaskPair(TypeTaskPair.Type.UNDOCLEAR, null); }
				 */
			}
		} else if (commandTypeString.equalsIgnoreCase(".clear")) {
			// Clear clearObject = new Clear(taskList);
			// clearObject.execute();
			taskResult = new TypeTaskPair(TypeTaskPair.Type.CLEAR, null);
			// storageObject.writeTaskList();
		} else if (commandTypeString.equalsIgnoreCase(".help")) {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.HELP, null);
		} else if (commandTypeString.equalsIgnoreCase(".exit")) {
			System.exit(0);
		} else {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.INVALID, null);
		}

		// DEBUG: TASK LIST MEMORY VIEW
		/**
		 * System.out.println("TASK LIST MEMORY VIEW: "); for (int i = 0; i <
		 * taskList.size(); i++) {
		 * System.out.println(taskList.get(i).getDescription()); }
		 * System.out.println("// END OF TASK LIST MEMORY VIEW //");
		 */
	}
}