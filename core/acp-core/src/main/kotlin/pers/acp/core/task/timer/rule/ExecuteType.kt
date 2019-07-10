package pers.acp.core.task.timer.rule

import pers.acp.core.exceptions.EnumValueUndefinedException
import java.util.HashMap

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
enum class ExecuteType {

    WeekDay,
    Weekend,
    All;

    companion object {

        private var nameMap: MutableMap<String, ExecuteType> = HashMap()

        init {
            for (type in values()) {
                nameMap[type.name.toUpperCase()] = type
            }
        }

        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): ExecuteType {
            if (nameMap.containsKey(name.toLowerCase())) {
                return nameMap.getValue(name.toLowerCase())
            }
            throw EnumValueUndefinedException(ExecuteType::class.java, name)
        }
    }

}