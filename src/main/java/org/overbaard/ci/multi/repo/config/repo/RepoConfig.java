package org.overbaard.ci.multi.repo.config.repo;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class RepoConfig {
    static final boolean DEFAULT_GIT_LFS = false;
    private final Map<String, String> env;
    private final String javaVersion;
    private final boolean gitLfs;

    RepoConfig(Map<String, String> env, String javaVersion, boolean gitLfs) {
        this.env = env;
        this.javaVersion = javaVersion;
        this.gitLfs = gitLfs;
    }

    RepoConfig() {
        this.env = Collections.emptyMap();
        this.javaVersion = null;
        this.gitLfs = DEFAULT_GIT_LFS;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public boolean isGitLfs() {
        return gitLfs;
    }
}
