package com.arcbees.plugin.eclipse.wizard.createpresenter;

import org.eclipse.jface.wizard.Wizard;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;

public class CreatePresenterWizard extends Wizard {
    private CreatePresenterPage createPresenterPage;

    public CreatePresenterWizard() {
        setWindowTitle("Create Presenter");
    }

    @Override
    public void addPages() {
        PresenterConfigModel presenterConfigModel = new PresenterConfigModel();
        
        createPresenterPage = new CreatePresenterPage(presenterConfigModel);
        
        addPage(createPresenterPage);
    }

    @Override
    public boolean performFinish() {
        return false;
    }
}
