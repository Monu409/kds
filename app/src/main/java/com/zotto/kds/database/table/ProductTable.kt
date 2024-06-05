package com.zotto.kds.database.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "productTable")
class ProductTable() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    
    @JsonProperty("product_id")
    @ColumnInfo(name = "product_id")
    var product_id: String? = null
    
    @JsonProperty("cat_type")
    @ColumnInfo(name = "cat_type")
    var cat_type: String? = null
    
    @JsonProperty("category_id")
    @ColumnInfo(name = "category_id")
    var category_id: String? = null
    
    @JsonProperty("chain_owner_id")
    @ColumnInfo(name = "chain_owner_id")
    var chain_owner_id: String? = null
    
    @JsonProperty("cn_optional_modifer_id")
    @ColumnInfo(name = "cn_optional_modifer_id")
    var cn_optional_modifer_id: String? = null
    
    @JsonProperty("pname")
    @ColumnInfo(name = "pname")
    var pname: String? = null
    
    @JsonProperty("cn_pname")
    @ColumnInfo(name = "cn_pname")
    var cn_pname: String? = null

    @JsonProperty("date")
    @ColumnInfo(name = "date")
    var date: String? = null

    @JsonProperty("picture")
    @ColumnInfo(name = "picture")
    var picture: String? = null

    @JsonProperty("restaurant_id")
    @ColumnInfo(name = "restaurant_id")
    var restaurant_id: String? = null

    @JsonProperty("status")
    @ColumnInfo(name = "status")
    var status: String? = null

    @JsonProperty("thumb")
    @ColumnInfo(name = "thumb")
    var thumb: String? = null

    @JsonProperty("type")
    @ColumnInfo(name = "type")
    var type: String? = null

    @JsonProperty("price")
    @ColumnInfo(name = "price")
    var price: String? = null
    
    @JsonProperty("optional_modifer_id")
    @ColumnInfo(name = "optional_modifer_id")
    var optional_modifer_id: String? = null
    
    @JsonProperty("optional_modifier_id2")
    @ColumnInfo(name = "optional_modifier_id2")
    var optional_modifier_id2: String? = null
    
    @JsonProperty("add_om_id")
    @ColumnInfo(name = "add_om_id")
    var add_om_id: String? = null
    
    @JsonProperty("rem_om_id")
    @ColumnInfo(name = "rem_om_id")
    var rem_om_id: String? = null
    
    @JsonProperty("forced_modifer_id")
    @ColumnInfo(name = "forced_modifer_id")
    var forced_modifer_id: String? = null
    
    @JsonProperty("forced_modifier_id2")
    @ColumnInfo(name = "_pour")
    var forced_modifier_id2: String? = null
    
    @JsonProperty("drink_cat_id")
    @ColumnInfo(name = "drink_cat_id")
    var drink_cat_id: String? = null
    
    @JsonProperty("drink_id")
    @ColumnInfo(name = "drink_id")
    var drink_id: String? = null
    
    @JsonProperty("is_vegan")
    @ColumnInfo(name = "is_vegan")
    var is_vegan: String? = null
    
    @JsonProperty("is_hot")
    @ColumnInfo(name = "is_hot")
    var is_hot: String? = null
    
    @JsonProperty("is_reduced")
    @ColumnInfo(name = "is_reduced")
    var is_reduced: String? = null
    
    @JsonProperty("gluten_free")
    @ColumnInfo(name = "gluten_free")
    var gluten_free: String? = null
    
    @JsonProperty("lactose_free")
    @ColumnInfo(name = "lactose_free")
    var lactose_free: String? = null
    
    constructor(parcel: Parcel) : this() {
        product_id = parcel.readString()
        cat_type = parcel.readString()
        category_id = parcel.readString()
        chain_owner_id = parcel.readString()
        cn_optional_modifer_id = parcel.readString()
        pname = parcel.readString()
        cn_pname = parcel.readString()
        price = parcel.readString()
        date = parcel.readString()
        optional_modifer_id = parcel.readString()
        optional_modifier_id2 = parcel.readString()
        add_om_id = parcel.readString()
        rem_om_id = parcel.readString()
        forced_modifer_id = parcel.readString()
        forced_modifier_id2 = parcel.readString()
        drink_cat_id = parcel.readString()
        picture = parcel.readString()
        drink_id = parcel.readString()
        is_vegan = parcel.readString()
        is_hot = parcel.readString()
        is_reduced = parcel.readString()
        restaurant_id = parcel.readString()
        gluten_free = parcel.readString()
        status = parcel.readString()
        lactose_free = parcel.readString()
        thumb = parcel.readString()
        type = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(prcl: Parcel, p1: Int) {
        prcl.writeString(product_id)
        prcl.writeString(cat_type)
        prcl.writeString(category_id)
        prcl.writeString(chain_owner_id)
        prcl.writeString(cn_optional_modifer_id)
        prcl.writeString(pname)
        prcl.writeString(cn_pname)
        prcl.writeString(price)
        prcl.writeString(date)
        prcl.writeString(optional_modifer_id)
        prcl.writeString(optional_modifier_id2)
        prcl.writeString(add_om_id)
        prcl.writeString(rem_om_id)
        prcl.writeString(forced_modifer_id)
        prcl.writeString(forced_modifier_id2)
        prcl.writeString(drink_cat_id)
        prcl.writeString(picture)
        prcl.writeString(drink_id)
        prcl.writeString(is_vegan)
        prcl.writeString(is_hot)
        prcl.writeString(is_reduced)
        prcl.writeString(restaurant_id)
        prcl.writeString(gluten_free)
        prcl.writeString(status)
        prcl.writeString(lactose_free)
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