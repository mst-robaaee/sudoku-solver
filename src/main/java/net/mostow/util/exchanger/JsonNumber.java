package net.mostow.util.exchanger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface JsonNumber {
	String failureMessage = "پارامتر باید عدد باشد";
	String defaultValue() default "-1";
}
