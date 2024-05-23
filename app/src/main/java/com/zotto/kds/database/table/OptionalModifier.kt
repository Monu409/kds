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
@Entity(tableName = "optionalmodifier")
class OptionalModifier() :Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JsonProperty("om_item_name")
    @ColumnInfo(name = "om_item_name")
    var om_item_name: String? = null

    @JsonProperty("om_cat_name")
    @ColumnInfo(name = "om_cat_name")
    var om_cat_name: String? = null

    @JsonProperty("om_item_id")
    @ColumnInfo(name = "om_item_id")
    var om_item_id: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        om_item_name = parcel.readString()
        om_cat_name = parcel.readString()
        om_item_id = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(om_item_name)
        parcel.writeString(om_cat_name)
        parcel.writeString(om_item_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OptionalModifier> {
        override fun createFromParcel(parcel: Parcel): OptionalModifier {
            return OptionalModifier(parcel)
        }

        override fun newArray(size: Int): Array<OptionalModifier?> {
            return arrayOfNulls(size)
        }
    }


}