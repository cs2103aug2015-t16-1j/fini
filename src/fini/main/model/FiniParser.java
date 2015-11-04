package fini.main.model;

import java.time.Period;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.ParseLocation;
import com.joestelmach.natty.Parser;

import fini.main.model.Task.Priority;

public class FiniParser {
	private static FiniParser finiParser;
	private Parser parser;

	private static final String[] REDUNDANT_WORDS = {" on ", " from ", " by "};

	private String storedParameters;
	private String cleanParameters;
	private Priority priority;
	private ArrayList<LocalDateTime> datetimes;
	private boolean isRecurring;
	private LocalDateTime recursUntil;
	private Period interval;
	private String notParsed;

	public FiniParser() {
		initializeFields();
	}

	public String parse(String commandParameters) {
		try {
			initializeFields();
			storedParameters = commandParameters;
			cleanParameters = storedParameters; // init of clean

			String[] splitStoredParameters = storedParameters.split(" ");
			priority = determinePriority(splitStoredParameters);
			notParsed = determineDatetimes(cleanParameters);
			notParsed = eliminateRedundantWords(notParsed);
			return "FiniParser.parse SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "FiniParser.parse ERROR";
		}
	}

	private Priority determinePriority(String[] splitStoredParameters) {
		List<String> words = Arrays.asList(splitStoredParameters);
		for (String word : words) {
			if (word.toLowerCase().equals("priority")) {
				if (words.indexOf(word) != words.size() - 1) {
					String priority = words.get(words.indexOf(word) + 1);
					Priority returnPriority;
					switch (priority.toLowerCase()) {
					case "high":
						returnPriority = Priority.HIGH;
						break;
					case "medium":
						returnPriority = Priority.MEDIUM;
						break;
					case "low":
						returnPriority = Priority.LOW;
						break;
					default:
						returnPriority = Priority.NORMAL;
						break;
					}

					if (!returnPriority.equals(Priority.NORMAL)) {
						cleanParameters = cleanParameters.replaceAll(word + " " + priority, "");
						cleanParameters = getSimpleCleanString(cleanParameters);
						return returnPriority;
					}
				} else {
					// "priority" keyword appears at the end
					break;
				}
			}
		}
		return Priority.NORMAL;
	}
	
	private String determineDatetimes(String cleanParameters) {
		if (cleanParameters.contains("repeat")) {
			System.out.println("HERE1");
			String[] splitParameters = cleanParameters.split("repeat");
			notParsed = processFrontPart(getSimpleCleanString(splitParameters[0])) + processBackPart(getSimpleCleanString(splitParameters[1]));
		} else {
			notParsed = processFrontPart(cleanParameters);
		}
		return getSimpleCleanString(notParsed);
	}
	
	private String processBackPart(String parameters) {
		System.out.println("HERE2");
		isRecurring = true;
		List<DateGroup> groups = parser.parse(parameters);
		if (!parameters.startsWith("every")) {
			return parameters;
		}
		String returnNotParsed = "";
		
		if (groups.size() != 0) {
			System.out.println(groups.size() + "|" + parameters.contains("until") + "|" + groups.get(0).isRecurring());
		} else {
			System.out.println("ZERORORO");
		}
		
		if (groups.size() == 0) { // everyday/every week (no until)
			returnNotParsed = everyWeekNoUntil(parameters);
		} else if (groups.size() == 1 && parameters.contains("until") && !groups.get(0).isRecurring()) {
			returnNotParsed = everyWeekUntil(parameters, groups.get(0));
		} else if (groups.size() == 1 && !parameters.contains("until") && groups.get(0).isRecurring()) {
			returnNotParsed = everyTwoWeeksNoUntil(parameters);
		} else if (groups.size() == 1 && parameters.contains("until") && groups.get(0).isRecurring()) {
			returnNotParsed = everyTwoWeeksUntil(parameters, groups.get(0));
		} else { // default: everyday endlessly
			interval = Period.ofDays(1);
		}
		return returnNotParsed;
	}
	
	private String everyTwoWeeksUntil(String parameters, DateGroup group) {
		recursUntil = LocalDateTime.ofInstant(group.getRecursUntil().toInstant(), ZoneId.systemDefault());
		String returnNotParsed = parameters;
		String[] splitParameters = parameters.split(" ");
		if (isValidNumbering(splitParameters[1]) && isIntervalUnits(splitParameters[2])) {
			System.out.println("HERE4");
			interval = determineIntervalUnits(splitParameters[1], splitParameters[2]);
			returnNotParsed = returnNotParsed.replaceAll("every " + splitParameters[1] + " " + splitParameters[2], "");
		}
		return returnNotParsed;
	}
	
	private String everyTwoWeeksNoUntil(String parameters) {
		String returnNotParsed = parameters;
		String[] splitParameters = parameters.split(" ");
		if (splitParameters[0].equals("every") && isValidNumbering(splitParameters[1]) && isIntervalUnits(splitParameters[2])) {
			interval = determineIntervalUnits(splitParameters[1], splitParameters[2]);
			returnNotParsed = returnNotParsed.replaceAll(splitParameters[0] + " " + splitParameters[1] + " " + splitParameters[2], "");
		}
		return returnNotParsed;
	}
	
