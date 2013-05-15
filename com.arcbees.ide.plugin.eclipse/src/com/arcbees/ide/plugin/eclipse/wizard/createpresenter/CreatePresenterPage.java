package com.arcbees.ide.plugin.eclipse.wizard.createpresenter;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.arcbees.ide.plugin.eclipse.domain.PresenterConfigModel;

public class CreatePresenterPage extends WizardPage {
    private PresenterConfigModel presenterConfigModel;

    public CreatePresenterPage(PresenterConfigModel presenterConfigModel) {
        super("wizardPageCreatePresenter");
        
        this.presenterConfigModel = presenterConfigModel;
        
        setTitle("Create Presenter");
        setDescription("Create a presenter for the proejct.");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
    }
}
