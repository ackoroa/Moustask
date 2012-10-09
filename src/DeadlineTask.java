public class DeadlineTask extends AbstractTask {
    private String endDate;

    public DeadlineTask(String description, String endDate) {
	super(description, AbstractTask.DEADLINE);
	
	this.endDate = endDate;
    }

    public DeadlineTask(String description, String endDate, String venue) {
	super(description, venue, AbstractTask.DEADLINE);
	this.endDate = endDate;
    }
    
    public String toString() {
	if (this.getVenue().equals(""))
	    return this.getType() + ": " + this.getDescription() + " by " + endDate;
	else
	    return this.getType() + ": " + this.getDescription() + " at "
		    + this.getVenue() + " by " + endDate;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String ed) {
	endDate = ed;
    }

}
