package net.seninp.jmotif;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.text.Params;
import net.seninp.jmotif.text.TextProcessor;
import net.seninp.jmotif.text.WordBag;

/**
 * Shared SAX-VSM train/test evaluation used by the CLI and golden tests.
 */
public final class SAXVSMEvaluator {

  private SAXVSMEvaluator() {
  }

  public static final class Result {
    private final double accuracy;
    private final double error;
    private final int correct;
    private final int total;

    public Result(double accuracy, double error, int correct, int total) {
      this.accuracy = accuracy;
      this.error = error;
      this.correct = correct;
      this.total = total;
    }

    public double getAccuracy() {
      return accuracy;
    }

    public double getError() {
      return error;
    }

    public int getCorrect() {
      return correct;
    }

    public int getTotal() {
      return total;
    }
  }

  public static Result evaluate(Map<String, List<double[]>> trainData,
      Map<String, List<double[]>> testData, Params params) throws SAXException {
    TextProcessor tp = new TextProcessor();
    List<WordBag> bags = tp.labeledSeries2WordBags(trainData, params);
    HashMap<String, HashMap<String, Double>> tfidf = tp.computeTFIDF(bags);

    int total = 0;
    int correct = 0;
    for (String label : tfidf.keySet()) {
      List<double[]> testSeries = testData.get(label);
      for (double[] series : testSeries) {
        correct += tp.classify(label, series, tfidf, params);
        total++;
      }
    }

    double accuracy = total == 0 ? 0.0d : (double) correct / (double) total;
    return new Result(accuracy, 1.0d - accuracy, correct, total);
  }
}
