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
		// taskList = storageObject.load();
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
		if (taskList.isEmpty()) {
			if ((commandTypeString.equalsIgnoreCase(".add"))
					|| (commandTypeString.equalsIgnoreCase(".undo"))
					|| (commandTypeString.equalsIgnoreCase(".help"))
					|| (commandTypeString.equalsIgnoreCase(".exit"))) {
				executeCommand(commandTypeString, commandMessage);
			} else if ((commandTypeString.equalsIgnoreCase(".delete"))
					|| (commandTypeString.equalsIgnoreCase(".edit"))
					|| (commandTypeString.equalsIgnoreCase(".search"))
					|| (commandTypeString.equalsIgnoreCase(".display"))
					|| (commandTypeString.equalsIgnoreCase(".clear"))) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.EMPTY, null);
			}
		} else if (!taskList.isEmpty()) {
			checkIsSearchOrDisplayTaskListEmpty(commandTypeString,
					commandMessage);
		}
	}

	private void checkIsSearchOrDisplayTaskListEmpty(String commandTypeString,
			String commandMessage) {
		if (searchOrDisplayTaskList.isEmpty()) {
			if ((commandTypeString.equalsIgnoreCase(".edit"))
					|| (commandTypeString.equalsIgnoreCase(".delete"))) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.ERROR, null);
			} else {
				executeCommand(commandTypeString, commandMessage);
			}
		} else if (!searchOrDisplayTaskList.isEmpty()) {
			if ((commandTypeString.equalsIgnoreCase(".add"))
					|| (commandTypeString.equalsIgnoreCase(".search"))
					|| (commandTypeString.equalsIgnoreCase(".display"))
					|| (commandTypeString.equalsIgnoreCase(".clear"))
					|| (commandTypeString.equalsIgnoreCase(".undo"))
					|| (commandTypeString.equalsIgnoreCase(".help"))
					|| (commandTypeString.equalsIgnoreCase(".exit"))) {
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
		} else if (commandTypeString.equalsIgnoreCase(".delete")) {
			try {
				int index = Integer.parseInt(commandMessage);
				Delete deleteObject = new Delete(searchOrDisplayTaskList, index);
				List<AbstractTask> deleteResult = deleteObject
						.execute(taskList);
				taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE,
						deleteResult);
			} catch (NumberFormatException e) {
				taskResult = new TypeTaskPair(null, null);
				System.out.println("Error - " + e.getMessage());
			} catch (IndexOutOfBoundsException e) {
				taskResult = new TypeTaskPair(null, null);
				System.out.println("Error - " + e.getMessage());
			}
		} else if (commandTypeString.equalsIgnoreCase(".edit")) {
			try {
				int index = Integer.parseInt(commandMessage);
				Edit editObject = new Edit(searchOrDisplayTaskList, index);
				List<AbstractTask> editResult = editObject.execute(taskList);
				taskResult = new TypeTaskPair(TypeTaskPair.Type.DELETE,
						editResult);
			} catch (NumberFormatException e) {
				taskResult = new TypeTaskPair(null, null);
				System.out.println("Error - " + e.getMessage());
			} catch (IndexOutOfBoundsException e) {
				taskResult = new TypeTaskPair(null, null);
				System.out.println("Error - " + e.getMessage());
			}
		} else if (commandTypeString.equalsIgnoreCase(".search")) {
			// Search searchObject = new Search();
			// searchOrDisplayTaskList = searchObject.execute(commandMessage);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.SEARCH,
					searchOrDisplayTaskList);
		} else if (commandTypeString.equalsIgnoreCase(".display")) {
			searchOrDisplayTaskList = taskList;
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DISPLAY, taskList);
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