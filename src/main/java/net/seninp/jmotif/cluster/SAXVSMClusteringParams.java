package net.seninp.jmotif.cluster;

import java.util.ArrayList;
import java.util.List;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import com.beust.jcommander.Parameter;

/**
 * Command-line parameters for {@link SAXVSMClusteringCLI}.
 */
public class SAXVSMClusteringParams {

  @Parameter
  public List<String> parameters = new ArrayList<String>();

  @Parameter(names = { "--train_data", "-train" }, required = true,
      description = "UCR-format training file (one series per line, class label first)")
  public static String TRAIN_FILE;

  @Parameter(names = { "--clusters", "-k" }, description = "Number of clusters")
  public static int K = 3;

  @Parameter(names = { "--method", "-m" }, description = "Clustering method: kmeans or hierarchical")
  public static String METHOD = "kmeans";

  @Parameter(names = { "--linkage" }, description = "Hierarchical linkage: single or complete")
  public static String LINKAGE = "single";

  @Parameter(names = { "--init" }, description = "k-means seeding: random or furthest_first")
  public static String INIT = "furthest_first";

  @Parameter(names = { "--seed" }, description = "Random seed for k-means initialization")
  public static long SEED = 0L;

  @Parameter(names = { "--newick_out" }, description = "Optional file for hierarchical Newick output")
  public static String NEWICK_OUT;

  @Parameter(names = { "--window_size", "-w" }, description = "SAX sliding window size")
  public static int SAX_WINDOW_SIZE = 30;

  @Parameter(names = { "--word_size", "-p" }, description = "SAX PAA word size")
  public static int SAX_PAA_SIZE = 4;

  @Parameter(names = { "--alphabet_size", "-a" }, description = "SAX alphabet size")
  public static int SAX_ALPHABET_SIZE = 3;

  @Parameter(names = "--strategy", description = "SAX numerosity reduction strategy")
  public static NumerosityReductionStrategy SAX_NR_STRATEGY = NumerosityReductionStrategy.EXACT;

  @Parameter(names = "--threshold", description = "SAX normalization threshold")
  public static double SAX_NORM_THRESHOLD = 0.01;

}
