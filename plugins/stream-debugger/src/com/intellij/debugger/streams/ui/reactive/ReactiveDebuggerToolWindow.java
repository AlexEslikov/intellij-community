// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.ui.reactive;

import com.intellij.debugger.streams.action.ChainResolver;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import icons.JavaDebuggerStreamsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intellij.debugger.streams.action.TraceStreamAction.runTrace;

/**
 * @author Aleksandr Eslikov
 */
public class ReactiveDebuggerToolWindow {
  private JPanel myToolWindowContent;
  private JBTable table;
  private JBScrollPane pane;
  private JButton visualizeButton;

  private static final Logger LOG = Logger.getInstance(ReactiveDebuggerToolWindow.class);
  private static final ChainResolver CHAIN_RESOLVER = new ChainResolver();
  private final List<ViewModel> rows = Collections.synchronizedList(new ArrayList<>());

  public ReactiveDebuggerToolWindow(ToolWindow toolWindow, @NotNull Project project) {
    visualizeButton.setIcon(JavaDebuggerStreamsIcons.Stream_debugger);
    visualizeButton.setContentAreaFilled(false);

    String[] tableHead = {"Stream chain"};
    DefaultTableModel defaultTableModel = new DefaultTableModel(tableHead, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    table.setModel(defaultTableModel);
    table.setCellSelectionEnabled(true);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);

    visualizeButton.addActionListener(listener -> {
      int selectedRow = table.getSelectedRow();
      if (selectedRow == -1) return;
      ViewModel row = rows.get(selectedRow);
      PsiElement psiElement = row.element;
      XDebugSession debugSession = getDebugSession(psiElement);
      if (debugSession == null) return;

      List<ChainResolver.StreamChainWithLibrary> chains = CHAIN_RESOLVER.getChainsWithLibrary(psiElement, false);
      if (chains.isEmpty()) {
        LOG.warn("stream chain is not built");
        return;
      }
      if (chains.size() == 1) {
        runTrace(chains.get(0).chain, chains.get(0).provider, debugSession);
      }
    });

    project.getMessageBus().connect()
      .subscribe(ReactiveStreamChainUpdateListener.Companion.getReactiveStreamChainsUpdate(), new ReactiveStreamChainUpdateListener() {

        @Override
        public void deleteRow(@NotNull String streamId) {
          rows.removeIf(viewModel -> viewModel.streamId == streamId);
          ApplicationManager.getApplication().invokeLater(() -> updateTable());
        }

        @Override
        public void createRow(@NotNull String streamId, @NotNull PsiElement psiElement) {
          rows.add(new ViewModel(psiElement, streamId));
          ApplicationManager.getApplication().invokeLater(() -> updateTable());
        }
      });
  }

  @Nullable
  private static XDebugSession getDebugSession(@NotNull PsiElement psiElement) {
    final Project project = psiElement.getProject();
    return XDebuggerManager.getInstance(project).getCurrentSession();
  }

  private void updateTable() {
    final DefaultTableModel model = (DefaultTableModel)table.getModel();
    model.setRowCount(0);
    for (ViewModel row : rows) {
      model.addRow(new String[]{row.streamId});
    }
  }

  public JPanel getContent() {
    return myToolWindowContent;
  }

  private static class ViewModel {
    private final PsiElement element;
    private final String streamId;

    private ViewModel(PsiElement element, String streamId) {
      this.element = element;
      this.streamId = streamId;
    }

    public PsiElement getElement() {
      return element;
    }
  }
}
