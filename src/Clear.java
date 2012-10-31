import java.util.LinkedList;
import java.util.List;

public class Clear implements UndoableCommand {

	private List<AbstractTask> wholeTaskList;
	private List<AbstractTask> undoTaskList = new LinkedList<AbstractTask>();
	private Logging clearLog = new Logging("Clear Function"); 
	
	public Clear() {
	}

	public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
		this.wholeTaskList = wholeTaskList;
		for (int i = 0; i < wholeTaskList.size(); i++) {
			undoTaskList.add(wholeTaskList.get(i));	
		}
		wholeTaskList.clear();
		clearLog.addLog(Logging.LoggingLevel.INFO, "Cleared Moustask.txt");
		return null;
	}

	public List<AbstractTask> undo() {
		for (int i = 0; i < undoTaskList.size(); i++) {
			wholeTaskList.add(undoTaskList.get(i));
		}
		clearLog.addLog(Logging.LoggingLevel.INFO, "Undo clear.");
		return null;
	}
}