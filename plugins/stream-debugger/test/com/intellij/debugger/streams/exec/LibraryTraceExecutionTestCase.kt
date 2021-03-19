// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.exec

import com.intellij.debugger.streams.test.TraceExecutionTestCase
import com.intellij.execution.configurations.JavaParameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PluginPathManager
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.PsiTestUtil
import java.io.File
import java.nio.file.Paths

/**
 * @author Vitaliy.Bibaev
 */
abstract class LibraryTraceExecutionTestCase(jarNames: List<String>) : TraceExecutionTestCase() {
  constructor(jarName: String) : this(listOf(jarName))

  private val libraryDirectory = File(PluginPathManager.getPluginHomePath("stream-debugger") + "/lib").absolutePath
  private val jarPaths = jarNames.map { jarName -> Paths.get(libraryDirectory, jarName).toAbsolutePath().toString() }


  private companion object {
    fun String.replaceLibraryPath(libraryPaths: List<String>): String {
      val caseSensitive = SystemInfo.isFileSystemCaseSensitive
      var result = this
      libraryPaths.forEach { libraryPath ->
        result = StringUtil.replace(result, File(libraryPath).toURI().toString().removePrefix("file:"), "!LIBRARY_JAR!", !caseSensitive)
        result = StringUtil.replace(result, FileUtil.toSystemDependentName(libraryPath), "!LIBRARY_JAR!", !caseSensitive)
        result = StringUtil.replace(result, FileUtil.toSystemIndependentName(libraryPath), "!LIBRARY_JAR!", !caseSensitive)
      }
      return result
    }
  }

  override fun setUpModule() {
    super.setUpModule()
    ApplicationManager.getApplication().runWriteAction {
      VfsRootAccess.allowRootAccess(testRootDisposable, libraryDirectory)
      jarPaths.forEach { PsiTestUtil.addLibrary(myModule, it) }
    }
  }

  override fun replaceAdditionalInOutput(str: String): String {
    return super.replaceAdditionalInOutput(str).replaceLibraryPath(jarPaths)
  }

  override fun createJavaParameters(mainClass: String?): JavaParameters {
    val parameters = super.createJavaParameters(mainClass)
    jarPaths.forEach { parameters.classPath.add(it) }
    return parameters
  }

  final override fun getTestAppPath(): String {
    return File(PluginPathManager.getPluginHomePath("stream-debugger") + "/testData/${getTestAppRelativePath()}").absolutePath
  }

  abstract fun getTestAppRelativePath(): String
}
