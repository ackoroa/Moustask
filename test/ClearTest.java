import static org.junit.Assert.assertEquals;
import org.junit.Before;
import java.util.Vector;
import org.junit.Test;

public class ClearTest {
	public static Vector<AbstractTask> taskList;

	@Before
	public void setUpBeforeClass() throws Exception {
		taskList = new Vector<AbstractTask>();
		taskList.add(new FloatingTask("I write words", "Home"));

		taskList.add(new DeadlineTask("Do project", "2012-12-12 18:00"));

		taskList.add(new TimedTask("Project Got Time", "2012-12-12 18:00",
				"2012-12-12 20:00", "School"));
	}

	@Test
	public void test() {
		Clear clearTest = new Clear();
		clearTest.execute(taskList);
		assertEquals(taskList.size(), 0);

		clearTest.undo();
		assertEquals(taskList.size(), 3);
	}

}