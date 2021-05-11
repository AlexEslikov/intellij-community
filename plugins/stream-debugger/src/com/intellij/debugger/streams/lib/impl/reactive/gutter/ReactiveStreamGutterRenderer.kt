// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerInfo.LineMarkerGutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.util.ui.EmptyIcon
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import icons.JavaDebuggerStreamsIcons
import javax.swing.Icon

/**
 * @author Aleksandr Eslikov
 */
internal data class ReactiveStreamGutterRenderer(
  val lineMarker: LineMarkerInfo<PsiElement>,
  val psiElement: PsiElement
) : LineMarkerGutterIconRenderer<PsiElement>(lineMarker) {
  var streamLoggingEnabled = false

  override fun getIcon(): Icon {
    if (getCurrentDebugSession(psiElement) == null) {
      streamLoggingEnabled = false
      return EmptyIcon.ICON_0
    }
    if (streamLoggingEnabled) {
      return JavaDebuggerStreamsIcons.Reactive_stream
    }
    else {
      return JavaDebuggerStreamsIcons.Reactive_stream_disabled
    }
  }

  fun getCurrentDebugSession(element: PsiElement): XDebugSession? {
    val project = element.project
    return XDebuggerManager.getInstance(project).currentSession
  }
}
