package kz.kolesa;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvDiffCheckAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        EnvDiffSettings.State config = EnvDiffSettings.getInstance().getState();
        assert config != null;
        File envFile = new File(project.getBasePath(), config.envFile);
        File envTestingFile = new File(project.getBasePath(), config.testingFile);
        File ignoreFile = new File(project.getBasePath(), config.ignoreFile);

        try {
            Map<String, String> env = parseEnvFile(envFile);
            Map<String, String> envTesting = parseEnvFile(envTestingFile);
            Set<String> ignoreKeys = loadIgnoreList(ignoreFile);

            Set<String> allKeys = new HashSet<>();
            allKeys.addAll(env.keySet());
            allKeys.addAll(envTesting.keySet());

            StringBuilder diffReport = new StringBuilder();
            for (String key : allKeys) {
                if (ignoreKeys.contains(key)) continue;
                String val1 = env.get(key);
                String val2 = envTesting.get(key);
                if (!Objects.equals(val1, val2)) {
                    diffReport.append(key).append(": ").append(val1).append(" ≠ ").append(val2).append("\n");
                }
            }

            if (!diffReport.isEmpty()) {
                new EnvDiffDialog(project, diffReport.toString()).showAndGet();
            } else {
                Messages.showInfoMessage(project, "No differences found.", "EnvDiff");
            }

        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Error during diff: " + ex.getMessage(), "EnvDiff Error");
        }
    }

    private Map<String, String> parseEnvFile(File file) throws java.io.IOException {
        Map<String, String> map = new java.util.HashMap<>();
        if (!file.exists()) return map;

        for (String line : java.nio.file.Files.readAllLines(file.toPath())) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        return map;
    }

    private java.util.Set<String> loadIgnoreList(File file) {
        java.util.Set<String> ignored = new java.util.HashSet<>();
        if (!file.exists()) return ignored;

        try {
            String json = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            Matcher matcher = Pattern.compile("\"([^\"]+)\"").matcher(json);
            while (matcher.find()) {
                ignored.add(matcher.group(1));
            }
        } catch (java.io.IOException ignoredEx) {
            // ignore
        }

        return ignored;
    }
}
