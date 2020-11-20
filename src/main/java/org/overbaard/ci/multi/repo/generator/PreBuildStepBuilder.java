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

        if (context.hasDependencies() || !context.isBuildJob()) {
            // We rely on snapshots. The pre-build-action wants to put them into ~/m2-repository
            // but has no access to that location on the runner so we mount it into the workspace
            // folder so that it can write to it
            Map<String, Object> mountM2Repository = createMountStep();
            list.add(mountM2Repository);
        }

        // Add the step
        Map<String, Object> preBuildStep = new LinkedHashMap<>();
        preBuildStep.put("id", id);
        preBuildStep.put("uses", action);
        preBuildStep.put("with", buildWith());
        list.add(preBuildStep);

        if (context.hasDependencies() || !context.isBuildJob()) {
            // OLD WAY - keep in case the mounting I am doing bends too many rules
            // gets disabled in the future
            /*
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
            */
        }

        return list;
    }

    private Map<String, Object> buildWith() {
        Map<String, Object> with = new LinkedHashMap<>();
        with.put("build", context.isBuildJob() ? 1 : 0);
        with.put("custom", context.isCustomComponentJob() ? 1 : 0);
        return with;
    }

    static Map<String, Object> createMountStep() {
        Map<String, Object> mountM2Repository = new LinkedHashMap<>();
        mountM2Repository.put("name", "Mount ~/.m2/repository to .m2-repo-mount since the actions can't access ~/.m2 directly");
        mountM2Repository.put("run", "mkdir .m2-repo-mount && sudo mount --rbind ~/.m2/repository/ .m2-repo-mount");
        return mountM2Repository;
    }
}
