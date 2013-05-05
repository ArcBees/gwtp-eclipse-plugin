package com.arcbees.ide.plugin.eclipse.project;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.ResourceManager;

public class CreateProjectPage extends WizardPage {
    public CreateProjectPage() {
        super("wizardPageCreateProject");
        
        setImageDescriptor(ResourceManager.getPluginImageDescriptor("com.arcbees.ide.plugin.eclipse", "icons/logo.png"));
        setTitle("GWTP Project Creation");
        setDescription("Create a GWT-Platform project.");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
    }
}
