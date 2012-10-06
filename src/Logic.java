import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Logic {
	private List<AbstractTask> taskList;
	private TypeTaskPair taskResult;
	private Stack<Command> undoStack;
	//private Storage storageObject;

	public Logic() {
		taskList = new LinkedList<AbstractTask>();
		undoStack = new Stack<Command>();
		//storageObject = new Storage();
	}

	public TypeTaskPair processCommand(String taskMessages) {
		String commandTypeString = getFirstWord(taskMessages);
		String commandMessage = getMessage(taskMessages);
		checkCommandType(commandTypeString, commandMessage);
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

	private void checkCommandType(String commandTypeString,
			String commandMessage) {
		if (commandTypeString.equalsIgnoreCase(".add")) {
			Add addObject = new Add(commandMessage);
			List<AbstractTask> addResult = addObject.execute(taskList);
			undoStack.push(addObject);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.ADD, addResult);
		} else if (commandTypeString.equalsIgnoreCase(".delete")) {

		} else if (commandTypeString.equalsIgnoreCase(".edit")) {

		} else if (commandTypeString.equalsIgnoreCase(".search")) {

		} else if (commandTypeString.equalsIgnoreCase(".clear")) {

		} else if (commandTypeString.equalsIgnoreCase(".display")) {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.DISPLAY, taskList);
		}else if (commandTypeString.equalsIgnoreCase(".undo")) {
			if(undoStack.isEmpty()) {
				taskResult = new TypeTaskPair(TypeTaskPair.Type.UNDONULL, null);
			} else {
				//taskResult = new TypeTaskPair(TypeTaskPair.Type., taskList);
			}
		}
		else if (commandTypeString.equalsIgnoreCase(".help")) {

		} else if (commandTypeString.equalsIgnoreCase(".exit")) {
			System.exit(0);
		} else {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.INVALID, null);
		}

		// DEBUG: TASK LIST MEMORY VIEW
		/**
		 System.out.println("TASK LIST MEMORY VIEW: ");
		 for (int i = 0; i < taskList.size(); i++) {
		 	System.out.println(taskList.get(i).getDescription());
		 }
		 System.out.println("// END OF TASK LIST MEMORY VIEW //");
		 */
	}
}