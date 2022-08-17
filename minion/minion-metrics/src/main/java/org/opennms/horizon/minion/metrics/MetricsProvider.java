package org.opennms.horizon.minion.metrics;

import com.codahale.metrics.MetricRegistry;

/**
 * Dedicated SPI interface which allows to retrieve a dynamic set of metrics.
 */
public interface MetricsProvider {

  MetricRegistry getMetrics();

}