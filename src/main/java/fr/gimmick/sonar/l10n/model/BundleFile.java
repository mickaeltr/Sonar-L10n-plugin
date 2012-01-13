package fr.gimmick.sonar.l10n.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Represents a bundle file
 * @author Mickaël Tricot
 */
public final class BundleFile {

    /** If all the keys have been excluded: properties are empty, but they are actually not! */
    private final boolean allKeysExcluded;

    /** Properties */
    private final Map<String, String> properties;

    /**
     * Construction
     * @param properties Properties
     * @param allKeysExcluded All keys excluded?
     */
    BundleFile(Map<String, String> properties, boolean allKeysExcluded) {
        if (allKeysExcluded) {
            assert properties.isEmpty();
        }
        this.properties = ImmutableMap.copyOf(properties);
        this.allKeysExcluded = allKeysExcluded;
    }

    /**
     * Properties getter
     * @return Properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * All keys excluded getter
     * @return All keys excluded?
     */
    public boolean isAllKeysExcluded() {
        return allKeysExcluded;
    }
}