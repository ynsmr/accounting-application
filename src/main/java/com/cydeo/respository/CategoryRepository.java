package com.cydeo.respository;


import com.cydeo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    boolean existsByDescription(String description);
    boolean existsByDescriptionAndCompany_Id(String username, Long companyId);
}
