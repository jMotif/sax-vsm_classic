package net.seninp.jmotif.cluster;

/**
 * Initial centroid selection for {@link KMeans}.
 */
public enum KMeansInit {
  /** Sample {@code k} distinct series vectors at random. */
  RANDOM,
  /** Greedy furthest-first seeding (k-means++ style on cosine similarity). */
  FURTHEST_FIRST
}
