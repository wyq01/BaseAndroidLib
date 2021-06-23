package com.ts.base.http

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * DEMO写法
 * 需要自行实现一个ApiService
 *
interface ApiService {

@POST("app/user/login") // 登录接口
fun login(@Body body: LoginContract.Body): Observable<ResultBean<UserInfo>>

@GET("app/data") // 数据接口
fun measureDownload(@Query("timestamp") timestamp: Long): Observable<ResultBean<DownloadData<Measure>>>

@GET("app/point") // point下载
fun pointDownload(@Query("timestamp") timestamp: Long): Observable<ResultBean<DownloadData<Point>>>

@GET("app/hole") // hole下载
fun holeDownload(@Query("timestamp") timestamp: Long): Observable<ResultBean<DownloadData<Hole>>>

@GET
fun upgrade(@Url url: String = "https://rdis.jiancehulian.com/app/version", @Query("packageName") packageName: String): Observable<UpgradeBean>

@Multipart
@POST("app/feedback")
fun upload(@Part params: Array<MultipartBody.Part>): Observable<ResultBean<String>>

@Multipart
@POST("app/feedback")
fun upload(@Part("title") title: RequestBody, @Part("content") content: RequestBody, @Part file: MultipartBody.Part): Observable<ResultBean<String>>

// 正确写法
@Multipart
@POST
fun upload(@Url url: String = "http://zqsb.cn.utools.club/upload", @Part companyId: MultipartBody.Part, @Part file: MultipartBody.Part): Observable<ResultBean<String>>

}
 *
 */
interface FileApiService {

    @Streaming // 大文件
    @GET // 通用下载接口
    fun download(@Url url: String): Observable<ResponseBody>

}