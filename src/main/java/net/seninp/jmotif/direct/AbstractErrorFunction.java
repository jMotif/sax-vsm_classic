package net.seninp.jmotif.direct;

/**
 * A template.
 * 
 * The direct code was taken from JCOOL (Java COntinuous Optimization Library), and altered for
 * SAX-VSM needs.
 * 
 * @see <a href="https://github.com/dhonza/JCOOL/wiki">https://github.com/dhonza/JCOOL/wiki</a>
 *
 */
public interface AbstractErrorFunction {

  public double valueAt(Point point);

}
