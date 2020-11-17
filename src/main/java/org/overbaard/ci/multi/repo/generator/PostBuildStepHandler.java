package org.overbaard.ci.multi.repo.generator;

import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class PostBuildStepHandler extends PreBuildStepBuilder {
    private String component;

    public PostBuildStepHandler(String action, boolean build, boolean custom, String component) {
        super(action, null, build, custom);
        this.component = component;
    }

    @Override
    Map<String, Object> buildWith() {
        Map<String, Object> with = super.buildWith();
        with.put("component", component);
        return with;
    }
}
