package loansystem;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Custom TableBuilder designed to create a Table of Data based on fields of a class
 * Designed for this project, UOL Coursework
 * @param <T> Class to create the table from
 */
public class TableBuilder<T> implements Serializable {


    /**
     * @serialField type of class to use
     */
    private final Class<T> clazz;
    /**
     * @serialField Amount of spacing to use
     */
    private int spacing;
    /**
     * @serialField Delimiter to put between spaces
     */
    private String delimiter;
    /**
     * @serialField l(eft) to right or (r)ight to left
     */
    private boolean backwards;
    /**
     * @serialField objects of type T to be used
     */
    private T[] objects;
    /**
     * @serialField string of a field to be sorted by
     */
    private String sortByField;
    /**
     * @serialField string of the creation date of the table
     */
    private String creationDate;

    /**
     * Constructor for creating a TableBuilder
     *
     * @param clazz The type to be used for the generic class
     */
    public TableBuilder(Class<T> clazz) {
        this.clazz = clazz;
        this.backwards = false;
        this.sortByField = null;
        this.spacing = 2;
        this.delimiter = "";
        this.setCreationDate();
    }

    /**
     * Set a creation date
     * @param localDateTime Use localdatetime for formatting
     */
    private void setCreationDate(LocalDateTime localDateTime) {
        this.creationDate = getCreationDate(localDateTime);
    }

    /**
     * Set a creation date
     * @param string Uses a pre-formatted localdatetime
     */
    private void setCreationDate(String string) {
        this.creationDate = string;
    }

    /**
     * Sets the creation date to the current time using localdatetime
     */
    private void setCreationDate() {
        setCreationDate(LocalDateTime.now());
    }

    /**
     * Get the creation date as a string from localdate time
     * @param localDateTime The time to be used
     * @return String of the formatted time
     */
    public String getCreationDate(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return localDateTime.format(formatter);
    }

    /**
     * Optional method in the builder to choose if the content should be displayed from right to left
     *
     * @param backwards true/false - which side the data should start from
     * @return The instance of the object
     */
    public TableBuilder<T> backwards(boolean backwards) {
        this.backwards = backwards;
        return this;
    }

    /**
     * Optional method in the builder to insert a delimiter
     *
     * @param delimiter String to be inserted
     * @return The instance of the object
     */
    public TableBuilder<T> delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * Optional method in the builder to decide the spacing between headings
     *
     * @param spacing Amount of spaces between headings
     * @return The instance of the object
     */
    public TableBuilder<T> spacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    /**
     * Method used to set the data objects into the tablebuilder
     *
     * @param objects The data objects to be used by the tablebuilder
     * @return The instance of the object
     */
    public TableBuilder<T> objects(T[] objects) {
        this.objects = objects;
        return this;
    }

    /**
     * Optional method in the builder to decide how the table should be sorted
     *
     * @param fieldName The field name to be used as the comparator for sorting
     * @return The instance of the object
     */
    public TableBuilder<T> sortBy(String fieldName) {
        this.sortByField = fieldName;
        return this;
    }

    /**
     * Method to be called to tabularise the fields.
     * Prints the objects attributes in the structure of a table
     */
    public void tabularise() {
        if (isEmptyData()) return;

        sortData();

        Field[] fields = prepareFields();
        List<Field> filteredFields = Arrays.stream(fields).toList();
        int[] maxWidths = calculateMaxWidths(filteredFields);

        String sorted = this.sortByField == null ? "" : "(Sorted by: " + this.sortByField + ")";
        System.out.println("\nData for [" + FileUtils.getPrettyDateFromDateString(this.creationDate) + "] table " + sorted);
        printHeaders(filteredFields, maxWidths);
        printDataRows(filteredFields, maxWidths);
    }

    /**
     * Check if the given array of objects is empty or completely null
     */
    private boolean isEmptyData() {
        if (this.objects == null || this.objects.length == 0) {
            System.out.println("No data available.");
            return true;
        }
        return false;
    }

    /**
     * Prepare all the fields of the class by setting them to be accessible
     */
    //ignoring transient fields in here
    private Field[] prepareFields() {
        Field[] fields = this.clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .toArray(Field[]::new);
    }

    /**
     * Get the list of field names excluding transient fields (allows exclusion of data)
     * @return List of strings of the field names
     */
    public List<String> getFieldNames() {
        Field[] fields = this.clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .map(Field::getName)
                .toList();
    }

    /**
     * Calculate the max widths of the fields.
     * Use math to find the size of strings
     *
     * @param fields A list of all the fields to be calculated
     */
    private int[] calculateMaxWidths(List<Field> fields) {
        int[] maxWidths = new int[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            maxWidths[i] = prettyNameFromFieldName(field.getName()).length();
            for (T object : this.objects) {
                if (object == null) continue;
                try {
                    Object value = field.get(object);
                    maxWidths[i] = Math.max(maxWidths[i], value.toString().length());
                } catch (Exception e) {
                    System.out.println("Error calculating width for " + field.getName() + ": " + e.getMessage());
                }
            }
        }
        return maxWidths;
    }

