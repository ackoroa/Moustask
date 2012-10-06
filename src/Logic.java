import java.util.LinkedList;
import java.util.List;

public class Logic {
	List<AbstractTask> taskList; // the virtual list of tasks
	private TypeTaskPair taskResult;

	public Logic() {
		taskList = new LinkedList<AbstractTask>();
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
			List<AbstractTask> addResult;
			Add addObject = new Add(commandMessage);
			addResult = addObject.execute(taskList);
			taskResult = new TypeTaskPair(TypeTaskPair.Type.ADD, addResult);
		} else if (commandTypeString.equalsIgnoreCase(".delete")) {

		} else if (commandTypeString.equalsIgnoreCase(".edit")) {

		} else if (commandTypeString.equalsIgnoreCase(".search")) {

		} else if (commandTypeString.equalsIgnoreCase(".clear")) {

		} else if (commandTypeString.equalsIgnoreCase(".display")) {

		} else if (commandTypeString.equalsIgnoreCase(".help")) {

		} else if (commandTypeString.equalsIgnoreCase(".exit")) {
			System.exit(0);
		} else {
			taskResult = new TypeTaskPair(TypeTaskPair.Type.INVALID, null);
		}
		
		// DEBUG PRINT
		System.out.println("//DEBUG\nThings in memory: ");
		for(int i=0;i<taskList.size();i++) {
			System.out.println(taskList.get(i).getDescription());
			System.out.println("//END OF DEBUG.");
		}
	}
}