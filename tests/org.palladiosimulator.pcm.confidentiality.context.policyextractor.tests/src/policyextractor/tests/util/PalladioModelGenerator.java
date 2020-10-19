package policyextractor.tests.util;

import java.io.IOException;

import org.palladiosimulator.pcm.confidentiality.context.ConfidentialAccessSpecification;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import policyextractor.common.tests.util.TestUtil;
import util.Logger;

public class PalladioModelGenerator {
    private UsageModel usageModel;
    private Repository repository;
    private System system;
    private ConfidentialAccessSpecification contextModel;

    public PalladioModelGenerator() {
        // Create models
        repository = RepositoryGenerator.createNewRepository();
        system = AssemblyGenerator.createNewAssemblyModel();
        usageModel = UsageModelGenerator.createNewUsageModel();
        contextModel = ContextModelGenerator.createNewContextModel();

        try {
            saveTestModels();
        } catch (IOException e) {
            Logger.error("Couldn't create TestData");
        }

        // Create Interfaces
        RepositoryGenerator.createInterfaces();

        // Create Roles in System
        AssemblyGenerator.createInterfaces();
        AssemblyGenerator.createComponents();

        // Create usagemodel last
        UsageModelGenerator.generateUsageScenarios();

        // Contexts
        ContextModelGenerator.createContexts();
        ContextModelGenerator.createSpecifications();
    }

    public void saveTestModels() throws IOException {

        String usageModelPath = TestUtil.getTestDataPathForGeneration() + "newUsageModel.usagemodel";
        String repositoryPath = TestUtil.getTestDataPathForGeneration() + "newRepository.repository";
        String systemPath = TestUtil.getTestDataPathForGeneration() + "newAssembly.system";
        String contextModelPath = TestUtil.getTestDataPathForGeneration() + "My.context";

        new TestDataHandler().saveTestModel(repository, repositoryPath);
        new TestDataHandler().saveTestModel(system, systemPath);
        new TestDataHandler().saveTestModel(usageModel, usageModelPath);
        new TestDataHandler().saveTestModel(contextModel, contextModelPath);
    }
}