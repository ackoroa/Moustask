import java.util.List;
import java.util.Vector;

public class Clear implements UndoableCommand {

    private List<AbstractTask> wholeTaskList, undoList;

    public Clear() {
    }

    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
	this.wholeTaskList = wholeTaskList;
	undoList = new Vector<AbstractTask>(wholeTaskList);
	wholeTaskList.clear();

	return null;
    }

    public List<AbstractTask> undo() {
	wholeTaskList = undoList;
	
	return null;
    }

}