	private String everyWeekUntil(String parameters, DateGroup group) {
		String returnNotParsed = parameters;
		recursUntil = LocalDateTime.ofInstant(group.getDates().get(0).toInstant(), ZoneId.systemDefault());
		String[] splitParameters = parameters.split(" ");
		if (parameters.startsWith("everyday")) {
			interval = Period.ofDays(1);
			returnNotParsed = returnNotParsed.replaceAll("everyday", "");
		} else if (splitParameters[0].equals("every") && isIntervalUnit(splitParameters[1])) {
			interval = determineIntervalUnit(splitParameters[1]);
			returnNotParsed = returnNotParsed.replaceAll("every", "");
			returnNotParsed = returnNotParsed.replaceAll(splitParameters[1], "");
		}
		returnNotParsed = returnNotParsed.replaceAll("until", "");
		returnNotParsed = returnNotParsed.replaceAll(group.getText(), "");
		return returnNotParsed;
	}
	
	private String everyWeekNoUntil(String parameters) {
		String returnNotParsed = parameters;
		String[] splitParameters = parameters.split(" ");
		if (parameters.startsWith("everyday")) {
			interval = Period.ofDays(1);
			returnNotParsed = returnNotParsed.replaceAll("everyday", "");
		} else if (splitParameters[0].equals("every") && isIntervalUnit(splitParameters[1])) {
			interval = determineIntervalUnit(splitParameters[1]);
			returnNotParsed = returnNotParsed.replaceAll("every", "");
			returnNotParsed = returnNotParsed.replaceAll(splitParameters[1], "");
		}
		return returnNotParsed;
	}
	
	private Period determineIntervalUnits(String numbering, String word) {
		String[] numWithinTen = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
		int number = 0;
		for (int i = 0; i < numWithinTen.length; ++i) {
			if (numWithinTen[i].equals(numbering)) {
				number = i + 1;
				break;
			}
		}
		
		if (number == 0) {
			try {
				number = Integer.parseInt(numbering);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		switch (word) {
		case "days":
			return Period.ofDays(number);
		case "weeks":
			return Period.ofWeeks(number);
		case "months":
			return Period.ofMonths(number);
		case "years":
			return Period.ofYears(number);
		default:
			return Period.ofDays(1);
		}
	}
	
	private Period determineIntervalUnit(String word) {
		switch (word) {
		case "day":
			return Period.ofDays(1);
		case "week":
			return Period.ofWeeks(1);
		case "month":
			return Period.ofMonths(1);
		case "year":
			return Period.ofYears(1);
		default:
			return Period.ofDays(1);
		}
	}
	
	private boolean isValidNumbering(String word) {
		String[] numWithinTen = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
		for (String num : numWithinTen) {
			if (word.equals(num)) {
				return true;
			}
		}
		
		try {
			Integer.parseInt(word);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean isIntervalUnits(String word) {
		return word.equals("days") || word.equals("weeks") || word.equals("months") || word.equals("years");
	}
	
	private boolean isIntervalUnit(String word) {
		return word.equals("day") || word.equals("week") || word.equals("month") || word.equals("year");
	}
	
	private String processFrontPart(String parameters) {
		List<DateGroup> groups = parser.parse(parameters);
		
		if (groups.size() == 0) {
			return getSimpleCleanString(parameters);
		} else {
			DateGroup group = groups.get(0);
			List<Date> dateList = group.getDates(); 
			Map<String, List<ParseLocation>> parseMap = group.getParseLocations();
			if (!parseMap.containsKey("explicit_time")) {
				for (Date date : dateList) {
					LocalDateTime temp = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
					datetimes.add(LocalDateTime.of(temp.toLocalDate(), LocalTime.MAX));
				}
			} else {
				for (Date date : dateList) {
					datetimes.add(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
				}
			}
			String returnNotParsed = parameters;
			for (ParseLocation parsedWord : parseMap.get("parse")) {
				returnNotParsed = returnNotParsed.substring(0, parsedWord.getStart() - 1) + returnNotParsed.substring(parsedWord.getEnd() - 1);
			}
			return getSimpleCleanString(returnNotParsed);
		}
	}
////////////////////////////////////////////////
	
	private String eliminateRedundantWords(String notParsed) {
		String cleanString = notParsed;
		for (String word : REDUNDANT_WORDS) {
			cleanString = cleanString.replaceAll(word, "");
		}
		return getSimpleCleanString(cleanString);
	}

	// Public Getters
	public String getStoredParameters() {
		return storedParameters;
	}

	public String getCleanParameters() {
		return cleanParameters;
	}

	public Priority getPriority() {
		return priority;
	}
	
	public ArrayList<LocalDateTime> getDatetimes() {
		return datetimes;
	}

	public boolean getIsRecurring() {
		return isRecurring;
	}

	public LocalDateTime getRecursUntil() {
		return recursUntil;
	}

	public Period getInterval() {
		return interval;
	}

	public String getNotParsed() {
		return notParsed;
	}

	// Utility Methods
	private String getSimpleCleanString(String input) {
		return input.trim().replaceAll("\\s+", " ");
	}

	// Initialization Methods
	public static FiniParser getInstance() {
		if (finiParser == null) {
			finiParser = new FiniParser();
		}
		return finiParser;
	}

	private void initializeFields() {
		parser = new Parser();
		storedParameters = "";
		priority = null;
		datetimes = new ArrayList<LocalDateTime>();
		isRecurring = false;
		recursUntil = null;
		interval = null;
		notParsed = "";
	}
}
