import java.lang.String;
import java.lang.Integer;
import java.util.Vector;
import java.util.List;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

	public Search(){
	}

	public Search(String wholeSearchLine){
		searchLine = wholeSearchLine;
	}

	public List < AbstractTask > execute(List < AbstractTask > taskList){
		maintainWholeTaskList(taskList);

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
					searchWords = searchWords + " " + words[currentWordIndex + 1] + " " + words[currentWordIndex + 2];
					currentWordIndex = currentWordIndex + 3;
				}
				else{
					searchWords = searchWords + " " + word;
					currentWordIndex = currentWordIndex + 1;
				}
			}
		}

		searchCommandExecution(searchCommand, searchWords, taskList, searchResults);
		removeDuplicateTasks(searchResults);

		if(isChainCommand){
			searchResults = chainCommandExecution(word, words, currentWordIndex, taskList, searchResults);
		}

		return searchResults;
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

	public List < AbstractTask > chainCommandExecution(String chainCommand, String[] searchWords, int currentWordIndex, List < AbstractTask > TaskList, List < AbstractTask > searchResults){
		if(chainCommand.equalsIgnoreCase(OR_SEARCH)){
			searchLine = "";
			for(int i = currentWordIndex + 1; i < searchWords.length; i++){
				searchLine = searchLine + " " + searchWords[i];
			}
			searchResults = execute(TaskList);
		}

		else if(chainCommand.equalsIgnoreCase(AND_SEARCH)){
			searchLine = "";
			for(int i = currentWordIndex + 1; i < searchWords.length; i++){
				searchLine = searchLine + " " + searchWords[i];
			}
			searchResults = andSearch(searchResults);		
		}

		else if(chainCommand.equalsIgnoreCase(NOT_SEARCH)){
			searchLine = "";
			for(int i = currentWordIndex + 1; i < searchWords.length; i++){
				searchLine = searchLine + " " + searchWords[i];
			}
			searchResults = notSearch(searchResults);
		}
		return searchResults;
	}


	public void searchCommandExecution(String searchCommand, String searchWords, List < AbstractTask > tasksForSearch, List < AbstractTask > vectorForTasksInsertion){
		if(searchCommand.equalsIgnoreCase(VENUE_SEARCH)){
			searchVenue(searchWords, tasksForSearch, searchResults);
		}
		else if(searchCommand.equalsIgnoreCase(START_TIME_SEARCH)){
			searchFromStartTimeToEndTime(searchWords, tasksForSearch, searchResults);
		}
		else if(searchCommand.equalsIgnoreCase(WITHIN_TIMEFRAME_SEARCH)){
			searchTimeFrame(searchWords,tasksForSearch, searchResults);
		}
		else if(searchCommand.equalsIgnoreCase(CATEGORY_SEARCH)){
			searchCategory(searchWords,tasksForSearch, searchResults);
		}
		else if(searchCommand.equalsIgnoreCase(STATUS_SEARCH)){
			searchStatus(searchWords,tasksForSearch, searchResults);
		}
	}

	public void searchTimeFrame(String timeFrame, List < AbstractTask > tasksForSearch, List < AbstractTask > vectorForTasksInsertion){
		DateFormat dateformat = new SimpleDateFormat("yyyy MM dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(calendar.getTime());

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
						vectorForTasksInsertion.add(tasksForSearch.get(i));
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
						vectorForTasksInsertion.add(tasksForSearch.get(i));
					}
				}
			}
		}

	}


	public void searchFromStartTimeToEndTime(String time, List < AbstractTask > tasksForSearch, List < AbstractTask > vectorForTasksInsertion){
		String [] datesAndTimes = time.split(" ");
		String[] startDate = datesAndTimes[0].split("-");
		int startYear = Integer.parseInt(startDate[0]);
		int startMonth = Integer.parseInt(startDate[1]);
		int startDay = Integer.parseInt(startDate[2]);
		String[] startTime = datesAndTimes[1].split(":");
		int startHour = Integer.parseInt(startTime[0]);
		int startMinute = Integer.parseInt(startTime[1]);

		String[] endDate = datesAndTimes[2].split("-");
		int endYear = Integer.parseInt(endDate[0]);
		int endMonth = Integer.parseInt(endDate[1]);
		int endDay = Integer.parseInt(endDate[2]);
		String[] endTime = datesAndTimes[3].split(":");
		int endHour = Integer.parseInt(endTime[0]);
		int endMinute = Integer.parseInt(endTime[1]);

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

					if(dateComparator(startYear, startMonth, startDay, taskStartYear, taskStartMonth, taskStartDay)){
						if(timeComparator(startHour, startMinute, taskStartHour, taskStartMinute)){
							if(dateComparator(taskEndYear, taskEndMonth, taskEndDay, endYear, endMonth, endDay)){
								if(timeComparator(taskEndHour, taskEndMinute, endHour, endMinute)){
									vectorForTasksInsertion.add(tasksForSearch.get(i));
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
						if(timeComparator(taskEndHour, taskEndMinute, endHour, endMinute)){
							vectorForTasksInsertion.add(tasksForSearch.get(i));
						}
					}
				}
			}
		}

	}

	public boolean dateComparator(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay){
		boolean isLater = false;
		if(startYear <= endYear){
			if(startMonth <= endMonth){
				if(startDay <= endDay){
					isLater = true;
				}
			}
		}
		return isLater;
	}

	public boolean timeComparator(int startHour, int startMinute, int endHour, int endMinute){
		boolean isLater = false;
		if(startHour <= endHour){
			if(startMinute <= endMinute){
				isLater = true;
			}
		}
		return isLater;
	}

	public void searchVenue(String venue, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getVenue().contains(venue)){
				results.add(tasksForSearch.get(i));
				break;
			}
		}
	}

	public void searchCategory(String category, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getType().toString().equalsIgnoreCase(category)){
				results.add(tasksForSearch.get(i));
			}
		}
	}

	public void searchStatus(String status, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getStatus().toString().equalsIgnoreCase(status)){
				results.add(tasksForSearch.get(i));
			}
		}
	}

	public List < AbstractTask > andSearch (List < AbstractTask > taskList){
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
					searchWords = searchWords + " " + words[currentWordIndex + 1] + " " + words[currentWordIndex + 2];
				}
				else{
					searchWords = searchWords + " " + word;
				}
			}
		}

		searchCommandExecution(searchCommand, searchWords, taskList, filteredResults);

		if(isChainCommand){
			filteredResults = chainCommandExecution(word, words, currentWordIndex, taskList, searchResults);
		}
		return filteredResults;
	}

	public List < AbstractTask > notSearch (List < AbstractTask > taskList){
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
					searchWords = searchWords + " " + words[currentWordIndex + 1] + " " + words[currentWordIndex + 2];
				}
				else{
					searchWords = searchWords + " " + word;
				}
			}
		}

		searchCommandExecution(searchCommand, searchWords, taskList, filteredResults);
		filteredResults = removeUnwantedTasks(taskList, filteredResults);

		if(isChainCommand){
			filteredResults = chainCommandExecution(word, words, currentWordIndex, taskList, searchResults);
		}
		return filteredResults;
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
}
