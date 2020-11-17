package org.overbaard.ci.multi.repo.directory.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.overbaard.ci.multi.repo.ToolCommand;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class GrabMavenVersion {
    private final Path rootPom;

    public GrabMavenVersion(Path rootPom) {
        this.rootPom = rootPom;
    }

    private static void grabVersion(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalStateException("Need the following args: <root pom path>");
        }

        Path rootPom = Paths.get(args[0]).toAbsolutePath();
        if (!Files.exists(rootPom)) {
            throw new IllegalStateException("Root pom path does not exist: " + rootPom);
        }

        GrabMavenVersion grabber = new GrabMavenVersion(rootPom);
        grabber.grabVersion();
    }

    private void grabVersion() throws Exception {
        Model model = ReadMavenModelUtil.readModel(rootPom);
        String version = model.getVersion();
        System.out.println(version);
    }


    public static class Command implements ToolCommand {
        public static final String NAME = "grab-maven-project-version";

        @Override
        public String getDescription() {
            return "Outputs the maven version produced by the maven project this is pointed to";
        }

        @Override
        public void invoke(String[] args) throws Exception {
            GrabMavenVersion.grabVersion(args);
        }
    }

}
