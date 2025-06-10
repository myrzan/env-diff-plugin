package kz.kolesa;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class EnvDiffConfigurable implements Configurable {
    private JTextField envFileField;
    private JTextField testingFileField;
    private JTextField ignoreFileField;

    @Override
    public @Nls String getDisplayName() {
        return "EnvDiff Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        EnvDiffSettings.State state = EnvDiffSettings.getInstance().getState();

        assert state != null;
        envFileField = createField(state.envFile);
        testingFileField = createField(state.testingFile);
        ignoreFileField = createField(state.ignoreFile);

        panel.add(new JLabel("Local env filename:"));
        panel.add(envFileField);
        panel.add(new JLabel("Testing env filename:"));
        panel.add(testingFileField);
        panel.add(new JLabel("Ignore list filename:"));
        panel.add(ignoreFileField);

        return panel;
    }

    private JTextField createField(String initialValue) {
        JTextField field = new JTextField(initialValue);
        Dimension preferredSize = new Dimension(400, 25); // ширина и высота
        field.setMaximumSize(preferredSize);
        field.setPreferredSize(preferredSize);
        return field;
    }

    @Override
    public boolean isModified() {
        EnvDiffSettings.State state = EnvDiffSettings.getInstance().getState();
        assert state != null;
        return !envFileField.getText().equals(state.envFile) ||
                !testingFileField.getText().equals(state.testingFile) ||
                !ignoreFileField.getText().equals(state.ignoreFile);
    }

    @Override
    public void apply() {
        EnvDiffSettings.State state = EnvDiffSettings.getInstance().getState();
        assert state != null;
        state.envFile = envFileField.getText();
        state.testingFile = testingFileField.getText();
        state.ignoreFile = ignoreFileField.getText();
    }

    @Override
    public void reset() {
        EnvDiffSettings.State state = EnvDiffSettings.getInstance().getState();
        assert state != null;
        envFileField.setText(state.envFile);
        testingFileField.setText(state.testingFile);
        ignoreFileField.setText(state.ignoreFile);
    }
}
