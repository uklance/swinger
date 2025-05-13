package com.swinger;

import com.swinger.model.ComponentResources;
import com.swinger.test.Panel1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class SwingerTest {
    private Swinger swinger;

    @BeforeEach
    public void beforeEach() throws Exception {
        swinger = new TestSwingerBuilder().build();
    }

    @Test @Disabled
    public void testPanel1() throws Exception {
        ComponentResources panel1Resources = swinger.createComponent(Panel1.class);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);

        frame.getContentPane().add(panel1Resources.getComponent());
        //frame.pack();
        frame.setVisible(true);

        Thread.sleep(20_000);
    }
}