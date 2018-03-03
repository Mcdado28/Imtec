package com.murtic.adis.techyz

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Imtec_User_Api {

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("user/add")
    fun addUser(@Body user: Imtec_User): Observable<String>

    companion object {
        fun create(): Imtec_User_Api {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://imtec.virtuooza.com/imtecuseri/public/")
                    .build()

            return retrofit.create(Imtec_User_Api::class.java)

        }
    }
}