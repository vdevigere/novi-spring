package org.novi.web;


import org.novi.core.ActivationConfig;
import org.novi.core.Flag;
import org.novi.core.activations.BaseActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.core.exceptions.ContextParseException;
import org.novi.persistence.FlagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/evaluatedFlags")
public class EvaluatedFlagController {
    Logger logger = LoggerFactory.getLogger(EvaluatedFlagController.class);

    private final FlagRepository flagRepository;


    private final Map<String, BaseActivation> foundActivations;

    public EvaluatedFlagController(@Autowired FlagRepository flagRepository,
                                   @Autowired
                                   @Qualifier("foundActivations") Map<String, BaseActivation> foundActivations) {
        this.flagRepository = flagRepository;
        this.foundActivations = foundActivations;
    }

    @GetMapping("/{id}")
    public Flag getEvaluatedFlagById(@PathVariable(name = "id") Long id, @RequestBody String context) throws ConfigurationParseException, ContextParseException {
        logger.debug("Id: {}", id);
        Flag flag = flagRepository.findById(id).orElse(null);
        if (flag != null) {
            boolean resultingStatus = flag.isStatus();
            for (ActivationConfig activationConfig : flag.getActivationConfigs()) {
                BaseActivation activation = foundActivations.get(activationConfig.getName());
                if (activation != null) {
                    boolean evaluatedStatus = activation.whenConfiguredWith(activationConfig.getConfig()).evaluateFor(context);
                    logger.debug("{} -> Original Status: {}, Evaluated Status: {}", activationConfig.getName(), resultingStatus, evaluatedStatus);
                    resultingStatus &= evaluatedStatus;
                }
            }
            logger.debug("Final Status: {}", resultingStatus);
            flag.setStatus(resultingStatus);
        }
        return flag;
    }
}
