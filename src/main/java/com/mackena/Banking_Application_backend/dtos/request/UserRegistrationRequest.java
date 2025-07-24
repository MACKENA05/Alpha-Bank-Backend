package com.mackena.Banking_Application_backend.dtos.request;

import com.mackena.Banking_Application_backend.models.enums.AccountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max= 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Paasword is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",message = "Password must contain atleast one uppercase letter,one lowercase and one number" )
    private String Password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "First name can only contain letters and spaces")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name can only contain letters and spaces")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+254|0)[1-9]\\d{8}$",
            message = "Please provide a valid Kenyan phone number (e.g., +254712345678 or 0712345678)")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 500, message = "Address must be between 10 and 500 characters")
    private String address;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "100.0", message = "Initial deposit must be at least KES 100")
    @DecimalMax(value = "1000000.0", message = "Initial deposit cannot exceed KES 1,000,000")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount format")
    private BigDecimal initialDeposit;

    @NotBlank(message = "Transaction PIN is required")
    @Pattern(regexp = "^\\d{4}$", message = "Transaction PIN must be exactly 4 digits")
    private String transactionPin;

    @NotBlank(message = "PIN confirmation is required")
    private String confirmPin;

    //custom validation for confirming the pin
    @AssertTrue(message = "PIN and confirmation PIN do not match")
    public boolean isPinMatching() {
        return transactionPin != null && transactionPin.equals(confirmPin);
    }



}
