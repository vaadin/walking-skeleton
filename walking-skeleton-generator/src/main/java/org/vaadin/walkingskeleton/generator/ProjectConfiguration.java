package org.vaadin.walkingskeleton.generator;

record ProjectConfiguration(GroupId groupId,
                            ArtifactId artifactId,
                            PackageName basePackage,
                            UIFramework uiFramework) {
}
