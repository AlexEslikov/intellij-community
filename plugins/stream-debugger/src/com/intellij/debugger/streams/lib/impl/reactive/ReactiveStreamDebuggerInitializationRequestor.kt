// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.lib.impl.reactive

import com.intellij.debugger.InstanceFilter
import com.intellij.debugger.engine.DebugProcessImpl
import com.intellij.debugger.engine.DebuggerUtils
import com.intellij.debugger.engine.SuspendContextImpl
import com.intellij.debugger.engine.evaluation.EvaluateException
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl
import com.intellij.debugger.engine.events.SuspendContextCommandImpl
import com.intellij.debugger.settings.DebuggerSettings
import com.intellij.debugger.ui.breakpoints.FilteredRequestor
import com.intellij.ui.classFilter.ClassFilter
import com.sun.jdi.ClassLoaderReference
import com.sun.jdi.ClassType
import com.sun.jdi.ObjectReference
import com.sun.jdi.event.LocatableEvent

/**
 * Attaches reactive debugger agent to the debugged process and starts code instrumentation
 *
 * @author Aleksandr Eslikov
 */
class ReactiveStreamDebuggerInitializationRequestor(private val myProcess: DebugProcessImpl) : FilteredRequestor {

  private val DEBUG_AGENT = "com.intellij.debugger.streams.reactive.agent.ReactiveDebuggerAgent"
  private val AGENT_INIT_METHOD = "init"
  private val AGENT_PROCESS_EXISTING_CLASSES_METHOD = "processExistingClasses"

  private val CLASS_LOADER_CLASS = "java.lang.ClassLoader"
  private val GET_SYSTEM_CLASS_LOADER_METHOD = "getSystemClassLoader"
  private val INSTANCE_FIELD = "INSTANCE"

  private val CLASS_EXCLUSION_FILTERS = arrayOf(
    ClassFilter("java.*"),
    ClassFilter("jdk.*"),
    ClassFilter("sun.*"),
    ClassFilter("kotlin.*"),
    ClassFilter("com.intellij.*"),
    ClassFilter("reactor.tools.*"),
    ClassFilter("io.reactivex.rxjava3.*"),
    ClassFilter("net.bytebuddy.*"))

  override fun processLocatableEvent(action: SuspendContextCommandImpl, event: LocatableEvent?): Boolean {
    myProcess.requestsManager.deleteRequest(this)

    val suspendContext = action.suspendContext ?: return false

    val evaluationContext = EvaluationContextImpl(suspendContext, suspendContext.frameProxy)
    return try {
      val classLoader = getSystemClassLoader(evaluationContext)
      resumeAndReturn(callAgent(evaluationContext, classLoader), suspendContext)
    }
    catch (e: EvaluateException) {
      resumeAndReturn(true, suspendContext)
    }
  }

  private fun getSystemClassLoader(evaluationContext: EvaluationContextImpl): ClassLoaderReference {
    val classLoaderType = myProcess.findClass(evaluationContext, CLASS_LOADER_CLASS, null) as ClassType
    val getClassLoaderMethod = DebuggerUtils.findMethod(classLoaderType, GET_SYSTEM_CLASS_LOADER_METHOD, null)
    val classLoaderValue = myProcess.invokeMethod(evaluationContext, classLoaderType, getClassLoaderMethod, emptyList())
    return classLoaderValue as ClassLoaderReference
  }

  private fun callAgent(evaluationContext: EvaluationContextImpl, classLoader: ClassLoaderReference): Boolean {
    val classType = getClassType(evaluationContext, classLoader, DEBUG_AGENT) ?: return false
    val agentInstance = getAgentInstance(classType) ?: return false

    return callInstanceMethods(evaluationContext, classType,
                               agentInstance,
                               AGENT_INIT_METHOD,
                               AGENT_PROCESS_EXISTING_CLASSES_METHOD)
  }

  private fun getClassType(evaluationContext: EvaluationContextImpl,
                           classLoaderReference: ClassLoaderReference,
                           @Suppress("SameParameterValue") className: String): ClassType? {
    val type = myProcess.findClass(evaluationContext, className, classLoaderReference)
    return if (type is ClassType) type else null
  }

  private fun getAgentInstance(classType: ClassType): ObjectReference? {
    val instanceField = classType.fieldByName(INSTANCE_FIELD)
    val instance = classType.getValue(instanceField)
    if (instance is ObjectReference) {
      return instance
    }
    else {
      return null
    }
  }

  private fun callInstanceMethods(
    evaluationContext: EvaluationContextImpl,
    classType: ClassType,
    objectReference: ObjectReference,
    vararg methodsNames: String
  ): Boolean {
    for (methodName in methodsNames) {
      val method = DebuggerUtils.findMethod(classType, methodName, null) ?: continue
      myProcess.invokeInstanceMethod(evaluationContext, objectReference, method, emptyList(), 0)
    }
    return true
  }

  private fun resumeAndReturn(returnValue: Boolean, suspendContext: SuspendContextImpl?): Boolean {
    if (suspendContext != null) {
      myProcess.managerThread.schedule(myProcess.createResumeCommand(suspendContext))
    }
    return returnValue
  }

  override fun getSuspendPolicy(): String {
    return DebuggerSettings.SUSPEND_ALL
  }

  override fun isInstanceFiltersEnabled(): Boolean {
    return false
  }

  override fun getInstanceFilters(): Array<InstanceFilter?>? {
    return InstanceFilter.EMPTY_ARRAY
  }

  override fun isCountFilterEnabled(): Boolean {
    return false
  }

  override fun getCountFilter(): Int {
    return 0
  }

  override fun isClassFiltersEnabled(): Boolean {
    return true
  }

  override fun getClassFilters(): Array<ClassFilter?>? {
    return ClassFilter.EMPTY_ARRAY
  }

  override fun getClassExclusionFilters(): Array<ClassFilter> {
    return CLASS_EXCLUSION_FILTERS
  }
}
