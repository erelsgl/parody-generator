package Imitator.common;

/**
 * This class helps to use a clock.
 * 
 * @author Roni Vered
 *
 */
public class Timer {

    private long _lastLogTime;

    private long _startTime;

    public void startClock() {
	_startTime = System.currentTimeMillis();
	_lastLogTime = _startTime;
    }

    public void finish() {
	long endTime = System.currentTimeMillis();

	int minutesFromBegin = (int) (endTime - _startTime) / (1000 * 60);
	int secondsFromBegin = (int) ((endTime - _startTime) / 1000) % 60;

	System.out.println("Total runtime: " + minutesFromBegin + ":" + secondsFromBegin + " minutes");
    }

    public boolean hasEnoughTimePassed() {
	long currentTime = System.currentTimeMillis();

	if (currentTime - _lastLogTime > 30000) {
	    _lastLogTime = currentTime;
	    return true;
	}

	return false;

    }

    public String getFormattedTimeFromBegin() {
	long currentTime = System.currentTimeMillis();

	int hoursFromBegin = (int) (currentTime - _startTime) / (1000 * 60 * 60);
	int minutesFromBegin = (int) ((currentTime - _startTime) / (1000 * 60)) % 60;
	int secondsFromBegin = (int) ((currentTime - _startTime) / 1000) % 60;

	String minutesString = minutesFromBegin < 10 ? "0" + String.valueOf(minutesFromBegin) : String.valueOf(minutesFromBegin);
	String secondsString = secondsFromBegin < 10 ? "0" + String.valueOf(secondsFromBegin) : String.valueOf(secondsFromBegin);
	
	StringBuffer sb = new StringBuffer();
	sb.append(String.valueOf(hoursFromBegin)).append(":").append(minutesString).append(":").append(secondsString);
	
	return sb.toString();
    }
}
