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

package com.arcbees.ide.plugin.eclipse.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * Archetype validator.
 */
public class ArchetypeSelectionValidator implements IValidator {
    @Override
    public IStatus validate(Object value) {
        if (value instanceof Integer) {
            Integer index = ((Integer) value);
            if (index > -1) {
                return ValidationStatus.ok();
            } else {
                return ValidationStatus.error("Select a Archetype.");
            }
        }
        return ValidationStatus.error("Selection is not a Integer.");
    }
}
