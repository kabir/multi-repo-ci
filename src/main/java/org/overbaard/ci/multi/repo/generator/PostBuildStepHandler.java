package org.overbaard.ci.multi.repo.generator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.overbaard.ci.multi.repo.generator.GitHubActionGenerator.ComponentJobContext;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class PostBuildStepHandler {
    private final String action;
    private final ComponentJobContext context;
    public static final String SNAPSHOTS_TGZ = ".snapshots.tgz";

    public PostBuildStepHandler(String action, ComponentJobContext context) {
        this.action = action;
        this.context = context;
    }

    List<Map<String, Object>> build() {
        List<Map<String, Object>> list = new ArrayList<>();

        if (context.isBuildJob()) {
            // The post-build action has no access to the ~/.m2 repository so we
            // need to grab the snapshots from the ~/.m2/repository directory
            // and put them in a known place for the action.
            // The action will then store the snapshots so that they can be used
            // by later jobs
            Map<String, Object> tarSnapshotsStep = new LinkedHashMap<>();
            tarSnapshotsStep.put("name", "Tarring snapshots from this build into .snapshots.tgz");
            String run = "cd ~/.m2/repository && " +
                    String.format("find  -type d  -name '*-SNAPSHOT' -exec tar cfzv ${GITHUB_WORKSPACE}/%s {} +", SNAPSHOTS_TGZ);
            tarSnapshotsStep.put("run", run);
            list.add(tarSnapshotsStep);
        }

        // Add the step
        Map<String, Object> postBuildStep = new LinkedHashMap<>();
        postBuildStep.put("uses", action);
        Map<String, Object> with = buildWith();
        postBuildStep.put("with", with);
        list.add(postBuildStep);

        return list;
    }
    Map<String, Object> buildWith() {
        Map<String, Object> with = new LinkedHashMap<>();
        with.put("build", context.isBuildJob() ? 1 : 0);
        with.put("custom", context.isCustomComponentJob() ? 1 : 0);
        with.put("component", context.getComponent().getName());
        with.put("snapshots", SNAPSHOTS_TGZ);
        return with;
    }
}
