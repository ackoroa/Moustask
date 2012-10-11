import java.util.List;
import java.util.Vector;

public class Clear implements UndoableCommand {

    private List<AbstractTask> undoList;

    public clear(){
	
    }
    
    public List<AbstractTask> execute(List<AbstractTask> taskList) {
	undoList = taskList;
	
	List<AbstractTask> returnList = new Vector<AbstractTask>();
	return returnList;
    }

    public List<AbstractTask> undo() {
	return undoList;
    }

}