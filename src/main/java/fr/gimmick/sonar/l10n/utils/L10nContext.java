package fr.gimmick.sonar.l10n.utils;

import java.util.Collection;
import java.util.Locale;

import com.google.common.collect.ImmutableList;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;

/**
 * Localization context, for rule violations check
 * @author Mickaël Tricot
 */
public final class L10nContext {

    /** Locales to check */
    private final Collection<Locale> locales;

    /** Sonar project */
    private final Project project;

    /** Sonar rules profile */
    private final RulesProfile rulesProfile;

    /** Sonar sensor context */
    private final SensorContext sensorContext;

    /**
     * Constructor
     * @param sensorContext Sensor context
     * @param locales Locales
     * @param rulesProfile Rules profile
     * @param project Project
     */
    public L10nContext(SensorContext sensorContext, Collection<Locale> locales, RulesProfile rulesProfile,
            Project project) {
        this.sensorContext = sensorContext;
        this.locales = ImmutableList.copyOf(locales);
        this.rulesProfile = rulesProfile;
        this.project = project;
    }

    /**
     * Locales getter
     * @return Locales
     */
    public Collection<Locale> getLocales() {
        return locales;
    }

    /**
     * Project getter
     * @return Project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Rules profile getter
     * @return Rules profile
     */
    public RulesProfile getRulesProfile() {
        return rulesProfile;
    }

    /**
     * Sensor context getter
     * @return Sensor context
     */
    public SensorContext getSensorContext() {
        return sensorContext;
    }
}
