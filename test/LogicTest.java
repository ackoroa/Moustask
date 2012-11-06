//@author A0058657N
import static org.junit.Assert.assertEquals;
import java.io.File;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class LogicTest {
	Logic logicObject;
	TypeTaskPair taskResult;
	static Boolean moved = false;

	@Before
	public void setUp1() {
		File mtFile = new File("moustask.txt");
		File tempFile = new File("temp.txt");

		if (mtFile.exists()) {
			mtFile.renameTo(tempFile);
			mtFile.delete();
			moved = true;
		}
		logicObject = Logic.getInstance();
	}

	@Test
	public void testLogic() {
		// //////////////////////////////////////////////////////////////////////////////////////////////////
		// //////// EMPTY TASKLIST
		// /////////////////////////////////////////////////////////////////////////////////////////////////
		// Invalid command
		taskResult = logicObject.processCommand("added");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.INVALID);

		// Undo command
		taskResult = logicObject.processCommand(".undo");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.UNDONULL);

		// Delete command
		taskResult = logicObject.processCommand(".delete 1");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EMPTY);

		// Edit command
		taskResult = logicObject.processCommand(".edit");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EMPTY);

		// Search command
		taskResult = logicObject.processCommand(".search");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EMPTY);

		// Display command
		taskResult = logicObject.processCommand(".display");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EMPTY);

		// Clear command
		taskResult = logicObject.processCommand(".clear");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EMPTY);

		// Undo command
		taskResult = logicObject.processCommand(".undo");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.UNDONULL);

		// Help command
		taskResult = logicObject.processCommand(".help");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.HELP);

		// Exit command
		taskResult = logicObject.processCommand(".exit");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EXIT);

		// Add valid deadline task with venue
		taskResult = logicObject
				.processCommand(".add Task .at Orchard Road .by Monday");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.ADD);

		// //////////////////////////////////////////////////////////////////////////////////////////////////
		// //////// FILLED TASKLIST
		// /////////////////////////////////////////////////////////////////////////////////////////////////
		// Invalid command
		taskResult = logicObject.processCommand("added");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.INVALID);

		// Add valid floating task without venue
		taskResult = logicObject.processCommand(".add Task");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.ADD);

		// Add valid floating task without venue
		taskResult = logicObject.processCommand(".add More Task");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.ADD);

		// Undo command
		taskResult = logicObject.processCommand(".undo");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.UNDOADD);

		// Delete without using Display first
		taskResult = logicObject.processCommand(".delete 2");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.ERROR);

		// Delete with IndexOutOfBoundsException
		taskResult = logicObject.processCommand(".display");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DISPLAY);
		taskResult = logicObject.processCommand(".delete 100");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DELETE);

		// Delete with NumberFormatException
		taskResult = logicObject.processCommand(".display");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DISPLAY);
		taskResult = logicObject.processCommand(".delete sdsadasd");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DELETE);

		// Delete with Display used first
		taskResult = logicObject.processCommand(".display");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DISPLAY);
		taskResult = logicObject.processCommand(".delete 2");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DELETE);

		// Undo command
		taskResult = logicObject.processCommand(".undo");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.UNDODELETE);

		// Add valid floating task without venue
		taskResult = logicObject.processCommand(".add More Task");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.ADD);

		// Edit without using Display first
		taskResult = logicObject.processCommand(".edit 2");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.ERROR);

		// Edit with IndexOutOfBoundsException
		taskResult = logicObject.processCommand(".display");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DISPLAY);
		taskResult = logicObject.processCommand(".edit 100");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EDIT);

		// Edit with NumberFormatException
		taskResult = logicObject.processCommand(".display");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DISPLAY);
		taskResult = logicObject.processCommand(".edit sdsadasd");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EDIT);

		// Edit with Display used first
		taskResult = logicObject.processCommand(".display");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.DISPLAY);
		taskResult = logicObject.processCommand(".edit 2 Edited More Task");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EDIT);

		// Undo command
		taskResult = logicObject.processCommand(".undo");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.UNDOEDIT);

		// Invalid Search
		taskResult = logicObject.processCommand(".search");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.SEARCH);

		// Invalid Search with IllegalArgumentException
		taskResult = logicObject.processCommand(".search .from");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.SEARCH);

		// Invalid Search with ArrayIndexOutOfBoundsException
		taskResult = logicObject.processCommand(".search .by asd.days");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.SEARCH);

		// Search
		taskResult = logicObject.processCommand(".search task");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.SEARCH);

		// Help
		taskResult = logicObject.processCommand(".help");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.HELP);

		// Clear
		taskResult = logicObject.processCommand(".clear");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.CLEAR);

		// Undo command
		taskResult = logicObject.processCommand(".undo");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.UNDOCLEAR);

		// Exit
		taskResult = logicObject.processCommand(".exit");
		assertEquals(taskResult.getType(), TypeTaskPair.Type.EXIT);
	}

	@AfterClass
	public static void tearDownBeforeClass() {
		File mtFile = new File("moustask.txt");
		File tempFile = new File("temp.txt");

		if (moved) {
			mtFile.delete();
			tempFile.renameTo(mtFile);
			tempFile.delete();
		}
	}
}