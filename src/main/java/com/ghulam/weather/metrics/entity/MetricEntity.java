package com.ghulam.weather.metrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "metric")
@Validated
public class MetricEntity {

    /**
     * Primary key, auto generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    /**
     * metric name, what will be Temp, Humidity and WindSpeed
     */
    @Pattern(regexp = "Temp|Humidity|WindSpeed", message = "metricName must be either 'Temp' or 'Humidity' or WindSpeed")
    @NotNull(message = "metricName cannot be null")
    @NotEmpty(message = "metricName cannot be empty")
    @Column(name = "metric_name")
    private String metricName;

    /**
     * Value of metric
     */
    @NotNull(message = "Metric value cannot be null")
    @Valid
    @Column(name = "metric_value")
    private Double metricValue;

    /**
     * Unit of measurement
     * Temp - Celsius
     *  WindSpeed - km/h
     *  Humidity - RH or g/kg
     */
    @Column(name = "unit")
    private String unit;

}
