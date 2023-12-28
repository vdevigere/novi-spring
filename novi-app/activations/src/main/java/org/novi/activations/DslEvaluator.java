package org.novi.activations;

import org.novi.core.activations.BaseActivation;
import org.novi.core.dsl.DslActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class DslEvaluator implements BaseActivation<DslActivation> {
    private DslActivation dsl;

    private final Logger logger = LoggerFactory.getLogger(DslEvaluator.class);

    @Override
    public BaseActivation<DslActivation> valueOf(String configuration) throws ConfigurationParseException {
        logger.debug("Parsing DSL: {}", configuration);
        try {
            ScriptEngineManager sem = new ScriptEngineManager(DslEvaluator.class.getClassLoader());
            List<ScriptEngineFactory> factories = sem.getEngineFactories();
            logger.debug("Found the following Script Engines:");
            for (ScriptEngineFactory factory : factories) {
                logger.debug("{}, {}, {}", factory.getEngineName(), factory.getEngineVersion(), factory.getNames());
            }
            if (factories.isEmpty())
                logger.debug("No Script Engines found");
            this.dsl = (DslActivation) sem.getEngineByName("scala").eval(configuration);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public Boolean apply(String context) {
        return dsl.apply(context);
    }

    @Override
    public DslActivation configuration() {
        return this.dsl;
    }
}
