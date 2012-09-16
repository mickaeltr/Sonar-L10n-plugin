package fr.gimmick.sonar.l10n.model;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Represents a bundle identifier: directory + name. The related file is directory/name.properties or
 * directory/name_locale.properties
 * @author Mickaël Tricot
 */
public final class BundleId {

    /** Directory */
    private final String directory;

    /** Name */
    private final String name;

    /**
     * Constructor
     * @param directory Directory
     * @param name Name
     */
    BundleId(String directory, String name) {
        this.directory = directory;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof BundleId && ObjectUtils.equals(getName(), ((BundleId) obj).getName()) &&
                ObjectUtils.equals(getDirectory(), ((BundleId) obj).getDirectory());

    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(getClass()) *
                (ObjectUtils.hashCode(getDirectory()) + ObjectUtils.hashCode(getName()));
    }

    /**
     * Directory getter
     * @return Directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Name getter
     * @return Name
     */
    public String getName() {
        return name;
    }
}