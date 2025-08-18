package io.github.abbassizied.order_service.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import io.github.abbassizied.order_service.service.OrderItemService;
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
 * Validate that the id value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = OrderItemProductUnique.OrderItemProductUniqueValidator.class
)
public @interface OrderItemProductUnique {

    String message() default "{Exists.orderItem.product}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class OrderItemProductUniqueValidator implements ConstraintValidator<OrderItemProductUnique, Long> {

        private final OrderItemService orderItemService;
        private final HttpServletRequest request;

        public OrderItemProductUniqueValidator(final OrderItemService orderItemService,
                final HttpServletRequest request) {
            this.orderItemService = orderItemService;
            this.request = request;
        }

        @Override
        public boolean isValid(final Long value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equals(orderItemService.get(Long.parseLong(currentId)).getProduct())) {
                // value hasn't changed
                return true;
            }
            return !orderItemService.productExists(value);
        }

    }

}
