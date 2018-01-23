// Name         : VariableAnalysis.java
// Author       : Fatma Serce
// Version      : 1.00
// Modified     : Kevin Tran
// Description  : VariableAnalysis class will process a java file and output all variables within it (including type,
// variable name, and value if applicable). Uses regex completely to process it.

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableAnalysis {

    //parse method
    public static void parse() throws FileNotFoundException {

        //File is defaulted to read from A.java. Can be changed.
        File file = new File("A.java");
        Scanner object = new Scanner(file);
        String input = "";
        String text = "";

        //parsing each line from the file into a string variable for processing.
        while(object.hasNextLine()){
            input = object.nextLine();
            text+=input+"\n";
        }

        //Pattern will match any important variable pre-declarations (non-inclusive) and inclusively get the type,
        //variable name, and variable value (if available) in each line if possible.
        Matcher m = Pattern.compile("\\s*(private |final |static |protected |)*(\\w*) (\\w*)( = '?(.*)'?)*;"
            ).matcher(text);
        while(m.find()){
            System.out.println("Type: " + m.group(2));
            System.out.println("Variable name: " + m.group(3));
            System.out.print("Value: ");

            //when getting value, if not inputted originally, will set the output as a null value.
            try{
                System.out.println(m.group(5));
            }catch(NullPointerException n){
                System.out.println("null");
            }
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        }
    }

    //main method launcher
    public static void main(String[] args) throws IOException {
        parse();
    }
}
