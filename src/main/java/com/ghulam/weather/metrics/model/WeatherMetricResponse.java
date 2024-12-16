package com.ghulam.weather.metrics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghulam.weather.metrics.entity.WeatherMetricEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;


/**
 * Weather metric model to respond to API
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WeatherMetricResponse {

    /**
     * List of Weather metric
     */
    List<WeatherMetricEntity> weatherMetrics;
}
