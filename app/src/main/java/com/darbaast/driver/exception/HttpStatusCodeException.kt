package com.darbaast.driver.exception

import okio.IOException

class HttpStatusCodeException(
    val statusCode: Int,
    message: String
) : IOException(message)
