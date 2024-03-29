//@author A0092101Y
public class FloatingTask extends AbstractTask {

    public FloatingTask(String description) {
	super(description, AbstractTask.Type.FLOATING);
    }

    public FloatingTask(String description, String venue) {
	super(description, venue, AbstractTask.Type.FLOATING);
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
