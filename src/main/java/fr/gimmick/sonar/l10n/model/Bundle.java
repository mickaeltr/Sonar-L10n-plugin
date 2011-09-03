package fr.gimmick.sonar.l10n.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Represents a bundle: set of L10n properties files
 * @author Mickaël Tricot
 */
public final class Bundle {

    /** Files: locale -> properties */
    private final Map<Locale, BundleFile> files;

    /** ID */
    private final BundleId id;

    /** Keys for all the files */
    private final Collection<String> keys;

    /**
     * Constructor
     * @param id ID
     */
    public Bundle(BundleId id) {
        this.id = id;
        keys = new HashSet<String>();
        files = new HashMap<Locale, BundleFile>();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Bundle && ObjectUtils.equals(getId(), ((Bundle) o).getId());

    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(getId());
    }

    /**
     * Files getter
     * @return Files
     */
    public Map<Locale, BundleFile> getFiles() {
        return files;
    }

    /**
     * ID getter
     * @return ID
     */
    public BundleId getId() {
        return id;
    }

    /**
     * Keys getter
     * @return Keys
     */
    public Collection<String> getKeys() {
        return keys;
    }
}