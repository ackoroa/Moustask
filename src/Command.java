//@author A0092101Y
import java.util.List;

public interface Command {
    public List<AbstractTask> execute(List<AbstractTask> WholeTaskList);
}
