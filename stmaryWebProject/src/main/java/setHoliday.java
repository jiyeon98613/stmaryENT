

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.time.Month;
import java.time.format.*;
import java.lang.reflect.Type;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

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
    BufferedReader br;
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());

    public static void main(String[] args)
    {
        
        setHoliday setHoliday = new setHoliday();
        //파일 인코딩 확인과정
        System.out.println("Current file encoding: " + System.getProperty("file.encoding"));
        System.out.println("한글 테스트중.");
            // 사용 가능한 문자셋 출력 (선택적)
        //java.nio.charset.Charset.availableCharsets().keySet().forEach(System.out::println);

        System.out.println("If the line above is broken, try again with typing \"chcp 65001 \" before running this program.");
        setHoliday.printList();
        return;
    }

    /**
     * This method will load the json files(yearly-jsons/yyyy-holidays.json) data into the program .
     */
    private void readingExistingHolidays(){

    }

    private void printList()
    {
        try {
            br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        } catch (Exception e) {
            System.err.println("error in buffer reader at printlist()");
        }
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
            String dateStr = br.readLine();
            int date = Integer.parseInt(dateStr);
            //newHoliday = scanner.nextInt();
            //scanner.nextLine();
            System.out.println("type title.(enter for null)");
            //String title = scanner.nextLine();
            String title = br.readLine();
            holidays.put(date, title);
        } finally
        {
            System.out.println("your input: " + newHoliday + ": " + holidays.get(newHoliday));
        }
    }

    private void deleteHoliday()
    {
        while (true) {
            System.out.println("type date you are trying to delete:");
            System.out.println("(to return to main, type \"m\")");
            String del = scanner.nextLine();
            if (del == "m") {
                return;
            }
            int intDel = Integer.getInteger(del); // int or null
            // if there is no such holidays in the hashmap
            if (holidays.get(intDel) != null) {
                holidays.remove(intDel);
                System.out.println("holiday on the " + intDel + "th of the " + getStrMonth(month) + "is deleted:");
                printHolidays();
                System.out.println("");

            } else {
                System.out.println("unregistered date or wrong input type. Try again.");
                continue;
            }
        }
    }

    private void printHolidays()
    {
        if (holidays == null || holidays.isEmpty()) {
            System.out.println("Current month doesn't have any holidays being set up yet.");
            System.out.println("");
            return;
        }
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
        // Map<String, Object>[] holidaysList = new HashMap[holidays.size()];
        // Set<Integer> dataSet = holidays.keySet();
        // int i = 0;
        // for (int date : dataSet) {
        //     holidaysList[i].put("date", date);
        //     holidaysList[i].put("title", holidays.get(date));
        //     ++i;

        // }
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

        //creates new parent file if not
        File file = new File(filename + ".json");
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); 
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(filename + ".json", true), StandardCharsets.UTF_8))) {
            writer.write(json);
        }  catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(data.toString());

    }

    /**
     * check if there is any saved data of current month,
     * 
     */
    private void setupACalendar()
    {
        String folderPath = "monthly-jsons/";
        String year = Integer.toString(year_first_two) + Integer.toString(year_last_two);
        jsonFileName = "yearly-jsons/" + year + "_holidays";
        jsonTargetMonth = folderPath + year + "-" + getStrMonth(month);
        //folderPath에 jsonTargetMonth파일이 존재하는지 확인 후, 존자하면 그 파일안의 holiday 데이터를
        // mem var인 HashMap<Integer, String> holidays 에 로드하기.
        Gson gson = new Gson();

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonTargetMonth + ".json")));
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> jsonMap = gson.fromJson(jsonContent, type);

            if (jsonMap.containsKey("holidays")) {
                Map<String, String> holidaysMap = (Map<String, String>) jsonMap.get("holidays");
                for (Map.Entry<String, String> entry : holidaysMap.entrySet()) {
                    holidays.put(Integer.parseInt(entry.getKey()), entry.getValue());
                }
            }

            //delete all data in json file
            new FileWriter(jsonTargetMonth + ".json", false).close();
            // print loaded data.
            printHolidays();
            
        } catch (NoSuchFileException nsfe){
            System.out.println("Identified as a new Month data.");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        int start_day = 1;
        day_of_First_day = calculateDay(start_day, month, year_last_two, year_first_two);
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
                String year_month_temp = scanner.nextLine();
                // input check
                int m = Integer.parseInt(year_month_temp.substring(5));
                Integer year_first_two_temp = Integer.parseInt(year_month_temp.substring(0, 2));
                Integer year_last_two_temp = Integer.parseInt(year_month_temp.substring(2, 4));
                
                //if user has been working on other month, ask if they want to save the data
                if (holidays.isEmpty()) {
                    //do nothing
                }else{
                    boolean doNotUpdateMonth = false;
                    while (true) {
                        System.out.println("Do you want to delete the previous works?");
                        printHolidays();
                        System.out.println("Reply with Y/N:");
                        String response = scanner.nextLine().toLowerCase();
                        if (response.equals("y") || response.equals("yes")) {
                            // ignore the previous works and continue this method
                            // delete the previous works
                            year_month = "";
                            day_of_First_day = null;
                            firstday = 1;
                            month = 0;
                            year_first_two = 00;
                            year_last_two = 00;
                            jsonFileName = null;
                            jsonTargetMonth = null;
                            holidays.clear();
                            ;
                            break;
                        } else if (response.equals("n") || response.equals("no")) {
                            doNotUpdateMonth = true;
                            System.out.println("Do you want to save your work? Y/N");
                            response = scanner.nextLine().toLowerCase();
                            if (response.equals("y") || response.equals("yes")) {
                                break;
                            } else if (response.equals("n") || response.equals("no")) {

                                break;
                            }
                        } else {
                            continue;
                        }

                    }
                    
                    if (doNotUpdateMonth) {
                        while (true) {
                            
                            System.out.println("Do you want to 1)save current work and continue or 2)continue working on the previous month("
                                    + getStrMonth(month) + ")?");
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
                                "Please confirms the year and month:" + year_first_two_temp + year_last_two_temp + "-" + m);
                        System.out.println("answer with Y/N");
                        String response = scanner.nextLine().toLowerCase();
                        if (response.equals("y")  || response.equals("yes")) {
                            break;
                        } else if (response.equals("n") || response.equals("no")) {
                            wrongInput = true;
                            break;
                        } else {
                            continue;
                        }
                    }
                    if (wrongInput) {
                        continue; // repeat the outer while loop
                    }

                    //update the mem vars
                    month = m;
                    year_first_two = year_first_two_temp;
                    year_last_two = year_last_two_temp;
                    year_month = year_month_temp;

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
        String s =Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase();
        return s;
    }
}