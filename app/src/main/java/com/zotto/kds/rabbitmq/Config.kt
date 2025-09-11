package com.zotto.kds.rabbitmq

class Config {
  companion object {
    val CONN_BROKE = "connectionBroke"
    @JvmField
    val ORDER_NOTIFICATION = "orderNotification111"
    @JvmField
    val LOCAL_IP_ORDER_NOTIFICATION = "local_ip_order"
    val APPUPDATE_NOTIFICATION = "appUpdateNotification0214"
    @JvmField
    val ROBOT_NOTIFICATION = "robotNotification478"
    val NOTIFICATION_ID = 301
    val LOG_STATUS = true
    val HOSTNAME = "api.opushospitalitymanager.com"
//    val USERNAME = "cibo"
//    val PASSWORD = "123456"
    val USERNAME = "opus"
    val PASSWORD = "0pu5@123"
    val ONLINE_EXCHANGE_NAME = "onlineordering"
    val ROUTING_EXCHANGE_NAME = "routing"
    val VERSION_EXCHANGE_NAME = "version.control.app"
  }

}

//http://api.opushospitalitymanager.com:15672