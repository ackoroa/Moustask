public class DeadlineTask implements Task {
    private String description;
    private String venue;
    private int type;
    private String endDate;

    public DeadlineTask(String desc, String endDate, int t) {
	description = desc;
	type = t;
	venue = "";
	this.endDate = endDate;
    }

    public DeadlineTask(String desc, String endDate, String v, int t) {
	description = desc;
	type = t;
	venue = v;
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

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String ed) {
	endDate = ed;
    }

}
