package com.arcbees.gwtp.plugin.core.presenter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

@SuppressWarnings("restriction")
public class CreatePresenterWizard extends NewElementWizard {

    private final CreatePresenterPage page;

    public CreatePresenterWizard() {
        setWindowTitle("Create GWTP Presenter");
        setHelpAvailable(false);
        page = new CreatePresenterPage();
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
