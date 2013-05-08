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

package com.arcbees.ide.plugin.eclipse.wizard.createproject;

import org.eclipse.jface.wizard.Wizard;

public class CreateProjectWizard extends Wizard {
    private CreateProjectPage createProjectPage;
    private SelectArchetypePage selectArchetypePage;

    public CreateProjectWizard() {
        setWindowTitle("Create GWTP Project");
    }

    @Override
    public void addPages() {
        createProjectPage = new CreateProjectPage();
        selectArchetypePage = new SelectArchetypePage();
        
        addPage(createProjectPage);
        addPage(selectArchetypePage);
    }

    @Override
    public boolean performFinish() {
        return false;
    }
}
