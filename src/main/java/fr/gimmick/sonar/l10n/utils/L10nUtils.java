package fr.gimmick.sonar.l10n.utils;

import fr.gimmick.sonar.l10n.L10nConfiguration;
import fr.gimmick.sonar.l10n.model.Bundle;
import fr.gimmick.sonar.l10n.rules.L10nRule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Localization utils
 * @author Mickaël Tricot
 */
public final class L10nUtils {

    /** Filename name/locale separator */
    public static final Character FILENAME_NAME_LOCALE_SEPARATOR = '_';

    /** Collection transformer: trim to null all String */
    private static final Transformer TRANSFORMER_STRING_TRIM_TO_NULL = new Transformer() {
        /** {@inheritDoc} */
        @Override
        public String transform(Object excludedKeyPrefix) {
            if (excludedKeyPrefix instanceof String) {
                return StringUtils.trimToNull((String) excludedKeyPrefix);
            }
            return null;
        }
    };

    /**
     * Get comma-separated values from the Sonar configuration
     * @param settings Sonar settings
     * @param property Configuration property
     * @return Values
     */
    public static Collection<String> getCSV(Settings settings, String property) {
        Collection<String> csv = new HashSet<String>(Arrays.asList(settings.getStringArray(property)));
        CollectionUtils.transform(csv, TRANSFORMER_STRING_TRIM_TO_NULL);
        csv.remove(null);
        return Collections.unmodifiableCollection(csv);
    }

    /**
     * Get the Sonar resource for a bundle and a locale
     * @param project Sonar project
     * @param bundle Bundle
     * @param locale Locale
     * @return Sonar resource
     */
    public static Resource<?> getResource(Project project, Bundle bundle, Locale locale) {
        StringBuilder builder = new StringBuilder(bundle.getId().getName());
        if (locale != null) {
            builder.append(FILENAME_NAME_LOCALE_SEPARATOR).append(locale.toString());
        }
        String filename =
                builder.append(FilenameUtils.EXTENSION_SEPARATOR).append(L10nConfiguration.FILE_EXTENSION).toString();
        return new File(Java.INSTANCE,
                bundle.getId().getDirectory().substring(project.getFileSystem().getBasedir().getPath().length() + 1),
                filename);
    }

    /**
     * Get the localization rule key
     * @param ruleClass Localization rule class
     * @return Rule key
     */
    public static String getRuleKey(Class<? extends L10nRule> ruleClass) {
        return ruleClass.getSimpleName();
    }

    /**
     * Convert Properties to Map
     * @param properties Properties
     * @param onlyKeys Only keep the keys? (set the values to null)
     * @return Map
     */
    public static Map<String, String> toMap(Properties properties, boolean onlyKeys) {
        Map<String, String> map = new HashMap<String, String>(properties.size());
        for (Entry<Object, Object> property : properties.entrySet()) {
            if (property.getKey() instanceof String && property.getValue() instanceof String) {
                map.put((String) property.getKey(), onlyKeys ? null : (String) property.getValue());
            }
        }
        return map;
    }

    /** Constructor (prevents from instantiation) */
    private L10nUtils() {
    }
}