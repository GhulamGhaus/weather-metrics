package com.ghulam.weather.metrics.exception;

public class WeatherMetricException extends RuntimeException{

    private String errorCode;
    private String errorMessage;

    public WeatherMetricException(Throwable cause, String errorCode, String errorMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }


    public WeatherMetricException(String errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public WeatherMetricException(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public WeatherMetricException() {
        super();
    }
}
