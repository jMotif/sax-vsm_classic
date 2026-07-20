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

  public static void main(String[] args) {

    int x1Steps = (int) ((X1_END - X1_START) / step) + 1;
    int x2Steps = (int) ((X2_END - X2_START) / step) + 1;

    for (int i2 = 0; i2 < x2Steps; i2++) {
      double x2 = X2_START + i2 * step;
      for (int i1 = 0; i1 < x1Steps; i1++) {
        double x1 = X1_START + i1 * step;

        @SuppressWarnings("unused")
        double value = GoldsteinPriceFunction.compute(x1, x2);
      }
    }

  }
}
