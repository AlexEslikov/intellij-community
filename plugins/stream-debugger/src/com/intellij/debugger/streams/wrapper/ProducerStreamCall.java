// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.wrapper;

import com.intellij.debugger.streams.trace.impl.handler.type.GenericType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Aleksandr Eslikov
 */
public interface ProducerStreamCall extends StreamCall, TypeAfterAware {
  @Nullable
  PsiElement getPsiElement();

  @NotNull
  GenericType getResultType();
}
