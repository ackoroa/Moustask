public abstract class AbstractTask {
    public final static int TIMED = 0;
    public final static int FLOAT = 1;
    public final static int DEADLINE = 2;
    
    public final static int UNDONE = 0;
    public final static int DONE = 1;
    public final static int IMPOSSIBLE = 2;

    private String description;
    private String venue;
    private int type;
    private int status;
    
    public AbstractTask(String desc, int t) {
	description = desc;
	type = t;
	venue = "";
	status = UNDONE;
    }

    public AbstractTask(String desc, String v, int t) {
	description = desc;
	type = t;
	venue = v;
	status = UNDONE;
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
    
    public void setStatus(int s){
    	status = s;
    }
    
    public int getStatus(){
    	return status;
    }
}
