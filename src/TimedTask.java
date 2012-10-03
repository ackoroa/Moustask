public class TimedTask implements Task {
    private String description;
    private String venue;
    private int type;
    private String startDate, endDate;

    public TimedTask(String desc, String startDate, String endDate, int t) {
	description = desc;
	type = t;
	venue = "";
	this.startDate = startDate;
	this.endDate = endDate;
    }

    public TimedTask(String desc, String startDate, String endDate, String v,
	    int t) {
	description = desc;
	type = t;
	venue = v;
	this.startDate = startDate;
	this.endDate = endDate;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String desc) {
	description = desc;
    }

    public String getVenue() {
	return venue;
    }

    public void setVenue(String v) {
	venue = v;
    }

    public int getType() {
	return type;
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
