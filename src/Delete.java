import java.util.List;

public class Delete implements UndoableCommand {
    private List<AbstractTask> deleteSpace;
    int index;

    public Delete(List<AbstractTask> deleteSpace, int index) {
	this.deleteSpace = deleteSpace;
	this.index = index;
    }

    @Override
    public List<AbstractTask> execute() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void undo() {
	// TODO Auto-generated method stub
    }

}
