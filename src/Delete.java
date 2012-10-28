import java.util.List;
import java.util.Vector;

public class Delete implements UndoableCommand {
    private List<AbstractTask> deleteSpace, wholeTaskList;
    private int index;
    private AbstractTask deletedTask;
    private Logging deleteLog = new Logging("Delete");

    // Initialize delete parameters
    public Delete(List<AbstractTask> deleteSpace, int index)
	    throws IndexOutOfBoundsException, IllegalArgumentException {

	if (deleteSpace == null || deleteSpace.size() <= 0) {
	    deleteLog.addLog(Logging.LoggingLevel.WARNING,
		    "Delete(): delete object initialization failed. Illegal deleteSpace (deleteSpace = "
			    + deleteSpace + ")");

	    throw new IllegalArgumentException(
		    "deleteSpace cannot be empty or null");
	}

	if (index <= 0 || index > deleteSpace.size()) {
	    deleteLog.addLog(Logging.LoggingLevel.WARNING,
		    "Delete(): delete object initialization failed. Index is out of bounds (index = "
			    + index + ")");

	    throw new IndexOutOfBoundsException(
		    "index pointer is outside the delete space");
	}

	this.deleteSpace = deleteSpace;
	this.index = index;

	deleteLog.addLog(Logging.LoggingLevel.INFO,
		"Delete(): delete object initialized with deleteSpace of size: "
			+ deleteSpace.size() + " and index: " + index);
    }

    // Deletes the specified task from taskList, stores the deletedTask and
    // returns it in a list
    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList)
	    throws IllegalArgumentException {

	if (wholeTaskList == null || wholeTaskList.size() <= 0) {
	    deleteLog.addLog(Logging.LoggingLevel.WARNING,
		    "Delete.execute(): delete execution failed. Illegal task list (wholeTaskList = "
			    + wholeTaskList + ")");

	    throw new IllegalArgumentException(
		    "taskList cannot be empty or null");
	}

	deletedTask = deleteSpace.get(index - 1);
	this.wholeTaskList = wholeTaskList;

	wholeTaskList.remove(deletedTask);

	deleteLog.addLog(Logging.LoggingLevel.INFO,
		"Delete.execute(): The task \"" + deletedTask
			+ "\" has been deleted from the task list");

	return generateReturnList();
    }

    // Undoes this delete operation
    // returns the task re-added
    public List<AbstractTask> undo() {
	// the task list from where the deleted task came from must still exist
	assert wholeTaskList != null;

	wholeTaskList.add(deletedTask);

	deleteLog.addLog(Logging.LoggingLevel.INFO, "Delete.undo(): The task \""
		+ deletedTask + "\" has been re-added to the task list");

	return generateReturnList();
    }

    // Creates the list of task to be returned
    private List<AbstractTask> generateReturnList() {
	List<AbstractTask> returnList = new Vector<AbstractTask>();
	returnList.add(deletedTask);
	return returnList;
    }
}