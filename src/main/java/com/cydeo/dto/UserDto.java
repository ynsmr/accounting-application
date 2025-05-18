package com.cydeo.dto;

import com.cydeo.annotation.UniqueUserName;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;

@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    
    private Long id;
    @NotBlank(message = "Username is a requiured field.")
    @Email
    @UniqueUserName
    private String username;
    @NotBlank(message = "Password is a required field.")
    @Pattern(regexp = "^(?=.*[A-Z]).{4,}$", message = "Password should be at least 4 character long and contain a capital letter.")
    private String password;
    @NotBlank(message = "Confirm password is a required field.")
    private String confirmPassword;
    @NotBlank(message = "First name is a required field.")
    @Size(min = 2, max = 50, message = "Firstname should be 2-50 character long.")
    private String firstname;
    @NotBlank(message = "Last name is a required field.")
    @Size(min = 2, max = 50, message = "Lasttname should be 2-50 character long.")
    private String lastname;
    @NotBlank(message = "Phone is a requied field.")
    @Pattern(regexp = "^d {5}([-]|s*)?(d {4})?$", message = "Phone number should be in valid format.")
    private String phone;
    private RoleDto role;
    @Valid
    private CompanyDto company;
    private boolean isOnlyAdmin;
    @AssertTrue(message = "Passwords should match.")
    private boolean isPasswordMatching(){
        return password.equals(confirmPassword);
    }
    
    
    
}
