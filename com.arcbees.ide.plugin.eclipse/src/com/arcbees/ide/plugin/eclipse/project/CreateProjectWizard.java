package com.arcbees.ide.plugin.eclipse.project;

import org.eclipse.jface.wizard.Wizard;

public class CreateProjectWizard extends Wizard {
    public CreateProjectWizard() {
        setWindowTitle("Create GWTP Project");
    }

    @Override
    public void addPages() {
        addPage(new CreateProjectPage());
    }

    @Override
    public boolean performFinish() {
        return false;
    }
}
