package com.example.test.repository;

import com.example.test.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
@Repository
public interface WeatherRepository extends JpaRepository<Weather, String> {

    public Weather findWeatherByDate(LocalDate date);
}
