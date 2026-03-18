package io.github.gyroskalitz.adherequest.core.processor.impl

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import io.github.gyroskalitz.adherequest.core.io.InPropTypeStringGson
import io.github.gyroskalitz.adherequest.core.io.NoHtmlEscapingGson
import io.github.gyroskalitz.adherequest.core.io.contentEquals
import java.io.File
/**
 * 描述排序题类型的数据结构信息。
 * @property items [SorterInfo] 包含的所有待排序项列表。
 */
data class SorterInfo(val items: List<String>){
    /**
     * 将当前的 [SorterInfo] 转换为当前顺序对应的 [SorterData]。
     * @return 包含以原始顺序排序元素的 `SorterData`。
     */
    fun toSorterData()= SorterData(items)
}

/**
 * 排序题类型的实际数据包装类。
 * @property sortedItems [SorterData] 中经过用户排序后的项列表。
 */
data class SorterData(val sortedItems: List<String>)

/**
 * 处理排序题类型数据的 [SorterPropertyProcessor] 实现类。
 */
open class SorterPropertyProcessor: SelectionPropertyProcessor() {
    companion object{
        /**
         * 排序题类型在 [PersonPropertyInfo.valueTypeString] 中的标识符前缀。
         */
        const val SORT_TYPE_STRING_PREFIX: String="sort"
        /**
         * 从排序题类型对应的字符串中提取 [SorterInfo]。
         * @param propTypeString 原始的类型描述字符串。
         * @return 解析成功返回 [SorterInfo]，解析失败或格式不匹配返回 `null`。
         */
        fun extractSortInfo(propTypeString: String): SorterInfo?{
            return try {
                val secondPart=propTypeString.substring(5)
                InPropTypeStringGson.fromJson(secondPart, SorterInfo::class.java)
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
        /**
         * 根据 [SorterInfo] 生成标准的排序类型字符串。
         * @param sorterInfo 排序配置信息。
         * @return 带有前缀并序列化了 `SorterInfo` 的字符串。
         */
        fun generateSorterTypeString(sorterInfo: SorterInfo): String{
            return "$SORT_TYPE_STRING_PREFIX:${InPropTypeStringGson.toJson(SorterInfo(sorterInfo.items))}"
        }
    }


    override fun formatMatches(propInfo1: PersonPropertyInfo, propInfo2: PersonPropertyInfo): Boolean {
        val targetSortInfo=extractSortInfo(propInfo1.valueTypeString)
        val dataSortInfo=extractSortInfo(propInfo2.valueTypeString)
        if (targetSortInfo !is SorterInfo ||dataSortInfo !is SorterInfo) return false
        return targetSortInfo.items.contentEquals(targetSortInfo.items,false)
    }

    override fun typeMatches(propInfo: PersonPropertyInfo): Boolean{
        return propInfo.valueTypeString.startsWith("$SORT_TYPE_STRING_PREFIX:")&&extractSortInfo(propInfo.valueTypeString)!=null
    }


    override fun serialize(propInfo: PersonPropertyInfo, value: Any): String {
        return NoHtmlEscapingGson.toJson(value as SorterData)
    }

    override fun deserialize(
        propInfo: PersonPropertyInfo,
        value: String
    ): Any {
        extractSortInfo(propInfo.valueTypeString)?:throw RuntimeException("Invalid Sort Info")
        return NoHtmlEscapingGson.fromJson(value, SorterData::class.java)
    }

    override fun generateDefaultProperty(propInfo: PersonPropertyInfo, workDir: File): PersonProperty {
        return PersonProperty(
            propInfo,
            extractSortInfo(propInfo.valueTypeString)!!.toSorterData()
        )
    }

}