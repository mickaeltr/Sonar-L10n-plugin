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

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof BundleId && ObjectUtils.equals(getName(), ((BundleId) o).getName()) &&
                ObjectUtils.equals(getDirectory(), ((BundleId) o).getDirectory());

    }

    /** {@inheritDoc} */
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