package fini.main.tests;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/*
 * This class employs the Java Robot to test the GUI is a pre-programmed way.
 * Since this is pre-programmed onto fixed coordinates on a laptop, it may not 
 * work as expected on another computer.
 * 
 * @@author A0121828H
 */

public class RobotTest
{
	Robot robot = new Robot();

	public static void main(String[] args) throws AWTException
	{
		new RobotTest();
	}

	public RobotTest() throws AWTException
	{
		robot.setAutoDelay(40);
		robot.setAutoWaitForIdle(true);

		robot.delay(4000);
		robot.mouseMove(116, 206);
		rightClick();
		robot.delay(500);
		
		robot.delay(4000);
		robot.mouseMove(226,590);
		robot.delay(500);

		robot.mouseMove(467, 592);
		leftClick();
		robot.delay(500);
		
		robot.mouseMove(839, 428);
		leftClick();
		robot.delay(500);
		
		robot.keyPress(KeyEvent.VK_ENTER);
		
		robot.mouseMove(389, 665);
		leftClick();
		robot.delay(500);
		
		type("add new event today");
		
		robot.delay(50);
		type(KeyEvent.VK_ENTER);
	}
	
	// @@author A0121828H-reused
	private void leftClick()
	{
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.delay(200);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(200);
	}

	// @@author A0121828H-reused
	private void rightClick() {
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.delay(200);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
        robot.delay(200);
	}
	
	// @@author A0121828H-reused
	private void type(int i)
	{
		robot.delay(40);
		robot.keyPress(i);
		robot.keyRelease(i);
	}

	//@@author A0121828H-reused
	private void type(String s)
	{
		byte[] bytes = s.getBytes();
		for (byte b : bytes)
		{
			int code = b;
			// keycode only handles [A-Z] (which is ASCII decimal [65-90])
			if (code > 96 && code < 123) code = code - 32;
			robot.delay(40);
			robot.keyPress(code);
			robot.keyRelease(code);
		}
	}
}
