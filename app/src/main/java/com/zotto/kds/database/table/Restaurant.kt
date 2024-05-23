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
@Entity(tableName = "restaurant")
class Restaurant() :Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JsonProperty("username")
    @ColumnInfo(name = "username")
    var username: String? = null

    @JsonProperty("type")
    @ColumnInfo(name = "type")
    var type: String? = null

    @JsonProperty("restaurantid")
    @ColumnInfo(name = "restaurantid")
    var restaurantid: Long? = null

    @JsonProperty("token")
    @ColumnInfo(name = "token")
    var token: String? = null

    @JsonProperty("restaurantname")
    @ColumnInfo(name = "restaurantname")
    var restaurantname: String? = null

    @JsonProperty("ownerid")
    @ColumnInfo(name = "ownerid")
    var ownerid: Long? = null

    @JsonProperty("chain_id")
    @ColumnInfo(name = "chain_id")
    var chain_id: Long? = null

    @JsonProperty("mobile")
    @ColumnInfo(name = "mobile")
    var mobile: String? = null

    @JsonProperty("phone")
    @ColumnInfo(name = "phone")
    var phone: String? = null

    @JsonProperty("access_level")
    @ColumnInfo(name = "access_level")
    var access_level: Int? = null

    @JsonProperty("website")
    @ColumnInfo(name = "website")
    var website: String? = null

    @JsonProperty("pin")
    @ColumnInfo(name = "pin")
    var pin: String? = null

    @JsonProperty("is_logged_in")
    @ColumnInfo(name = "is_logged_in")
    var logged_in: Boolean? = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        username = parcel.readString()
        type = parcel.readString()
        token = parcel.readString()
        restaurantid = parcel.readValue(Long::class.java.classLoader) as? Long
        restaurantname = parcel.readString()
        ownerid = parcel.readValue(Long::class.java.classLoader) as? Long
        chain_id = parcel.readValue(Long::class.java.classLoader) as? Long
        mobile = parcel.readString()
        phone = parcel.readString()
        access_level = parcel.readValue(Int::class.java.classLoader) as? Int
        website = parcel.readString()
        pin = parcel.readString()
        logged_in = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(username)
        parcel.writeString(token)
        parcel.writeString(type)
        parcel.writeValue(restaurantid)
        parcel.writeString(restaurantname)
        parcel.writeValue(ownerid)
        parcel.writeValue(chain_id)
        parcel.writeString(mobile)
        parcel.writeString(phone)
        parcel.writeValue(access_level)
        parcel.writeString(website)
        parcel.writeString(pin)
        parcel.writeValue(logged_in)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }
    }


}