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
@Entity(tableName = "base")
class Base() :Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JsonProperty("pizza_base_id")
    @ColumnInfo(name = "pizza_base_id")
    var pizza_base_id: String? = null

    @JsonProperty("restaurant_id")
    @ColumnInfo(name = "restaurant_id")
    var restaurant_id: String? = null

    @JsonProperty("name")
    @ColumnInfo(name = "name")
    var name: String? = null

    @JsonProperty("cn_name")
    @ColumnInfo(name = "cn_name")
    var cn_name: String? = null

    @JsonProperty("is_deleted")
    @ColumnInfo(name = "is_deleted")
    var deleted: String? = null

    @JsonProperty("image")
    @ColumnInfo(name = "image")
    var image: String? = null

    @JsonProperty("chain_owner_id")
    @ColumnInfo(name = "chain_owner_id")
    var chain_owner_id: String? = null

    @JsonProperty("restaurant_group_id")
    @ColumnInfo(name = "restaurant_group_id")
    var restaurant_group_id: String? = null

    @JsonProperty("sku")
    @ColumnInfo(name = "sku")
    var sku: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        pizza_base_id = parcel.readString()
        restaurant_id = parcel.readString()
        name = parcel.readString()
        cn_name = parcel.readString()
        deleted = parcel.readString()
        image = parcel.readString()
        chain_owner_id = parcel.readString()
        restaurant_group_id = parcel.readString()
        sku = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(pizza_base_id)
        parcel.writeString(restaurant_id)
        parcel.writeString(name)
        parcel.writeString(cn_name)
        parcel.writeString(deleted)
        parcel.writeString(image)
        parcel.writeString(chain_owner_id)
        parcel.writeString(restaurant_group_id)
        parcel.writeString(sku)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Base> {
        override fun createFromParcel(parcel: Parcel): Base {
            return Base(parcel)
        }

        override fun newArray(size: Int): Array<Base?> {
            return arrayOfNulls(size)
        }
    }


}