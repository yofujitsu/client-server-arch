package practice2;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {


    /**
     * @param equation String of the quadratic equation with necessarily put space between each its terms.
     *                 For example: 2x^2 + 4x + 3 = 0
     * @return List of three quotients - a, b and c.
     */
    public static List<Double> parseEquation(String equation) {
        //ax^2 + bx + c = 0
        String str = ("+" + equation).replaceAll("\\s", "");


        double a = getCoefficient(str, "([+-][0-9]*)x\\^2");
        double b = getCoefficient(str, "([+-][0-9]*)x(?!\\^)");
        double c = getCoefficient(str, "([+-][0-9]*)=");
        System.out.println("Values are a: " + a + "; b: " + b + "; c: " + c);


        return List.of(a, b, c);
    }


    private static double getCoefficient(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);


        String coefficient;
        double value = 0;
        while (match.find()) {
            coefficient = match.group(1);
            if (coefficient.equals("+") || coefficient.equals("-")){
                coefficient = coefficient.concat("1");
            }
            value = value + Double.parseDouble(coefficient);
        }


        return value;
    }
}
