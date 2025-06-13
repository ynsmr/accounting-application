package com.cydeo.service;

import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CategoryNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CategoryRepository;
import com.cydeo.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserService userService;
    @Mock
    private CompanyService companyService;
    @Mock
    private ProductService productService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    private Category category;
    private CategoryDto categoryDto;
    
    @BeforeEach
    void setUp(){
        category = new Category();
        category.setId(1L);
        category.setDescription("TV");
        Company company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        category.setCompany(company);

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setDescription("TV");
        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(1L);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        categoryDto.setCompany(companyDto);
    }
    
    private List<Category> getmultipleCategories(){
        Category category1 = new Category();
        category1.setId(2L);
        category1.setDescription("TV");
        Company company1 = new Company();
        company1.setId(1L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        category1.setCompany(company1);

        Category category2 = new Category();
        category2.setId(3L);
        category2.setDescription("TV");
        category2.setCompany(company1);
        
        return List.of(category, category1, category2);
    }

    private List<CategoryDto> getmultipleCategorieDtos(){
        CategoryDto category1 = new CategoryDto();
        category1.setId(2L);
        category1.setDescription("TV");
        CompanyDto company1 = new CompanyDto();
        company1.setId(1L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        category1.setCompany(company1);

        CategoryDto category2 = new CategoryDto();
        category2.setId(3L);
        category2.setDescription("TV");
        category2.setCompany(company1);

        return List.of(categoryDto, category1, category2);
    }
    
    @Test
    void should_find_by_id(){
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        CategoryDto actualCategory = categoryService.findById(category.getId());
        CategoryDto expectedCategory = categoryDto;
        
        assertThat(actualCategory).usingRecursiveComparison().isEqualTo(expectedCategory);
        verify(categoryRepository).findById(category.getId());
    }
    
    @Test
    void should_not_find_by_id(){
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> categoryService.findById(category.getId()));
        
        assertInstanceOf(CategoryNotFoundException.class, throwable);
        assertEquals("No such category found", throwable.getMessage());
        
        verify(categoryRepository).findById(category.getId());
    }
    
    @Test
    void should_find_all(){
        mockAuthentication();
        when(categoryRepository.findAll()).thenReturn(getmultipleCategories());

        List<CategoryDto> actualCategories = categoryService.findAll();
        List<CategoryDto> expectedCategories = getmultipleCategorieDtos();
        
        assertThat(actualCategories).usingRecursiveComparison().isEqualTo(expectedCategories);
        verify(categoryRepository).findAll();
    }
    
    @Test
    void should_save_category(){
        when(categoryRepository.save(any())).thenReturn(category);
        
        categoryService.saveCategory(categoryDto);
        
        verify(categoryRepository).save(category);
    }

    @Test
    void should_update_category(){
        when(categoryRepository.save(any())).thenReturn(category);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        categoryService.updateCategory(categoryDto);

        verify(categoryRepository).save(category);
    }
    
    @Test
    void should_delete_category(){
        when(categoryRepository.save(any())).thenReturn(category);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        
        categoryService.deleteCategory(category.getId());
        
        assertTrue(category.getIsDeleted());
        verify(categoryRepository).save(category);
    }

    private void mockAuthentication(){
        User user = new User();
        user.setId(1L);
        user.setFirstname("Mike");
        user.setLastname("Tyson");
        user.setUsername("miketyson");
        user.setAccountNonLocked(true);

        Company company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setInsertDateTime(LocalDateTime.of(2012, 12, 12, 0, 0, 0));
        user.setCompany(company);

        lenient().when(userService.getLoggedInUser()).thenReturn(user);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

    }
}
