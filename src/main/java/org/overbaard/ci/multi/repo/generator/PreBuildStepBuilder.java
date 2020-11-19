package org.overbaard.ci.multi.repo.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.overbaard.ci.multi.repo.generator.GitHubActionGenerator.ComponentJobContext;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
class PreBuildStepBuilder {
    private final String action;
    private final String id;
    private final ComponentJobContext context;

    public PreBuildStepBuilder(String action, String id, ComponentJobContext context) {
        this.action = action;
        this.id = id;
        this.context = context;
    }

    List<Map<String, Object>> build() {
        List<Map<String, Object>> list = new ArrayList<>();

        // Add the step
        Map<String, Object> preBuildStep = new LinkedHashMap<>();
        preBuildStep.put("id", id);
        preBuildStep.put("uses", action);
        preBuildStep.put("with", buildWith());
        list.add(preBuildStep);

        if (context.hasDependencies() || !context.isBuildJob()) {
            // We rely on snapshots. The pre-build-action puts them somewhere nice for us
            // but has no access to the ~/.m2 directory so we need to handle that ourselves.
            // Essentially we want to overlay them on the ~/.m2/repository directory
            Map<String, Object> copySnapshots = new LinkedHashMap<>();
            // snapshots-tar is one of the outputs defined in the action
            String outputExpr = String.format("${{steps.%s.outputs.snapshots-tar}}", id);
            copySnapshots.put("name", "Overlaying downloaded snapshots onto ~/.m2/repository");
            copySnapshots.put("env", Collections.singletonMap("TAR_NAME", outputExpr));
            copySnapshots.put("run", "cd ~/.m2/repository && mv ${GITHUB_WORKSPACE}/${TAR_NAME} . && tar xfzv ${TAR_NAME}");
            list.add(copySnapshots);
        }

        return list;
    }

    private Map<String, Object> buildWith() {
        Map<String, Object> with = new LinkedHashMap<>();
        with.put("build", context.isBuildJob() ? 1 : 0);
        with.put("custom", context.isCustomComponentJob() ? 1 : 0);
        return with;
    }
}
