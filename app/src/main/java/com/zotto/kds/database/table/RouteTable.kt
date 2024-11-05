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
@Entity(tableName = "routeTable")
class RouteTable() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @JsonProperty("rule_id")
    @ColumnInfo(name = "rule_id")
    var rule_id: Int? = null
    @JsonProperty("restaurant_id")
    @ColumnInfo(name = "restaurant_id")
    var restaurant_id: String? = null
    @JsonProperty("device_ids")
    @ColumnInfo(name = "device_ids")
    var device_ids: String? = null
    @JsonProperty("printer_ids")
    @ColumnInfo(name = "printer_ids")
    var printer_ids: String? = null
    @JsonProperty("services")
    @ColumnInfo(name = "services")
    var services: String? = null
    @JsonProperty("category_ids")
    @ColumnInfo(name = "category_ids")
    var category_ids: String? = null
    @JsonProperty("products")
    @ColumnInfo(name = "products")
    var products: String? = null
    @JsonProperty("added_on")
    @ColumnInfo(name = "added_on")
    var added_on: String? = null
    @JsonProperty("updated_on")
    @ColumnInfo(name = "updated_on")
    var updated_on: String? = null
    @JsonProperty("status")
    @ColumnInfo(name = "status")
    var status: String? = null
    @JsonProperty("rule_name")
    @ColumnInfo(name = "rule_name")
    var rule_name: String? = null


    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        rule_id = parcel.readInt()
        restaurant_id = parcel.readString()
        device_ids = parcel.readString()
        printer_ids = parcel.readString()
        services = parcel.readString()
        category_ids = parcel.readString()
        products = parcel.readString()
        added_on = parcel.readString()
        updated_on = parcel.readString()
        status = parcel.readString()
        rule_name = parcel.readString()
    }

    override fun toString(): String {
        return "$rule_name"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(rule_id!!)
        parcel.writeString(restaurant_id)
        parcel.writeString(device_ids)
        parcel.writeString(printer_ids)
        parcel.writeString(services)
        parcel.writeString(category_ids)
        parcel.writeString(products)
        parcel.writeString(added_on)
        parcel.writeString(updated_on)
        parcel.writeString(status)
        parcel.writeString(rule_name)
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