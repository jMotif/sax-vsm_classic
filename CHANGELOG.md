# Changelog

All notable changes to sax-vsm are documented here.
This project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [2.0.2] — 2026-07-22

Feature and hardening release on top of **2.0.1**.

### Added
- **`SAXVSMClusteringCLI`** for command-line unsupervised clustering.
- **Clustering API rewrite** with CBF / Gun_Point benchmark documentation.

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
