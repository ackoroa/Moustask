public interface Task {
    public final static int TIMED = 0;
    public final static int FLOAT = 1;
    public final static int DEADLINE = 2;

    public String getDescription();
    public void setDescription(String description);
    public String getVenue();
    public void setVenue(String venue);
    public int getType();
}
