package net.seninp.jmotif.direct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPoint {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testPoint() {
    Point p1 = Point.at(10.0);
    Point p2 = Point.at(10.0);

    Point p3 = Point.at(11.0);

    assertTrue(p1.equals(p2));
    assertEquals(p1.hashCode(), p2.hashCode());

    assertFalse(p3.equals(p2));
    assertNotEquals(p3.hashCode(), p2.hashCode());

    assertTrue(p1.toString().contains("10.0"));
    assertTrue(p1.toString().contains("]"));

    assertTrue(p1.toLogString().contains("10.0"));
    assertFalse(p1.toLogString().contains("]"));

    assertTrue(Arrays.equals(Point.at(10.49, 10.51).toIntArray(), new int[] { 10, 11 }));

    assertTrue(Point.getDefault().equals(Point.at()));

    Point p4 = Point.random(11);
    assertEquals(11, p4.toArray().length);

  }

}
