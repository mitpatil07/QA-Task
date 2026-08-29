package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVDataReader — Utility for reading CSV test-data files into TestNG @DataProvider format
 * ===========================================================================================
 * TestNG's @DataProvider annotation expects a method that returns Object[][].
 * Each outer array element is one test invocation; the inner array holds the parameters.
 *
 * Example CSV (login-data.csv):
 *   email,password,expectedResult          ← header row (skipped)
 *   user@test.com,Pass@123,success         ← row 1 → Object[]{"user@test.com","Pass@123","success"}
 *   bad@test.com,wrong,failure             ← row 2 → Object[]{"bad@test.com","wrong","failure"}
 *
 * Usage in a test class:
 *   @DataProvider(name = "loginData")
 *   public Object[][] getLoginData() {
 *       return CSVDataReader.read("src/test/resources/testdata/login-data.csv");
 *   }
 *
 * WHY OpenCSV?
 *   Plain BufferedReader would break on values containing commas (e.g., addresses).
 *   OpenCSV handles quoted fields correctly: "123 Main St, Apt 4" stays as one value.
 */
public class CSVDataReader {

    /**
     * Read a CSV file and return its rows as Object[][] for TestNG @DataProvider.
     *
     * @param filePath Path to the CSV file, relative to the Maven project root.
     *                 Example: "src/test/resources/testdata/login-data.csv"
     * @return Object[][] where each row is one test invocation's parameter set.
     *         Returns empty Object[0][0] if the file is empty or unreadable.
     */
    public static Object[][] read(String filePath) {
        List<Object[]> data = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> allRows = csvReader.readAll();

            // Skip the first row (header row with column labels)
            // We start the loop at index 1, not 0
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);

                // Skip blank lines (empty row or all-whitespace cells)
                if (row == null || row.length == 0 || isRowBlank(row)) {
                    continue;
                }

                // Trim each cell to remove accidental spaces around values
                String[] trimmedRow = trimAll(row);
                
                // Inject password from environment variable if placeholder is found
                for (int j = 0; j < trimmedRow.length; j++) {
                    if ("YOUR_PASSWORD_HERE".equals(trimmedRow[j])) {
                        String envPass = System.getenv("FFC_PASSWORD");
                        if (envPass != null && !envPass.trim().isEmpty()) {
                            trimmedRow[j] = envPass.trim();
                        } else {
                            String sysPass = System.getProperty("FFC_PASSWORD");
                            if (sysPass != null && !sysPass.trim().isEmpty()) {
                                trimmedRow[j] = sysPass.trim();
                            }
                        }
                    }
                }
                
                data.add(trimmedRow);
            }

            System.out.println("[CSVDataReader] Read " + data.size() + " data rows from: " + filePath);

        } catch (IOException e) {
            // File not found or unreadable — fail with a clear message
            throw new RuntimeException(
                "[CSVDataReader] ERROR: Cannot read file '" + filePath + "'. " +
                "Make sure the file exists and the path is correct.\n" +
                "Error: " + e.getMessage(), e
            );
        } catch (CsvException e) {
            throw new RuntimeException(
                "[CSVDataReader] ERROR: CSV parsing failed for '" + filePath + "': " + e.getMessage(), e
            );
        }

        // Convert List<Object[]> → Object[][] as required by TestNG
        return data.toArray(new Object[0][]);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /** Returns true if all cells in the row are null or blank */
    private static boolean isRowBlank(String[] row) {
        for (String cell : row) {
            if (cell != null && !cell.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** Trims whitespace from every cell in a row */
    private static String[] trimAll(String[] row) {
        String[] trimmed = new String[row.length];
        for (int i = 0; i < row.length; i++) {
            trimmed[i] = (row[i] != null) ? row[i].trim() : "";
        }
        return trimmed;
    }
}
