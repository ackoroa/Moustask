public class FloatingTask extends AbstractTask {

    public FloatingTask(String description) {
	super(description, AbstractTask.Type.FLOAT);
    }

    public FloatingTask(String description, String venue) {
	super(description, venue, AbstractTask.Type.FLOAT);
    }

    public String toString() {
	if (this.getVenue().equals(""))
	    return this.getType() + ": " + this.getDescription();
	else
	    return this.getType() + ": " + this.getDescription() + " at "
		    + this.getVenue();
    }

    public boolean equals(Object o) {
	return super.equals(o);
    }

    public int hashCode() {
	int hashCode = super.hashCode();

	return hashCode;
    }
}
