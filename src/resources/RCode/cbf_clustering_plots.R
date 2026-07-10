#!/usr/bin/env Rscript
#
# CBF SAX-VSM clustering figures (style aligned with best_words_heat.R + shingles.R).
# Run from this directory:
#   Rscript cbf_clustering_plots.R
#
suppressPackageStartupMessages({
  library(jmotif)
  library(ggplot2)
  library(scales)
  library(grid)
  library(gridExtra)
  library(ggdendro)
  library(Cairo)
})

# SAX-VSM params match TestSAXVSMClustering / classifier golden (PAA=8, not 6).
W <- 60
P <- 8
A <- 6
NR <- "exact"
RRA <- 0.01
K <- 3

CLASS_NAMES <- c("1" = "Cylinder", "2" = "Bell", "3" = "Funnel")
CLUSTER_COLS <- c("#006400", "#B22222", "#DAA520")  # darkgreen, firebrick, goldenrod

read_ucr <- function(path) {
  d <- read.table(path, header = FALSE)
  labels <- as.integer(d[, 1])
  data <- as.matrix(d[, -1, drop = FALSE])
  storage.mode(data) <- "double"
  list(labels = labels, data = data)
}

series_ids <- function(labels) {
  counts <- integer(3)
  ids <- character(length(labels))
  for (i in seq_along(labels)) {
    lab <- as.character(labels[i])
    idx <- counts[lab]
    ids[i] <- paste0(lab, ":", idx)
    counts[lab] <- counts[lab] + 1L
  }
  ids
}

series_tfidf <- function(data, labels, w, p, a, nr, rra) {
  ids <- series_ids(labels)
  bags <- setNames(vector("list", length(ids)), ids)
  for (i in seq_len(nrow(data))) {
    bags[[ids[i]]] <- series_to_wordbag(as.numeric(data[i, ]), w, p, a, nr, rra)
  }
  tf <- bags_to_tfidf(bags)
  words <- tf$words
  mat <- t(as.matrix(tf[, -1, drop = FALSE]))
  storage.mode(mat) <- "double"
  mat[is.na(mat)] <- 0
  colnames(mat) <- words
  rownames(mat) <- ids
  list(tf = tf, matrix = mat, ids = ids)
}

cosine_distance_matrix <- function(mat) {
  n <- nrow(mat)
  dn <- rownames(mat)
  d <- matrix(0, n, n, dimnames = list(dn, dn))
  norms <- sqrt(rowSums(mat * mat))
  norms[norms == 0] <- 1
  for (i in seq_len(n)) {
    for (j in i:n) {
      sim <- sum(mat[i, ] * mat[j, ]) / (norms[i] * norms[j])
      dist <- 1 - sim
      d[i, j] <- dist
      d[j, i] <- dist
    }
  }
  stats::as.dist(d)
}

furthest_first_kmeans <- function(mat, k, seed = 2L) {
  set.seed(seed)
  n <- nrow(mat)
  ids <- rownames(mat)
  dist <- as.matrix(cosine_distance_matrix(mat))
  chosen <- sample.int(n, 1)
  while (length(chosen) < k) {
    best <- NA
    best_val <- Inf
    for (cand in setdiff(seq_len(n), chosen)) {
      closest <- max(1 - dist[cand, chosen])
      if (closest < best_val) {
        best_val <- closest
        best <- cand
      }
    }
    chosen <- c(chosen, best)
  }
  centroids <- mat[chosen, , drop = FALSE]
  assign <- integer(n)
  for (i in seq_len(n)) {
    sims <- numeric(k)
    for (j in seq_len(k)) {
      num <- sum(mat[i, ] * centroids[j, ])
      den <- sqrt(sum(mat[i, ]^2)) * sqrt(sum(centroids[j, ]^2))
      sims[j] <- if (den == 0) 0 else num / den
    }
    assign[i] <- which.max(sims)
  }
  setNames(assign, ids)
}

label_purity <- function(assign_vec, true_labels) {
  correct <- 0
  for (cid in unique(assign_vec)) {
    correct <- correct + max(tabulate(true_labels[assign_vec == cid]))
  }
  correct / length(assign_vec)
}

plot_series_line <- function(x, y, title, colour = "cornflowerblue", size = 1.2) {
  dat <- data.frame(x = x, y = as.vector(y))
  ggplot(dat, aes(x, y)) +
    geom_line(colour = colour, linewidth = size) +
    ggtitle(title) +
    theme_bw() +
    theme(
      plot.title = element_text(size = 18),
      axis.title.x = element_blank(),
      axis.title.y = element_blank(),
      axis.text.x = element_text(size = 10),
      axis.text.y = element_blank(),
      axis.ticks.y = element_blank(),
      panel.grid.major.y = element_blank(),
      panel.grid.minor.y = element_blank()
    )
}

plot_series_cluster <- function(x, y, title, cluster_id) {
  plot_series_line(x, y, title, CLUSTER_COLS[cluster_id])
}

plot_dendrogram <- function(hc, labels, ids) {
  dendr <- dendro_data(hc, type = "rectangle")
  leaf_labels <- labels[match(hc$labels, ids)]
  dendr$labels$label <- CLASS_NAMES[as.character(leaf_labels[hc$order])]
  ggplot() +
    geom_segment(data = segment(dendr), aes(x = x, y = y, xend = xend, yend = yend),
                 colour = "gray30") +
    geom_text(data = label(dendr), aes(x = x, y = y, label = label, hjust = -0.15), size = 4) +
    ggtitle("CBF — single-linkage dendrogram (cosine distance on tf·idf)") +
    coord_flip() +
    scale_y_reverse(expand = c(0.15, 0.05)) +
    theme_bw() +
    theme(
      plot.title = element_text(size = 16),
      axis.line.y = element_blank(),
      axis.ticks.y = element_blank(),
      axis.text.y = element_blank(),
      axis.title.y = element_blank(),
      axis.title.x = element_blank(),
      panel.grid = element_blank()
    )
}

