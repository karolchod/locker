package com.dictionaryapp.objects

data class ParcelEntity(
    var id: Int,
    var box_id: Int,
    var user_sender_id: Int,
    var user_recipient_id: Int,
    var createddate: String,
    var isfinished: Boolean,

    var locker_id: Int,
    var locker_name: String,
    var user_sender_name: String,
    var user_sender_username: String,
    var user_recipient_name: String,
    var user_recipient_username: String
) {
    constructor() : this(-1,-1,-1,-1,"",false,-1,"","","","","" )
}