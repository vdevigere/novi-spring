package org.novi.web;


import org.novi.core.ActivationConfig;
import org.novi.core.Flag;
import org.novi.core.activations.BaseActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.core.exceptions.ContextParseException;
import org.novi.persistence.ActivationConfigRepository;
import org.novi.persistence.FlagRepository;
import org.novi.web.activations.ComboBooleanActivations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/evaluatedFlags")
public class EvaluatedFlagController {
    private final ApplicationContext applicationContext;
    Logger logger = LoggerFactory.getLogger(EvaluatedFlagController.class);

    private final FlagRepository flagRepository;


    private final Map<String, BaseActivation> foundActivations;
    private final ActivationConfigRepository activationConfigRepository;

    public EvaluatedFlagController(@Autowired FlagRepository flagRepository,
                                   @Autowired
                                   @Qualifier("foundActivations") Map<String, BaseActivation> foundActivations,
                                   @Autowired ApplicationContext applicationContext,
                                   @Autowired ActivationConfigRepository activationConfigRepository) {
        this.flagRepository = flagRepository;
        this.foundActivations = foundActivations;
        this.applicationContext = applicationContext;
        this.activationConfigRepository = activationConfigRepository;
    }

    @GetMapping("/{id}")
    public Flag getEvaluatedFlagById(@PathVariable(name = "id") Long id, @RequestBody String context) throws ContextParseException {
        logger.debug("Id: {}", id);
        Flag flag = flagRepository.findById(id).orElse(null);
        if (flag != null) {
            ComboBooleanActivations comboBooleanActivations = new ComboBooleanActivations(applicationContext, activationConfigRepository, foundActivations);
            Boolean resultingStatus = comboBooleanActivations.whenConfiguredWith(flag.getActivationConfigs(), ComboBooleanActivations.OPERATION.AND).evaluateFor(context);
            logger.debug("Final Status: {}", resultingStatus);
            flag.setStatus(resultingStatus);
        }
        return flag;
    }
}
