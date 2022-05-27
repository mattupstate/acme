package com.acme.web.test.core

import java.lang.RuntimeException

class NavigationException(message: String, cause: Throwable) : RuntimeException(message, cause)
