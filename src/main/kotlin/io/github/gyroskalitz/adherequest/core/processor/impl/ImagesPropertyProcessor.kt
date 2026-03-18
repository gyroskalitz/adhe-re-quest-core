package io.github.gyroskalitz.adherequest.core.processor.impl

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import io.github.gyroskalitz.adherequest.core.processor.PersonPropertyProcessor
import io.github.gyroskalitz.adherequest.core.processor.TextSaveValuePropertyProcessor
import io.github.gyroskalitz.adherequest.core.io.InPropTypeStringGson
import io.github.gyroskalitz.adherequest.core.io.NoHtmlEscapingGson
import java.io.File

/**
 * 描述图片类型的数据结构信息。
 * @property imageCountRange 定义了该属性允许包含的图片数量范围。
 */
data class ImagesInfo(val imageCountRange: IntRange)

/**
 * 图片类型的实际数据包装类。
 * @property imageItems 包含的所有图片项信息列表。
 */
data class ImagesData(val imageItems: List<ImageDataItem>)
/**
 * 单张图片的详细信息数据模型。
 * @property location 图片的存储路径。
 * @property uriRelative 标识 [location] 是否为相对路径。相对路径是指从[PersonProperty]的工作目录出发。
 * @property description 图片的文字描述。
 */
data class ImageDataItem(val location: String, val uriRelative: Boolean, val description: String)

/**
 * 处理多张图片类型数据的 [PersonPropertyProcessor] 实现类，
 * 通过 JSON 格式在工作目录的文本文件中持久化 [ImagesData] ，图片则储存在工作目录中。
 */
open class ImagesPropertyProcessor : TextSaveValuePropertyProcessor() {
    companion object{
        /**
         * 图片属性在 [PersonPropertyInfo.valueTypeString] 中的标识符前缀。
         */
        const val IMAGES_TYPE_STRING_PREFIX: String="images"
        /**
         * 从图片类型对应的数据字符串中提取 [ImagesInfo] 。
         * @param propTypeString 原始的类型描述字符串。
         * @return 解析成功返回 `ImagesInfo`，解析失败或格式不匹配返回 `null`。
         */
        fun extractImagesInfo(propTypeString: String): ImagesInfo?{
            return try {
                val secondPart=propTypeString.substring(7)
                InPropTypeStringGson.fromJson(secondPart, ImagesInfo::class.java)
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
        /**
         * 根据 [ImagesInfo] 生成标准的图片类型数据对应的字符串。
         * @param imagesInfo 图片配置信息。
         * @return 带有前缀的序列化字符串。
         */
        fun generateImagesTypeString(imagesInfo: ImagesInfo): String{
            return "$IMAGES_TYPE_STRING_PREFIX:${InPropTypeStringGson.toJson(imagesInfo)}"
        }
    }

    /**
     * 校验两个图片属性在格式上是否兼容。
     * 逻辑：当数据源 [propInfo2] 的图片数量范围包含目标 [propInfo1] 的范围时，视为兼容。
     */
    override fun formatMatches(propInfo1: PersonPropertyInfo, propInfo2: PersonPropertyInfo): Boolean {
        val targetRange=extractImagesInfo(propInfo1.valueTypeString)?.imageCountRange
        val dataRange=extractImagesInfo(propInfo2.valueTypeString)?.imageCountRange
        return targetRange!=null&&dataRange!=null
                && (dataRange.contains(targetRange.first) && dataRange.contains(targetRange.last))
    }

    override fun serialize(propInfo: PersonPropertyInfo, value: Any): String {
        return NoHtmlEscapingGson.toJson(value as ImagesData)
    }

    /**
     * 将从文件中读取的字符串反序列化为`propInfo`对应的`Property`的值。
     * 注意：在反序列化过程中，会检验[ImagesInfo]是否正确。
     * @param propInfo 要反序列化得到值对应`PropertyInfo`的信息。
     * @param value 读取到的字符串内容。
     * @return 反序列化后的，对应`PropertyInfo`的值。
     */
    override fun deserialize(
        propInfo: PersonPropertyInfo,
        value: String
    ): Any {
        extractImagesInfo(propInfo.valueTypeString)?:throw RuntimeException("Invalid Images Info")
        return NoHtmlEscapingGson.fromJson(value,ImagesData::class.java)

    }

    override fun typeMatches(propInfo: PersonPropertyInfo): Boolean{
        return propInfo.valueTypeString.startsWith("$IMAGES_TYPE_STRING_PREFIX:")&&extractImagesInfo(propInfo.valueTypeString)!=null
    }

    override fun generateDefaultProperty(
        propInfo: PersonPropertyInfo,
        workDir: File
    ): PersonProperty {
        return PersonProperty(
            propInfo,
            ImagesData(listOf())
        )
    }

}