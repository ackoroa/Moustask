import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Edit implements UndoableCommand {
    private List<AbstractTask> editSpace, wholeTaskList;
    private AbstractTask originalTask, editedTask;
    private String editParameter;
    int index;

    // Initializes the edit parameters
    public Edit(List<AbstractTask> editSpace, int index, String editParameter) {
	if (editParameter == null || editParameter.length() <= 0)
	    throw new IllegalArgumentException(
		    "the edit parameter cannot be empty or null");
	if (index <= 0 || index > editSpace.size())
	    throw new IndexOutOfBoundsException(
		    "index pointer is outside the edit space");

	this.editSpace = editSpace;
	this.index = index;
	this.editParameter = editParameter;
    }

    // Executes the edit operation, saving both the original and edited tasks
    // and returning them in a list original->edited
    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
	if (wholeTaskList == null || wholeTaskList.size() <= 0)
	    throw new IllegalArgumentException(
		    "taskList cannot be empty or null");

	this.wholeTaskList = wholeTaskList;
	originalTask = editSpace.get(index - 1);
	editedTask = editTask(originalTask);

	wholeTaskList.remove(originalTask);
	wholeTaskList.add(editedTask);

	return generateReturnList(originalTask, editedTask);
    }

    // edits a task according to the parameter given and returns it
    private AbstractTask editTask(AbstractTask originalTask) {
	String paramToken;
	AbstractTask editedTask = (AbstractTask) originalTask.clone();
	StringTokenizer parameterTokenizer = new StringTokenizer(editParameter,
		".", true);

	paramToken = parameterTokenizer.nextToken().trim();
	if (!paramToken.equals(".")) {
	    editDescription(editedTask, paramToken);
	}

	while (parameterTokenizer.hasMoreTokens()) {
	    paramToken = parameterTokenizer.nextToken();

	    if (paramToken.equals("."))
		continue;

	    String fieldType = getFirstWord(paramToken).toLowerCase();
	    String fieldValue = removeFirstWord(paramToken);

	    switch (fieldType) {
	    case "at": case "venue":
		editVenue(editedTask, fieldValue);
		break;
	    case "status":
		editStatus(editedTask, fieldValue);
		break;
	    case "by": case "deadline":
		editDeadline(editedTask, fieldValue);
		break;
	    case "from":
		editStartTime(editedTask, fieldValue);
		break;
	    case "to":
		editEndTime(editedTask, fieldValue);
		break;
	    }
	}

	return editedTask;
    }

    private void editEndTime(AbstractTask editedTask, String fieldValue) {
	TimedTask task = (TimedTask) editedTask;
	task.setEndDate(fieldValue);
    }

    private void editStartTime(AbstractTask editedTask, String fieldValue) {
	TimedTask task = (TimedTask) editedTask;
	task.setStartDate(fieldValue);
    }

    private void editDeadline(AbstractTask editedTask, String fieldValue) {
	DeadlineTask task = (DeadlineTask) editedTask;
	task.setEndDate(fieldValue);
    }

    private void editStatus(AbstractTask editedTask, String fieldValue) {
	switch (fieldValue.toLowerCase()) {
	case "done":
	    editedTask.setStatus(AbstractTask.Status.DONE);
	    break;
	case "undone":
	    editedTask.setStatus(AbstractTask.Status.UNDONE);
	    break;
	case "impossible":
	    editedTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
	    break;
	}
    }

    private void editVenue(AbstractTask editedTask, String fieldValue) {
	editedTask.setVenue(fieldValue);
    }

    private void editDescription(AbstractTask editedTask, String fieldValue) {
	editedTask.setDescription(fieldValue);
    }

    // undoes this edit operation
    // returns the list: edited->original
    public List<AbstractTask> undo() {
	// the task list from where the edited task came from must still exist
	assert wholeTaskList != null;
	// the edited task must exist in task list
	assert wholeTaskList.contains(editedTask);

	wholeTaskList.remove(editedTask);
	wholeTaskList.add(originalTask);

	return generateReturnList(editedTask, originalTask);
    }

    // generates the returned task list
    private List<AbstractTask> generateReturnList(AbstractTask oldTask,
	    AbstractTask newTask) {
	List<AbstractTask> returnList = new Vector<AbstractTask>();
	returnList.add(oldTask);
	returnList.add(newTask);

	return returnList;
    }

    private String getFirstWord(String s) {
	StringTokenizer st = new StringTokenizer(s);
	return st.nextToken();
    }

    private String removeFirstWord(String s) {
	return s.replaceFirst(getFirstWord(s), "").trim();
    }

}
