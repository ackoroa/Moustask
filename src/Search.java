import java.util.List;

public class Search implements Command {
	private final String OR_SEARCH = "or";
	private final String AND_SEARCH = "and";
	private final String NOT_SEARCH = "not";
	
	private String searchLine;
	private List < AbstractTask > searchResults;
	
	public Search(){
	}
	
	public Search(String wholeSearchLine){
		searchLine = wholeSearchLine;
	}
	
	public List < AbstractTask > execute(List < AbstractTask > wholeTaskList){
		return searchResults;
	}
}
