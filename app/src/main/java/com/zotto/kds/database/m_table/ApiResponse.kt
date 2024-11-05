package com.zotto.kds.database.m_table

data class ApiResponse(
    val status: Int,
    val data: List<ResCategory>
)

data class ResCategory(
    val category_id: String,
    val cname: String,
    val cn_name: String,
    val restaurant_id: Int,
    val picture: String,
    val thumb: String,
    val status: Int,
    val online: Int,
    val type: String,
    val cat_sort: Int,
    val description: String,
    val cn_description: String,
    val printer_id: Int,
    val printer_type: String,
    val happy_hours_id: String,
    val cat_type: String,
    val inshop_ordering: Int,
    val remote_cat_id: String,
    val is_deleted: Int,
    val chain_owner_id: Int,
    val restaurant_group_id: Int,
    val sku: String,
    val date: String,
    val device_id: Int,
    val is_pour: Int,
    val suggestions: Any?,
    val color_code: Any?,
    val cloud_image_url: Any?
)
