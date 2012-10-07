import java.util.List;

public class TypeTaskPair {
	public static enum Type {
		// list of possible operation types
		ADD, DELETE, EDIT, CLEAR, SEARCH, DISPLAY, INVALID, EMPTY, ERROR, UNDOADD, UNDODELETE, UNDOEDIT, UNDOCLEAR, UNDONULL, HELP
	}

	Type type;
	List<AbstractTask> taskList;

	public TypeTaskPair(Type type, List<AbstractTask> taskList) {
		this.type = type;
		this.taskList = taskList;
	}

	public Type getType() {
		return type;
	}

	public List<AbstractTask> getTasks() {
		return taskList;
	}

}