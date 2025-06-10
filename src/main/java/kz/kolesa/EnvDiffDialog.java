package kz.kolesa;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class EnvDiffDialog extends DialogWrapper {

    private final String diff;

    public EnvDiffDialog(@Nullable Project project, String diff) {
        super(project);
        this.diff = diff;
        setTitle("EnvDiff: Differences Found");
        init(); // обязательный вызов
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JTextArea textArea = new JTextArea(diff);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        return scrollPane;
    }
}
