//@author A0092101Y
import java.util.List;

public interface UndoableCommand extends Command {
    public List<AbstractTask> undo();
}
