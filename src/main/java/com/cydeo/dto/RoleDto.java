package com.cydeo.dto;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class RoleDto {
    @NotEmpty
    private Long id;
    private String description;
    
    
}
