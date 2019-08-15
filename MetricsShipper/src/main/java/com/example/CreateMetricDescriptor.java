package com.example;

public class CreateMetricDescriptor {

  public static void main(String[] args) {

    /*
     * args[0] is for eg: "etl/streaming/processing_time_ms"
     * monitoredProjectId is the monitored project ID
     */
    String metricName = args[0];
    String monitoredProjectId = args[1];
    String metricDescription = args[2];
    CustomMetricDescriptor customMetricDescriptor = new CustomMetricDescriptor(monitoredProjectId);
    /*
     * Add Custom MetricDescriptor
     */
    customMetricDescriptor.create(metricName, metricDescription);
  }
}
