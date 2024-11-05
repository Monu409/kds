package com.zotto.kds.restapi

import com.zotto.kds.database.table.*
import com.zotto.kds.model.BaseUrlModel
import com.zotto.kds.model.OnlineProducts
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {
  @Headers("Content-Type: application/json")
  // @POST("/api/kdsApi/v1/kdsApiSingle/login")
  @POST("/api/kdsLogin")
  fun kdsLogin(@Body body: String?): Observable<GenericResponse<Restaurant>>

  @Headers("Content-Type: application/json")
  //@POST("/api/kdsApi/v1/kdsApiSingle/getOrderItems")
  @POST("/api/getKdsOrders")
  fun getAllOrders(
    @Header("Authorization") token: String?,
    @Body body: String?
  ): Observable<GenericResponse<List<Order>>>

  @GET("/api/getKdsSingleOrder/{restid}/{orderid}")
  fun getSingleOrder(
    @Header("Authorization") token: String?,
    @Path("restid") restid: String?,
    @Path("orderid") orderid: String?
  ): Observable<GenericResponse<Order>>

  @GET("api/getCategoryList/{restid}")
  fun getCategories(
    @Header("Authorization") token: String?,
    @Path("restid") restid: String?
  ): Observable<GenericResponse<List<CategoryTable>>>

  @GET("api/getProductList/{restid}/{catId}")
  suspend fun getProductsByCatId(
    @Header("Authorization") token: String?,
    @Path("restid") restid: String?,
    @Path("catId") catId: String?
  ): Response<OnlineProducts>

  @GET("/api/getDeviceList/{restid}/all")
  fun getDeviceList(
    @Header("Authorization") token: String?,
    @Path("restid") restid: String?
  ): Observable<GenericResponse<List<DeviceTable>>>

  @GET("/api/getRouteDetails/{restid}")
  fun getRouteList(
    @Header("Authorization") token: String?,
    @Path("restid") restid: String?
  ): Observable<GenericResponse<List<RouteTable>>>

  @Headers("Content-Type: application/json")
  @POST("/api/kds-updatestatus")
  fun updateOrder(@Header("Authorization") token: String?, @Body body: String?): Observable<String>

  @Headers("Content-Type: application/json")
  @POST("/api/updateProductStockStatus")
  fun updateItem(@Header("Authorization") token: String?, @Body body: String?): Observable<String>

  @Headers("Content-Type: application/json")
  @POST("/api/updateProductKds")
  fun updateProduct(
    @Header("Authorization") token: String?,
    @Body body: String?
  ): Observable<String>

  @Headers("Content-Type: application/json")
  @POST("/api/updateTicketKds")
  fun updateProductTicket(
    @Header("Authorization") token: String?,
    @Body body: String?
  ): Observable<String>

  @Headers("Content-Type: application/json")
  @POST("/api/posApi/v1/sendRabbitNotificationAsPerRoute")
  fun sendKDStoKDS(
    @Header("Authorization") token: String?,
    @Body body: String?
  ): Observable<String>
//https://demopay.z-pay.co.uk/api/getBaseUrl
  @GET("/api/getBaseUrl")
  fun getBaseUrl(
//    @Header("Authorization") token: String?,
//    @Path("restid") restid: String?
  ): Observable<BaseUrlModel>

}