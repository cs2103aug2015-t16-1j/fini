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

import fini.main.MainApp;
import fini.main.model.Task.Priority;

/**
 * This FiniParser is created to evaluate user command, by extracting necessary information like:
 * DateTimes, recur info, priority, project
 * 
 * The date & time parser is making use of an open-source Java project called Natty.
 * 
 * Natty is a natural language date parser written in Java.
 * http://natty.joestelmach.com/
 * 
 * @@author A0127483B
 */
public class FiniParser {
    /* ***********************************
     * Constants
     * ***********************************/
    private static final String[] REDUNDANT_WORDS = {"on", "from", "by"};
    
    /* ***********************************
     * Fields
     * ***********************************/
    // Singleton
    private static FiniParser finiParser;
    private Parser parser;

    private String storedParameters;
    private String cleanParameters;
    private Priority priority;
    private String projectName;
    private ArrayList<LocalDateTime> datetimes;
    private boolean recurFlag;
    private boolean isRecurring;
    private LocalDateTime recursUntil;
    private Period interval;
    private String notParsed;

    /* ***********************************
     * Private constructor
     * ***********************************/
    private FiniParser() {
        initializeFields();
    }
    
    // getInstance method
    public static FiniParser getInstance() {
        if (finiParser == null) {
            finiParser = new FiniParser();
        }
        return finiParser;
    }

    /* ***********************************
     * Public parsing method
     * ***********************************/    
    public String parse(String commandParameters) {
        try {
            initializeFields();
            storedParameters = commandParameters;
            cleanParameters = storedParameters; // init of clean

            String[] splitStoredParameters = storedParameters.split(" ");
            priority = determinePriority(splitStoredParameters);
            projectName = determineProjectName(splitStoredParameters);
            notParsed = determineDatetimes(cleanParameters);
            notParsed = eliminateRedundantWords(notParsed);
            
            MainApp.finiLogger.info("Clean parameters: " + cleanParameters);
            MainApp.finiLogger.info("Not parsed words: " + notParsed);
            
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

    private String determineProjectName(String[] splitStoredParameters) {
        List<String> words = Arrays.asList(splitStoredParameters);
        for (String word : words) {
            if (word.toLowerCase().equals("project")) {
                if (words.indexOf(word) != words.size() - 1) {
                    String projectName = words.get(words.indexOf(word) + 1);
                    cleanParameters = cleanParameters.replaceAll(word + " " + projectName, "");
                    cleanParameters = getSimpleCleanString(cleanParameters);
                    return projectName;
                } else {
                    // "project" keyword appears at the end
                    break;
                }
            }
        }
        return "Inbox";
    }

    private String determineDatetimes(String cleanParameters) {
        if (cleanParameters.contains("repeat")) {
            String[] splitParameters = cleanParameters.split("repeat");
            if (splitParameters.length == 2) {
                notParsed = processFrontPart(getSimpleCleanString(splitParameters[0])) + processBackPart(getSimpleCleanString(splitParameters[1]));
            } else {
                notParsed = processFrontPart(getSimpleCleanString(splitParameters[0]));
            }
        } else {
            notParsed = processFrontPart(cleanParameters);
        }
        return getSimpleCleanString(notParsed);
    }

    /* ***********************************
     * Public getters
     * ***********************************/
    public String getStoredParameters() {
        return storedParameters;
    }

    public String getCleanParameters() {
        return cleanParameters;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getProjectName() {
        return projectName;
    }

    public ArrayList<LocalDateTime> getDatetimes() {
        assert datetimes != null;
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

    /* ***********************************
     * Utility method
     * ***********************************/
    private String getSimpleCleanString(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }
    
    private String processBackPart(String parameters) {
        if (recurFlag) {
            isRecurring = true;
        } else {
            return parameters;
        }
        List<DateGroup> groups = parser.parse(parameters);
        if (!parameters.startsWith("every")) {
            interval = Period.ofDays(1);
            return parameters;
        }

        String returnNotParsed = "";
        if (groups.size() == 0) { // everyday/every week (no until)
            //          System.out.println("A");
            returnNotParsed = everyWeekNoUntil(parameters);
        } else if (groups.size() == 1 && parameters.contains("until") && !groups.get(0).isRecurring()) {
            //          System.out.println("B");
            returnNotParsed = everyWeekUntil(parameters, groups.get(0));
        } else if (groups.size() == 1 && !parameters.contains("until") && groups.get(0).isRecurring()) {
            //          System.out.println("C");
            returnNotParsed = everyTwoWeeksNoUntil(parameters);
        } else if (groups.size() == 1 && parameters.contains("until") && groups.get(0).isRecurring()) {
            //          System.out.println("D");
            returnNotParsed = everyTwoWeeksUntil(parameters, groups.get(0));
        } else { // default: everyday endlessly
            //          System.out.println("E");
            interval = Period.ofDays(1);
        }
        return returnNotParsed;
    }

    private String everyTwoWeeksUntil(String parameters, DateGroup group) {
        recursUntil = LocalDateTime.ofInstant(group.getRecursUntil().toInstant(), ZoneId.systemDefault());
        String returnNotParsed = parameters;
        String[] splitParameters = parameters.split(" ");
        if (isValidNumbering(splitParameters[1]) && isIntervalUnits(splitParameters[2])) {
            interval = determineIntervalUnits(splitParameters[1], splitParameters[2]);
            returnNotParsed = returnNotParsed.replaceAll("every " + splitParameters[1] + " " + splitParameters[2], "");
        } else {
            interval = Period.ofDays(1);
        }
        return returnNotParsed;
    }

    private String everyTwoWeeksNoUntil(String parameters) {
        String returnNotParsed = parameters;
        String[] splitParameters = parameters.split(" ");
        if (splitParameters[0].equals("every") && isValidNumbering(splitParameters[1]) && isIntervalUnits(splitParameters[2])) {
            interval = determineIntervalUnits(splitParameters[1], splitParameters[2]);
            returnNotParsed = returnNotParsed.replaceAll(splitParameters[0] + " " + splitParameters[1] + " " + splitParameters[2], "");
        } else {
            interval = Period.ofDays(1);
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
            recurFlag = false;
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

    private String eliminateRedundantWords(String notParsed) {
        String cleanString = "";
        ArrayList<String> splitCleanString = new ArrayList<String>(Arrays.asList(notParsed.split(" ")));
        for (String word : REDUNDANT_WORDS) {
            while (splitCleanString.contains(word)) {
                int removeIndex = splitCleanString.indexOf(word);
                splitCleanString.set(removeIndex, "");
            }
        }
        for (String word : splitCleanString) {
            cleanString += word + " ";
        }
        return getSimpleCleanString(cleanString);
    }

    /* ***********************************
     * Initialization method
     * ***********************************/
    private void initializeFields() {
        parser = new Parser();
        storedParameters = "";
        priority = null;
        projectName = "Inbox";
        datetimes = new ArrayList<LocalDateTime>();
        recurFlag = true;
        isRecurring = false;
        recursUntil = null;
        interval = null;
        notParsed = "";
    }
}
