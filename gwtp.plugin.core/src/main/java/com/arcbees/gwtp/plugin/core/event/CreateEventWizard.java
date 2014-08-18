package com.arcbees.gwtp.plugin.core.event;

import com.arcbees.gwtp.plugin.core.common.GWTPNewTypeWizard;

@SuppressWarnings("restriction")
public class CreateEventWizard extends GWTPNewTypeWizard {

    public CreateEventWizard() {
        super("Create GWTP Presenter", new CreateEventPage());
    }



}
