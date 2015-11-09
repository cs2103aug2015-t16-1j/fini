package fini.main.tests;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.PrimitiveIterator.OfDouble;

public class JavaRobotExample
{
	Robot robot = new Robot();

	public static void main(String[] args) throws AWTException
	{
		new JavaRobotExample();
	}

	public JavaRobotExample() throws AWTException
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
		
//		robot.delay(500);
//		leftClick();
//
//		robot.delay(500);
//		type("Hello, world");
//
//		robot.mouseMove(40, 160);
//		robot.delay(500);
//
//		leftClick();
//		robot.delay(500);
//		leftClick();
//
//		robot.delay(500);
//		type("This is a test of the Java Robot class");
//
//		robot.delay(50);
//		type(KeyEvent.VK_DOWN);
//
//		robot.delay(250);
//		type("Four score and seven years ago, our fathers ...");
//
//		robot.delay(1000);
//		System.exit(0);
	}

	private void leftClick()
	{
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.delay(200);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(200);
	}
	
	private void rightClick() {
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.delay(200);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
        robot.delay(200);
	}

	private void type(int i)
	{
		robot.delay(40);
		robot.keyPress(i);
		robot.keyRelease(i);
	}

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
