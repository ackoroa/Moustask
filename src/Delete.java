import java.util.List;
import java.util.Vector;

public class Delete implements UndoableCommand {
    private List<AbstractTask> deleteSpace, wholeTaskList;
    int index;
    AbstractTask deletedTask;

    public Delete(List<AbstractTask> deleteSpace, int index) {
	this.deleteSpace = deleteSpace;
	this.index = index;
    }

    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
	if (wholeTaskList == null || wholeTaskList.size() <= 0)
	    throw new IllegalArgumentException(
		    "taskList cannot be empty or null");
	if (index < 0 || index >= deleteSpace.size())
	    throw new IndexOutOfBoundsException(
		    "index pointer is outside the delete space");

	deletedTask = deleteSpace.get(index);
	this.wholeTaskList = wholeTaskList;
	wholeTaskList.remove(deletedTask);

	List<AbstractTask> returnList = new Vector<AbstractTask>();
	returnList.add(deletedTask);

	return returnList;
    }

    public List<AbstractTask> undo() {
	assert wholeTaskList != null; // the task list from where the deleted
				      // task came from must still exist

	wholeTaskList.add(deletedTask);

	List<AbstractTask> returnList = new Vector<AbstractTask>();
	returnList.add(deletedTask);

	return returnList;
    }

}