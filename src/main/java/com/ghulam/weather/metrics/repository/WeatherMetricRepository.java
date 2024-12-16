package com.ghulam.weather.metrics.repository;

import com.ghulam.weather.metrics.entity.WeatherMetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface WeatherMetricRepository extends JpaRepository<WeatherMetricEntity, Long>  {

    @Query("SELECT s FROM weather_metric s WHERE s.sensorId IN :sensorIds")
    List<WeatherMetricEntity> findBySensorIds(List<String> sensorIds);

    @Query("SELECT s FROM weather_metric s WHERE s.sensorId IN :sensorIds AND s.timestamp BETWEEN :startTimestamp AND :endTimestamp")
    List<WeatherMetricEntity> findBySensorIdsAndTimestampBetween(List<String> sensorIds,LocalDateTime startTimestamp, LocalDateTime endTimestamp);
    List<WeatherMetricEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
