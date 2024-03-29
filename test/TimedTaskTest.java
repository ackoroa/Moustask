//@author A0092101Y
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class TimedTaskTest {
    private static TimedTask firstObject, secondObject;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	firstObject = new TimedTask("eat", "McD", "2012-10-31 15:00");
	secondObject = (TimedTask) firstObject.clone();
    }

    @Test
    public void testClone() {
	assertNotSame("the clone is not the same object", firstObject,
		secondObject);
	assertEquals("the clone have the same field values", firstObject,
		secondObject);
    }
    
    @Test
    public void testHashCode() {
	assertEquals("firstObject.hashCode == secondObject.hashCode()",
		firstObject.hashCode(), secondObject.hashCode());
    }

    @Test
    public void testEqualsObject() {
	assertTrue("firstObject equals secondObject",
		firstObject.equals(secondObject));
    }

    @Test
    public void testSetVenue() {
	TimedTask changedVenue = (TimedTask) firstObject.clone();
	changedVenue.setVenue("BK");
	
	assertEquals("venue changed", "BK", changedVenue.getVenue());
    }
    
    @Test
    public void testGetType() {
	assertEquals("type is timed", AbstractTask.Type.TIMED, firstObject.getType());
    }
    
    @Test
    public void testGetSetStatus() {
	assertEquals("status is initially undone", AbstractTask.Status.UNDONE, firstObject.getStatus());
	
	firstObject.setStatus(AbstractTask.Status.IMPOSSIBLE);
	assertEquals("status changed to impossible", AbstractTask.Status.IMPOSSIBLE, firstObject.getStatus());
	
	firstObject.setStatus(AbstractTask.Status.DONE);
	assertEquals("status changed to done", AbstractTask.Status.DONE, firstObject.getStatus());
	
	firstObject.setStatus(AbstractTask.Status.UNDONE);
	assertEquals("status changed to undone", AbstractTask.Status.UNDONE, firstObject.getStatus());
    }

    
}
