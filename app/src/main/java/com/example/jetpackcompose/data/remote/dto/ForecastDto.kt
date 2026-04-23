package com.example.jetpackcompose.data.remote.dto

data class CategoryForecastResponse(
    val category_id: Long,
    val predicted_spending: Double,
    val current_spent: Double,
    val budget: Double,
    val budget_used_pct: Double,
    val forecast_usage_pct: Double,
    val status: String,
    val suggestion: String,
    val suggested_daily: Double
)

data class TrendResponse(
    val category_id: Long,
    val population_average: Double,
    val user_spending: Double,
    val deviation_percent: Double,
    val status: String,
    val message: String
)

data class AnomalyResponse(
    val transaction_id: Long,
    val amount: Double,
    val category_name: String,
    val is_anomaly: Boolean,
    val anomaly_score: Double,
    val message: String
)

data class SpendingAlertResponse(
    val alert_date: String,
    val day_of_month: Int,
    val expected_spending: Double,
    val times_higher: Double,
    val category_name: String,
    val message: String,
    val suggestion: String
)

data class AISummaryResponse(
    val weekly_forecast: WeeklyForecastData?,
    val category_forecasts: List<CategoryForecastResponse>,
    val anomaly_count: Int,
    val top_anomalies: List<AnomalyResponse>,
    val upcoming_alerts: List<SpendingAlertResponse>
)

data class WeeklyForecastData(
    val predicted_spending: Double,
    val input_weeks: List<Double>,
    val trend: String,
    val change_percent: Double,
    val remaining_days: Int,
    val is_over_budget: Boolean,
    val warning_message: String
)
