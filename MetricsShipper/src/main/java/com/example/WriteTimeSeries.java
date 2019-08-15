package com.example;

import com.google.api.Metric;
import com.google.api.MonitoredResource;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.CreateTimeSeriesRequest;
import com.google.monitoring.v3.Point;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeInterval;
import com.google.monitoring.v3.TimeSeries;
import com.google.monitoring.v3.TypedValue;
import com.google.protobuf.util.Timestamps;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WriteTimeSeries {

  private static final String CUSTOM_METRIC_DOMAIN = "custom.googleapis.com";
  private static final Random random = new Random();
  private final ProjectName monitoredProjectName;
  private final String monitoredProjectId;
  MetricServiceClient metricServiceClient;

  WriteTimeSeries(String monitoredProjectId) {
    this.monitoredProjectId = monitoredProjectId;
    this.monitoredProjectName = ProjectName.of(monitoredProjectId);
  }



  List<Point> createRandomDataPoint() {
    // Prepares an individual data point
    TimeInterval interval =
        TimeInterval.newBuilder()
            .setEndTime(Timestamps.fromMillis(System.currentTimeMillis()))
            .build();
    TypedValue value = TypedValue.newBuilder().setDoubleValue(random.nextDouble() * 10000).build();
    Point point = Point.newBuilder().setInterval(interval).setValue(value).build();
    List<Point> pointList = new ArrayList<>();
    pointList.add(point);
    return pointList;
  }

  Metric createMetric(String metricType) {
    // Prepares the metric
    Map<String, String> metricLabels = new HashMap<>();
    return Metric.newBuilder().setType(metricType).putAllLabels(metricLabels).build();
  }

  private String getRandomElement(List<String> list) {
    return list.get(random.nextInt(list.size()));
  }

  MonitoredResource createResourceDescriptor() {
    // Prepares the monitored resource descriptor
    Map<String, String> resourceLabels = new HashMap<>();
    resourceLabels.put("project_id", this.monitoredProjectId);
    resourceLabels.put(
        "location", getRandomElement(Arrays.asList("us-central1", "us-east1", "us-west1")));
    resourceLabels.put(
        "namespace", getRandomElement(Arrays.asList("order-pipeline", "reporting-pipeline", "cleansing-pipeline")));
    resourceLabels.put("job", getRandomElement(Arrays.asList("consumer", "transformer", "producer")));
    resourceLabels.put(
        "task_id", getRandomElement(Arrays.asList("reader", "validator", "combiner", "writer")));
    return MonitoredResource.newBuilder()
        .setType("generic_task")
        .putAllLabels(resourceLabels)
        .build();
  }

  CreateTimeSeriesRequest createTimeSeriesRequest(Metric metric, MonitoredResource resource) {
    // Prepares the time series request
    TimeSeries timeSeries =
        TimeSeries.newBuilder()
            .setMetric(metric)
            .setResource(resource)
            .addAllPoints(createRandomDataPoint())
            .build();

    List<TimeSeries> timeSeriesList = new ArrayList<>();
    timeSeriesList.add(timeSeries);

    return CreateTimeSeriesRequest.newBuilder()
        .setName(this.monitoredProjectName.toString())
        .addAllTimeSeries(timeSeriesList)
        .build();
  }

  void pushTimeSeriesData(CreateTimeSeriesRequest createTimeSeriesRequest) {
    // Writes time series data
    try {
      this.metricServiceClient = MetricServiceClient.create();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      this.metricServiceClient.shutdownNow();
    }
  }

  public static void main(String[] args) {
    String monitoredProjectId = args[0];
    String metricName = args[1];
    Integer maxDataPoints = Integer.parseInt(args[2]);
    for (int i = 0; i < maxDataPoints; i++) {
      WriteTimeSeries writeTimeSeries = new WriteTimeSeries(monitoredProjectId);
      Metric metric = writeTimeSeries.createMetric(CUSTOM_METRIC_DOMAIN + "/" + metricName);
      MonitoredResource monitoredResource = writeTimeSeries.createResourceDescriptor();
      CreateTimeSeriesRequest createTimeSeriesRequest =
          writeTimeSeries.createTimeSeriesRequest(metric, monitoredResource);
      System.out.println(createTimeSeriesRequest.getTimeSeriesList().toString());

      writeTimeSeries.pushTimeSeriesData(createTimeSeriesRequest);
      try {
        Thread.sleep(60 * 1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
