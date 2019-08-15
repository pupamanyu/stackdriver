package com.example;

import com.google.api.LabelDescriptor;
import com.google.api.MetricDescriptor;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.CreateMetricDescriptorRequest;
import com.google.monitoring.v3.MetricDescriptorName;
import com.google.monitoring.v3.ProjectName;

import java.io.IOException;

public class CustomMetricDescriptor {
  private static final String CUSTOM_METRIC_DOMAIN = "custom.googleapis.com";
  private final ProjectName monitoredProjectName;
  private MetricServiceClient metricServiceClient;

  CustomMetricDescriptor(String monitoredProjectId) {
    this.monitoredProjectName = ProjectName.of(monitoredProjectId);
    try {
      this.metricServiceClient = MetricServiceClient.create();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  MetricDescriptor getMetricDescriptor(String metricName, String description) {
    /*
     * Use Monitored Resource of Generic task and adopt the needed labels
     */
    return MetricDescriptor.newBuilder()
        .setType(CUSTOM_METRIC_DOMAIN + "/" + metricName)
        .addLabels(
            LabelDescriptor.newBuilder()
                .setKey("project_id")
                .setValueType(LabelDescriptor.ValueType.STRING)
                .setDescription("The identifier of the GCP project associated with this resource")
                .build())
        .addLabels(
            LabelDescriptor.newBuilder()
                .setKey("location")
                .setValueType(LabelDescriptor.ValueType.STRING)
                .setDescription(
                    "The GCP region in which data about the resource is stored. For example, us-central1-a")
                .build())
        .addLabels(
            LabelDescriptor.newBuilder()
                .setKey("namespace")
                .setValueType(LabelDescriptor.ValueType.STRING)
                .setDescription("A namespace identifier, such as a pipeline name")
                .build())
        .addLabels(
            LabelDescriptor.newBuilder()
                .setKey("job")
                .setValueType(LabelDescriptor.ValueType.STRING)
                .setDescription(
                    "An identifier for a grouping of related tasks, such as the name of a microservice or distributed batch job")
                .build())
        .addLabels(
            LabelDescriptor.newBuilder()
                .setKey("task_id")
                .setValueType(LabelDescriptor.ValueType.STRING)
                .setDescription(
                    "A unique identifier for the task within the namespace and job, such as a pipeline instance(job id) identifying the task within the job.")
                .build())
        .setDescription(description)
        .setMetricKind(MetricDescriptor.MetricKind.GAUGE)
        .setValueType(MetricDescriptor.ValueType.DOUBLE)
        .setUnit("ms")
        .build();
  }

  CreateMetricDescriptorRequest getMetricDescriptorRequest(MetricDescriptor metricDescriptor) {
    return CreateMetricDescriptorRequest.newBuilder()
        .setName(this.monitoredProjectName.toString())
        .setMetricDescriptor(metricDescriptor)
        .build();
  }

  void create(String metricName, String description) {

    MetricDescriptor metricDescriptor = getMetricDescriptor(metricName, description);
    /*
     * MetricDescriptor creation is an idempotent operation
     */
    this.metricServiceClient.createMetricDescriptor(getMetricDescriptorRequest(metricDescriptor));
  }

  void delete(String monitoredProjectId, String metricName) {
    MetricDescriptorName metricType =
        MetricDescriptorName.of(monitoredProjectId, CUSTOM_METRIC_DOMAIN + "/" + metricName);
    this.metricServiceClient.deleteMetricDescriptor(metricType);
  }
}
