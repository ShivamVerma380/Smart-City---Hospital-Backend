package com.smartcity.hospital.controller.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcity.hospital.helper.ResponseMessage;
import com.smartcity.hospital.model.Citizen;
import com.smartcity.hospital.services.registration.RegistrationService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class RegistrationController {
    

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private RegistrationService registrationService;

    private Log log = LogFactory.getLog(RegistrationController.class);

    @PostMapping(value = "/citizen", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCitizen(@RequestBody Citizen citizen) {
        try {
            log.info("Received Citizen: " + citizen);
            return registrationService.createCitizen(citizen);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @GetMapping(value = "/citizen/{email}/{password}")
    public ResponseEntity<?> createCitizen(@PathVariable("email") String email, @PathVariable("password") String password) {
        try {
            return registrationService.loginCitizen(email, password);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}
