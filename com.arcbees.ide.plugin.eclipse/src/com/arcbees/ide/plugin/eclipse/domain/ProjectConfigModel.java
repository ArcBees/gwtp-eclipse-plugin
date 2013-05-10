package com.arcbees.ide.plugin.eclipse.domain;

public class ProjectConfigModel extends ModelObject {
    private String projectName;
    private String packageName;
    private String moduleName;
    private String groupId;
    private String artifactId;
    private String workspacePath;
    private Archetype archetypeSelected;
    
    public ProjectConfigModel() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        firePropertyChange("projectName", this.projectName, this.projectName = projectName);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        firePropertyChange("packageName", this.packageName, this.packageName = packageName);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        firePropertyChange("moduleName", this.moduleName, this.moduleName = moduleName);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        firePropertyChange("groupId", this.groupId, this.groupId = groupId);
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        firePropertyChange("artifactId", this.artifactId, this.artifactId = artifactId);
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        firePropertyChange("workspacePath", this.workspacePath, this.workspacePath = workspacePath);
    }
    
    public Archetype getArchetypeSelected() {
        return archetypeSelected;
    }

    public void seArchetypeSelected(Archetype archetypeSelected) {
        firePropertyChange("archetypeSelected", this.archetypeSelected, this.archetypeSelected = archetypeSelected);
    }
    
    @Override
    public String toString() {
        String s = "{ ProjectConfigModel: ";
        s += "projectName=" + projectName + " ";
        s += "packageName=" + packageName + " ";
        s += "moduleName=" + moduleName + " ";
        s += "groupId=" + groupId + " ";
        s += "artifactId=" + artifactId + " ";
        s += "workspacePath=" + workspacePath + " ";
        s += "archetypeSelected=" + archetypeSelected + " ";
        s += " }"; 
        return s;
    }

}
