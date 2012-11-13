//@author A0087171A
import static org.junit.Assert.*;

import java.util.Vector;
import java.util.Calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.junit.BeforeClass;
import org.junit.Test;

public class SearchTest {
	static Vector<AbstractTask> taskList;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		taskList = new Vector<AbstractTask>();
		// add a few tasks into the list to search for
		taskList.add(new FloatingTask("eat","Crystal Jade"));

		taskList.add(new DeadlineTask("finish eating", "2012-01-12 18:00"));

		taskList.add(new TimedTask("must eat", "2012-01-12 18:00",
				"2012-01-12 20:00", "KFC"));

		taskList.add(new TimedTask("eat lunch", "2012-01-15 13:00", "2012-01-15 15:00","mcdonalds"));

		taskList.add(new DeadlineTask("project","2013-03-05 10:00"));

		taskList.add(new FloatingTask("read book"));

		DateTime tuesdayDate	= new DateTime("tuesday");
		String taskDate 		= tuesdayDate.generateDateTime(tuesdayDate.validateDateTime());

		taskList.add(new DeadlineTask("read tuesday with morrie", taskDate));
	}

	@Test
	public void testExecute() throws Exception {
		// search by keywords in description
		Search searchObject = new Search("eat");
		Vector<AbstractTask> searchResults 		= (Vector<AbstractTask>) searchObject.execute(taskList);

		Vector<AbstractTask> expectedResults 	= new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));

		assertEquals("description search successful", expectedResults, searchResults);

		// search by venue
		searchObject 	= new Search(".venue mcdonalds");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(3));

		assertEquals("venue search successful", expectedResults, searchResults);

		// search by category
		searchObject 	= new Search(".category timed");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));

		assertEquals("category search successful", expectedResults, searchResults);

		// search by status
		searchObject 	= new Search(".status undone");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));
		expectedResults.add(taskList.get(4));
		expectedResults.add(taskList.get(5));
		expectedResults.add(taskList.get(6));

		assertEquals("status search successful", expectedResults, searchResults);

		// search for a free slot using hours
		searchObject 	= new Search(".free 2 .hours");

		expectedResults = new Vector<AbstractTask>();

		// intialise the free slot to be returned
		DateFormat dateFormat 	= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar calendar 		= Calendar.getInstance();
		calendar.setTime(calendar.getTime());
		String StartDate 		= dateFormat.format(calendar.getTime());
		calendar.add(Calendar.HOUR, 2);
		String EndDate 			= dateFormat.format(calendar.getTime());
		taskList.add(new TimedTask("task 2", StartDate, EndDate));

		StartDate 		= dateFormat.format(calendar.getTime());
		calendar.add(Calendar.HOUR, 2);
		EndDate 		= dateFormat.format(calendar.getTime());

		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults.add(new TimedTask("FREE SLOT", StartDate, EndDate));

		assertEquals("time frame search successful", expectedResults, searchResults);
		
		// search for a free slot using days
		searchObject 	= new Search(".free 2 .days");

		expectedResults = new Vector<AbstractTask>();

		calendar.add(Calendar.HOUR, -2);
		StartDate 		= dateFormat.format(calendar.getTime());
		calendar.add(Calendar.DATE, 2);
		EndDate 		= dateFormat.format(calendar.getTime());

		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults.add(new TimedTask("FREE SLOT", StartDate, EndDate));

		assertEquals("time frame search successful", expectedResults, searchResults);
		// remove the task in the tasklist that was added to test for free slot
		taskList.remove(taskList.lastElement());


		// search within a time frame
		searchObject 	= new Search(".from 2012-01-10 00:00 .to 2012-01-15 15:00");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));

		assertEquals("time frame search successful", expectedResults, searchResults);

		// different modes of searching within a time frame
		searchObject 	= new Search(".from 2012-01-15 13:00 .to 2012-01-15 15:00");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(3));

		assertEquals("time frame search successful", expectedResults, searchResults);

		// not specifying the start time
		searchObject 	= new Search(".from 2012-01-10 .to 2012-01-15 15:00");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));

		assertEquals("time frame search successful", expectedResults, searchResults);

		// not specifying the end time
		searchObject 	= new Search(".from 2012-01-10 00:00 .to 2012-01-15");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));

		assertEquals("time frame search successful", expectedResults, searchResults);

		// searching by a deadline
		searchObject 	= new Search(".by 9 .months");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(4));
		expectedResults.add(taskList.get(6));

		assertEquals("by date search successful", expectedResults, searchResults);

		// searching by a deadline using the day of the week
		searchObject 	= new Search(".by tuesday");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(6));

		assertEquals("by date search successful", expectedResults, searchResults);

		// chain search using .and, meaning to include results that only match both searches
		searchObject 	= new Search(".from 2012-01-10 00:00 .to 2012-01-15 .and .category timed");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));

		assertEquals("multiple time frame search successful", expectedResults, searchResults);

		// chain search using .not, meaning to exclude results that matches the second search
		searchObject 	= new Search(".category deadline .not .from 2012-01-10 00:00 .to 2012-01-15  ");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(4));
		expectedResults.add(taskList.get(6));

		assertEquals("multiple time frame search successful", expectedResults, searchResults);

		// multiple chain searches
		searchObject 	= new Search(".by 5 .days .or eat .not .status done .and .category floating");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));

		assertEquals("multiple search successful", expectedResults, searchResults);

		searchObject 	= new Search("eat .not eating");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));

		assertEquals("not search successful", expectedResults, searchResults);

		// checking for duplicate tasks
		searchObject 	= new Search("eat .and eat");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);

		expectedResults = new Vector<AbstractTask>();
		expectedResults.add(taskList.get(0));
		expectedResults.add(taskList.get(1));
		expectedResults.add(taskList.get(2));
		expectedResults.add(taskList.get(3));

		assertEquals("and search successful", expectedResults, searchResults);

		// The tests below is to check for errors, garbage searches and exceptions
		searchObject 	= new Search(".........................................................");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();

		assertEquals("garbage search successful", expectedResults, searchResults);

		searchObject 	= new Search(".by 5 .moths");
		searchResults 	= (Vector<AbstractTask>) searchObject.execute(taskList);
		expectedResults = new Vector<AbstractTask>();

		assertEquals("invalid search successful", expectedResults, searchResults);

		searchObject 	= new Search(".from apple orange .to watermelon grape fruit");
		try {
			searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
			assertEquals("correct exception for invalid date format",
					"the end date parameter cannot be missing", e.getMessage());
		}

		searchObject 		= new Search(".from apple orange .to 2012-10-10");
		try {
			searchResults  	= (Vector<AbstractTask>) searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
			assertEquals("correct exception for invalid date format",
					"Invalid date format!", e.getMessage());
		}

		searchObject 		= new Search(".from");
		try {
			searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
			assertEquals("correct exception for invalid date format",
					"Date parameters cannot be empty!", e.getMessage());
		}

		searchObject 		= new Search(".from 2012-10-12 .to");
		try {
			searchObject.execute(taskList);
		} catch (ArrayIndexOutOfBoundsException e) {
			assertEquals("correct exception for invalid date format",
					"the end date parameter cannot be empty or null", e.getMessage());
		}

		searchObject 		= new Search("cat .and .from");
		try {
			searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
			assertEquals("correct exception for invalid date format",
					"Date parameters cannot be empty!", e.getMessage());
		}

		searchObject 		= new Search("cat .and .from 2012-10-12 .to");
		try {
			searchObject.execute(taskList);
		} catch (ArrayIndexOutOfBoundsException e) {
			assertEquals("correct exception for invalid date format",
					"the end date parameter cannot be empty or null", e.getMessage());
		}

		searchObject 		= new Search("cat .and .from 2012-10-12 .to 20121015");
		try {
			searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
			assertEquals("correct exception for invalid date format",
					"the end date parameter cannot be missing", e.getMessage());
		}

		searchObject 		= new Search("cat .not .from 2012-10-12 .to 20121015");
		try {
			searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
			assertEquals("correct exception for invalid date format",
					"the end date parameter cannot be missing", e.getMessage());
		}


		searchObject 		= new Search("cat .not .from");
		try {
			searchObject.execute(taskList);
		} catch (IllegalArgumentException e) {
			assertEquals("correct exception for invalid date format",
					"Date parameters cannot be empty!", e.getMessage());
		}

		searchObject	 	= new Search("cat .not .from 2012-10-12 .to");
		try {
			searchObject.execute(taskList);
		} catch (ArrayIndexOutOfBoundsException e) {
			assertEquals("correct exception for invalid date format",
					"the end date parameter cannot be empty or null", e.getMessage());
		}


		try{
			searchObject 		= new Search(null);
		} catch (NullPointerException e){
			assertEquals("null search", "Search line cannot be empty!",e.getMessage());
		}

	}
}
