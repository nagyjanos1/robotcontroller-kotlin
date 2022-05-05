package com.example.robotcontroller.joystick

import com.example.robotcontroller.data.entities.Limit
import com.example.robotcontroller.data.entities.Rule
import com.example.robotcontroller.data.entities.Universe
import java.lang.Error
import java.lang.StringBuilder

class MicroFRIHandler(private val bSocketService: BluetoothDataSharingService) {

    fun sendUniverseInitFrame(universeCnt: Int, ruleCnt: Int): Boolean {
        val frame = "FI$universeCnt:$ruleCnt"
        val replacedControlSigns = replacer(frame.toString())

        return bSocketService.sendMessageToText(replacedControlSigns.toByteArray())
    }

    fun sendUniverses(universes: Array<Universe>, limits: ArrayList<Limit>): Boolean {
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

        val replacedControlSigns = replacer(frame.toString())

        return bSocketService.sendMessageToText(replacedControlSigns.toString().toByteArray())
    }

    fun sendRule(rules: Array<Rule>): Boolean {
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

        val replacedControlSigns = replacer(sb.toString())

        return bSocketService.sendMessageToText(replacedControlSigns.toByteArray())
    }

    fun replacer(message: String): String {
        val sb = StringBuilder();

        for (ch in message.toCharArray()) {
            when (ch) {
                'F' -> sb.append("255");
                'I' -> sb.append('1')
                'U' -> sb.append('2')
                'R' -> sb.append('3')
                'E' -> sb.append('4')
                '|', ':' -> {}
                else ->
                    sb.append(ch)
            }
        }

        return sb.toString()
    }
}