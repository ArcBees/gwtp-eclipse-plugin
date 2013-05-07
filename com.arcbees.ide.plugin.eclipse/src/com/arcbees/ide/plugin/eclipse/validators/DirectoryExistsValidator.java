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

import java.io.File;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * Directory validator.
 */
public class DirectoryExistsValidator implements IValidator {
    private File file;

    @Override
    public IStatus validate(Object value) {
        if (value instanceof String) {
            String path = ((String) value).trim();
            try {
                file = new File(path);
            } catch (Exception e) {
                String message = "Directory path error. Choose a better directory.";
                return ValidationStatus.error(message);
            }
            
            if (file.isFile()) {
                String message = "Path is not a directory.";
                return ValidationStatus.error(message);
            } else if (!file.exists()) {
                return ValidationStatus.ok();
            } else {
                String message = "File exists.";
                return ValidationStatus.error(message);
            }
        }
        return ValidationStatus.error("Name is not a String.");
    }
}
