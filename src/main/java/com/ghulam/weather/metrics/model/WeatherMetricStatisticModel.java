package com.ghulam.weather.metrics.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Calculated Statistic per sensor
 */
@Data
@ToString
@Builder
public class WeatherMetricStatisticModel {

    /**
     * Sensor identifier that is capturing weather metrics
     */
    private String sensorId;

    /**
     * Timestamp when statistic start calculating.
     * e.g "2024-12-13T20:55:16.042614 - statistic will be calculated 13 Dec on wards
     */
    private LocalDateTime timestamp;

    /**
     * List of metric with calculated statistic
     */
    List<MetricStatisticModel> metrics;

}
