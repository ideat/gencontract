package mindware.com.utilities;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class ValidatorForNumbers implements Validator {

    @Override
    public ValidationResult apply(Object value, ValueContext valueContext) {
        if (!(value instanceof  Integer && value instanceof Double)) return ValidationResult.error("Debe ser un numero!");
         else   return ValidationResult.ok();
    }

    @Override
    public Object apply(Object o, Object o2) {
        return null;
    }

}
