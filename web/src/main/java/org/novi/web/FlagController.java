package org.novi.web;

import org.novi.core.Flag;
import org.novi.persistence.FlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("flags")
public class FlagController {

    @Autowired
    FlagRepository flagRepository;


    @PostMapping
    public Flag createFlag(@RequestBody Flag flagDAO) {
        flagRepository.save(flagDAO);
        return flagDAO;
    }

    @GetMapping
    public Iterable<Flag> getFlags() {
        return flagRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public Flag flagById(@PathVariable(name = "id") Long id) {
        return flagRepository.findById(id).orElseThrow();
    }
}
