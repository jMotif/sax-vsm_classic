package net.seninp.jmotif.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;

public class TestSAXVSMClusteringCLI {

  private static final double TOL = 1e-12;

  @After
  public void resetParams() {
    SAXVSMClusteringParams.TRAIN_FILE = null;
    SAXVSMClusteringParams.K = 3;
    SAXVSMClusteringParams.METHOD = "kmeans";
    SAXVSMClusteringParams.LINKAGE = "single";
    SAXVSMClusteringParams.INIT = "furthest_first";
    SAXVSMClusteringParams.SEED = 0L;
    SAXVSMClusteringParams.NEWICK_OUT = null;
    SAXVSMClusteringParams.SAX_WINDOW_SIZE = 30;
    SAXVSMClusteringParams.SAX_PAA_SIZE = 4;
    SAXVSMClusteringParams.SAX_ALPHABET_SIZE = 3;
    SAXVSMClusteringParams.SAX_NR_STRATEGY = NumerosityReductionStrategy.EXACT;
    SAXVSMClusteringParams.SAX_NORM_THRESHOLD = 0.01;
  }

  @Test
  public void cbfKMeansGoldenSummary() throws Exception {
    SAXVSMClusteringParams.TRAIN_FILE = "src/resources/data/cbf/CBF_TRAIN";
    SAXVSMClusteringParams.K = 3;
    SAXVSMClusteringParams.METHOD = "kmeans";
    SAXVSMClusteringParams.INIT = "furthest_first";
    SAXVSMClusteringParams.SEED = 2L;
    SAXVSMClusteringParams.SAX_WINDOW_SIZE = 60;
    SAXVSMClusteringParams.SAX_PAA_SIZE = 8;
    SAXVSMClusteringParams.SAX_ALPHABET_SIZE = 6;

    SAXVSMClusteringCLI.Result result = SAXVSMClusteringCLI.execute();
    assertEquals(
        "clustering results: method kmeans, strategy EXACT, window 60, PAA 8, alphabet 6, k 3, init furthest_first, seed 2, purity 0.90",
        result.summaryLine);
    assertNull(result.newick);
    assertEquals(3, result.clusterLines.length);
  }

  @Test
  public void cbfHierarchicalGoldenSummary() throws Exception {
    SAXVSMClusteringParams.TRAIN_FILE = "src/resources/data/cbf/CBF_TRAIN";
    SAXVSMClusteringParams.K = 3;
    SAXVSMClusteringParams.METHOD = "hierarchical";
    SAXVSMClusteringParams.LINKAGE = "single";
    SAXVSMClusteringParams.SAX_WINDOW_SIZE = 60;
    SAXVSMClusteringParams.SAX_PAA_SIZE = 8;
    SAXVSMClusteringParams.SAX_ALPHABET_SIZE = 6;

    SAXVSMClusteringCLI.Result result = SAXVSMClusteringCLI.execute();
    assertEquals(
        "clustering results: method hierarchical, strategy EXACT, window 60, PAA 8, alphabet 6, k 3, linkage single, purity 1.00",
        result.summaryLine);
    assertNotNull(result.newick);
    assertEquals(3, result.clusterLines.length);
  }

  @Test
  public void usageExitWhenNoArgs() {
    assertEquals(-10, SAXVSMClusteringCLI.run(new String[0]));
  }
}
