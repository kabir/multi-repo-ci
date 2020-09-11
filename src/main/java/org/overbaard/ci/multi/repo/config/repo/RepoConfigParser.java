package org.overbaard.ci.multi.repo.config.repo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.overbaard.ci.multi.repo.config.BaseParser;
import org.yaml.snakeyaml.Yaml;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class RepoConfigParser extends BaseParser {
    private final Path yamlFile;

    private RepoConfigParser(Path yamlFile) {
        this.yamlFile = yamlFile;
    }

    public static RepoConfigParser create(Path yamlFile) {
        return new RepoConfigParser(yamlFile);
    }

    public RepoConfig parse() throws Exception {
        if (Files.exists(yamlFile)) {
            System.out.println("Parsing repository config: " + yamlFile);
        } else {
            System.err.println("No " + yamlFile + " found. Proceeding without a global repo config");
            return new RepoConfig();
        }
        Map<String, Object> input = null;
        try {
            Yaml yaml = new Yaml();
            input = yaml.load(new BufferedInputStream(new FileInputStream(yamlFile.toFile())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Object envInput = input.remove("env");
        Object javaVersionInput = input.remove("java-version");
        Object gitLfsInput = input.remove("git-lfs");

        if (input.size() > 0) {
            throw new IllegalStateException("Unknown entries: " + input.keySet());
        }
        boolean gitLfs = RepoConfig.DEFAULT_GIT_LFS;
        if (gitLfsInput != null) {
            if (!(gitLfsInput instanceof Boolean)) {
                throw new IllegalStateException("'git-lfs' must be a boolean (true or false without quotes)");
            }
            gitLfs = ((Boolean) gitLfsInput).booleanValue();
        }

        Map<String, String> env = parseEnv(envInput);
        String javaVersion = parseJavaVersion(javaVersionInput);

        return new RepoConfig(env, javaVersion, gitLfs);
    }
}
