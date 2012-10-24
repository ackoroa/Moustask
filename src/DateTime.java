import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

public class DateTime {
	private String _dateTime;
	private Date _dateDateTime;

	public DateTime(String dateAndTime) {
		_dateTime = dateAndTime;
	}

	// check for valid date time: return true if valid, return false if invalid
	public boolean validateDateTime() {
		Vector<String> dateTimeToken = new Vector<String>();
		dateTimeToken = splitDateTime(_dateTime);
		boolean isDateTimeTokenSizeOne = dateTimeToken.size() == 1;
		boolean isDateTimeTokenSizeTwo = dateTimeToken.size() == 2;

		try {
			if (isDateTimeTokenSizeOne) {
				SimpleDateFormat dayFormat = new SimpleDateFormat("E");
				dayFormat.setLenient(false);
				_dateDateTime = dayFormat.parse(_dateTime);
				return true;
			} else if (isDateTimeTokenSizeTwo) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				dateFormat.setLenient(false);
				_dateDateTime = dateFormat.parse(_dateTime);
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			return false;
		}
	}

	// convert day of the week to our String date time format, if the input is
	// already in our String date time format, no conversion is done
	public String generateDateTime() {
		Vector<String> dateTimeToken = new Vector<String>();
		dateTimeToken = splitDateTime(_dateTime);
		boolean isDateTimeTokenSizeOne = dateTimeToken.size() == 1;

		if (isDateTimeTokenSizeOne) {
			Calendar calendar = Calendar.getInstance();
			Calendar nowCalendar = Calendar.getInstance();
			nowCalendar.setTime(_dateDateTime);
			while (calendar.get(Calendar.DAY_OF_WEEK) != nowCalendar
					.get(Calendar.DAY_OF_WEEK)) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			_dateTime = new SimpleDateFormat("yyyy-MM-dd 00:00")
					.format(calendar.getTime());
		}
		return _dateTime;
	}

	// Compare the current date object with a new date input
	// Return value is = 0, if both dates are equal
	// Return value is > 0, if argument is smaller than the instance
	// Return value is < 0, if argument is larger than the instance
	public int compareTo(String dateAndTime) {
		return _dateTime.compareTo(dateAndTime);
	}

	public Date getDate() {
		return _dateDateTime;
	}

	public String getDateTime() {
		return _dateTime;
	}

	private static Vector<String> splitDateTime(String dateTime) {
		Vector<String> dateTimeToken = new Vector<String>();
		StringTokenizer st = new StringTokenizer(dateTime, " ");
		while (st.hasMoreTokens()) {
			dateTimeToken.add(st.nextToken());
		}
		return dateTimeToken;
	}
}