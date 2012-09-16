package fr.gimmick.sonar.l10n;

import fr.gimmick.sonar.l10n.rules.L10nRule;
import fr.gimmick.sonar.l10n.rules.L10nRule.Flag;
import fr.gimmick.sonar.l10n.utils.L10nUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Localization plugin configuration
 * @author Mickaël Tricot
 */
public final class L10nConfiguration {

    /** CSV splitter */
    private static final Pattern CSV_SPLITTER = Pattern.compile(",");

    /** File extension to check */
    public static final String FILE_EXTENSION = "properties";

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(L10nConfiguration.class);

    /** Property for excluding key prefixes from the analysis */
    public static final String PROPERTY_EXCLUDE_KEY_PREFIXES = "sonar.l10n.excludeKeyPrefixes";

    /** Property for locales */
    public static final String PROPERTY_LOCALES_KEY = "sonar.l10n.locales";

    /** Value for locale null */
    public static final String PROPERTY_LOCALES_VALUE_NULL = "null";

    /** Property for source directories */
    public static final String PROPERTY_SOURCE_DIRECTORIES_KEY = "sonar.l10n.sourceDirectories";

    /** Default value for source directories */
    public static final String PROPERTY_SOURCE_DIRECTORIES_VALUE = "src/main/resources, src/main/java";

    /**
     * Get active rule
     * @param profile Sonar rules profile
     * @param ruleClass Localization rule class
     * @return Active rule (nullable)
     */
    public static ActiveRule getActiveRule(RulesProfile profile, Class<? extends L10nRule> ruleClass) {
        return profile.getActiveRule(L10nPlugin.KEY, L10nUtils.getRuleKey(ruleClass));
    }

    /**
     * Get all flags for the active rules
     * @param profile Sonar rules profile
     * @return Flags
     */
    public static Collection<Flag> getActiveRuleFlags(RulesProfile profile) {
        Collection<Flag> flags = new ArrayList<Flag>();
        for (L10nRule rule : L10nRuleRepository.RULES) {
            ActiveRule activeRule = getActiveRule(profile, rule.getClass());
            if (activeRule != null) {
                flags.addAll(rule.getFlags());
            }
        }
        return flags;
    }

    /**
     * Get active rules from a list of localization rules
     * @param profile Sonar rules profile
     * @return Active rules from the list
     */
    public static Collection<ActiveRule> getActiveRules(RulesProfile profile) {
        Collection<ActiveRule> activeRules = new ArrayList<ActiveRule>();
        for (L10nRule rule : L10nRuleRepository.RULES) {
            ActiveRule activeRule = getActiveRule(profile, rule.getClass());
            if (activeRule != null) {
                activeRules.add(activeRule);
            }
        }
        return activeRules;
    }

    /**
     * Get the directories from the Sonar configuration
     * @param project Sonar project
     * @param settings Sonar settings
     * @return Directories
     */
    public static Collection<File> getConfigurationDirectories(Project project, Settings settings) {
        Collection<String> directoryPaths = L10nUtils.getCSV(settings, PROPERTY_SOURCE_DIRECTORIES_KEY);
        Collection<File> directories = new HashSet<File>(directoryPaths.size());
        for (String directoryPath : directoryPaths) {
            File directory = getConfigurationDirectory(project, directoryPath, true);
            if (directory != null) {
                directories.add(directory);
            }
        }
        if (directories.isEmpty()) {
            LOG.info("{}: no directory (properly) configured, falling back to default directories '{}'",
                    PROPERTY_SOURCE_DIRECTORIES_KEY, PROPERTY_SOURCE_DIRECTORIES_VALUE);
            for (String directoryPath : CSV_SPLITTER.split(PROPERTY_SOURCE_DIRECTORIES_VALUE)) {
                File directory = getConfigurationDirectory(project, directoryPath, false);
                if (directory != null) {
                    directories.add(directory);
                }
            }
        }
        LOG.info("{}: {}", PROPERTY_SOURCE_DIRECTORIES_KEY, directories);
        return Collections.unmodifiableCollection(directories);
    }

