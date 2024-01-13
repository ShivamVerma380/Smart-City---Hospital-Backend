package com.smartcity.hospital.services.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.smartcity.hospital.config.MySecurityConfig;
import com.smartcity.hospital.dao.AdminDao;
import com.smartcity.hospital.dao.CitizenDao;
import com.smartcity.hospital.helper.JwtUtil;
import com.smartcity.hospital.helper.ResponseMessage;
import com.smartcity.hospital.model.Citizen;
import com.smartcity.hospital.services.CustomUserDetailsService;

@Component
public class RegistrationService {
    
    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private CitizenDao citizenDao;

    @Autowired
    private AdminDao adminDao;

    @Autowired
    public AuthenticationManager authenticationManager;

    @Autowired
    public CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtUtil jwtUtil;

    @Autowired
    public MySecurityConfig mySecurityConfig;

    private Log log = LogFactory.getLog(RegistrationService.class);

    public ResponseEntity<?> createCitizen(Citizen citizen) {
        try {
            if (citizen.getPassword() == null) {
                // Handle the case where the password is null
                throw new IllegalArgumentException("Password cannot be null");
            }
            //Step 1: Citizen should not exist either in citizen or admin DB.
            String email = citizen.getEmail();
            if (citizenDao.getCitizenByemail(email)!=null || adminDao.getAdminByemail(email)!=null) {
                responseMessage.setMessage("Email already exists.....");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseMessage);
            }

            log.info("Citizen:"+citizen.toString());
            
            String password = citizen.getPassword();
            citizen.setPassword(mySecurityConfig.passwordEncoder().encode(citizen.getPassword()));

            //Step 2: Save the citizen in db.
            citizenDao.save(citizen);

            //Step 3: Token generate krna for the citizen
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(citizen.getEmail()); //username == email
            
            String token = jwtUtil.generateToken(userDetails);

            //Step 4: Return the token in message.
            responseMessage.setMessage(token);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    public ResponseEntity<?> loginCitizen(String email, String password) {
        try {
            Citizen citizen = citizenDao.getCitizenByemail(email);
            if (citizen==null) {
                responseMessage.setMessage("Email id does not exist....");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            if (bCryptPasswordEncoder.matches(password, citizen.getPassword())) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(citizen.getEmail());
                String token = jwtUtil.generateToken(userDetails);
                responseMessage.setMessage(token);
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            }

            responseMessage.setMessage("Bad credentials.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseMessage);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}
