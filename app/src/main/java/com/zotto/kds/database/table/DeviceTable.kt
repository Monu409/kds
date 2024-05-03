package com.zotto.kds.database.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "deviceTable")
class DeviceTable() : Parcelable {
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0
  @JsonProperty("cn_device_name")
  @ColumnInfo(name = "cn_device_name")
  var cn_device_name: String? = null
  @JsonProperty("device_id")
  @ColumnInfo(name = "device_id")
  var device_id: String? = null
  @JsonProperty("device_name")
  @ColumnInfo(name = "device_name")
  var device_name: String? = null
  @JsonProperty("device_type")
  @ColumnInfo(name = "device_type")
  var device_type: String? = null
  @JsonProperty("image")
  @ColumnInfo(name = "image")
  var image: String? = null
  @JsonProperty("ip_address")
  @ColumnInfo(name = "ip_address")
  var ip_address: String? = null
  @JsonProperty("port")
  @ColumnInfo(name = "port")
  var port: String? = null
  @JsonProperty("pour_type")
  @ColumnInfo(name = "pour_type")
  var pour_type: String? = null
  @JsonProperty("restaurant_id")
  @ColumnInfo(name = "restaurant_id")
  var restaurant_id: String? = null
  @JsonProperty("serial_no")
  @ColumnInfo(name = "serial_no")
  var serial_no: String? = null
  @JsonProperty("status")
  @ColumnInfo(name = "status")
  var status: String? = null
  @JsonProperty("tid")
  @ColumnInfo(name = "tid")
  var tid: String? = null
  @JsonProperty("udid")
  @ColumnInfo(name = "udid")
  var udid: String? = null

  @Ignore
  constructor(device_name:String) : this() {
    this.device_name = device_name
  }
  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    cn_device_name = parcel.readString()
    device_id = parcel.readString()
    device_name = parcel.readString()
    device_type = parcel.readString()
    image = parcel.readString()
    ip_address = parcel.readString()
    port = parcel.readString()
    pour_type = parcel.readString()
    serial_no = parcel.readString()
    status = parcel.readString()
    tid = parcel.readString()
    udid = parcel.readString()
    restaurant_id = parcel.readString()
  }

  override fun toString(): String {
    return "$device_name"
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeLong(id)
    parcel.writeString(cn_device_name)
    parcel.writeString(device_id)
    parcel.writeString(device_name)
    parcel.writeString(device_type)
    parcel.writeString(image)
    parcel.writeString(ip_address)
    parcel.writeString(port)
    parcel.writeString(pour_type)
    parcel.writeString(serial_no)
    parcel.writeString(status)
    parcel.writeString(tid)
    parcel.writeString(udid)
    parcel.writeString(restaurant_id)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<DeviceTable> {
    override fun createFromParcel(parcel: Parcel): DeviceTable {
      return DeviceTable(parcel)
    }

    override fun newArray(size: Int): Array<DeviceTable?> {
      return arrayOfNulls(size)
    }
  }
}