package com.justai.jaicf.context

/**
 * Class that contains a required data for request processing. The instance of this class is available fro each request in [ActionContext].
 * Here are scoped mutable properties available to store the arbitrary user-related data during the scenario execution.
 * You can use this class to store some data in scenarios. BotContext manager is responsible for persisting of this data for you.
 *
 * Usage example:
 *
 * ```
 * state("name") {
 *   activators {
 *     catchAll()
 *   }
 *   action {
 *     context.client["name"] = request.input
 *   }
 * }
 * ```
 *
 * You also can use Kotlin property delegation feature to propagate values from/to [BotContext] in your own model classes:
 *
 * ```
 * class MyContext(context: BotContext) {
 *   var name: String? by context.client
 *   var order: String? by context.session
 * }
 * ```
 *
 * Once you assign some value to name of order, it is propagated to the client and session scoped maps of [BotContext]. You also can use any non-primitive types including your own, just make sure it can be serialized properly.
 *
 * @property clientId a user's identifier
 * @property dialogContext a current state of the dialogue
 * @property client mutable map to persist an arbitrary user-related data. These values would be preserved between different user's sessions.
 * @property session mutable map to persist an arbitrary user's session-related data. These values would be preserved between different user's requests but will be cleaned once the session is ended, expired or cleanSessionData() method was invoked.
 * @property temp a temporary user-related data. These values are available during the single user's requests and should be used mainly to pass values between multiple states in scenario.
 * @property result may contain some arbitrary session-scoped result that is generated by some sub-scenarios. Learn more about sub-scenarios in documentation.
 *
 * @see ActionContext
 * @see com.justai.jaicf.context.manager.BotContextManager
 */
data class BotContext(
    val clientId: String,
    val dialogContext: DialogContext
) {

    // TODO: Change BotContextManager API in further major releases, get rid of this dirty fix.
    constructor(clientId: String) : this(clientId, DialogContext()) {
        temp[BotContextKeys.IS_NEW_USER_KEY] = true
    }

    val client = mutableMapOf<String, Any?>().withDefault { null }
    val session = mutableMapOf<String, Any?>().withDefault { null }
    val temp = mutableMapOf<String, Any?>().withDefault { null }

    var result: Any? = null

    /**
     * Cleans the session-scoped data: [result], [temp] and [session]
     */
    fun cleanSessionData() {
        cleanTempData()
        result = null
        session.clear()
    }

    /**
     * Cleans the request-scoped data: [temp]
     */
    fun cleanTempData() {
        temp.clear()
    }
}

internal object BotContextKeys {
    const val IS_NEW_USER_KEY = "com/justai/jaicf/core/context/isNewUser"
}
