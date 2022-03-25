package net.mostow.util.exchanger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface JsonMandatory {
	String defaultValue() default "";
	int maximumLength() default 2147483647;
	int minimumLength() default 0;
	String failureMessage = "parameter can not be empty.";
	String maximumLengthError = "parameter lenth is too long.";
	String minimumLengthError = "parameter lenth is too small.";
}
