import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Edit implements UndoableCommand {
    private List<AbstractTask> editSpace, wholeTaskList;
    private AbstractTask originalTask, editedTask;
    private String editParameter;
    int index;

    // Initializes the edit parameters
    public Edit(List<AbstractTask> editSpace, int index, String editParameter)
	    throws IndexOutOfBoundsException, IllegalArgumentException {
	if (editSpace == null || editSpace.size() <= 0)
	    throw new IllegalArgumentException(
		    "editSpace cannot be empty or null");
	if (editParameter == null)
	    throw new IllegalArgumentException(
		    "the edit parameter cannot be null");
	if (index <= 0 || index > editSpace.size())
	    throw new IndexOutOfBoundsException(
		    "index pointer is outside the edit space");

	this.editSpace = editSpace;
	this.index = index;
	this.editParameter = editParameter;
    }

    // Executes the edit operation, saving both the original and edited tasks
    // and returning them in a list original->edited
    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList)
	    throws IllegalArgumentException {
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
	AbstractTask editedTask = (AbstractTask) originalTask.clone();

	StringTokenizer st = new StringTokenizer(editParameter);
	String paramToken = st.nextToken();

	if (!paramToken.startsWith(".")) {
	    editDescription(editedTask, paramToken);

	    while (st.hasMoreTokens()) {
		paramToken = st.nextToken();
		if (!paramToken.startsWith("."))
		    addToField(editedTask, paramToken, "desc");
		else
		    break;
	    }
	}

	while (true) {
	    switch (paramToken) {
	    case ".at":
	    case ".venue":
		paramToken = st.nextToken();
		editVenue(editedTask, paramToken);

		while (st.hasMoreTokens()) {
		    paramToken = st.nextToken();
		    if (!paramToken.startsWith("."))
			addToField(editedTask, paramToken, "venue");
		    else
			break;
		}
		break;
	    case ".status":
		paramToken = st.nextToken();
		editStatus(editedTask, paramToken);
		break;
	    case ".by":
	    case ".deadline":
		String deadline = st.nextToken();
		if (st.hasMoreTokens())
		    paramToken = st.nextToken();
		else
		    paramToken = "";

		deadline = checkAndInsertTime(deadline, paramToken, "end");
		if (!(new DateTime(deadline)).validateDateTime())
		    throw new IllegalArgumentException(
			    "invalid deadline format");

		editDeadline(editedTask, deadline);
		break;
	    case ".from":
		String from = st.nextToken();
		if (st.hasMoreTokens())
		    paramToken = st.nextToken();
		else
		    paramToken = "";
		
		from = checkAndInsertTime(from, paramToken, "start");
		if (!(new DateTime(from)).validateDateTime())
		    throw new IllegalArgumentException(
			    "invalid start time format");

		editStartTime(editedTask, from);
		break;
	    case ".to":
		String to = st.nextToken();
		if (st.hasMoreTokens())
		    paramToken = st.nextToken();
		else
		    paramToken = "";
		
		to = checkAndInsertTime(to, paramToken, "end");
		if (!(new DateTime(to)).validateDateTime())
		    throw new IllegalArgumentException(
			    "invalid end time format");

		editEndTime(editedTask, to);
		break;
	    }

	    if (paramToken.startsWith("."))
		continue;
	    else if (st.hasMoreTokens())
		paramToken = st.nextToken();
	    else
		break;
	}

	return editedTask;
    }

    private String checkAndInsertTime(String date, String time, String type) {
	if (!time.startsWith(".") && !time.equals(""))
	    date = date + " " + time;
	else if (type.equals("start"))
	    date = date + " 00:00";
	else
	    date = date + " 23:59";

	return date;
    }

    private void addToField(AbstractTask task, String value, String last) {
	switch (last) {
	case "desc":
	    editDescription(task, task.getDescription() + " " + value);
	    break;
	case "venue":
	    editVenue(task, task.getVenue() + " " + value);
	    break;
	default:
	    assert false;
	}
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
}
