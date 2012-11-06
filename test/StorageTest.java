//@author A0092101Y
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StorageTest {
    static Boolean moved = false;

    @BeforeClass
    public static void setUpBeforeClass() {
	File mtFile = new File("moustask.txt");
	File tempFile = new File("temp.txt");

	if (mtFile.exists()) {
	    mtFile.renameTo(tempFile);
	    mtFile.delete();
	    moved = true;
	}
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

    @Test
    public void testStorage() {
	Storage storage = new Storage();
	File mtFile;

	try {
	    assertEquals("Loading nonexistent file returns empty list",
		    new Vector<AbstractTask>(), storage.loadTaskList());
	} catch (IOException e1) {
	    System.out
		    .println("Cannot load nonexistent file during storage test");
	    System.exit(-1);
	}
	mtFile = new File("moustask.txt");
	assertTrue("Loading nonexistent file creates the file", mtFile.exists());

	List<AbstractTask> taskList = new Vector<AbstractTask>(), readList;
	AbstractTask tempTask;

	tempTask = new FloatingTask("have fun", "everywhere");
	tempTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
	taskList.add(tempTask);

	taskList.add(new FloatingTask("eat"));

	tempTask = new FloatingTask("makan");
	tempTask.setStatus(AbstractTask.Status.DONE);
	taskList.add(tempTask);
	
	tempTask = new DeadlineTask("finish eating", "2012-12-12 18:00");
	tempTask.setStatus(AbstractTask.Status.DONE);
	taskList.add(tempTask);
	
	tempTask = new TimedTask("must eat", "2012-12-12 16:00",
		"2012-12-12 18:00");
	tempTask.setStatus(AbstractTask.Status.DONE);
	taskList.add(tempTask);
	
	taskList.add(new DeadlineTask("reach", "2012-12-12 20:00", "home"));
	taskList.add(new TimedTask("relax", "2012-12-12 20:00",
		"2012-12-12 22:00", "home"));
	
	tempTask = new DeadlineTask("finish eating", "2012-12-12 18:00");
	tempTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
	taskList.add(tempTask);
	
	tempTask = new TimedTask("must eat", "2012-12-12 16:00",
		"2012-12-12 18:00");
	tempTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
	taskList.add(tempTask);
	
	try {
	    storage.writeTaskList(taskList);
	} catch (IOException e) {
	    System.out.println("Cannot write to file during storage test");
	    System.exit(-1);
	}

	mtFile = new File("moustask.txt");
	assertTrue(mtFile.exists());
	assertTrue(mtFile.isFile());

	BufferedReader fin;

	StringBuilder sb = new StringBuilder();
	String line;
	try {
	    fin = new BufferedReader(new FileReader(mtFile));

	    while ((line = fin.readLine()) != null)
		sb.append(line + "\n");

	    assertEquals(
		    "File written correctly",
		    "Floating | have fun | everywhere | IMPOSSIBLE" +
		    "\nFloating | eat | UNDONE" +
		    "\nFloating | makan | DONE" +
		    "\nDeadline | finish eating | 2012-12-12 18:00 | DONE" +
		    "\nTimed | must eat | 2012-12-12 16:00 | 2012-12-12 18:00 | DONE" +
		    "\nDeadline | reach | 2012-12-12 20:00 | home | UNDONE" +
		    "\nTimed | relax | 2012-12-12 20:00 | 2012-12-12 22:00 | home | UNDONE" +
		    "\nDeadline | finish eating | 2012-12-12 18:00 | IMPOSSIBLE" +
		    "\nTimed | must eat | 2012-12-12 16:00 | 2012-12-12 18:00 | IMPOSSIBLE\n",
		    sb.toString());
	} catch (FileNotFoundException e) {
	    System.out.println("Cannot find file during storage test");
	    System.exit(-1);
	} catch (IOException e) {
	    System.out.println("Cannot read file during storage test");
	    System.exit(-1);
	}

	try {
	    readList = storage.loadTaskList();

	    assertTrue(
		    "stored and loaded lists are the same",
		    readList.containsAll(taskList)
			    && taskList.containsAll(readList));
	} catch (IOException e) {
	    System.out.println("Error loading file during storage test");
	    System.exit(-1);
	}
    }
}
