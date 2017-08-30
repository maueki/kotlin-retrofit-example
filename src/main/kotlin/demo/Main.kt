package demo

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.google.gson.GsonBuilder
import com.google.gson.FieldNamingPolicy

import java.util.Date
import java.math.BigDecimal
import java.util.logging.Logger
import java.net.Proxy
import java.net.InetSocketAddress

import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable

interface YahooService {
@GET("yql?format=json")
        fun yqlQuery(
                        @Query("q") query: String,
                        @Query("env") env: String) : Single<YahooStockResult>
}

class RetrofitYahooServiceFactory {

        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

//        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("proxy address", 8080))
//        val client = OkHttpClient.Builder().addInterceptor(interceptor).proxy(proxy).build()
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

        val retrofit = Retrofit.Builder()
                        .client(client)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl("https://query.yahooapis.com/v1/public/")
                        .build()

        fun create(): YahooService {
                return retrofit.create(YahooService::class.java)
        }
}

data class YahooStockResult(
        val query: YahooStockQuery
)

data class YahooStockQuery (
        val count: Int,
        val created: Date,
        val results: YahooStockResults
)

data class YahooStockResults (
        val quote: List<YahooStockQuote>
)

data class YahooStockQuote (
        val symbol: String,
        val name: String,
        val lastTradePriceOnly: BigDecimal,
        val daysLow: BigDecimal,
        val daysHigh: BigDecimal,
        val volume: String
)

fun main(args: Array<String>) {
        val logger = Logger.getLogger("app")

        val yahooService = RetrofitYahooServiceFactory().create()

        val query = "select * from yahoo.finance.quote where symbol in ('YHOO', 'AAPL', 'GOOG', 'MSFT')"
        val env = "store://datatables.org/alltableswithkeys"

        yahooService.yqlQuery(query,env)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(Schedulers.single())
                        .toObservable()
                        .map { r -> r.query.results.quote }
                        .flatMap { r -> Observable.fromIterable(r) }
//                        .observeOn(Schedulers.single())
                        .subscribe { data ->
                                logger.info(data.symbol + ": " + data.lastTradePriceOnly)
                        }

        println("Hello World!")
}
