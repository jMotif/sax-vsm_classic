package net.seninp.jmotif.cluster;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class TestDendrogram {

  @Test
  public void partitionSingleLeaf() {
    ClusterNode leaf = new ClusterNode("a");
    Dendrogram tree = new Dendrogram(leaf);
    ClusterAssignments cut = tree.partition(1);
    assertEquals(1, cut.k());
    assertEquals(0, cut.clusterOf("a"));
  }

  @Test
  public void partitionGreedySplit() {
    ClusterNode a = new ClusterNode("a");
    ClusterNode b = new ClusterNode("b");
    ClusterNode c = new ClusterNode("c");
    ClusterNode d = new ClusterNode("d");
    ClusterNode ab = new ClusterNode();
    ab.merge(a, b, 0.1d);
    ClusterNode cd = new ClusterNode();
    cd.merge(c, d, 0.1d);
    ClusterNode root = new ClusterNode();
    root.merge(ab, cd, 0.5d);

    Dendrogram tree = new Dendrogram(root);
    ClusterAssignments cut = tree.partition(2);
    assertEquals(2, cut.k());
    assertEquals(cut.clusterOf("a"), cut.clusterOf("b"));
    assertEquals(cut.clusterOf("c"), cut.clusterOf("d"));
  }

  @Test
  public void rejectsInvalidK() {
    Dendrogram tree = new Dendrogram(new ClusterNode("a"));
    try {
      tree.partition(0);
      assertEquals(true, false);
    }
    catch (IllegalArgumentException expected) {
      assertEquals("k must be >= 1 (got 0)", expected.getMessage());
    }
  }
}
