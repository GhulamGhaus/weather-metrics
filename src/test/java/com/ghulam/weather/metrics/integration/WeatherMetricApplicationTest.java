package com.ghulam.weather.metrics.integration;

import com.ghulam.weather.metrics.entity.MetricEntity;
import com.ghulam.weather.metrics.entity.WeatherMetricEntity;
import com.ghulam.weather.metrics.repository.WeatherMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherMetricApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherMetricRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void testGetMetricsIntegration() throws Exception {
        // populate data into database to use in API call
        WeatherMetricEntity entity = repository.save
                (WeatherMetricEntity.builder().sensorId("sensor-1").timestamp(LocalDateTime.now())
                        .metrics( List.of(

                                MetricEntity.builder().metricName("Temp").metricValue(34.0).unit("C").build(),
                                MetricEntity.builder().metricName("Humidity").metricValue(57.0).unit("g/kg").build(),
                                MetricEntity.builder().metricName("WindSpeed").metricValue(80.0).unit("g/kg").build())).build()
                        );


        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/weather/metric")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateMetricIntegration() throws Exception {
        String requestBody = """
                 {
                             "sensorId": "sensor-2",
                             "timestamp": "2024-12-13T20:55:16.042614",
                             "metrics": [
                                 {
                                     "metricName": "Temp",
                                     "metricValue": 34.0,
                                     "unit": "C"
                                 },
                                 {
                                     "metricName": "Humidity",
                                     "metricValue": 25.0,
                                     "unit": "g/kg"
                                 },
                                 {
                                     "metricName": "WindSpeed",
                                     "metricValue": 7.0,
                                     "unit": "km/h"
                                 }
                             ]
                         }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/weather/metric")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

    }

}
