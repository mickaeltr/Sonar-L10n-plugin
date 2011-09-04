package fr.gimmick.sonar.l10n.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.check.Cardinality;

import fr.gimmick.sonar.l10n.L10nConfiguration;
import fr.gimmick.sonar.l10n.L10nPlugin;
import fr.gimmick.sonar.l10n.model.Bundle;
import fr.gimmick.sonar.l10n.model.BundleFile;
import fr.gimmick.sonar.l10n.model.BundleProject;
import fr.gimmick.sonar.l10n.utils.L10nContext;
import fr.gimmick.sonar.l10n.utils.L10nUtils;

/**
 * Unused bundle localization rule (for empty bundles or non supported locales)
 * @author Mickaël Tricot
 */
public final class UnusedBundleRule implements L10nRule {

    /** Sonar rule */
    private final Rule rule;

    /** Constructor */
    public UnusedBundleRule() {
        rule = Rule.create();
        rule.setUniqueKey(L10nPlugin.KEY, L10nUtils.getRuleKey(getClass()));
        rule.setName("Unused L10n bundle");
        rule.setCardinality(Cardinality.MULTIPLE);
        rule.setSeverity(RulePriority.MINOR);
        rule.setDescription("Localization bundle that is not expected to be available");
    }

    /** {@inheritDoc} */
    @Override
    public void checkViolations(BundleProject project, L10nContext context) {
        for (Bundle bundle : project.getBundles().values()) {
            if (bundle.getKeys().isEmpty()) {
                for (Entry<Locale, BundleFile> file : bundle.getFiles().entrySet()) {
                    if (!file.getValue().isAllKeysExcluded()) {
                        Violation violation = Violation
                                .create(L10nConfiguration.getActiveRule(context.getRulesProfile(), getClass()),
                                        L10nUtils.getResource(context.getProject(), bundle, file.getKey()));
                        violation.setMessage("Bundle '" + bundle.getId().getName() + "' is empty for all locales");
                        context.getSensorContext().saveViolation(violation);
                    }
                }
            } else {
                Collection<Locale> unusedLocales = new HashSet<Locale>(bundle.getFiles().keySet());
                unusedLocales.removeAll(context.getLocales());
                for (Locale locale : unusedLocales) {
                    Violation violation = Violation
                            .create(L10nConfiguration.getActiveRule(context.getRulesProfile(), getClass()),
                                    L10nUtils.getResource(context.getProject(), bundle, locale));
                    violation.setMessage("Bundle '" + bundle.getId().getName() + "' is unused" +
                            (locale != null ? " for the locale '" + locale + '\'' : StringUtils.EMPTY));
                    context.getSensorContext().saveViolation(violation);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Flag> getFlags() {
        return ImmutableList.of(Flag.USES_KEYS);
    }

    /** {@inheritDoc} */
    @Override
    public Rule getRule() {
        return rule;
    }
}