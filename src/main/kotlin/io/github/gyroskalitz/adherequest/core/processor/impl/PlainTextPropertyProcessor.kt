package io.github.gyroskalitz.adherequest.core.processor.impl

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import io.github.gyroskalitz.adherequest.core.processor.TextSaveValuePropertyProcessor
import java.io.File
/**
 * 处理纯文本类型数据的 [PlainTextPropertyProcessor] 实现类。
 */
open class PlainTextPropertyProcessor: TextSaveValuePropertyProcessor() {
    companion object{
        /**
         * 纯文本属性在 `valueTypeString` 中的标识符。
         */
        const val PLAIN_TEXT_TYPE_STRING: String = "plain_text"
    }
    override fun formatMatches(propInfo1: PersonPropertyInfo, propInfo2: PersonPropertyInfo): Boolean {
        return propInfo1.valueTypeString==propertyMatchType&&propInfo2.valueTypeString==propertyMatchType
    }

    /**
     * 当前[PlainTextPropertyProcessor]支持处理的[PersonPropertyInfo.valueTypeString]字符串。
     */
    open val propertyMatchType:String
        get() = PLAIN_TEXT_TYPE_STRING

    override fun serialize(
        propInfo: PersonPropertyInfo,
        value: Any
    ): String{
        return value.toString()
    }

    override fun deserialize(
        propInfo: PersonPropertyInfo,
        value: String
    ): String =value

    override fun typeMatches(propInfo: PersonPropertyInfo): Boolean =propInfo.valueTypeString==propertyMatchType
    override fun generateDefaultProperty(
        propInfo: PersonPropertyInfo,
        workDir: File
    ): PersonProperty {
        return PersonProperty(propInfo, "")
    }

}