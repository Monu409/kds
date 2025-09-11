package com.zotto.kds.database.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "order")
class Order() : Parcelable {
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0

  @JsonProperty("restaurant_id")
  @ColumnInfo(name = "restaurant_id")
  var restaurant_id: String? = null

  @JsonProperty("rname")
  @ColumnInfo(name = "rname")
  var rname: String? = null

  @JsonProperty("order_id")
  @ColumnInfo(name = "order_id")
  var order_id: String? = null

  @JsonProperty("order_date")
  @ColumnInfo(name = "order_date")
  var order_date: String? = null

  @JsonProperty("kds_active")
  @ColumnInfo(name = "kds_active")
  var kds_active: String? = null

  @JsonProperty("order_time")
  @ColumnInfo(name = "order_time")
  var order_time: String? = null

  @JsonProperty("pay_method")
  @ColumnInfo(name = "pay_method")
  var pay_method: String? = null

  @JsonProperty("tax")
  @ColumnInfo(name = "tax")
  var tax: String? = null

  @JsonProperty("comments")
  @ColumnInfo(name = "comments")
  var comments: String? = null

  @JsonProperty("chain_order_id")
  @ColumnInfo(name = "chain_order_id")
  var chain_order_id: String? = null

  @JsonProperty("member_id")
  @ColumnInfo(name = "member_id")
  var member_id: String? = null

  @JsonProperty("is_online")
  @ColumnInfo(name = "is_online")
  var online: String? = null

  @JsonProperty("grand_total")
  @ColumnInfo(name = "grand_total")
  var grand_total: String? = null

  @JsonProperty("emp_id")
  @ColumnInfo(name = "emp_id")
  var emp_id: String? = null

  @JsonProperty("split_payment_flag")
  @ColumnInfo(name = "split_payment_flag")
  var split_payment_flag: String? = null

  @JsonProperty("general_discount")
  @ColumnInfo(name = "general_discount")
  var general_discount: String? = null

  @JsonProperty("pos_table_name")
  @ColumnInfo(name = "pos_table_name")
  var pos_table_name: String? = null

  @JsonProperty("pickup")
  @ColumnInfo(name = "pickup")
  var pickup: String? = null

  @JsonProperty("booking_id")
  @ColumnInfo(name = "booking_id")
  var booking_id: String? = null

  @JsonProperty("order_location")
  @ColumnInfo(name = "order_location")
  var order_location: String? = null

  @JsonProperty("delivery_time")
  @ColumnInfo(name = "delivery_time")
  var delivery_time: String? = null

  @JsonProperty("delivery_firstname")
  @ColumnInfo(name = "delivery_firstname")
  var delivery_firstname: String? = null

  @JsonProperty("delivery_lastname")
  @ColumnInfo(name = "delivery_lastname")
  var delivery_lastname: String? = null

  @JsonProperty("order_status")
  @ColumnInfo(name = "order_status")
  var order_status: String? = null

  @JsonProperty("type")
  @ColumnInfo(name = "type")
  var type: String? = null

  @JsonProperty("delivery_zipcode")
  @ColumnInfo(name = "delivery_zipcode")
  var delivery_zipcode: String? = null

  @JsonProperty("delivery_charge")
  @ColumnInfo(name = "delivery_charge")
  var delivery_charge: String? = null

  @JsonProperty("delivery_address")
  @ColumnInfo(name = "delivery_address")
  var delivery_address: String? = null

  @JsonProperty("delivery_city")
  @ColumnInfo(name = "delivery_city")
  var delivery_city: String? = null

  @JsonProperty("delivery_country")
  @ColumnInfo(name = "delivery_country")
  var delivery_country: String? = null

  @JsonProperty("delivery_mobile")
  @ColumnInfo(name = "delivery_mobile")
  var delivery_mobile: String? = null

  @JsonProperty("delivery_email")
  @ColumnInfo(name = "delivery_email")
  var delivery_email: String? = null

  @JsonProperty("paid")
  @ColumnInfo(name = "paid")
  var paid: String? = null

  @JsonProperty("delivery_option")
  @ColumnInfo(name = "delivery_option")
  var delivery_option: String? = null

  @JsonProperty("sequence_order_id")
  @ColumnInfo(name = "sequence_order_id")
  var sequence_order_id: String? = null

  @JsonProperty("order_from")
  @ColumnInfo(name = "order_from")
  var order_from: String? = null

  @JsonProperty("product")
  //@JsonAlias("Items")
  @ColumnInfo(name = "product")
  var products: ArrayList<Product>? = null

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    restaurant_id = parcel.readString()
    rname = parcel.readString()
    order_id = parcel.readString()
    order_date = parcel.readString()
    kds_active = parcel.readString()
    order_time = parcel.readString()
    pay_method = parcel.readString()
    tax = parcel.readString()
    comments = parcel.readString()
    chain_order_id = parcel.readString()
    member_id = parcel.readString()
    online = parcel.readString()
    grand_total = parcel.readString()
    emp_id = parcel.readString()
    split_payment_flag = parcel.readString()
    general_discount = parcel.readString()
    pos_table_name = parcel.readString()
    pickup = parcel.readString()
    booking_id = parcel.readString()
    order_location = parcel.readString()
    delivery_time = parcel.readString()
    delivery_firstname = parcel.readString()
    delivery_lastname = parcel.readString()
    order_status = parcel.readString()
    type = parcel.readString()
    delivery_zipcode = parcel.readString()
    delivery_charge = parcel.readString()
    delivery_address = parcel.readString()
    delivery_city = parcel.readString()
    delivery_country = parcel.readString()
    delivery_mobile = parcel.readString()
    delivery_email = parcel.readString()
    paid = parcel.readString()
    delivery_option = parcel.readString()
    sequence_order_id = parcel.readString()
      order_from = parcel.readString()
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeLong(id)
    parcel.writeString(restaurant_id)
    parcel.writeString(rname)
    parcel.writeString(order_id)
    parcel.writeString(order_date)
    parcel.writeString(kds_active)
    parcel.writeString(order_time)
    parcel.writeString(pay_method)
    parcel.writeString(tax)
    parcel.writeString(comments)
    parcel.writeString(chain_order_id)
    parcel.writeString(member_id)
    parcel.writeString(online)
    parcel.writeString(grand_total)
    parcel.writeString(emp_id)
    parcel.writeString(split_payment_flag)
    parcel.writeString(general_discount)
    parcel.writeString(pos_table_name)
    parcel.writeString(pickup)
    parcel.writeString(booking_id)
    parcel.writeString(order_location)
    parcel.writeString(delivery_time)
    parcel.writeString(delivery_firstname)
    parcel.writeString(delivery_lastname)
    parcel.writeString(order_status)
    parcel.writeString(type)
    parcel.writeString(delivery_zipcode)
    parcel.writeString(delivery_charge)
    parcel.writeString(delivery_address)
    parcel.writeString(delivery_city)
    parcel.writeString(delivery_country)
    parcel.writeString(delivery_mobile)
    parcel.writeString(delivery_email)
    parcel.writeString(paid)
    parcel.writeString(delivery_option)
    parcel.writeString(sequence_order_id)
    parcel.writeString(order_from)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Order> {
    override fun createFromParcel(parcel: Parcel): Order {
      return Order(parcel)
    }

    override fun newArray(size: Int): Array<Order?> {
      return arrayOfNulls(size)
    }
  }


}