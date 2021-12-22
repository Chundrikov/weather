#Task Weather service

a REST service with a single endpoint /weather.

When called, this service query a database for today's weather (temperature) in the table "weather_history". If no record is found in the database for the current date, then it must read the current temperature value from page "yandex.ru". After reading temperature, it must insert a new record in "weather_history". In the end, it must return the temperature value back to the user.

Table weather_history has two columns:

weather_date DATE
weather_value VARCHAR

