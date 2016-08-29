package org.kotyle.squall.base

import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger("org.kotyle.squall.base")

class SquallException(message: String, val args: Map<String,Any> = mapOf(), cause: Exception? = null):
        Exception(message, cause) {}
