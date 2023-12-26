package org.novi.web.activations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.novi.core.ActivationConfig;
import org.novi.core.activations.BaseActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.persistence.ActivationConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ComboBooleanActivations implements BaseActivation<Iterable<ActivationConfig>> {

    @Autowired
    private ActivationConfigRepository activationConfigRepository;

    @Autowired
    private ScriptEngine scriptEngine;

    Logger logger = LoggerFactory.getLogger(ComboBooleanActivations.class);

    private Iterable<ActivationConfig> configuration;

    private OPERATION operation;

    // Needed for ServiceLoader
    public ComboBooleanActivations() {

    }

    public ComboBooleanActivations(ActivationConfigRepository activationConfigRepository, ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
        this.activationConfigRepository = activationConfigRepository;
    }

    public enum OPERATION {
        AND, OR
    }

    public record ConfigRecord(Iterable<Long> activationIds, OPERATION operation) {
    }


    @Override
    public ComboBooleanActivations configuration(String configuration) throws ConfigurationParseException {
        // Array of activationConfig Ids. Read the actual activationConfigs from the database
        logger.debug("Parsing configuration:{}", configuration);
        try {
            ConfigRecord configMap = new ObjectMapper().readValue(configuration, new TypeReference<>() {
            });
            return whenConfiguredWith(configMap);

        } catch (JsonProcessingException e) {
            throw new ConfigurationParseException(e);
        }
    }

    @Override
    public Boolean apply(String context) {
        Boolean resultingStatus = null;
        for (ActivationConfig activationConfig : configuration()) {
            try {
                BaseActivation<?> baseActivation = (BaseActivation) scriptEngine.eval(activationConfig.getConfig());
                Boolean evaluatedStatus = baseActivation.apply(context);
                Boolean originalStatus = resultingStatus;
                logger.debug("{} -> Original Status: {}, Evaluated Status: {}", activationConfig.getName(), originalStatus, evaluatedStatus);
                switch (operation) {
                    case OR ->
                            resultingStatus = (resultingStatus == null) ? evaluatedStatus : resultingStatus | evaluatedStatus;
                    case AND ->
                            resultingStatus = (resultingStatus == null) ? evaluatedStatus : resultingStatus & evaluatedStatus;
                }
                logger.debug("{} {} {} = {}", originalStatus, operation, evaluatedStatus, resultingStatus);
            } catch (ScriptException configurationParseException) {
                throw new RuntimeException(configurationParseException);
            }
        }
        return resultingStatus;
    }


    @Override
    public Iterable<ActivationConfig> configuration() {
        return configuration;
    }

    public ComboBooleanActivations whenConfiguredWith(ConfigRecord configMap) {
        logger.debug("Looking up Ids: {} from DB", configMap.activationIds);
        Iterable<ActivationConfig> activationConfigs = activationConfigRepository.findAllById(configMap.activationIds);
        return whenConfiguredWith(activationConfigs, configMap.operation());
    }

    public ComboBooleanActivations whenConfiguredWith(Iterable<ActivationConfig> activationConfigs, OPERATION operation) {
        this.configuration = activationConfigs;
        this.operation = operation;
        return this;
    }
}
