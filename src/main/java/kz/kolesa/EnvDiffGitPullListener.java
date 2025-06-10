package kz.kolesa;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.notification.*;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

@Service
public final class EnvDiffGitPullListener implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        ApplicationManager.getApplication().getMessageBus()
                .connect(project)
                .subscribe(VirtualFileManager.VFS_CHANGES);
    }

    static class GitPullVfsListener implements VcsListener {
        private final Project project;

        public GitPullVfsListener(Project project) {
            this.project = project;
        }

        @Override
        public void directoryMappingChanged() {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    EnvDiffSettings.State cfg = EnvDiffSettings.getInstance().getState();
                    File base = new File(Objects.requireNonNull(project.getBasePath()));
                    assert cfg != null;
                    File env = new File(base, cfg.envFile);
                    File testing = new File(base, cfg.testingFile);
                    File ignore = new File(base, cfg.ignoreFile);

                    Map<String, String> m1 = EnvDiffUtils.parseEnvFile(env);
                    Map<String, String> m2 = EnvDiffUtils.parseEnvFile(testing);
                    Set<String> ignoreSet = EnvDiffUtils.loadIgnoreList(ignore);

                    StringBuilder diff = new StringBuilder();
                    Set<String> keys = new HashSet<>();
                    keys.addAll(m1.keySet());
                    keys.addAll(m2.keySet());

                    for (String key : keys) {
                        if (ignoreSet.contains(key)) continue;
                        String v1 = m1.get(key);
                        String v2 = m2.get(key);
                        if (!Objects.equals(v1, v2)) {
                            diff.append(key).append(": ").append(v1).append(" ≠ ").append(v2).append("\n");
                        }
                    }

                    if (!diff.isEmpty()) {
                        NotificationGroup group = NotificationGroupManager.getInstance()
                                .getNotificationGroup("EnvDiff Notifications");

                        Notification notification = group.createNotification(
                                "EnvDiff found differences",
                                NotificationType.INFORMATION
                        ).addAction(new NotificationAction("View diff") {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification n) {
                                new EnvDiffDialog(project, diff.toString()).show();
                            }
                        });

                        Notifications.Bus.notify(notification, project);
                    }

                } catch (Exception ignored) {
                }
            });
        }
    }
}