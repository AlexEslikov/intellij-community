// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive.gutter

import com.intellij.debugger.engine.JavaValue
import com.intellij.debugger.streams.action.ChainResolver
import com.intellij.debugger.streams.wrapper.StreamChain
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.xdebugger.XExpression
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase
import com.sun.jdi.BooleanValue

/**
 * @author Aleksandr Eslikov
 */
internal class ReactiveChainGutterClickAction(private val reactiveStreamGutterRenderer: ReactiveStreamGutterRenderer) : AnAction() {
  private companion object {
    private val LOG = Logger.getInstance(ReactiveChainGutterClickAction::class.java)
    private const val STREAM_INFO_STORAGE = "com.intellij.debugger.streams.reactive.agent.StreamInfoStorage.INSTANCE"
  }

  private val chainResolver = ChainResolver()

  override fun actionPerformed(e: AnActionEvent) {
    val psiElement = reactiveStreamGutterRenderer.psiElement

    val session = reactiveStreamGutterRenderer.getCurrentDebugSession(psiElement)
    val stackFrame = session?.currentStackFrame
    val evaluator = session?.debugProcess?.evaluator

    if (stackFrame == null || evaluator == null) {
      reactiveStreamGutterRenderer.streamLoggingEnabled = false
      return
    }

    val document = getDocument(psiElement) ?: return
    val chains = chainResolver.getChainsWithLibrary(psiElement, false)
    if (chains.isEmpty() || chains.size != 1) {
      return
    }
    val chain = chains[0].chain
    if (chain.producerCall == null) return
    val expression = buildExpression(psiElement.containingFile, document, chain)
    evaluateExpression(evaluator, expression, stackFrame)
  }

  private fun evaluateExpression(evaluator: XDebuggerEvaluator, expression: XExpression, stackFrame: XStackFrame) {
    ReadAction.nonBlocking {
      evaluator.evaluate(expression, object : XEvaluationCallbackBase() {
        override fun errorOccurred(errorMessage: String) {
          LOG.debug("Can't enable reactive stream logging. Can't evaluate expression. $errorMessage")
        }

        override fun evaluated(evaluationResult: XValue) {
          if (evaluationResult is JavaValue) {
            val value = evaluationResult.descriptor.value
            if (value is BooleanValue) {
              val result = value.value()
              reactiveStreamGutterRenderer.streamLoggingEnabled = result
              stackFrame.sourcePosition?.file?.let { repaintEditor(it) }
            }
          }
        }
      }, stackFrame.sourcePosition)
    }.submit(AppExecutorUtil.getAppExecutorService())
  }

  private fun repaintEditor(file: VirtualFile) {
    val document = FileDocumentManager.getInstance().getDocument(file) ?: return
    EditorFactory.getInstance().editors(document).forEach {
      it.component.repaint()
    }
  }

  private fun buildExpression(file: PsiFile, document: Document, chain: StreamChain): XExpression {
    val streamLineNumbers = getStreamLineNumbers(chain, document)
    val expressionText = getExpressionText(file.name, streamLineNumbers)
    LOG.debug("Reactive stream logging expression text: $expressionText")
    return XExpressionImpl.fromText(expressionText)
  }

  private fun getExpressionText(fileName: String, streamLineNumbers: List<Int>): String {
    val streamFirstLineNumber = streamLineNumbers.first()
    val streamLinesIds = streamLineNumbers.joinToString { lineNumber -> "\"$fileName:$lineNumber\"" }

    return """
      int lineNumber = $streamFirstLineNumber;
      boolean loggingEnabled = $STREAM_INFO_STORAGE.isLoggingEnabled("$fileName:$streamFirstLineNumber");
      if(loggingEnabled) {
        $STREAM_INFO_STORAGE.disableLogForStream(java.util.List.of($streamLinesIds));
      } else {
        $STREAM_INFO_STORAGE.enableLogForStream(java.util.List.of($streamLinesIds));
      }
      !loggingEnabled
    """.trimIndent()
  }

  private fun getStreamLineNumbers(chain: StreamChain, document: Document): List<Int> {
    val streamChainLineNumbers = mutableListOf<Int>()
    chain.producerCall?.psiElement?.apply {
      val producerLineNumber = document.getLineNumberOneBased(startOffset)
      streamChainLineNumbers.add(producerLineNumber)
    }

    chain.intermediateCalls.forEach { intermediateCall ->
      val intermediateLineNumber = document.getLineNumberOneBased(intermediateCall.textRange.endOffset)
      streamChainLineNumbers.add(intermediateLineNumber)
    }

    val terminationLineNumber = document.getLineNumberOneBased(chain.terminationCall.textRange.endOffset)
    streamChainLineNumbers.add(terminationLineNumber)

    return streamChainLineNumbers
  }

  private fun getDocument(element: PsiElement): Document? {
    val containingFile: PsiFile = element.containingFile
    val project = containingFile.project
    val psiDocumentManager = PsiDocumentManager.getInstance(project)
    return psiDocumentManager.getDocument(containingFile)
  }

  private fun Document.getLineNumberOneBased(offset: Int): Int {
    return this.getLineNumber(offset) + 1
  }
}
