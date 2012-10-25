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
		try{
		FileHandler fileHandler=new FileHandler("clearLogging.txt",true);
		clearLogger.addHandler(fileHandler);
		
		this.wholeTaskList = wholeTaskList;
		
		for (int i = 0; i < wholeTaskList.size(); i++) {
			undoTaskList.add(wholeTaskList.get(i));
			clearLogger.log(Level.INFO, i+" loop/s");
		}
		clearLogger.log(Level.INFO, "No exception");
		wholeTaskList.clear();
		return null;
		}		
		catch (IOException ex){
			clearLogger.log(Level.WARNING, "Exception",ex);
			return null;
		}
	}

	public List<AbstractTask> undo() {
		for (int i = 0; i < undoTaskList.size(); i++) {
			wholeTaskList.add(undoTaskList.get(i));
		}
		return null;
	}
}