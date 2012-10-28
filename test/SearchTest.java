import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

public class SearchTest {
	static Vector<AbstractTask> taskList;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		taskList = new Vector<AbstractTask>();
		taskList.add(new FloatingTask("eat","Crystal Jade"));

		taskList.add(new DeadlineTask("finish eating", "2012-12-12 18:00"));

		taskList.add(new TimedTask("must eat", "2012-12-12 18:00",
				"2012-12-12 20:00", "KFC"));

		taskList.add(new TimedTask("eat lunch", "2012-12-15 13:00", "2012-12-15 15:00","mcdonalds"));

		taskList.add(new DeadlineTask("project","2013-01-05 10:00"));

		taskList.add(new FloatingTask("read book"));
	}

	@Test
	public void testExecute() throws Exception {
		Search searchObject = new Search("eat");
		Vector<AbstractTask> searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		Vector<AbstractTask> expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		
		assertEquals("description search successful", expectedResults, searchResults);
		
		searchObject = new Search(".venue mcdonalds");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(3));
		
		assertEquals("venue search successful", expectedResults, searchResults);
		
		searchObject = new Search(".category timed");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		
		assertEquals("category search successful", expectedResults, searchResults);
		
		searchObject = new Search(".status undone");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		expectedResults.add(taskList.get(4));
		expectedResults.add(taskList.get(5));
		
		assertEquals("status search successful", expectedResults, searchResults);
		
		searchObject = new Search(".from 2012-12-10 00:00 .to 2012-12-15 15:00");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		
		assertEquals("time frame search successful", expectedResults, searchResults);
		
		searchObject = new Search(".by 5 .months");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		expectedResults.add(taskList.get(4));
		
		assertEquals("by date search successful", expectedResults, searchResults);
		
		searchObject = new Search(".by 5 .months .or eat .not .status done .and .category floating");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		
		assertEquals("multiple search successful", expectedResults, searchResults);
	}
}