    /**
     * Print all the headers of the fields
     *
     * @param fields The fields to print the headers of
     * @param maxWidths The widths to be printed (use math to find how many spaces need to be added to field headers)
     */
    private void printHeaders(List<Field> fields, int[] maxWidths) {
        for (int i = 0; i < fields.size(); i++) {
            String header = prettyNameFromFieldName(fields.get(i).getName());
            System.out.print(header + " ".repeat(maxWidths[i] - header.length()) + placeDelimiterInString(" ".repeat(this.spacing), this.delimiter));
        }
        System.out.println();
    }

    /**
     * Print the data rows of the objects/values for each field
     *
     * @param fields The fields to get the data of
     * @param maxWidths The maximum widths of the values to be in the table
     */
    private void printDataRows(List<Field> fields, int[] maxWidths) {
        for (T object : this.objects) {
            if (object == null) continue;
            for (int i = 0; i < fields.size(); i++) {
                try {
                    Field field = fields.get(i);
                    Object value = field.get(object);
                    String valueString = value.toString();
                    String toPrint = this.backwards ? " ".repeat(maxWidths[i] - valueString.length()) + valueString : valueString + " ".repeat(maxWidths[i] - valueString.length());
                    System.out.print(toPrint + placeDelimiterInString(" ".repeat(this.spacing), this.delimiter));
                } catch (Exception e) {
                    System.out.println("Error invoking " + fields.get(i).getName() + ": " + e.getMessage());
                }
            }
            System.out.println();
        }
    }

    /**
     * Sort data by a field name
     */
    private void sortData() {
        if (this.sortByField == null || this.sortByField.isEmpty()) return;

        Comparator<T> comparator = (o1, o2) -> {
            try {
                Field field = this.clazz.getDeclaredField(this.sortByField);
                field.setAccessible(true);
                Comparable value1 = (Comparable) field.get(o1);
                Comparable value2 = (Comparable) field.get(o2);
                return value1.compareTo(value2);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Error sorting by " + this.sortByField, e);
            }
        };
        Arrays.sort(this.objects, comparator);
    }


    /**
     * Method to make field names more readable (capitalise first letter)
     * @param fieldName The field name to make pretty
     * @return The new string of the field name
     */
    private String prettyNameFromFieldName(String fieldName) {
        fieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return fieldName;
    }

    /**
     *
     * @param string The string to place the delimiter in
     * @param delimiter The delimiter string to be added
     * @return A new string with the delimiter placed in
     */
    private String placeDelimiterInString(String string, String delimiter) {
        // Check if the string is null or empty
        if (string == null || string.isEmpty()) {
            return string;
        }

        // Find the middle index of the string
        int middleIndex = string.length() / 2;

        // Insert the delimiter at the middle index
        StringBuilder builder = new StringBuilder(string);
        builder.insert(middleIndex, delimiter);

        // Return the new string
        return builder.toString();
    }


    /* =======================================================================

     * IMPORTANT: THESE METHODS WILL NOT WORK UNLESS ALL DATA IS SERIALIZABLE.
     * USE THE @Serializable ANNOTATION ON ANY DATA THAT NEEDS TO BE SAVED

     ======================================================================= */


    /**
     * Method to save the current TableBuilder object
     * @throws IOException if an error occurs while saving
     */
    public void saveCurrentDataToFile() throws IOException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(FileUtils.createFile());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
        } catch (IOException exception) {
            throw new IOException("Unable to save data to file");
        }
    }

    /**
     * Loads a TableBuilder from a specified filename
     * @param fileName The name of the file to load the data from
     * @return loadDataFromFile() with the actual file
     * @throws IOException if an error occurs while loading data
     */
    public TableBuilder<T> loadDataFromFile(String fileName) throws IOException {

        if(!fileName.endsWith(".txt")) {
            fileName = fileName + ".txt";
        }
        return loadDataFromFile(new File(FileUtils.getDataFolder(), fileName));

    }

    /**
     *
     * @param file The file to load the data from
     * @return The new TableBuilder instance
     * @throws IOException if an error occurs while loading data
     */
    public TableBuilder<T> loadDataFromFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        try {
            TableBuilder<T> tableBuilder = (TableBuilder<T>) objectInputStream.readObject();
            objectInputStream.close();
            tableBuilder.setCreationDate(FileUtils.getDateFromFile(file));
            return tableBuilder;
        } catch (Exception e) {
            System.out.println("except: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw new IOException("File not found");
        }

    }


    /**
     * Constructor for creating a TableBuilder
     *
     * @param clazz The type to be used for the generic class
     * @param spacing The amount of spaces to use between table headers and data
     */
    /*
    public TableBuilder(Class<T> clazz, int spacing) {
        this(clazz, spacing, "");
    }

     */

    /**
     * Constructor for creating a TableBuilder
     *
     * @param clazz The type to be used for the generic class
     * @param spacing The amount of spaces to use between table headers and data
     * @param delimiter The delimiter to be used to separate headers & data
     */

    /*
    public TableBuilder(Class<T> clazz, int spacing, String delimiter) {
        this.clazz = clazz;
        this.spacing = spacing;
        this.delimiter = delimiter;
    }
     */
}
