package io.github.abbassizied.customer_service.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import io.github.abbassizied.customer_service.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the phone value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = CustomerPhoneUnique.CustomerPhoneUniqueValidator.class
)
public @interface CustomerPhoneUnique {

    String message() default "{Exists.customer.phone}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CustomerPhoneUniqueValidator implements ConstraintValidator<CustomerPhoneUnique, String> {

        private final CustomerService customerService;
        private final HttpServletRequest request;

        public CustomerPhoneUniqueValidator(final CustomerService customerService,
                final HttpServletRequest request) {
            this.customerService = customerService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(customerService.get(Long.parseLong(currentId)).getPhone())) {
                // value hasn't changed
                return true;
            }
            return !customerService.phoneExists(value);
        }

    }

}
