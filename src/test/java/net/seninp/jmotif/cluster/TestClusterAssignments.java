package net.seninp.jmotif.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class TestClusterAssignments {

  @Test
  public void labelPurityCountsPluralityPerCluster() {
    Map<Integer, List<String>> groups = new HashMap<Integer, List<String>>();
    groups.put(0, Arrays.asList("1:0", "1:1", "2:0"));
    groups.put(1, Arrays.asList("2:1", "2:2", "2:3"));

    Map<String, String> labels = new HashMap<String, String>();
    labels.put("1:0", "1");
    labels.put("1:1", "1");
    labels.put("2:0", "2");
    labels.put("2:1", "2");
    labels.put("2:2", "2");
    labels.put("2:3", "2");

    ClusterAssignments assignments = ClusterAssignments.of(groups);
    assertEquals(0.8333333333333334, assignments.labelPurity(labels), 1e-12);
  }

  @Test
  public void clusterOfRoundTripsMembers() {
    Map<Integer, List<String>> groups = new HashMap<Integer, List<String>>();
    groups.put(0, Arrays.asList("a", "b"));
    groups.put(1, Arrays.asList("c"));

    ClusterAssignments assignments = ClusterAssignments.of(groups);
    assertEquals(2, assignments.k());
    assertEquals(Arrays.asList("a", "b"), assignments.members(0));
    assertEquals(0, assignments.clusterOf("a"));
    assertEquals(1, assignments.clusterOf("c"));
  }
}
