import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class DeadlineTaskTest {
    private static DeadlineTask firstObject, secondObject;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	firstObject = new DeadlineTask("eat", "McD", "2012-10-31 15:00");
	secondObject = (DeadlineTask) firstObject.clone();
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
	DeadlineTask changedVenue = (DeadlineTask) firstObject.clone();
	changedVenue.setVenue("BK");
	
	assertEquals("venue changed", "BK", changedVenue.getVenue());
    }

}
