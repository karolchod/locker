package com.dictionaryapp.objects

data class Word(
    var word1: String,
    var word2: String
) {
    constructor() : this("", "")
}