package com.eoi.grupo5.validacionesCustom;

import com.eoi.grupo5.validacionesCustom.anotaciones.TelefonoPattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefonoPatternValidator implements ConstraintValidator<TelefonoPattern, String> {

    private static final String TELEFONO_PATTERN = "\\d{9,15}";

    @Override
    public void initialize(TelefonoPattern constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.matches(TELEFONO_PATTERN);
    }
}
