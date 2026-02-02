package ekobadd.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Marks a test which is anticipated to fail.
* Expected failures and unexpected passes are counted separately and the results of expected failures are not displayed.
*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XFail {}