import java.util.List;
import java.util.Vector;

public class Add implements UndoableCommand {
	private List<AbstractTask> wholeTaskList;
	private String messageToAdd;
	private AbstractTask taskToAdd;

	public Add(String commandMessage) {
		messageToAdd = commandMessage;
	}

	public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
		String[] addTokenArray;
		List<String> addTokenList = new Vector<String>();
		this.wholeTaskList = wholeTaskList;

		addTokenArray = messageToAdd.replaceAll("(\\.\\w+)",
				"DELIMITER$1DELIMITER").split("DELIMITER");

		for (int i = 0; i < addTokenArray.length; i++) {
			addTokenList.add(addTokenArray[i].trim());
		}
		/*
		 * extracting keywords from messages and add them to a vector check for
		 * correct keyword and duplicates of keywords check for key keyword and
		 * determine if it is timed, deadline or floating tasks if keyword is
		 * deadline, make sure that it has .from and .to and create deadline
		 * object to store the attributes
		 */
		if ((addTokenList.contains(".from")) && (addTokenList.contains(".to"))) {
			addTokenList.remove(".from");
			addTokenList.remove(".to");
			if (addTokenList.contains(".at")) {
				addTokenList.remove(".at");
				TimedTask timedTaskObject = new TimedTask(addTokenList.get(0),
						addTokenList.get(2), addTokenList.get(3));
				timedTaskObject.setVenue(addTokenList.get(1));
				return generateReturnList(timedTaskObject);
			} else {
				TimedTask timedTaskObject = new TimedTask(addTokenList.get(0),
						addTokenList.get(1), addTokenList.get(2));
				return generateReturnList(timedTaskObject);
			}

		} else if (addTokenList.contains(".by")) {
			addTokenList.remove(".by");
			if (addTokenList.contains(".at")) {
				addTokenList.remove(".at");
				DeadlineTask deadlineTaskObject = new DeadlineTask(
						addTokenList.get(0), addTokenList.get(2));
				deadlineTaskObject.setVenue(addTokenList.get(1));
				return generateReturnList(deadlineTaskObject);
			} else {
				DeadlineTask deadlineTaskObject = new DeadlineTask(
						addTokenList.get(0), addTokenList.get(1));
				return generateReturnList(deadlineTaskObject);
			}
		} else {
			if (addTokenList.contains(".at")) {
				addTokenList.remove(".at");
				FloatingTask floatingTaskObject = new FloatingTask(
						addTokenList.get(0));
				floatingTaskObject.setVenue(addTokenList.get(1));
				return generateReturnList(floatingTaskObject);
			} else {
				FloatingTask floatingTaskObject = new FloatingTask(
						addTokenList.get(0));
				return generateReturnList(floatingTaskObject);
			}
		}
	}

	private List<AbstractTask> generateReturnList(AbstractTask taskAdded) {
		List<AbstractTask> returnList = new Vector<AbstractTask>();
		wholeTaskList.add(taskAdded);
		returnList.add(taskAdded);
		return returnList;
	}

	public List<AbstractTask> undo() {
		wholeTaskList.remove(taskToAdd);
		List<AbstractTask> returnList = new Vector<AbstractTask>();
		returnList.add(taskToAdd);
		return returnList;
	}
}