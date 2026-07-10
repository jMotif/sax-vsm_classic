package net.seninp.jmotif.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.jmotif.text.Params;
import net.seninp.jmotif.text.TextProcessor;
import net.seninp.util.UCRUtils;

public class TestSAXVSMClustering {

  private static final double TOL = 1e-12;

  @Test
  public void perSeriesWordBagsUseClassIndexLabels() throws Exception {
    Map<String, List<double[]>> train = UCRUtils
        .readUCRData("src/resources/data/Gun_Point/Gun_Point_TRAIN");
    Params params = new Params(33, 17, 15, 0.01, NumerosityReductionStrategy.EXACT);

    TextProcessor tp = new TextProcessor();
    assertEquals(50, tp.perSeriesWordBags(train, params).size());
    assertEquals(2, tp.labeledSeries2WordBags(train, params).size());
  }

  @Test
  public void seriesTfidfKeysMatchSeriesLabels() throws Exception {
    Map<String, List<double[]>> train = UCRUtils
        .readUCRData("src/resources/data/Gun_Point/Gun_Point_TRAIN");
    Params params = new Params(33, 17, 15, 0.01, NumerosityReductionStrategy.EXACT);

    Map<String, Map<String, Double>> tfidf = SAXVSMClustering.seriesTfidf(train, params);
    Map<String, String> labels = SAXVSMClustering.seriesLabels(train);
    assertEquals(labels.keySet(), tfidf.keySet());
  }

  @Test
  public void gunPointKMeansRecoversClasses() throws Exception {
    Map<String, List<double[]>> train = UCRUtils
        .readUCRData("src/resources/data/Gun_Point/Gun_Point_TRAIN");
    Params params = new Params(33, 17, 15, 0.01, NumerosityReductionStrategy.EXACT);

    Map<String, Map<String, Double>> tfidf = SAXVSMClustering.seriesTfidf(train, params);
    assertEquals(50, tfidf.size());

    ClusterAssignments assignments =
        SAXVSMClustering.kMeans(tfidf, 2, KMeansInit.FURTHEST_FIRST, 21L);
    double purity = assignments.labelPurity(SAXVSMClustering.seriesLabels(train));
    assertEquals(0.86, purity, TOL);
  }

  @Test
  public void gunPointHierarchicalClusteringBuildsTree() throws Exception {
    Map<String, List<double[]>> train = UCRUtils
        .readUCRData("src/resources/data/Gun_Point/Gun_Point_TRAIN");
    Params params = new Params(33, 17, 15, 0.01, NumerosityReductionStrategy.EXACT);

    Map<String, Map<String, Double>> tfidf = SAXVSMClustering.seriesTfidf(train, params);
    Dendrogram tree = SAXVSMClustering.hierarchical(tfidf, Linkage.COMPLETE);
    assertFalse(tree.toNewick().isEmpty());
    assertEquals(50, tree.root().memberIds().size());
  }

  @Test
  public void cbfKMeansRecoversClasses() throws Exception {
    Map<String, List<double[]>> train = UCRUtils.readUCRData("src/resources/data/cbf/CBF_TRAIN");
    Params params = new Params(60, 8, 6, 0.01, NumerosityReductionStrategy.EXACT);

    Map<String, Map<String, Double>> tfidf = SAXVSMClustering.seriesTfidf(train, params);
    assertEquals(30, tfidf.size());

    ClusterAssignments assignments =
        SAXVSMClustering.kMeans(tfidf, 3, KMeansInit.FURTHEST_FIRST, 2L);
    double purity = assignments.labelPurity(SAXVSMClustering.seriesLabels(train));
    assertEquals(0.9, purity, TOL);
  }

  @Test
  public void cbfSingleLinkagePartitionsClasses() throws Exception {
    Map<String, List<double[]>> train = UCRUtils.readUCRData("src/resources/data/cbf/CBF_TRAIN");
    Params params = new Params(60, 8, 6, 0.01, NumerosityReductionStrategy.EXACT);

    Map<String, Map<String, Double>> tfidf = SAXVSMClustering.seriesTfidf(train, params);
    Dendrogram tree = SAXVSMClustering.hierarchical(tfidf, Linkage.SINGLE);
    ClusterAssignments assignments = tree.partition(3);
    assertEquals(3, assignments.k());
    assertEquals(1.0, assignments.labelPurity(SAXVSMClustering.seriesLabels(train)), TOL);
  }
}
