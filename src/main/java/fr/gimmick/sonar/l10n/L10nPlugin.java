package fr.gimmick.sonar.l10n;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

/**
 * Localization plugin
 * @author Mickaël Tricot
 */
@Properties({@Property(key = L10nConfiguration.PROPERTY_LOCALES_KEY, name = "Locales",
        description = "Comma-separated list of locales (autodiscovered by default)"),
        @Property(key = L10nConfiguration.PROPERTY_SOURCE_DIRECTORIES_KEY, name = "Source directories",
                description = "Comma-separated list of source directories.",
                defaultValue = L10nConfiguration.PROPERTY_SOURCE_DIRECTORIES_VALUE),
        @Property(key = L10nConfiguration.PROPERTY_EXCLUDE_KEY_PREFIXES, name = "Key prefixes excluded",
                description = "Comma-separated list of key prefixes excluded")})
public final class L10nPlugin extends SonarPlugin {

    /** Plugin key */
    public static final String KEY = "L10n";

    /** {@inheritDoc} */
    @Override
    public List getExtensions() {
        return ImmutableList.of(L10nRuleRepository.class, L10nSensor.class);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}