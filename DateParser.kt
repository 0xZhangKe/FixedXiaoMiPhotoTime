package com.zhangke.fuckmiui

import java.text.SimpleDateFormat

interface DateParser {

    fun parse(text: String): Long?
}

/**
 * xxx20161223xxx220301xxx.jpg
 */
class StringDateParser: DateParser{

    private val regex = "[0-9]{8}.*[0-9]{6}".toRegex()
    private val format = SimpleDateFormat("yyyyMMdd-HHmmss")

    override fun parse(text: String): Long? {
        val result = regex.find(text)?.value ?: return null
        val date = result.substring(0, 8)
        val time = result.substring(result.length - 6, result.length)
        return format.parse("$date-$time").time
    }
}

/**
 * xxxxx1643180090066xxxxx.jpg
 */
class TimestampDateParser: DateParser{

    private val regex = "[0-9]{13}".toRegex()

    override fun parse(text: String): Long? {
        val result = regex.find(text)?.value ?: return null
        return result.toLong()
    }
}