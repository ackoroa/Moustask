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
		
		searchObject = new Search(".from 2012-12-15 13:00 .to 2012-12-15 15:00");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(3));
		
		assertEquals("time frame search successful", expectedResults, searchResults);
		
		searchObject = new Search(".from 2012-12-10 .to 2012-12-15 15:00");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		
		assertEquals("time frame search successful", expectedResults, searchResults);
		
		searchObject = new Search(".from 2012-12-10 00:00 .to 2012-12-15");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		
		assertEquals("time frame search successful", expectedResults, searchResults);
		
		searchObject = new Search(".category timed .and .from 2012-12-10 00:00 .to 2012-12-15");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		
		assertEquals("multiple time frame search successful", expectedResults, searchResults);
		
		searchObject = new Search(".category deadline .not .from 2012-12-10 00:00 .to 2012-12-15  ");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(4));
		
		assertEquals("multiple time frame search successful", expectedResults, searchResults);
		
		searchObject = new Search(".by 5 .months");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		expectedResults.add(taskList.get(4));
		
		assertEquals("by date search successful", expectedResults, searchResults);
		
		searchObject = new Search(".by 5 .days .or eat .not .status done .and .category floating");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		
		
		assertEquals("multiple search successful", expectedResults, searchResults);
		
		searchObject = new Search("eat .not eating");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		
		assertEquals("not search successful", expectedResults, searchResults);
		
		searchObject = new Search("eat .and eat");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		
		assertEquals("and search successful", expectedResults, searchResults);
		
		searchObject = new Search(".........................................................");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		
		assertEquals("garbage search successful", expectedResults, searchResults);
		
		searchObject = new Search(".by 5 .moths");
		searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();
		
		assertEquals("invalid search successful", expectedResults, searchResults);
		
		searchObject = new Search(".from apple orange .to watermelon grape fruit");
		try {
			searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
		    assertEquals("correct exception for invalid date format",
		    		"the end date parameter cannot be missing", e.getMessage());
		}
		
		searchObject = new Search(".from apple orange .to 2012-10-10");
		try {
			searchResults  = (Vector<AbstractTask>) searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
		    assertEquals("correct exception for invalid date format",
		    		"Invalid date format!", e.getMessage());
		}
		
		searchObject = new Search(".from");
		try {
			searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
		    assertEquals("correct exception for invalid date format",
		    		"Date parameters cannot be empty!", e.getMessage());
		}
		
		searchObject = new Search(".from 2012-10-12 .to");
		try {
			searchObject.execute(taskList);
		} catch (ArrayIndexOutOfBoundsException e) {
		    assertEquals("correct exception for invalid date format",
		    		"the end date parameter cannot be empty or null", e.getMessage());
		}
		
		searchObject = new Search(null);
		try{
			searchResults = (Vector<AbstractTask>) searchObject.execute(taskList);
		} catch (NullPointerException e){
			assertEquals("null search", "Search line cannot be empty!",e.getMessage());
		}
		
	}
}