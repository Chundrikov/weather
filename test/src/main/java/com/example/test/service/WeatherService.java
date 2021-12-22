package com.example.test.service;

import com.example.test.entity.Weather;
import com.example.test.repository.WeatherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class WeatherService {

    private WeatherRepository weatherRepository;
    private WeatherSearchService weatherSearchService;

    /**
     * Метод показывает температуру, сохраненную в базе данных weather_history
     * Если записей на указанную дату нет, то выполняется поиск в Яндексе
     * Полученное значение записывается в базу данных, а затем возвращается
     * пользователю
     */
    @Transactional
    public Weather getWeather() {
        Weather result = weatherRepository.findWeatherByDate(LocalDate.now());
        if (result != null) {
            return result;
        } else {
            findWeatherIfNotExist();
        }
        return result;
    }

    @Transactional
    public Weather findWeatherIfNotExist() {
        double temperature = weatherSearchService.getWeatherFromTheInternet();
        Weather newWeather = new Weather(UUID.randomUUID().toString(), temperature, LocalDate.now());
        weatherRepository.save(newWeather);
        return newWeather;
    }
}

