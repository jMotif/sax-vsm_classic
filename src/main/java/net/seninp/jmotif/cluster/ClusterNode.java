package net.seninp.jmotif.cluster;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;
import net.seninp.jmotif.text.CosineDistanceMatrix;

/**
 * Node in a hierarchical clustering dendrogram over series (bag) ids.
 */
public final class ClusterNode {

  public ClusterNode left = null;
  public ClusterNode right = null;
  public double mergeDistance = 0.0d;
  private TreeSet<String> memberIds;

  public ClusterNode() {
    super();
  }

  public ClusterNode(String seriesId) {
    this.memberIds = new TreeSet<String>();
    memberIds.add(seriesId);
  }

  void merge(ClusterNode left, ClusterNode right, double distance) {
    this.left = left;
    this.right = right;
    this.memberIds = new TreeSet<String>();
    this.memberIds.addAll(left.memberIds);
    this.memberIds.addAll(right.memberIds);
    this.mergeDistance = distance;
  }

  public TreeSet<String> memberIds() {
    return memberIds;
  }

  public boolean isLeaf() {
    return left == null && right == null;
  }

  double distanceTo(ClusterNode other, CosineDistanceMatrix distanceMatrix, Linkage linkage) {
    if (other.memberIds.size() == 1 && this.memberIds.size() == 1) {
      return distanceMatrix.distanceBetween(other.memberIds.first(), this.memberIds.first());
    }
    if (linkage == Linkage.SINGLE) {
      double minDist = Double.MAX_VALUE;
      for (String idA : this.memberIds) {
        for (String idB : other.memberIds) {
          double dist = distanceMatrix.distanceBetween(idA, idB);
          if (dist < minDist) {
            minDist = dist;
          }
        }
      }
      return minDist;
    }
    if (linkage == Linkage.COMPLETE) {
      double maxDist = Double.MIN_VALUE;
      for (String idA : this.memberIds) {
        for (String idB : other.memberIds) {
          double dist = distanceMatrix.distanceBetween(idA, idB);
          if (dist > maxDist) {
            maxDist = dist;
          }
        }
      }
      return maxDist;
    }
    throw new IllegalArgumentException("Unsupported linkage: " + linkage);
  }

  public String toNewick() {
    StringBuilder sb = new StringBuilder();
    if (left != null && right != null) {
      double height = mergeHeight();
      if (left.isLeaf()) {
        sb.append(left.toNewick()).append(":").append(formatNumber(height));
      }
      else {
        sb.append("(").append(left.toNewick()).append("):").append(formatNumber(height));
      }
      sb.append(",");
      if (right.isLeaf()) {
        sb.append(right.toNewick()).append(":").append(formatNumber(height));
      }
      else {
        sb.append("(").append(right.toNewick()).append("):").append(formatNumber(height));
      }
      return sb.toString();
    }
    return memberIds.first();
  }

  private double mergeHeight() {
    if (left.isLeaf() && !right.isLeaf()) {
      return Math.abs(mergeDistance - right.mergeDistance);
    }
    if (!left.isLeaf() && right.isLeaf()) {
      return Math.abs(mergeDistance - left.mergeDistance);
    }
    return mergeDistance;
  }

  private static String formatNumber(double number) {
    DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
    symbols.setDecimalSeparator('.');
    DecimalFormat format = new DecimalFormat("#.##########", symbols);
    return format.format(number / 2.0d);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((memberIds == null) ? 0 : memberIds.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ClusterNode other = (ClusterNode) obj;
    if (memberIds == null) {
      return other.memberIds == null;
    }
    return memberIds.equals(other.memberIds);
  }

  @Override
  public String toString() {
    return Arrays.toString(memberIds.toArray());
  }
}
