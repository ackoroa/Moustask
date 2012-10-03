import java.util.List;

public interface Command {
    public List<AbstractTask> execute();
}
