package org.overbaard.ci.multi.repo.generator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
class PreBuildStepBuilder {
    private final String action;
    private final String id;
    private final boolean build;
    private final boolean custom;

    public PreBuildStepBuilder(String action, String id, boolean build, boolean custom) {
        this.action = action;
        this.id = id;
        this.build = build;
        this.custom = custom;
    }

    Map<String, Object> build() {
        Map<String, Object> checkout = new LinkedHashMap<>();
        if (id != null) {
            checkout.put("id", id);
        }
        checkout.put("uses", action);
        Map<String, Object> with = buildWith();
        if (with.size() != 0) {
            checkout.put("with", with);
        }
        return checkout;
    }

    Map<String, Object> buildWith() {
        Map<String, Object> with = new LinkedHashMap<>();
        with.put("build", build ? 1 : 0);
        with.put("custom", custom ? 1 : 0);
        return with;
    }
}
