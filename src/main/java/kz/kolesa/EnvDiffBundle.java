package kz.kolesa;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class EnvDiffBundle {
    @NonNls
    private static final String BUNDLE_NAME = "kz.kolesa.messages"; // путь без .properties

    private static ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    static {
        Locale locale = Locale.getDefault();
        // Принудительно обрезаем региональную часть: ru_KZ → ru
        Locale normalizedLocale = new Locale(locale.getLanguage());

        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("kz.kolesa.messages", normalizedLocale);
        } catch (MissingResourceException e) {
            bundle = ResourceBundle.getBundle("kz.kolesa.messages", Locale.ENGLISH);
        }

        BUNDLE = bundle;
    }

    private EnvDiffBundle() {
        // Utility class — no instantiation
    }

    public static String message(@NotNull String key, @NotNull Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
    }
}
