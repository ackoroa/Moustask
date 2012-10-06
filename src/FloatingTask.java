public class FloatingTask extends AbstractTask {

    public FloatingTask(String description) {
	super(description, AbstractTask.FLOAT);
    }

    public FloatingTask(String description, String venue) {
	super(description, venue, AbstractTask.FLOAT);
    }

}
