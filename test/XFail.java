/**
* Marks a test which is anticipated to fail.
* Expected failures and unexpected passes are counted separately and the results of expected failures are not displayed.
*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface XFail {}