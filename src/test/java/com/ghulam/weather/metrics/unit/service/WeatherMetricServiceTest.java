package com.ghulam.weather.metrics.unit.service;

import com.ghulam.weather.metrics.entity.MetricEntity;
import com.ghulam.weather.metrics.entity.WeatherMetricEntity;
import com.ghulam.weather.metrics.model.WeatherMetricResponse;
import com.ghulam.weather.metrics.model.WeatherMetricStatisticModel;
import com.ghulam.weather.metrics.repository.WeatherMetricRepository;
import com.ghulam.weather.metrics.service.WeatherMetricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class WeatherMetricServiceTest {


    @Mock
    private WeatherMetricRepository weatherMetricRepository;

    @InjectMocks
    private WeatherMetricService weatherMetricService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMetric() {
        // Arrange
        WeatherMetricEntity mockEntity = WeatherMetricEntity.builder()
                .sensorId("sensor-1")
                .timestamp(LocalDateTime.now())
                .metrics(List.of(MetricEntity.builder().metricName("Temp").metricValue(2.0).unit("C").build()))
                .build();
        when(weatherMetricRepository.save(mockEntity)).thenReturn(mockEntity);

        // Act
        WeatherMetricEntity result = weatherMetricService.saveMetric(mockEntity);

        // Assert
        assertEquals(mockEntity, result);
        verify(weatherMetricRepository, times(1)).save(mockEntity);
    }

    @Test
    void testGetWeatherActualData_WithSensorIds() {
        // Arrange
        List<String> sensorIds = List.of("sensor-1", "sensor-2");
        List<WeatherMetricEntity> mockData = List.of(
                WeatherMetricEntity.builder().sensorId("sensor-1").build(),
                WeatherMetricEntity.builder().sensorId("sensor-2").build()
        );
        when(weatherMetricRepository.findBySensorIds(sensorIds)).thenReturn(mockData);

        // Act
        WeatherMetricResponse result = weatherMetricService.getWeatherActualData(sensorIds);

        // Assert
        assertEquals(2, result.getWeatherMetrics().size());
        verify(weatherMetricRepository, times(1)).findBySensorIds(sensorIds);
    }

    @Test
    void testGetWeatherActualData_NoSensorIds() {
        // Arrange
        List<WeatherMetricEntity> mockData = List.of(
                WeatherMetricEntity.builder().sensorId("sensor-1").build(),
                WeatherMetricEntity.builder().sensorId("sensor-2").build()
        );
        when(weatherMetricRepository.findAll()).thenReturn(mockData);

        // Act
        WeatherMetricResponse result = weatherMetricService.getWeatherActualData(null);

        // Assert
        assertEquals(2, result.getWeatherMetrics().size());
        verify(weatherMetricRepository, times(1)).findAll();
    }

    @Test
    void testGetAllMetricStatistics_WithDateRange() {
        // Arrange
        List<WeatherMetricEntity> mockData = List.of(
                WeatherMetricEntity.builder()
                        .sensorId("sensor-1")
                        .timestamp(LocalDateTime.now())
                        .metrics(List.of(MetricEntity.builder().metricName("Temp").metricValue(2.0).unit("C").build()))
                        .build()
        );
        List<String> sensorIds = List.of("sensor-1");
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(weatherMetricRepository.findBySensorIdsAndTimestampBetween(sensorIds, startDate, endDate))
                .thenReturn(mockData);

        // Act
        List<WeatherMetricStatisticModel> result = weatherMetricService.getAllMetricStatistics(
                List.of("temp"), sensorIds, "avg", startDate, endDate);

        // Assert
        assertEquals(0, result.size());

    }

    @Test
    void testCalculateStatistics_Average() {
        // Arrange
        List<WeatherMetricEntity> mockData = List.of(
                WeatherMetricEntity.builder()
                        .sensorId("sensor-1")
                        .timestamp(LocalDateTime.now())
                        .metrics(List.of(
                                MetricEntity.builder().metricName("Temp").metricValue(27.0).unit("C").build(),
                                MetricEntity.builder().metricName("Humidity").metricValue(50.0).unit("g/kg").build(),
                                MetricEntity.builder().metricName("WindSpeed").metricValue(67.0).unit("km/h").build()))
                        .build()
        );

        // Act
        List<WeatherMetricStatisticModel> result = weatherMetricService.calculateStatics(mockData, "avg");

        // Assert
        assertEquals(1, result.size());


    }

    @Test
    void testCalculateStatistics_Min() {
        // Arrange
        List<WeatherMetricEntity> mockData = List.of(
                WeatherMetricEntity.builder()
                        .sensorId("sensor-1")
                        .timestamp(LocalDateTime.now())
                        .metrics(List.of(
                                MetricEntity.builder().metricName("Temp").metricValue(25.0).unit("C").build(),
                                MetricEntity.builder().metricName("Temp").metricValue(30.0).unit("c").build(),
                                MetricEntity.builder().metricName("WindSpeed").metricValue(50.0).unit("km/h").build()))
                        .build()
        );

        // Act
        List<WeatherMetricStatisticModel> result = weatherMetricService.calculateStatics(mockData, "min");

        // Assert
        assertEquals(1, result.size());


    }
}
