package com.zotto.kds.model

class Summary {
    var name:String?=""
//    var quantity:Int?=(0..4).random()
    var quantity:Int?=0
    var fmname:String?=""
    var detourname:String?=""
    var omname:String?=""
    var toppingname:String?=""
    var productType= ""

    override fun toString(): String {
        return "Summary(name=$name, quantity=$quantity, fmname=$fmname, detourname=$detourname, omname=$omname, toppingname=$toppingname, productType='$productType')"
    }

}