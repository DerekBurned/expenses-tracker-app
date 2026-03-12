package com.example.expenses_tracker_app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExpenseApi {
    @GET("api/expenses")
    suspend fun getAllExpenses(
        @Query("sortBy") sortBy: String = "date"
    ): List<TransactionDTO>

    @POST("api/expenses/sync")
    suspend fun syncExpenses(@Body expenses: List<TransactionDTO>): Response<String>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<String>
    @POST("api/categories/sync")
    suspend fun syncSettings(@Body categories: List<SettingsDTO>): Response<String>
}