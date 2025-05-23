package com.cydeo.respository;


import com.cydeo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    @Query("SELECT u.company FROM User u WHERE u.id =?1")
    Optional<Company> findCompanyByLoggedInUser(Long userId);
    
    
    
}
