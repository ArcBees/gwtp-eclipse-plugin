package com.arcbees.ide.plugin.eclipse.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * Name validator. 
 * 
 * Validates: Start with capital letter and then letters, spaces and numbers can follow.
 */
public class NameValidator implements IValidator {
    @Override
    public IStatus validate(Object value) {
        if (value instanceof String) {
            String name = ((String) value).trim();
            boolean passes = name.matches("[A-Z][a-zA-Z\0400-9]+");
            if (passes) {
                return ValidationStatus.ok();
            } else {
                String message = "";
                if (name.matches("[a-z].*")) {
                    message = "Name must start with a capital letter.";
                } else {
                    message = "Name must start with a capital letter. " +
                    		"After the capital, it can contain letters, spaces and numbers.";
                }
                ValidationStatus.error(message);
            }
        }
        return ValidationStatus.error("Name is not a String.");
    }
}
