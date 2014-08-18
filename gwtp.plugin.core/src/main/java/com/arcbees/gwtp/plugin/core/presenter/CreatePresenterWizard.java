package com.arcbees.gwtp.plugin.core.presenter;

import com.arcbees.gwtp.plugin.core.common.GWTPNewTypeWizard;

public class CreatePresenterWizard extends GWTPNewTypeWizard {

    public CreatePresenterWizard() {
        super("Create GWTP Presenter", new CreatePresenterPage());
    }

}
