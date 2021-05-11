// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.ui.reactive

import com.intellij.psi.PsiElement
import com.intellij.util.messages.Topic

/**
 * @author Aleksandr Eslikov
 */
interface ReactiveStreamChainUpdateListener {
  fun createRow(streamId: String, element: PsiElement)
  fun deleteRow(streamId: String)

  companion object {
    val ReactiveStreamChainsUpdate: Topic<ReactiveStreamChainUpdateListener> = Topic(ReactiveStreamChainUpdateListener::class.java,
                                                                                     Topic.BroadcastDirection.TO_PARENT)
  }
}
