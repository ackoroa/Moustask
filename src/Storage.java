//@author A0087723X
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

public class Storage {

	private List<AbstractTask> loadTaskList = new Vector<AbstractTask>();

	public Storage() {
	}

	// loads the tasks from text file into whole task list
	public List<AbstractTask> loadTaskList() throws IOException {
		try {
			File moustaskFile = new File("moustask.txt");
			Scanner readMoustaskFile = new Scanner(moustaskFile);
			while (readMoustaskFile.hasNextLine()) {
				// read line by line inside the text file
				String line = readMoustaskFile.nextLine();
				StringTokenizer taskToken = new StringTokenizer(line, "|");
				int numberOfTaskToken = taskToken.countTokens();
				String taskCategory = taskToken.nextToken().trim();
				String taskDescription = taskToken.nextToken().trim();
				if (taskCategory.equalsIgnoreCase("Floating")) {
					loadsFloatingTask(taskToken, numberOfTaskToken,
							taskDescription);
				} else if (taskCategory.equalsIgnoreCase("Deadline")) {
					loadsDeadlineTask(taskToken, numberOfTaskToken,
							taskDescription);
				} else {
					loadsTimedTask(taskToken, numberOfTaskToken,
							taskDescription);
				}
			}
			readMoustaskFile.close();
			return loadTaskList;
		} catch (Exception moustaskFileNotFound) {
			Writer createNewMoustaskFile = new BufferedWriter(new FileWriter(
					"moustask.txt"));
			createNewMoustaskFile.close();
		}
		return loadTaskList;
	}

	// adds timed task from text file into whole task list
	private void loadsTimedTask(StringTokenizer taskToken,
			int numberOfTaskToken, String taskDescription) {
		String taskStartDate = taskToken.nextToken().trim();
		String taskEndDate = taskToken.nextToken().trim();
		TimedTask timedTask = new TimedTask(taskDescription, taskStartDate,
				taskEndDate);
		if (numberOfTaskToken == 5) {
			String taskStatus = taskToken.nextToken().trim();
			if (taskStatus.equalsIgnoreCase("Done")) {
				timedTask.setStatus(AbstractTask.Status.DONE);
			} else if (taskStatus.equalsIgnoreCase("Impossible")) {
				timedTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
			}
		} else {
			String taskVenue = taskToken.nextToken().trim();
			String taskStatus = taskToken.nextToken().trim();
			timedTask.setVenue(taskVenue);
			if (taskStatus.equalsIgnoreCase("Done")) {
				timedTask.setStatus(AbstractTask.Status.DONE);
			} else if (taskStatus.equalsIgnoreCase("Impossible")) {
				timedTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
			}
		}
		loadTaskList.add(timedTask);
	}

	// adds deadline task from text file into whole task list
	private void loadsDeadlineTask(StringTokenizer taskToken,
			int numberOfTaskToken, String taskDescription) {
		String taskEndDate = taskToken.nextToken().trim();
		DeadlineTask deadlineTask = new DeadlineTask(taskDescription,
				taskEndDate);
		if (numberOfTaskToken == 4) {
			String taskStatus = taskToken.nextToken().trim();
			if (taskStatus.equalsIgnoreCase("Done")) {
				deadlineTask.setStatus(AbstractTask.Status.DONE);
			} else if (taskStatus.equalsIgnoreCase("Impossible")) {
				deadlineTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
			}
		} else {
			String taskVenue = taskToken.nextToken().trim();
			String taskStatus = taskToken.nextToken().trim();
			deadlineTask.setEndDate(taskEndDate);
			deadlineTask.setVenue(taskVenue);
			if (taskStatus.equalsIgnoreCase("Done")) {
				deadlineTask.setStatus(AbstractTask.Status.DONE);
			} else if (taskStatus.equalsIgnoreCase("Impossible")) {
				deadlineTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
			}
		}
		loadTaskList.add(deadlineTask);
	}

	// adds floating task from text file into whole task list
	private void loadsFloatingTask(StringTokenizer taskToken,
			int numberOfTaskToken, String taskDescription) {
		FloatingTask floatingTask = new FloatingTask(taskDescription);
		if (numberOfTaskToken == 3) {
			String taskStatus = taskToken.nextToken().trim();
			if (taskStatus.equalsIgnoreCase("Done")) {
				floatingTask.setStatus(AbstractTask.Status.DONE);
			} else if (taskStatus.equalsIgnoreCase("Impossible")) {
				floatingTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
			}
		} else {
			String taskVenue = taskToken.nextToken().trim();
			String taskStatus = taskToken.nextToken().trim();
			floatingTask.setVenue(taskVenue);
			if (taskStatus.equalsIgnoreCase("Done")) {
				floatingTask.setStatus(AbstractTask.Status.DONE);
			} else if (taskStatus.equalsIgnoreCase("Impossible")) {
				floatingTask.setStatus(AbstractTask.Status.IMPOSSIBLE);
			}
		}
		loadTaskList.add(floatingTask);
	}

