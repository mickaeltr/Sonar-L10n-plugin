package fr.gimmick.sonar.l10n.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a set of bundles
 * @author Mickaël Tricot
 */
public final class BundleProject {

    /** Bundles */
    private final Map<BundleId, Bundle> bundles;

    /** Locales for all the bundle */
    private final Collection<Locale> locales;

    /** Constructor */
    BundleProject() {
        locales = new HashSet<Locale>();
        bundles = new HashMap<BundleId, Bundle>();
    }

    /**
     * Bundles getter
     * @return Bundles
     */
    public Map<BundleId, Bundle> getBundles() {
        return bundles;
    }

    /**
     * Locales getter
     * @return Locales
     */
    public Collection<Locale> getLocales() {
        return locales;
    }
}
