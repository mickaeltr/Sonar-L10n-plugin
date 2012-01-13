package fr.gimmick.sonar.l10n;

import java.io.File;
import java.util.Collection;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;

import fr.gimmick.sonar.l10n.model.BundleProject;
import fr.gimmick.sonar.l10n.model.BundleProjectBuilder;
import fr.gimmick.sonar.l10n.rules.L10nRule;
import fr.gimmick.sonar.l10n.rules.L10nRule.Flag;
import fr.gimmick.sonar.l10n.utils.L10nContext;

/**
 * Localization sensor
 * @author Mickaël Tricot
 */
public final class L10nSensor implements Sensor {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(L10nSensor.class);

    /** Sonar rules profile */
    private final RulesProfile profile;

    /** Sonar settings */
    private final Settings settings;

    /**
     * Constructor
     * @param profile Sonar rules profile
     * @param settings Sonar settings
     */
    public L10nSensor(RulesProfile profile, Settings settings) {
        this.profile = profile;
        this.settings = settings;
    }

    /** {@inheritDoc} */
    @Override
    public void analyse(Project project, SensorContext context) {

        if (L10nConfiguration.getActiveRules(profile).isEmpty()) {
            LOG.info("No active {} rule", L10nPlugin.KEY);
            return;
        }

        Collection<File> directories = L10nConfiguration.getConfigurationDirectories(project, settings);
        Collection<File> files = L10nConfiguration.getFiles(directories);
        Collection<String> excludedKeyPrefixes = L10nConfiguration.getExcludedKeyPrefixes(settings);
        Collection<Flag> flags = L10nConfiguration.getActiveRuleFlags(profile);
        BundleProject l10nProject = BundleProjectBuilder.build(files, excludedKeyPrefixes, flags);
        Collection<Locale> locales = L10nConfiguration.getConfigurationLocales(settings, l10nProject.getLocales());
        L10nContext configuration = new L10nContext(context, locales, profile, project);

        for (L10nRule rule : L10nRuleRepository.RULES) {
            if (L10nConfiguration.isActiveRule(profile, rule.getClass())) {
                rule.checkViolations(l10nProject, configuration);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldExecuteOnProject(Project project) {
        // this sensor is executed on any type of project
        return true;
    }
}