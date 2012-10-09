public class TimedTask extends AbstractTask {
    private String startDate, endDate;

    public TimedTask(String description, String startDate, String endDate) {
	super(description, AbstractTask.TIMED);
	this.startDate = startDate;
	this.endDate = endDate;
    }

    public TimedTask(String description, String startDate, String endDate,
	    String venue) {
	super(description, venue, AbstractTask.TIMED);
	this.startDate = startDate;
	this.endDate = endDate;
    }

    public String toString() {
	if (this.getVenue().equals(""))
	    return this.getType() + ": " + this.getDescription() + " from "
		    + startDate + " to " + endDate;
	else
	    return this.getType() + ": " + this.getDescription() + " at "
		    + this.getVenue() + " from " + startDate + " to " + endDate;
    }

    public String getStartDate() {
	return startDate;
    }

    public void getStartDate(String sd) {
	startDate = sd;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String ed) {
	endDate = ed;
    }

}