pick_example <- function(data, labels, class_id, which = 1) {
  rows <- which(labels == class_id)
  data[rows[which], ]
}

# --- load data (bundled UCR sample: 30 train series) ---
train <- read_ucr("../data/cbf/CBF_TRAIN")
labels <- train$labels
data <- train$data
cat(sprintf("CBF TRAIN: %d series\n", nrow(data)))
print(table(labels))

tfidf <- series_tfidf(data, labels, W, P, A, NR, RRA)
dist <- cosine_distance_matrix(tfidf$matrix)
hc <- hclust(dist, method = "single")
hc_assign <- cutree(hc, k = K)[tfidf$ids]
km_assign <- furthest_first_kmeans(tfidf$matrix, K, seed = 2L)[tfidf$ids]

purity_hc <- label_purity(hc_assign, labels)
purity_km <- label_purity(km_assign, labels)
cat(sprintf("Single-linkage HC purity (k=3): %.2f\n", purity_hc))
cat(sprintf("k-means purity (furthest-first, seed=2): %.2f\n", purity_km))

# --- figure 1: class examples (3x3), cornflowerblue like shingles.R ---
p_cyl <- plot_series_line(1:ncol(data), pick_example(data, labels, 1, 1), "Cylinder")
p_bell <- plot_series_line(1:ncol(data), pick_example(data, labels, 2, 1), "Bell")
p_fun <- plot_series_line(1:ncol(data), pick_example(data, labels, 3, 1), "Funnel")
p_cyl2 <- plot_series_line(1:ncol(data), pick_example(data, labels, 1, 2), "Cylinder")
p_bell2 <- plot_series_line(1:ncol(data), pick_example(data, labels, 2, 2), "Bell")
p_fun2 <- plot_series_line(1:ncol(data), pick_example(data, labels, 3, 2), "Funnel")
p_cyl3 <- plot_series_line(1:ncol(data), pick_example(data, labels, 1, 3), "Cylinder")
p_bell3 <- plot_series_line(1:ncol(data), pick_example(data, labels, 2, 3), "Bell")
p_fun3 <- plot_series_line(1:ncol(data), pick_example(data, labels, 3, 3), "Funnel")

as_grob <- function(p) ggplotGrob(p)

fig_classes <- arrangeGrob(
  as_grob(p_cyl), as_grob(p_bell), as_grob(p_fun),
  as_grob(p_cyl2), as_grob(p_bell2), as_grob(p_fun2),
  as_grob(p_cyl3), as_grob(p_bell3), as_grob(p_fun3),
  ncol = 3,
  top = textGrob("CBF training shapes (Cylinder · Bell · Funnel)",
                 gp = gpar(fontsize = 18, fontface = "bold"))
)

# --- figure 2: dendrogram ---
fig_dend <- plot_dendrogram(hc, labels, tfidf$ids)

# --- figure 3: one series per HC cluster, colored by cluster ---
hc_plots <- lapply(sort(unique(hc_assign)), function(cid) {
  rows <- which(hc_assign == cid)
  labs <- labels[rows]
  maj <- as.integer(names(sort(table(labs), decreasing = TRUE)[1]))
  row <- data[rows[labs == maj][1], ]
  plot_series_cluster(1:ncol(data), row,
                      sprintf("Cluster %d (%s)", cid, CLASS_NAMES[as.character(maj)]),
                      as.integer(cid))
})
fig_clusters <- arrangeGrob(
  grobs = lapply(hc_plots, as_grob),
  ncol = 3,
  top = textGrob(sprintf("HC single-linkage clusters (purity %.0f%%)", purity_hc * 100),
                 gp = gpar(fontsize = 18, fontface = "bold"))
)

# --- figure 4: overview = dendrogram + class grid ---
fig_overview <- arrangeGrob(
  ggplotGrob(fig_dend), fig_classes,
  ncol = 1,
  heights = c(1.1, 2.2),
  top = textGrob("SAX-VSM tf·idf clustering on CBF (PAA=8, SAX=6, window=60)",
                 gp = gpar(fontsize = 20, fontface = "bold"))
)

out_dir <- "../assets"
dir.create(out_dir, showWarnings = FALSE, recursive = TRUE)

CairoPNG(file.path(out_dir, "cbf_clustering_classes.png"), width = 1200, height = 900, dpi = 120)
grid.draw(fig_classes)
dev.off()

CairoPNG(file.path(out_dir, "cbf_clustering_dendrogram.png"), width = 900, height = 500, dpi = 120)
print(fig_dend)
dev.off()

CairoPNG(file.path(out_dir, "cbf_clustering_hc_clusters.png"), width = 1200, height = 420, dpi = 120)
grid.draw(fig_clusters)
dev.off()

CairoPNG(file.path(out_dir, "cbf_clustering_overview.png"), width = 1200, height = 1400, dpi = 120)
grid.draw(fig_overview)
dev.off()

cat("Wrote:\n")
cat(" ", file.path(out_dir, "cbf_clustering_classes.png"), "\n")
cat(" ", file.path(out_dir, "cbf_clustering_dendrogram.png"), "\n")
cat(" ", file.path(out_dir, "cbf_clustering_hc_clusters.png"), "\n")
cat(" ", file.path(out_dir, "cbf_clustering_overview.png"), "\n")
