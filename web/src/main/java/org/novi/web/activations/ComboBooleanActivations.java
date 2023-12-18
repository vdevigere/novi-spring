package org.novi.web.activations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.novi.core.ActivationConfig;
import org.novi.core.activations.BaseActivation;
import org.novi.core.activations.BaseConfiguredActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.persistence.ActivationConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class ComboBooleanActivations implements BaseActivation, ApplicationContextAware {
    private enum OPERATION{
        AND, OR
    };

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    private ActivationConfigRepository activationConfigRepository;

    private Map<String, BaseActivation> foundActivations;

    Logger logger = LoggerFactory.getLogger(ComboBooleanActivations.class);

    public record ConfigRecord(Iterable<Long> activationIds, OPERATION operation){};

    @Override
    public BaseConfiguredActivation<Iterable<ActivationConfig>> whenConfiguredWith(String configuration) throws ConfigurationParseException {
        // Array of activationConfig Ids. Read the actual activationConfigs from the database
        try {
            ConfigRecord configMap = new ObjectMapper().readValue(configuration, new TypeReference<>() {
            });
            return whenConfiguredWith(configMap);

        } catch (JsonProcessingException e) {
            throw new ConfigurationParseException(e);
        }
    }

    public BaseConfiguredActivation<Iterable<ActivationConfig>> whenConfiguredWith(ConfigRecord configMap) throws ConfigurationParseException {
        Iterable<ActivationConfig> activationConfigs = activationConfigRepository.findAllById(configMap.activationIds);
        return new BaseConfiguredActivation<>(activationConfigs) {
            @Override
            public Boolean evaluateFor(Map<String, Object> context) {
                Boolean resultingStatus = null;
                for (ActivationConfig activationConfig : activationConfigs){
                    BaseActivation activation = foundActivations.get(activationConfig.getName());
                    if (activation != null){
                        try {
                            Boolean evaluatedStatus = activation.whenConfiguredWith(activationConfig.getConfig()).evaluateFor(context);
                            logger.debug("{} -> Original Status: {}, Evaluated Status: {}", activationConfig.getName(), resultingStatus, evaluatedStatus);
                            switch (configMap.operation){
                                case OR -> resultingStatus = (resultingStatus == null) ? evaluatedStatus : resultingStatus | evaluatedStatus;
                                case AND -> resultingStatus = (resultingStatus == null) ? evaluatedStatus : resultingStatus & evaluatedStatus;
                            }
                        } catch (ConfigurationParseException configurationParseException){
                          throw new RuntimeException(configurationParseException);
                        }
                    }
                }
                return resultingStatus;
            }
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.foundActivations = (Map<String, BaseActivation>) applicationContext.getBean("foundActivations");
        this.activationConfigRepository = applicationContext.getBean(ActivationConfigRepository.class);
    }
}
