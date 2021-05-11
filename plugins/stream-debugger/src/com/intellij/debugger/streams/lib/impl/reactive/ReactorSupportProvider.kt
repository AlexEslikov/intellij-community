// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive

import com.intellij.debugger.streams.lib.LibrarySupport
import com.intellij.debugger.streams.lib.LibrarySupportProvider
import com.intellij.debugger.streams.psi.impl.InheritanceBasedChainDetector
import com.intellij.debugger.streams.psi.impl.JavaChainTransformerImpl
import com.intellij.debugger.streams.psi.impl.JavaStreamChainBuilder
import com.intellij.debugger.streams.trace.TraceExpressionBuilder
import com.intellij.debugger.streams.trace.dsl.impl.DslImpl
import com.intellij.debugger.streams.trace.dsl.impl.java.ReactiveStatementFactory
import com.intellij.debugger.streams.wrapper.StreamChainBuilder
import com.intellij.openapi.project.Project


/**
 * @author Aleksandr Eslikov
 */
internal class ReactorSupportProvider : LibrarySupportProvider {
  private companion object {
    private const val CLASS_NAME = "org.reactivestreams.Publisher"
  }

  private val librarySupport = ReactorSupport()
  private val dsl = DslImpl(ReactiveStatementFactory())

  override fun getLanguageId(): String = "JAVA"

  override fun getChainBuilder(): StreamChainBuilder {
    return JavaStreamChainBuilder(JavaChainTransformerImpl(), InheritanceBasedChainDetector(CLASS_NAME))
  }

  override fun getExpressionBuilder(project: Project): TraceExpressionBuilder {
    return ReactiveTraceExpressionBuilder()
  }

  override fun getLibrarySupport(): LibrarySupport = librarySupport
}
