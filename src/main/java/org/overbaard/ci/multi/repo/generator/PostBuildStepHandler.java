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
            // OLD WAY - keep in case the mounting I am doing bends too many rules
            // gets disabled in the future
            /*
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
            */
            if (!context.hasDependencies()) {
                // If we had dependencies this would be done by the pre build job
                list.add(PreBuildStepBuilder.createMountStep());
            }
        }

        // Add the step
        Map<String, Object> postBuildStep = new LinkedHashMap<>();
        postBuildStep.put("uses", action);
        Map<String, Object> with = buildWith();
        postBuildStep.put("with", with);
        list.add(postBuildStep);

        if (context.isBuildJob()) {
            // Unmount what we mounted in the  pre build step or above
            Map<String, Object> mountM2Repository = new LinkedHashMap<>();
            mountM2Repository.put("name", "Unmount .m2-repo-mount");
            mountM2Repository.put("run", "sudo umount .m2-repo-mount && rmdir .m2-repo-mount");
            list.add(mountM2Repository);

        }

        return list;
    }
    Map<String, Object> buildWith() {
        Map<String, Object> with = new LinkedHashMap<>();
        with.put("build", context.isBuildJob() ? 1 : 0);
        with.put("custom", context.isCustomComponentJob() ? 1 : 0);
        with.put("component", context.getComponent().getName());
        // Only needed for the 'old' way uncommented above
        //with.put("snapshots", SNAPSHOTS_TGZ);
        return with;
    }
}
