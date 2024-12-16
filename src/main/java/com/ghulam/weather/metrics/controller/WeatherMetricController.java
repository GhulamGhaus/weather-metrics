package com.ghulam.weather.metrics.controller;

import com.ghulam.weather.metrics.entity.WeatherMetricEntity;
import com.ghulam.weather.metrics.model.WeatherMetricStatisticModel;
import com.ghulam.weather.metrics.model.WeatherMetricResponse;
import com.ghulam.weather.metrics.service.WeatherMetricService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/weather")
@Slf4j
public class WeatherMetricController {

    private static final String STATISTIC_AVG = "avg";
    private static final Set<String> ALLOWED_METRICS = Set.of("Temp", "Humidity", "WindSpeed");
    private static final Set<String> ALLOWED_STATISTIC = Set.of("min", "max", "avg", "sum");

    @Value("${latestByDays}")
    private long latestByDays;
    @Autowired
    WeatherMetricService weatherMetricService;

    /**
     * API to receives weather metrics data from various sensors
     *
     * @param weatherMetricEntity - Weather metrics input
     * @return - success
     */
    @PostMapping("/metric")
    public ResponseEntity<String> addWeatherMetric(@Validated @RequestBody WeatherMetricEntity weatherMetricEntity) {
        log.info("Received weather metrics at: {}", weatherMetricEntity.getTimestamp());
        WeatherMetricEntity addedResponse = weatherMetricService.saveMetric(weatherMetricEntity);
        return ResponseEntity.ok("Success");
    }

    /**
     * API to get all weather metrics data, You can fetch it for given sensors if not return all available metrics.
     * @param sensorId list of sensor id
     * @return return Weather metrics
     */
    @GetMapping("/metric")
    public ResponseEntity<WeatherMetricResponse> getAllMetrics(@Valid @RequestParam( required = false) List<String> sensorId) {
        log.info("Request to received to get all weather metrics");
        WeatherMetricResponse response =  weatherMetricService.getWeatherActualData(sensorId);
        return ResponseEntity.ok(response);
    }


    /**
     * Give me the statistics(average or min or max or sum) temperature and humidity for sensor 1 and sensor-2 in the last week.
     * Give me the  statistics(average or min or max or sum) of given metricName, sensorId from start date to end date.
     * <p>
     * This method return the Weather statistics for all metrics grouped by sensor id,
     * It will also allow to filter the data based on matching metrics, sensorId
     * By default Avg statistic will be provided
     * It will also accept the statistic ( min or max or sum or avg)
     * Statistic will be calculated based on given date range (start date, end date) if no range input, default latest is by one week.
     * <p>
     * Default latest value can be configured by the user at deployment time in application.yaml
     *
     * @param metricName Metric name, Temp, Humidity, WindSpeed
     * @param sensorId - Sensor Id
     * @param statistic - min, max, sum, avg
     * @param startDate - Date from statistic will be calculated
     * @param endDate  - End Date, Till date statistic will be calculated
     * @return - return list of statistics of Weather Metrics
     */
    @GetMapping("/metric/statistic")
    public ResponseEntity<List<WeatherMetricStatisticModel>> getStatistics(
            @Valid @RequestParam(required = false) List<String> metricName,
            @Valid @RequestParam( required = false) List<String> sensorId,
            @Valid @RequestParam(required = false, defaultValue = STATISTIC_AVG) String statistic,
            @Valid @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Valid @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        LocalDateTime start = startDate==null? LocalDateTime.now().minusDays(latestByDays):startDate;
        LocalDateTime end = endDate==null?LocalDateTime.now():endDate;
        log.info("Retrieving statistic , start date {} to end date {}", start, end);

        if (!ObjectUtils.isEmpty(metricName) && metricName.stream().anyMatch(m -> !ALLOWED_METRICS.contains(m))) {
            throw new IllegalArgumentException("Invalid metric name(s) provided: " + metricName);
        }

        if(!ALLOWED_STATISTIC.contains(statistic)) {
            throw new IllegalArgumentException("Invalid statistic name(s) provided: " + statistic);
        }

        List<WeatherMetricStatisticModel> allMetricStatistics = weatherMetricService.getAllMetricStatistics(metricName, sensorId, statistic, start, end);
        return ResponseEntity.ok(allMetricStatistics);

    }

}
