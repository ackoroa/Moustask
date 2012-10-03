public class DeadlineTask extends AbstractTask {
    private String endDate;

    public DeadlineTask(String description, String endDate, int type) {
	super(description, type);
	this.endDate = endDate;
    }

    public DeadlineTask(String description, String endDate, String venue,
	    int type) {
	super(description, venue, type);
	this.endDate = endDate;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String ed) {
	endDate = ed;
    }

}
