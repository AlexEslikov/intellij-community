// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.exec.reactive.reactor

import com.intellij.debugger.streams.exec.LibraryTraceExecutionTestCase
import com.intellij.debugger.streams.lib.LibrarySupportProvider
import com.intellij.debugger.streams.lib.impl.reactive.ReactorSupportProvider

/**
 * @author Aleksandr Eslikov
 */
abstract class ReactorTestCase
  : LibraryTraceExecutionTestCase(listOf("reactor-core-3.4.4.jar", "reactive-streams-1.0.3.jar")) {


  protected abstract val packageName: String

  override fun getLibrarySupportProvider(): LibrarySupportProvider {
    return ReactorSupportProvider()
  }

  override fun replaceAdditionalInOutput(str: String): String {
    return super.replaceAdditionalInOutput(str)
      .replace("file:/!LIBRARY_JAR!", "file:!LIBRARY_JAR!")
  }

  private val className: String
    get() = packageName + "." + getTestName(false)

  final override fun getTestAppRelativePath(): String = "reactive/reactor"

  protected fun doReactorVoidTest() = doTest(true, className)
  protected fun doReactorWithResultTest() = doTest(false, className)
}
