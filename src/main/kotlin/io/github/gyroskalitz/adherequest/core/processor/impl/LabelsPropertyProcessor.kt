package io.github.gyroskalitz.adherequest.core.processor.impl

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import io.github.gyroskalitz.adherequest.core.io.NoHtmlEscapingGson
import io.github.gyroskalitz.adherequest.core.processor.TextSaveValuePropertyProcessor
import java.io.File
/**
 * 描述单个标签项的 [LabelDataItem]。
 * @property name 该 `LabelItem` 的名称。
 * @property description 该 `LabelItem` 的详细描述。
 */
data class LabelDataItem(val name: String, val description: String)
/**
 * 标签属性的数据包装类 [LabelsData]。
 * @property imageItems 包含的所有 [LabelDataItem] 列表。
 */
data class LabelsData(val imageItems: List<LabelDataItem>)
/**
 * 处理“标签+描述”类型数据的 [LabelsPropertyProcessor] 实现类。
 */
open class LabelsPropertyProcessor : TextSaveValuePropertyProcessor() {
    companion object {
        /**
         * 标签属性在 `valueTypeString` 中的标识符。
         */
        const val LABELS_TYPE_STRING: String = "labels"
    }

    override fun serialize(propInfo: PersonPropertyInfo, value: Any): String {
        return NoHtmlEscapingGson.toJson(value)
    }

    override fun deserialize(
        propInfo: PersonPropertyInfo,
        value: String
    ): Any {
        return NoHtmlEscapingGson.fromJson(value, LabelsData::class.java)
    }

    override fun typeMatches(propInfo: PersonPropertyInfo): Boolean =propInfo.valueTypeString==LABELS_TYPE_STRING

    override fun generateDefaultProperty(
        propInfo: PersonPropertyInfo,
        workDir: File
    ): PersonProperty {
        return PersonProperty(propInfo, LabelsData(listOf()))
    }

}