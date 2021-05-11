// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.trace.impl.handler

import com.intellij.debugger.streams.trace.impl.handler.type.GenericType

/**
 * @author Aleksandr Eslikov
 */
class DoOnNextCall(lambda: String, elementType: GenericType) : PeekCall(lambda, elementType) {
  override fun getName(): String {
    return "doOnNext"
  }
}