	// writes the tasks into text file
	public void writeTaskList(List<AbstractTask> taskList) throws IOException {
		BufferedWriter writeMoustaskFile = new BufferedWriter(new FileWriter(
				"moustask.txt", false));
		// loops through the whole task list to write the type of tasks into the
		// text file
		for (int taskNumber = 0; taskNumber < taskList.size(); taskNumber++) {
			AbstractTask.Type taskType = taskList.get(taskNumber).getType();
			if (taskType == AbstractTask.Type.FLOATING) {
				writesFloatingTask(taskList, writeMoustaskFile, taskNumber);
			} else if (taskType == AbstractTask.Type.DEADLINE) {
				writesDeadlineTask(taskList, writeMoustaskFile, taskNumber);
			} else {
				writesTimedTask(taskList, writeMoustaskFile, taskNumber);
			}
		}
		writeMoustaskFile.close();
	}

	// writes timed tasks
	private void writesTimedTask(List<AbstractTask> taskList,
			BufferedWriter writeMoustaskFile, int taskNumber)
			throws IOException {
		if (((TimedTask) taskList.get(taskNumber)).getVenue().equalsIgnoreCase(
				"")) {
			writeMoustaskFile.write("Timed | "
					+ ((TimedTask) taskList.get(taskNumber)).getDescription()
					+ " | "
					+ ((TimedTask) taskList.get(taskNumber)).getStartDate()
					+ " | "
					+ ((TimedTask) taskList.get(taskNumber)).getEndDate()
					+ " | "
					+ ((TimedTask) taskList.get(taskNumber)).getStatus()
							.toString());
			writeMoustaskFile.newLine();
		} else {
			writeMoustaskFile.write("Timed | "
					+ ((TimedTask) taskList.get(taskNumber)).getDescription()
					+ " | "
					+ ((TimedTask) taskList.get(taskNumber)).getStartDate()
					+ " | "
					+ ((TimedTask) taskList.get(taskNumber)).getEndDate()
					+ " | "
					+ ((TimedTask) taskList.get(taskNumber)).getVenue()
					+ " | "
					+ ((TimedTask) taskList.get(taskNumber)).getStatus()
							.toString());
			writeMoustaskFile.newLine();
		}
	}

	// writes deadline tasks
	private void writesDeadlineTask(List<AbstractTask> taskList,
			BufferedWriter writeMoustaskFile, int taskNumber)
			throws IOException {
		if (((DeadlineTask) taskList.get(taskNumber)).getVenue()
				.equalsIgnoreCase("")) {
			writeMoustaskFile.write("Deadline | "
					+ ((DeadlineTask) taskList.get(taskNumber))
							.getDescription()
					+ " | "
					+ ((DeadlineTask) taskList.get(taskNumber)).getEndDate()
					+ " | "
					+ ((DeadlineTask) taskList.get(taskNumber)).getStatus()
							.toString());
			writeMoustaskFile.newLine();
		} else {
			writeMoustaskFile.write("Deadline | "
					+ ((DeadlineTask) taskList.get(taskNumber))
							.getDescription()
					+ " | "
					+ ((DeadlineTask) taskList.get(taskNumber)).getEndDate()
					+ " | "
					+ ((DeadlineTask) taskList.get(taskNumber)).getVenue()
					+ " | "
					+ ((DeadlineTask) taskList.get(taskNumber)).getStatus()
							.toString());
			writeMoustaskFile.newLine();
		}
	}

	// writes floating tasks
	private void writesFloatingTask(List<AbstractTask> taskList,
			BufferedWriter writeMoustaskFile, int taskNumber)
			throws IOException {
		if (((FloatingTask) taskList.get(taskNumber)).getVenue()
				.equalsIgnoreCase("")) {
			writeMoustaskFile.write("Floating | "
					+ ((FloatingTask) taskList.get(taskNumber))
							.getDescription()
					+ " | "
					+ ((FloatingTask) taskList.get(taskNumber)).getStatus()
							.toString());
			writeMoustaskFile.newLine();
		} else {
			writeMoustaskFile.write("Floating | "
					+ ((FloatingTask) taskList.get(taskNumber))
							.getDescription() + " | "
					+ ((FloatingTask) taskList.get(taskNumber)).getVenue()
					+ " | " + taskList.get(taskNumber).getStatus().toString());
			writeMoustaskFile.newLine();
		}
	}

}