package fr.gimmick.sonar.l10n.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;
import org.sonar.api.resources.Resource;
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
 * Missing key localization rule
 * @author Mickaël Tricot
 */
public final class MissingKeyRule implements L10nRule {

    /** Sonar rule */
    private final Rule rule;

    /** Constructor */
    public MissingKeyRule() {
        rule = Rule.create();
        rule.setUniqueKey(L10nPlugin.KEY, L10nUtils.getRuleKey(getClass()));
        rule.setName("Missing L10n key");
        rule.setCardinality(Cardinality.MULTIPLE);
        rule.setSeverity(RulePriority.CRITICAL);
        rule.setDescription("Localization key that is expected to be available");
    }

    /** {@inheritDoc} */
    @Override
    public void checkViolations(BundleProject project, L10nContext context) {
        for (Bundle bundle : project.getBundles().values()) {
            for (Entry<Locale, BundleFile> file : bundle.getFiles().entrySet()) {
                if (context.getLocales().contains(file.getKey())) {
                    Collection<String> missingKeys = new HashSet<String>(bundle.getKeys());
                    missingKeys.removeAll(file.getValue().getProperties().keySet());
                    if (!missingKeys.isEmpty()) {
                        Resource resource = L10nUtils.getResource(context.getProject(), bundle, file.getKey());
                        for (String key : missingKeys) {
                            Violation violation = Violation
                                    .create(L10nConfiguration.getActiveRule(context.getRulesProfile(), getClass()),
                                            resource);
                            violation.setMessage("Key '" + key + "' is not translated" +
                                    (file.getKey() != null ? " for the locale '" + file.getKey() + '\'' :
                                            StringUtils.EMPTY));
                            context.getSensorContext().saveViolation(violation);
                        }
                    }
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
