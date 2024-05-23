package com.zotto.kds.database.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.zotto.kds.database.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "product")
class Product() :Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JsonProperty("order_product_id")
    @ColumnInfo(name = "order_product_id")
    var order_product_id: String? = null

    @JsonProperty("order_id")
    @ColumnInfo(name = "order_id")
    var order_id: String? = null

    @JsonProperty("product_id")
    @ColumnInfo(name = "product_id")
    var product_id: String? = null

    @JsonProperty("quantity")
    @JsonAlias("qty")
    @ColumnInfo(name = "quantity")
    var quantity: Int? = 0

    @JsonProperty("size")
    @ColumnInfo(name = "size")
    var size: String? = null

    @JsonProperty("size_value")
    @ColumnInfo(name = "size_value")
    var size_value: String? = null

    @JsonProperty("price")
    @ColumnInfo(name = "price")
    var price: String? = null

    @JsonProperty("offer_amount")
    @ColumnInfo(name = "offer_amount")
    var offer_amount: String? = null

    @JsonProperty("pizza_base_id")
    @ColumnInfo(name = "pizza_base_id")
    var pizza_base_id: String? = null

    @JsonProperty("forced_modifier_ids")
    @ColumnInfo(name = "forced_modifier_ids")
    var forced_modifier_ids: String? = null

    @JsonProperty("optional_modifier_ids")
    @ColumnInfo(name = "optional_modifier_ids")
    var optional_modifier_ids: String? = null

    @JsonProperty("is_detour")
    @ColumnInfo(name = "is_detour")
    var detouris: String? = "0"

    @JsonProperty("detours")
    @ColumnInfo(name = "detours")
    var detours: String? = null

    @JsonProperty("restaurant_id")
    @ColumnInfo(name = "restaurant_id")
    var restaurant_id: String? = null

    @JsonProperty("remove_addons")
    @ColumnInfo(name = "remove_addons")
    var remove_addons: String? = null

    @JsonProperty("type")
    @ColumnInfo(name = "type")
    var type: String? = null

    @JsonProperty("sub_total")
    @ColumnInfo(name = "sub_total")
    var sub_total: String? = null

    @JsonProperty("topping_id_x1")
    @ColumnInfo(name = "topping_id_x1")
    var topping_id_x1: String? = null

    @JsonProperty("topping_id_x2")
    @ColumnInfo(name = "topping_id_x2")
    var topping_id_x2: String? = null

    @JsonProperty("first_half_toppings")
    @ColumnInfo(name = "first_half_toppings")
    var first_half_toppings: String? = null

    @JsonProperty("second_half_toppings")
    @ColumnInfo(name = "second_half_toppings")
    var second_half_toppings: String? = null

    @JsonProperty("points")
    @ColumnInfo(name = "points")
    var points: String? = null

    @JsonProperty("device_id")
    @ColumnInfo(name = "device_id")
    var device_id: String? = null

    @JsonProperty("update_status")
    @ColumnInfo(name = "update_status")
    var update_status: String? = null

    @JsonProperty("setmenu_pizza_id")
    @ColumnInfo(name = "setmenu_pizza_id")
    var setmenu_pizza_id: String? = null

    @JsonProperty("setmenu_dish_id")
    @ColumnInfo(name = "setmenu_dish_id")
    var setmenu_dish_id: String? = null

    @JsonProperty("setmenu_drink_id")
    @ColumnInfo(name = "setmenu_drink_id")
    var setmenu_drink_id: String? = null

    @JsonProperty("setmenu_items_data")
    @ColumnInfo(name = "setmenu_items_data")
    var setmenu_items_data: String? = null

    @JsonProperty("cooking_instruction")
    @ColumnInfo(name = "cooking_instruction")
    var cooking_instruction: String? = null

    @JsonProperty("unique_id")
    @ColumnInfo(name = "unique_id")
    var unique_id: String? = null

    @JsonProperty("tax_total")
    @ColumnInfo(name = "tax_total")
    var tax_total: String? = null

    @JsonProperty("tax_sub_total")
    @ColumnInfo(name = "tax_sub_total")
    var tax_sub_total: String? = null

    @JsonProperty("vat")
    @ColumnInfo(name = "vat")
    var vat: String? = null

    @JsonProperty("service_tax")
    @ColumnInfo(name = "service_tax")
    var service_tax: String? = null

    @JsonProperty("local_tax")
    @ColumnInfo(name = "local_tax")
    var local_tax: String? = null

    @JsonProperty("other_tax")
    @ColumnInfo(name = "other_tax")
    var other_tax: String? = null

    @JsonProperty("pre_payment_flag")
    @ColumnInfo(name = "pre_payment_flag")
    var pre_payment_flag: String? = null

    @JsonProperty("happy_hours_id")
    @ColumnInfo(name = "happy_hours_id")
    var happy_hours_id: String? = null

    @JsonProperty("topping_serving_size")
    @ColumnInfo(name = "topping_serving_size")
    var topping_serving_size: String? = null

    @JsonProperty("remote_product_id")
    @ColumnInfo(name = "remote_product_id")
    var remote_product_id: String? = null

    @JsonProperty("ticket_id")
    @ColumnInfo(name = "ticket_id")
    var ticket_id: String? = null

    @JsonProperty("ticket_status")
    @ColumnInfo(name = "ticket_status")
    var ticket_status: String? = null

    @JsonProperty("serial_no")
    @ColumnInfo(name = "serial_no")
    var serial_no: String? = null

    @JsonProperty("is_update")
    @ColumnInfo(name = "is_update")
    var update: String? = null

    @JsonProperty("timestamp")
    @ColumnInfo(name = "timestamp")
    var timestamp: String? = null

    @JsonProperty("status")
    @ColumnInfo(name = "status")
    var status: String? = null

    @JsonProperty("preparation_time")
    @ColumnInfo(name = "preparation_time")
    var preparation_time: String? = null

    @JsonProperty("isrefund")
    @ColumnInfo(name = "isrefund")
    var refund: String? = null

    @JsonProperty("is_deposit")
    @ColumnInfo(name = "is_deposit")
    var deposit: String? = null

    @JsonProperty("total_deposit")
    @ColumnInfo(name = "total_deposit")
    var total_deposit: String? = null

    @JsonProperty("is_open")
    @ColumnInfo(name = "is_open")
    var open: String? = null

    @JsonProperty("is_stock")
    @ColumnInfo(name = "is_stock")
    var stock: String? = null

    @JsonProperty("stock_updated_on")
    @ColumnInfo(name = "stock_updated_on")
    var stock_updated_on: String? = null

    @JsonProperty("name")
    @ColumnInfo(name = "name")
    var name: String? = null

    @JsonProperty("printer_id")
    @ColumnInfo(name = "printer_id")
    var printer_id: String? = null

    @JsonProperty("printer_type")
    @ColumnInfo(name = "printer_type")
    var printer_type: String? = null

    @JsonProperty("printer_id2")
    @ColumnInfo(name = "printer_id2")
    var printer_id2: String? = null

    @JsonProperty("printer_type2")
    @ColumnInfo(name = "printer_type2")
    var printer_type2: String? = null

    @JsonProperty("food_category")
    @ColumnInfo(name = "food_category")
    var food_category: String? = null

    @TypeConverters(ForcedModifierConverters::class)
    @JsonProperty("fm")
    @ColumnInfo(name = "fm")
    var fm: ArrayList<ForcedModifier>? = ArrayList()

    @TypeConverters(OptionalModifierConverter::class)
    @JsonProperty("om")
    @ColumnInfo(name = "om")
    var om: ArrayList<OptionalModifier>? = ArrayList()

    @TypeConverters(DetourConverters::class)
    @JsonProperty("detour")
    @ColumnInfo(name = "detour")
    var detour: ArrayList<Detour>? = ArrayList()

    @TypeConverters(DetourOmConverters::class)
    @JsonProperty("dom")
    @ColumnInfo(name = "dom")
    var detourom: ArrayList<DetourOm>? = ArrayList()

    @TypeConverters(BaseConverters::class)
    @JsonProperty("base")
    @ColumnInfo(name = "base")
    var base: ArrayList<Base>? = ArrayList()

    @TypeConverters(ToppingConverters::class)
    @JsonProperty("topping")
    @ColumnInfo(name = "topping")
    var topping: ArrayList<Topping>? = ArrayList()

    @JsonProperty("fmname")
    @ColumnInfo(name = "fmname")
    var fmname: String? = ""

    @JsonProperty("omname")
    @ColumnInfo(name = "omname")
    var omname: String? = ""

    @JsonProperty("detourname")
    @ColumnInfo(name = "detourname")
    var detourname: String? = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        order_product_id = parcel.readString()
        order_id = parcel.readString()
        product_id = parcel.readString()
        quantity = parcel.readValue(Int::class.java.classLoader) as? Int
        size = parcel.readString()
        size_value = parcel.readString()
        price = parcel.readString()
        offer_amount = parcel.readString()
        pizza_base_id = parcel.readString()
        forced_modifier_ids = parcel.readString()
        optional_modifier_ids = parcel.readString()
        detouris = parcel.readString()
        detours = parcel.readString()
        restaurant_id = parcel.readString()
        remove_addons = parcel.readString()
        type = parcel.readString()
        sub_total = parcel.readString()
        topping_id_x1 = parcel.readString()
        topping_id_x2 = parcel.readString()
        first_half_toppings = parcel.readString()
        second_half_toppings = parcel.readString()
        points = parcel.readString()
        device_id = parcel.readString()
        update_status = parcel.readString()
        setmenu_pizza_id = parcel.readString()
        setmenu_dish_id = parcel.readString()
        setmenu_drink_id = parcel.readString()
        setmenu_items_data = parcel.readString()
        cooking_instruction = parcel.readString()
        unique_id = parcel.readString()
        tax_total = parcel.readString()
        tax_sub_total = parcel.readString()
        vat = parcel.readString()
        service_tax = parcel.readString()
        local_tax = parcel.readString()
        other_tax = parcel.readString()
        pre_payment_flag = parcel.readString()
        happy_hours_id = parcel.readString()
        topping_serving_size = parcel.readString()
        remote_product_id = parcel.readString()
        ticket_id = parcel.readString()
        ticket_status = parcel.readString()
        serial_no = parcel.readString()
        update = parcel.readString()
        timestamp = parcel.readString()
        status = parcel.readString()
        preparation_time = parcel.readString()
        refund = parcel.readString()
        deposit = parcel.readString()
        total_deposit = parcel.readString()
        open = parcel.readString()
        stock = parcel.readString()
        stock_updated_on = parcel.readString()
        name = parcel.readString()
        printer_id = parcel.readString()
        printer_type = parcel.readString()
        printer_id2 = parcel.readString()
        printer_type2 = parcel.readString()
        food_category = parcel.readString()
        fmname = parcel.readString()
        omname = parcel.readString()
        detourname = parcel.readString()
    }

    override fun toString(): String {
        return "Product(id=$id, order_product_id=$order_product_id, order_id=$order_id, product_id=$product_id, quantity=$quantity, size=$size, size_value=$size_value, price=$price, offer_amount=$offer_amount, pizza_base_id=$pizza_base_id, forced_modifier_ids=$forced_modifier_ids, optional_modifier_ids=$optional_modifier_ids, detouris=$detouris, detours=$detours, restaurant_id=$restaurant_id, remove_addons=$remove_addons, type=$type, sub_total=$sub_total, topping_id_x1=$topping_id_x1, topping_id_x2=$topping_id_x2, first_half_toppings=$first_half_toppings, second_half_toppings=$second_half_toppings, points=$points, device_id=$device_id, update_status=$update_status, setmenu_pizza_id=$setmenu_pizza_id, setmenu_dish_id=$setmenu_dish_id, setmenu_drink_id=$setmenu_drink_id, setmenu_items_data=$setmenu_items_data, cooking_instruction=$cooking_instruction, unique_id=$unique_id, tax_total=$tax_total, tax_sub_total=$tax_sub_total, vat=$vat, service_tax=$service_tax, local_tax=$local_tax, other_tax=$other_tax, pre_payment_flag=$pre_payment_flag, happy_hours_id=$happy_hours_id, topping_serving_size=$topping_serving_size, remote_product_id=$remote_product_id, ticket_id=$ticket_id, ticket_status=$ticket_status, serial_no=$serial_no, update=$update, timestamp=$timestamp, status=$status, preparation_time=$preparation_time, refund=$refund, deposit=$deposit, total_deposit=$total_deposit, open=$open, stock=$stock, stock_updated_on=$stock_updated_on, name=$name, printer_id=$printer_id, printer_type=$printer_type, printer_id2=$printer_id2, printer_type2=$printer_type2, food_category=$food_category, fm=$fm, om=$om, detour=$detour, detourom=$detourom, base=$base, topping=$topping, fmname=$fmname, omname=$omname, detourname=$detourname)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(order_product_id)
        parcel.writeString(order_id)
        parcel.writeString(product_id)
        parcel.writeValue(quantity)
        parcel.writeString(size)
        parcel.writeString(size_value)
        parcel.writeString(price)
        parcel.writeString(offer_amount)
        parcel.writeString(pizza_base_id)
        parcel.writeString(forced_modifier_ids)
        parcel.writeString(optional_modifier_ids)
        parcel.writeString(detouris)
        parcel.writeString(detours)
        parcel.writeString(restaurant_id)
        parcel.writeString(remove_addons)
        parcel.writeString(type)
        parcel.writeString(sub_total)
        parcel.writeString(topping_id_x1)
        parcel.writeString(topping_id_x2)
        parcel.writeString(first_half_toppings)
        parcel.writeString(second_half_toppings)
        parcel.writeString(points)
        parcel.writeString(device_id)
        parcel.writeString(update_status)
        parcel.writeString(setmenu_pizza_id)
        parcel.writeString(setmenu_dish_id)
        parcel.writeString(setmenu_drink_id)
        parcel.writeString(setmenu_items_data)
        parcel.writeString(cooking_instruction)
        parcel.writeString(unique_id)
        parcel.writeString(tax_total)
        parcel.writeString(tax_sub_total)
        parcel.writeString(vat)
        parcel.writeString(service_tax)
        parcel.writeString(local_tax)
        parcel.writeString(other_tax)
        parcel.writeString(pre_payment_flag)
        parcel.writeString(happy_hours_id)
        parcel.writeString(topping_serving_size)
        parcel.writeString(remote_product_id)
        parcel.writeString(ticket_id)
        parcel.writeString(ticket_status)
        parcel.writeString(serial_no)
        parcel.writeString(update)
        parcel.writeString(timestamp)
        parcel.writeString(status)
        parcel.writeString(preparation_time)
        parcel.writeString(refund)
        parcel.writeString(deposit)
        parcel.writeString(total_deposit)
        parcel.writeString(open)
        parcel.writeString(stock)
        parcel.writeString(stock_updated_on)
        parcel.writeString(name)
        parcel.writeString(printer_id)
        parcel.writeString(printer_type)
        parcel.writeString(printer_id2)
        parcel.writeString(printer_type2)
        parcel.writeString(food_category)
        parcel.writeString(fmname)
        parcel.writeString(omname)
        parcel.writeString(detourname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }


}