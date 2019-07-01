package calc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculatorTest {

    CalculatorInt calculator;

    @Before
    public void setUp() throws Exception {
        calculator = new Calculator(2, 3);
    }

    @Test
    public void testSum() {
        assertEquals(5, calculator.sum());
    }
}