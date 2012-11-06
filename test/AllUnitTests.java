//@author A0092101Y
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddTest.class, ClearTest.class, DeadlineTaskTest.class,
	DeleteTest.class, EditTest.class, FloatingTaskTest.class,
	HelpTest.class, SearchTest.class, StorageTest.class,
	TimedTaskTest.class, TypeTaskPairTest.class })
public class AllUnitTests {

}
