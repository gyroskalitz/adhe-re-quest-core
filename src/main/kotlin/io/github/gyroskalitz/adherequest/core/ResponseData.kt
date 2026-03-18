package io.github.gyroskalitz.adherequest.core

/**
 * `Questionnaire`对应`Response`的结构定义。
 * 记录了特定用户针对某份 [QuestionnaireData] 填写的信息。
 *
 * @property respondentName 填写者的名称。起辅助作用，主要看填写者的ID（[respondentId]）。
 * @property respondentId 填写者的ID。
 * @property questionnaire 对应的[QuestionnaireData]。
 * @property createdEditTimeStamp 首次创建的时间戳。
 * @property lastEditTimeStamp 最后一次修改的时间戳。
 * @property answerStatusList 对应问卷中每个问题的`AnswerStatus`列表。列表顺序与 [QuestionnaireData.questions] 对应。
 */
data class ResponseData(
    val respondentName: String,
    val respondentId: String,
    val questionnaire: QuestionnaireData,
    val createdEditTimeStamp: Long,
    val lastEditTimeStamp: Long,
    val answerStatusList: List<AnswerStatus>
)


/**
 * 单个[QuestionnaireData]对应的回答状态的枚举。
 * 该类用于指示 [QuestionnaireData.questions] 在具体位置中的回答状态。
 */
enum class AnswerStatus {
    /**
     * 表示该[QuestionnaireData]尚未被回答。
     */
    NoAnswerYet,
    /**
     * 表示该[QuestionnaireData]已被正确回答。
     */
    Answered,
    /**
     * 表示该[QuestionnaireData]被回答者（[ResponseData.respondentId]）认为需要留空。
     */
    MarkAsBlank,
    /**
     * 表示该[QuestionnaireData]在回答者（[ResponseData.respondentId]）处无法正常回答。
     */
    ErrorCountered
}