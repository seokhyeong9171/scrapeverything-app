package com.scrapeverything.app.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedUrlHolder @Inject constructor() {
    var url: String? = null

    fun consume(): String? {
        val value = url
        url = null
        return value
    }
}
