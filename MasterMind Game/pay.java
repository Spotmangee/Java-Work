import java.util.Scanner;

/**
 * Created by Matthew on 06/03/2016.
 */
public abstract class pay extends Test {
        private String name;
        private double pay;

    public pay(String name, double salary)
        {
            this.name = name;
            this.pay = salary;


        }
        public double computePay()
        {
            return 0.0;
        }

    public String getName()
    {
        return name;
    }

    public double getPay()
    {
        return pay;
    }

        public String toString()
        {
            return "Salary of " + name + " with pay of " + pay + " per week.";
        }
}

  class Salary extends pay {
    private double salary; //Annual salary
    public Salary(String name, double salary) {
        super(name, salary);
        setSalary(salary);
    }

    public double getSalary()
    {
        return salary;
    }
    public void setSalary(double newSalary)
    {
        if(newSalary >= 0.0)
        {
            salary = newSalary;
        }
    }

    public double computePay()
    {
        System.out.println("Computing salary pay for " + getName());
        return getPay()/52;

    }
}

    class hourly_pay extends Salary {
                public hourly_pay(String name, double salary) {
            super(name, salary);
        }
    }

    class commission_pay extends Salary {
        public commission_pay(String name, double salary) {
            super(name, salary);
        }
    }

    class piece_pay extends Salary {
        public piece_pay(String name, double salary) {
            super(name, salary);
        }
    }