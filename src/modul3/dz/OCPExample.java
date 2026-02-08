package modul3.dz;
public class OCPExample {
    public static void main(String[] args) {
        Employee emp = new PermanentEmployee("Ali", 1000);
        SalaryCalculator calculator = new SalaryCalculator();
        System.out.println("Salary: " + calculator.calculate(emp));
    }
}

abstract class Employee {
    String name;
    double baseSalary;

    public Employee(String name, double baseSalary) {
        this.name = name;
        this.baseSalary = baseSalary;
    }

    public abstract double getSalary();
}

class PermanentEmployee extends Employee {
    public PermanentEmployee(String name, double baseSalary) {
        super(name, baseSalary);
    }

    public double getSalary() {
        return baseSalary * 1.2;
    }
}

class ContractEmployee extends Employee {
    public ContractEmployee(String name, double baseSalary) {
        super(name, baseSalary);
    }

    public double getSalary() {
        return baseSalary * 1.1;
    }
}

class InternEmployee extends Employee {
    public InternEmployee(String name, double baseSalary) {
        super(name, baseSalary);
    }

    public double getSalary() {
        return baseSalary * 0.8;
    }
}

class SalaryCalculator {
    public double calculate(Employee employee) {
        return employee.getSalary();
    }
}

