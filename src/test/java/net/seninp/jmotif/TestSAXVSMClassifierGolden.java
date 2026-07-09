package net.seninp.jmotif;

import static org.junit.Assert.assertEquals;
import java.util.Map;
import org.junit.Test;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.text.Params;
import net.seninp.util.UCRUtils;

public class TestSAXVSMClassifierGolden {

  private static final double TOL = 1e-12;

  @Test
  public void testGunPointDocumentedParams() throws Exception {
    Map<String, java.util.List<double[]>> train = UCRUtils
        .readUCRData("src/resources/data/Gun_Point/Gun_Point_TRAIN");
    Map<String, java.util.List<double[]>> test = UCRUtils
        .readUCRData("src/resources/data/Gun_Point/Gun_Point_TEST");
    Params params = new Params(33, 17, 15, 0.01, NumerosityReductionStrategy.EXACT);
    SAXVSMEvaluator.Result result = SAXVSMEvaluator.evaluate(train, test, params);
    assertEquals(148, result.getCorrect());
    assertEquals(150, result.getTotal());
    assertEquals(0.9866666666666666, result.getAccuracy(), TOL);
    assertEquals(0.013333333333333334, result.getError(), TOL);
  }

  @Test
  public void testCBFDocumentedParams() throws Exception {
    Map<String, java.util.List<double[]>> train = UCRUtils.readUCRData("src/resources/data/cbf/CBF_TRAIN");
    Map<String, java.util.List<double[]>> test = UCRUtils.readUCRData("src/resources/data/cbf/CBF_TEST");
    Params params = new Params(60, 8, 6, 0.01, NumerosityReductionStrategy.EXACT);
    SAXVSMEvaluator.Result result = SAXVSMEvaluator.evaluate(train, test, params);
    assertEquals(900, result.getCorrect());
    assertEquals(900, result.getTotal());
    assertEquals(1.0, result.getAccuracy(), TOL);
    assertEquals(0.0, result.getError(), TOL);
  }
}
