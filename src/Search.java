import java.lang.String;
import java.lang.Integer;
import java.util.Vector;
import java.util.List;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

// throws NullPointerException, ArrayIndexOutOfBoundsException, IllegalArgumentException
public class Search implements Command {
	private final String OR_SEARCH = ".or";
	private final String AND_SEARCH = ".and";
	private final String NOT_SEARCH = ".not";
	private final String VENUE_SEARCH = ".venue";
	private final String START_TIME_SEARCH = ".from";
	private final String END_TIME_SEARCH = ".to";
	private final String WITHIN_TIMEFRAME_SEARCH = ".by";
	private final String WITHIN_DAYS_SEARCH = ".days";
	private final String WITHIN_MONTHS_SEARCH = ".months";
	private final String CATEGORY_SEARCH = ".category";
	private final String STATUS_SEARCH = ".status";

	private String searchLine;
	private List < AbstractTask > searchResults = new Vector < AbstractTask > ();
	private List < AbstractTask > wholeTaskList = new Vector < AbstractTask > ();
	
	private Logging searchLog = new Logging("Search Function");

	public Search(String wholeSearchLine) throws NullPointerException {
		searchLine = wholeSearchLine;
		if(searchLine == null) {
			searchLog.addLog(Logging.LoggingLevel.WARNING, "Search(): Search object initialization failed. Illegal wholeSearchLine (wholeSearchLine = "
			    + wholeSearchLine + ")");
			
			throw new NullPointerException("Search line cannot be empty!");
		}
		
		searchLog.addLog(Logging.LoggingLevel.INFO, "Search(): Search object initialization successful. (searchLine = "
			    + searchLine + ")");
		// System.out.println(searchLine); for debug
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// //////// Modes of searching (Chain Commands)
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	public List < AbstractTask > execute (List < AbstractTask > taskList) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		maintainWholeTaskList(taskList);
		
		String [] words = searchLine.split(" ");
		/* for(int i = 0; i < words.length; i++ ){		for debug
		 * System.out.println(words[i]);
		 * } 
		 */
		String word = "";
		String searchCommand = words[0];
		String searchWords = "";

		boolean isChainCommand = false;
		int currentWordIndex = 1;
		while(currentWordIndex != words.length){
			word = words[currentWordIndex];
			if(chainCommand(word)){
				isChainCommand = true;
				break;
			}
			else{
				if(word.equalsIgnoreCase(END_TIME_SEARCH)){
					if(currentWordIndex + 1 >= words.length){
						searchLog.addLog(Logging.LoggingLevel.WARNING, 
								"Search.andSearch(): search execution failed. Illegal search parameters, missing end date (currentWordIndex =" +
								"" + currentWordIndex + ")");
						
						throw new ArrayIndexOutOfBoundsException("the end date parameter cannot be empty or null");
					}
					if(!words[currentWordIndex + 1].contains("-")){
						searchLog.addLog(Logging.LoggingLevel.WARNING, 
								"Search.andSearch(): search execution failed. Illegal search parameters, missing end date (words[currentWordIndex + 1] =" +
								"" + words[currentWordIndex+1] + ")");
						
						throw new IllegalArgumentException(
							    "the end date parameter cannot be missing");
					}
					if(currentWordIndex + 2 < words.length){
						if(words[currentWordIndex + 2].contains(":")){
							searchWords = searchWords + " " + words[currentWordIndex + 1] + " " + words[currentWordIndex + 2];
							currentWordIndex = currentWordIndex + 3;
						}
						else{
							currentWordIndex = currentWordIndex + 1;
						}
					}
					else{
						searchWords = searchWords + " " + words[currentWordIndex + 1];
						currentWordIndex = currentWordIndex + 2;
					}
				}
				else{
					if(currentWordIndex == 1){
						searchWords = word;
					}
					else{
						searchWords = searchWords + " " + word;
					}
					currentWordIndex = currentWordIndex + 1;
				}
			}
		}
		
		searchLog.addLog(Logging.LoggingLevel.INFO, "Search.execute(): Search command initialization successful. (searchCommand = "
			    + searchCommand + ")");
		
		searchResults = searchCommandExecution(searchCommand, searchWords, wholeTaskList, searchResults);
		searchResults = removeDuplicateTasks(searchResults);

		if(isChainCommand){
			searchLog.addLog(Logging.LoggingLevel.INFO, "Search.execute(): chain command detected. (word = "
				    + word + ")");
			
			searchResults = chainCommandExecution(word, words, currentWordIndex, taskList, searchResults);
		}

		return searchResults;
	}

