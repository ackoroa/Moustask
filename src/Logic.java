import java.util.List;

public class Logic {
    List<AbstractTask> taskList; // the virtual list of tasks

    public Logic() {

    }

    class Delete implements UndoableCommand {
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

    class Edit implements UndoableCommand {
	private List<AbstractTask> editSpace;
	int index;

	public Edit(List<AbstractTask> editSpace, int index) {
	    this.editSpace = editSpace;
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

}
