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
        System.out.println("Type a month you want to work on(format: yyyy-mm)");
        year_month = scanner.nextLine();
        //@TODO format checker
        System.out.println("year and month: " + year_month);
        setupACalendar();   // day of the week of first day of the month is setted.
        System.out.println("first day of the month is " + day_of_First_day);
        System.out.println(""); //space
        while (true) {
            try {
                System.out.println("Choose an action you wanna do: (int)");
                System.out.println("1. add new holiday");
                System.out.println("2. delete one");
                System.out.println("3. print selected month's holiday");
                System.out.println("4. save as json file");
                System.out.println("5. quit");
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
                        saveAsJSON();
                        break;
                        
                    case 5: 
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
    private void saveAsJSON()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // JsonArray
        String mString = getStrMonth(month);
        Map<String, Object> monthMap = new HashMap<>();
        Map<String, Object>[] holidaysList = new HashMap[holidays.size()];
        Set<Integer> dataSet = holidays.keySet();
        int i = 0;
        for (int date: dataSet)  {
            holidaysList[i].put("date", date);
            holidaysList[i].put("title", holidays.get(date));
            ++i;
            
        }
        monthMap.put("id", month);
        monthMap.put("firstday", firstday);
        monthMap.put("holidays", holidays);

        Map<String, Object> data = new HashMap<>();
        data.put(mString, monthMap);

        String json = gson.toJson(data);

        //save file on current directory.
        try (FileWriter writer = new FileWriter( jsonFileName+".json")) {
            writer.write(json);
            System.out.println("JSON file has saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(data.toString());
    }

    private void setupACalendar()
    {
        int start_day = 1;
        month = Integer.parseInt(year_month.substring(5));
        year_first_two = Integer.parseInt(year_month.substring(0, 2));
        year_last_two = Integer.parseInt(year_month.substring(2, 4));
        day_of_First_day = calculateDay(start_day, month, year_last_two, year_first_two);
        jsonFileName = Integer.toString(year_first_two) + Integer.toString(year_last_two)+ "_holidays";
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