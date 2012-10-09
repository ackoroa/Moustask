public abstract class AbstractTask {
    public final static String TIMED = "TIMED";
    public final static String FLOAT = "FLOAT";
    public final static String DEADLINE = "DEADLINE";

    public final static int UNDONE = 0;
    public final static int DONE = 1;
    public final static int IMPOSSIBLE = 2;

    private String description;
    private String venue;
    private String type;
    private int status;

    public AbstractTask(String desc, String t) {
	description = desc;
	type = t;
	venue = "";
	status = UNDONE;
    }

    public AbstractTask(String desc, String v, String t) {
	description = desc;
	type = t;
	venue = v;
	status = UNDONE;
    }

    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException e) {
	    System.out.println("Cloning Task Failed");
	    assert false;
	}
	return null;
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

    public String getType() {
	return type;
    }

    public void setStatus(int s) {
	status = s;
    }

    public int getStatus() {
	return status;
    }
}
