package kz.kolesa;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "EnvDiffSettings", storages = @Storage("envDiffSettings.xml"))
public class EnvDiffSettings implements PersistentStateComponent<EnvDiffSettings.State> {
    public static class State {
        public String envFile = ".env";
        public String testingFile = "env.testing";
        public String ignoreFile = ".envdiffignore.json";
    }

    private State state = new State();

    @Override
    public @Nullable State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public static EnvDiffSettings getInstance() {
        return com.intellij.openapi.application.ApplicationManager.getApplication().getService(EnvDiffSettings.class);
    }
}
