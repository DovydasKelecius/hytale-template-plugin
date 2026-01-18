# com.hypixel.hytale.metrics

This package contains classes for collecting and reporting server metrics.

## Overview

The `metrics` package provides a system for collecting and reporting various performance and operational metrics from the Hytale server. This data can be used to monitor server health, identify performance bottlenecks, and gain insights into server behavior.

### Key Classes

*   `MetricsRegistry`: A central registry for all metrics.
*   `MetricProvider`: An interface for providing metrics.
*   `ExecutorMetricsRegistry`: A metrics registry for executors.
*   `JVMMetrics`: Collects metrics from the Java Virtual Machine.
*   `MetricResults`: Stores the results of metric collection.

### Sub-packages

*   `metric`: Contains classes for defining and collecting individual metrics.
