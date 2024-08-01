package com.edison.mulaki.io

class RyanDahl {
    val HTTP = "http://"
    val HTTPS = "https://"

    val PROTOCOL = HTTP

    val IPV4V1 = "192.168.1.111"
    val IPV4V2 = "172.20.10.2"

    val IPV4 = IPV4V1
    val LOCAL = "localhost"
    val MODEM = "88.249.149.217"

    val HOST = IPV4
    val MACHINE = PROTOCOL + HOST

    //MAIN

    val MAIN_PORT = 5000
    val MAIN_URL = MACHINE + ":" + MAIN_PORT

    //SOCKET

    val SOCKET_PORT = 7000
    val SOCKET_URL = MACHINE + ":" + SOCKET_PORT
    val SOCKET_PATH_INBOX = "/socket/inbox"

    //STATIC

    val URL_USER_MEDIA = MAIN_URL + "/users/"
    val URL_POST_MEDIA = MAIN_URL + "/posts/"
    val URL_ROOM_MEDIA = MAIN_URL + "/rooms/"

    //POST

    val API_POST_COUNT = MAIN_URL + "/post/count/"
    val API_POST_LIST = MAIN_URL + "/post/list"
    val API_POST_MORE = MAIN_URL + "/post/list/more"

    val API_ROOM_LIST = MAIN_URL + "/room/list"

    val API_BUBBLE_LIST = MAIN_URL + "/bubble/list"
    val API_BUBBLE_COUNT = MAIN_URL + "/bubble/count"
    val API_BUBBLE_SEND_FILE = MAIN_URL + "/bubble/send/file"
    val API_BUBBLE_SEND = MAIN_URL + "/bubble/send"

    val API_USER_PROFILE = MAIN_URL + "/user/profile"

    val API_AUTH_LOGIN = MAIN_URL + "/auth/login"
    val API_AUTH_REGISTER = MAIN_URL + "/auth/register"


}