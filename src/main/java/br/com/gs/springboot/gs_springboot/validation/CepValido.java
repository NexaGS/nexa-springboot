package br.com.gs.springboot.gs_springboot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {CepValidator.class})
@Target(FIELD)
@Retention(RUNTIME)
public @interface CepValido {
    String message() default "CEP inválido.";

    Class<?>[] groups() default {};

    Class<? extends Payload> [] payload() default {};
}
