package com.eoi.grupo5.validacionesCustom.anotaciones;


import com.eoi.grupo5.validacionesCustom.TelefonoPatternValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TelefonoPatternValidator.class)
public @interface TelefonoPattern {
    String message() default "El teléfono debe tener entre 9 y 15 dígitos";
    Class[] groups() default {};
    Class[] payload() default {};
}
