package br.com.gs.springboot.gs_springboot.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CepValidator implements ConstraintValidator<CepValido, String> {
    @Override
    public void initialize(CepValido constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        return cep != null && cep.matches("\\d{8}");
    }
}
