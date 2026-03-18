package io.github.gyroskalitz.adherequest.core.processor.impl

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import io.github.gyroskalitz.adherequest.core.io.InPropTypeStringGson
import io.github.gyroskalitz.adherequest.core.io.NoHtmlEscapingGson
import io.github.gyroskalitz.adherequest.core.io.contentEquals
import io.github.gyroskalitz.adherequest.core.processor.TextSaveValuePropertyProcessor
import java.io.File

/**
 * 描述选择题类型的数据结构信息。
 * @property singleSelection 标识该 [SelectionInfo] 是否为单选模式。
 * @property options 该 [SelectionInfo] 中所有可供选择的候选列表。
 */
data class SelectionInfo(val singleSelection: Boolean, val options: List<String>)

/**
 * 选择题类型的实际数据包装类。
 * @property selectedOptions 用户在 [SelectionData] 中已选择的选项列表。
 */
data class SelectionData(val selectedOptions: List<String>)

/**
 * 处理选择题类型数据的 [SelectionPropertyProcessor] 实现类。
 */
open class SelectionPropertyProcessor: TextSaveValuePropertyProcessor()  {
    companion object{
        /**
         * 选择题属性在 [PersonPropertyInfo.valueTypeString] 中的标识符前缀。
         */
        const val SELECTION_TYPE_STRING_PREFIX: String="selection"

        /**
         * 从选择题类型对应的字符串中提取 [SelectionInfo]。
         * @param propTypeString 原始的类型描述字符串。
         * @return 解析成功返回 [SelectionInfo]，解析失败或格式不匹配返回 `null`。
         */
        fun extractSelectionInfo(propTypeString: String): SelectionInfo?{
            return try {
                val secondPart=propTypeString.substring(10)//selection:
                InPropTypeStringGson.fromJson(secondPart, SelectionInfo::class.java)
            }catch (_: Exception){
                null
            }
        }

        /**
         * 根据 [SelectionInfo] 生成标准的选择题类型字符串。
         * @param selectionInfo 选择题的配置信息。
         * @return 带有前缀并序列化了 `SelectionInfo` 的字符串。
         */
        fun generateSelectionTypeString(selectionInfo: SelectionInfo): String{
            return "$SELECTION_TYPE_STRING_PREFIX:${InPropTypeStringGson.toJson(selectionInfo)}"
        }
    }

    override fun formatMatches(propInfo1: PersonPropertyInfo, propInfo2: PersonPropertyInfo): Boolean {
        val targetSelectionInfo=extractSelectionInfo(propInfo1.valueTypeString)
        val dataSelectionInfo=extractSelectionInfo(propInfo2.valueTypeString)
        if (targetSelectionInfo !is SelectionInfo ||dataSelectionInfo !is SelectionInfo) return false
        return targetSelectionInfo.options.contentEquals(dataSelectionInfo.options,false)
    }

    override fun serialize(propInfo: PersonPropertyInfo, value: Any): String {
        return NoHtmlEscapingGson.toJson(value as SelectionData)
    }

    override fun deserialize(
        propInfo: PersonPropertyInfo,
        value: String
    ): Any {
        extractSelectionInfo(propInfo.valueTypeString)?:throw RuntimeException("Invalid selection info")
        val selectionData= NoHtmlEscapingGson.fromJson(value, SelectionData::class.java)
        return selectionData
    }

    override fun typeMatches(propInfo: PersonPropertyInfo): Boolean{
        return propInfo.valueTypeString.startsWith("$SELECTION_TYPE_STRING_PREFIX:")&&extractSelectionInfo(propInfo.valueTypeString)!=null
    }

    override fun generateDefaultProperty(
        propInfo: PersonPropertyInfo,
        workDir: File
    ): PersonProperty {
        return PersonProperty(
            propInfo,
            SelectionData(listOf())
        )
    }

}