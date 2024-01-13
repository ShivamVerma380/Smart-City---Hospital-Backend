package com.smartcity.hospital.daos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.smartcity.hospital.models.Citizen;

@Repository
@Component
public interface CitizenDao extends CrudRepository<Citizen, String> {
    
    public Citizen getCitizenByEmail(String email);
}
