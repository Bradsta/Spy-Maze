package com.spymaze.utility;

public class Timer {

	public final long startTime;
	
	private Timer pausedTimer;
	private long totalPausedTime;
	
	public Timer(long startTime) {
		this.startTime = startTime;
	}
	
	public long getTimeElapsed() {
		return System.currentTimeMillis() - startTime - (isPaused() ?  pausedTimer.getTimeElapsed() : 0) - totalPausedTime;
	}
	
	public void pause() {
		if (isRunning()) {
			pausedTimer = new Timer(System.currentTimeMillis());
		}
	}
	
	public boolean isPaused() {
		return pausedTimer != null;
	}
	
	public void resume() {
		if (isPaused()) {
			totalPausedTime += pausedTimer.getTimeElapsed();
			
			pausedTimer = null;
		}
	}
	
	public boolean isRunning() {
		return pausedTimer == null;
	}
	
	public static String timeElapsedToString(long timePassed) {
		int minutes = (int) (timePassed / 60000);
		int seconds = (int) ((((timePassed % 3600000) % 60000)) / 1000);
		
		StringBuilder timeSB = new StringBuilder();
			
		if (minutes < 10) timeSB.append("0" + minutes + ":");
		else timeSB.append(minutes + ":");
		
		if (seconds < 10) timeSB.append("0" + seconds);
		else timeSB.append(seconds);
		
		return timeSB.toString();
	}

}
