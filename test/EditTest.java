import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.Stack;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

public class EditTest {
    static Vector<AbstractTask> taskList, origTaskList;
    static AbstractTask oldTask;
    static UndoableCommand editObject;
    static Stack<UndoableCommand> undoStack;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	taskList = new Vector<AbstractTask>();
	taskList.add(new FloatingTask("eat"));

	oldTask = new DeadlineTask("finish eating", "2012-12-12 18:00");
	taskList.add(oldTask);

	taskList.add(new TimedTask("must eat", "2012-12-12 18:00",
		"2012-12-12 20:00"));

	origTaskList = (Vector<AbstractTask>) taskList.clone();
	undoStack = new Stack<UndoableCommand>();
    }

    @Test
    public void testExecute() {
	editObject = new Edit(taskList, 2, "reach restaurant");
	editObject.execute(taskList);
	undoStack.push(editObject);

	assertEquals("description changed", "reach restaurant", taskList
		.lastElement().getDescription());

	editObject = new Edit(taskList, 3, ".at mcd");
	editObject.execute(taskList);
	undoStack.push(editObject);

	assertEquals("venue changed", "mcd", taskList.lastElement().getVenue());

	editObject = new Edit(taskList, 3, ".by 2012-12-12 17:30");
	editObject.execute(taskList);
	undoStack.push(editObject);

	DeadlineTask changedDeadline = (DeadlineTask) taskList.lastElement();
	assertEquals("deadline changed", "2012-12-12 17:30",
		changedDeadline.getEndDate());
	
	editObject = new Edit(taskList, 2, ".from 2012-12-12 08:00");
	editObject.execute(taskList);
	undoStack.push(editObject);

	TimedTask changedStart = (TimedTask) taskList.lastElement();
	assertEquals("deadline changed", "2012-12-12 08:00",
		changedStart.getStartDate());
	
	editObject = new Edit(taskList, 3, ".to 2012-12-12 20:00");
	editObject.execute(taskList);
	undoStack.push(editObject);

	TimedTask changedEnd = (TimedTask) taskList.lastElement();
	assertEquals("deadline changed", "2012-12-12 20:00",
		changedEnd.getEndDate());
	
	editObject = new Edit(taskList, 3, "party .at comicket .from 2012-12-12 08:00 .to 2012-12-12 20:00");
	editObject.execute(taskList);
	undoStack.push(editObject);

	TimedTask changedMultiple = (TimedTask) taskList.lastElement();
	assertEquals("description changed in multiple changing", "party",
		changedMultiple.getDescription());
	assertEquals("venue changed in multiple changing", "comicket",
		changedMultiple.getVenue());
	assertEquals("start time changed in multiple changing", "2012-12-12 08:00",
		changedMultiple.getStartDate());
	assertEquals("end time changed in multiple changing", "2012-12-12 20:00",
		changedMultiple.getEndDate());

    }

    @Test
    public void testUndo() {
	while (!undoStack.isEmpty()) {
	    undoStack.pop().undo();
	}

	assertEquals("old task is the same as unoded task", oldTask,
		taskList.get(2));
    }

}
