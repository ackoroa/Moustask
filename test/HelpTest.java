import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HelpTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
	System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
	System.setOut(null);
    }

    @Test
    public void testHelp() {
	Help help = new Help();

	assertFalse(help == null);

	help.execute();

	assertTrue(outContent.toString().length() > 0);
    }

}
