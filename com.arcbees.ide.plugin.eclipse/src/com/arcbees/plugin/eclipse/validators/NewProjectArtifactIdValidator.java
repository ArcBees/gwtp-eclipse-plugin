/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.plugin.eclipse.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class NewProjectArtifactIdValidator implements IValidator {
    @Override
    public IStatus validate(Object value) {
        if (value instanceof String) {
            String name = ((String) value).trim();
            boolean passes = validatePattern(name);
            if (passes) {
                return ValidationStatus.ok();
            } else {
                String message = "The package format doesn't follow the Java language specification.";
                return ValidationStatus.error(message);
            }
        }
        return ValidationStatus.error("Name is not a String.");
    }

    private boolean validatePattern(String name) {
        Pattern pattern = Pattern.compile("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*",
                Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }
}
