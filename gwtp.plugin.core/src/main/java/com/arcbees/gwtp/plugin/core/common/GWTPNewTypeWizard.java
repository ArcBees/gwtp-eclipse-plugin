package com.arcbees.gwtp.plugin.core.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

@SuppressWarnings("restriction")
public abstract class GWTPNewTypeWizard extends NewElementWizard {

    private final GWTPNewTypeWizardPage page;

    public GWTPNewTypeWizard(final String windowTitle, final GWTPNewTypeWizardPage page) {
        setWindowTitle("Create GWTP Presenter");
        setHelpAvailable(false);
        this.page = page;
    }

    @Override
    public void addPages() {
        page.init(getSelection());
        addPage(page);
    }

    @Override
    protected void finishPage(final IProgressMonitor monitor) throws InterruptedException, CoreException {
        page.createType(monitor);
    }

    @Override
    public IJavaElement getCreatedElement() {
        return page.getCreatedType();
    }

    @Override
    public boolean performFinish() {
        warnAboutTypeCommentDeprecation();
        return super.performFinish();
    }

}
