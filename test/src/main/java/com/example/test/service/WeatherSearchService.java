package com.example.test.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Поиск погоды по дате с помощью API Visual Crossing
 * (требуется уникальный API ключ)
 */
@Service
public class WeatherSearchService {

    public double getWeatherFromTheInternet() {
        String apiEndPoint = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
        String location = "Saint-Petersburg";
        String startDate = String.valueOf(LocalDate.now());
        String unitGroup = "metric"; //us,metric,uk
        String apiKey = "2YQHS8FJMDLQCDY986D35UL65"; //заменить на полученный при регистрации

        //Build the URL pieces
        StringBuilder requestBuilder = new StringBuilder(apiEndPoint);
        requestBuilder.append(location);

        if (startDate != null && !startDate.isEmpty()) {
            requestBuilder.append("/").append(startDate);
        }

        URIBuilder builder = new URIBuilder(requestBuilder.toString());
        builder.setParameter("unitGroup", unitGroup)
                .setParameter("key", apiKey);

        /**
         * Создание GET-запроса к API
         */

        HttpGet get = new HttpGet(builder.build());

        CloseableHttpClient httpclient = HttpClients.createDefault();

        CloseableHttpResponse response = httpclient.execute(get);

        String rawResult = null;
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.out.printf("Bad response status code:%d%n",
                        response.getStatusLine().getStatusCode());
                return;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                rawResult = EntityUtils.toString(entity, Charset.forName("utf-8"));
            }

        } finally {
            response.close();
        }

        parseTimelineJson(rawResult);

        /**
         * Разбор JSON
         */
        JSONObject timelineResponse = new JSONObject(rawResult);
        ZoneId zoneId = ZoneId.of(timelineResponse.getString("timezone"));
        JSONArray values = timelineResponse.getJSONArray("days");

        System.out.printf("Date\tMaxTemp\tMinTemp\tPrecip\tSource%n");
        for (int i = 0; i < values.length(); i++) {
            JSONObject dayValue = values.getJSONObject(i);

            ZonedDateTime datetime =
                    ZonedDateTime.ofInstant(Instant.ofEpochSecond(
                            dayValue.getLong("datetimeEpoch")), zoneId);

            double maxtemp = dayValue.getDouble("tempmax");
            double mintemp = dayValue.getDouble("tempmin");
            double pop = dayValue.getDouble("precip");
            String source = dayValue.getString("source");
            System.out.printf("%s\t%.1f\t%.1f\t%.1f\t%s%n",
                    datetime.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    maxtemp, mintemp, pop, source);
            return maxtemp;
        }

        return rawResult;
    }
}
