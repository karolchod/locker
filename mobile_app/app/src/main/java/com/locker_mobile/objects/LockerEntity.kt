package com.locker_mobile.objects

class LockerEntity(
    var id: Int,
    var name: String,
    var emptyBoxes: String
) {
    constructor() : this(-1, "", "")
}