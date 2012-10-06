import java.util.List;
import java.util.Vector;

public class Add implements UndoableCommand {
	private String addMessage;

	public Add(String commandMessage) {
		addMessage = commandMessage;
	}

	public List<AbstractTask> execute(List<AbstractTask> WholeTaskList) {
		/*
		 * extracting keywords from messages and add them to a vector
		 * check for duplicates of keywords
		 * check for key keyword and determine if it is timed, deadline or floating tasks
		 * if keyword is deadline, make sure that it has .from and .to and create deadline object to store the attributes
		 *
		 */
		List<AbstractTask> addResult = new Vector<AbstractTask>();
		if (addMessage.contains(".at")) {
			String event = getEvent(addMessage);
			String venue = getVenue(addMessage);
			FloatingTask floatTaskObject = new FloatingTask(event, venue);
			addResult.add(floatTaskObject);
			WholeTaskList.add(floatTaskObject);
		}
		return addResult;
	}

	private static String getEvent(String addMessage) {
		String commandTypeString = addMessage.trim().split(".at")[0];
		return commandTypeString;
	}

	private static String getVenue(String addMessage) {
		String checkCommandType = addMessage.replace(getEvent(addMessage), "")
				.trim();
		return checkCommandType;
	}

	public List<AbstractTask> undo() {
		return null;
	}
}