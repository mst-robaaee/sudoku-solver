package net.mostow.util.exchanger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface JsonDate {
	String dateFormat() default defaultFormat;
	String defaultFormat = "yyyy-MM-dd HH:mm:ss.S";
	String failureMessage = "پارامتر باید تاریخ با فرمت مجاز باشد.";
}
