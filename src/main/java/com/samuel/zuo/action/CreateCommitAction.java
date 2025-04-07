package com.samuel.zuo.action;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler;
import com.samuel.zuo.factory.ModelServiceFactory;
import com.samuel.zuo.service.JavaCodeContextExtractor;
import com.samuel.zuo.service.ModelService;
import com.samuel.zuo.setting.CommitByAISettingsState;
import icons.MyIcons;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
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
        if(!checkConfiguration()) {
            aiProcessing = false;
            return;
        }
        String prompt = buildPrompt(includedChanges, baseDir, project);
        new Thread(() -> createCommitMessage(prompt, commitPanel)).start();
    }

    private boolean checkConfiguration() {
        CommitByAISettingsState settingsState = CommitByAISettingsState.getInstance();
        if (settingsState.type.equals("remote")) {
            if (StringUtils.isEmpty(settingsState.aiServerAddress) || StringUtils.isEmpty(settingsState.token)) {
                System.out.println("Error: Please add remote API or token in your plugin config page.");
                Notification notification = new Notification("Summarize Code Changes",
                        "Summarize changes failed",
                        "Please add remote API or token in your plugin config page.", NotificationType.WARNING);
                notification.addAction(new MyNotificationAction("Summarize code changes"));
                Notifications.Bus.notify(notification);
                return false;
            }
        }
        return true;
    }

    private void createCommitMessage(String prompt, CommitMessageI commitPanel) {
        System.out.println(prompt);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为10秒
                .readTimeout(60, TimeUnit.SECONDS) // 设置读取超时时间为30秒
                .build();
        // 请求体数据
        String model = CommitByAISettingsState.getInstance().model;
        ModelService modelService = ModelServiceFactory.getModelService(model);
        // 创建请求体
        Request request = modelService.buildRequest(prompt);
        StringBuilder sb = new StringBuilder();
        // 发起请求并处理响应
        boolean apiSuccess = false;
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Process the JSON data
                        String message = modelService.parseResponse(line);
                        System.out.print(message);
                        sb.append(message);
                        String freshMessage = sb.toString();
                        ApplicationManager.getApplication().invokeLater(() -> {
                            commitPanel.setCommitMessage(freshMessage);
                        });
                    }
                }
                apiSuccess = true;
            } else {
                // 处理失败响应
                System.out.println("api response code: " + response.code());
                System.out.println(response.body());
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        } finally {
            aiProcessing = false;
            if (!apiSuccess) {
                Notifications.Bus.notify(new Notification("Summarize Code Changes",
                        "Summarize changes failed",
                        "Please check if AI service is running well by selected configuration.", NotificationType.ERROR));
            }
        }
    }

    private String buildPrompt(List<Change> includedChanges, String baseDir, Project project) {
        List<String> totalUnifiedDiffs = new ArrayList<>();
        List<String> methodStackSummaryList = new ArrayList<>();
        String prompt = new String(CommitByAISettingsState.getInstance().prompt);
        for (Change change : includedChanges) {
            HashSet<PsiMethod> changedMethods = new HashSet<>();
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
            // for java file, we need to extract method call graph
            if (prompt.contains("${MethodStackSummary}") && change.getVirtualFile().getName().endsWith(".java")) {
                VirtualFile file = change.getVirtualFile();
                if (file != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    if (psiFile != null) {
                        psiFile.accept(new JavaRecursiveElementVisitor() {
                            @Override
                            public void visitMethod(PsiMethod method) {
                                super.visitMethod(method);
                                // You might want more refined change detection logic here
                                changedMethods.add(method);
                            }
                        });
                    }
                }
                JavaCodeContextExtractor callGraphBuilder = new JavaCodeContextExtractor();
                for (PsiMethod method : changedMethods) {
                    PsiFile psiFile = method.getContainingFile();
                    callGraphBuilder.buildCallGraph(psiFile);
                }
                Map<PsiMethod, Set<PsiMethod>> callGraphMap = callGraphBuilder.getCallGraph();
                // Step 3: Trace the call stack for each changed method
                StringBuilder methodStackSummary = new StringBuilder("It's the method structure summary of file: " + relativePath + "\n");
                for (PsiMethod changedMethod : changedMethods) {
                    methodStackSummary.append(" - " + JavaCodeContextExtractor.printMethodSignature(changedMethod)).append("\n");
                    for (PsiMethod method : callGraphMap.get(changedMethod)) {
                        methodStackSummary.append("    - " + JavaCodeContextExtractor.printMethodSignature(method)).append("\n");
                    }
                }
                methodStackSummaryList.add(methodStackSummary.toString());
            }
        }
        if (prompt.contains("${MethodStackSummary}") && !methodStackSummaryList.isEmpty()) {
            prompt = prompt.replace("${MethodStackSummary}", String.join("\n", methodStackSummaryList));
        }
        prompt = prompt.replace("${UnifiedDiff}", String.join("\n", totalUnifiedDiffs));
        prompt = prompt.replace("${TotalFileCount}", String.valueOf(includedChanges.size()));
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
