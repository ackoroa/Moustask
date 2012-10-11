import java.util.List;
import java.util.Vector;

public class Clear implements UndoableCommand{

	private List<AbstractTask> wholeTaskList,undoTaskList;

	public Clear(){
	}

	public List<AbstractTask> execute(List<AbstractTask> wholeTaskList){
		this.wholeTaskList=wholeTaskList;
		undoTaskList=wholeTaskList;
		return null;
	}

	public List<AbstractTask> undo(){
		wholeTaskList=undoTaskList;
		return null;
	}
}