package fr.gimmick.sonar.l10n.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.check.Cardinality;

import fr.gimmick.sonar.l10n.L10nConfiguration;
import fr.gimmick.sonar.l10n.L10nPlugin;
import fr.gimmick.sonar.l10n.model.Bundle;
import fr.gimmick.sonar.l10n.model.BundleProject;
import fr.gimmick.sonar.l10n.utils.L10nContext;
import fr.gimmick.sonar.l10n.utils.L10nUtils;

/**
 * Missing bundle localization rule
 * @author Mickaël Tricot
 */
public final class MissingBundleRule implements L10nRule {

    /** Sonar rule */
    private final Rule rule;

    /** Constructor */
    public MissingBundleRule() {
        rule = Rule.create();
        rule.setUniqueKey(L10nPlugin.KEY, L10nUtils.getRuleKey(getClass()));
        rule.setName("Missing L10n bundle");
        rule.setCardinality(Cardinality.MULTIPLE);
        rule.setSeverity(RulePriority.CRITICAL);
        rule.setDescription("Localization bundle that is expected to be available");
    }

    /** {@inheritDoc} */
    @Override
    public void checkViolations(BundleProject project, L10nContext context) {
        for (Bundle bundle : project.getBundles().values()) {
            Collection<Locale> missingLocales = new HashSet<Locale>(context.getLocales());
            missingLocales.removeAll(bundle.getFiles().keySet());
            for (Locale locale : missingLocales) {
                Violation violation = Violation
                        .create(L10nConfiguration.getActiveRule(context.getRulesProfile(), getClass()),
                                L10nUtils.getResource(context.getProject(), bundle, locale));
                violation.setMessage("Bundle '" + bundle.getId().getName() + "' is missing" +
                        (locale != null ? " for the locale '" + locale + '\'' : StringUtils.EMPTY));
                context.getSensorContext().saveViolation(violation);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Flag> getFlags() {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public Rule getRule() {
        return rule;
    }
}