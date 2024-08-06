package com.eoi.grupo5.validacionesCustom.anotaciones;

import com.eoi.grupo5.validacionesCustom.DniPatternValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DniPatternValidator.class)
public @interface DniPattern {
    String message() default "El DNI debe tener 8 dígitos seguidos de una letra";
    Class[] groups() default {};
    Class[] payload() default {};
}
