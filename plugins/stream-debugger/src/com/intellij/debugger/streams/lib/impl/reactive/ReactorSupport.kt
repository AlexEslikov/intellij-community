// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive

import com.intellij.debugger.streams.lib.impl.DistinctOperation
import com.intellij.debugger.streams.lib.impl.LibrarySupportBase
import com.intellij.debugger.streams.trace.impl.handler.unified.DistinctTraceHandler

/**
 * @author Aleksandr Eslikov
 */
class ReactorSupport : LibrarySupportBase() {

  init {
    addIntermediateOperationsSupport(*filterOperations(
      "filter"))

    addIntermediateOperationsSupport(*mapOperations(
      "map"))

    addIntermediateOperationsSupport(*flatMapOperations(
      "flatMap"))

    addIntermediateOperationsSupport(
      DistinctOperation("distinct") { num, call, dsl -> DistinctTraceHandler(num, call, dsl) })
  }
}
