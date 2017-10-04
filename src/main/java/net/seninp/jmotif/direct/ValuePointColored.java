package net.seninp.jmotif.direct;

/**
 * 
 * The direct code was taken from JCOOL (Java COntinuous Optimization Library), and altered for
 * SAX-VSM needs.
 * 
 * See <a href="https://github.com/dhonza/JCOOL/wiki">https://github.com/dhonza/JCOOL/wiki</a>
 * 
 * Created by IntelliJ IDEA. User: Carnuss Date: 3.5.2010 Time: 12:24:44 An immutable point wrapper
 * to represent the value of a function at a specified point, with variable best. The immutability
 * makes this class threadsafe.
 * 
 */
public class ValuePointColored implements Comparable<ValuePoint> {
  private final Point point;
  private final double value;
  private boolean best;

  /**
   * Constructor.
   * 
   * @param point the point coordinates.
   * @param value the payload.
   * @param best is it the best or not.
   */
  private ValuePointColored(Point point, double value, boolean best) {
    this.point = point;
    this.value = value;
    this.best = best;
  }

  private static final ValuePointColored DEFAULT = new ValuePointColored(Point.getDefault(),
      Double.NaN, false);

  /**
   * Constructor.
   * 
   * @return a new instance.
   */
  public static ValuePointColored getDefault() {
    return DEFAULT;
  }

  /**
   * Constructor.
   * 
   * @param p at coordinates.
   * @param value the payload.
   * @param best is it the best.
   * @return the configured point instance.
   */
  public static ValuePointColored at(Point p, double value, boolean best) {
    return new ValuePointColored(p, value, best);
  }

  /**
   * Getter.
   * 
   * @return the value.
   */
  public double getValue() {
    return value;
  }

  /**
   * Getter.
   * 
   * @return the point.
   */
  public Point getPoint() {
    return point;
  }

  /**
   * Getter.
   * 
   * @return the color.
   */
  public boolean getBest() {
    return best;
  }

  public void setBest(boolean best) {
    this.best = best;
  }

  @Override
  public String toString() {
    return value + "@" + point;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ValuePointColored other = (ValuePointColored) obj;
    if (this.point != other.point && (this.point == null || !this.point.equals(other.point))) {
      return false;
    }
    if (this.value != other.value) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 17 * hash + (this.point != null ? this.point.hashCode() : 0);
    hash = 17 * hash + (int) (Double.doubleToLongBits(this.value)
        ^ (Double.doubleToLongBits(this.value) >>> 32));
    return hash;
  }

  public int compareTo(ValuePoint o) {
    // return (int) (o.getValue() - this.getValue());

    if (this.getValue() > o.getValue())
      return 1;

    if (this.getValue() < o.getValue())
      return -1;

    return 0;
  }
}
