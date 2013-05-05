package com.arcbees.ide.plugin.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.arcbees.ide.plugin.eclipse.project.CreateProjectWizard;

public class CreateProjectHandler extends AbstractHandler {
    public CreateProjectHandler() {
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        openCreateProjectWizard(window);
        return null;
    }

    private void openCreateProjectWizard(IWorkbenchWindow window) {
        Wizard wizard = new CreateProjectWizard();
       
        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.create();
        dialog.open();
    }    
}
