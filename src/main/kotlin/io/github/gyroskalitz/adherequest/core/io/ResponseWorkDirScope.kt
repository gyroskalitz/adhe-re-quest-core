package io.github.gyroskalitz.adherequest.core.io

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import io.github.gyroskalitz.adherequest.core.QuestionnaireData
import io.github.gyroskalitz.adherequest.core.ResponseData
import io.github.gyroskalitz.adherequest.core.processor.PersonPropertyProcessor
import java.io.File

/**
 * `Response Metadata`在`Response`工作目录中对应的 JSON 文件名称。
 */
private const val ResponseMetadataJsonFileName="response_metadata.json"
/**
 * 用于管理[ResponseData]工作目录的作用域。
 *
 * 该类负责通过文件系统的访问`Response`。主要用于处理`Response`相关的序列化和反序列化，
 * 以及`Questionnaire`中各个[QuestionnaireData]的回答数据（[PersonProperty]）。
 *
 * @property responseWorkDir 该`Response`对应的工作目录。
 */
open class ResponseWorkDirScope(val responseWorkDir: File) {
    /**
     * 当尝试加载的回答[PersonProperty]与[ResponseData.questionnaire]中记录的格式不匹配时抛出的异常。
     *
     * @property answerFormat 预期的定义信息，即[QuestionnaireData]中的版本。
     * @property dataPropertyInfo 实际从[PersonProperty]工作目录中`Metadata`文件中读取到的版本。
     */
    class PropertyAnswerFormatNotMatchException(val answerFormat: PersonPropertyInfo,val dataPropertyInfo: PersonPropertyInfo): RuntimeException(
        "Answer format is not match for Data format."
    )

    /**
     * 从指定目录加载一个已回答问题对应的数据（[PersonProperty]）。
     *
     * 该方法会检查 [dataPropertyWorkDir] 中的`Metadata`，确认其格式是否与 [answerFormat] 匹配，
     * 然后利用 [answerPropertyProcessor] 加载实际的`Property`。
     *
     * @param answerFormat 预期的`Property`定义信息。
     * @param dataPropertyWorkDir 该`Property`数据存放的工作目录。
     * @param answerPropertyProcessor 用于处理该`Property`的`PropertyProcessor`。
     * @return 加载完成的 `PersonProperty` 实例。
     * @throws PropertyAnswerFormatNotMatchException 文件中的元数据与预期格式不符时抛出。
     */
    open fun loadAnswer(answerFormat: PersonPropertyInfo, dataPropertyWorkDir: File, answerPropertyProcessor: PersonPropertyProcessor): PersonProperty {
        return PropertyWorkDirScope(dataPropertyWorkDir).run {
            val dataPropertyInfo= extractPropertyInfo()
            if (!answerPropertyProcessor.formatMatches(answerFormat,dataPropertyInfo)) throw PropertyAnswerFormatNotMatchException(answerFormat,dataPropertyInfo)
            answerPropertyProcessor.load(answerFormat,dataPropertyWorkDir)
        }
    }
    /**
     * 将一个回答属性（[PersonProperty]）保存到其对应的工作目录。
     *
     * 如果目标目录或`Property Metadata`文件不存在，该方法会自动创建它们。随后委托 [answerPropertyProcessor] 执行具体的保存逻辑。
     *
     * @param answer 要保存的`Property`实例。
     * @param dataPropertyWorkDir 该`Property`对应的工作目录。
     * @param answerPropertyProcessor 用于处理该`Property`的`PropertyProcessor`。
     */
    open fun saveAnswer(answer: PersonProperty, dataPropertyWorkDir: File, answerPropertyProcessor: PersonPropertyProcessor){
        PropertyWorkDirScope(dataPropertyWorkDir).run {
            if (!dataPropertyWorkDir.exists()) dataPropertyWorkDir.mkdirs()
            val metadataFile= metadataFile()
            if (!metadataFile.isFile) {
                metadataFile.createNewFile()
                metadataFile.writeText(NoHtmlEscapingGson.toJson(answer.info))
            }
            answerPropertyProcessor.save(answer,dataPropertyWorkDir)
        }
    }
    /**
     * 获取指向存储 [ResponseData] 的 JSON 文件对应的 [File] 对象。
     *
     * @return `ResponseData Metadata`对应文件的引用。
     */
    open fun metadataFile()=File(responseWorkDir,ResponseMetadataJsonFileName)
    /**
     * 将完整的 [ResponseData] 保存到工作目录中。
     *
     * @param responseData 要保存的`ResponseData`。
     */
    open fun saveMetaData(responseData: ResponseData) {
        if (!responseWorkDir.exists()) responseWorkDir.mkdirs()
        val responseMetadataJsonFile=metadataFile()
        if (!responseMetadataJsonFile.exists()) responseMetadataJsonFile.createNewFile()
        responseMetadataJsonFile.writeText(NoHtmlEscapingGson.toJson(responseData))
    }
    /**
     * 从工作目录中读取并解析`ResponseData Metadata`。
     *
     * @return 解析出的 [ResponseData] 实例。
     */
    open fun extractResponseData(): ResponseData {
        return NoHtmlEscapingGson.fromJson(metadataFile().readText(),ResponseData::class.java)
    }
    /**
     * 从`ResponseData Metadata`中提取关联的[QuestionnaireData]。
     *
     * @return 该`ResponseData`持有的 `QuestionnaireData`。
     */
    open fun loadQuestionnaireData(): QuestionnaireData {
        return extractResponseData().questionnaire
    }
}