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

	deletedTask = deleteSpace.get(index-1);
	this.wholeTaskList = wholeTaskList;
	
	wholeTaskList.remove(deletedTask);

	return generateReturnList();
    }

    public List<AbstractTask> undo() {
	// the task list from where the deleted task came from must still exist
	assert wholeTaskList != null; 
	
	wholeTaskList.add(deletedTask);
	return generateReturnList();
    }


    private List<AbstractTask> generateReturnList() {
	List<AbstractTask> returnList = new Vector<AbstractTask>();
	returnList.add(deletedTask);
	return returnList;
    }
}