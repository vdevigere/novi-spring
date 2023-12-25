package org.novi.web;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.novi.core.Flag;
import org.novi.core.exceptions.ContextParseException;
import org.novi.persistence.FlagRepository;
import org.novi.web.activations.ComboBooleanActivations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/evaluatedFlags")
public class EvaluatedFlagController {
    Logger logger = LoggerFactory.getLogger(EvaluatedFlagController.class);

    private final FlagRepository flagRepository;

    private final ComboBooleanActivations comboBooleanActivations;

    public EvaluatedFlagController(@Autowired FlagRepository flagRepository,
                                   @Autowired ComboBooleanActivations comboBooleanActivations) {
        this.flagRepository = flagRepository;
        this.comboBooleanActivations = comboBooleanActivations;
    }

    @GetMapping("/{id}")
    public Flag getEvaluatedFlagById(@PathVariable(name = "id") Long id, @RequestBody String context) throws ContextParseException {
        logger.debug("Id: {}", id);
        Flag flag = flagRepository.findById(id).orElse(null);
        if (flag != null) {
            Boolean resultingStatus = comboBooleanActivations.whenConfiguredWith(flag.getActivationConfigs(), ComboBooleanActivations.OPERATION.AND).evaluateFor(context);
            logger.debug("Final Status: {}", resultingStatus);
            flag.setStatus(resultingStatus);
        }
        return flag;
    }
}
