package fr.gimmick.sonar.l10n.rules;

import java.util.Collection;

import org.sonar.api.rules.Rule;

import fr.gimmick.sonar.l10n.model.BundleProject;
import fr.gimmick.sonar.l10n.utils.L10nContext;

/**
 * Localization rule
 * @author Mickaël Tricot
 */
public interface L10nRule {

    /** Flags indicating which information needs to be retrieved in order to check the violations */
    enum Flag {
        /** Need to process the bundle keys */
        USES_KEYS,
        /** Need to process the bundle values */
        USES_VALUES
    }

    /**
     * Check the violations
     * @param project Bundle project
     * @param context Localization context
     */
    void checkViolations(BundleProject project, L10nContext context);

    /**
     * Flags getter
     * @return Flags
     */
    Collection<Flag> getFlags();

    /**
     * Sonar rule getter
     * @return Rule
     */
    Rule getRule();
}