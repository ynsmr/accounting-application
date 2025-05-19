package com.cydeo.dto;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class RoleDto {
    @NotNull
    private Long id;
    private String description;
    
    
}
