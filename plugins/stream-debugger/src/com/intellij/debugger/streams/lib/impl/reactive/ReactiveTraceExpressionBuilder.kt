// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive

import com.intellij.debugger.streams.trace.TraceExpressionBuilder
import com.intellij.debugger.streams.wrapper.StreamChain
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiDocumentManager
import com.intellij.refactoring.suggested.startOffset

/**
 * @author Aleksandr Eslikov
 */
class ReactiveTraceExpressionBuilder : TraceExpressionBuilder {
  private companion object {
    private val LOG = Logger.getInstance(ReactiveTraceExpressionBuilder::class.java)
    private const val STREAM_INFO_STORAGE = "com.intellij.debugger.streams.reactive.agent.StreamInfoStorage.INSTANCE"
  }

  override fun createTraceExpression(chain: StreamChain): String {
    return ApplicationManager.getApplication().runReadAction(getComputable(chain))
  }

  private fun getComputable(chain: StreamChain) = Computable {
    val element = chain.producerCall?.psiElement ?: return@Computable ""
    val containingFile = element.containingFile
    val psiDocumentManager = PsiDocumentManager.getInstance(containingFile.project)
    val document = psiDocumentManager.getDocument(containingFile) ?: return@Computable ""
    val lineNumber: Int = document.getLineNumber(element.startOffset) + 1
    val expression = "$STREAM_INFO_STORAGE.getResult(\"${containingFile.name}:$lineNumber\");"
    LOG.debug("Created expression for retrieving information about reactive stream: $expression")
    expression
  }
}
