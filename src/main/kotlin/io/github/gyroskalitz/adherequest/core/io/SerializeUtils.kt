package io.github.gyroskalitz.adherequest.core.io

import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * 用于序列化[PersonPropertyInfo.valueTypeString]的[Gson]实例。
 * 该`Gson`实例不会使用换行来创建紧凑的 JSON 字符串输出。
 */
val InPropTypeStringGson: Gson= GsonBuilder().disableHtmlEscaping().create()

/**
 * 用于序列化多数对象的[Gson]实例。
 * 该`Gson`实例会使用换行来创建结构清晰的 JSON 字符串输出。
 */
val NoHtmlEscapingGson: Gson= GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

/**
 * 对比两个字符串列表的内容是否相同。
 * @param this 第一个字符串列表。
 * @param other 第二个字符串列表。
 * @param withOrder 是否需要考虑顺序。
 * @return 如果两个列表的内容相同，则返回`true`，否则返回`false`。
 */
fun List<String>.contentEquals(other: List<String>, withOrder: Boolean=true): Boolean {
    if (size!=other.size) return false
    if (withOrder){
        for (i in indices) {
            if (this[i] != other[i]) {
                return false
            }
        }
        return true
    }else{
        val otherSet=other.toSet()
        return all { it in otherSet }
    }

}