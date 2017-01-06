package net.seninp.jmotif.direct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestValuePointColored {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testPoint() {
    ValuePointColored p1 = ValuePointColored.at(Point.at(10.0), 1.0, true);
    ValuePointColored p2 = ValuePointColored.at(Point.at(10.0), 1.0, true);

    ValuePointColored p3 = ValuePointColored.at(Point.at(13.0), 1.0, true);
    ValuePointColored p4 = ValuePointColored.at(Point.at(10.0), 2.0, false);

    assertTrue(p1.equals(p2));
    assertEquals(p1.hashCode(), p2.hashCode());

    assertFalse(p1.equals(p3));
    assertNotEquals(p3.hashCode(), p2.hashCode());

    assertFalse(p1.equals(p4));
    assertNotEquals(p4.hashCode(), p2.hashCode());

    assertTrue(p1.toString().contains("1.0@"));

    assertEquals(1, p1.getValue(), 0.000001);
    assertEquals(Point.at(10.0), p1.getPoint());

    assertTrue(p1.getBest());
    assertFalse(p4.getBest());

    assertTrue(p4.compareTo(ValuePoint.at(Point.at(13.0), 1.0)) > 0);
    assertTrue(p3.compareTo(ValuePoint.at(Point.at(10.0), 2.0)) < 0);
    assertTrue(ValuePointColored.at(Point.at(19.0), 2.0, true)
        .compareTo(ValuePoint.at(Point.at(10.0), 2.0)) == 0);

  }

}
