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
@Entity(tableName = "disabledTable")
class DisabledTable() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @JsonProperty("restaurant_id")
    @ColumnInfo(name = "restaurant_id")
    var restaurant_id: String? = null

    @JsonProperty("category_id")
    @ColumnInfo(name = "category_id")
    var category_id: String? = null

    @JsonProperty("status")
    @ColumnInfo(name = "status")
    var status: Int? = 0

    @JsonProperty("type")
    @ColumnInfo(name = "type")
    var type: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        restaurant_id = parcel.readString()
        category_id = parcel.readString()
        status = parcel.readValue(Int::class.java.classLoader) as? Int
        type = parcel.readString()
    }


    override fun toString(): String {
        return "Product(id=$id, category_id=$category_id, status=$status, type=$type)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(restaurant_id)
        parcel.writeString(category_id)
        parcel.writeValue(status)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DisabledTable> {
        override fun createFromParcel(parcel: Parcel): DisabledTable {
            return DisabledTable(parcel)
        }

        override fun newArray(size: Int): Array<DisabledTable?> {
            return arrayOfNulls(size)
        }
    }
}