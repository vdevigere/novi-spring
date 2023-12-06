package com.viddu.novi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FlagController {

    @Autowired
    FlagRepository flagRepository;


    @PostMapping(path = "/flags")
    public Flag createFlag(@RequestParam String name, @RequestParam boolean status){
        Flag flag = new Flag();
        flag.setName(name);
        flag.setStatus(status);
        flagRepository.save(flag);
        return flag;
    }

    @GetMapping(path = "/flags")
    public Iterable<Flag> getFlags(){
        return flagRepository.findAll();
    }

    @GetMapping(path = "/flags/{id}")
    public Flag one(@PathVariable long id){
        return flagRepository.findById(id).orElseThrow();
    }
}
