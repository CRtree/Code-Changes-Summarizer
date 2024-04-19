package com.samuel.zuo.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * description: CommitMessageDocListener
 * date: 2024/3/17 16:59
 * author: samuel_zuo
 * version: 1.0
 */
public class CommitMessageDocListener implements DocumentListener {
    @Override
    public void beforeDocumentChange(DocumentEvent event) {

    }

    @Override
    public void documentChanged(DocumentEvent event) {
        // 假定获取的文档内容
        String content = event.getDocument().getText();
        // 判断是否需要弹出补全提示
        if (shouldShowCompletion(event)) {
            ApplicationManager.getApplication().invokeLater(() -> showCompletionPopup(event.getDocument()));
        }
    }

    private boolean shouldShowCompletion(DocumentEvent event) {
        // 简单的触发条件判断，实际逻辑可能会更复杂
        return StringUtils.isNotBlank(event.getNewFragment());
    }

    private void showCompletionPopup(Document document) {
        List<String> dataList = List.of("feat", "fix", "docs", "style", "refactor", "perf", "test", "chore");

        // 使用 JBList 创建一个选择列表
        JBList<String> list = new JBList<>(dataList);

        // 获取当前激活的编辑器的实例
        Editor[] editors = EditorFactory.getInstance().getEditors(document);
        Editor editor = editors[0];
        if (editor == null) {
            return; // 这里简单处理，实际应该有更严谨的处理
        }

        // 创建并显示弹出菜单
        JBPopup jbPopup = JBPopupFactory.getInstance().createListPopupBuilder(list)
                .setItemChoosenCallback(() -> {
                    // 这里处理用户的选择
                    String selectedValue = list.getSelectedValue();
                    System.out.println("Selected: " + selectedValue);
                    // 实际中这里将需要将选中的值插入到 Commit Message 文档中
                    insertCompletionAtCaret(editor, document, selectedValue);
                })
                .createPopup();
        jbPopup.showInBestPositionFor(editor);
    }

    private void insertCompletionAtCaret(Editor editor, Document document, String textToInsert) {
        // 获取项目示例，你需要确保此处获取的project是正确的
        Project project = editor.getProject();
        // 使用WriteCommandAction来封装写操作
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // 获取当前光标的偏移量
            int offset = editor.getCaretModel().getOffset();
            // 在文档中插入选中的补全项
            document.insertString(offset, textToInsert);
            // 选择性地移动光标到插入文本之后的位置
            editor.getCaretModel().moveToOffset(offset + textToInsert.length() + 1);
        });
    }
}
