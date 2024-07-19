package com.marginallyclever;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "headless environment")
public class TestDial {
    private FrameFixture window;
    Dial dial;

    @Test
    void testInitialValue() {
        Dial dial2 = new Dial();
        assertEquals(0, dial2.getValue(), "com.marginallyclever.Dial should initialize at 0");
    }

    @BeforeEach
    public void setUp() {
        Robot robot = BasicRobot.robotWithNewAwtHierarchy();
        dial = new Dial();
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame();
            f.add(dial);
            f.pack();
            return f;
        });
        window = new FrameFixture(robot, frame);
        window.show(); // shows the frame to test
    }

    @AfterEach
    protected void tearDown() {
        if(window!=null) window.cleanUp();
    }

    @Test
    public void testMouseClickAndDrag() {
        int [] steps = new int[1];

        dial.addActionListener(e -> {
            if(e.getActionCommand().equals("turn")) {
                steps[0]++;
            }
        });

        // Simulate mouse click and drag on the com.marginallyclever.Dial
        // Note: You'll need to adjust the drag coordinates based on the com.marginallyclever.Dial's size and position
        window.panel().robot().pressMouse(window.panel().target(), new Point(25, 25));
        window.panel().robot().moveMouse(new Point(50, 50));
        window.panel().robot().releaseMouseButtons();

        // Assert the com.marginallyclever.Dial's value changed as expected
        assert(0!=dial.getValue());
        assert(0!=steps[0]);
        // Note: Implement the logic to retrieve and assert the com.marginallyclever.Dial's value after interaction
    }
}