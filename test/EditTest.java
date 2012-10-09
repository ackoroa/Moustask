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

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	taskList = new Vector<AbstractTask>();
	taskList.add(new FloatingTask("eat at mcd"));

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

	fail("Not yet implemented");
    }

    @Test
    public void testUndo() {
	fail("Not yet implemented");
    }

}
