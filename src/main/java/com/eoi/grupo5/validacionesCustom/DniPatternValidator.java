package com.eoi.grupo5.validacionesCustom;

import com.eoi.grupo5.validacionesCustom.anotaciones.DniPattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DniPatternValidator implements ConstraintValidator<DniPattern, String> {

    private static final String DNI_PATTERN = "\\d{8}[A-Za-z]";

    @Override
    public void initialize(DniPattern constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Campo vacío, no se valida el patrón
        }
        return value.matches(DNI_PATTERN);
    }
}
