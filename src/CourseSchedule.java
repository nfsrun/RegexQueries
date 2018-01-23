// Name         : CourseSchedule.java
// Author       : Fatma Serce
// Version      : 1.00
// Modified     : Kevin Tran
// Description  : CourseSchedule class will ask users a couple of questions in regards to the Bellevue College schedule
// and process the Bellevue College website to output the list of available departments for the quarter. Then, it will
// ask two more questions in regards to a specific class and that class' available time schedules would appear. Regex
// (and two instances of String's substring) were used in this project.
// Note: I have tested ENGL 072 and such a class will not work as a combination class!! However, the other class
// connected to it, ENGL 092, should work!! Also, some classes that have an ampersand in the title would display as
// &amp;.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseSchedule {

    //parse method
    public static void parse() throws IOException {

        //A scanner is used and a series of questions were called in order to create the correct URL and PHP request
        Scanner s = new Scanner(System.in);
        System.out.print("Enter Quarter: ");
        String q = s.next();
        String total = q;
        System.out.print("Enter Year: ");
        int y = s.nextInt();
        total += y + "?letter=";
        System.out.print("Enter the initial for the program: ");
        total += s.next();
        System.out.println();
        URL u = new URL("https://www.bellevuecollege.edu/classes/" + total);

        //We do not use Scanner when reading from the  website
        BufferedReader b = new BufferedReader(new InputStreamReader(u.openStream()));

        //variables were created to keep track of various strings and delimiters.
        String input = "";
        String text = "";
        boolean record = false;
        Pattern p = Pattern.compile(">(.*)<");
        Pattern code = Pattern.compile("(\\((.*[\\w])\\))|(\\(.*&).*(\\))");
        Pattern initial = Pattern.compile("<li class=\"subject-name\">");
        System.out.println("Programs: ");

        //this outputs the various available departments for that quarter/year in Bellevue College
        while ((input = b.readLine()) != null) {
            if (record == false) {
                record = initial.matcher(input).find();
            } else {
                if (!input.matches("</ul>")) {
                    Matcher m = p.matcher(input);
                    Matcher o = code.matcher(input);
                    while (m.find() && o.find()) {

                        //
                        if(o.group(1)==null){
                            System.out.println(m.group(1) + " " + o.group(3)  + o.group(4));
                        }else{
                            System.out.println(m.group(1) + " " + o.group(1));
                        }
                    }
                    text += (input + "\n");
                } else {
                    break;
                }
            }
        }

        //Ask the last set of questions to get the correct class name.
        System.out.println();
        System.out.print("Enter the program's name: ");
        s.nextLine();
        String dept = s.nextLine();
        System.out.print("Enter the course ID: ");
        String course = s.nextLine();
        System.out.println();
        System.out.println(dept + " Courses in " + q + " " + y);
        System.out.println("==================================");

        //reloads the HTML to the correct url address if else part of this code uses substring to get rid of the
        //unnecessary ampersand and/or space and number in the class code because the website URL for the department
        //does not use such words in the URL
        record = false;
        b.close();
        if(course.contains("&")){
            u = new URL("https://www.bellevuecollege.edu/classes/" + q  + y + "/" +
                    course.substring(0, course.indexOf('&')));
        }else{
            u = new URL("https://www.bellevuecollege.edu/classes/" + q  + y + "/" +
                    course.substring(0, course.indexOf(' ')));
        }
        b = new BufferedReader(new InputStreamReader(u.openStream()));
        initial = Pattern.compile("\\s*<span class=\"courseID\">" + course + "</span.*");
        text="";

        //while loop would go through and capture a specific portion of the department website to retrieve all classes
        //with the correct class code in it
        while ((input = b.readLine()) != null) {
            if (record == false) {
                record = initial.matcher(input).find();
            } else {

                //delimitter used when looking at combined classes
                boolean stop = false;
                while ((input = b.readLine()) != null) {

                    //complex if and else if represents three cases the else if to stop adding text to the general body
                    //of text to be checked. The first case is if we meet a course id that is not what the user wants.
                    //If this happens, then we want to turn on our delimiter to prevent text from getting added. The
                    //second case is if there was a history of being within the wrong course id but the next course id
                    //in the same combined class matches the course id of classes we are trying to get. In this case, we
                    //turn off our delimiter again to continue adding text the next lines forward until further notice.
                    //The last case is to allow such not null text to be added if it is not delimited by our boolean,
                    //this literally means that the text is basically within acceptable range right now.
                    if (Pattern.compile("\\s*<span class=\"courseID\">.*").matcher(input).find() &&
                            !initial.matcher(input).find()) {
                        stop = true;
                    }else if(initial.matcher(input).find() && stop) {
                        stop = false;
                    }else if(input != null && !stop) {
                        text += input += "\n";
                    }
                }

                //instead of break, this boolean will be turned back to false to check if any classes exist in the page
                //with the class code (e.g. ENGL& 101 has numerous types that can be considered multiple credit with
                //another ENGL& class)
                record=false;
            }
        }

        //A new set of regex delimiters to keep track of specific portions of the HTML code with its respective
        //variables
        Pattern pc = Pattern.compile(".*Item number.*>(.*)<");
        Pattern pi = Pattern.compile("[\\s]*<a href=\"https:\\/\\/www.bellevuecollege.edu\\/directory.*>(.*)<");
        Pattern pd = Pattern.compile("(<span class=\"days (online)\">)|(.*<span class=\"days\">\\n(.*=\"(.*)\".*[.\\s]*)*)");
        Matcher processingClass = pc.matcher(text);
        Matcher processingInstructor = pi.matcher(text);
        Matcher processingDays = pd.matcher(text);
        processingDays.find();

        //while loop goes through each valid code and pastes in the information on the console
        while(processingClass.find()) {
            System.out.println("Code: " + course);
            System.out.println("Item#: " + processingClass.group(1));
            processingInstructor.find();
            System.out.println("Instructor: " + processingInstructor.group(1));
            Matcher specificsEnd = Pattern.compile(".*<li class=\"bookstoreinfo\">").matcher(text);

            //There are five groups, group 2 when no null indicates if online, group 5 when not null indicates a
            //specific date.
            if (processingDays.group(2)!=null) {
                System.out.println("Days: Online");
                processingDays.find();
            } else {
                specificsEnd.find(processingDays.start());
                while(specificsEnd.start()>=processingDays.start()){
                    System.out.print("Days: ");
                    System.out.println(processingDays.group(5));
                    if(!processingDays.find())
                        break;
                }
            }
            System.out.println("==================================");
        }
    }

    //main method launcher
    public static void main(String[] args) throws IOException {
        parse();
    }
}