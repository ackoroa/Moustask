import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Edit implements UndoableCommand {
    private List<AbstractTask> editSpace, wholeTaskList;
    private AbstractTask oldTask;
    private String editParameter;
    int index;

    public Edit(List<AbstractTask> editSpace, int index, String editParameter) {
	this.editSpace = editSpace;
	this.index = index;
	this.editParameter = editParameter;
    }

    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
	if (wholeTaskList == null || wholeTaskList.size() <= 0)
	    throw new IllegalArgumentException(
		    "taskList cannot be empty or null");
	if (editParameter == null || editParameter.length() <= 0)
	    throw new IllegalArgumentException(
		    "the edit parameter cannot be empty or null");
	if (index < 0 || index >= editSpace.size())
	    throw new IndexOutOfBoundsException(
		    "index pointer is outside the edit space");

	this.wholeTaskList = wholeTaskList;
	oldTask = editSpace.get(index);

	StringTokenizer editParameterTokenizer = new StringTokenizer(
		editParameter, ".");

	List<AbstractTask> returnList = new Vector<AbstractTask>();
	returnList.add(oldTask);

	return returnList;
    }

    public List<AbstractTask> undo() {
	assert wholeTaskList != null; // the task list from where the edited
				      // task came from must still exist
	assert oldTask != null; // the edited task must still exist

	return null;
    }

}
