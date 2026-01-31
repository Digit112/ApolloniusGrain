import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Marks a method as a test.
* The method will be executed when the containing TestSet's test() method is run.
*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface Test {}