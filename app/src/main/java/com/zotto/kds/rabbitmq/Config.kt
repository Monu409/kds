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
    val HOSTNAME = "rabbit.ciboapp.com"
    val USERNAME = "cibo"
    val PASSWORD = "123456"
    val ONLINE_EXCHANGE_NAME = "onlineordering"
    val VERSION_EXCHANGE_NAME = "version.control.app"
  }

}