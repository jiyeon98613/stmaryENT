import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.time.Month;
import java.time.format.*;

import com.google.gson.*;

public class setHoliday
{
    String year_month = "";
    String day_of_First_day;
    int firstday;
    int month;
    int year_first_two;
    int year_last_two;
    String jsonFileName; 
    String jsonTargetMonth;
    HashMap<Integer, String> holidays = new HashMap<Integer, String>();

    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        setHoliday setHoliday = new setHoliday();
        setHoliday.printList();
        return;
    }

    private void printList()
    {
        int action;
        setYear_Date();
        System.out.println("first day of the month is " + day_of_First_day);
        System.out.println(""); //space
        while (true) {
            try {
                System.out.println("Choose an action you wanna do: (int)");
                System.out.println("1. add new holiday");
                System.out.println("2. delete one");
                System.out.println("3. print selected month's holiday");
                System.out.println("4. save as json file");
                System.out.println("5. change the month that I would like to work on.");
                System.out.println("6. quit");

                action = scanner.nextInt();
                switch (action) {
                    case 1:
                        addHoloday();
                        break;
                    case 2:
                        deleteHoliday();
                        break;
                    case 3:
                        printHolidays();
                        break;
                    case 4:
                        saveAsJSON(jsonFileName);
                        saveAsJSON(jsonTargetMonth);
                        break;
                        
                    case 5: 
                        setYear_Date();
                        break;
                    case 6:
                        scanner.close();
                        return;    

                }
                
            } catch (InputMismatchException e) {
                System.out.println("Error: input is not integer.");
                System.out.println("Try again.");
            } catch (DateTimeParseException e) {
                System.out.println("Error: format wrong: type as yyyy-mm");
                System.out.println("Try again.");
            } catch (Exception e) {
                System.out.println("Error: unidentifiable error " + e.getMessage());
                System.out.println("Try again.");
            }
        }
    }

    /**
     * store the holidays into the hashmap 'holidays'
     */
    private void addHoloday() throws InputMismatchException, Exception
    {
        int newHoliday = 0;
        try {
            System.out.println("type date:");
        newHoliday = scanner.nextInt();
        scanner.nextLine();
        System.out.println("type title.(enter for null)");
        String title = scanner.nextLine();
        holidays.put(newHoliday, title);
        } finally
        {
            System.out.println("your input: " + newHoliday + ": " + holidays.get(newHoliday));
        }
    }

    /**
     * 
     */
    private void deleteHoliday()
    {
        System.out.println("type date you are trying to delete:");
        int del = scanner.nextInt();
        scanner.nextLine();
        //if there is no such holidays in the hashmap
        if (holidays.get(del) != null) {
           holidays.remove(del);
        }else{ System.out.println("unregistered date.");}
    }

    private void printHolidays()
    {
        Set<Integer> date_entry = holidays.keySet();
        for (int date : date_entry) {
            System.out.println(month + "/" + date + ": " + holidays.get(date));
        }
    }

    /**
     * store the hashmap data into the json file.
     */
    private void saveAsJSON(String filename)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Object> monthMap = new HashMap<>();
        Map<String, Object>[] holidaysList = new HashMap[holidays.size()];
        Set<Integer> dataSet = holidays.keySet();
        int i = 0;
        for (int date : dataSet) {
            holidaysList[i].put("date", date);
            holidaysList[i].put("title", holidays.get(date));
            ++i;

        }
        monthMap.put("id", month);
        monthMap.put("firstday", firstday);
        monthMap.put("holidays", holidays);
         Map<String, Object> data;
        if(filename == jsonFileName)
        {
            // JsonArray
            String mString = getStrMonth(month);
            data = new HashMap<>();
            data.put(mString, monthMap);

        }
        else{
            data = monthMap;
        }
        String json = gson.toJson(data);
        
        //save yyyy_holidays file on current directory.
        //if the file already exists, then the data should be appended on it.
        try (FileWriter writer = new FileWriter( filename+".json", true)) { //true = available append mode
            writer.write(json);
            System.out.println( filename + "JSON file has saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(data.toString());

    }

    private void setupACalendar()
    {
        int start_day = 1;
        day_of_First_day = calculateDay(start_day, month, year_last_two, year_first_two);
        String year = Integer.toString(year_first_two) + Integer.toString(year_last_two);
        jsonFileName = year + "_holidays";
        jsonTargetMonth = year + "-" + getStrMonth(month);
    }

    /**
     * This method will set/reset the year and the month which user's trying to work
     * on.
     * this method will identify if there were a month user wasa working on
     * previously, and if true, then
     * it will ask user if they either want to delete or save the data.
     * This method will also handle the input exceptions and check the format.
     * Lastly, this method will ask user if the month they wrote is correctly being
     * set, and then it will call the setupACalendar() method.
     * 
     */
    private void setYear_Date()
    {
        while (true) {

            try {
                System.out.println("Type a month you would like to work on. (format: yyyy-mm)");
                year_month = scanner.nextLine();
                // input check
                int m = Integer.parseInt(year_month.substring(5));
                year_first_two = Integer.parseInt(year_month.substring(0, 2));
                year_last_two = Integer.parseInt(year_month.substring(2, 4));
                
                //if user has been working on other month, ask if they want to save the data
                if (holidays.isEmpty()) {
                    //do nothing
                }else{
                    boolean doNotUpdateMonth = false;
                    while (true) {
                        System.out.println("Do you want to delete the previous works?");
                        printHolidays();
                        System.out.println("Reply with Y/N:");
                        String response = scanner.nextLine();
                        if (response.toLowerCase() == "y" || response.toLowerCase() == "yes") {
                            //ignore the previous works and continue this method
                            //delete the previous works
                            year_month = "";
                            day_of_First_day = null;
                            firstday = 1;
                            month = 0;
                            year_first_two = 00;
                            year_last_two = 00;
                            jsonFileName = null;
                            jsonTargetMonth = null;
                            holidays.clear();;
                            break;
                        } else if (response.toLowerCase() == "n" || response.toLowerCase() == "no") {
                            doNotUpdateMonth = true;
                            System.out.println("Do you want to save your work? Y/N");
                            response = scanner.nextLine();
                            if (response.toLowerCase() == "y" || response.toLowerCase() == "yes") {
                                break;
                            } else if (response.toLowerCase() == "n" || response.toLowerCase() == "no") {
                                
                                break;
                            }
                        } else {
                            continue;
                        }   
                    }
                    if (doNotUpdateMonth) {
                        while (true) {
                            
                            System.out.println("Do you want to 1)save current work and continue or 2)continue working on the previous("
                                    + month + "th) month? ");
                            String integer = scanner.nextLine();
                            int res = Integer.getInteger(integer);
                            if (res == 1) {
                                // save current holidays
                                saveAsJSON(jsonFileName);
                                saveAsJSON(jsonTargetMonth);
                                //quit this while loop
                                break;
                            } else if (res == 2) {
                                // quit this function
                                return;
                            } else {
                                // repeat this question
                                continue;
                            }
                        }
                    }
                    
                }

                if (m > 12 || m < 1) {
                    System.out.println("Input is wrong! Try again. Month should be between [1,12]");
                    continue; //repeat the while loop
                }
                else{
                    //check if the user confirms with their input.
                    boolean wrongInput = false;
                    while (true) {
                        System.out.println(
                                "Please confirms the year and month:" + year_first_two + year_last_two + "-" + m);
                        System.out.println("answer with Y/N");
                        String response = scanner.nextLine();
                        if (response.toLowerCase() == "y" || response.toLowerCase() == "yes") {
                            break;
                        } else if (response.toLowerCase() == "n" || response.toLowerCase() == "no") {
                            wrongInput = true;
                            break;
                        } else {
                            continue;
                        }
                    }
                    if (wrongInput) {
                        continue; // repeat the outer while loop
                    }
                    month = m;
                    System.out.println("New year and month is set.");
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input Exception. Try again.");
            }
        }
        setupACalendar();
        
    }
    private String calculateDay(int k, int m, int d, int c )
    {
        if (m < 3) {
            d -= k;
            m += 10;
        }
        else{m -=2;}
        //System.out.println("k:"+k + " m:" + m + " d:" + d + " c:"+c );
        int day = k + ((13*m-1)/5) + d + (d/4) +(c/4)-2*c;
        if (day<0) {
            day %= 7;
            day += 7;
        }
        else{
            day %= 7;
        }
        firstday = day;
        String dayStr = "";
        switch (day) {
            case 0:
                dayStr = "Sunday";
                break;
            case 1:
                dayStr = "Monday";
                break;
            case 2:
                dayStr = "Tuesday";
                break;
            case 3:
                dayStr = "Wednesday";
                break;
            case 4:
                dayStr = "Thursday";
                break;
            case 5:
                dayStr = "Friday";
                break;
            case 6:
                dayStr = "Saturday";
                break;
            default:
                break;
        }
        return dayStr;
    }
    private String getStrMonth(int m)
    {
        String s =Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return s.toLowerCase();
    }
}