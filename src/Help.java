//@author A0087171A
public class Help {

    public Help() {
    }

    public void execute() {
	System.out.println("Welcome to MousTask Help!");
	System.out.println("To add a task, type '.add', followed by the description of the task.");
	System.out.println("To add details for the task, type '.venue' to enter a venue name after the command.");
	System.out.println();
	System.out.println("To add a timed task, after the details have been typed, type '.from' and enter a start date in the format YYYY-MM-DD");
	System.out.println("followed by a start time in the format HOURS:MINUTES. Type '.to' followed by the end date and time in the same format.");
	System.out.println("Do note that the adding of start and end time is optional.");
	System.out.println("E.g: .add must finish by online assignment .venue home .from 2012-12-20 .to 2012-12-31 24:00");
	System.out.println();
	System.out.println("To add a deadline task, after the details have been typed, type '.by' followed by the end date and time in the same format as timed task");
	System.out.println("You can also specify the day of the week as the date. Do note the end time is optional.");
	System.out.println("E.g: .add Project Due Date .by 2012-12-20 12:30");
	System.out.println("E.g .add project .by tuesday");
	System.out.println();
	System.out.println("To add a floating task, after the details have been typed, simply enter the command after the details have been typed.");
	System.out.println("E.g: .add read book");
	System.out.println();
	System.out.println("To display all the tasks stored in the list, type '.display' and enter the command");
	System.out.println("E.g: .display");
	System.out.println();
	System.out.println("To search for tasks using keywords, type '.search' followed by the keywords. Any tasks with any keywords in their description");
	System.out.println("will be shown.");
	System.out.println("E.g: .search project meeting");
	System.out.println();
	System.out.println("To search for tasks within a timeframe, type '.search' followed by '.from' followed by the start date and time and the end date and time");
	System.out.println("in the same format as adding a timed task. All tasks happening within that timeframe will be shown.");
	System.out.println("Do note that the start and end time is optional.");
	System.out.println("E.g: .search .from 2012-07-13 08:00 .to 2012-07-20");
	System.out.println();
	System.out.println("To search for tasks by a certain day or month from the present date, type '.search' followed by '.by' followed by the number of days or months.");
	System.out.println("Type '.days' after the number if you wish to search by days or '.months' if you wish to search by months. All results within that time will be shown.");
	System.out.println("You can also search by the day of the week as well.");
	System.out.println("E.g. .search .by monday");
	System.out.println("E.g: .search .by 7 .days");
	System.out.println();
	System.out.println("To search for tasks by venue, type '.search' followed by '.venue' followed by the venue name. The tasks with the exact venue will be shown.");
	System.out.println("E.g: .search .venue orchard road");
	System.out.println();
	System.out.println("To search for tasks by category, type '.search' followed by '.category' followed by the category 'timed', 'deadline' or 'floating'.");
	System.out.println("All tasks with that category will be shown.");
	System.out.println("E.g: .search .category timed");
	System.out.println();
	System.out.println("To search for tasks by status, type '.search' followed by '.status' followed by the status 'undone', 'done' or 'impossible'.");
	System.out.println("All tasks with that status will be shown.");
	System.out.println("E.g: .search .status undone");
	System.out.println();
	System.out.println("To chain search commands, type '.and' if you wish to search for tasks that fulfills both searches typed before and after the 'and'.");
	System.out.println("Type '.or' if you wish to search for tasks that fulfills either searches typed before and after the '.or'");
	System.out.println("Type '.not' if you wish to search for tasks that fulfills the search typed before but not the search typed after the '.not'.");
	System.out.println("You may use as many chain commands in a single search line, but the search will proceed from left to right.");
	System.out.println("E.g: .search .venue orchard road .or .by 5 .days .not .status done");
	System.out.println("The example above is to search for tasks that happens in orchard road or within 5 days but not tasks that are already done.");
	System.out.println();
	System.out.println("To edit a task, first search for tasks. Next, type '.edit' followed by the task number of the task as shown in the search results. Next, type the new");
	System.out.println("description and details. You may also update the status of the task. Note that you cannot change the type of task to a different");
	System.out.println("category, E.g: changing a timed task to a floating task.");
	System.out.println("E.g: .search .venue orchard road");
	System.out.println("E.g: .edit 2 project meeting .at raffles place .status. done");
	System.out.println();
	System.out.println("To delete a task, first search for tasks. Mext, type '.delete' followed by the task number of the task as shown in the search results.");
	System.out.println("E.g: .Search .status impossible");
	System.out.println("E.g: .delete 1");
	System.out.println();
	System.out.println("To clear all the tasks stored in the list, type '.clear'.");
	System.out.println("E.g: .clear");
	System.out.println();
	System.out.println("To undo a undoable command, i.e clear, edit, add and delete, type '.undo'.");
	System.out.println("E.g: .undo");
    }

}
