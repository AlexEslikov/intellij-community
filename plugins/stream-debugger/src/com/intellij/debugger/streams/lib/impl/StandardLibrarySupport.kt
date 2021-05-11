// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl

import com.intellij.debugger.streams.trace.impl.handler.unified.DistinctTraceHandler
import com.intellij.debugger.streams.trace.impl.interpret.AllMatchTraceInterpreter
import com.intellij.debugger.streams.trace.impl.interpret.AnyMatchTraceInterpreter
import com.intellij.debugger.streams.trace.impl.interpret.NoneMatchTraceInterpreter

/**
 * @author Vitaliy.Bibaev
 */
class StandardLibrarySupport
  : LibrarySupportBase() {

  init {
    addIntermediateOperationsSupport(*filterOperations(
      "filter", "limit", "skip", "peek", "onClose"))

    addIntermediateOperationsSupport(*mapOperations(
      "map", "mapToInt", "mapToLong", "mapToDouble", "mapToObj", "boxed"))

    addIntermediateOperationsSupport(*flatMapOperations(
      "flatMap", "flatMapToInt", "flatMapToLong", "flatMapToDouble"))

    addIntermediateOperationsSupport(DistinctOperation("distinct") { num, call, dsl -> DistinctTraceHandler(num, call, dsl) })
    addIntermediateOperationsSupport(SortedOperation("sorted"))
    addIntermediateOperationsSupport(ParallelOperation("parallel"))

    addTerminationOperationsSupport(MatchingOperation("anyMatch",
                                                      AnyMatchTraceInterpreter()),
                                    MatchingOperation("allMatch",
                                                      AllMatchTraceInterpreter()),
                                    MatchingOperation("noneMatch",
                                                      NoneMatchTraceInterpreter()))

    addTerminationOperationsSupport(OptionalResultOperation("min"),
                                    OptionalResultOperation("max"),
                                    OptionalResultOperation("findAny"),
                                    OptionalResultOperation("findFirst"))

    addTerminationOperationsSupport(ToCollectionOperation("toArray"),
                                    ToCollectionOperation("collect"))
  }
}