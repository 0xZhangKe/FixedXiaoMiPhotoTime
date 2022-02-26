package com.zhangke.fuckmiui

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun main() {
    var directory: String? = null
    do {
        println("Please enter a folder")
        val line = readLine() ?: continue
        val file = File(line)
        if (!file.exists()) {
            System.err.println("folder does not exist")
            continue
        }
        if (!file.isDirectory) {
            System.err.println("this is not a folder")
            continue
        }
        directory = line
    } while (directory == null)
    FuckMIUI().process(File(directory))
}

class FuckMIUI {

    private val parserList = listOf(StringDateParser(), TimestampDateParser())
    private val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun process(directory: File) {
        val formatDate = Date()
        val currentTime = formatDate.time
        val failedList = mutableListOf<File>()
        var maxTime = Pair("", Long.MIN_VALUE)
        var minTime = Pair("", Long.MAX_VALUE)
        directory.listFiles()?.forEach {
            val time = it.getRealCreatedTime()
            if (time == null) {
                failedList += it
                println("Bad name:${it.nameWithoutExtension}")
                return@forEach
            }
            if (checkAbnormalTime(it, time, currentTime)) return@forEach
            formatDate.time = time
            it.setLastModified(time)
            println("${it.nameWithoutExtension} -> ${outputFormat.format(formatDate)}")
            if (time >= maxTime.second) {
                maxTime = Pair(it.name, time)
            }
            if (time <= minTime.second) {
                minTime = Pair(it.name, time)
            }
        }
        printResult(failedList, maxTime, minTime)
    }

    private fun printResult(failedList: List<File>, max: Pair<String, Long>, min: Pair<String, Long>) {
        val formatDate = Date()
        println()
        println("Bad name files:")
        failedList.forEach { println(it.name) }
        println()
        formatDate.time = max.second
        println("Max time file:${max.first}, time is ${outputFormat.format(formatDate)}")
        formatDate.time = min.second
        println("Min time file:${min.first}, time is ${outputFormat.format(formatDate)}")
    }

    private fun checkAbnormalTime(file: File, time: Long, currentTime: Long): Boolean {
        return when {
            time <= 0 -> {
                processLongLongAgoFile(file, time, currentTime)
                true
            }
            time > currentTime -> {
                processAfterFile(file, time, currentTime)
                true
            }
            else -> false
        }
    }

    private fun processAfterFile(file: File, time: Long, currentTime: Long) {
        println("We calculated ths file ${file.name} is modified in future(${outputFormat.format(time)}), what do you want?")
        println("\t1. Keep the original time:${outputFormat.format(Date(file.lastModified()))}.")
        println("\t2. Change to ${outputFormat.format(Date(time))}.")
        println("\t3. Change to ${outputFormat.format(Date(currentTime))}.")
        var number: Int? = null
        do {
            val line = readLine()
            if (line == null || line.trim().isEmpty()) {
                continue
            }
            number = line.toIntOrNull()
            if (number == null) {
                println("Please enter a number")
            }
        } while (number == null)
        when (number) {
            2 -> file.setLastModified(time)
            3 -> file.setLastModified(0)
        }
    }

    private fun processLongLongAgoFile(file: File, time: Long, currentTime: Long) {
        println("We calculated ths file ${file.name} was modified longer than the Big Bang(1970-1-1 00:00:00), what do you want?")
        println("\t1. Keep the original time:${outputFormat.format(Date(file.lastModified()))}.")
        println("\t2. Change to ${outputFormat.format(Date(time))}.")
        println("\t3. Change to 1970-1-1 00:00:00.")
        var number: Int? = null
        do {
            val line = readLine()
            if (line == null || line.trim().isEmpty()) {
                continue
            }
            number = line.toIntOrNull()
            if (number == null) {
                println("Please enter a number")
            }
        } while (number == null)
        when (number) {
            2 -> file.setLastModified(time)
            3 -> file.setLastModified(0)
        }
    }

    private fun File.getRealCreatedTime(): Long? {
        val text = nameWithoutExtension
        parserList.forEach {
            val time = it.parse(text)
            if (time != null) return time
        }
        return null
    }
}