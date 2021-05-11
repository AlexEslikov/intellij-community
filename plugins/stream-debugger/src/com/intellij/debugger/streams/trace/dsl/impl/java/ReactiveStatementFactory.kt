// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.trace.dsl.impl.java

import com.intellij.debugger.streams.trace.impl.handler.DoOnNextCall
import com.intellij.debugger.streams.trace.impl.handler.type.GenericType
import com.intellij.debugger.streams.wrapper.IntermediateStreamCall

/**
 * @author Aleksandr Eslikov
 */
class ReactiveStatementFactory : JavaStatementFactory() {
  override fun createPeekCall(elementsType: GenericType, lambda: String): IntermediateStreamCall = DoOnNextCall(lambda, elementsType)
}