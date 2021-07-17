package com.jcy.letsgohiking.network.adapter

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter

open class RxApiResponseCallAdapter<R>(private val wrapped: CallAdapter<R, *>) :
    CallAdapter<R, Any> {

    @Suppress("UNCHECKED_CAST")
    override fun adapt(call: Call<R>): Any {
        val result = wrapped.adapt(call)

        return when {
            result as? Observable<R> != null -> result
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
            result as? Single<R> != null -> result
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
            else -> result
        }
    }

    override fun responseType() = wrapped.responseType()
}