	public List < AbstractTask > andSearch (List < AbstractTask > taskList) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		List < AbstractTask > filteredResults = new Vector < AbstractTask > ();

		String [] words = searchLine.split(" ");
		String word = "";
		String searchCommand = words[0];
		String searchWords = "";

		boolean isChainCommand = false;
		int currentWordIndex = 1;
		while(currentWordIndex != words.length){
			word = words[currentWordIndex];
			if(chainCommand(word)){
				isChainCommand = true;
				break;
			}
			else{
				if(word.equalsIgnoreCase(END_TIME_SEARCH)){
					if(currentWordIndex + 1 >= words.length){
						searchLog.addLog(Logging.LoggingLevel.WARNING, 
								"Search.execute(): search execution failed. Illegal search parameters, missing end date (currentWordIndex =" +
								"" + currentWordIndex + ")");
						
						throw new ArrayIndexOutOfBoundsException(
							    "the end date parameter cannot be empty or null");
					}
						
					if(!words[currentWordIndex + 1].contains("-")){
						searchLog.addLog(Logging.LoggingLevel.WARNING, 
								"Search.execute(): search execution failed. Illegal search parameters, missing end date (words[currentWordIndex + 1] =" +
								"" + words[currentWordIndex+1] + ")");
						
						throw new IllegalArgumentException(
							    "the end date parameter cannot be missing");
					}
					if(currentWordIndex + 2 < words.length){
						if(words[currentWordIndex + 2].contains(":")){
							searchWords = searchWords + " " + words[currentWordIndex + 1] + " " + words[currentWordIndex + 2];
							currentWordIndex = currentWordIndex + 3;
						}
						else{
							currentWordIndex = currentWordIndex + 1;
						}
					}
					else{
						searchWords = searchWords + " " + words[currentWordIndex + 1];
						currentWordIndex = currentWordIndex + 2;
					}
				}
				else{
					if(currentWordIndex == 1){
						searchWords = word;
					}
					else{
						searchWords = searchWords + " " + word;
					}
					currentWordIndex = currentWordIndex + 1;
				}
			}
		}
		searchLog.addLog(Logging.LoggingLevel.INFO, "Search.notSearch(): Search command initialization successful. (searchCommand = "
			    + searchCommand + ")");

		filteredResults = searchCommandExecution(searchCommand, searchWords, taskList, filteredResults);

