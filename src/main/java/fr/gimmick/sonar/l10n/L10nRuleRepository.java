package fr.gimmick.sonar.l10n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;

import fr.gimmick.sonar.l10n.rules.L10nRule;
import fr.gimmick.sonar.l10n.rules.MissingBundleRule;
import fr.gimmick.sonar.l10n.rules.MissingKeyRule;
import fr.gimmick.sonar.l10n.rules.MissingValueRule;
import fr.gimmick.sonar.l10n.rules.UnusedBundleRule;

/**
 * Localization rule repository
 * @author Mickaël Tricot
 */
public final class L10nRuleRepository extends RuleRepository {

    /** Localization rules */
    public static final Collection<L10nRule> RULES = ImmutableList
            .of(new MissingBundleRule(), new MissingKeyRule(), new MissingValueRule(), new UnusedBundleRule());

    /** Constructor */
    public L10nRuleRepository() {
        super(L10nPlugin.KEY, Java.KEY);
        setName(L10nPlugin.KEY);
    }

    /** {@inheritDoc} */
    @Override
    public List<Rule> createRules() {
        List<Rule> rules = new ArrayList<Rule>(RULES.size());
        for (L10nRule rule : RULES) {
            rules.add(rule.getRule());
        }
        return rules;
    }
}
