// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive

import com.intellij.debugger.engine.JavaDebugProcess
import com.intellij.debugger.impl.DebuggerManagerListener
import com.intellij.debugger.impl.DebuggerSession
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.xdebugger.impl.XDebugSessionImpl

/**
 * Action used to register [ReactiveStreamDebuggerInitializationRequestor]
 * when debugger session is attached
 *
 * @author Aleksandr Eslikov
 */
class ReactiveDebuggerStartupActivity : StartupActivity {
  override fun runActivity(project: Project) {
    val debuggerListener = object : DebuggerManagerListener {
      override fun sessionAttached(session: DebuggerSession?) {

        val xSession = session?.xDebugSession as? XDebugSessionImpl ?: return
        val javaDebugProcess = xSession.debugProcess as? JavaDebugProcess ?: return
        val process = javaDebugProcess.debuggerSession.process

        val debugInitializationRequestor = ReactiveStreamDebuggerInitializationRequestor(process)
        process.requestsManager.createMethodEntryRequest(debugInitializationRequestor).isEnabled = true
      }
    }

    project.messageBus.connect().subscribe(DebuggerManagerListener.TOPIC, debuggerListener)
  }
}
