package net.seninp.jmotif.direct;

/**
 * A test function.
 * 
 * The direct code was taken from JCOOL (Java COntinuous Optimization Library), and altered for
 * SAX-VSM needs.
 * 
 * @see <a href="https://github.com/dhonza/JCOOL/wiki">https://github.com/dhonza/JCOOL/wiki</a>
 *
 */
public class GoldsteinPriceFunctionSampler {

  private static final double X1_START = -20.0;
  private static final double X1_END = 20.0;

  private static final double X2_START = -20.0;
  private static final double X2_END = 20.0;

  private static final double step = 0.5;

  private GoldsteinPriceFunctionSampler() {
  }

  public static void main(String[] args) {

    double x1 = X1_START;
    double x2 = X2_START;

    while ((x1 < X1_END) && (x2 < X2_END)) {

      @SuppressWarnings("unused")
      double value = GoldsteinPriceFunction.compute(x1, x2);

      // increment variable values
      //
      x1 = x1 + step;
      if ((x1 >= X1_END) && (x2 < X2_END)) {
        x1 = X1_START;
        x2 = x2 + step;
      }
    }

  }
}
