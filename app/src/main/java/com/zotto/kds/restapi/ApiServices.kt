package com.zotto.kds.restapi

import com.zotto.kds.database.table.DeviceTable
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Restaurant
import io.reactivex.Observable
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

  @GET("/api/getDeviceList/{restid}/all")
  fun getDeviceList(
    @Header("Authorization") token: String?,
    @Path("restid") restid: String?
  ): Observable<GenericResponse<List<DeviceTable>>>

  @Headers("Content-Type: application/json")
  @POST("/api/kds-updatestatus")
  fun updateOrder(@Header("Authorization") token: String?, @Body body: String?): Observable<String>

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
}