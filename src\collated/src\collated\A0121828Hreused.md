# A0121828Hreused
###### src/fini/main/tests/RobotTest.java
``` java
	private void leftClick()
	{
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.delay(200);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(200);
	}

```
###### src/fini/main/tests/RobotTest.java
``` java
	private void rightClick() {
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.delay(200);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
        robot.delay(200);
	}
	
```
###### src/fini/main/tests/RobotTest.java
``` java
	private void type(int i)
	{
		robot.delay(40);
		robot.keyPress(i);
		robot.keyRelease(i);
	}

```
###### src/fini/main/tests/RobotTest.java
``` java
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
```
