package loansystem;

import java.io.Serializable;

/**
 * The class that you want to be creating the table for must:
 * Implement Serializable
 * Contain only accessible fields
 */
public class ExampleClass implements Serializable {

    private final String exampleId;
    private final int exampleNum;
    private final double exampleDouble;

    public ExampleClass(String id, int num, double dub) {
        this.exampleId = id;
        this.exampleNum = num;
        this.exampleDouble = dub;
    }

}
