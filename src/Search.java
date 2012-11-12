//@author A0087171A
import java.lang.String;
import java.lang.Integer;

import java.util.Vector;
import java.util.List;
import java.util.Calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Search implements Command {
	private final String OR_SEARCH 					= ".or";
	private final String AND_SEARCH 				= ".and";
	private final String NOT_SEARCH					= ".not";
	private final String VENUE_SEARCH				= ".venue";
	private final String START_TIME_SEARCH 			= ".from";
	private final String END_TIME_SEARCH 			= ".to";
	private final String WITHIN_TIMEFRAME_SEARCH 	= ".by";
	private final String WITHIN_DAYS_SEARCH 		= ".days";
	private final String WITHIN_MONTHS_SEARCH 		= ".months";
	private final String WITHIN_HOURS_SEARCH		= ".hours";
	private final String CATEGORY_SEARCH 			= ".category";
	private final String STATUS_SEARCH 				= ".status";
	private final String FREE_SEARCH				= ".free";

	private String searchQuery;
	private List < AbstractTask > searchResults 	= new Vector < AbstractTask > ();
	private List < AbstractTask > wholeTaskList 	= new Vector < AbstractTask > ();

	private Logging searchLog = new Logging("Search Function");

	public Search(String wholeSearchQuery) throws NullPointerException {
		// Initialise the search query
		searchQuery = wholeSearchQuery;

		if (searchQuery == null) {
			searchLog.addLog(Logging.LoggingLevel.WARNING, "Search(): Search object initialization failed. " +
					"Illegal wholeSearchLine (wholeSearchLine = " + wholeSearchQuery + ")");

			throw new NullPointerException("Search line cannot be empty!");
		}

		searchLog.addLog(Logging.LoggingLevel.INFO, "Search(): Search object initialization successful. " +
				"(searchLine = " + searchQuery + ")");
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// //////// Modes of searching (Chain searches and Simple Search)
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	// execute is the simple search. execute is also the .or search for chain searches
	public List < AbstractTask > execute (List < AbstractTask > taskList) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		intialiseWholeTaskList(taskList);

		// process the search parameters
		String [] allWords 			= searchQuery.split(" ");
		String currentWord		 	= "";
		String searchCommandWord 	= allWords[0];
		String searchWords 			= "";

		boolean isChainSearch 		= false;
		int currentWordIndex 		= 1;

		while (currentWordIndex != allWords.length) {
			currentWord = allWords[currentWordIndex];
			if (chainCommandChecker(currentWord)) {
				isChainSearch 		= true;
				break;
			} else {
				searchWords 		= searchWordsSeparator(searchWords, currentWordIndex, currentWord, allWords);
				currentWordIndex 	= currentWordIndex + 1;
			}
		}

		searchLog.addLog(Logging.LoggingLevel.INFO, "Search.execute(): Search command initialization " +
				"successful. (searchCommand = "	+ searchCommandWord + ")");

		// carry out the search
		searchResults 	= searchCommandExecution(searchCommandWord, searchWords, wholeTaskList, searchResults);
		searchResults 	= removeDuplicateTasks(searchResults);

		// detection of chain searches
		if (isChainSearch) {
			searchLog.addLog(Logging.LoggingLevel.INFO, "Search.execute(): chain command detected. (word = "
					+ currentWord + ")");

			searchResults = chainSearchExecution(currentWord, allWords, currentWordIndex, taskList, searchResults);
		}

		return searchResults;
	}

	// andSearch is for the .and search, and it is strictly a chain command.
	private List < AbstractTask > andSearch (List < AbstractTask > taskList) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		List < AbstractTask > filteredResults = new Vector < AbstractTask > ();

		// process the filter parameters
		String [] allWords 			= searchQuery.split(" ");
		String currentWord 			= "";
		String searchCommandWord 	= allWords[0];
		String searchWords 			= "";

		boolean isChainSearch 		= false;
		int currentWordIndex 		= 1;

		while (currentWordIndex != allWords.length) {
			currentWord = allWords[currentWordIndex];
			if (chainCommandChecker(currentWord)) {
				isChainSearch = true;
				break;
			} else {
				searchWords 		= searchWordsSeparator(searchWords, currentWordIndex, currentWord, allWords);
				currentWordIndex 	= currentWordIndex + 1;
			}
		}

		searchLog.addLog(Logging.LoggingLevel.INFO, "Search.notSearch(): Search command initialization " +
				"successful. (searchCommand = " + searchCommandWord + ")");

		// carry out the filtering of results
		filteredResults = searchCommandExecution(searchCommandWord, searchWords, taskList, filteredResults);

		// detection of chain searches
		if (isChainSearch) {
			searchLog.addLog(Logging.LoggingLevel.INFO, "Search.execute(): chain command detected. " +
					"(word = " + currentWord + ")");

			filteredResults = chainSearchExecution(currentWord, allWords, currentWordIndex, taskList, filteredResults);
		}

		return filteredResults;
	}

	// notSearch is for the .not search, and it is strictly a chain command.
	private List < AbstractTask > notSearch (List < AbstractTask > taskList) throws 
	ArrayIndexOutOfBoundsException, IllegalArgumentException {
		List < AbstractTask > filteredResults = new Vector < AbstractTask > ();

		// process the filter parameters
		String [] words 			= searchQuery.split(" ");
		String currentWord 			= "";
		String searchCommandWord 	= words[0];
		String searchWords 			= "";

		boolean isChainSearch 		= false;
		int currentWordIndex 		= 1;

		while (currentWordIndex != words.length) {
			currentWord = words[currentWordIndex];
			if (chainCommandChecker(currentWord)) {
				isChainSearch = true;
				break;
			} else {
				searchWords 		= searchWordsSeparator(searchWords, currentWordIndex, currentWord, words);
				currentWordIndex 	= currentWordIndex + 1;
			}
		}

		// carry out the filtering of results
		filteredResults = searchCommandExecution(searchCommandWord, searchWords, taskList, filteredResults);
		filteredResults = removeUnwantedTasks(taskList, filteredResults);

		// detection of chain searches
		if (isChainSearch) {
			searchLog.addLog(Logging.LoggingLevel.INFO, "Search.execute(): chain command detected. " +
					"(word = " + currentWord + ")");

			filteredResults = chainSearchExecution(currentWord, words, currentWordIndex, taskList, filteredResults);
		}

		return filteredResults;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////// Helper functions for the simple search and the chain searches
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void intialiseWholeTaskList(List < AbstractTask > TaskList){
		if (wholeTaskList.size() == 0) {
			wholeTaskList = TaskList;
		}
	}

	// searchWordsSeparator helps to separate each word in the search query.
	private String searchWordsSeparator(String searchWords, int currentWordIndex, String currentWord , String[] allWords){
		if (currentWord.equalsIgnoreCase(END_TIME_SEARCH)) {
			if (currentWordIndex + 1 >= allWords.length) {
				searchLog.addLog(Logging.LoggingLevel.WARNING, "Search.notSearch(): search execution " +
						"		failed. Illegal search parameters, missing end date (currentWordIndex =" +
						"" + currentWordIndex + ")");

				throw new ArrayIndexOutOfBoundsException(
						"the end date parameter cannot be empty or null");
			}
			if (!allWords[currentWordIndex + 1].contains("-")) {
				searchLog.addLog(Logging.LoggingLevel.WARNING, "Search.notSearch(): search execution " +
						"failed. Illegal search parameters, missing end date (words[currentWordIndex + 1] =" +
						"" + allWords[currentWordIndex+1] + ")");

				throw new IllegalArgumentException(
						"the end date parameter cannot be missing");
			}
		} else {
			if (currentWordIndex == 1) {
				searchWords = currentWord;
			} else {
				searchWords = searchWords + " " + currentWord;
			}
		}

		return searchWords;
	}

	private boolean chainCommandChecker(String word){
		boolean isChainCommand 	= false;
		if (word.equalsIgnoreCase(OR_SEARCH) || word.equalsIgnoreCase(AND_SEARCH) || word.equalsIgnoreCase(NOT_SEARCH)) {
			isChainCommand 		= true;
		}

		return isChainCommand;
	}

	private List < AbstractTask > chainSearchExecution(String chainCommand, String[] searchWords, int currentWordIndex, List < AbstractTask > currentTaskList, List < AbstractTask > searchResults){
		if (chainCommand.equalsIgnoreCase(OR_SEARCH)) {
			searchQuery = searchWords[currentWordIndex + 1];

			for(int i = currentWordIndex + 2; i < searchWords.length; i++){
				searchQuery = searchQuery + " " + searchWords[i];
			}
			searchResults = execute(currentTaskList);
		} else if (chainCommand.equalsIgnoreCase(AND_SEARCH)) {
			searchQuery = searchWords[currentWordIndex + 1];

			for (int i = currentWordIndex + 2; i < searchWords.length; i++) {
				searchQuery = searchQuery + " " + searchWords[i];
			}
			searchResults = andSearch(searchResults);		
		} else if (chainCommand.equalsIgnoreCase(NOT_SEARCH)) {
			searchQuery = searchWords[currentWordIndex + 1];

			for (int i = currentWordIndex + 2; i < searchWords.length; i++) {
				searchQuery = searchQuery + " " + searchWords[i];
			}
			searchResults = notSearch(searchResults);
		}

		return searchResults;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////// Searching functions
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List < AbstractTask > searchCommandExecution(String searchCommand, String searchWords, List < AbstractTask > taskListForSearching, List < AbstractTask > searchResults){
		if (searchCommand.equalsIgnoreCase(VENUE_SEARCH)) {
			searchResults 	= searchByVenue(searchWords, taskListForSearching, searchResults);
		} else if (searchCommand.equalsIgnoreCase(START_TIME_SEARCH)) {
			searchResults 	= searchFromStartTimeToEndTime(searchWords, taskListForSearching, searchResults);
		} else if (searchCommand.equalsIgnoreCase(WITHIN_TIMEFRAME_SEARCH)) {
			searchResults 	= searchByDeadlineDate(searchWords,taskListForSearching, searchResults);
		} else if (searchCommand.equalsIgnoreCase(CATEGORY_SEARCH)) {
			searchResults 	= searchByCategory(searchWords,taskListForSearching, searchResults);
		} else if (searchCommand.equalsIgnoreCase(STATUS_SEARCH)) {
			searchResults 	= searchByStatus(searchWords,taskListForSearching, searchResults);
		} else if (searchCommand.equalsIgnoreCase(FREE_SEARCH)) {
			searchResults 	= searchByFreeSlot(searchWords,taskListForSearching, searchResults);
		} else {
			searchWords 	= searchCommand + " "  + searchWords ;
			searchResults 	= searchByKeyWords(searchWords, taskListForSearching, searchResults);
		}

		return searchResults;
	}

	private List < AbstractTask > searchByKeyWords(String keyWords, List < AbstractTask > taskListForSearching, List < AbstractTask > searchResults){
		String [] allKeyWords = keyWords.split(" ");
		for (int i = 0; i < taskListForSearching.size(); i++) {
			for (int j = 0; j < allKeyWords.length; j++) {
				// search for each keyword in the description for each task
				if (taskListForSearching.get(i).getDescription().toLowerCase().contains(allKeyWords[j].toLowerCase())) {
					searchResults.add(taskListForSearching.get(i));
				}
			}
		}

		return searchResults;
	}

	private List < AbstractTask > searchByFreeSlot(String durationOfWantedFreeSlot, List < AbstractTask > taskListForSearching, List < AbstractTask > searchResults)
			throws IllegalArgumentException{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar calendar 		= Calendar.getInstance();
		calendar.setTime(calendar.getTime());
		String startSlot = dateFormat.format(calendar.getTime());
		String endSlot;

		// the first slot we have to check is the one starting from the current time
		String[] dateAndTimeOfToday 	= startSlot.split(" ");
		String[] dateOfToday 			= dateAndTimeOfToday[0].split("-");
		String[] TimeOfToday 			= dateAndTimeOfToday[1].split(":");

		int yearOfStartSlot 			= Integer.parseInt(dateOfToday[0]);
		int monthOfStartSlot  			= Integer.parseInt(dateOfToday[1]);
		int dayOfStartSlot  			= Integer.parseInt(dateOfToday[2]);	

		int hourOfStartSlot 			= Integer.parseInt(TimeOfToday[0]);
		int minuteOfStartSlot  			= Integer.parseInt(TimeOfToday[1]);

		int yearOfEndSlot 			= -1;
		int monthOfEndSlot  		= -1;
		int dayOfEndSlot 			= -1;

		int hourOfEndSlot 			= 24;
		int minuteOfEndSlot  		= 60;

		// process the date and time of the tail(end) of the current free slot we are checking for
		String[] wantedFreeSlot = durationOfWantedFreeSlot.split(" ");
		if (wantedFreeSlot[1].equalsIgnoreCase(WITHIN_DAYS_SEARCH)) {
			int daysIncrement 		= Integer.parseInt(wantedFreeSlot[0]);
			calendar.add(Calendar.DATE, daysIncrement);
			endSlot = dateFormat.format(calendar.getTime());
			String[] dateAndTimeOfEndSlot 	= endSlot.split(" ");
			String[] dateOfEndSlot 			= dateAndTimeOfEndSlot[0].split("-");

			yearOfEndSlot 			= Integer.parseInt(dateOfEndSlot[0]);
			monthOfEndSlot  		= Integer.parseInt(dateOfEndSlot[1]);
			dayOfEndSlot  			= Integer.parseInt(dateOfEndSlot[2]);	

			hourOfEndSlot 			= 23;
			minuteOfEndSlot  		= 59;
		} else if (wantedFreeSlot[1].equalsIgnoreCase(WITHIN_HOURS_SEARCH)) {
			int hoursIncrement 		= Integer.parseInt(wantedFreeSlot[0]);
			calendar.add(Calendar.HOUR, hoursIncrement);
			endSlot = dateFormat.format(calendar.getTime());
			String[] dateAndTimeOfEndSlot 	= endSlot.split(" ");
			String[] dateOfEndSlot 			= dateAndTimeOfEndSlot[0].split("-");
			String[] timeOfEndSlot 			= dateAndTimeOfEndSlot[1].split(":");

			yearOfEndSlot 			= Integer.parseInt(dateOfEndSlot[0]);
			monthOfEndSlot  		= Integer.parseInt(dateOfEndSlot[1]);
			dayOfEndSlot  			= Integer.parseInt(dateOfEndSlot[2]);	

			hourOfEndSlot 			= Integer.parseInt(timeOfEndSlot[0]);
			minuteOfEndSlot  		= Integer.parseInt(timeOfEndSlot[1]);
		}

		// invalid commands given
		else{
			throw new IllegalArgumentException("Invalid free date format!");
		}

		if(!validDateChecker(yearOfEndSlot, monthOfEndSlot, dayOfEndSlot, hourOfEndSlot, minuteOfEndSlot)){
			throw new IllegalArgumentException("Invalid free date format!");
		}

		AbstractTask taskInterruptingPreviousFreeSlot = null;
		// if isFreeSlot still remains true after checking the list, the current free slot is a valid free slot
		boolean isFreeSlot = true;

		// search through the lists of task to check for interruption of the current free slot
		for (int i = 0; i < taskListForSearching.size() ; i++) {	
			// checks that can be avoided
			if (taskInterruptingPreviousFreeSlot != null && taskInterruptingPreviousFreeSlot.equals(taskListForSearching.get(i))) {
				continue;
			}

			if (taskListForSearching.get(i).getType().toString().equalsIgnoreCase("timed")) {
				// intialise the parameters for the check
				TimedTask currentTask 			= (TimedTask) taskListForSearching.get(i);

				String[] taskStartDateAndTime 	= currentTask.getStartDate().split(" ");
				String[] taskStartDate 			= taskStartDateAndTime[0].split("-");

				int taskStartYear 				= Integer.parseInt(taskStartDate[0]);
				int taskStartMonth 				= Integer.parseInt(taskStartDate[1]);
				int taskStartDay 				= Integer.parseInt(taskStartDate[2]);

				String[] taskStartTime 			= taskStartDateAndTime[1].split(":");

				int taskStartHour 				= Integer.parseInt(taskStartTime[0]);
				int taskStartMinute 			= Integer.parseInt(taskStartTime[1]);

				String[] taskEndDateAndTime 	= currentTask.getEndDate().split(" ");
				String[] taskEndDate 			= taskEndDateAndTime[0].split("-");

				int taskEndYear 				= Integer.parseInt(taskEndDate[0]);
				int taskEndMonth 				= Integer.parseInt(taskEndDate[1]);
				int taskEndDay 					= Integer.parseInt(taskEndDate[2]);

				String[] taskEndTime 			= taskEndDateAndTime[1].split(":");

				int taskEndHour 				= Integer.parseInt(taskEndTime[0]);
				int taskEndMinute 				= Integer.parseInt(taskEndTime[1]);

				// checks that can be avoided
				if (dateComparator(taskEndYear, taskEndMonth, taskEndDay, yearOfStartSlot, monthOfStartSlot, dayOfStartSlot)) {
					if (sameDayChecker(taskEndYear, taskEndMonth, taskEndDay, yearOfStartSlot, monthOfStartSlot, dayOfStartSlot)) {
						if (timeComparator(taskEndHour, taskEndMinute, hourOfStartSlot, minuteOfStartSlot)) {
							continue;
						}
					} else {
						continue;
					}
				}

				// the various checks for interruption. Interruption occurs when either the task cuts into the free slot or the slot
				// cuts into the free slot. So we have to check for all cases.
				if (withinTimeFrameComparator(taskStartYear, taskStartMonth, taskStartDay, yearOfStartSlot, monthOfStartSlot, 
						dayOfStartSlot, taskStartHour, taskStartMinute, hourOfStartSlot, minuteOfStartSlot, 
						taskEndYear, taskEndMonth,  taskEndDay, taskEndHour, taskEndMinute)) {
					if (sameDayChecker(taskEndYear, taskEndMonth, taskEndDay, yearOfStartSlot, monthOfStartSlot, dayOfStartSlot)) {
						if (timeComparator(taskEndHour, taskEndMinute, hourOfStartSlot, minuteOfStartSlot)) {
							continue;
						} else {
							isFreeSlot 	= false;
						}
					} else {
						isFreeSlot 		= false;
					}
				}

				if (withinTimeFrameComparator(taskStartYear, taskStartMonth, taskStartDay, yearOfEndSlot, monthOfEndSlot, 
						dayOfEndSlot, taskStartHour, taskStartMinute, hourOfEndSlot, minuteOfEndSlot, 
						taskEndYear, taskEndMonth,  taskEndDay, taskEndHour, taskEndMinute)) {
					if (sameDayChecker(taskEndYear, taskEndMonth, taskEndDay, yearOfStartSlot, monthOfStartSlot, dayOfStartSlot)) {
						if (timeComparator(taskEndHour, taskEndMinute, hourOfStartSlot, minuteOfStartSlot)) {
							continue;
						} else {
							isFreeSlot 	= false;
						}
					} else {
						isFreeSlot 		= false;
					}
				}
				if (withinTimeFrameComparator(yearOfStartSlot, monthOfStartSlot, dayOfStartSlot, taskStartYear, taskStartMonth,   
						taskStartDay, hourOfStartSlot, minuteOfStartSlot, taskStartHour, taskStartMinute, 
						yearOfEndSlot, monthOfEndSlot, dayOfEndSlot,hourOfEndSlot, minuteOfEndSlot)) {
					if (sameDayChecker(taskEndYear, taskEndMonth, taskEndDay, yearOfStartSlot, monthOfStartSlot, dayOfStartSlot)) {
						if (timeComparator(taskEndHour, taskEndMinute, hourOfStartSlot, minuteOfStartSlot)) {
							continue;
						} else {
							isFreeSlot 	= false;
						}
					} else {
						isFreeSlot 		= false;
					}
				}

				if (withinTimeFrameComparator(yearOfStartSlot, monthOfStartSlot, dayOfStartSlot, taskEndYear, taskEndMonth,   
						taskEndDay, hourOfStartSlot, minuteOfStartSlot, taskEndHour, taskEndMinute, 
						yearOfEndSlot, monthOfEndSlot, dayOfEndSlot,hourOfEndSlot, minuteOfEndSlot)) {
					if (sameDayChecker(taskEndYear, taskEndMonth, taskEndDay, yearOfStartSlot, monthOfStartSlot, dayOfStartSlot)) {
						if (timeComparator(taskEndHour, taskEndMinute, hourOfStartSlot, minuteOfStartSlot)) {
							continue;
						} else {
							isFreeSlot 	= false;
						}
					} else {
						isFreeSlot		= false;
					}
				}

				// current free slot is invalid, we have to adjust the free slot to avoid interruptions by tasks' timing
				if (isFreeSlot == false) {
					// reset the check parameters
					isFreeSlot 	= true;
					i 			= 0;
					taskInterruptingPreviousFreeSlot 	= currentTask;

					// adjust the free slot
					startSlot	= currentTask.getEndDate();

					String[] dateAndTimeOfStart 	= startSlot.split(" ");
					String[] dateOfStart			= dateAndTimeOfStart[0].split("-");
					String[] TimeOfStart			= dateAndTimeOfStart[1].split(":");

					yearOfStartSlot 			= Integer.parseInt(dateOfStart[0]);
					monthOfStartSlot  			= Integer.parseInt(dateOfStart[1]);
					dayOfStartSlot  			= Integer.parseInt(dateOfStart[2]);	

					hourOfStartSlot 			= Integer.parseInt(TimeOfStart[0]);
					minuteOfStartSlot  			= Integer.parseInt(TimeOfStart[1]);

					try {
						calendar.setTime(dateFormat.parse(startSlot));
					} catch (ParseException e) {
						System.out.println("Error occured while searching for a free slot, please try again.");
						searchLog.addLog(Logging.LoggingLevel.WARNING, "Search.searchByFreeSlot(): Searching for a free slot failed." +
								"	Invalid time parsing (parse time = " + startSlot + ")");
						
						return searchResults;
					}

					if (wantedFreeSlot[1].equalsIgnoreCase(WITHIN_DAYS_SEARCH)) {
						int daysIncrement 		= Integer.parseInt(wantedFreeSlot[0]);
						calendar.add(Calendar.DATE, daysIncrement);
						endSlot = dateFormat.format(calendar.getTime());
						String[] dateAndTimeOfEndSlot 	= endSlot.split(" ");
						String[] dateOfEndSlot 			= dateAndTimeOfEndSlot[0].split("-");

						yearOfEndSlot 			= Integer.parseInt(dateOfEndSlot[0]);
						monthOfEndSlot  		= Integer.parseInt(dateOfEndSlot[1]);
						dayOfEndSlot  			= Integer.parseInt(dateOfEndSlot[2]);	

						hourOfEndSlot 			= 23;
						minuteOfEndSlot  		= 59;
					} else if (wantedFreeSlot[1].equalsIgnoreCase(WITHIN_HOURS_SEARCH)) {
						int hoursIncrement 		= Integer.parseInt(wantedFreeSlot[0]);
						calendar.add(Calendar.HOUR, hoursIncrement);
						endSlot = dateFormat.format(calendar.getTime());
						String[] dateAndTimeOfEndSlot 	= endSlot.split(" ");
						String[] dateOfEndSlot 			= dateAndTimeOfEndSlot[0].split("-");
						String[] timeOfEndSlot 			= dateAndTimeOfEndSlot[1].split(":");

						yearOfEndSlot 			= Integer.parseInt(dateOfEndSlot[0]);
						monthOfEndSlot  		= Integer.parseInt(dateOfEndSlot[1]);
						dayOfEndSlot  			= Integer.parseInt(dateOfEndSlot[2]);	

						hourOfEndSlot 			= Integer.parseInt(timeOfEndSlot[0]);
						minuteOfEndSlot  		= Integer.parseInt(timeOfEndSlot[1]);
					}
				}
			}
		}
		// create a free slot and returns the caller
		TimedTask freeSlot = new TimedTask("FREE SLOT", startSlot, endSlot);

		searchResults.add(freeSlot);
		return searchResults;
	}

	private List < AbstractTask > searchByDeadlineDate(String deadlineDate, List < AbstractTask > taskListForSearching, List < AbstractTask > searchResults){
		// intialise the deadline parameters for checking
		DateFormat dateFormat 	= new SimpleDateFormat("yyyy MM dd");
		Calendar calendar 		= Calendar.getInstance();
		calendar.setTime(calendar.getTime());

		String[] dateOfToday 	= dateFormat.format(calendar.getTime()).split(" ");
		int yearOfToday 		= Integer.parseInt(dateOfToday[0]);
		int monthOfToday 		= Integer.parseInt(dateOfToday[1]);
		int dayOfToday 			= Integer.parseInt(dateOfToday[2]);

		int yearOfDeadline 		= 0;
		int monthOfDeadline 	= 0;
		int dayOfDeadline 		= 0;

		String[] deadlineWords 	= deadlineDate.split(" ");
		if (deadlineWords.length > 1) {
			if (deadlineWords[1].equalsIgnoreCase(WITHIN_DAYS_SEARCH)) {
				// user searches by number of days
				int daysIncrement 		= Integer.parseInt(deadlineWords[0]);
				calendar.add(Calendar.DATE, daysIncrement);
				String[] dateOfDeadline = dateFormat.format(calendar.getTime()).split(" ");

				yearOfDeadline 			= Integer.parseInt(dateOfDeadline[0]);
				monthOfDeadline 		= Integer.parseInt(dateOfDeadline[1]);
				dayOfDeadline 			= Integer.parseInt(dateOfDeadline[2]);	
			} else if (deadlineWords[1].equalsIgnoreCase(WITHIN_MONTHS_SEARCH)) {
				// user searches by number of weeks
				int monthIncrement 		= Integer.parseInt(deadlineWords[0]);
				calendar.add(Calendar.MONTH, monthIncrement);
				String[] dateOfDeadline = dateFormat.format(calendar.getTime()).split(" ");

				yearOfDeadline 			= Integer.parseInt(dateOfDeadline[0]);
				monthOfDeadline 		= Integer.parseInt(dateOfDeadline[1]);
				dayOfDeadline 			= Integer.parseInt(dateOfDeadline[2]);
			}
		} else if (validDayChecker(deadlineWords[0])) {
			// user searches by day of the week
			DateTime dayOfTheWeek 			= new DateTime(deadlineWords[0]);
			dayOfTheWeek.generateDateTime(dayOfTheWeek.validateDateTime());
			String[] dateAndTimeOfDeadline 	= dayOfTheWeek.getDateTime().split(" ");		
			String[] dateOfDeadline 		= dateAndTimeOfDeadline[0].split("-");

			yearOfDeadline 					= Integer.parseInt(dateOfDeadline[0]);
			monthOfDeadline 				= Integer.parseInt(dateOfDeadline[1]);
			dayOfDeadline 					= Integer.parseInt(dateOfDeadline[2]);
		}

		for (int i = 0; i < taskListForSearching.size() ; i++) {
			// for each task in the list, if it falls within the deadline set by the user then it is a valid task
			if (taskWithDateChecker(taskListForSearching.get(i))) {
				if (taskListForSearching.get(i).getType().toString().equalsIgnoreCase("timed")) {
					TimedTask currentTask 			= (TimedTask) taskListForSearching.get(i);
					String[] taskStartDateAndTime 	= currentTask.getStartDate().split(" ");
					String[] taskStartDate 			= taskStartDateAndTime[0].split("-");

					int taskStartYear 				= Integer.parseInt(taskStartDate[0]);
					int taskStartMonth 				= Integer.parseInt(taskStartDate[1]);
					int taskStartDay 				= Integer.parseInt(taskStartDate[2]);

					String[] taskEndDateAndTime 	= currentTask.getEndDate().split(" ");
					String[] taskEndDate 			= taskEndDateAndTime[0].split("-");

					int taskEndYear 				= Integer.parseInt(taskEndDate[0]);
					int taskEndMonth 				= Integer.parseInt(taskEndDate[1]);
					int taskEndDay 					= Integer.parseInt(taskEndDate[2]);

					if (byDateComparator(taskStartYear, taskStartMonth, taskStartDay, yearOfDeadline, monthOfDeadline, 
							dayOfDeadline, yearOfToday, monthOfToday,dayOfToday)) {
						searchResults.add(taskListForSearching.get(i));
					}
					if (byDateComparator(taskEndYear, taskEndMonth, taskEndDay, yearOfDeadline, monthOfDeadline, 
							dayOfDeadline, yearOfToday, monthOfToday,dayOfToday)) {
						searchResults.add(taskListForSearching.get(i));
					}
				} else if (taskListForSearching.get(i).getType().toString().equalsIgnoreCase("deadline")) {
					DeadlineTask currentTask 		= (DeadlineTask) taskListForSearching.get(i);

					String[] taskEndDateAndTime 	= currentTask.getEndDate().split(" ");
					String[] taskEndDate 			= taskEndDateAndTime[0].split("-");

					int taskEndYear 				= Integer.parseInt(taskEndDate[0]);
					int taskEndMonth 				= Integer.parseInt(taskEndDate[1]);
					int taskEndDay 					= Integer.parseInt(taskEndDate[2]);

					if (byDateComparator(taskEndYear, taskEndMonth, taskEndDay, yearOfDeadline, monthOfDeadline, 
							dayOfDeadline, yearOfToday, monthOfToday,dayOfToday)) {
						searchResults.add(taskListForSearching.get(i));
					}
				}
			}
		}

		return searchResults;
	}


	private List < AbstractTask > searchFromStartTimeToEndTime(String timeFrame, List < AbstractTask > taskListForSearching, List < AbstractTask > searchResults) 
			throws IllegalArgumentException{
		// intialise the search parameters
		String [] datesAndTimes = timeFrame.split(" ");

		if (datesAndTimes.length < 2) {
			searchLog.addLog(Logging.LoggingLevel.WARNING, "Search.searchFromStartTimeToEndTime(): " +
					"Searching within a timeframe failed. Invalid time frame (time = " + timeFrame + ")");

			throw new IllegalArgumentException("Date parameters cannot be empty!");
		}

		int endYear 	= -1;
		int endMonth 	= -1;
		int endDay 		= -1;
		int endHour 	= 0;
		int endMinute 	= 0;
		int startYear 	= -1;
		int startMonth 	= -1;
		int startDay 	= -1;
		int startHour 	= 0;
		int startMinute = 0;

		// if the user doesn't specify time, we shall assume that it's 00:00 for start time and
		// 23:59 for end time
		if (datesAndTimes[0].contains("-")) {
			String[] startDate 	= datesAndTimes[0].split("-");

			startYear 			= Integer.parseInt(startDate[0]);
			startMonth 			= Integer.parseInt(startDate[1]);
			startDay 			= Integer.parseInt(startDate[2]);
		}

		if (datesAndTimes[1].contains(":")) {
			String[] startTime 	= datesAndTimes[1].split(":");

			startHour 			= Integer.parseInt(startTime[0]);
			startMinute 		= Integer.parseInt(startTime[1]);
		} else {
			startHour 	= 00;
			startMinute = 00;
		}

		if (datesAndTimes[1].contains("-")) {
			String[] endDate 	= datesAndTimes[1].split("-");

			endYear 			= Integer.parseInt(endDate[0]);
			endMonth 			= Integer.parseInt(endDate[1]);
			endDay 				= Integer.parseInt(endDate[2]);
		}
		if (datesAndTimes.length >= 3 && datesAndTimes[2].contains("-")) {
			String[] endDate 	= datesAndTimes[2].split("-");

			endYear 			= Integer.parseInt(endDate[0]);
			endMonth 			= Integer.parseInt(endDate[1]);
			endDay 				= Integer.parseInt(endDate[2]);
		}
		if (datesAndTimes.length >= 3 && datesAndTimes[2].contains(":")) {
			String[] endTime 	= datesAndTimes[2].split(":");

			endHour 			= Integer.parseInt(endTime[0]);
			endMinute 			= Integer.parseInt(endTime[1]);
		} else if (datesAndTimes.length >= 4 && datesAndTimes[3].contains(":")) {
			String[] endTime 	= datesAndTimes[3].split(":");

			endHour 			= Integer.parseInt(endTime[0]);
			endMinute 			= Integer.parseInt(endTime[1]);
		} else {
			endHour 			= 23;
			endMinute 			= 59;
		}

		if (!validDateChecker(startYear, startMonth, startDay, startHour, startMinute) || !validDateChecker(endYear, endMonth, endDay, endHour, endMinute)) {
			searchLog.addLog(Logging.LoggingLevel.WARNING, "Search.searchFromStartTimeToEndTime(): " +
					"Searching within a timeframe failed. Invalid time frame (time = " + timeFrame + ")");

			throw new IllegalArgumentException("Invalid date format!");
		}

		for (int i = 0; i < taskListForSearching.size() ; i++) {
			// for each task in the list, if it falls within the time frame then we shall add it to the results
			if (taskWithDateChecker(taskListForSearching.get(i))) {
				if (taskListForSearching.get(i).getType().toString().equalsIgnoreCase("timed")) {
					TimedTask currentTask 			= (TimedTask) taskListForSearching.get(i);

					String[] taskStartDateAndTime 	= currentTask.getStartDate().split(" ");
					String[] taskStartDate 			= taskStartDateAndTime[0].split("-");

					int taskStartYear 				= Integer.parseInt(taskStartDate[0]);
					int taskStartMonth 				= Integer.parseInt(taskStartDate[1]);
					int taskStartDay 				= Integer.parseInt(taskStartDate[2]);

					String[] taskStartTime 			= taskStartDateAndTime[1].split(":");

					int taskStartHour 				= Integer.parseInt(taskStartTime[0]);
					int taskStartMinute 			= Integer.parseInt(taskStartTime[1]);

					String[] taskEndDateAndTime 	= currentTask.getEndDate().split(" ");
					String[] taskEndDate 			= taskEndDateAndTime[0].split("-");

					int taskEndYear 				= Integer.parseInt(taskEndDate[0]);
					int taskEndMonth 				= Integer.parseInt(taskEndDate[1]);
					int taskEndDay 					= Integer.parseInt(taskEndDate[2]);

					String[] taskEndTime 			= taskEndDateAndTime[1].split(":");

					int taskEndHour 				= Integer.parseInt(taskEndTime[0]);
					int taskEndMinute 				= Integer.parseInt(taskEndTime[1]);

					if (withinTimeFrameComparator(startYear, startMonth, startDay, taskStartYear, taskStartMonth, 
							taskStartDay,startHour, startMinute, taskStartHour, taskStartMinute, 
							endYear, endMonth,  endDay, endHour, endMinute)) {
						searchResults.add(taskListForSearching.get(i));
					}

					if (withinTimeFrameComparator(startYear, startMonth, startDay, taskEndYear, taskEndMonth, 
							taskEndDay,startHour, startMinute, taskEndHour, taskEndMinute, 
							endYear, endMonth,  endDay, endHour, endMinute)) {
						searchResults.add(taskListForSearching.get(i));
					}
				} else if (taskListForSearching.get(i).getType().toString().equalsIgnoreCase("deadline")) {
					DeadlineTask currentTask 	= (DeadlineTask) taskListForSearching.get(i);

					String[] taskEndDateAndTime = currentTask.getEndDate().split(" ");
					String[] taskEndDate 		= taskEndDateAndTime[0].split("-");

					int taskEndYear 			= Integer.parseInt(taskEndDate[0]);
					int taskEndMonth 			= Integer.parseInt(taskEndDate[1]);
					int taskEndDay 				= Integer.parseInt(taskEndDate[2]);

					String[] taskEndTime 		= taskEndDateAndTime[1].split(":");

					int taskEndHour 			= Integer.parseInt(taskEndTime[0]);
					int taskEndMinute 			= Integer.parseInt(taskEndTime[1]);


					if (withinTimeFrameComparator(startYear, startMonth, startDay, taskEndYear, taskEndMonth, 
							taskEndDay, startHour, startMinute, taskEndHour, taskEndMinute, endYear, 
							endMonth,  endDay, endHour, endMinute)) {
						searchResults.add(taskListForSearching.get(i));
					}
				}
			}
		}

		searchResults = removeDuplicateTasks(searchResults);
		return searchResults;
	}

	private List < AbstractTask > searchByVenue(String venue, List < AbstractTask > taskListForSearching, List < AbstractTask > searchResults){
		for (int i = 0; i < taskListForSearching.size() ; i++) {
			// check each task in the list for valid venue
			if (taskListForSearching.get(i).getVenue().equalsIgnoreCase(venue)) {
				searchResults.add(taskListForSearching.get(i));
			}
		}

		return searchResults;
	}

	private List < AbstractTask > searchByCategory(String category, List < AbstractTask > taskListForSearching, List < AbstractTask > searchResults){
		for (int i = 0; i < taskListForSearching.size() ; i++) {
			if (taskListForSearching.get(i).getType().toString().equalsIgnoreCase(category)) {
				searchResults.add(taskListForSearching.get(i));
			}
		}

		return searchResults;
	}

	private List < AbstractTask > searchByStatus(String status, List < AbstractTask > taskListForSearching, List < AbstractTask > searchResults){
		for (int i = 0; i < taskListForSearching.size() ; i++) {
			if (taskListForSearching.get(i).getStatus().toString().equalsIgnoreCase(status)) {
				searchResults.add(taskListForSearching.get(i));
			}
		}

		return searchResults;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////// Helper functions for the different types of search execution
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List < AbstractTask > removeUnwantedTasks (List < AbstractTask > taskList, List < AbstractTask > unwantedTasks){
		List < AbstractTask > filteredResults = new Vector < AbstractTask >();

		for (int i = 0; i < taskList.size(); i++) {
			boolean isWanted = true;
			for (int j = 0; j < unwantedTasks.size(); j++) {
				if (taskList.get(i).equals(unwantedTasks.get(j))) {
					isWanted = false;
					break;
				}
			}
			if (isWanted) {
				filteredResults.add(taskList.get(i));
			}
		}
		return filteredResults;
	}

	private List < AbstractTask > removeDuplicateTasks (List < AbstractTask > taskList){
		List < AbstractTask > filteredResults = new Vector < AbstractTask >();

		for (int i = 0; i < taskList.size(); i++) {
			boolean isWanted = true;
			for (int j = i + 1; j < taskList.size(); j++) {
				if (taskList.get(i).equals(taskList.get(j))) {
					isWanted = false;
					break;
				}
			}
			if (isWanted) {
				filteredResults.add(taskList.get(i));
			}
		}

		return filteredResults;
	}
	private boolean sameDayChecker(int yearOfFirstDate, int monthOfFirstDate, int dayOfFirstDate, int yearOfSecondDate, 
			int monthOfSecondDate, int dayOfSecondDate){
		boolean isSameDay = false;
		if (yearOfFirstDate == yearOfSecondDate && monthOfFirstDate == monthOfSecondDate && dayOfFirstDate == dayOfSecondDate) {
			isSameDay = true;
		}

		return isSameDay;
	}

	private boolean dateComparator(int earlierYear, int earlierMonth, int earlierDay, int laterYear, 
			int laterMonth, int laterDay){
		boolean isLater = false;
		if (earlierYear <= laterYear) {
			if (earlierYear == laterYear) {
				if (earlierMonth <= laterMonth) {
					if (earlierMonth == laterMonth) {
						if (earlierDay <= laterDay) {
							isLater = true;
						}
					} else {
						isLater = true;
					}
				}
			} else {
				isLater = true;
			}
		}

		return isLater;
	}

	private boolean timeComparator(int earlierHour, int earlierMinute, int laterHour, int laterMinute){
		boolean isLater = false;
		if (earlierHour <= laterHour) {
			if (earlierHour == laterHour) {
				if (earlierMinute <= laterMinute) {
					isLater = true;
				}
			} else {
				isLater = true;
			}
		}

		return isLater;
	}

	private boolean byDateComparator(int taskYear, int taskMonth, int taskDay, int searchByYear, int searchByMonth,
			int searchByDay, int todayYear, int todayMonth, int todayDay){
		boolean isLater = false;
		if (dateComparator(taskYear, taskMonth, taskDay, searchByYear, searchByMonth, searchByDay)) {
			if (dateComparator(todayYear, todayMonth, todayDay ,taskYear, taskMonth, taskDay)) {
				isLater = true;
			}
		}

		return isLater;
	}

	// withinTimeFrameComparator combines many different time checks. It will only return true when a date falls within 
	// a start date and time and an end date and time
	private boolean withinTimeFrameComparator(int timeFrameStartYear, int timeFrameStartMonth, int timeFrameStartDay, 
			int taskYear, int taskMonth, int taskDay, int timeFrameStartHour, int timeFrameStartMinute, 
			int taskHour, int taskMinute, int timeFrameEndYear, int timeFrameEndMonth, 
			int timeFrameEndDay, int timeFrameEndHour, int timeFrameEndMinute){
		boolean isWithinTimeFrame = false;
		if (dateComparator(timeFrameStartYear, timeFrameStartMonth, timeFrameStartDay, taskYear, taskMonth, taskDay)) {
			if (sameDayChecker(timeFrameStartYear, timeFrameStartMonth, timeFrameStartDay, taskYear, taskMonth, taskDay)) {
				if (timeComparator(timeFrameStartHour, timeFrameStartMinute, taskHour, taskMinute)) {
					if (dateComparator(taskYear, taskMonth, taskDay,timeFrameEndYear, timeFrameEndMonth, timeFrameEndDay)) {
						if (sameDayChecker(taskYear, taskMonth, taskDay,timeFrameEndYear, timeFrameEndMonth, timeFrameEndDay)) {
							if (timeComparator( taskHour, taskMinute,timeFrameEndHour, timeFrameEndMinute)) {
								isWithinTimeFrame = true;
							}
						} else {
							isWithinTimeFrame = true;
						}
					}
				}
			} else {
				if (dateComparator(taskYear, taskMonth, taskDay,timeFrameEndYear, timeFrameEndMonth, timeFrameEndDay)) {
					if (sameDayChecker(taskYear, taskMonth, taskDay,timeFrameEndYear, timeFrameEndMonth, timeFrameEndDay)) {
						if (timeComparator( taskHour, taskMinute,timeFrameEndHour, timeFrameEndMinute )) {
							isWithinTimeFrame = true;
						}
					} else {
						isWithinTimeFrame = true;
					}
				}
			}
		}

		return isWithinTimeFrame;
	}


	private boolean validDateChecker(int year, int month, int day, int hour, int minute){
		boolean isValidDate = true;
		if (year == -1 || month == -1 || day == -1 || hour > 23 || minute > 59 ) {
			isValidDate = false;
		}

		return isValidDate;
	}

	private boolean validDayChecker(String word){
		boolean isValidDay = false;
		if (word.equalsIgnoreCase("monday") || word.equalsIgnoreCase("tuesday") || word.equalsIgnoreCase("wednesday") || 
				word.equalsIgnoreCase("thursday") || word.equalsIgnoreCase("friday") || word.equalsIgnoreCase("saturday") || 
				word.equalsIgnoreCase("sunday")) {

			isValidDay = true;
		}

		return isValidDay;
	}

	private boolean taskWithDateChecker(AbstractTask task){
		boolean isTaskWithDate = false;
		if (task.getType().toString().equalsIgnoreCase("timed") || task.getType().toString().equalsIgnoreCase("deadline")) {
			isTaskWithDate = true;
		}

		return isTaskWithDate;
	}
}
