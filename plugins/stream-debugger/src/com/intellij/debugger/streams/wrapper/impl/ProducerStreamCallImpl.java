// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.wrapper.impl;

import com.intellij.debugger.streams.trace.impl.handler.type.GenericType;
import com.intellij.debugger.streams.wrapper.CallArgument;
import com.intellij.debugger.streams.wrapper.ProducerStreamCall;
import com.intellij.debugger.streams.wrapper.StreamCallType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Aleksandr Eslikov
 */
public class ProducerStreamCallImpl extends StreamCallImpl implements ProducerStreamCall {
  private final GenericType myTypeAfter;
  private final GenericType myReturnType;
  private final PsiElement myElement;

  public ProducerStreamCallImpl(@NotNull String name,
                                @Nullable PsiElement element,
                                @NotNull List<CallArgument> args,
                                @NotNull GenericType typeAfter,
                                @NotNull GenericType resultType,
                                @NotNull TextRange range) {
    super(name, args, StreamCallType.TERMINATOR, range);
    myElement = element;
    myTypeAfter = typeAfter;
    myReturnType = resultType;
  }

  @NotNull
  @Override
  public GenericType getTypeAfter() {
    return myTypeAfter;
  }

  @Override
  public @Nullable PsiElement getPsiElement() {
    return myElement;
  }

  @NotNull
  @Override
  public GenericType getResultType() {
    return myReturnType;
  }
}
