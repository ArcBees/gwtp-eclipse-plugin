package com.arcbees.ide.plugin.eclipse.project;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.ResourceManager;

public class CreateProjectPage extends WizardPage {
    public CreateProjectPage() {
        super("wizardPageCreateProject");
        setMessage("Create a GWT-Platform project.");
        setPageComplete(false);
        
        setImageDescriptor(ResourceManager.getPluginImageDescriptor("com.arcbees.ide.plugin.eclipse", "icons/logo.png"));
        setTitle("GWTP Project Creation");
        setDescription("Create a GWT-Platform project.");
    }

    public void createControl(Composite parent) {
        parent.setTouchEnabled(true);
        Composite container = new Composite(parent, SWT.NULL);
        container.setTouchEnabled(true);

        setControl(container);
    }
}
