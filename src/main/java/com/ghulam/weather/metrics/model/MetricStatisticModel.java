package com.ghulam.weather.metrics.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricStatisticModel {

    /**
     * metric name, what will be Temp, Humidity and WindSpeed
     */
    private String metricName;

    /**
     * Value of metric
     */
    private Double metricValue;

    /**
     * Statistics, min, max, avg, sum
     */
    private String statistic;
}
