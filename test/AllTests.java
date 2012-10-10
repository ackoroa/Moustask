import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DeadlineTaskTest.class, DeleteTest.class,
	FloatingTaskTest.class, TimedTaskTest.class })
public class AllTests {

}
