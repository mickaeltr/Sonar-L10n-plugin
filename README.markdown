# Sonar L10n plugin

[Sonar](http://www.sonarsource.org/) plugin for L10n properties files analysis

## Configuration

### Source directories

 * Key: **sonar.l10n.sourceDirectories**
 * Description: comma-separated list of source directories
 * Default:

        src/main/resources, src/main/java

### Locales

 * Key: **sonar.l10n.locales**
 * Description: comma-separated list of locales
 * Default: *autodiscovered*

### Key prefixes excluded

 * Key: **sonar.l10n.excludeKeyPrefixes**
 * Description: comma-separated list of key prefixes excluded
 * Default: *none*

## Development

Build and run a Sonar instance with embedded database:

    mvn clean install org.codehaus.sonar:sonar-dev-maven-plugin::start-war
        -Dsonar.runtimeVersion=2.10
        -Dsonar.workDir=/tmp/sonar

Go to the Sonar instance at <http://localhost:9000/>:

 1. Login with admin/admin
 2. Go to Configuration > Quality profiles
 3. Create a new profile
 4. Make the profile the default
 5. Edit the profile, go to Coding rules
 6. Select the L10n plugin (any / any) and activate the rules

In the target project, run:

    mvn sonar:sonar
