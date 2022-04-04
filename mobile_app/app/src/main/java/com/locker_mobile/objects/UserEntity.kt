package com.locker_mobile.objects;

public class UserEntity(
    var id: Int,
    var name: String,
    var username: String
) {
    constructor() : this(-1,"","" )
}