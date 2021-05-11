// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.exec.reactive.reactor

/**
 * @author Aleksandr Eslikov
 */
class DistinctOperationsTest : ReactorTestCase() {
  override val packageName: String = "distinct"

  fun testDistinct() = doReactorWithResultTest()
  fun testDistinctUntilChanged() = doReactorWithResultTest()
}
