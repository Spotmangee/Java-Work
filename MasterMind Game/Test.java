/**
 * Created by Matthew on 06/03/2016.
 */
import java.util.*;

public class Test {
    public static double moneyPerHour = 1.00;
    public static void main(String[] args) {

        Salary s = new Salary("Manager", 500);
        System.out.println(s);

        hourly_pay h = new hourly_pay("Hourly worker: ", 100 * moneyPerHour);
        System.out.println(h);

        commission_pay c = new commission_pay("Commission Worker: ", 200 + 200);
        System.out.println(c);

        piece_pay p = new piece_pay("Piece Worker: ", 200 + 100);
        System.out.println(p);

    }
}

//don't print the method, print the extended class



/*
        private Scanner scan1 = new Scanner(System.in);
        private Scanner scan2 = new Scanner(System.in);
        private Scanner scan3 = new Scanner(System.in);
        System.out.println("Please input the number of hours worked by an hourly worker: ");

        input1 = scan1.nextInt();

        System.out.println("Please input an extra value for piece workers: ");

        input2 = scan2.nextInt();

        System.out.println("Please input an extra value for commission workers: ");

        input3 = scan3.nextInt();
 */