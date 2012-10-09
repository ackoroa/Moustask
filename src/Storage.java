import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.io.*;

public class Storage {

	public Storage() {

	}

	public List<AbstractTask> loadTaskList() throws IOException {
		return readMoustaskFile();	
	}
	
	//List<AbstractTask> returnList = new Vector<AbstractTask>();

	public List<AbstractTask> readMoustaskFile() throws IOException{
		List<AbstractTask> taskList=new Vector<AbstractTask>();
		try{
			File moustaskFile=new File("moustask.txt");
			Scanner readMoustaskFile=new Scanner(moustaskFile);
			
			
			return taskList;
		}
		catch (Exception moustaskFileNotFound){
			Writer createNewMoustaskFile= new BufferedWriter(new FileWriter("moustask.txt"));
		}
		return taskList;
	}
	// check if moustask.txt is empty
	// then I have to read line until end of file
	// for every line, i check whether it is timed,deadline or floating task
	// then i check their respective variables into different parts
	// then i assign them into as a task objects (create new object)
	// then i add them into taskList

	// remember to hard code file name
}
