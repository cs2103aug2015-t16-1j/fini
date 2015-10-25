package fini.main.model;

import java.time.Period;
import java.time.LocalDateTime;
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
	
	private String storedParameters;
	private String cleanParameters;
	private Priority priority;
//	private String projectName;
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
//			System.out.println("CleanParameters - FiniParser parse(): " + cleanParameters);
			
			String[] splitStoredParameters = storedParameters.split(" ");
			priority = determinePriority(splitStoredParameters);
			// projectName = determineProjectName(userInputSplitArray);
			notParsed = determineDatetimes(cleanParameters);
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

	//	private String determineProjectName(String[] userInputSplitArray) {
	//		ArrayList<String> words = new ArrayList<String>(Arrays.asList(userInputSplitArray));
	//		for (String word : words) {
	//			if (word.toLowerCase().equals("project")) {
	//				if (words.indexOf(word) != words.size() - 1) {
	//					String projectName = words.get(words.indexOf(word) + 1);
	//										
	//					cleanParameters = cleanParameters.replaceAll(word + " " + projectName, "");
	//					cleanParameters = getSimpleCleanString(cleanParameters);
	//				} else {
	//					break;
	//				}
	//			}
	//		}
	//		return null;
	//	}

	private String determineDatetimes(String cleanParameters) {
		String tempParameters = cleanParameters;
		List<DateGroup> groups = parser.parse(tempParameters);
		
		if (groups.size() == 1) {
			DateGroup group = groups.get(0);
			if (group.isRecurring()) {
				List<Date> dateList = group.getDates();
				Map<String, List<ParseLocation>> parseMap = group.getParseLocations();
				
				for (Date date : dateList) {
					datetimes.add(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
				}
				
				notParsed = tempParameters;
				for (ParseLocation parsedWord : parseMap.get("parse")) {
					notParsed = notParsed.substring(0, parsedWord.getStart() - 1) + notParsed.substring(parsedWord.getEnd() - 1);
				}
				return getSimpleCleanString(notParsed);
			} else {
				if (tempParameters.contains("repeat everyday")) { // add xxx repeat everyday
					isRecurring = true;
					interval = Period.ofDays(1);
					notParsed = tempParameters.replaceAll("repeat everyday", "");
					return getSimpleCleanString(notParsed);
				}
				
				List<Date> dateList = group.getDates();
				for (Date date : dateList) {
					datetimes.add(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
				}

				String matchingValue = group.getText();
				notParsed = tempParameters;
				notParsed = notParsed.replaceAll(matchingValue, "");
				return getSimpleCleanString(notParsed);
			}
		} else if (groups.size() == 2) {
			if (tempParameters.contains("repeat every") && !groups.get(0).isRecurring()) {
				DateGroup group1 = groups.get(0);
				List<Date> dateList1 = group1.getDates();
				Map<String, List<ParseLocation>> parseMap1 = group1.getParseLocations();
				for (Date date : dateList1) {
					datetimes.add(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
				}
				
				notParsed = tempParameters;
				for (ParseLocation parsedWord : parseMap1.get("parse")) {
					notParsed = notParsed.substring(0, parsedWord.getStart() - 1) + notParsed.substring(parsedWord.getEnd() - 1);
				}
				notParsed = getSimpleCleanString(notParsed);
				
				DateGroup group2 = groups.get(1);
				if (group2.isRecurring()) {
					Map<String, List<ParseLocation>> parseMap2 = group2.getParseLocations();
					String potentialInterval = "every " + parseMap2.get("date_time_alternative").get(0).getText();
					boolean validIntervalFormat = checkIntervalFormat(potentialInterval);
					if (validIntervalFormat) {
						isRecurring = true;
						interval = determineInterval(potentialInterval);
						recursUntil = group2.getRecursUntil() == null ? null : LocalDateTime.ofInstant(group2.getRecursUntil().toInstant(), ZoneId.systemDefault());
					}
					
					for (ParseLocation parsedWord : parseMap2.get("parse")) {
						notParsed = notParsed.substring(0, parsedWord.getStart() - 1) + notParsed.substring(parsedWord.getEnd() - 1);
					}
					return getSimpleCleanString(notParsed);
				} else { // add xxx repeat everyday until xxx / add xxx repeat every minute until
					String potentialInterval = tempParameters.substring(tempParameters.indexOf("repeat") + 6, tempParameters.indexOf("until")).trim();
					boolean validIntervalFormat = checkIntervalFormat(potentialInterval);
					if (validIntervalFormat) {
						isRecurring = true;
						interval = determineInterval(potentialInterval);
						recursUntil = LocalDateTime.ofInstant(group2.getDates().get(0).toInstant(), ZoneId.systemDefault());
					}
					notParsed = notParsed.replaceAll("repeat.*until", "");
					return getSimpleCleanString(notParsed);
				}
			} else {
				DateGroup group = groups.get(0);
				List<Date> dateList = group.getDates();
				Map<String, List<ParseLocation>> parseMap = group.getParseLocations();
				for (Date date : dateList) {
					datetimes.add(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
				}
				notParsed = tempParameters;
				for (ParseLocation parsedWord : parseMap.get("parse")) {
					notParsed = notParsed.substring(0, parsedWord.getStart() - 1) + notParsed.substring(parsedWord.getEnd() - 1);
				}
				return getSimpleCleanString(notParsed);
			}
		} else {
			return cleanParameters;
		}
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

//	public String getProjectName() {
//		return projectName;
//	}

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
	
	private boolean checkIntervalFormat(String potentialInterval) {
		String[] splitPotentialInterval = potentialInterval.split(" ");
		if (splitPotentialInterval.length == 1) {
			if (splitPotentialInterval[0].toLowerCase().equals("everyday")) {
				return true;
			}
		} else if (splitPotentialInterval.length == 2) {
			if (splitPotentialInterval[0].toLowerCase().equals("every")) {
				String intervalUnit = splitPotentialInterval[1].toLowerCase();
				if (intervalUnit.equals("day") ||
					intervalUnit.equals("week") ||
					intervalUnit.equals("month") ||
					intervalUnit.equals("year")) {
					return true;
				}
			}
		} else if (splitPotentialInterval.length == 3) {
			if (splitPotentialInterval[0].toLowerCase().equals("every")) {
				try {
					int prefix = Integer.parseInt(splitPotentialInterval[1]);
					String intervalUnit = splitPotentialInterval[2].toLowerCase();
					if (intervalUnit.equals("days") ||
						intervalUnit.equals("weeks") ||
						intervalUnit.equals("months") ||
						intervalUnit.equals("years")) {
						return true;
					}
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return false;
	}
	
	private Period determineInterval(String potentialInterval) {
		String[] splitPotentialInterval = potentialInterval.split(" ");
		if (splitPotentialInterval.length == 1) {
			return Period.ofDays(1);
		} 
		
		if (splitPotentialInterval.length == 2) {
			String intervalUnit = splitPotentialInterval[1].toLowerCase();
			switch (intervalUnit) {
			case "day":
				return Period.ofDays(1);
			case "week":
				return Period.ofWeeks(1);
			case "month":
				return Period.ofMonths(1);
			case "year":
				return Period.ofYears(1);
			}
		}
		
		if (splitPotentialInterval.length == 3) {
			int prefix = Integer.parseInt(splitPotentialInterval[1]);
			String intervalUnit = splitPotentialInterval[2].toLowerCase();
			switch (intervalUnit) {
			case "days":
				return Period.ofDays(prefix);
			case "weeks":
				return Period.ofWeeks(prefix);
			case "months":
				return Period.ofMonths(prefix);
			case "years":
				return Period.ofYears(prefix);
			}
		}
		
		return Period.ZERO;
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
//		projectName = null;
		datetimes = new ArrayList<LocalDateTime>();
		isRecurring = false;
		recursUntil = null;
		interval = null;
		notParsed = "";
	}
}
