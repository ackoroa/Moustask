import java.util.List;
import java.util.Vector;

public class Clear implements UndoableCommand{
	
	private List<AbstractTask> undoList=new Vector<AbstractTask>();
	
	public List<AbstractTask> execute(List<AbstractTask> taskList){
		List<AbstractTask> returnList = new Vector<AbstractTask>();
		return returnList;
	}
	
	public List<AbstractTask> undo(){
		return undoList;
	}
	
}