//@author A0058657N
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Vector;
import org.junit.Test;

public class AddTest {
	static Vector<AbstractTask> floatingTaskList, deadlineTaskList,
			timedTaskList, undoTaskList;
	static AbstractTask addedTask;
	static UndoableCommand addObject;

	@Test
	public void testFloatingTask() {
		floatingTaskList = new Vector<AbstractTask>();

		// Adding valid floating tasks
		addObject = new Add("Valid Floating Task without Venue");
		addObject.execute(floatingTaskList);

		addObject = new Add("Valid Floating Task with Venue .at Orchard");
		addObject.execute(floatingTaskList);

		// Adding invalid floating tasks
		addObject = new Add("");
		addObject.execute(floatingTaskList);

		addObject = new Add("Invalid keyword used .in Orchard");
		addObject.execute(floatingTaskList);

		addObject = new Add("Without venue .at ");
		addObject.execute(floatingTaskList);

		addObject = new Add(".at ");
		addObject.execute(floatingTaskList);

		addObject = new Add(".in ");
		addObject.execute(floatingTaskList);

		addObject = new Add(".by ");
		addObject.execute(floatingTaskList);

		// Results
		assertTrue("Size of task list is 2", floatingTaskList.size() == 2);
		for (int index = 0; index < floatingTaskList.size(); index++) {
			assertTrue("task type is floating task", floatingTaskList
					.get(index).getType().equals(AbstractTask.Type.FLOATING));
		}
	}

	@Test
	public void testDeadlineTask() {
		deadlineTaskList = new Vector<AbstractTask>();

		// Adding valid Deadline Task
		addObject = new Add(
				"Valid Deadline Task with Venue .at City Hall .by 2012-12-21 00:00");
		addObject.execute(deadlineTaskList);

		addObject = new Add(
				"Valid Deadline Task without Venue .by 2012-12-21 00:00");
		addObject.execute(deadlineTaskList);

		addObject = new Add("Valid Deadline Task with Day .by Tuesday");
		addObject.execute(deadlineTaskList);

		addObject = new Add("Valid Deadline Task with Date only .by 2012-12-21");
		addObject.execute(deadlineTaskList);

		// Adding invalid Deadline Task
		addObject = new Add("");
		addObject.execute(deadlineTaskList);

		addObject = new Add(".by 2012-12-21 00:00");
		addObject.execute(deadlineTaskList);

		addObject = new Add("Deadline Task without end time .by");
		addObject.execute(deadlineTaskList);

		addObject = new Add("Deadline Task with wrong keyword .in 2012-12-21");
		addObject.execute(deadlineTaskList);

		addObject = new Add(
				"Deadline Task with wrong date format .by 2012-12 00:00");
		addObject.execute(deadlineTaskList);

		addObject = new Add(
				"Deadline Task with wrong time format .by 2012-12-21 abc");
		addObject.execute(deadlineTaskList);

		addObject = new Add(
				"Deadline Task with wrong date time format .by date time");
		addObject.execute(deadlineTaskList);

		addObject = new Add(
				"Deadline Task with date out of range .by 2012-12-32");
		addObject.execute(deadlineTaskList);

		addObject = new Add(
				"Deadline Task with time out of range .by 2012-12-21 24:00");
		addObject.execute(deadlineTaskList);

		addObject = new Add("Deadline Task with wrong day .by Nonday");
		addObject.execute(deadlineTaskList);

		// Results
		assertEquals("Size of task list is 4", 4, deadlineTaskList.size());
		for (int index = 0; index < deadlineTaskList.size(); index++) {
			assertTrue("task type is deadline task", deadlineTaskList
					.get(index).getType().equals(AbstractTask.Type.DEADLINE));
		}
	}

	@Test
	public void testTimedTask() {
		timedTaskList = new Vector<AbstractTask>();

		// Add valid Timed Task
		addObject = new Add(
				"Valid Timed Task Event .at Jurong East .from 2012-10-11 13:53 .to 2012-12-21 00:00");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Valid Timed Task Event .at Jurong East .from Monday .to 2012-12-21 00:00");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Valid Timed Task Event .at Jurong East .from 2011-10-11 13:53 .to Friday");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Valid Timed Task Event .at Jurong East .from 2012-10-10 .to 2012-10-11");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Valid Timed Task Event .at Jurong East .to 2012-10-11 .from 2012-10-10");
		addObject.execute(timedTaskList);

		// Add invalid Timed Task
		addObject = new Add("");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Wrong keyword used .in 2012-10-10 .till 2012-10-11");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Start time greater than End time .from 2012-10-11 13:01 .to 2012-10-11 13:00");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Start date greater than End date .from 2012-10-11 13:00 .to 2012-10-10 13:01");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Start Date out of range .from 2012-10-32 .to 2012-10-11");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"End Date out of range .from 2012-10-28 .to 2012-10-32");
		addObject.execute(timedTaskList);

		addObject = new Add("Wrong days .from aMonday .to zWednesday");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"Start Time out of range .from 2012-10-11 24:00 .to 2012-12-21 00:00");
		addObject.execute(timedTaskList);

		addObject = new Add(
				"End Time out of range .from 2012-10-11 00:00 .to 2012-12-21 24:00");
		addObject.execute(timedTaskList);

		// Results
		assertEquals("Size of task list is 5", 5, timedTaskList.size());
		for (int index = 0; index < timedTaskList.size(); index++) {
			assertTrue("task type is timed task", timedTaskList.get(index)
					.getType().equals(AbstractTask.Type.TIMED));
		}
	}

	@Test
	public void testUndo() {
		undoTaskList = new Vector<AbstractTask>();
		List<AbstractTask> undoReturn = new Vector<AbstractTask>();

		addObject = new Add(
				"Valid Deadline Task with Venue .at City Hall .by 2012-12-21 00:00");
		addObject.execute(undoTaskList);

		undoReturn = addObject.undo();
		assertTrue("undo task type is timed task", undoReturn.get(0).getType()
				.equals(AbstractTask.Type.DEADLINE));
	}
}