package kz.kolesa;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class EnvDiffCheckAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        EnvDiffService.generateDiff(project).ifPresentOrElse(
            diff -> new EnvDiffDialog(project, diff).show(),
            () -> Messages.showInfoMessage(project, EnvDiffBundle.message("plugin.diff_not_found"), "EnvDiff")
        );
    }
}
