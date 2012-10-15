import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;
import org.junit.Test;

public class AddTest {
	static Vector<AbstractTask> taskList, taskListForInvalid;
	static AbstractTask addedTask;
	static UndoableCommand addObject;

	@Test
	public void testExecute() {
		taskList = new Vector<AbstractTask>();

		// Add valid Floating Task
		addObject = new Add("Valid Floating Task Event .at Orchard");
		addObject.execute(taskList);
		assertTrue("Size of task list increases to 1", taskList.size() == 1);
		assertTrue("task type is floating task", taskList.get(0).getType()
				.equals(AbstractTask.Type.FLOATING));

		// Add valid Deadline Task
		addObject = new Add(
				"Valid Deadline Task Event .at City Hall .by 2012-12-21 00:00");
		addObject.execute(taskList);
		assertTrue("Size of task list increases to 2", taskList.size() == 2);
		assertTrue("task type is deadline task", taskList.get(1).getType()
				.equals(AbstractTask.Type.DEADLINE));

		// Add valid Timed Task
		addObject = new Add(
				"Valid Timed Task Event .at Jurong East .from 2012-10-11 13:53 .to 2012-12-21 00:00");
		addObject.execute(taskList);
		assertTrue("Size of task list increases to 3", taskList.size() == 3);
		assertTrue("task type is timed task",
				taskList.get(2).getType().equals(AbstractTask.Type.TIMED));
	}

	@Test
	public void testExecuteInvalid() {
		taskListForInvalid = new Vector<AbstractTask>();
		// empty event
		addObject = new Add(".by 2012-10-11 13:53");
		// wrong time format
		addObject = new Add("Wrong time format .by 1221");
		// mixing all valid keywords
		addObject = new Add(
				"Valid Timed Task Event .at Jurong East .by 2012-10-11 13:53  .from 2012-10-11 13:53 .to 2012-12-21 00:00");
		// wrong keywords used
		addObject = new Add(
				"Valid Timed Task Event .at Jurong East .start 2012-10-11 13:53 .to 2012-12-21 00:00");
		addObject.execute(taskListForInvalid);
		assertTrue("Size of task list remains at 3",
				taskListForInvalid.size() == 0);
	}

	@Test
	public void testUndo() {
		// Undo - Return valid Timed Task
		List<AbstractTask> undoReturn = addObject.undo();
		assertTrue("undo task type is timed task", undoReturn.get(0).getType()
				.equals(AbstractTask.Type.TIMED));
	}
}