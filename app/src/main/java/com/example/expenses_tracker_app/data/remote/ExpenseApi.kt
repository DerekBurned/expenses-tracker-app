package com.example.expenses_tracker_app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ExpenseApi {
    @POST("api/expenses/sync")
    suspend fun syncExpenses(@Body expenses: List<ExpenseDto>): Response<String>
}