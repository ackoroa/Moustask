public class DeadlineTask extends AbstractTask {
    private String endDate;

    public DeadlineTask(String description, String endDate) {
	super(description, AbstractTask.Type.DEADLINE);

	this.endDate = endDate;
    }
    
    public DeadlineTask(String description, String endDate, String venue) {
	super(description, venue, AbstractTask.Type.DEADLINE);

	this.endDate = endDate;
    }

    public String toString() {
	if (this.getVenue().equals(""))
	    return this.getType() + ": " + this.getDescription() + " by "
		    + endDate;
	else
	    return this.getType() + ": " + this.getDescription() + " at "
		    + this.getVenue() + " by " + endDate;
    }

    public boolean equals(Object o) {
	if (!o.getClass().equals(this.getClass()))
	    return false;

	DeadlineTask otherTask = (DeadlineTask) o;

	return (super.equals(o) && (otherTask.endDate.equals(this.endDate)));
    }

    public int hashCode() {
	int hashCode = super.hashCode() + endDate.hashCode();

	return hashCode;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String ed) {
	endDate = ed;
    }
}
