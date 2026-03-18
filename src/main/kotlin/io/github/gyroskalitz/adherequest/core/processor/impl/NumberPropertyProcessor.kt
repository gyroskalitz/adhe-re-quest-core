package io.github.gyroskalitz.adherequest.core.processor.impl

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import java.io.File
/**
 * 处理数字类型的 [NumberPropertyProcessor] 实现类。
 * 注意：考虑到数字的类型比较多样（小数，整数等等），[PersonProperty.value] 使用字符串型储存。
 */
open class NumberPropertyProcessor: PlainTextPropertyProcessor() {
    companion object{
        /**
         * 数字属性在 `valueTypeString` 中的固定标识符。
         */
        const val NUMBER_TYPE_STRING: String = "number"
    }

    override fun generateDefaultProperty(
        propInfo: PersonPropertyInfo,
        workDir: File
    ): PersonProperty {
        return PersonProperty(PersonPropertyInfo(propInfo.name, propertyMatchType), "0")
    }

    override val propertyMatchType:String
        get() = NUMBER_TYPE_STRING

}