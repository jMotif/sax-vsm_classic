package net.seninp.jmotif;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.text.Params;
import net.seninp.util.StackTrace;
import net.seninp.util.UCRUtils;
import com.beust.jcommander.JCommander;

/**
 * This implements a classifier.
 * 
 * @author psenin
 * 
 */
public class SAXVSMClassifier {

  private static final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
  private static DecimalFormat fmt = new DecimalFormat("0.00###", otherSymbols);
  private static final Object CR = "\n";
  private static final String COMMA = ", ";

  private static Map<String, List<double[]>> trainData;
  private static Map<String, List<double[]>> testData;

  private static final Logger consoleLogger = LoggerFactory.getLogger(SAXVSMClassifier.class);

  public static void main(String[] args) throws SAXException {

    try {

      SAXVSMClassifierParams params = new SAXVSMClassifierParams();
      JCommander jct = new JCommander(params);

      if (0 == args.length) {
        jct.usage();
        System.exit(-10);
      }

      jct.parse(args);

      StringBuffer sb = new StringBuffer(1024);
      sb.append("SAX-VSM Classifier").append(CR);
      sb.append("parameters:").append(CR);

      sb.append("  train data:                  ").append(SAXVSMClassifierParams.TRAIN_FILE).append(CR);
      sb.append("  test data:                   ").append(SAXVSMClassifierParams.TEST_FILE).append(CR);
      sb.append("  SAX sliding window size:     ").append(SAXVSMClassifierParams.SAX_WINDOW_SIZE).append(CR);
      sb.append("  SAX PAA size:                ").append(SAXVSMClassifierParams.SAX_PAA_SIZE).append(CR);
      sb.append("  SAX alphabet size:           ").append(SAXVSMClassifierParams.SAX_ALPHABET_SIZE).append(CR);
      sb.append("  SAX numerosity reduction:    ").append(SAXVSMClassifierParams.SAX_NR_STRATEGY).append(CR);
      sb.append("  SAX normalization threshold: ").append(SAXVSMClassifierParams.SAX_NORM_THRESHOLD).append(CR);

      trainData = UCRUtils.readUCRData(SAXVSMClassifierParams.TRAIN_FILE);
      consoleLogger.info("trainData classes: " + trainData.size() + ", series length: "
          + trainData.entrySet().iterator().next().getValue().get(0).length);
      for (Entry<String, List<double[]>> e : trainData.entrySet()) {
        consoleLogger.info(" training class: " + e.getKey() + " series: " + e.getValue().size());
      }

      testData = UCRUtils.readUCRData(SAXVSMClassifierParams.TEST_FILE);
      consoleLogger.info("testData classes: " + testData.size() + ", series length: "
          + testData.entrySet().iterator().next().getValue().get(0).length);
      for (Entry<String, List<double[]>> e : testData.entrySet()) {
        consoleLogger.info(" test class: " + e.getKey() + " series: " + e.getValue().size());
      }

    }
    catch (Exception e) {
      System.err.println("There was an error...." + StackTrace.toString(e));
      System.exit(-10);
    }
    Params params = new Params(SAXVSMClassifierParams.SAX_WINDOW_SIZE,
        SAXVSMClassifierParams.SAX_PAA_SIZE, SAXVSMClassifierParams.SAX_ALPHABET_SIZE,
        SAXVSMClassifierParams.SAX_NORM_THRESHOLD, SAXVSMClassifierParams.SAX_NR_STRATEGY);
    SAXVSMEvaluator.Result result = SAXVSMEvaluator.evaluate(trainData, testData, params);
    System.out.println("classification results: "
        + toLogStr(params, result.getAccuracy(), result.getError()));
  }

  static SAXVSMEvaluator.Result evaluateLoadedData(Params params) throws SAXException {
    return SAXVSMEvaluator.evaluate(trainData, testData, params);
  }

  protected static String toLogStr(Params params, double accuracy, double error) {
    StringBuffer sb = new StringBuffer();
    sb.append("strategy ").append(params.getNrStartegy().toString()).append(COMMA);
    sb.append("window ").append(params.getWindowSize()).append(COMMA);
    sb.append("PAA ").append(params.getPaaSize()).append(COMMA);
    sb.append("alphabet ").append(params.getAlphabetSize()).append(COMMA);
    sb.append(" accuracy ").append(fmt.format(accuracy)).append(COMMA);
    sb.append(" error ").append(fmt.format(error));
    return sb.toString();
  }

}
