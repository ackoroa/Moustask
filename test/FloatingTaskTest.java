import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class FloatingTaskTest {
    private static FloatingTask firstObject, secondObject;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	firstObject = new FloatingTask("eat");
	secondObject = (FloatingTask) firstObject.clone();
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

}