		if(isChainCommand){
			searchLog.addLog(Logging.LoggingLevel.INFO, "Search.execute(): chain command detected. (word = "
				    + word + ")");
			
			filteredResults = chainCommandExecution(word, words, currentWordIndex, taskList, filteredResults);
		}
		return filteredResults;
	}

	public List < AbstractTask > notSearch (List < AbstractTask > taskList) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		List < AbstractTask > filteredResults = new Vector < AbstractTask > ();

		String [] words = searchLine.split(" ");
		String word = "";
		String searchCommand = words[0];
		String searchWords = "";

		boolean isChainCommand = false;
		int currentWordIndex = 1;
		while(currentWordIndex != words.length){
			word = words[currentWordIndex];
			if(chainCommand(word)){
				isChainCommand = true;
				break;
			}
			else{
				if(word.equalsIgnoreCase(END_TIME_SEARCH)){
					if(currentWordIndex + 1 >= words.length){
						searchLog.addLog(Logging.LoggingLevel.WARNING, 
								"Search.notSearch(): search execution failed. Illegal search parameters, missing end date (currentWordIndex =" +
								"" + currentWordIndex + ")");
						
						throw new ArrayIndexOutOfBoundsException(
							    "the end date parameter cannot be empty or null");
					}
					if(!words[currentWordIndex + 1].contains("-")){
						searchLog.addLog(Logging.LoggingLevel.WARNING, 
								"Search.notSearch(): search execution failed. Illegal search parameters, missing end date (words[currentWordIndex + 1] =" +
								"" + words[currentWordIndex+1] + ")");
						
						throw new IllegalArgumentException(
							    "the end date parameter cannot be missing");
					}
					if(currentWordIndex + 2 < words.length){
						if(words[currentWordIndex + 2].contains(":")){
							searchWords = searchWords + " " + words[currentWordIndex + 1] + " " + words[currentWordIndex + 2];
							currentWordIndex = currentWordIndex + 3;
						}
						else{
							currentWordIndex = currentWordIndex + 1;
						}
					}
					else{
						searchWords = searchWords + " " + words[currentWordIndex + 1];
						currentWordIndex = currentWordIndex + 2;
					}
				}
				else{
					if(currentWordIndex == 1){
						searchWords = word;
					}
					else{
						searchWords = searchWords + " " + word;
					}
					currentWordIndex = currentWordIndex + 1;
				}
			}
		}

		filteredResults = searchCommandExecution(searchCommand, searchWords, taskList, filteredResults);
		filteredResults = removeUnwantedTasks(taskList, filteredResults);

		if(isChainCommand){
			searchLog.addLog(Logging.LoggingLevel.INFO, "Search.execute(): chain command detected. (word = "
				    + word + ")");
			
			filteredResults = chainCommandExecution(word, words, currentWordIndex, taskList, filteredResults);
		}
		return filteredResults;
	}

	public void maintainWholeTaskList(List < AbstractTask > TaskList){
		if(wholeTaskList.size() == 0){
			wholeTaskList = TaskList;
		}
	}

	public boolean chainCommand(String word){
		if(word.equalsIgnoreCase(OR_SEARCH) || word.equalsIgnoreCase(AND_SEARCH) || word.equalsIgnoreCase(NOT_SEARCH)){
			return true;
		}
		else{
			return false;
		}
	}

	public List < AbstractTask > chainCommandExecution(String chainCommand, String[] searchWords, int currentWordIndex, List < AbstractTask > TaskList, List < AbstractTask > Results){
		if(chainCommand.equalsIgnoreCase(OR_SEARCH)){
			searchLine = searchWords[currentWordIndex + 1];
			for(int i = currentWordIndex + 2; i < searchWords.length; i++){
				searchLine = searchLine + " " + searchWords[i];
			}
			Results = execute(TaskList);
		}

		else if(chainCommand.equalsIgnoreCase(AND_SEARCH)){
			searchLine = searchWords[currentWordIndex + 1];
			for(int i = currentWordIndex + 2; i < searchWords.length; i++){
				searchLine = searchLine + " " + searchWords[i];
			}
			Results = andSearch(Results);		
		}

		else if(chainCommand.equalsIgnoreCase(NOT_SEARCH)){
			searchLine = searchWords[currentWordIndex + 1];
			for(int i = currentWordIndex + 2; i < searchWords.length; i++){
				searchLine = searchLine + " " + searchWords[i];
			}
			Results = notSearch(Results);
		}
		return Results;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////// Searching functions
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List < AbstractTask > searchCommandExecution(String searchCommand, String searchWords, List < AbstractTask > tasksForSearch, List < AbstractTask > vectorForTasksInsertion){
		if(searchCommand.equalsIgnoreCase(VENUE_SEARCH)){
			vectorForTasksInsertion= searchVenue(searchWords, tasksForSearch, vectorForTasksInsertion);
		}
		else if(searchCommand.equalsIgnoreCase(START_TIME_SEARCH)){
			vectorForTasksInsertion = searchFromStartTimeToEndTime(searchWords, tasksForSearch, vectorForTasksInsertion);
		}
		else if(searchCommand.equalsIgnoreCase(WITHIN_TIMEFRAME_SEARCH)){
			vectorForTasksInsertion = searchTimeFrame(searchWords,tasksForSearch, vectorForTasksInsertion);
		}
		else if(searchCommand.equalsIgnoreCase(CATEGORY_SEARCH)){
			vectorForTasksInsertion = searchCategory(searchWords,tasksForSearch, vectorForTasksInsertion);
		}
		else if(searchCommand.equalsIgnoreCase(STATUS_SEARCH)){
			vectorForTasksInsertion = searchStatus(searchWords,tasksForSearch, vectorForTasksInsertion);
		}
		else{
			searchWords = searchCommand + " "  + searchWords ;
			vectorForTasksInsertion = searchKeywords(searchWords, tasksForSearch, vectorForTasksInsertion);
		}
		return vectorForTasksInsertion;
	}

	public List < AbstractTask > searchKeywords(String keywords, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		String [] words = keywords.split(" ");
		for(int i = 0; i < tasksForSearch.size() ; i++){
			for(int j = 0; j < words.length; j++){
				if(tasksForSearch.get(i).getDescription().toLowerCase().contains(words[j].toLowerCase())){
					results.add(tasksForSearch.get(i));
				}
			}
		}
		return results;
	}

	public List < AbstractTask > searchTimeFrame(String timeFrame, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		DateFormat dateformat = new SimpleDateFormat("yyyy MM dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(calendar.getTime());

		String[] todayDate = dateformat.format(calendar.getTime()).split(" ");
		int todayYear = Integer.parseInt(todayDate[0]);
		int todayMonth = Integer.parseInt(todayDate[1]);
		int todayDay = Integer.parseInt(todayDate[2]);

		int searchByYear = 0;
		int searchByMonth = 0;
		int searchByDay = 0;

		String[] timeFrameWords = timeFrame.split(" ");
		if(timeFrameWords[1].equalsIgnoreCase(WITHIN_DAYS_SEARCH)){
			int daysIncrement = Integer.parseInt(timeFrameWords[0]);
			calendar.add(Calendar.DATE, daysIncrement);
			String[] searchByDate = dateformat.format(calendar.getTime()).split(" ");
			searchByYear = Integer.parseInt(searchByDate[0]);
			searchByMonth = Integer.parseInt(searchByDate[1]);
			searchByDay = Integer.parseInt(searchByDate[2]);
		}
		else if(timeFrameWords[1].equalsIgnoreCase(WITHIN_MONTHS_SEARCH)){
			int monthIncrement = Integer.parseInt(timeFrameWords[0]);
			calendar.add(Calendar.MONTH, monthIncrement);
			String[] searchByDate = dateformat.format(calendar.getTime()).split(" ");
			searchByYear = Integer.parseInt(searchByDate[0]);
			searchByMonth = Integer.parseInt(searchByDate[1]);
			searchByDay = Integer.parseInt(searchByDate[2]);
		}

		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getType().toString().equalsIgnoreCase("timed") || tasksForSearch.get(i).getType().toString().equalsIgnoreCase("deadline")){
				if(tasksForSearch.get(i).getType().toString().equalsIgnoreCase("timed")){
					TimedTask task = (TimedTask) tasksForSearch.get(i);
					String[] taskStartDateAndTime = task.getStartDate().split(" ");
					String[] taskStartDate = taskStartDateAndTime[0].split("-");
					int taskStartYear = Integer.parseInt(taskStartDate[0]);
					int taskStartMonth = Integer.parseInt(taskStartDate[1]);
					int taskStartDay = Integer.parseInt(taskStartDate[2]);

					if(dateComparator(taskStartYear, taskStartMonth, taskStartDay, searchByYear, searchByMonth, searchByDay)){
						if(dateComparator(todayYear, todayMonth, todayDay ,taskStartYear, taskStartMonth, taskStartDay)){
							results.add(tasksForSearch.get(i));
						}
					}
				}

				else if(tasksForSearch.get(i).getType().toString().equalsIgnoreCase("deadline")){
					DeadlineTask task = (DeadlineTask) tasksForSearch.get(i);
					String[] taskEndDateAndTime = task.getEndDate().split(" ");
					String[] taskEndDate = taskEndDateAndTime[0].split("-");
					int taskEndYear = Integer.parseInt(taskEndDate[0]);
					int taskEndMonth = Integer.parseInt(taskEndDate[1]);
					int taskEndDay = Integer.parseInt(taskEndDate[2]);

					if(dateComparator(taskEndYear, taskEndMonth, taskEndDay, searchByYear, searchByMonth, searchByDay)){
						if(dateComparator(todayYear, todayMonth, todayDay ,taskEndYear, taskEndMonth, taskEndDay)){
							results.add(tasksForSearch.get(i));
						}
					}
				}
			}
		}
		return results;
	}


	public List < AbstractTask > searchFromStartTimeToEndTime(String time, List < AbstractTask > tasksForSearch, List < AbstractTask > results) throws IllegalArgumentException{
		String [] datesAndTimes = time.split(" ");
		if(datesAndTimes.length < 2){
			searchLog.addLog(Logging.LoggingLevel.WARNING, "Search.searchFromStartTimeToEndTime(): Searching within a timeframe failed. Invalid time frame (time = "
				    + time + ")");
			
			throw new IllegalArgumentException("Date parameters cannot be empty!");
		}
		int endYear = -1;
		int endMonth = -1;
		int endDay = -1;
		int endHour = 0;
		int endMinute = 0;
		int startYear = -1;
		int startMonth = -1;
		int startDay = -1;
		int startHour = 0;
		int startMinute = 0;
		
		if(datesAndTimes[0].contains("-")){
			String[] startDate = datesAndTimes[0].split("-");
			startYear = Integer.parseInt(startDate[0]);
			startMonth = Integer.parseInt(startDate[1]);
			startDay = Integer.parseInt(startDate[2]);
		}

		if(datesAndTimes[1].contains(":")){
			String[] startTime = datesAndTimes[1].split(":");
			startHour = Integer.parseInt(startTime[0]);
			startMinute = Integer.parseInt(startTime[1]);
		}
		else{
			startHour = 00;
			startMinute = 00;
		}
		// System.out.println(startYear + "," + startMonth + "," + startDay); debug
		if(datesAndTimes[1].contains("-")){
			String[] endDate = datesAndTimes[1].split("-");
			endYear = Integer.parseInt(endDate[0]);
			endMonth = Integer.parseInt(endDate[1]);
			endDay = Integer.parseInt(endDate[2]);
		}
		if(datesAndTimes.length >= 3 && datesAndTimes[2].contains("-")){
			String[] endDate = datesAndTimes[2].split("-");
			endYear = Integer.parseInt(endDate[0]);
			endMonth = Integer.parseInt(endDate[1]);
			endDay = Integer.parseInt(endDate[2]);
		}
		if(datesAndTimes.length >= 3 && datesAndTimes[2].contains(":")){
			String[] endTime = datesAndTimes[2].split(":");
			endHour = Integer.parseInt(endTime[0]);
			endMinute = Integer.parseInt(endTime[1]);
		}
		else if(datesAndTimes.length >= 4 && datesAndTimes[3].contains(":")){
			String[] endTime = datesAndTimes[3].split(":");
			endHour = Integer.parseInt(endTime[0]);
			endMinute = Integer.parseInt(endTime[1]);
		}

		else{
			endHour = 23;
			endMinute = 59;
		}
		// System.out.println(endYear + "," + endMonth + "," + endDay); debug
		
		if(!dateChecker(startYear,startMonth,startDay,startHour,startMinute) || !dateChecker(endYear,endMonth,endDay,endHour,endMinute)){
			searchLog.addLog(Logging.LoggingLevel.WARNING, "Search.searchFromStartTimeToEndTime(): Searching within a timeframe failed. Invalid time frame (time = "
				    + time + ")");
			
			throw new IllegalArgumentException("Invalid date format!");
		}

		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getType().toString().equalsIgnoreCase("timed") || tasksForSearch.get(i).getType().toString().equalsIgnoreCase("deadline")){
				if(tasksForSearch.get(i).getType().toString().equalsIgnoreCase("timed")){
					TimedTask task = (TimedTask) tasksForSearch.get(i);
					String[] taskStartDateAndTime = task.getStartDate().split(" ");
					String[] taskStartDate = taskStartDateAndTime[0].split("-");
					int taskStartYear = Integer.parseInt(taskStartDate[0]);
					int taskStartMonth = Integer.parseInt(taskStartDate[1]);
					int taskStartDay = Integer.parseInt(taskStartDate[2]);
					String[] taskStartTime = taskStartDateAndTime[1].split(":");
					int taskStartHour = Integer.parseInt(taskStartTime[0]);
					int taskStartMinute = Integer.parseInt(taskStartTime[1]);
					
					String[] taskEndDateAndTime = task.getEndDate().split(" ");
					String[] taskEndDate = taskEndDateAndTime[0].split("-");
					int taskEndYear = Integer.parseInt(taskEndDate[0]);
					int taskEndMonth = Integer.parseInt(taskEndDate[1]);
					int taskEndDay = Integer.parseInt(taskEndDate[2]);
					String[] taskEndTime = taskEndDateAndTime[1].split(":");
					int taskEndHour = Integer.parseInt(taskEndTime[0]);
					int taskEndMinute = Integer.parseInt(taskEndTime[1]);

					//System.out.println(taskStartYear + "," + taskStartMonth + "," + taskStartDay); debug

					if(dateComparator(startYear, startMonth, startDay, taskStartYear, taskStartMonth, taskStartDay)){
						if(sameDayComparator(startYear, startMonth, startDay, taskStartYear, taskStartMonth, taskStartDay)){
							if(timeComparator(startHour, startMinute, taskStartHour, taskStartMinute)){
								if(dateComparator(taskStartYear, taskStartMonth, taskStartDay,endYear, endMonth, endDay)){
									if(sameDayComparator(taskStartYear, taskStartMonth, taskStartDay,endYear, endMonth, endDay)){
										if(timeComparator( taskStartHour, taskStartMinute,endHour, endMinute )){
											results.add(tasksForSearch.get(i));
										}
									}
									else{
										results.add(tasksForSearch.get(i));
									}
								}
							}
						}
						else{
							if(dateComparator(taskStartYear, taskStartMonth, taskStartDay,endYear, endMonth, endDay)){
								if(sameDayComparator(taskStartYear, taskStartMonth, taskStartDay,endYear, endMonth, endDay)){
									if(timeComparator( taskStartHour, taskStartMinute,endHour, endMinute )){
										results.add(tasksForSearch.get(i));
									}
								}
								else{
									results.add(tasksForSearch.get(i));
								}
							}
						}
					}
					
					if(dateComparator(taskEndYear, taskEndMonth, taskEndDay, endYear, endMonth, endDay)){
						if(sameDayComparator(taskEndYear, taskEndMonth, taskEndDay, endYear, endMonth, endDay)){
							if(timeComparator(taskEndHour, taskEndMinute, endHour, endMinute)){
								if(dateComparator(startYear, startMonth, startDay, taskEndYear, taskEndMonth, taskEndDay)){	
									if(sameDayComparator(startYear, startMonth, startDay, taskEndYear,taskEndMonth, taskEndDay)){
										if(timeComparator(startHour, startMinute, taskEndHour, taskEndMinute)){
											results.add(tasksForSearch.get(i));
										}
									}
									else{
										results.add(tasksForSearch.get(i));
									}
								}
							}
						}
						else {
							if(dateComparator(startYear, startMonth, startDay, taskEndYear,taskEndMonth, taskEndDay)){	
								if(sameDayComparator(startYear, startMonth, startDay, taskEndYear,taskEndMonth, taskEndDay)){
									if(timeComparator(startHour, startMinute, taskEndHour, taskEndMinute)){
										results.add(tasksForSearch.get(i));
									}
								}
								else{
									results.add(tasksForSearch.get(i));
								}
							}
						}
					}
				}

				else if(tasksForSearch.get(i).getType().toString().equalsIgnoreCase("deadline")){
					DeadlineTask task = (DeadlineTask) tasksForSearch.get(i);
					String[] taskEndDateAndTime = task.getEndDate().split(" ");
					String[] taskEndDate = taskEndDateAndTime[0].split("-");
					int taskEndYear = Integer.parseInt(taskEndDate[0]);
					int taskEndMonth = Integer.parseInt(taskEndDate[1]);
					int taskEndDay = Integer.parseInt(taskEndDate[2]);
					String[] taskEndTime = taskEndDateAndTime[1].split(":");
					int taskEndHour = Integer.parseInt(taskEndTime[0]);
					int taskEndMinute = Integer.parseInt(taskEndTime[1]);


					if(dateComparator(taskEndYear, taskEndMonth, taskEndDay, endYear, endMonth, endDay)){
						if(sameDayComparator(taskEndYear, taskEndMonth, taskEndDay, endYear, endMonth, endDay)){
							if(timeComparator(taskEndHour, taskEndMinute, endHour, endMinute)){
								if(dateComparator(startYear, startMonth, startDay, taskEndYear, taskEndMonth, taskEndDay)){	
									if(sameDayComparator(startYear, startMonth, startDay, taskEndYear,taskEndMonth, taskEndDay)){
										if(timeComparator(startHour, startMinute, taskEndHour, taskEndMinute)){
											results.add(tasksForSearch.get(i));
										}
									}
									else{
										results.add(tasksForSearch.get(i));
									}
								}
							}
						}
						else {
							if(dateComparator(startYear, startMonth, startDay, taskEndYear,taskEndMonth, taskEndDay)){	
								if(sameDayComparator(startYear, startMonth, startDay, taskEndYear,taskEndMonth, taskEndDay)){
									if(timeComparator(startHour, startMinute, taskEndHour, taskEndMinute)){
										results.add(tasksForSearch.get(i));
									}
								}
								else{
									results.add(tasksForSearch.get(i));
								}
							}
						}
					}
				}
			}
		}
		results = removeDuplicateTasks(results);
		return results;

	}

	public List < AbstractTask > searchVenue(String venue, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getVenue().equalsIgnoreCase(venue)){
				results.add(tasksForSearch.get(i));
			}
		}
		return results;
	}

	public List < AbstractTask > searchCategory(String category, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getType().toString().equalsIgnoreCase(category)){
				results.add(tasksForSearch.get(i));
			}
		}
		return results;
	}

	public List < AbstractTask > searchStatus(String status, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		// System.out.println(status); debug
		for(int i = 0; i < tasksForSearch.size() ; i++){
			//System.out.println(tasksForSearch.get(i).getStatus().toString()); debug
			if(tasksForSearch.get(i).getStatus().toString().equalsIgnoreCase(status)){
				// System.out.println("YES"); debug
				results.add(tasksForSearch.get(i));
			}
		}
		return results;
	}



	public List < AbstractTask > removeUnwantedTasks (List < AbstractTask > taskList, List < AbstractTask > unwantedTasks){
		List < AbstractTask > filteredResults = new Vector < AbstractTask >();
		for(int i = 0; i < taskList.size(); i++){
			boolean isWanted = true;
			for(int j = 0; j < unwantedTasks.size(); j++){
				if(taskList.get(i).getDescription().equalsIgnoreCase(unwantedTasks.get(j).getDescription())){
					isWanted = false;
					break;
				}
			}
			if(isWanted){
				filteredResults.add(taskList.get(i));
			}
		}
		return filteredResults;
	}

	public List < AbstractTask > removeDuplicateTasks (List < AbstractTask > taskList){
		List < AbstractTask > filteredResults = new Vector < AbstractTask >();
		for(int i = 0; i < taskList.size(); i++){
			boolean isWanted = true;
			for(int j = i + 1; j < taskList.size(); j++){
				if(taskList.get(i).equals(taskList.get(j))){
					isWanted = false;
					break;
				}
			}
			if(isWanted){
				filteredResults.add(taskList.get(i));
			}
		}
		return filteredResults;
	}
	public boolean sameDayComparator(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay){
		if(startYear == endYear && startMonth == endMonth && startDay == endDay){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean dateComparator(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay){
		boolean isLater = false;
		if(startYear <= endYear){
			if(startYear == endYear){
				if(startMonth <= endMonth){
					if(startMonth == endMonth){
						if(startDay <= endDay){
							isLater = true;
						}
					}
					else{
						isLater = true;
					}
				}
			}
			else{
				isLater = true;
			}
		}
		return isLater;
	}

	public boolean timeComparator(int startHour, int startMinute, int endHour, int endMinute){
		boolean isLater = false;
		if(startHour <= endHour){
			if(startHour == endHour){
				if(startMinute <= endMinute){
					isLater = true;
				}
			}

			else{
				isLater = true;
			}

		}
		return isLater;
	}
	
	public boolean dateChecker(int year, int month, int day, int hour, int minute){
		if(year == -1 || month == -1 || day == -1 || hour > 23 || minute > 59 ){
			return false;
		}
		
		return true;
	}
}
