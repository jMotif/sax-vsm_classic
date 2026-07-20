package net.seninp.jmotif.cluster;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.text.Params;
import net.seninp.util.StackTrace;
import net.seninp.util.UCRUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.beust.jcommander.JCommander;

/**
 * Command-line entry point for unsupervised SAX-VSM clustering.
 *
 * <p>Mirrors {@link net.seninp.jmotif.SAXVSMClassifier}: read UCR train data, build per-series
 * tf·idf, cluster with k-means or hierarchical clustering, report label purity when class labels
 * are present.</p>
 */
public final class SAXVSMClusteringCLI {

  private static final DecimalFormat FMT =
      new DecimalFormat("0.00###", DecimalFormatSymbols.getInstance(Locale.US));
  private static final Logger LOGGER = LoggerFactory.getLogger(SAXVSMClusteringCLI.class);

  private SAXVSMClusteringCLI() {
  }

  public static void main(String[] args) {
    System.exit(run(args));
  }

  static int run(String[] args) {
    try {
      SAXVSMClusteringParams cli = new SAXVSMClusteringParams();
      JCommander jct = new JCommander(cli);
      if (args.length == 0) {
        jct.setProgramName("SAXVSMClusteringCLI");
        jct.usage();
        return -10;
      }
      jct.parse(args);
      Result result = execute();
      System.out.println(result.summaryLine);
      for (String line : result.clusterLines) {
        System.out.println(line);
      }
      if (result.newick != null) {
        System.out.println("newick: (" + result.newick + ")");
      }
      return 0;
    }
    catch (Exception e) {
      System.err.println("There was an error...." + StackTrace.toString(e));
      return -10;
    }
  }

  static Result execute() throws SAXException, IOException {
    Map<String, List<double[]>> train = UCRUtils.readUCRData(SAXVSMClusteringParams.TRAIN_FILE);
    LOGGER.info("trainData classes: " + train.size() + ", series length: "
        + train.entrySet().iterator().next().getValue().get(0).length);
    for (Entry<String, List<double[]>> e : train.entrySet()) {
      LOGGER.info(" training class: " + e.getKey() + " series: " + e.getValue().size());
    }

    Params params = new Params(SAXVSMClusteringParams.SAX_WINDOW_SIZE,
        SAXVSMClusteringParams.SAX_PAA_SIZE, SAXVSMClusteringParams.SAX_ALPHABET_SIZE,
        SAXVSMClusteringParams.SAX_NORM_THRESHOLD, SAXVSMClusteringParams.SAX_NR_STRATEGY);

    Map<String, Map<String, Double>> tfidf = SAXVSMClustering.seriesTfidf(train, params);
    Map<String, String> labels = SAXVSMClustering.seriesLabels(train);

    String method = SAXVSMClusteringParams.METHOD.trim().toLowerCase(Locale.US);
    int k = SAXVSMClusteringParams.K;
    ClusterAssignments assignments;
    String newick = null;

    if ("kmeans".equals(method) || "km".equals(method)) {
      KMeansInit init = parseInit(SAXVSMClusteringParams.INIT);
      assignments = SAXVSMClustering.kMeans(tfidf, k, init, SAXVSMClusteringParams.SEED);
    }
    else if ("hierarchical".equals(method) || "hc".equals(method)) {
      Linkage linkage = parseLinkage(SAXVSMClusteringParams.LINKAGE);
      Dendrogram tree = SAXVSMClustering.hierarchical(tfidf, linkage);
      assignments = tree.partition(k);
      newick = tree.toNewick();
      if (SAXVSMClusteringParams.NEWICK_OUT != null) {
        try (FileWriter fw = new FileWriter(SAXVSMClusteringParams.NEWICK_OUT)) {
          fw.write("(" + newick + ")\n");
        }
      }
    }
    else {
      throw new IllegalArgumentException(
          "Unknown method: " + SAXVSMClusteringParams.METHOD + " (use kmeans or hierarchical)");
    }

    double purity = assignments.labelPurity(labels);
    String summary = buildSummaryLine(params, method, k, purity);
    return new Result(summary, formatClusters(assignments, labels), newick);
  }

  private static KMeansInit parseInit(String value) {
    String v = value.trim().toLowerCase(Locale.US);
    if ("random".equals(v)) {
      return KMeansInit.RANDOM;
    }
    if ("furthest_first".equals(v) || "ff".equals(v)) {
      return KMeansInit.FURTHEST_FIRST;
    }
    throw new IllegalArgumentException(
        "Unknown init: " + value + " (use random or furthest_first)");
  }

  private static Linkage parseLinkage(String value) {
    String v = value.trim().toLowerCase(Locale.US);
    if ("single".equals(v)) {
      return Linkage.SINGLE;
    }
    if ("complete".equals(v)) {
      return Linkage.COMPLETE;
    }
    throw new IllegalArgumentException(
        "Unknown linkage: " + value + " (use single or complete)");
  }

  private static String buildSummaryLine(Params params, String method, int k, double purity) {
    StringBuilder sb = new StringBuilder();
    sb.append("clustering results: method ").append(method);
    sb.append(", strategy ").append(params.getNrStartegy());
    sb.append(", window ").append(params.getWindowSize());
    sb.append(", PAA ").append(params.getPaaSize());
    sb.append(", alphabet ").append(params.getAlphabetSize());
    sb.append(", k ").append(k);
    if ("kmeans".equals(method) || "km".equals(method)) {
      sb.append(", init ").append(SAXVSMClusteringParams.INIT);
      sb.append(", seed ").append(SAXVSMClusteringParams.SEED);
    }
    else {
      sb.append(", linkage ").append(SAXVSMClusteringParams.LINKAGE);
    }
    sb.append(", purity ").append(FMT.format(purity));
    return sb.toString();
  }

  private static String[] formatClusters(ClusterAssignments assignments,
      Map<String, String> labels) {
    String[] lines = new String[assignments.k()];
    int i = 0;
    for (Entry<Integer, List<String>> cluster : assignments.asMap().entrySet()) {
      Map<String, Integer> counts = new HashMap<String, Integer>();
      for (String id : cluster.getValue()) {
        String lab = labels.get(id);
        Integer n = counts.get(lab);
        counts.put(lab, n == null ? 1 : n + 1);
      }
      lines[i++] = "  cluster " + cluster.getKey() + " (n=" + cluster.getValue().size()
          + "): " + counts;
    }
    return lines;
  }

  static final class Result {
    final String summaryLine;
    final String[] clusterLines;
    final String newick;

    Result(String summaryLine, String[] clusterLines, String newick) {
      this.summaryLine = summaryLine;
      this.clusterLines = clusterLines;
      this.newick = newick;
    }
  }
}
