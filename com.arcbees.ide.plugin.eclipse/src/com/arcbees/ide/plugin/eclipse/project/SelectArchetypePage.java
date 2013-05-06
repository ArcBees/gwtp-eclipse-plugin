package com.arcbees.ide.plugin.eclipse.project;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SelectArchetypePage extends WizardPage {
    public SelectArchetypePage() {
        super("wizardPageSelectArchetype");
        setTitle("Select Archetype");
        setDescription("Select a project template to start with.");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
    }
}
