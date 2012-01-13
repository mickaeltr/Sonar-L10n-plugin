package fr.gimmick.sonar.l10n.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.gimmick.sonar.l10n.rules.L10nRule.Flag;
import fr.gimmick.sonar.l10n.utils.L10nUtils;

/**
 * Builder for the BundleProject
 * @author Mickaël Tricot
 */
public final class BundleProjectBuilder {

    /** Bundle filename splitter */
    private static final Pattern FILENAME_SPLITTER =
            Pattern.compile(L10nUtils.FILENAME_NAME_LOCALE_SEPARATOR.toString());

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(BundleProjectBuilder.class);

    /**
     * Build the bundle project
     * @param files Files to process
     * @param excludedKeyPrefixes Key prefixes to exclude
     * @param flags Active rule flags
     * @return Bundle project
     */
    public static BundleProject build(Iterable<File> files, Collection<String> excludedKeyPrefixes,
            Collection<Flag> flags) {
        BundleProject bundleProject = new BundleProject();
        for (File file : files) {
            LOG.debug("Processing file '{}'", file);
            Bundle bundle = buildBundle(bundleProject, file, excludedKeyPrefixes, flags);
            if (bundle == null) {
                LOG.warn("File '{}' ignored", file);
            }
        }
        return bundleProject;
    }

    /**
     * Build a bundle
     * @param bundleProject Bundle project
     * @param file File to process
     * @param excludedKeyPrefixes Key prefixes to exclude
     * @param flags Active rule flags
     * @return Bundle
     */
    private static Bundle buildBundle(BundleProject bundleProject, File file, Collection<String> excludedKeyPrefixes,
            Collection<Flag> flags) {
        Bundle bundle = null;
        try {
            if (file != null && file.exists() && file.isFile() && file.canRead()) {

                String[] fileNameSplit = getFileNameSplit(file);
                String name = getBundleName(file, fileNameSplit);
                MutableObject<Locale> localeWrapper = getBundleLocale(file, fileNameSplit);

                MutableObject<Map<String, String>> propertiesWrapper = null;
                if (name != null && localeWrapper != null &&
                        (flags.contains(Flag.USES_VALUES) || flags.contains(Flag.USES_KEYS))) {
                    propertiesWrapper = getBundleProperties(file, flags);
                }
                boolean allKeysExcluded = excludeKeys(propertiesWrapper, excludedKeyPrefixes);

                bundle = buildBundle(bundleProject, file, name, localeWrapper, propertiesWrapper, allKeysExcluded);
            }
        } catch (SecurityException e) {
            LOG.error(e.getMessage(), e);
        }
        return bundle;
    }

    /**
     * Build a bundle
     * @param bundleProject BundleProject
     * @param file File
     * @param name Bundle name (nullable)
     * @param localeWrapper Bundle locale wrapper (nullable)
     * @param propertiesWrapper Bundle properties wrapper (nullable)
     * @param allKeysExcluded If all keys are excluded
     * @return Bundle
     */
    private static Bundle buildBundle(BundleProject bundleProject, File file, String name,
            MutableObject<Locale> localeWrapper, MutableObject<Map<String, String>> propertiesWrapper,
            boolean allKeysExcluded) {
        Bundle bundle = null;
        if (name != null && localeWrapper != null && propertiesWrapper != null) {
            bundleProject.getLocales().add(localeWrapper.getValue());
            BundleId id = new BundleId(file.getParent(), name);
            bundle = bundleProject.getBundles().get(id);
            if (bundle == null) {
                bundle = new Bundle(id);
                bundleProject.getBundles().put(id, bundle);
            }
            bundle.getFiles()
                    .put(localeWrapper.getValue(), new BundleFile(propertiesWrapper.getValue(), allKeysExcluded));
            bundle.getKeys().addAll(propertiesWrapper.getValue().keySet());
            LOG.debug("Bundle created with name '{}', locale '{}', properties {} for file '{}'",
                    new Object[]{name, localeWrapper.getValue(), propertiesWrapper.getValue(), file});
        }
        return bundle;
    }

