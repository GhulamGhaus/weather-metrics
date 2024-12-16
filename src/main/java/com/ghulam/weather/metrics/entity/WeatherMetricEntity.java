package com.ghulam.weather.metrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;


@Entity(name = "weather_metric")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherMetricEntity {

    /**
     * Unique identifier for the metric. Auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    /**
     * Sensor identifier that is capturing weather metrics
     */
    @NotNull(message = "Sensor ID cannot be null")
    @NotEmpty(message = "Sensor ID cannot be empty")
    @Valid
    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    /**
     * Timestamp when sensor send it
     */
    @NotNull(message = "Timestamp cannot be null")
    @Valid
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "timestamp")
    private LocalDateTime timestamp;


    /**
     * List of metrics per sensor id, It will be one-to-many relationship with child table and it will Eager fetch, get deleted if sensor data deleted
     */
    @NotNull(message = "Metrics list cannot be null")
    @Size(min = 1, message = "Metrics list must have at least one metric")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "sensor_id")
    List<MetricEntity> metrics;

}