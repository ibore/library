package me.ibore.demo.http.model

data class Weather(
        val cityInfo: CityInfo,
        val `data`: Data,
        val date: String,
        val message: String,
        val status: Int,
        val time: String
)

data class CityInfo(
        val city: String,
        val citykey: String,
        val parent: String,
        val updateTime: String
)

data class Data(
        val forecast: List<Forecast>,
        val ganmao: String,
        val pm10: Double,
        val pm25: Double,
        val quality: String,
        val shidu: String,
        val wendu: String,
        val yesterday: Yesterday
)

data class Forecast(
        val aqi: Int,
        val date: String,
        val fl: String,
        val fx: String,
        val high: String,
        val low: String,
        val notice: String,
        val sunrise: String,
        val sunset: String,
        val type: String,
        val week: String,
        val ymd: String
)

data class Yesterday(
        val aqi: Int,
        val date: String,
        val fl: String,
        val fx: String,
        val high: String,
        val low: String,
        val notice: String,
        val sunrise: String,
        val sunset: String,
        val type: String,
        val week: String,
        val ymd: String
)