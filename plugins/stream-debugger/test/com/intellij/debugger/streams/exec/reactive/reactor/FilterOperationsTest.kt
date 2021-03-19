// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.exec.reactive.reactor

/**
 * @author Aleksandr Eslikov
 */
class FilterOperationsTest : ReactorTestCase() {
  override val packageName: String = "filtering"

  fun testFilter() = doReactorWithResultTest()
  fun testSkip() = doReactorWithResultTest()
  fun testSkipLast() = doReactorWithResultTest()
}
