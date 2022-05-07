package com.example.robotcontroller.joystick

import com.example.robotcontroller.data.entities.Limit
import com.example.robotcontroller.data.entities.Rule
import com.example.robotcontroller.data.entities.Universe
import java.lang.Error
import java.lang.StringBuilder

class MicroFRIHandler {

    private val frameByte: Byte = 255.toByte()
    private val okByte: Byte = 10
    private val errByte: Byte = 20

    fun createInitFrame(universeCnt: Int, ruleCnt: Int): ByteArray {
        val frame = "FI$universeCnt:$ruleCnt"

        return replacer(frame)
    }

    fun createUniverses(universes: ArrayList<Universe>, limits: ArrayList<Limit>): ByteArray {
        if (universes.isEmpty()) {
            throw Error("Universe array is empty.")
        }

        val frame = StringBuilder("FU")
        universes.forEachIndexed { indexOfUniverse, universe ->
            val universeLimits = ArrayList<Limit>()
            limits.forEach { limit ->
                run {
                    if (limit.universeId == universe.id) {
                        universeLimits.add(limit)
                    }
                }
            }

            frame.append("$indexOfUniverse:$universeLimits.size")
            frame.append(":")

            for (universelimit in universeLimits) {
                frame.append("${universelimit.minValue}|${universelimit.maxValue}")
                frame.append(":")
            }
        }

        return replacer(frame.toString())
    }

    fun createRule(rules: ArrayList<Rule>): ByteArray {
        if (rules.isEmpty()) {
            throw Error("Rules array is empty.")
        }

        val sb = StringBuilder("FR")
        rules.forEachIndexed { indexOfRule, rule ->
            val sizeOfLimits = rules.size
            sb.append("$indexOfRule:$sizeOfLimits")
            sb.append(":")
            sb.append("${rule.baseUniverseId}")

            sb.append("FE")
            sb.append("1:1:${rule.ruleUniverseId}|${rule.baseLimitId}:${rule.ruleLimitId}")
        }

        return replacer(sb.toString())
    }

    fun sendMoveFrame(angle: Int, strength: Int) {

    }

    fun getResponse(msg: ByteArray): String {
        if (msg.isEmpty() || msg.size < 2) {
            return "Invalid message"
        }

        if (msg[0] != (-1).toByte()) {
            return "Invalid first byte"
        }

        if (msg[1] == okByte) {
            return "OK"
        }

        if (msg[1] == errByte) {
            return "ERROR"
        }

        return String(msg, 1, msg.size)
    }

    private fun replacer(message: String): ByteArray {
        val sb = ArrayList<Byte>();

        for (ch in message.toCharArray()) {
            when (ch) {
                'F' -> {
                    sb.add(255.toByte())
                };
                'I' -> sb.add(1)
                'U' -> sb.add(2)
                'R' -> sb.add(3)
                'E' -> sb.add(4)
                '|', ':' -> {}
                else ->
                    sb.add((ch.code.toByte()-48).toByte())
            }
        }

        return sb.toByteArray()
    }
}