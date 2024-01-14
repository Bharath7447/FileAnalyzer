import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileAnalyzer {
    public static void main(String[] args) {
        String csvFile = "D:\\Download\\JAVA\\Assignment_Timecard.xlsx - Sheet1.csv"; // Replace with the path to your CSV file

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm"); // Adjust date format based on your CSV

            // Read the first line (header) and discard it
            br.readLine();

            String previousEmployeeName = null;
            Date previousTimeOut = null;
            Map<String, Integer> consecutiveDaysMap = new HashMap<>(); // Moved outside the loop
            Map<String, Boolean> printedMessageMap = new HashMap<>(); // Track printed messages

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 8) { // Ensure there are at least 8 fields
                    String employeeName = fields[7]+fields[8];
                    String position = fields[0].trim();
                    String timeInString = fields[2].trim();
                    String timeOutString = fields[3].trim(); // Assuming the time out is in the 4th column
                    String dateString = fields[5].trim(); // Assuming the date is in the 6th column

                    // Check for empty fields before processing
                    if (!employeeName.isEmpty() && !position.isEmpty() && !timeOutString.isEmpty() && !dateString.isEmpty()) {
                        try {
                            Date currentTimeOut = dateFormat.parse(timeOutString);

                            // Time between shifts logic
                            if (previousEmployeeName != null && previousEmployeeName.equals(employeeName)) {
                                long hoursBetweenShifts = (currentTimeOut.getTime() - previousTimeOut.getTime()) / (60 * 60 * 1000);

                                // Check if the message has already been printed for the current employee
                                if (hoursBetweenShifts > 1 && hoursBetweenShifts < 10 && !printedMessageMap.getOrDefault(employeeName, false)) {
                                    System.out.println("Employee: " + employeeName + ", Position: " + position );
                                    printedMessageMap.put(employeeName, true); // Mark as printed
                                }
                            }

                            previousEmployeeName = employeeName;
                            previousTimeOut = currentTimeOut;

                            // Consecutive days logic
                            if (consecutiveDaysMap.containsKey(employeeName)) {
                                consecutiveDaysMap.put(employeeName, consecutiveDaysMap.get(employeeName) + 1);
                            } else {
                                consecutiveDaysMap.put(employeeName, 1);
                            }

                            if (consecutiveDaysMap.get(employeeName) == 7) {
                                System.out.println("Employee: " + employeeName + ", Position: " + position);
                                // You can choose to break or continue processing, depending on your requirements
                            }
                            Date timeIn = dateFormat.parse(timeInString);
                            Date timeOut = dateFormat.parse(timeOutString);

                            long hoursWorked = (timeOut.getTime() - timeIn.getTime()) / (60 * 60 * 1000);

                            if (hoursWorked > 14) {
                                System.out.println("Employee: " + employeeName + ", Position: " + position);
                                // You can choose to break or continue processing, depending on your requirements
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
