package net.seninp.jmotif.direct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestLOOCVStackEntry {

  @Test
  public void testLOOCVStackEntry() {
    LOOCVStackEntry entry1 = new LOOCVStackEntry("first", new double[] { 0.0, 1.0, 2.0 }, 11);

    assertTrue("first".equalsIgnoreCase(entry1.getKey()));
    assertEquals(1.0, entry1.getValue()[1], 0.01);
    assertEquals(11, entry1.getIndex());

    entry1.setKey("second");
    entry1.setValue(new double[] { -2.0, -1.0, 0.0 });
    entry1.setIndex(-11);

    assertTrue("second".equalsIgnoreCase(entry1.getKey()));
    assertEquals(-1.0, entry1.getValue()[1], 0.01);
    assertEquals(-11, entry1.getIndex());
  }

}
