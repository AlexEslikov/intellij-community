// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.debugger.streams.StreamDebuggerBundle
import com.intellij.debugger.streams.action.ChainResolver
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import icons.JavaDebuggerStreamsIcons
import javax.swing.Icon

/**
 * @author Aleksandr Eslikov
 */
class ReactiveStreamLineMarkerProvider : LineMarkerProviderDescriptor() {
  private val chainResolver = ChainResolver()

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    return null //do nothing
  }

  override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
    for (element in elements) {
      ProgressManager.checkCanceled()
      if (element is PsiIdentifier) {
        val chains = chainResolver.getChainsWithLibrary(element, false)
        if (chains.isEmpty()) {
          continue
        }
        if (chains.any { resolver -> resolver.chain.producerCall?.psiElement == element }) {
          result.add(ReactiveStreamMarkerInfo(element))
        }
      }
    }
  }

  override fun getName(): String {
    return StreamDebuggerBundle.message("line.marker.reactive.stream")
  }

  override fun getIcon(): Icon {
    return JavaDebuggerStreamsIcons.Reactive_stream
  }
}
