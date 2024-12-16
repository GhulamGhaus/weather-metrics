package com.ghulam.weather.metrics.service;

import com.ghulam.weather.metrics.entity.MetricEntity;
import com.ghulam.weather.metrics.entity.WeatherMetricEntity;
import com.ghulam.weather.metrics.model.MetricStatisticModel;
import com.ghulam.weather.metrics.model.WeatherMetricStatisticModel;
import com.ghulam.weather.metrics.model.WeatherMetricResponse;
import com.ghulam.weather.metrics.repository.WeatherMetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherMetricService {

    WeatherMetricRepository weatherMetricRepository;

    @Autowired
    public WeatherMetricService(WeatherMetricRepository weatherMetricRepository) {
        this.weatherMetricRepository = weatherMetricRepository;
    }

    /**
     *  Persist the weather metrics into database
     * @param weatherMetricEntity - Metrics to be added
     * @return - return the save entity
     */
    public WeatherMetricEntity saveMetric(WeatherMetricEntity weatherMetricEntity) {
        return weatherMetricRepository.save(weatherMetricEntity);

    }

    /**
     * Weather metrics for the given sensor
     * @param sensorIds fetch weather metrics for given sensors if not fetch all
     * @return weather metrics data
     */
    public WeatherMetricResponse getWeatherActualData(List<String> sensorIds) {
        var all = ObjectUtils.isEmpty(sensorIds) ? weatherMetricRepository.findAll() : weatherMetricRepository.findBySensorIds(sensorIds);
        return WeatherMetricResponse.builder().weatherMetrics(all).build();

    }

    /**
     *  This method is to return the Weather statistics for all metrics grouped by sensor id,
     *  It will also allow to filter the data based on matching metric name, sensorId
     *  By default Avg statistic will be provided
     *  Statistic will be calculated based on given date range (start date, end date) if no range provided, default latest is one week.
     * <p>
     *
     * @param metricNamesToMatch -  List of metrics wanted to query
     * @param sensorId - List of sensor id wanted to query
     * @param statistic - Expected statistic e.g - min, max, sum, avg
     * @param startDate - date from statistic will be start calculating
     * @param endDate - date till statistic will stop calculating
     * @return List of Weather Metrics with calculated statistics
     */
    public List<WeatherMetricStatisticModel> getAllMetricStatistics(List<String> metricNamesToMatch, List<String> sensorId, String statistic, LocalDateTime startDate, LocalDateTime endDate) {

        // Fetch the data from Db based on given queries, SensorIds And date range
        List<WeatherMetricEntity> all = ObjectUtils.isEmpty(sensorId)
                ? weatherMetricRepository.findByTimestampBetween(startDate, endDate)
                : weatherMetricRepository.findBySensorIdsAndTimestampBetween(sensorId, startDate, endDate);

        //Calculate Statistic for given metric, if not, process all metricName
        if(ObjectUtils.isEmpty(metricNamesToMatch)){
                return calculateStatics(all, statistic);
        }
        List<WeatherMetricEntity> filteredWeatherMetrics = all.stream()
                .map(wm ->
                        WeatherMetricEntity.builder()
                                .sensorId(wm.getSensorId())
                                .timestamp(wm.getTimestamp())
                                .metrics(wm.getMetrics().stream()
                                        .filter(metric -> metricNamesToMatch.contains(metric.getMetricName())) // Filter metrics by metricName
                                        .collect(Collectors.toList())).build()


                )
                .filter(wm -> !wm.getMetrics().isEmpty()) // Retain only entities with matching metrics
                .toList(); // Collect the final list of WeatherMetricEntity

        return calculateStatics(filteredWeatherMetrics, statistic); // Calculate static for filtered data

    }

    /**
     * Calculate the statistic for Weather metrics group by sensor id
     * @param all -  Weather metrics data
     * @param statistic - Expected statistic e.g - min, max, sum, avg
     * @return return Weather metrics statistic
     */
    public static List<WeatherMetricStatisticModel> calculateStatics(List<WeatherMetricEntity> all, String statistic) {
       return all.stream()
                // Group by sensorId
                .collect(Collectors.groupingBy(WeatherMetricEntity::getSensorId)).entrySet().stream().map(entry -> {
                    String sensorId = entry.getKey();
                    List<WeatherMetricEntity> metricsBySensor = entry.getValue();

                   Map<String, Double> statisticMappedByMetricName = getStatisticMappedByMetricName(metricsBySensor, statistic);

                   // Aggregate MetricStatisticModel from Statistic mapped by metricName
                    List<MetricStatisticModel> aggregatedMetrics = statisticMappedByMetricName.entrySet().stream()
                            .map(avgEntry -> MetricStatisticModel.builder()
                                    .metricName(avgEntry.getKey())
                                    .metricValue(avgEntry.getValue())
                                    .statistic(statistic).build()
                            )
                            .collect(Collectors.toList());


                    // Transform the Weather Metric Statistic
                    return WeatherMetricStatisticModel.builder()
                            .metrics(aggregatedMetrics)
                            .sensorId(sensorId)
                            .timestamp(metricsBySensor.get(0).getTimestamp())
                            .build();

                })
                .collect(Collectors.toList());
    }

    /**
     * Calculate the statistics (min, max, sum, avg) for a metric on given sensor data
     * @param metricsBySensor - Sensor data
     * @param statistic - Expected statistic e.g - min, max, sum, avg
     * @return return map of metric and its statistic (Temp, Avg)
     */
    private static Map<String, Double> getStatisticMappedByMetricName(List<WeatherMetricEntity> metricsBySensor, String statistic) {
        // Step 2: Flatten the metrics and calculate statistics by metricName
        return switch (statistic) {
            case "max" -> getMax(metricsBySensor);
            case "min" -> getMin(metricsBySensor);
            case "sum" -> getSum(metricsBySensor);
            case "avg" -> getAverage(metricsBySensor);
            default -> // Handle default or invalid statistic type
                    getAverage(metricsBySensor); // return average by all  other case
        };
    }

    /**
     * Calculate the Average for all metric
     *
     * @param metricsBySensor sensor data
     * @return return map of metric and average metric
     */
    private static Map<String, Double> getAverage(List<WeatherMetricEntity> metricsBySensor) {
        return metricsBySensor.stream()
                .flatMap(wm -> wm.getMetrics().stream())
                .collect(Collectors.groupingBy(
                        MetricEntity::getMetricName,
                        Collectors.averagingDouble(MetricEntity::getMetricValue)
                ));
    }

    /**
     * Calculate the min for all metrics
     *
     * @param metricsBySensor sensor data
     * @return return map of metric and min metrics
     */
    private static Map<String, Double> getMin(List<WeatherMetricEntity> metricsBySensor) {
        return metricsBySensor.stream()
                .flatMap(wm -> wm.getMetrics().stream())
                .collect(Collectors.groupingBy(
                        MetricEntity::getMetricName,
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparingDouble(MetricEntity::getMetricValue)),
                                min -> min.map(MetricEntity::getMetricValue).orElse(0.0) // Default to 0.0 if no value exists
                        )
                ));
    }

    /**
     * Calculate the max for all metrics
     *
     * @param metricsBySensor sensor data
     * @return return map of metric and max metrics
     */
    private static Map<String, Double> getMax(List<WeatherMetricEntity> metricsBySensor) {
        return metricsBySensor.stream()
                .flatMap(wm -> wm.getMetrics().stream())
                .collect(Collectors.groupingBy(
                        MetricEntity::getMetricName,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(MetricEntity::getMetricValue)),
                                max -> max.map(MetricEntity::getMetricValue).orElse(0.0) // Default to 0.0 if no value exists
                        )
                ));
    }

    /**
     * Calculate the sum for all metrics
     *
     * @param metricsBySensor sensor data
     * @return return map of metric and sum of metrics
     */
    private static Map<String, Double> getSum(List<WeatherMetricEntity> metricsBySensor) {
        return metricsBySensor.stream()
                .flatMap(wm -> wm.getMetrics().stream())
                .collect(Collectors.groupingBy(
                        MetricEntity::getMetricName,
                        Collectors.summingDouble(MetricEntity::getMetricValue)
                ));
    }
}
