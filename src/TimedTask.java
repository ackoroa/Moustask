public class TimedTask extends AbstractTask {
    private String startDate, endDate;

    public TimedTask(String description, String startDate, String endDate, int type) {
	super(description, type);
	this.startDate = startDate;
	this.endDate = endDate;
    }

    public TimedTask(String description, String startDate, String endDate, String venue,
	    int type) {
	super(description, venue, type);
	this.startDate = startDate;
	this.endDate = endDate;
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
