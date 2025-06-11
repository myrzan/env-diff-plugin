package kz.kolesa;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.notification.*;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class EnvDiffStartupActivity implements ProjectActivity {
    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        String basePath = project.getBasePath();
        if (basePath == null) {
            System.err.println("[EnvDiff] ❌ Project base path is null");
            return null;
        }

        File defaultIgnore = new File(basePath, "envdiffignore.default.json");
        if (!defaultIgnore.exists()) {
            try {
                String defaultContent = "{\n  \"DIFF_IGNORE\": [\n    \"APP_NAME\",\n    \"CACHE_TTL\",\n    \"REDIS_URL\"\n  ]\n}";
                Files.writeString(defaultIgnore.toPath(), defaultContent);
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("EnvDiff Notifications")
                        .createNotification("✅ Default ignore file created", NotificationType.INFORMATION)
                        .notify(project);
            } catch (IOException e) {
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("EnvDiff Notifications")
                        .createNotification("❌ Failed to create envdiffignore.default.json: " + e.getMessage(), NotificationType.ERROR)
                        .notify(project);
            }
        }

        ApplicationManager.getApplication().getMessageBus()
                .connect(project)
                .subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
                    @Override
                    public void after(@NotNull List<? extends VFileEvent> events) {
                        for (VFileEvent event : events) {
                            String path = event.getPath();
                            if (path.contains(".git/FETCH_HEAD")) {
                                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                                    ApplicationManager.getApplication().runReadAction(() -> {
                                        EnvDiffService.generateDiff(project).ifPresent(diff -> {
                                            NotificationGroup group = NotificationGroupManager.getInstance()
                                                    .getNotificationGroup("EnvDiff Notifications");

                                            Notification notification = group.createNotification(
                                                    EnvDiffBundle.message("plugin.diff_found_text"),
                                                    NotificationType.INFORMATION
                                            ).addAction(new NotificationAction(EnvDiffBundle.message("plugin.view_diff")) {
                                                @Override
                                                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification n) {
                                                    new EnvDiffDialog(project, diff).show();
                                                }
                                            });

                                            Notifications.Bus.notify(notification, project);
                                        });
                                    });
                                });
                                break;
                            }
                        }
                    }
                });
        return null;
    }
}