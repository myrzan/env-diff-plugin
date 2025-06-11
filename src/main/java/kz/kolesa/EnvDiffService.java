package kz.kolesa;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.editor.Document;

import java.io.File;
import java.util.*;

public class EnvDiffService {
    public static Optional<String> generateDiff(Project project) {
        try {
            EnvDiffSettings.State cfg = EnvDiffSettings.getInstance().getState();
            File base = new File(Objects.requireNonNull(project.getBasePath()));

            assert cfg != null;
            Map<String, String> env = EnvDiffUtils.parseEnvFile(new File(base, cfg.envFile));
            Map<String, String> envTesting = EnvDiffUtils.parseEnvFile(new File(base, cfg.testingFile));
            Set<String> ignore = new HashSet<>();

            String[] possibleIgnoreFiles = {cfg.ignoreFile, "envdiffignore.default.json"};
            for (String filename : possibleIgnoreFiles) {
                File ignorePath = new File(base, filename);
                VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(ignorePath);
                if (vf != null) {
                    FileDocumentManager fdm = FileDocumentManager.getInstance();
                    Document doc = fdm.getDocument(vf);
                    if (doc != null) {
                        ApplicationManager.getApplication().invokeLater(() ->
                                ApplicationManager.getApplication().runWriteAction(() ->
                                        fdm.saveDocument(doc)
                                )
                        );
                        break;
                    }
                }
            }

            File userIgnoreFile = new File(base, cfg.ignoreFile);

            if (userIgnoreFile.exists()) {
                ignore.addAll(EnvDiffUtils.loadIgnoreList(userIgnoreFile));
            } else {
                ignore.addAll(EnvDiffUtils.loadIgnoreList(new File(base, "envdiffignore.default.json")));
            }

            Set<String> allKeys = new HashSet<>();
            allKeys.addAll(env.keySet());
            allKeys.addAll(envTesting.keySet());

            StringBuilder diff = new StringBuilder();

            for (String key : allKeys) {
                if (ignore.contains(key)) continue;

                String v1 = env.get(key);
                String v2 = envTesting.get(key);

                if (!Objects.equals(v1, v2)) {
                    diff.append(key).append(": ").append(v1).append(" ≠ ").append(v2).append("\n");
                }
            }

            return !diff.isEmpty() ? Optional.of(diff.toString()) : Optional.empty();
        } catch (Exception e) {
            return Optional.of("❌ Error during diff: " + e.getMessage());
        }
    }
}
