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
@Entity(tableName = "forcedmodifier")
class ForcedModifier() :Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JsonProperty("fm_item_name")
    @ColumnInfo(name = "fm_item_name")
    var fm_item_name: String? = null

    @JsonProperty("fm_cat_name")
    @ColumnInfo(name = "fm_cat_name")
    var fm_cat_name: String? = null

    @JsonProperty("fm_item_id")
    @ColumnInfo(name = "fm_item_id")
    var fm_item_id: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        fm_item_name = parcel.readString()
        fm_cat_name = parcel.readString()
        fm_item_id = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(fm_item_name)
        parcel.writeString(fm_cat_name)
        parcel.writeString(fm_item_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ForcedModifier> {
        override fun createFromParcel(parcel: Parcel): ForcedModifier {
            return ForcedModifier(parcel)
        }

        override fun newArray(size: Int): Array<ForcedModifier?> {
            return arrayOfNulls(size)
        }
    }


}