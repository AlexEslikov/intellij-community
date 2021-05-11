// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.ui.reactive

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManager


/**
 * @author Aleksandr Eslikov
 */
class ReactiveDebuggerToolWindowFactory : ToolWindowFactory {
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val reactiveDebuggerToolWindow = ReactiveDebuggerToolWindow(toolWindow, project)
    val contentFactory = ContentFactory.SERVICE.getInstance()
    val content: Content = contentFactory.createContent(reactiveDebuggerToolWindow.content, "", false)

    val contentManager: ContentManager = toolWindow.contentManager
    contentManager.addContent(content)
  }
}
