import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Marks a method as a fixture, which supplies a value to be used as an argument to tests and other fixtures.
* Parameters with the same name as a fixture obtain their binding from the return value of that fixture.
*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface Fixture {}