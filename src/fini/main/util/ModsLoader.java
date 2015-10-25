package fini.main.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import fini.main.model.Task;
import fini.main.model.Task.Priority;

public class ModsLoader {
	private ArrayList<Task> lessonTasks;
	
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHMM");
	
	public ModsLoader(File modsFile) {
		lessonTasks = new ArrayList<Task>();
		assert modsFile.exists();
		try {
			Document doc = Jsoup.parse(modsFile, "UTF-8");
			Elements lessons = doc.getElementsByClass("lesson");
			if (!lessons.isEmpty()) {
				for (Element lesson : lessons) {
					String title = lesson.text();
					LocalTime lessonStartTime = getTime(lesson.parent().className());
					LocalTime lessonEndTime = getTime(lesson.parent().nextElementSibling().className());
					LocalDate lessonDate = getDate(lesson.parent().parent().parent().id());
					ArrayList<LocalDateTime> lessonDateTimes = new ArrayList<LocalDateTime>();
					lessonDateTimes.add(LocalDateTime.of(lessonDate, lessonStartTime));
					lessonDateTimes.add(LocalDateTime.of(lessonDate, lessonEndTime));
					lessonTasks.add(new Task.TaskBuilder(title, true)
							            .setDatetimes(lessonDateTimes)
							            .setInterval(Period.ofWeeks(1)).build());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Task> getLessonTasks() {
		return lessonTasks;
	}
	
	private LocalTime getTime(String lessonTime) {
		String temp = lessonTime.substring(1, 3) + lessonTime.substring(5);
		LocalTime time = LocalTime.parse(temp, timeFormatter);
		return time;
	}
	
	private LocalDate getDate(String lessonDate) {
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(lessonDate);
		DateGroup group = groups.get(0);
		List<Date> dates = group.getDates();
		Date date = dates.get(0);
		LocalDate lessonLocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return lessonLocalDate;
	}
	
	public static void main(String[] args) {
		
	}
}
