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
@Entity(tableName = "topping")
class Topping() :Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JsonProperty("name")
    @ColumnInfo(name = "name")
    var name: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Topping> {
        override fun createFromParcel(parcel: Parcel): Topping {
            return Topping(parcel)
        }

        override fun newArray(size: Int): Array<Topping?> {
            return arrayOfNulls(size)
        }
    }
}