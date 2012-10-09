import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

public class DeleteTest {
    static Vector<AbstractTask> taskList, origTaskList;
    static AbstractTask deletedTask;
    static UndoableCommand deleteObject;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	taskList = new Vector<AbstractTask>();
	taskList.add(new FloatingTask("eat at mcd"));

	deletedTask = new DeadlineTask("finish eating", "2012-12-12 18:00");
	taskList.add(deletedTask);

	taskList.add(new TimedTask("must eat", "2012-12-12 18:00",
		"2012-12-12 20:00"));

	origTaskList = (Vector<AbstractTask>) taskList.clone();
	deleteObject = new Delete(taskList, 2);
    }

    @Test
    public void testExecute() {
	List<AbstractTask> deleteReturn = deleteObject.execute(taskList);

	assertEquals("Deleted task returned in list", deleteReturn.get(0),
		deletedTask);
	assertTrue("Size of task list decreases by 1",
		taskList.size() == (origTaskList.size() - 1));
	assertFalse("deleted task not in taskList",
		taskList.contains(deletedTask));
    }

    @Test
    public void testUndo() {
	List<AbstractTask> undoReturn = deleteObject.undo();

	assertTrue("deleted task is in taskList",
		taskList.contains(deletedTask));
	assertTrue(
		"undoed taskList == original list",
		taskList.containsAll(origTaskList)
			&& origTaskList.containsAll(taskList));
    }

}
