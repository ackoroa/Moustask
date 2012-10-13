import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Clear implements UndoableCommand {

	private List<AbstractTask> wholeTaskList;
	private List<AbstractTask> undoTaskList=new LinkedList<AbstractTask>();

	public Clear() {
	}

	public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
		this.wholeTaskList = wholeTaskList;
		for(int i=0;i<wholeTaskList.size();i++){
			undoTaskList.add(wholeTaskList.get(i));
		}
		wholeTaskList.clear();
		return null;
	}

	public List<AbstractTask> undo() {
		for(int i=0;i<undoTaskList.size();i++){
			wholeTaskList.add(undoTaskList.get(i));
		}
		return null;
	}
}