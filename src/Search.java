import java.lang.String;
import java.util.Vector;
import java.util.List;

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
	
	public List < AbstractTask > execute(List < AbstractTask > TaskList){
		maintainWholeTaskList(TaskList);
		
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
				if(word.equals(END_TIME_SEARCH)){
					searchWords = searchWords + " " + words[currentWordIndex + 1] + " " + words[currentWordIndex + 2];
				}
				else{
					searchWords = searchWords + " " + word;
				}
			}
		}
		
		searchCommandExecution(searchCommand, searchWords, TaskList, searchResults);
		removeDuplicateTasks(searchResults);
		
		if(isChainCommand){
			if(word.equals(OR_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				searchResults = execute(TaskList);
			}
			
			else if(word.equals(AND_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				searchResults = andSearch(searchResults);		
			}
			
			else if(word.equals(NOT_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				searchResults = notSearch(searchResults);
			}
		}
		
		return searchResults;
	}
	
	public void maintainWholeTaskList(List < AbstractTask > TaskList){
		if(wholeTaskList.size() == 0){
			wholeTaskList = TaskList;
		}
	}
	
	public boolean chainCommand(String word){
		if(word.equals(OR_SEARCH) || word.equals(AND_SEARCH) || word.equals(NOT_SEARCH)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void searchCommandExecution(String searchCommand, String searchWords, List < AbstractTask > tasksForSearch, List < AbstractTask > vectorForTasksInsertion){
		if(searchCommand.equals(VENUE_SEARCH)){
			searchVenue(searchWords, tasksForSearch, searchResults);
		}
		else if(searchCommand.equals(START_TIME_SEARCH)){
			searchFromStartTimeToEndTime(searchWords, tasksForSearch, searchResults);
		}
		else if(searchCommand.equals(WITHIN_TIMEFRAME_SEARCH)){
			searchTimeFrame(searchWords,tasksForSearch, searchResults);
		}
		else if(searchCommand.equals(CATEGORY_SEARCH)){
			searchCategory(searchWords,tasksForSearch, searchResults);
		}
		else if(searchCommand.equals(STATUS_SEARCH)){
			searchStatus(searchWords,tasksForSearch, searchResults);
		}
	}
	
	public void searchVenue(String venue, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		String[] venueWords = venue.split(" ");
		for(int i = 0; i < tasksForSearch.size() ; i++){
			for(int j = 0; j < venueWords.length; j++){
				if(tasksForSearch.get(i).getVenue().contains(venueWords[j])){
					results.add(tasksForSearch.get(i));
					break;
				}
			}
		}
	}
	
	public void searchCategory(String category, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getType().toLowerCase().equals(category.toLowerCase())){
				results.add(tasksForSearch.get(i));
			}
		}
	}
	
	public void searchStatus(String status, List < AbstractTask > tasksForSearch, List < AbstractTask > results){
		int statusIndex = -1;
		if(status.toLowerCase().equals("undone")){
			statusIndex = 0;
		}
		else if(status.toLowerCase().equals("done")){
			statusIndex = 1;
		}
		else if(status.toLowerCase().equals("impossible")){
			statusIndex = 2;
		}
		
		for(int i = 0; i < tasksForSearch.size() ; i++){
			if(tasksForSearch.get(i).getStatus() == statusIndex){
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
				if(word.equals(END_TIME_SEARCH)){
					searchWords = searchWords + " " + words[currentWordIndex + 1] + " " + words[currentWordIndex + 2];
				}
				else{
					searchWords = searchWords + " " + word;
				}
			}
		}
		
		searchCommandExecution(searchCommand, searchWords, taskList, filteredResults);
		
		if(isChainCommand){
			if(word.equals(OR_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				filteredResults = execute(taskList);
			}
			
			else if(word.equals(AND_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				filteredResults = andSearch(filteredResults);		
			}
			
			else if(word.equals(NOT_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				filteredResults = notSearch(filteredResults);
			}
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
				if(word.equals(END_TIME_SEARCH)){
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
			if(word.equals(OR_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				filteredResults = execute(taskList);
			}
			
			else if(word.equals(AND_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				filteredResults = andSearch(filteredResults);		
			}
			
			else if(word.equals(NOT_SEARCH)){
				searchLine = "";
				for(int i = currentWordIndex + 1; i < words.length; i++){
					searchLine = searchLine + " " + words[i];
				}
				filteredResults = notSearch(filteredResults);
			}
		}
		return filteredResults;
	}
	
	public List < AbstractTask > removeUnwantedTasks (List < AbstractTask > taskList, List < AbstractTask > unwantedTasks){
		List < AbstractTask > filteredResults = new Vector < AbstractTask >();
		for(int i = 0; i < taskList.size(); i++){
			boolean isWanted = true;
			for(int j = 0; j < unwantedTasks.size(); j++){
				if(taskList.get(i).getDescription().equals(unwantedTasks.get(j).getDescription())){
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
				if(taskList.get(i).getDescription().equals(taskList.get(j).getDescription())){
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
