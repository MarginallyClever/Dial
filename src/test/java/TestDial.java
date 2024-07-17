import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DialTest {
    @Test
    void testInitialValue() {
        Dial dial = new Dial();
        assertEquals(0, dial.getValue(), "Dial should initialize at 0");
    }

    @Test
    void testMouseClickAndDrag() {
        Dial dial = new Dial();

        // Simulate mouse click and drag to increase the dial's value
        // dial.mouseClickAndDrag(simulatedValue);
        // assertEquals(expectedValue, dial.getValue(), "Dial value should change on mouse click and drag");
    }

}