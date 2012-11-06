//@author A0092101Y
import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

public class TypeTaskPairTest {
    static TypeTaskPair ttpObject;
    
    @BeforeClass
    public static void setUpClass(){
	ttpObject = new TypeTaskPair(TypeTaskPair.Type.ADD, new Vector<AbstractTask>());
    }

    @Test
    public void testGetType() {
	assertEquals("Correct type returned", TypeTaskPair.Type.ADD, ttpObject.getType());
    }

    @Test
    public void testGetTasks() {
	assertEquals("TaskList returned correctly", new Vector<AbstractTask>(), ttpObject.getTasks());
    }
}
