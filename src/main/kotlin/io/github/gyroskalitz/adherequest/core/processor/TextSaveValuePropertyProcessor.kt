package io.github.gyroskalitz.adherequest.core.processor

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import io.github.gyroskalitz.adherequest.core.io.PropertyWorkDirScope
import java.io.File
/**
 * 文本值存储的默认文件名。
 */
const val TextValuePropertyFileName="value.txt"
/**
 * 在 [PropertyWorkDirScope] 作用域下，获取指向文本值文件的 [File] 对象。
 * @param textValueFileName 存储数据的文本值文件的名称。
 */
fun PropertyWorkDirScope.textValueFile(textValueFileName: String=TextValuePropertyFileName): File {
    return File(propertyWorkDir, textValueFileName)
}
/**
 * 从工作目录中提取并读取文本值文件的内容。
 * @param textValueFileName 存储数据的文本值文件的名称。
 * @return 文件中的字符串内容。
 */
fun PropertyWorkDirScope.extractTextValue(textValueFileName: String=TextValuePropertyFileName): String{
    val textFile=textValueFile(textValueFileName)
    return textFile.readText()
}

/**
 * 基于纯文本存储数据的 [PersonPropertyProcessor] 抽象基类。
 * 该处理器通过将[PersonPropertyInfo]序列化为 String 并保存到文本文件中（默认为[TextValuePropertyFileName]）来实现持久化。
 */
abstract class TextSaveValuePropertyProcessor: PersonPropertyProcessor {
    companion object{
        /**
         * 将序列化后的数据保存到工作目录中的文本值文件中。
         * 如果目录或文件不存在，会自动创建。
         * @param text 要保存的序列化后的数据字符串。
         * @param textValueFileName 存储数据的文本值文件的名称。
         */
        fun PropertyWorkDirScope.saveTextValue(text: String, textValueFileName: String=TextValuePropertyFileName){
            if (!propertyWorkDir.exists()) propertyWorkDir.mkdirs()
            val textFile=textValueFile(textValueFileName)
            textFile.createNewFile()
            textFile.writeText(text)
        }
    }

    open val textValueFileName: String
        get() = TextValuePropertyFileName

    /**
     * 将[PersonProperty.value]转换为可存储的字符串格式。
     * @param propInfo 该`Property`的`PropertyInfo`。
     * @param value `Property`带有的值。
     * @return 序列化后的字符串。
     */
    abstract fun serialize(propInfo: PersonPropertyInfo, value: Any):String
    /**
     * 将从文件中读取的字符串反序列化为[PersonPropertyInfo]对应的`Property`的值。
     * @param propInfo 反序列化得到值应该对应`PropertyInfo`的信息。
     * @param value 读取到的字符串内容。
     * @return 反序列化后的，对应`PropertyInfo`的值。
     */
    abstract fun deserialize(propInfo: PersonPropertyInfo, value: String):Any

    override fun formatMatches(propInfo1: PersonPropertyInfo, propInfo2: PersonPropertyInfo): Boolean {
        return propInfo1.valueTypeString==propInfo2.valueTypeString
    }

    override fun load(
        propInfo: PersonPropertyInfo,
        workDir: File
    ): PersonProperty {
        return PropertyWorkDirScope(workDir).run {
            val textValue=extractTextValue(textValueFileName)
            PersonProperty(propInfo,deserialize(propInfo,textValue))
        }
    }

    override fun save(property: PersonProperty, workDir: File) {
        PropertyWorkDirScope(workDir).run {
            val textValue=serialize(property.info,property.value)
            saveMetadata(property.info)
            saveTextValue(textValue,textValueFileName)
        }
    }
}

