package com.example;

public class DeleteMetricDescriptor {

  public static void main(String[] args) {

    /*
     * metricName is for eg: "etl/pipeline/batch/hydrator/processing_time_ms"
     * monitoredProjectId is the monitored project ID
     */
    String metricName = args[0];
    String monitoredProjectId = args[1];
    CustomMetricDescriptor customMetricDescriptor = new CustomMetricDescriptor(monitoredProjectId);
    /*
     * Delete Custom MetricDescriptor
     */
    customMetricDescriptor.delete(monitoredProjectId, metricName);
  }
}
