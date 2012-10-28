import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class DeleteTest {
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
	taskList.add(new TimedTask("relax", "2012-12-12 16:00",
		"2012-12-12 18:00", "home"));
    }

    @Test
    public void testConstructor() {
	UndoableCommand deleteObject;
	AbstractTask deletedTask;
	List<AbstractTask> deleteReturn, origTaskList;
	int deleteIndex;

	// deleteSpace is null
	try {
	    deleteObject = new Delete(null, 1);
	    assertFalse("delete constructor does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for null delete space",
		    "deleteSpace cannot be empty or null", e.getMessage());
	}

	// deleteSpace is empty
	try {
	    deleteObject = new Delete(new Vector<AbstractTask>(), 1);
	    assertFalse("delete constructor does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for empty delete space",
		    "deleteSpace cannot be empty or null", e.getMessage());
	}

	// Index outside deleteSpace (<=0)
	try {
	    deleteObject = new Delete(taskList, 0);
	    assertFalse("delete constructor does not throw exception", true);
	} catch (IndexOutOfBoundsException e) {
	    assertEquals("correct exception for index == 0",
		    "index pointer is outside the delete space", e.getMessage());
	}
	try {
	    deleteObject = new Delete(taskList, -1);
	    assertFalse("delete constructor does not throw exception", true);
	} catch (IndexOutOfBoundsException e) {
	    assertEquals("correct exception for index < 0",
		    "index pointer is outside the delete space", e.getMessage());
	}

	// Index outside deleteSpace (>deleteSpace.size)
	try {
	    deleteObject = new Delete(taskList, taskList.size() + 1);
	    assertFalse("delete constructor does not throw exception", true);
	} catch (IndexOutOfBoundsException e) {
	    assertEquals("correct exception for index > deleteSpace.size()",
		    "index pointer is outside the delete space", e.getMessage());
	}
    }

    @Test
    public void testExecute() {
	UndoableCommand deleteObject;
	AbstractTask deletedTask;
	List<AbstractTask> deleteReturn, origTaskList;
	int deleteIndex;

	// wholeTaskList is null
	deleteObject = new Delete(taskList, 1);
	try {
	    deleteReturn = deleteObject.execute(null);
	    assertFalse("execute does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for null whole task list",
		    "taskList cannot be empty or null", e.getMessage());
	}

	// whole task list is empty
	deleteObject = new Delete(taskList, 1);
	try {
	    deleteReturn = deleteObject.execute(new Vector<AbstractTask>());
	    assertFalse("execute does not throw exception", true);
	} catch (IllegalArgumentException e) {
	    assertEquals("correct exception for empty whole task list",
		    "taskList cannot be empty or null", e.getMessage());
	}

	// Delete first element
	deleteIndex = 1;
	deleteObject = new Delete(taskList, deleteIndex);
	deletedTask = taskList.get(deleteIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	deleteReturn = deleteObject.execute(taskList);

	assertEquals("Deleted task returned in list", deleteReturn.get(0),
		deletedTask);
	assertTrue("Size of task list decreases by 1",
		taskList.size() == (origTaskList.size() - 1));
	assertFalse("deleted task not in taskList",
		taskList.contains(deletedTask));

	// Delete last element
	deleteIndex = taskList.size();
	deleteObject = new Delete(taskList, deleteIndex);
	deletedTask = taskList.get(deleteIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	deleteReturn = deleteObject.execute(taskList);

	assertEquals("Deleted task returned in list", deleteReturn.get(0),
		deletedTask);
	assertTrue("Size of task list decreases by 1",
		taskList.size() == (origTaskList.size() - 1));
	assertFalse("deleted task not in taskList",
		taskList.contains(deletedTask));

	// Delete middle element
	deleteIndex = 2;
	deleteObject = new Delete(taskList, deleteIndex);
	deletedTask = taskList.get(deleteIndex - 1);
	origTaskList = (Vector<AbstractTask>) taskList.clone();
	deleteReturn = deleteObject.execute(taskList);

	assertEquals("Deleted task returned in list", deleteReturn.get(0),
		deletedTask);
	assertTrue("Size of task list decreases by 1",
		taskList.size() == (origTaskList.size() - 1));
	assertFalse("deleted task not in taskList",
		taskList.contains(deletedTask));
    }

    @Test
    public void testUndo() {
	List<AbstractTask> origTaskList = (Vector<AbstractTask>) taskList
		.clone();
	Stack<UndoableCommand> undoStack = new Stack<>();

	for(int i=0;i<origTaskList.size();i++){
	    UndoableCommand deleteObject = new Delete(taskList,1);
	    deleteObject.execute(taskList);
	    undoStack.push(deleteObject);
	}
	
	for(int i=0;i<origTaskList.size();i++){
		undoStack.pop().undo();
	}
	
	assertTrue(
		"undoed taskList == original list",
		taskList.containsAll(origTaskList)
			&& origTaskList.containsAll(taskList));
    }

}
