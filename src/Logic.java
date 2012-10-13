import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Logic {
	private List<AbstractTask> taskList;
	private List<AbstractTask> searchOrDisplayTaskList;
	private TypeTaskPair taskResult;
	private Stack<UndoableCommand> undoStack;
	private Storage storageObject;

	public Logic() {
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

		if (isCommandAdd) {
			Add addObject = new Add(commandMessage);
			List<AbstractTask> addResult = addObject.execute(taskList);
			undoStack.push(addObject);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.ADD, addResult);
			try {
				storageObject.writeTaskList(taskList);
			} catch (IOException e) {
				System.out.println("Unable to write task to the text file.");
			}
		} else if (isCommandDelete) {
			try {
				int index = Integer.parseInt(commandMessage);
				Delete deleteObject = new Delete(searchOrDisplayTaskList, index);
				List<AbstractTask> deleteResult = deleteObject
						.execute(taskList);
				undoStack.push(deleteObject);
				taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE,
						deleteResult);
				try {
					storageObject.writeTaskList(taskList);
				} catch (IOException e) {
					System.out
							.println("Unable to remove task from the text file.");
				}
			} catch (NumberFormatException e) {
				taskResult = new TypeTaskPair(null, null);
				System.out.println("Error - " + e.getMessage());
			}
		} else if (isCommandEdit) {
			try {
				String indexString = getFirstWord(commandMessage);
				String editParameter = getMessage(commandMessage);
				int index = Integer.parseInt(indexString);
				Edit editObject = new Edit(searchOrDisplayTaskList, index,
						editParameter);
				List<AbstractTask> returnEditedTask = editObject
						.execute(taskList);
				undoStack.push(editObject);
				taskResult = new TypeTaskPair(TypeTaskPair.Type.EDIT,
						returnEditedTask);
				try {
					storageObject.writeTaskList(taskList);
				} catch (IOException e) {
					System.out
							.println("Unable to update task to the text file.");
				}
			} catch (NumberFormatException e) {
				taskResult = new TypeTaskPair(null, null);
				System.out.println("Error - " + e.getMessage());
			}
		} else if (isCommandDisplay) {
			searchOrDisplayTaskList = taskList;
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DISPLAY, taskList);
		} else if (isCommandSearch) {
			Search searchObject = new Search(commandMessage);
			searchOrDisplayTaskList = searchObject.execute(taskList);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH,
					searchOrDisplayTaskList);
		} else if (isCommandUndo) {
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
					taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDOCLEAR,
							null);
				}
				try {
					storageObject.writeTaskList(taskList);
				} catch (IOException e) {
					System.out
							.println("Unable to update task to the text file.");
				}
			}
		} else if (isCommandClear) {
			Clear clearObject = new Clear();
			clearObject.execute(taskList);
			undoStack.push(clearObject);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.CLEAR, null);
			try {
				storageObject.writeTaskList(taskList);
			} catch (IOException e) {
				System.out.println("Unable to clear task from the text file.");
			}
		} else if (isCommandHelp) {
			Help helpObject = new Help();
			helpObject.execute();
			taskResult = new TypeTaskPair(TypeTaskPair.Type.HELP, null);
		} else if (isCommandExit) {
			System.exit(0);
		} else {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.INVALID, null);
		}
	}
}