    /**
     * Get the directory from its relative path
     * @param project Sonar project
     * @param directoryPath Directory relative path (nullable)
     * @param log Log if the directory cannot be created?
     * @return Directory (nullable)
     */
    private static File getConfigurationDirectory(Project project, String directoryPath, boolean log) {
        String directoryPathTrimmed = StringUtils.trimToNull(directoryPath);
        File directory = null;
        if (directoryPathTrimmed != null) {
            directoryPathTrimmed = FilenameUtils.normalize(
                    project.getFileSystem().getBasedir().getPath() + File.separatorChar + directoryPathTrimmed);
            File directoryTemp = new File(directoryPathTrimmed);
            try {
                if (directoryTemp.exists() && directoryTemp.isDirectory() && directoryTemp.canExecute() &&
                        directoryTemp.canRead()) {
                    directory = directoryTemp;
                }
            } catch (SecurityException e) {
                LOG.error(PROPERTY_SOURCE_DIRECTORIES_KEY + ": cannot execute or read directory '" +
                        directoryPathTrimmed + '\'', e);
            }
            if (log && directory == null) {
                LOG.error("{}: invalid directory '{}'", PROPERTY_SOURCE_DIRECTORIES_KEY, directoryPathTrimmed);
            }
        }
        return directory;
    }

    /**
     * Get the locale from a String
     * @param locale String
     * @return Locale wrapper (nullable)
     */
    private static MutableObject<Locale> getConfigurationLocale(String locale) {
        String localeStringTrimmed = StringUtils.trimToNull(locale);
        MutableObject<Locale> localeWrapper = null;
        if (localeStringTrimmed != null) {
            if (PROPERTY_LOCALES_VALUE_NULL.equalsIgnoreCase(localeStringTrimmed)) {
                localeWrapper = new MutableObject<Locale>();
            } else {
                try {
                    localeWrapper = new MutableObject<Locale>(LocaleUtils.toLocale(localeStringTrimmed));
                } catch (IllegalArgumentException ignored) {
                    LOG.error("{}: cannot create locale '{}'", PROPERTY_LOCALES_KEY, locale);
                }
            }
        }
        return localeWrapper;
    }

    /**
     * Get the locales to check from the Sonar configuration
     * @param settings Sonar settings
     * @param defaultLocales Default locales (if empty or not found in the configuration)
     * @return Locales to check
     */
    public static Collection<Locale> getConfigurationLocales(Settings settings, Collection<Locale> defaultLocales) {
        Collection<String> localeStrings = L10nUtils.getCSV(settings, PROPERTY_LOCALES_KEY);
        Collection<Locale> locales = new HashSet<Locale>(localeStrings.size());
        for (String localeString : localeStrings) {
            MutableObject<Locale> localeWrapper = getConfigurationLocale(localeString);
            if (localeWrapper != null) {
                locales.add(localeWrapper.getValue());
            }
        }
        if (locales.isEmpty()) {
            locales = defaultLocales;
            LOG.info("{}: {}, no locale (properly) configured, falling back to locales auto-discovered",
                    PROPERTY_LOCALES_KEY, defaultLocales);
        } else {
            LOG.info("{}: {}", PROPERTY_LOCALES_KEY, locales);
        }
        return Collections.unmodifiableCollection(locales);
    }

    /**
     * Get the excluded key prefixes from the Sonar configuration
     * @param settings Sonar settings
     * @return Excluded key prefixes
     */
    public static Collection<String> getExcludedKeyPrefixes(Settings settings) {
        return L10nUtils.getCSV(settings, PROPERTY_EXCLUDE_KEY_PREFIXES);
    }

    /**
     * Get the files to process
     * @param directories Directories
     * @return Files
     */
    public static Collection<File> getFiles(Iterable<File> directories) {
        Collection<File> files = new ArrayList<File>();
        for (File directory : directories) {
            files.addAll(FileUtils.listFiles(directory, new String[]{FILE_EXTENSION}, true));
        }
        return files;
    }

    /**
     * Is the localization rule active?
     * @param profile Sonar rules profile
     * @param ruleClass Localization rule class
     * @return TRUE if the rule is active
     */
    public static boolean isActiveRule(RulesProfile profile, Class<? extends L10nRule> ruleClass) {
        return profile.getActiveRule(L10nPlugin.KEY, L10nUtils.getRuleKey(ruleClass)) != null;
    }

    /** Prevents from instantiation */
    private L10nConfiguration() {
    }
}