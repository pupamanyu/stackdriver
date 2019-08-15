package com.example;

import com.google.monitoring.v3.MetricDescriptorName;

public class PushMetrics {
  private static final String CUSTOM_METRIC_DOMAIN = "custom.googleapis.com";

  public static void main(String[] args) {
    String monitoredProjectId = args[0];
    String metricName = args[1];
    Integer maxDataPoints = Integer.parseInt(args[3]);

    MetricDescriptorName metricType =
        MetricDescriptorName.of(monitoredProjectId, CUSTOM_METRIC_DOMAIN + "/" + metricName);
    CustomMetricDescriptor customMetricDescriptor = new CustomMetricDescriptor(monitoredProjectId);
    WriteTimeSeries writeTimeSeries = new WriteTimeSeries(monitoredProjectId);
  }
}
