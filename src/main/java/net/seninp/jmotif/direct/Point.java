package net.seninp.jmotif.direct;

import java.util.Arrays;

/**
 * 
 * The direct code was taken from JCOOL (Java COntinuous Optimization Library), and altered for
 * SAX-VSM needs. Below is the original DOC.
 * 
 * @see <a href="https://github.com/dhonza/JCOOL/wiki">https://github.com/dhonza/JCOOL/wiki</a>
 * 
 * Wrapper class around a point (or input vector) array. This class is immutable making it
 * threadsafe. It is not extendible because it has no public constructor. Creation is handled by a
 * static factory method an ideal point for caching or hashcode precomputation.
 * 
 * Added value:<ul>
 * <li>more abstraction
 * <li>default inplementations of equals + hashcode
 * <li>can be used in collections
 * <li>caching
 * </ul>
 * 
 * @author ytoh
 */
public class Point {

  // internal coordinate representation
  private final double[] array;

  /** */
  private String toString;

  /** */
  private static final Point DEFAULT = new Point(0);

  /**
   * Creates and initializes an instance of <code>Point</code> using the specified values.
   * 
   * @param array to use to initialize the created instance
   */
  private Point(double[] array) {
    this(array.length);
    System.arraycopy(array, 0, this.array, 0, this.array.length);

    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for (int i = 0; i < array.length; i++) {
      builder.append(array[i]);
      if (i < array.length - 1) {
        builder.append(",");
      }
    }

    this.toString = builder.append("]").toString();
  }

  /**
   * Creates a default instance of <code>Point</code>.
   * 
   * @param dimension representing the length of the <code>Point</code>
   */
  private Point(int dimension) {
    this.array = new double[dimension];
  }

  /**
   * Factory method for creating <code>Point</code> instances out of arrays of values. (Factory
   * method pattern)
   * 
   * @param array representing a point
   * @return a reference to an instance of <code>Point</code>
   */
  public static final Point at(double... array) {
    return new Point(array);
  }

  public static final Point random(int dimension) {
    return Point.random(dimension, -Double.MAX_VALUE, Double.MAX_VALUE);
  }

  public static final Point random(int dimension, double min, double max) {
    double[] array = new double[dimension];

    for (int i = 0; i < array.length; i++)
      array[i] = Math.random() * (max - min) + min;

    return new Point(array);
  }

  /**
   * Constructor.
   * 
   * @return a new instance.
   */
  public static final Point getDefault() {
    return DEFAULT;
  }

  /**
   * Returns the internal representation of this <code>Point</code> object. The returned value is a
   * copy of the internal immutable state.
   * 
   * @return internal state as an array
   */
  public double[] toArray() {
    double[] copy = new double[array.length];
    System.arraycopy(array, 0, copy, 0, copy.length);
    return copy;
  }

  @Override
  public String toString() {
    return toString;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(array);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Point other = (Point) obj;
    if (!Arrays.equals(array, other.array))
      return false;
    return true;
  }

  public int[] toIntArray() {
    int[] res = new int[array.length];
    for (int i = 0; i < array.length; i++) {
      res[i] = (int) Math.round(array[i]);
    }
    return res;
  }

  public String toLogString() {
    return toString.replaceAll("\\[|\\]", "").replace(",", "\t");
  }
}