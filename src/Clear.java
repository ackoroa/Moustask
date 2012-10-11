import java.util.List;
import java.util.Vector;

public class Clear implements UndoableCommand{

	private List<AbstractTask> wholeTaskList;

	public Clear(){
	}

	public List<AbstractTask> execute(List<AbstractTask> wholeTaskList){
		return generateReturnList();
	}


	private List<AbstractTask> generateReturnList() {
		List<AbstractTask> returnList = new Vector<AbstractTask>();
		return returnList;
	}

	public List<AbstractTask> undo(){
		
		//return undoList;
	}

}