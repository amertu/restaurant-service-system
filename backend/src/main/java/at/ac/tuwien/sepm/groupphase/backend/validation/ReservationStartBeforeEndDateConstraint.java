package at.ac.tuwien.sepm.groupphase.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = ReservationStartBeforeEndDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReservationStartBeforeEndDateConstraint {


    String message() default "End date time must be after start date time of the reservation.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

