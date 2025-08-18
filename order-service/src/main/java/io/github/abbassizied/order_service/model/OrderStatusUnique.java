package io.github.abbassizied.order_service.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import io.github.abbassizied.order_service.service.OrderService;
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
 * Validate that the status value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = OrderStatusUnique.OrderStatusUniqueValidator.class
)
public @interface OrderStatusUnique {

    String message() default "{Exists.order.status}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class OrderStatusUniqueValidator implements ConstraintValidator<OrderStatusUnique, OrderStatus> {

        private final OrderService orderService;
        private final HttpServletRequest request;

        public OrderStatusUniqueValidator(final OrderService orderService,
                final HttpServletRequest request) {
            this.orderService = orderService;
            this.request = request;
        }

        @Override
        public boolean isValid(final OrderStatus value,
                final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equals(orderService.get(Long.parseLong(currentId)).getStatus())) {
                // value hasn't changed
                return true;
            }
            return !orderService.statusExists(value);
        }

    }

}
