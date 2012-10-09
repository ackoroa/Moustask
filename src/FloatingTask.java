public class FloatingTask extends AbstractTask {

    public FloatingTask(String description) {
	super(description, AbstractTask.FLOAT);
    }

    public FloatingTask(String description, String venue) {
	super(description, venue, AbstractTask.FLOAT);
    }

    public String toString() {
	if (this.getVenue().equals(""))
	    return this.getType() + ": " + this.getDescription();
	else
	    return this.getType() + ": " + this.getDescription() + " at "
		    + this.getVenue();
    }

}
