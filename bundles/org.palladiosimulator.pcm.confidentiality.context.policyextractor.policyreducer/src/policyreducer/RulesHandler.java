package policyreducer;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.confidentiality.context.ConfidentialAccessSpecification;
import org.palladiosimulator.pcm.confidentiality.context.set.ContextSet;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

import data.ContextModelAbstraction;
import rules.IRulesDefinition;
import rules.RulesFlag;
import rules.RulesType;
import rules.impl.ParentChild;
import rules.impl.SimplerPolicy;
import rules.impl.SubstituteParent;
import util.ContextModelPrinter;
import util.Logger;

public class RulesHandler {
    private final ContextModelAbstraction contextModelAbs;
    private final RulesFlag rules;
    private EList<IRulesDefinition> rulesList = new BasicEList<>();

    public RulesHandler(ConfidentialAccessSpecification contextModel, RulesFlag rules) {
        this.contextModelAbs = new ContextModelAbstraction(contextModel);
        this.rules = rules;
    }

    public void execute() {
        Logger.infoDetailed("Rules-Start");

        new ContextModelPrinter().print(contextModelAbs.getContextModel(), false);

        int loopCount = 0;
        while(true) {
        	Logger.info("Loop-Start: " + loopCount + " -----------------");

        	initializeRules();
        	
        	for (IRulesDefinition rulesDefinition : rulesList) {
				rulesDefinition.applyRuleToModel();
			}
        	
        	//TODO crossverify records for conflicts?
        	
        	for (IRulesDefinition rulesDefinition : rulesList) {
        		rulesDefinition.executeRule();
        	}
        	
        	Logger.info("Loop-End: " + loopCount + " -----------------");
        	//TODO add condition
        	break;
        }
        

        new ContextModelPrinter().print(contextModelAbs.getContextModel(), false);

        Logger.infoDetailed("Rules-End");
    }
    
    private void initializeRules() {
    	rulesList = new BasicEList<>();

        if (rules.isRuleEnabled(RulesType.SimplerPolicy)) {
            rulesList.add(new SimplerPolicy(contextModelAbs));
        }

        if (rules.isRuleEnabled(RulesType.ParentChild)) {
            rulesList.add(new ParentChild(contextModelAbs));
        }

        if (rules.isRuleEnabled(RulesType.SubstituteParent)) {
            rulesList.add(new SubstituteParent(contextModelAbs));
        }
    }
}
