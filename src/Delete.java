import java.util.List;
import java.util.Vector;

public class Delete implements UndoableCommand {
    private List<AbstractTask> deleteSpace, wholeTaskList;
    int index;
    AbstractTask taskToDeleted;

    public Delete(List<AbstractTask> deleteSpace, int index) {
	this.deleteSpace = deleteSpace;
	this.index = index;
    }

    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
	taskToDeleted = deleteSpace.get(index);
	this.wholeTaskList = wholeTaskList;
	wholeTaskList.remove(taskToDeleted);

	List<AbstractTask> returnList = new Vector<AbstractTask>();
	returnList.add(taskToDeleted);

	return returnList;
    }

    public List<AbstractTask> undo() {
	wholeTaskList.add(taskToDeleted);

	List<AbstractTask> returnList = new Vector<AbstractTask>();
	returnList.add(taskToDeleted);

	return returnList;
    }

}