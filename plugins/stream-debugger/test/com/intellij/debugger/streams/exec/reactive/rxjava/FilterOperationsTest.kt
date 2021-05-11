// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.exec.reactive.rxjava

/**
 * @author Aleksandr Eslikov
 */
class FilterOperationsTest : RxJavaTestCase() {
  override val packageName: String = "filtering"

  fun testOfType() = doRxJavaWithResultTest()
  fun testFilter() = doRxJavaWithResultTest()
  fun testSkip() = doRxJavaWithResultTest()
  fun testSkipLast() = doRxJavaWithResultTest()
}
