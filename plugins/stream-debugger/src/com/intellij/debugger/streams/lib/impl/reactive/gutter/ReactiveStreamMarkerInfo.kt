// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.debugger.streams.StreamDebuggerBundle
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import icons.JavaDebuggerStreamsIcons
import java.util.function.Supplier

/**
 * @author Aleksandr Eslikov
 */
class ReactiveStreamMarkerInfo(private val psiElement: PsiElement) :
  LineMarkerInfo<PsiElement>(psiElement,
                             psiElement.textRange,
                             JavaDebuggerStreamsIcons.Reactive_stream,
                             { StreamDebuggerBundle.message("line.marker.reactive.stream") },
                             null,
                             GutterIconRenderer.Alignment.RIGHT,
                             Supplier<String> { StreamDebuggerBundle.message("line.marker.reactive.stream") }

  ) {
  override fun createGutterRenderer(): GutterIconRenderer {
    return ReactiveStreamGutterRenderer(this, psiElement)
  }
}
