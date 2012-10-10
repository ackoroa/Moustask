import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

public class Storage {

	private List<AbstractTask> loadTaskList=new Vector<AbstractTask>();

	public Storage() {
	}

	public List<AbstractTask> loadTaskList() throws IOException {
		return readMoustaskFile();	
	}

	public List<AbstractTask> readMoustaskFile() throws IOException{
		try{
			File moustaskFile=new File("moustask.txt");
			Scanner readMoustaskFile=new Scanner(moustaskFile);
			// if moustask.txt has data, import into taskList
			// else return empty taskList
			if (readMoustaskFile.hasNextLine()){
				String line=readMoustaskFile.nextLine();			
				StringTokenizer taskToken=new StringTokenizer(line,"|");
				int numberOfTaskToken=taskToken.countTokens();
				String taskCategory=taskToken.nextToken().trim();
				String taskDescription=taskToken.nextToken().trim();

				if(taskCategory.equalsIgnoreCase("Floating")){
					FloatingTask floatingTask=new FloatingTask(taskDescription);
					if (numberOfTaskToken==3){
						String taskStatus=taskToken.nextToken().trim();
						if (taskStatus.equalsIgnoreCase("Done")){
							floatingTask.setStatus(AbstractTask.DONE);
						}
						else if (taskStatus.equalsIgnoreCase("Impossible")){
							floatingTask.setStatus(AbstractTask.IMPOSSIBLE);
						}
					}
					else{
						String taskVenue=taskToken.nextToken().trim();
						String taskStatus=taskToken.nextToken().trim();
						floatingTask.setVenue(taskVenue);
						if (taskStatus.equalsIgnoreCase("Done")){
							floatingTask.setStatus(AbstractTask.DONE);
						}
						else if (taskStatus.equalsIgnoreCase("Impossible")){
							floatingTask.setStatus(AbstractTask.IMPOSSIBLE);
						}
					}
					this.loadTaskList.add(floatingTask);
				}

				else if (taskCategory.equalsIgnoreCase("Deadline")){	
					String taskEndDate=taskToken.nextToken().trim();
					DeadlineTask deadlineTask=new DeadlineTask(taskDescription,taskEndDate);
					if (numberOfTaskToken==4){
						String taskStatus=taskToken.nextToken().trim();
						if (taskStatus.equalsIgnoreCase("Done")){
							deadlineTask.setStatus(AbstractTask.DONE);
						}
						else if (taskStatus.equalsIgnoreCase("Impossible")){
							deadlineTask.setStatus(AbstractTask.IMPOSSIBLE);
						}
					}
					else{
						String taskVenue=taskToken.nextToken().trim();
						String taskStatus=taskToken.nextToken().trim();
						deadlineTask.setEndDate(taskEndDate);
						deadlineTask.setVenue(taskVenue);
						if (taskStatus.equalsIgnoreCase("Done")){
							deadlineTask.setStatus(AbstractTask.DONE);
						}
						else if (taskStatus.equalsIgnoreCase("Impossible")){
							deadlineTask.setStatus(AbstractTask.IMPOSSIBLE);
						}
					}
					this.loadTaskList.add(deadlineTask);
				}

				else{
					String taskStartDate=taskToken.nextToken().trim();
					String taskEndDate=taskToken.nextToken().trim();
					TimedTask timedTask=new TimedTask(taskDescription,taskStartDate,taskEndDate);
					if (numberOfTaskToken==5){
						String taskStatus=taskToken.nextToken().trim();
						if (taskStatus.equalsIgnoreCase("Done")){
							timedTask.setStatus(AbstractTask.DONE);
						}
						else if (taskStatus.equalsIgnoreCase("Impossible")){
							timedTask.setStatus(AbstractTask.IMPOSSIBLE);
						}
					}
					else{
						String taskVenue=taskToken.nextToken().trim();
						String taskStatus=taskToken.nextToken().trim();
						timedTask.setVenue(taskVenue);
						if (taskStatus.equalsIgnoreCase("Done")){
							timedTask.setStatus(AbstractTask.DONE);
						}
						else if (taskStatus.equalsIgnoreCase("Impossible")){
							timedTask.setStatus(AbstractTask.IMPOSSIBLE);
						}
					}
					this.loadTaskList.add(timedTask);
				}
			}
			return this.loadTaskList;
		}
		catch (Exception moustaskFileNotFound){
			Writer createNewMoustaskFile= new BufferedWriter(new FileWriter("moustask.txt"));
		}
		return this.loadTaskList;
	}

	public void writeTaskList(List<AbstractTask> taskList) throws IOException{
		Writer createNewMoustaskFile= new BufferedWriter(new FileWriter("moustask.txt",false));
		
			
				/*stringReadFromData.add(commandDescription);
				writerProgram = new BufferedWriter(new FileWriter(userFile));
				for (int i = 0; i < stringReadFromData.size(); i++) {
					writerProgram.write(stringReadFromData.get(i) + "\r\n");*/	
		}
	


}
