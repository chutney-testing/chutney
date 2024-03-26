package com.chutneytesting.kotlin.dsl

// Based on chutney.actions file

/**
 * Always success
 */
fun ChutneyStepBuilder.SuccessAction(outputs: Map<String, Any> = mapOf(), validations: Map<String, Any> = emptyMap()) {
    implementation = ChutneyStepImpl(
        type = "success",
        outputs = outputs,
        validations = validations
    )
}

/**
 * Always fail
 */
fun ChutneyStepBuilder.FailAction(outputs: Map<String, Any> = mapOf(), validations: Map<String, Any> = emptyMap()) {
    implementation = ChutneyStepImpl(
        type = "fail",
        outputs = outputs,
        validations = validations
    )
}

/**
 * Log values in the execution context
 */
fun ChutneyStepBuilder.DebugAction(filters: List<String> = listOf(), outputs: Map<String, Any> = mapOf(), validations: Map<String, Any> = emptyMap()) {
    implementation = ChutneyStepImpl(
        type = "debug",
        inputs = listOf(
            "filters" to filters
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Wait "5 min", "300 sec", "500 ms" or "until 14:34"
 */
fun ChutneyStepBuilder.SleepAction(duration: String, outputs: Map<String, Any> = mapOf(), validations: Map<String, Any> = emptyMap()) {
    implementation = ChutneyStepImpl(
        type = "sleep",
        inputs = mapOf("duration" to duration),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Add variable to execution context
 */
fun ChutneyStepBuilder.ContextPutAction(entries: Map<String, Any>, outputs: Map<String, Any> = mapOf(), validations: Map<String, Any> = emptyMap()) {
    implementation = ChutneyStepImpl(
        type = "context-put",
        inputs = mapOf("entries" to entries),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Add a final action, execute at the end of the execution in a teardown final step
 */
fun ChutneyStepBuilder.FinalAction(
    name: String,
    type: String,
    target: String? = null,
    inputs: Map<String, Any> = emptyMap(),
    strategyType: String? = null,
    strategyProperties: Map<String, Any> = emptyMap(),
    validations: Map<String, Any> = emptyMap(),
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "final",
        target = target,
        inputs = listOf(
            "name" to name,
            "type" to type,
            "inputs" to inputs,
            "strategy-type" to strategyType,
            "strategy-properties" to strategyProperties,
            "validations" to validations
        ).notEmptyToMap(),
        outputs = outputs)
}

/**
 * Execute a groovy script
 * -------
 * Outputs:
 * - Key/value returned by the script (outputs = (Map<String, Object>) script.run())
 */
fun ChutneyStepBuilder.GroovyAction(
    script: String,
    parameters: Map<String, Any> = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "groovy",
        inputs = listOf(
            "script" to script,
            "parameters" to parameters
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On an amqp target, bind a queue to an exchange
 * -------
 * Outputs:
 * - queueName : queue name created
 */
fun ChutneyStepBuilder.AmqpCreateBoundTemporaryQueueAction(
    target: String,
    exchangeName: String,
    routingKey: String,
    queueName: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-create-bound-temporary-queue",
        target = target,
        inputs = listOf(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            "queue-name" to queueName
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On an amqp target, delete a queue
 */
fun ChutneyStepBuilder.AmqpDeleteQueueAction(
    target: String,
    queueName: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-delete-queue",
        target = target,
        inputs = listOf(
            "queue-name" to queueName
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On an amqp target, unbind a queue from an exchange
 */
fun ChutneyStepBuilder.AmqpUnbindQueueAction(
    target: String,
    exchangeName: String,
    routingKey: String,
    queueName: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-unbind-queue",
        target = target,
        inputs = listOf(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            "queue-name" to queueName
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On an amqp target, publish a message
 * -------
 * Outputs:
 * - payload : the payload sent  (String)
 * - headers : the headers sent [key1,value1];[key2,value2]... (String)
 */
fun ChutneyStepBuilder.AmqpBasicPublishAction(
    target: String,
    exchangeName: String,
    routingKey: String,
    headers: Map<String, Any>,
    properties: Map<String, String>,
    payload: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-basic-publish",
        target = target,
        inputs = listOf(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            "headers" to headers,
            "properties" to properties,
            "payload" to payload
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On an amqp target, consume messages
 * -------
 * Outputs:
 * - body : the messages consumed (List<Map<String, Object>>), the 2 key of each map are "payload" and "headers")
 * - payload : the payloads consumed  (List<Map<String, Object>> if json, List<String> if not)
 * - headers : the headers consumed  (List<Map<String, Object>>)
 */
fun ChutneyStepBuilder.AmqpBasicConsumeAction(
    target: String,
    queueName: String,
    nbMessages: Int? = null,
    timeout: String? = null,
    selector: String? = null,
    ack: Boolean? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-basic-consume",
        target = target,
        inputs = listOf(
            "queue-name" to queueName,
            "nb-messages" to nbMessages,
            "timeout" to timeout,
            "selector" to selector,
            "ack" to ack
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On an amqp target, get a single message from a queue
 * -------
 * Outputs:
 * - message : the message (com.rabbitmq.client.GetResponse)
 * - body : the payload as String  (String)
 * - headers : the headers as Map  (Map<String, Object>)
 */
fun ChutneyStepBuilder.AmqpBasicGetAction(
    target: String,
    queueName: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-basic-get",
        target = target,
        inputs = listOf(
            "queue-name" to queueName
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On an amqp target, purge messages from queues
 */
fun ChutneyStepBuilder.AmqpCleanQueuesAction(
    target: String,
    queueNames: List<String>,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-clean-queues",
        target = target,
        inputs = listOf("queue-names" to queueNames).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Start an amqp server
 * -------
 * Outputs:
 * - qpidLauncher : the systemLauncher of the qpid server (org.apache.qpid.server.SystemLauncher)
 * -------
 * Finally action registered : QpidServerStopAction
 */
fun ChutneyStepBuilder.QpidServerStartAction(
    initConfig: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "qpid-server-start",
        inputs = listOf("init-config" to initConfig).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * On a mongoDb target, count documents
 * -------
 * Outputs:
 * - count : the count value (long)
 */
fun ChutneyStepBuilder.MongoCountAction(
    target: String,
    collection: String,
    query: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-count",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "query" to query
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a mongoDb target, delete documents
 * -------
 * Outputs:
 * - deletedCount : the number of deleted document (long)
 */
fun ChutneyStepBuilder.MongoDeleteAction(
    target: String,
    collection: String,
    query: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-delete",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "query" to query
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a mongoDb target, find documents
 * -------
 * Outputs:
 * - documents : the list as json of documents (List<String>)
 */
fun ChutneyStepBuilder.MongoFindAction(
    target: String,
    collection: String,
    query: String,
    limit: Int? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-find",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "query" to query,
            "limit" to limit
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a mongoDb target, insert a document
 */
fun ChutneyStepBuilder.MongoInsertAction(
    target: String,
    collection: String,
    document: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-insert",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "document" to document
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a mongoDb target, update documents
 * -------
 * Outputs:
 * - modifiedCount : the number of updated documents (long)
 */
fun ChutneyStepBuilder.MongoUpdateAction(
    target: String,
    collection: String,
    filter: String,
    update: String,
    arraysFilter: List<String> = listOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-update",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "filter" to filter,
            "update" to update,
            "arraysFilter" to arraysFilter
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a mongoDb target, list collections
 * -------
 * Outputs:
 * - collectionNames : the list of collection names (List<String>)
 */
fun ChutneyStepBuilder.MongoListAction(
    target: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-list",
        target = target,
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a http target, GET http call
 * -------
 * Outputs:
 * - status : http response status (int)
 * - body : http response body (String)
 * - headers : http response headers (org.springframework.http.HttpHeaders)
 */
fun ChutneyStepBuilder.HttpGetAction(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    timeout: String? = null,
    outputs: Map<String, Any> = mapOf(),
    strategy: Strategy? = null,
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-get",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a http target, POST http call
 * -------
 * Outputs:
 * - status : http response status (int)
 * - body : http response body (String)
 * - headers : http response headers (org.springframework.http.HttpHeaders)
 */
fun ChutneyStepBuilder.HttpPostAction(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    body: Any?,
    timeout: String? = null,
    outputs: Map<String, Any> = mapOf(),
    strategy: Strategy? = null,
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-post",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            ("body" to body).takeIf {
                when (body) {
                    is String? -> body.isNullOrBlank().not()
                    is Map<*, *>? -> body.isNullOrEmpty().not()
                    else -> false
                }
            },
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a http target, PUT http call
 * -------
 * Outputs:
 * - status : http response status (int)
 * - body : http response body (String)
 * - headers : http response headers (org.springframework.http.HttpHeaders)
 */
fun ChutneyStepBuilder.HttpPutAction(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    body: Map<String, Any> = mapOf(),
    timeout: String? = null,
    outputs: Map<String, Any> = mapOf(),
    strategy: Strategy? = null,
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-put",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            "body" to body,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a http target, DELETE http post
 * -------
 * Outputs:
 * - status : http response status (int)
 * - body : http response body (String)
 * - headers : http response headers (org.springframework.http.HttpHeaders)
 */
fun ChutneyStepBuilder.HttpDeleteAction(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    timeout: String? = null,
    outputs: Map<String, Any> = mapOf(),
    strategy: Strategy? = null,
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-delete",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a http target, SOAP http post
 * -------
 * Outputs:
 * - status : http response status (int)
 * - body : http response body (String)
 * - headers : http response headers (org.springframework.http.HttpHeaders)
 */
fun ChutneyStepBuilder.HttpSoapAction(
    target: String,
    uri: String,
    body: String,
    headers: Map<String, Any> = mapOf(),
    timeout: String? = null,
    username: String? = null,
    password: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "http-soap",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            "body" to body,
            "username" to username,
            "password" to password,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a http target, PATCH http post
 * -------
 * Outputs:
 * - status : http response status (int)
 * - body : http response body (String)
 * - headers : http response headers (org.springframework.http.HttpHeaders)
 */
fun ChutneyStepBuilder.HttpPatchAction(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    body: Any?,
    timeout: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "http-patch",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            ("body" to body).takeIf {
                when (body) {
                    is String? -> body.isNullOrBlank().not()
                    is Map<*, *>? -> body.isNullOrEmpty().not()
                    else -> false
                }
            },
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun statusValidation(statusCode: Int) = "status_ok" to "#status.equals($statusCode)".elEval()

/**
 * Start a http server
 * -------
 * Outputs:
 * - httpsServer : instance of the http server (com.github.tomakehurst.wiremock.WireMockServer)
 * -------
 * Finally action registered : HttpsServerStopAction
 */
fun ChutneyStepBuilder.HttpsServerStartAction(
    port: String?,
    trustStorePath: String?,
    trustStorePassword: String?,
    keyStorePath: String?,
    keyStorePassword: String?,
    keyPassword: String?,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "https-server-start",
        inputs = listOf(
            "port" to port,
            "truststore-path" to trustStorePath,
            "truststore-password" to trustStorePassword,
            "keystore-path" to keyStorePath,
            "keystore-password" to keyStorePassword,
            "key-password" to keyPassword
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Retrieve messages received from a http server
 * -------
 * Outputs:
 * - requests : list of message received (List<com.github.tomakehurst.wiremock.verification.LoggedRequest>)
 */
fun ChutneyStepBuilder.HttpsListenerAction(
    httpServerVarName: String = "httpsServer",
    uri: String,
    verb: String,
    expectedMessageCount: Int? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "https-listener",
        inputs = listOf(
            "https-server" to httpServerVarName.spEL(),
            "uri" to uri,
            "verb" to verb,
            "expected-message-count" to expectedMessageCount
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Upload via scp
 */
fun ChutneyStepBuilder.ScpUploadAction(
    target: String,
    source: String,
    destination: String,
    timeout: String? = "",
    strategy: Strategy? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = emptyMap()
) {
    implementation = ChutneyStepImpl(
        type = "scp-upload",
        target = target,
        inputs = listOf(
            "source" to source,
            "destination" to destination,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Download via scp
 */
fun ChutneyStepBuilder.ScpDownloadAction(
    target: String,
    source: String,
    destination: String,
    timeout: String? = "",
    strategy: Strategy? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = emptyMap()
) {
    implementation = ChutneyStepImpl(
        type = "scp-download",
        target = target,
        inputs = listOf(
            "source" to source,
            "destination" to destination,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Upload via sftp
 */
fun ChutneyStepBuilder.SftpUploadAction(
    target: String,
    source: String,
    destination: String,
    timeout: String? = "",
    strategy: Strategy? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = emptyMap()
) {
    implementation = ChutneyStepImpl(
        type = "sftp-upload",
        target = target,
        inputs = listOf(
            "source" to source,
            "destination" to destination,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Download via sftp
 */
fun ChutneyStepBuilder.SftpDownloadAction(
    target: String,
    source: String,
    destination: String,
    timeout: String? = "",
    strategy: Strategy? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = emptyMap()
) {
    implementation = ChutneyStepImpl(
        type = "sftp-download",
        target = target,
        inputs = listOf(
            "source" to source,
            "destination" to destination,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Get information from a file
 * -------
 * Outputs:
 * - CreationDate : java.time.LocalDateTime
 * - lastAccess : java.time.LocalDateTime
 * - lastModification : java.time.LocalDateTime
 * - type : String
 * - owner:group : String
 */
fun ChutneyStepBuilder.SftpFileInfoAction(
    target: String,
    file: String,
    timeout: String? = "",
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "sftp-file-info",
        target = target,
        inputs = listOf(
            "file" to file,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * List file from a directory
 * -------
 * Outputs:
 * - files : list of files (List<String>)
 */
fun ChutneyStepBuilder.SftpListDirAction(
    target: String,
    directory: String,
    timeout: String? = "",
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "sftp-list-dir",
        target = target,
        inputs = listOf(
            "directory" to directory,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

enum class SSH_CLIENT_CHANNEL { COMMAND, SHELL }

/**
 * Execute ssh command
 * -------
 * Outputs:
 * - results : list of result of commands (List<com.chutneytesting.action.ssh.sshj.CommandResult>)
 */
fun ChutneyStepBuilder.SshClientAction(
    target: String,
    commands: List<Any>,
    channel: SSH_CLIENT_CHANNEL?,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "ssh-client",
        target = target,
        inputs = listOf(
            "commands" to commands,
            "channel" to channel?.name
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Start a ssh server
 * -------
 * Outputs:
 * - sshServer : instance of ssh server (com.chutneytesting.action.ssh.sshd.SshServerMock)
 * -------
 * Finally action registered : SshServerStopAction
 */
fun ChutneyStepBuilder.SshServerStartAction(
    port: String? = null,
    host: String? = null,
    keyPair: String? = null,
    usernames: List<String> = emptyList(),
    passwords: List<String> = emptyList(),
    authorizedKeys: String? = null,
    stubs: List<String> = emptyList(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "ssh-server-start",
        inputs = listOf(
            "port" to port,
            "bind-address" to host,
            "private-key" to keyPair,
            "usernames" to usernames,
            "passwords" to passwords,
            "authorized-keys" to authorizedKeys,
            "responses" to stubs
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

@Deprecated("Bad naming", ReplaceWith("JmsCleanQueueAction(target, queueName)"), DeprecationLevel.WARNING)
fun ChutneyStepBuilder.JmsCleanQueuesAction(
    target: String,
    queueName: String
) {
    JmsCleanQueueAction(target, queueName)
}

/**
 * On a jms target, consume all messages from a queue
 */
fun ChutneyStepBuilder.JmsCleanQueueAction(
    target: String,
    destination: String,
    selector: String? = null,
    bodySelector: String? = null,
    browserMaxDepth: Int? = null,
    timeOut: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "jms-clean-queue",
        target = target,
        inputs = listOf(
            "destination" to destination,
            "selector" to selector,
            "bodySelector" to bodySelector,
            "browserMaxDepth" to browserMaxDepth,
            "timeOut" to timeOut
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a jms target, get TextMessage from a queue
 * -------
 * Outputs:
 * - textMessage : content of the message (String)
 * - jmsProperties : jms properties of the message (Map<String, Object>)
 */
fun ChutneyStepBuilder.JmsListenerAction(
    target: String,
    destination: String,
    selector: String? = null,
    bodySelector: String? = null,
    browserMaxDepth: Int? = null,
    timeOut: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "jms-listener",
        target = target,
        inputs = listOf(
            "destination" to destination,
            "selector" to selector,
            "bodySelector" to bodySelector,
            "browserMaxDepth" to browserMaxDepth,
            "timeOut" to timeOut
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a jms target, send a message to a queue
 */
fun ChutneyStepBuilder.JmsSenderAction(
    target: String,
    destination: String,
    headers: Map<String, Any> = mapOf(),
    payload: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "jms-sender",
        target = target,
        inputs = listOf(
            "destination" to destination,
            "body" to payload,
            "headers" to headers
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Start a jms server
 * -------
 * Outputs:
 * - jmsBrokerService : instance of jms server (org.apache.activemq.broker.BrokerService)
 * -------
 * Finally action registered : JmsBrokerStopAction
 */
fun ChutneyStepBuilder.JmsBrokerStartAction(
    configUri: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "jms-broker-start",
        inputs = listOf(
            "config-uri" to configUri,
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Execute SQL requests
 * -------
 * Outputs:
 * - recordResult : list of result of sql command (List<com.chutneytesting.action.sql.core.Records>)
 */
fun ChutneyStepBuilder.SqlAction(
    target: String,
    statements: List<String>,
    outputs: Map<String, Any> = mapOf(),
    nbLoggedRow: Int? = null,
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "sql",
        target = target,
        inputs = listOf(
            "statements" to statements,
            "nbLoggedRow" to nbLoggedRow
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

val defaultWebDriverSpel = "webDriver".spEL()

enum class SELENIUM_BY {
    id, NAME, className, cssSelector, xpath, zk;

    companion object {
        fun name(v: SELENIUM_BY): String {
            if (v == NAME) return "name"
            return v.name
        }
    }
}

/**
 * Start a local or remote selenium driver instance with Chrome Driver
 * -------
 * Outputs:
 * - webDriver : instance of webdriver (org.openqa.selenium.WebDriver)
 * -------
 * Finally action registered : SeleniumQuitAction
 */
fun ChutneyStepBuilder.SeleniumChromeDriverInitAction(
    hub: String? = null,
    headless: Boolean? = null,
    driverPath: String? = null,
    browserPath: String? = null,
    chromeOptions: List<String>? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-chrome-driver-init",
        inputs = listOf(
            "hub" to hub,
            "headless" to headless,
            "driverPath" to driverPath,
            "browserPath" to browserPath,
            "chromeOptions" to chromeOptions
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Start a local or remote selenium driver instance with Firefox Driver
 * -------
 * Outputs:
 * - webDriver : instance of webdriver (org.openqa.selenium.WebDriver)
 * -------
 * Finally action registered : SeleniumQuitAction
 */
fun ChutneyStepBuilder.SeleniumFirefoxDriverInitAction(
    hub: String? = null,
    headless: Boolean? = null,
    driverPath: String? = null,
    browserPath: String? = null,
    firefoxProfile: String? = null,
    firefoxPreferences: Map<String, Object>? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-firefox-driver-init",
        inputs = listOf(
            "hub" to hub,
            "headless" to headless,
            "driverPath" to driverPath,
            "browserPath" to browserPath,
            "firefoxProfile" to firefoxProfile,
            "firefoxPreferences" to firefoxPreferences
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Start a remote selenium driver instance with json selenium configuration
 * -------
 * Outputs:
 * - webDriver : instance of webdriver (org.openqa.selenium.WebDriver)
 * -------
 * Finally action registered : SeleniumQuitAction
 */
fun ChutneyStepBuilder.SeleniumGenericDriverInitAction(
    hub: String,
    jsonConfiguration: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-generic-driver-init",
        inputs = listOf(
            "hub" to hub,
            "jsonConfiguration" to jsonConfiguration,
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium click on an element
 */
fun ChutneyStepBuilder.SeleniumClickAction(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-click",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            "wait" to wait
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Close webdriver instance
 */
fun ChutneyStepBuilder.SeleniumCloseAction(
    webDriver: String = defaultWebDriverSpel,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-close",
        inputs = mapOf(
            "web-driver" to webDriver
        ),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium get url
 * -------
 * Outputs:
 * - outputGet : window handle identifier (String)
 */
fun ChutneyStepBuilder.SeleniumGetAction(
    webDriver: String = defaultWebDriverSpel,
    newTab: String? = null,
    url: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to newTab,
            "value" to url
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium get attribute from an element
 * -------
 * Outputs:
 * - outputAttributeValue : attribute value retrieved (String)
 */
fun ChutneyStepBuilder.SeleniumGetAttributeAction(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int? = null,
    attribute: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get-attribute",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            "wait" to wait,
            "attribute" to attribute
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium get text from an element
 * -------
 * Outputs:
 * - outputGetText : web element text or value attribute (String)
 */
fun ChutneyStepBuilder.SeleniumGetTextAction(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get-text",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            "wait" to wait,
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium screenshot
 */
fun ChutneyStepBuilder.SeleniumScreenShotAction(
    webDriver: String = defaultWebDriverSpel,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-screenshot",
        inputs = mapOf(
            "web-driver" to webDriver
        ),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium simulate typing into an element
 */
fun ChutneyStepBuilder.SeleniumSendKeysAction(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int? = null,
    value: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-send-keys",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            "wait" to wait,
            "value" to value
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

enum class SELENIUM_SWITCH { Frame, Window, Popup, AlertOk, AlertCancel }

/**
 * Selenium switch to frame, window, alertok, alertcancel or popup
 * -------
 * Outputs:
 * outputSwitchTo : window handler of popup (if Popup switch used) (String)
 */
fun ChutneyStepBuilder.SeleniumSwitchToAction(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int? = null,
    switchType: SELENIUM_SWITCH? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-switch-to",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            "wait" to wait,
            "switchType" to switchType?.name
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium wait until com.chutneytesting.action.selenium.SeleniumWaitAction.ExpectedByConditionEnum
 */
fun ChutneyStepBuilder.SeleniumWaitAction(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int? = null,
    value: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-wait",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            "wait" to wait,
            "value" to value
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium move and click
 */
fun ChutneyStepBuilder.SeleniumHoverThenClickAction(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-hover-then-click",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            "wait" to wait
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium scroll to element
 */
fun ChutneyStepBuilder.SeleniumScrollToAction(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-scroll-to",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            "wait" to wait
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Selenium resize browser
 */
fun ChutneyStepBuilder.SeleniumSetBrowserSizeAction(
    webDriver: String = defaultWebDriverSpel,
    width: Int,
    height: Int,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-set-browser-size",
        inputs = listOf(
            "web-driver" to webDriver,
            "width" to width,
            "height" to height
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Assert two json document (String) and expected (Map<String, Any>).
 * - Keys of the expected map are jsonpath
 * - Values of the expected map are expected values
 */
fun ChutneyStepBuilder.JsonAssertAction(
    document: String,
    expected: Map<String, Any> = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "json-assert",
        inputs = listOf(
            "document" to document,
            "expected" to expected
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Same as com.chutneytesting.kotlin.dsl.ChutneyStepImplExtensionsKt.JsonAssertAction(String, Map<String,Object>)
 * but inputs are execution context variables instead
 */
fun ChutneyStepBuilder.JsonAssertAction(
    documentVariable: String,
    expectationsVariable: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "json-assert",
        inputs = listOf(
            "document" to documentVariable.spEL,
            "expected" to expectationsVariable.spEL
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

enum class JsonCompareMode { STRICT, LENIENT }

/**
 * Compare path in two jsons
 */
fun ChutneyStepBuilder.JsonCompareAction(
    document1: String,
    document2: String,
    comparingPaths: Map<String, String>? = null,
    mode: JsonCompareMode = JsonCompareMode.STRICT,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "json-compare",
        inputs = listOf(
            "document1" to document1,
            "document2" to document2,
            "comparingPaths" to comparingPaths,
            "mode" to mode.name
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Validate a json from a json schema
 */
fun ChutneyStepBuilder.JsonValidationAction(
    schema: String,
    json: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "json-validation",
        inputs = listOf(
            "schema" to schema,
            "json" to json
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Assert xmlpath in a xml document
 * - Keys of the expected map are xpath
 * - Values of the expected map are expected values
 */
fun ChutneyStepBuilder.XmlAssertAction(
    document: String,
    expected: Map<String, Any> = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "xml-assert",
        inputs = listOf(
            "document" to document,
            "expected" to expected
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Compare two Strings
 */
fun ChutneyStepBuilder.StringAssertAction(
    document: String,
    expected: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "string-assert",
        inputs = listOf(
            "document" to document,
            "expected" to expected
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

@Deprecated("Bad naming", ReplaceWith("AssertAction(List<String>)"), DeprecationLevel.WARNING)
fun ChutneyStepBuilder.AssertTrueAction(asserts: List<Map<String, Any>>) {
    implementation = ChutneyStepImpl(
        type = "assert",
        inputs = listOf("asserts" to asserts).notEmptyToMap()
    )
}

/**
 * List of spEL to assert (spEL must return a boolean value)
 */
fun ChutneyStepBuilder.AssertAction(
    asserts: List<String>,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "assert",
        inputs = listOf(
            "asserts" to asserts.map { s -> mapOf("assert-true" to s) }
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 *  Validate a xml from a xsd
 */
fun ChutneyStepBuilder.XsdValidationAction(
    xml: String,
    xsdPath: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "xsd-validation",
        inputs = listOf(
            "xml" to xml,
            "xsd" to xsdPath
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Compare two String on multiple mode (equals, not-equals, contains, not-contains, greater-than, less-than)
 * For greater and less comparator, numerics are expected.
 */
fun ChutneyStepBuilder.CompareAction(
    mode: String,
    actual: String,
    expected: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "compare",
        inputs = listOf(
            "mode" to mode,
            "actual" to actual,
            "expected" to expected
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * On a kafka target, publish a message to a topic
 * -------
 * Outputs:
 * - payload : payload sent (String)
 * - headers : headers of the message sent (Map<String, String>)
 */
fun ChutneyStepBuilder.KafkaBasicPublishAction(
    target: String,
    topic: String,
    headers: Map<String, Any> = mapOf(),
    payload: Any,
    properties: Map<String, String> = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "kafka-basic-publish",
        target = target,
        inputs = listOf(
            "topic" to topic,
            "headers" to headers,
            "payload" to payload,
            "properties" to properties
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

// cf. org.springframework.kafka.listener.ContainerProperties.AckMode
enum class KafkaSpringOffsetCommitBehavior { RECORD, BATCH, TIME, COUNT, COUNT_TIME, MANUAL, MANUAL_IMMEDIATE }

/**
 * On a kafka target, consume messages from a topic
 * -------
 * Outputs:
 * - body : list of bodies of messages consumed (List<Map<String, Object>>)
 * - payloads : list of payload of messages consumed (List<Object>)
 * - headers : list of headers of messages consumed (List<Map<String, Object>>)
 */
fun ChutneyStepBuilder.KafkaBasicConsumeAction(
    target: String,
    topic: String,
    group: String,
    properties: Map<String, String> = mapOf(),
    timeout: String? = null,
    selector: String? = null,
    nbMessages: Int? = null,
    headerSelector: String? = null,
    contentType: String? = null,
    ackMode: KafkaSpringOffsetCommitBehavior? = null,
    resetOffset: Boolean = false,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "kafka-basic-consume",
        target = target,
        inputs = listOf(
            "topic" to topic,
            "group" to group,
            "timeout" to timeout,
            "selector" to selector,
            "properties" to properties,
            "nb-messages" to nbMessages,
            "header-selector" to headerSelector,
            "content-type" to contentType,
            "ackMode" to ackMode,
            "resetOffset" to resetOffset
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Start a local kafka broker
 * -------
 * Outputs:
 * - kafkaBroker : instance of kafka broker (org.springframework.kafka.test.EmbeddedKafkaBroker)
 * -------
 * Finally action registered : KafkaBrokerStopAction
 */
fun ChutneyStepBuilder.KafkaBrokerStartAction(
    port: String? = null,
    topics: List<String>? = null,
    properties: Map<String, Any>? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "kafka-broker-start-consume",
        inputs = listOf(
            "port" to port,
            "topics" to topics,
            "properties" to properties
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

/**
 * Create or update a micrometer counter
 * -------
 * Outputs:
 * - micrometerCounter : instance of the counter (io.micrometer.core.instrument.Counter)
 */
fun ChutneyStepBuilder.MicrometerCounterAction(
    name: String,
    description: String? = null,
    unit: String? = null,
    tags: List<String> = emptyList(),
    counter: String? = null,
    increment: String? = null,
    registry: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-counter",
        inputs = listOf(
            "name" to name,
            "description" to description,
            "unit" to unit,
            "tags" to tags,
            "counter" to counter,
            "increment" to increment,
            "registry" to registry
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Create or update a micrometer gauge
 * -------
 * Outputs:
 * - micrometerGaugeObject : instance of the gauge (io.micrometer.core.instrument.Gauge)
 */
fun ChutneyStepBuilder.MicrometerGaugeAction(
    name: String,
    description: String? = null,
    unit: String? = null,
    strongReference: Boolean? = null,
    tags: List<String> = emptyList(),
    gaugeObject: Any? = null, //Number or Object or List or Map
    gaugeFunction: String? = null,
    registry: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-gauge",
        inputs = listOf(
            "name" to name,
            "description" to description,
            "unit" to unit,
            "strongReference" to strongReference,
            "tags" to tags,
            "gaugeObject" to gaugeObject,
            "gaugeFunction" to gaugeFunction,
            "registry" to registry
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Create or update a micrometer timer
 * -------
 * Outputs:
 * - micrometerTimer : instance of the timer (io.micrometer.core.instrument.Timer)
 */
fun ChutneyStepBuilder.MicrometerTimerAction(
    name: String,
    description: String? = null,
    tags: List<String> = emptyList(),
    bufferLength: String? = null,
    expiry: String? = null,
    maxValue: String? = null,
    minValue: String? = null,
    percentilePrecision: String? = null,
    publishPercentilesHistogram: Boolean? = null,
    percentiles: String? = null,
    sla: String? = null,
    timer: String? = null,
    registry: String? = null,
    timeunit: String? = null,
    record: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer",
        inputs = listOf(
            "name" to name,
            "description" to description,
            "tags" to tags,
            "bufferLength" to bufferLength,
            "expiry" to expiry,
            "maxValue" to maxValue,
            "minValue" to minValue,
            "percentilePrecision" to percentilePrecision,
            "publishPercentilesHistogram" to publishPercentilesHistogram,
            "percentiles" to percentiles,
            "sla" to sla,
            "timer" to timer,
            "registry" to registry,
            "timeunit" to timeunit,
            "record" to record
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Start a timer
 * -------
 * Outputs:
 * - micrometerTimerSample : instance of the timer sample (io.micrometer.core.instrument.Timer.Sample)
 */
fun ChutneyStepBuilder.MicrometerTimerStartAction(
    registry: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer-start",
        inputs = listOf(
            "registry" to registry,
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Stop a timer
 * -------
 * Outputs:
 * - micrometerTimerSampleDuration : duration of the timer (java.time.Duration)
 */
fun ChutneyStepBuilder.MicrometerTimerStopAction(
    sample: String,
    timer: String,
    timeunit: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer-stop",
        inputs = listOf(
            "sample" to sample,
            "timer" to timer,
            "timeunit" to timeunit,
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * Create or update a micrometer distribution summary
 * -------
 * Outputs:
 * - micrometerSummary : instance of the timer (io.micrometer.core.instrument.DistributionSummary)
 */
fun ChutneyStepBuilder.MicrometerSummaryAction(
    name: String,
    description: String? = null,
    unit: String? = null,
    tags: List<String> = emptyList(),
    bufferLength: String? = null,
    expiry: String? = null,
    maxValue: String? = null,
    minValue: String? = null,
    percentilePrecision: String? = null,
    publishPercentilesHistogram: Boolean? = null,
    percentiles: String? = null,
    scale: String? = null,
    sla: String? = null,
    distributionSummary: String? = null,
    registry: String? = null,
    record: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-summary",
        inputs = listOf(
            "name" to name,
            "description" to description,
            "unit" to unit,
            "tags" to tags,
            "bufferLength" to bufferLength,
            "expiry" to expiry,
            "maxValue" to maxValue,
            "minValue" to minValue,
            "percentilePrecision" to percentilePrecision,
            "publishPercentilesHistogram" to publishPercentilesHistogram,
            "percentiles" to percentiles,
            "scale" to scale,
            "sla" to sla,
            "distributionSummary" to distributionSummary,
            "registry" to registry,
            "record" to record
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a radius target, do an authentication
 * -------
 * Outputs:
 * - radiusResponse : radius response (org.tinyradius.packet.RadiusPacket)
 */
fun ChutneyStepBuilder.RadiusAuthenticateAction(
    target: String,
    userName: String,
    userPassword: String,
    protocol: String? = null,
    attributes: Map<String, String>? = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "radius-authenticate",
        target = target,
        inputs = listOf(
            "userName" to userName,
            "userPassword" to userPassword,
            "protocol" to protocol,
            "attributes" to attributes
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

/**
 * On a radius target, do an accounting
 * -------
 * Outputs:
 * - radiusResponse : radius response (org.tinyradius.packet.RadiusPacket)
 */
fun ChutneyStepBuilder.RadiusAccountingAction(
    target: String,
    userName: String,
    accountingType: Int,
    attributes: Map<String, String>? = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "radius-accounting",
        target = target,
        inputs = listOf(
            "userName" to userName,
            "accountingType" to accountingType,
            "attributes" to attributes
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

// Helpers
private fun <T> List<Pair<String, T?>?>.notEmptyToMap(): Map<String, T> {
    return (this
        .filterNotNull()
        .filter { it.second != null }
        .filter {
            when (it.second) {
                is Collection<*> -> (it.second as Collection<*>).isNotEmpty()
                is Map<*, *> -> (it.second as Map<*, *>).isNotEmpty()
                else -> true
            }
        } as List<Pair<String, T>>).toMap()
}
