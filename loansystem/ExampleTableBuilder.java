package loansystem;

import java.io.IOException;

public class ExampleTableBuilder {

    public static void main(String[] args) {

        // Create an array of the Object you want to tabularise
        ExampleClass[] exampleClasses = new ExampleClass[]{
                new ExampleClass("lol", 5, 2D),
                new ExampleClass("another", 1, 4D),
                new ExampleClass("dasa", 3, 22D),
                new ExampleClass("kiki", 131, 7D)
        };

        // Create an instance of a new tablebuilder.
        // Use <YourObject> as the type parameter inside TableBuilder<T>.
        // Use the class reference in the parameter for instanciating the tablebuilder
        // e.g new TableBuilder<>(YourObject.class)
        TableBuilder<ExampleClass> tableBuilder = new TableBuilder<>(ExampleClass.class);

        // Use the TableBuilder to edit the table to your liking
        // add specific spacing with .spacing(amount)
        // add a delimiter with .delimiter(string)

        // IMPORTANT: you must add the objects to tabularise with .objects(array)
        // e.g we set an array of the object earlier, so we just use that
        // e.g .objects(yourObjectClasses)
        // finally use .tabularise() to print the data
        tableBuilder.spacing(4).delimiter("|").objects(exampleClasses).tabularise();

        // We can also save a table builder to edit it multiple times
        TableBuilder<ExampleClass> multipleUseTableBuilder = new TableBuilder<>(ExampleClass.class);

        // Instead of using .tabularise(), we just update the object with the new spacing, delimiter and objects.
        multipleUseTableBuilder = multipleUseTableBuilder.spacing(4).delimiter("|").objects(exampleClasses);

        // Now we can use our updated tablebuilder to sort, display or save the data without having to setup spacing, delimiter and objects again
        multipleUseTableBuilder.sortBy("exampleId");
        multipleUseTableBuilder.tabularise();
        multipleUseTableBuilder.sortBy("exampleNum");
        multipleUseTableBuilder.tabularise();
        multipleUseTableBuilder.sortBy("exampleDouble");
        multipleUseTableBuilder.tabularise();

        try {
            multipleUseTableBuilder.saveCurrentDataToFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* We are left with the following output:

        Data for [21/02/2024 05:04 AM] table
        ExampleId  |  ExampleNum  |  ExampleDouble  |
        lol        |  5           |  2.0            |
        another    |  1           |  4.0            |
        dasa       |  3           |  22.0           |
        kiki       |  131         |  7.0            |

        Data for [21/02/2024 05:04 AM] table (Sorted by: exampleId)
        ExampleId  |  ExampleNum  |  ExampleDouble  |
        another    |  1           |  4.0            |
        dasa       |  3           |  22.0           |
        kiki       |  131         |  7.0            |
        lol        |  5           |  2.0            |

        Data for [21/02/2024 05:04 AM] table (Sorted by: exampleNum)
        ExampleId  |  ExampleNum  |  ExampleDouble  |
        another    |  1           |  4.0            |
        dasa       |  3           |  22.0           |
        lol        |  5           |  2.0            |
        kiki       |  131         |  7.0            |

        Data for [21/02/2024 05:04 AM] table (Sorted by: exampleDouble)
        ExampleId  |  ExampleNum  |  ExampleDouble  |
        lol        |  5           |  2.0            |
        another    |  1           |  4.0            |
        kiki       |  131         |  7.0            |
        dasa       |  3           |  22.0           |

        */
    }
}
