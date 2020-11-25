package policyextractor.tests.accuracy;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.confidentiality.context.ConfidentialAccessSpecification;
import org.palladiosimulator.pcm.confidentiality.context.set.ContextSet;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import model.ModelHandler;
import modelabstraction.AssemblyAbstraction;
import modelabstraction.ContextModelAbstraction;
import modelabstraction.UsageModelAbstraction;
import policyderiver.PolicyDeriver;
import policyextractor.common.tests.util.TestContextModelAbstraction;
import policyextractor.common.tests.util.TestUtil;
import policyextractor.tests.util.EvaluationModelAbstraction;
import policyreducer.PolicyReducer;
import rules.RulesFlag;
import settings.Settings;
import util.Logger;

class AccuracyTestTemplate {
    protected ContextModelAbstraction abs;
    protected TestContextModelAbstraction testAbs;
    protected ModelHandler modelloader;
    protected ConfidentialAccessSpecification testContextModel;
    protected UsageModel testUsageModel;
    protected Repository testRepo;
    protected System testSystem;

    protected String canonicalPath;
    protected UsageModelAbstraction usageModelAbs;
    protected Repository repo;
    protected AssemblyAbstraction assemblyAbs;

    protected String caseStudyName;
    protected String scenarioName;

    protected String[] usageScenarios = null;
    protected String[][] systemCalls = null;
    protected String[][][] seffs = null;

    protected class ResultsRecord {
        final int x;
        final int y;
        final int z;

        ResultsRecord(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

    }

    protected void init() {

        testContextModel = modelloader.loadContextModel();
        testUsageModel = modelloader.loadUsageModel();
        testSystem = modelloader.loadAssemblyModel();
        testRepo = modelloader.loadRepositoryModel();

        assertNotNull(testContextModel);
        assertNotNull(testUsageModel);
        assertNotNull(testRepo);
        assertNotNull(testSystem);

        abs = new ContextModelAbstraction(testContextModel);
        testAbs = new TestContextModelAbstraction(testContextModel);

        usageModelAbs = new UsageModelAbstraction(testUsageModel);
        repo = testRepo;
        assemblyAbs = new AssemblyAbstraction(testSystem);
    }

    ResultsRecord executeMeasurement() throws IOException {
        canonicalPath = TestUtil.getTestDataPath() + "evaluation" + File.separator + caseStudyName;
        EvaluationModelAbstraction modelAbs = new EvaluationModelAbstraction(canonicalPath);
        modelloader = new ModelHandler(modelAbs);
        modelAbs.contextName = scenarioName;

        init();

        Logger.info("CS: " + caseStudyName);
        Logger.setActive(false);

        execute_deriver();

        Logger.setActive(true);

        int TP = 0;
        int FP = 0;
        int FN = 0;

        EList<ResourceDemandingBehaviour> listSeffs = new BasicEList<>();

        for (int index = 0; index < usageScenarios.length; index++) {
            for (int call = 0; call < systemCalls[index].length; call++) {
                String usname = usageScenarios[index];
                String systemCallName = systemCalls[index][call];

                EntryLevelSystemCall sc = getSystemCallByName(systemCallName);
                EList<ContextSet> cs = abs.getContextSet(sc);

                // AssemblyContext ac = getAssemblyContextByName("TravelPlanner <TravelPlanner>");
                // RepositoryComponent rp = ac.getEncapsulatedComponent__AssemblyContext();

                for (int seffId = 0; seffId < seffs[index][call].length; seffId++) {
                    String seffIdName = seffs[index][call][seffId];
                    ResourceDemandingBehaviour rp = getSeffById(seffIdName);

                    // TODO compare context set?

                    if (testAbs.isPolicyExisting((ServiceEffectSpecification) rp)) {
                        TP = TP + 1;
                        listSeffs.add(rp);
                    } else {
                        FN = FN + 1;
                    }
                }
            }
        }

        for (ResourceDemandingBehaviour seff : testAbs.contextModelAbs.getSEFFs()) {
            if (testAbs.isPolicyExisting((ServiceEffectSpecification) seff)) {
                if (!listSeffs.contains(seff)) {
                    FP = FP + 1;
                    Logger.info(seff.getId());
                }
            }
        }

        return new ResultsRecord(TP, FP, FN);
    }

    protected void execute_deriver() {
        Settings settings = new Settings(canonicalPath, false);
        PolicyDeriver deriver = new PolicyDeriver(settings, abs, testUsageModel, testRepo, testSystem);
        deriver.execute();
    }

    protected void execute_reducer() {

        RulesFlag rulesflag = new RulesFlag();
        PolicyReducer reducer = new PolicyReducer(abs, rulesflag);
        reducer.execute();

    }

    protected void execute_all() {
        // TODO mainhandler?
        execute_deriver();
        execute_reducer();
    }

    // TODO move
    private AssemblyContext getAssemblyContextByName(String name) {
        AssemblyContext ret = null;
        for (AssemblyContext ac : testSystem.getAssemblyContexts__ComposedStructure()) {
            if (ac.getEntityName().equals(name)) {
                ret = ac;
                break;
            }
        }
        assertNotNull(ret);
        return ret;
    }

    private ResourceDemandingBehaviour getSeffById(String id) {
        ResourceDemandingBehaviour ret = null;
        for (ResourceDemandingBehaviour rdb : testAbs.contextModelAbs.getSEFFs()) {
            if (rdb.getId().equals(id)) {
                ret = rdb;
                break;
            }
        }
        assertNotNull(ret);
        return ret;
    }

    private EntryLevelSystemCall getSystemCallByName(String name) {
        EntryLevelSystemCall ret = null;
        for (ScenarioBehaviour sb : usageModelAbs.getListofScenarioBehaviour()) {
            for (EntryLevelSystemCall sc : usageModelAbs.getListOfEntryLevelSystemCalls(sb)) {
                if (sc.getEntityName().equals(name)) {
                    ret = sc;
                    break;
                }
            }
        }
        assertNotNull(ret);
        return ret;
    }

}
