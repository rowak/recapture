package io.github.rowak.recapture;

import static org.junit.Assert.assertTrue;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = g.getScreenDevices();

		for (int i = 0; i < devices.length; i++) {
		    System.out.println("Width:" + devices[i].getDisplayMode().getWidth());
		    System.out.println("Height:" + devices[i].getDisplayMode().getHeight());
		}
    }
}
