import java.util.List;

public class Edit implements UndoableCommand {
    private List<AbstractTask> editSpace, wholeTaskList;
    int index;

    public Edit(List<AbstractTask> editSpace, int index, String editParameter) {
	this.editSpace = editSpace;
	this.index = index;
    }

    @Override
    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
	this.wholeTaskList = wholeTaskList;
	
	return null;
    }

    @Override
    public List<AbstractTask> undo() {
	// TODO Auto-generated method stub
	return null;
    }

}
