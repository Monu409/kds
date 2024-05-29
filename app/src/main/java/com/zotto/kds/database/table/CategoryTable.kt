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
@Entity(tableName = "categoryTable")
class CategoryTable() : Parcelable {
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0
  @JsonProperty("cat_sort")
  @ColumnInfo(name = "cat_sort")
  var cat_sort: String? = null
  @JsonProperty("cat_type")
  @ColumnInfo(name = "cat_type")
  var cat_type: String? = null
  @JsonProperty("category_id")
  @ColumnInfo(name = "category_id")
  var category_id: String? = null
  @JsonProperty("chain_owner_id")
  @ColumnInfo(name = "chain_owner_id")
  var chain_owner_id: String? = null
  @JsonProperty("cn_description")
  @ColumnInfo(name = "cn_description")
  var cn_description: String? = null
  @JsonProperty("cn_name")
  @ColumnInfo(name = "cn_name")
  var cn_name: String? = null
  @JsonProperty("cname")
  @ColumnInfo(name = "cname")
  var cname: String? = null
  @JsonProperty("color_code")
  @ColumnInfo(name = "color_code")
  var color_code: String? = null
  @JsonProperty("date")
  @ColumnInfo(name = "date")
  var date: String? = null
  @JsonProperty("description")
  @ColumnInfo(name = "description")
  var description: String? = null
  @JsonProperty("device_id")
  @ColumnInfo(name = "device_id")
  var device_id: String? = null
  @JsonProperty("happy_hours_id")
  @ColumnInfo(name = "happy_hours_id")
  var happy_hours_id: String? = null
  @JsonProperty("inshop_ordering")
  @ColumnInfo(name = "inshop_ordering")
  var inshop_ordering: String? = null
  @JsonProperty("is_deleted")
  @ColumnInfo(name = "is_deleted")
  var _deleted: String? = null
  @JsonProperty("is_pour")
  @ColumnInfo(name = "_pour")
  var _pour: String? = null
  @JsonProperty("online")
  @ColumnInfo(name = "online")
  var online: String? = null
  @JsonProperty("picture")
  @ColumnInfo(name = "picture")
  var picture: String? = null
  @JsonProperty("printer_id")
  @ColumnInfo(name = "printer_id")
  var printer_id: String? = null
  @JsonProperty("printer_type")
  @ColumnInfo(name = "printer_type")
  var printer_type: String? = null
  @JsonProperty("remote_cat_id")
  @ColumnInfo(name = "remote_cat_id")
  var remote_cat_id: String? = null
  @JsonProperty("restaurant_group_id")
  @ColumnInfo(name = "restaurant_group_id")
  var restaurant_group_id: String? = null
  @JsonProperty("restaurant_id")
  @ColumnInfo(name = "restaurant_id")
  var restaurant_id: String? = null
  @JsonProperty("sku")
  @ColumnInfo(name = "sku")
  var sku: String? = null
  @JsonProperty("status")
  @ColumnInfo(name = "status")
  var status: String? = null
  @JsonProperty("suggestions")
  @ColumnInfo(name = "suggestions")
  var suggestions: String? = null
  @JsonProperty("thumb")
  @ColumnInfo(name = "thumb")
  var thumb: String? = null
  @JsonProperty("type")
  @ColumnInfo(name = "type")
  var type: String? = null

  constructor(parcel: Parcel) : this() {
    cat_sort = parcel.readString()
    cat_type = parcel.readString()
    category_id = parcel.readString()
    chain_owner_id = parcel.readString()
    cn_description = parcel.readString()
    cn_name = parcel.readString()
    cname = parcel.readString()
    color_code = parcel.readString()
    date = parcel.readString()
    description = parcel.readString()
    device_id = parcel.readString()
    happy_hours_id = parcel.readString()
    inshop_ordering = parcel.readString()
    _deleted = parcel.readString()
    _pour = parcel.readString()
    online = parcel.readString()
    picture = parcel.readString()
    printer_id = parcel.readString()
    printer_type = parcel.readString()
    remote_cat_id = parcel.readString()
    restaurant_group_id = parcel.readString()
    restaurant_id = parcel.readString()
    sku = parcel.readString()
    status = parcel.readString()
    suggestions = parcel.readString()
    thumb = parcel.readString()
    type = parcel.readString()
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(prcl: Parcel, p1: Int) {
    prcl.writeString(cat_sort)
    prcl.writeString(cat_type)
    prcl.writeString(category_id)
    prcl.writeString(chain_owner_id)
    prcl.writeString(cn_description)
    prcl.writeString(cn_name)
    prcl.writeString(cname)
    prcl.writeString(color_code)
    prcl.writeString(date)
    prcl.writeString(description)
    prcl.writeString(device_id)
    prcl.writeString(happy_hours_id)
    prcl.writeString(inshop_ordering)
    prcl.writeString(_deleted)
    prcl.writeString(_pour)
    prcl.writeString(online)
    prcl.writeString(picture)
    prcl.writeString(printer_id)
    prcl.writeString(printer_type)
    prcl.writeString(remote_cat_id)
    prcl.writeString(restaurant_group_id)
    prcl.writeString(restaurant_id)
    prcl.writeString(sku)
    prcl.writeString(status)
    prcl.writeString(suggestions)
    prcl.writeString(thumb)
    prcl.writeString(type)
  }

  companion object CREATOR : Parcelable.Creator<CategoryTable> {
    override fun createFromParcel(parcel: Parcel): CategoryTable {
      return CategoryTable(parcel)
    }

    override fun newArray(size: Int): Array<CategoryTable?> {
      return arrayOfNulls(size)
    }
  }
}