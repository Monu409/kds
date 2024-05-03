package com.zotto.kds.model

class Order {
    var orderid:String?=null
    var tablename:String?=null
    var noofperson:String?=null
    var cookingtime:String?=null
    override fun toString(): String {
        return "Order(orderid=$orderid, tablename=$tablename, noofperson=$noofperson, cookingtime=$cookingtime)"
    }

}