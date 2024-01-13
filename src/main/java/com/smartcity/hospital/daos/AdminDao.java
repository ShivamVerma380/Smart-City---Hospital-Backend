package com.smartcity.hospital.daos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.smartcity.hospital.models.Admin;

@Repository
@Component
public interface AdminDao extends CrudRepository<Admin, String> {
    
    public Admin getAdminByEmail(String email);
}
