package ca.brij.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeartBeatController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
    public String heartbeat(Principal principal) {
        logger.info(principal.getName() + " is alive!");
        return "I'm Alive!";
    }
}
