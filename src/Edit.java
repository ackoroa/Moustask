import java.util.List;

public class Edit implements UndoableCommand {
    private List<AbstractTask> editSpace;
    int index;

    public Edit(List<AbstractTask> editSpace, int index) {
	this.editSpace = editSpace;
	this.index = index;
    }

    @Override
    public List<AbstractTask> execute(List<AbstractTask> wholeTaskList) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<AbstractTask> undo() {
	// TODO Auto-generated method stub
	return null;
    }

}