    /**
     * Remove excluded keys
     * @param propertiesWrapper Properties wrapper
     * @param excludedKeyPrefixes Excluded key prefixes
     * @return TRUE if all the properties have been excluded
     */
    private static boolean excludeKeys(Mutable<Map<String, String>> propertiesWrapper,
            Collection<String> excludedKeyPrefixes) {
        if (propertiesWrapper != null && propertiesWrapper.getValue() != null &&
                !propertiesWrapper.getValue().isEmpty() && !excludedKeyPrefixes.isEmpty()) {
            excludeKeys(propertiesWrapper.getValue().entrySet().iterator(), excludedKeyPrefixes);
            if (propertiesWrapper.getValue().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove excluded keys
     * @param propertiesIterator Properties iterator
     * @param excludedKeyPrefixes Excluded key prefixes
     */
    private static void excludeKeys(Iterator<Entry<String, String>> propertiesIterator,
            Collection<String> excludedKeyPrefixes) {
        while (propertiesIterator != null && propertiesIterator.hasNext()) {
            Entry<String, String> property = propertiesIterator.next();
            if (property != null && property.getKey() != null) {
                Iterator<String> j = excludedKeyPrefixes.iterator();
                boolean removed = false;
                while (!removed && j.hasNext()) {
                    if (property.getKey().startsWith(j.next())) {
                        propertiesIterator.remove();
                        removed = true;
                    }
                }
            }
        }
    }

    /**
     * Extract the bundle locale from the file(name)
     * @param file File
     * @param fileNameSplit Filename split (nullable)
     * @return Locale wrapper (nullable)
     */
    private static MutableObject<Locale> getBundleLocale(File file, String... fileNameSplit) {
        MutableObject<Locale> localeWrapper = null;
        if (fileNameSplit != null && fileNameSplit.length > 1) {
            try {
                localeWrapper = new MutableObject<Locale>(LocaleUtils.toLocale(fileNameSplit[1]));
            } catch (IllegalArgumentException e) {
                LOG.error("Invalid bundle locale '{}' for file '{}'", fileNameSplit[1], file);
            }
        } else {
            localeWrapper = new MutableObject<Locale>();
        }
        return localeWrapper;
    }

    /**
     * Extract the bundle name from the file(name)
     * @param file File
     * @param fileNameSplit Filename split
     * @return Name (nullable)
     */
    private static String getBundleName(File file, String... fileNameSplit) {
        String name = null;
        if (fileNameSplit != null) {
            name = StringUtils.trimToNull(fileNameSplit[0]);
            if (name == null) {
                LOG.error("Invalid (empty) bundle name for file '{}'", file);
            }
        }
        return name;
    }

    /**
     * Extract the bundle properties from the file
     * @param file File
     * @param flags Active rule flags
     * @return Properties wrapper (nullable)
     */
    private static MutableObject<Map<String, String>> getBundleProperties(File file, Collection<Flag> flags) {
        MutableObject<Map<String, String>> propertiesWrapper = null;
        InputStream is = null;
        try {
            Properties properties = new Properties();
            is = FileUtils.openInputStream(file);
            properties.load(is);
            propertiesWrapper = new MutableObject<Map<String, String>>(
                    L10nUtils.toMap(properties, !flags.contains(Flag.USES_VALUES)));
        } catch (IOException e) {
            LOG.error("Error while processing file " + file, e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return propertiesWrapper;
    }

    /**
     * Split the filename, to extract the name and locale
     * @param file File
     * @return Filename split
     */
    private static String[] getFileNameSplit(File file) {
        String[] fileNameSplit = null;
        String fileBaseName = FilenameUtils.getBaseName(file.getName());
        if (fileBaseName != null && !fileBaseName.isEmpty()) {
            fileNameSplit = FILENAME_SPLITTER.split(fileBaseName, 2);
        }
        if (ArrayUtils.isEmpty(fileNameSplit)) {
            fileNameSplit = null;
            LOG.error("Invalid bundle file name '{}'", file);
        }
        return fileNameSplit;
    }

    /** Constructor (prevents from instantiation) */
    private BundleProjectBuilder() {
    }
}