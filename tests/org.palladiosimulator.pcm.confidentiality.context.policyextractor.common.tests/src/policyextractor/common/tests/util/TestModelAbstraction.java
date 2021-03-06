package policyextractor.common.tests.util;

import model.IModelAbstraction;

public class TestModelAbstraction implements IModelAbstraction {
    private String projectPath;

    public TestModelAbstraction(String path) {
        this.projectPath = path;
    }

    public String getUsageModelPath() {
        String path = projectPath + "/" + "newUsageModel.usagemodel";
        return path;
    }

    public String getRepositoryModelPath() {
        String path = projectPath + "/" + "newRepository.repository";
        return path;
    }

    public String getAssemblyPath() {
        String path = projectPath + "/" + "newAssembly.system";
        return path;
    }

    public String getContextModelPath() {
        String path = projectPath + "/" + "My.context";
        return path;
    }

    public String getDeriverOutPath() {
        return null;
    }

    public String getReducerOutPath() {
        return null;
    }

    public String getCleanupOutpath() {
        return null;
    }

}
