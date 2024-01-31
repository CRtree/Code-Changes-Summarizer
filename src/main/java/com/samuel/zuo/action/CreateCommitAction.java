package com.samuel.zuo.action;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.ui.Refreshable;
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler;
import com.samuel.zuo.setting.CommitByAISettingsState;
import icons.MyIcons;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * description: CreateCommitAction
 * date: 2023/12/22 16:54
 * author: samuel_zuo
 * version: 1.0
 */
public class CreateCommitAction extends AnAction implements DumbAware {

    private static boolean aiProcessing = false;

    @Override
    public void actionPerformed(AnActionEvent event) {
        System.out.println("CreateCommitAction actionPerformed");
        CommitMessageI commitPanel = getCommitPanel(event);
        aiProcessing = true;
        handleChangedFiles(event, commitPanel);
    }

    private void handleChangedFiles(AnActionEvent e, CommitMessageI commitPanel) {
        final Project project = e.getProject();
        assert project != null;
        // get included changes
        AbstractCommitWorkflowHandler abstractCommitWorkflowHandler = (AbstractCommitWorkflowHandler) e.getDataContext().getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER);
        if (abstractCommitWorkflowHandler == null) {
            return;
        }
        List<Change> includedChanges = abstractCommitWorkflowHandler.getUi().getIncludedChanges();
        String baseDir = project.getBasePath();
        String prompt = buildPrompt(includedChanges, baseDir);
        new Thread(() -> createCommitMessage(prompt, commitPanel)).start();
    }

    private void createCommitMessage(String prompt, CommitMessageI commitPanel) {
        System.out.println(prompt);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为10秒
                .readTimeout(60, TimeUnit.SECONDS) // 设置读取超时时间为30秒
                .build();
        // 请求体数据
        JsonObject bodyJson = new JsonObject();
        String model = CommitByAISettingsState.getInstance().model;
        bodyJson.addProperty("model", model);
        bodyJson.addProperty("prompt", prompt);
        // 创建请求体
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyJson.toString());
        // 创建请求
        Request request = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .post(requestBody)
                .build();
        StringBuilder sb = new StringBuilder();
        // 发起请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body == null) {
                    return;
                }
                InputStream inputStream = body.byteStream();
                int read = 0;
                byte[] buffer = new byte[4096];
                while ((read = inputStream.read(buffer)) != -1) {
                    String responseBody = new String(buffer, 0, read);
                    if (responseBody.isEmpty()) {
                        continue;
                    }
                    responseBody = responseBody.trim();
                    if (responseBody.endsWith("}")) {
                        // parse response body to JsonObject
                        JsonElement jsonElement = JsonParser.parseString(responseBody);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        String message = jsonObject.get("response").getAsString();
                        // 处理成功响应
                        System.out.print(message);
                        sb.append(message);
                        String freshMessage = sb.toString();
                        ApplicationManager.getApplication().invokeLater(() -> {
                            commitPanel.setCommitMessage(freshMessage);
                        });
                    }
                }
            } else {
                // 处理失败响应
                System.out.println("Unexpected response code: " + response.code());
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }
        aiProcessing = false;
    }

    private String buildPrompt(List<Change> includedChanges, String baseDir) {
        List<String> totalUnifiedDiffs = new ArrayList<>();
        for (Change change : includedChanges) {
            ContentRevision beforeRevision = change.getBeforeRevision();
            ContentRevision afterRevision = change.getAfterRevision();
            String beforeContent = "";
            String afterContent = "";
            try {
                //获取修改前的文件版本的内容
                beforeContent = beforeRevision != null ? beforeRevision.getContent() : "";
                //获取修改后的文件版本的内容
                afterContent = afterRevision != null ? afterRevision.getContent() : "";
            } catch (VcsException e) {
                e.printStackTrace();
            }
            List<String> original = Arrays.stream(beforeContent.split("\n")).toList();
            List<String> revised = Arrays.stream(afterContent.split("\n")).toList();
            // 计算差异
            Patch<String> patch = DiffUtils.diff(original, revised);
            String relativePath = change.getVirtualFile().getPath().replace(baseDir, "");
            List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(relativePath, relativePath, original, patch, 3);
            totalUnifiedDiffs.addAll(unifiedDiff);
        }
        String prompt = new String(CommitByAISettingsState.getInstance().prompt);
        prompt = prompt.replace("${UnifiedDiff}", String.join("\n", totalUnifiedDiffs));
        return prompt;
    }

    @Nullable
    private static CommitMessageI getCommitPanel(@Nullable AnActionEvent e) {
        if (e == null) {
            return null;
        }
        Refreshable data = Refreshable.PANEL_KEY.getData(e.getDataContext());
        if (data instanceof CommitMessageI) {
            return (CommitMessageI) data;
        }
        return VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        //e.getPresentation().setEnabled(!aiProcessing);
        if (aiProcessing) {
            e.getPresentation().setIcon(MyIcons.loading);
            e.getPresentation().setEnabled(false);
        }else{
            e.getPresentation().setIcon(MyIcons.penGrey);
            e.getPresentation().setEnabled(true);
        }

    }
}
