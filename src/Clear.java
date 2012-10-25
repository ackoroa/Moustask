import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.*;
import java.util.List;

public class Clear implements UndoableCommand {

	private List<AbstractTask> wholeTaskList;
	private List<AbstractTask> undoTaskList = new LinkedList<AbstractTask>();
	private static Logger clearLogger = Logger.getLogger("clearLog");

	public Clear() {
	}

	public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
		try {
			FileHandler fileHandler = new FileHandler("clearLogging.txt", true);
			clearLogger.addHandler(fileHandler);
			this.wholeTaskList = wholeTaskList;
			for (int i = 0; i < wholeTaskList.size(); i++) {
				undoTaskList.add(wholeTaskList.get(i));
				clearLogger.log(Level.INFO, wholeTaskList.get(i)
						.getDescription() + " task/s cleared.");
			}
			clearLogger.log(Level.INFO, "Undo task list duplicated.");
			wholeTaskList.clear();
		} catch (IOException ex) {
			clearLogger.log(Level.WARNING, "Exception thrown.", ex);
		}
		return null;
	}

	public List<AbstractTask> undo() {
		try {
			FileHandler fileHandler = new FileHandler("clearLogging.txt", true);
			clearLogger.addHandler(fileHandler);
			for (int i = 0; i < undoTaskList.size(); i++) {
				wholeTaskList.add(undoTaskList.get(i));
				clearLogger.log(Level.INFO, wholeTaskList.get(i)
						.getDescription() + " task/s undo.");
			}
			clearLogger.log(Level.INFO, "Original task list reverted.");
		} catch (IOException ex) {
			clearLogger.log(Level.WARNING, "Exception thrown.", ex);
		}
		return null;
	}
}