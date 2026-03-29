package com.example.expenses_tracker_app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ExpenseApi {
    @GET("api/transactions")
    suspend fun getAllExpenses(
        @Query("sortBy") sortBy: String = "date"
    ): List<TransactionDTO>
    @GET("api/transactions/{id}")
    suspend fun getTransactionByID(@Path("id") id: String): TransactionDTO

    @POST("api/transactions/sync")
    suspend fun syncExpenses(@Body Transactions: List<TransactionDTO>): Response<String>
    @GET("api/transactions/exists/{id}")
    suspend fun checkIfTransactionExists(@Path("id") id: String): Response<Boolean>

    @DELETE("api/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): Response<String>
    @POST("api/settings/sync")
    suspend fun syncSettings(@Body categories: List<SettingsDTO>): Response<String>
    @GET("api/settings/")
    suspend fun getSettings(): SettingsDTO
    @PUT("api/transactions/update/{id}")
    suspend fun updateTransaction(
       @Path("id") id: String,
       @Body transaction: TransactionDTO): Response<String>
}




