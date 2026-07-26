# Changelog

All notable changes to sax-vsm are documented here.
This project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Fixed
- **`KMeans`:** an emptied cluster no longer crashes the run (`IllegalArgumentException`
  from `meanVector`); it keeps its previous centroid and may re-capture points. Reachable
  via duplicate tf·idf vectors picked as distinct centroids.
- **`KMeans`:** added a 500-iteration cap — all-zero tf·idf vectors (zero centroid is
  "maximally dissimilar" to itself under the cosine guard) could previously loop forever.
- **`TextProcessor.tfidfToTable`:** the zero-fill `DecimalFormat` now pins `Locale.US`;
  on decimal-comma locales it emitted `0,00000` inside comma-separated output.

## [2.1.0] — 2026-07-26

Feature and hardening release on top of **2.0.1**. Minor — not patch — because the
clustering package was rewritten in place: the 2.0.1 `net.seninp.jmotif.cluster`
classes are **removed**, not deprecated. (Briefly tagged `2.0.2`; never published
under that version.)

### Removed (breaking)
- The 2.0.1 clustering API: `Cluster`, `ClusterUtils`, `FurthestFirstStrategy`, `HC`,
  `LinkageCriterion`, `RandomStartStrategy`, `StartStrategy`, `TextKMeans`. Both 2.0.1
  algorithms were defective — `HC` merged non-mutual nearest neighbors, and
  furthest-first seeding used an unseeded RNG with an inverted distance comparison —
  so the package was replaced rather than patched.

### Added
- **Clustering API rewrite**: `SAXVSMClustering`, `KMeans` (seeded `RANDOM` /
  `FURTHEST_FIRST` init), `HierarchicalClustering` (mutual-nearest-neighbor chain;
  `SINGLE`/`COMPLETE`/`AVERAGE` linkage), `Dendrogram`/`ClusterNode`/`ClusterAssignments`;
  CBF / Gun_Point benchmark documentation and golden tests.
- **`SAXVSMClusteringCLI`** for command-line unsupervised clustering.
- `TextProcessor.perSeriesWordBags(...)`.

### Changed
- **Build:** added a **PMD + SpotBugs** quality gate (`-Pquality`); fixed a resource leak
  and comparison bugs surfaced by the gate.
- **Logging:** route `TextProcessor` cosine-distance warnings to SLF4J; remove dead debug
  prints.

### Fixed
- **Cosine distance/similarity:** guard zero-magnitude vectors (0/0 no longer produces
  NaN); regression tests for distance, similarity, and instrumented paths.
- **Zero-norm normalization** and **empty tf-idf classification** guarded against
  degenerate inputs.
- **`CosineDistanceMatrix`:** removed JVM-global `Locale.setDefault` side effect.

### Docs
- README badge URLs updated (Maven Central, GitHub Actions CI, Codecov).

## [2.0.1] — 2026-07-09

Stack alignment release: TF·IDF weighting standardized to `log1p` across saxpy, jmotif-R,
and Java; verified 3-way identical on CBF via
[jmotif-conformance](https://github.com/jMotif/jmotif-conformance). Published to Maven
Central.

## [2.0.0] — 2026-06-30

First release of the aligned 2.x SAX-VSM classifier line. See git tag `sax-vsm-2.0.0`.
