package calc;

public class MainCalculator {

    public static void main(String[] args) {
        CalculatorInt calculatorInt = new Calculator(2, 2);
        System.out.println("The sum is: " + calculatorInt.sum());
    }
}
