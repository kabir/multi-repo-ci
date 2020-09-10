package org.overbaard.ci.multi.repo.generator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class EnableGitLfsBuilder {
    private String workingDirectory;

    public EnableGitLfsBuilder setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    Map<String, Object> build() {
        Map<String, Object> command = new LinkedHashMap<>();
        command.put("name", "Enable Git lfs");
        if (workingDirectory != null) {
            command.put("working-directory", workingDirectory);
        }

        StringBuilder run = new StringBuilder();
        run.append("sudo apt-get install git-lfs\n");
        run.append("git lfs install\n");
        // This is needed to 'populate' the multi-repo-ci-tool.jar placeholder/pointer
        // with the real code (and the other lfs tracked files in the working directory).
        // Without it, it just contains 'metadata' about the file, e.g:
        //  $ more multi-repo-ci-tool.jar
        //  version https://git-lfs.github.com/spec/v1
        //  oid sha256:0535e69713f0095c91ef363f2d493d1acfc37f65351c570f744e1e6ef5af83e8
        //  size 857130
        run.append("git lfs pull\n");

        command.put("run", run.toString());

        return command;
    }
}
