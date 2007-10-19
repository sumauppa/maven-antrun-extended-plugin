package org.apache.maven.plugin.antrun;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.maven.artifact.Artifact;
import org.apache.tools.ant.taskdefs.Property;

/**
 * Ant task that resolves an artifact through Maven.
 *
 * TODO: support more convenient syntax
 * TODO: resolve dependency transitively.
 * 
 * @author Paul Sterk
 * @author Kohsuke Kawaguchi
 */
public class ResolveArtifactTask extends Task {

    private String property,groupId,artifactId,version,type="jar",classifier;

    public void setProperty(String property) {
        this.property = property;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public void execute() throws BuildException {
        log("Starting execute", Project.MSG_DEBUG);
        try {
            ArtifactResolverWrapper w = ArtifactResolverWrapper.get();
            Artifact a = w.getFactory().createArtifactWithClassifier(groupId, artifactId, version, type, classifier);
            w.getResolver().resolve(a, w.getRemoteRepositories(), w.getLocalRepository());
            // Property attribute is optional. Check for null value
            if (property != null) {                
                getProject().setProperty(property, a.getFile().getAbsolutePath());
            }
        } catch (Throwable ex) {
            log("Problem resolving artifact: "+ex.getMessage(), Project.MSG_ERR);
            throw new BuildException(ex);
        }
        log("Exiting execute", Project.MSG_DEBUG);
    }
}