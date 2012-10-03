public class FloatingTask implements Task{
    private String description;
    private String venue;
    private int type;

    public FloatingTask(String desc, int t) {
	description = desc;
	type = t;
	venue = "";
    }

    public FloatingTask(String desc, String v, int t) {
	description = desc;
	type = t;
	venue = v;
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
}
