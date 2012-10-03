public abstract class AbstractTask {
    public final static int TIMED = 0;
    public final static int FLOAT = 1;
    public final static int DEADLINE = 2;

    private String description;
    private String venue;
    private int type;
    
    public AbstractTask(String desc, int t) {
	description = desc;
	type = t;
	venue = "";
    }

    public AbstractTask(String desc, String v, int t) {
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
