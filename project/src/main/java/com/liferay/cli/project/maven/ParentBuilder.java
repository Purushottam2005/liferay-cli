package com.liferay.cli.project.maven;

import com.liferay.cli.model.Builder;
import com.liferay.cli.support.util.XmlUtils;
import org.w3c.dom.Element;

public class ParentBuilder implements Builder<Parent> {

    private final String artifactId;
    private final String groupId;
    private final String pomPath;
    private final String relativePath;
    private final String version;

    public ParentBuilder(final Element parentElement, final String pomPath) {
        groupId = XmlUtils.getTextContent("/groupId", parentElement);
        artifactId = XmlUtils.getTextContent("/artifactId", parentElement);
        version = XmlUtils.getTextContent("/version", parentElement);
        relativePath = XmlUtils.getTextContent("/relativePath", parentElement);
        this.pomPath = pomPath;
    }

    public Parent build() {
        return new Parent(groupId, artifactId, version, relativePath, pomPath);
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getPomPath() {
        return pomPath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getVersion() {
        return version;
    }
}
