import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class EditTest {
    static Vector<AbstractTask> taskList;

    @Before
    public void setUp() throws Exception {
	taskList = new Vector<AbstractTask>();

	taskList.add(new FloatingTask("have fun", "everywhere"));
	taskList.add(new FloatingTask("eat"));
	taskList.add(new DeadlineTask("finish eating", "2012-12-12 18:00"));
	taskList.add(new TimedTask("must eat", "2012-12-12 16:00",
		"2012-12-12 18:00"));
	taskList.add(new DeadlineTask("reach", "2012-12-12 20:00", "home"));
	taskList.add(new TimedTask("relax", "2012-12-12 20:00",
		"2012-12-12 22:00", "home"));
    }

    @Test
    public void testConstructor() {
	UndoableCommand editObject;
	AbstractTask editeddTask;
	List<AbstractTask> editReturn, origTaskList;
	int editIndex;

	// editSpace is null
	try {
	    editObject = new Edit(null, 1, "");
	    assertFalse("edit constructor does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for null edit space",
		    "editSpace cannot be empty or null", e.getMessage());
	}

	// editSpace is empty
	try {
	    editObject = new Edit(new Vector<AbstractTask>(), 1, "");
	    assertFalse("edit constructor does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for empty edit space",
		    "editSpace cannot be empty or null", e.getMessage());
	}

	// Index outside editSpace (<=0)
	try {
	    editObject = new Edit(taskList, 0, "");
	    assertFalse("edit constructor does not throw exception", true);
	} catch (IndexOutOfBoundsException e) {
	    assertEquals("correct exception for index == 0",
		    "index pointer is outside the edit space", e.getMessage());
	}
	try {
	    editObject = new Edit(taskList, -1, "");
	    assertFalse("edit constructor does not throw exception", true);
	} catch (IndexOutOfBoundsException e) {
	    assertEquals("correct exception for index < 0",
		    "index pointer is outside the edit space", e.getMessage());
	}

	// Index outside editSpace (>editSpace.size)
	try {
	    editObject = new Edit(taskList, taskList.size() + 1, "");
	    assertFalse("edit constructor does not throw exception", true);
	} catch (IndexOutOfBoundsException e) {
	    assertEquals("correct exception for index > editSpace.size()",
		    "index pointer is outside the edit space", e.getMessage());
	}

	// edit parameter is null
	try {
	    editObject = new Edit(taskList, 1, null);
	    assertFalse("edit constructor does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for null edit parameter",
		    "the edit parameter cannot be null", e.getMessage());
	}

	// all correct
	editObject = new Edit(taskList, 1, "");
	assertTrue("editObject created", editObject != null);
    }

    @Test
    public void testExecute() {
	UndoableCommand editObject;
	AbstractTask origTask, editedTask;
	List<AbstractTask> editReturn, origTaskList;
	int editIndex;
	String editParameter;
	DeadlineTask changedDeadline;
	TimedTask changedStart, changedEnd;

	// wholeTaskList is null
	editObject = new Edit(taskList, 1, "");
	try {
	    editReturn = editObject.execute(null);
	    assertFalse("execute does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for null whole task list",
		    "taskList cannot be empty or null", e.getMessage());
	}

	// whole task list is empty
	editObject = new Edit(taskList, 1, "");
	try {
	    editReturn = editObject.execute(new Vector<AbstractTask>());
	    assertFalse("execute does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for empty whole task list",
		    "taskList cannot be empty or null", e.getMessage());
	}

	// Edit description only
	// one word description
	editIndex = taskList.size();
	editParameter = "relaxing";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("description changed", "relaxing", taskList.lastElement()
		.getDescription());

	// longer description
	editIndex = taskList.size();
	editParameter = "relax relax relax";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("description changed", "relax relax relax", taskList
		.lastElement().getDescription());

	// Edit venue only
	// one word venue
	editIndex = taskList.size();
	editParameter = ".at house";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("venue changed", "house", taskList.lastElement()
		.getVenue());

	// longer venue
	editIndex = taskList.size();
	editParameter = ".at home sweet home";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("venue changed", "home sweet home", taskList.lastElement()
		.getVenue());

	// using alternate keyword .venue
	editIndex = taskList.size();
	editParameter = ".venue home sweeeet home";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("venue changed", "home sweeeet home", taskList
		.lastElement().getVenue());

	// edit status only
	// to done
	editIndex = taskList.size();
	editParameter = ".status DONE";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("status changed", AbstractTask.Status.DONE, taskList
		.lastElement().getStatus());
	
	// to undone
	editIndex = taskList.size();
	editParameter = ".status UNdone";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("status changed", AbstractTask.Status.UNDONE, taskList
		.lastElement().getStatus());

	// to impossible
	editIndex = taskList.size();
	editParameter = ".status impossible";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("status changed", AbstractTask.Status.IMPOSSIBLE, taskList
		.lastElement().getStatus());

	// edit deadline only
	editIndex = taskList.size() - 1;
	editParameter = ".by 2012-12-12 21:00";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();
	changedDeadline = (DeadlineTask) editedTask;

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("deadline changed", "2012-12-12 21:00",
		changedDeadline.getEndDate());

	// edit deadline only using .deadline
	editIndex = taskList.size();
	editParameter = ".deadline 2012-12-12 21:15";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();
	changedDeadline = (DeadlineTask) editedTask;

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("deadline changed", "2012-12-12 21:15",
		changedDeadline.getEndDate());

	// edit start only
	editIndex = taskList.size() - 1;
	editParameter = ".from 2012-12-12 21:00";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();
	changedStart = (TimedTask) editedTask;

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("start changed", "2012-12-12 21:00",
		changedStart.getStartDate());

	// edit end only
	editIndex = taskList.size();
	editParameter = ".to 2012-12-12 22:30";
	editObject = new Edit(taskList, editIndex, editParameter);
	origTask = taskList.get(editIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	editReturn = editObject.execute(taskList);
	editedTask = taskList.lastElement();
	changedStart = (TimedTask) editedTask;

	assertEquals("Original task returned in list", editReturn.get(0),
		origTask);
	assertEquals("Edited task returned in list", editReturn.get(1),
		editedTask);
	assertEquals("end changed", "2012-12-12 22:30",
		changedStart.getEndDate());
	
	// edit multiple fields
	// mix up order
    }

    @Test
    public void testUndo() {

    }

}
