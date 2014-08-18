package com.arcbees.gwtp.plugin.core.common;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public abstract class GWTPNewTypeWizardPage extends NewTypeWizardPage {

    public GWTPNewTypeWizardPage(final String id, final String title, final String description) {
        super(true, id);
        setTitle(title);
        setDescription(description);

    }

    protected Button createButton(final Composite container, final String text, final int type) {
        final Button button = new Button(container, type);
        button.setBounds(0, 0, 100, 20);
        button.setText(text);
        return button;
    }

    @Override
    public final void createControl(final Composite parent) {
        initializeDialogUnits(parent);
        final Composite container = new Composite(parent, SWT.NONE);
        container.setFont(parent.getFont());

        final GridLayout layout = new GridLayout();
        layout.numColumns = getNumberOfColumns();
        container.setLayout(layout);

        createTypeNameControls(container, getNumberOfColumns());
        createPackageControls(container, getNumberOfColumns());

        extendControl(container);

        setControl(container);

        Dialog.applyDialogFont(container);

    }

    protected Group createGroup(final Composite container, final String title, final int columns) {
        final Group group = new Group(container, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, getNumberOfColumns(), 1));
        group.setLayout(new GridLayout(columns, false));
        group.setText(title);
        return group;
    }

    private void doStatusUpdate() {
        final IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus, fTypeNameStatus };

        updateStatus(status);
    }

    protected final void ensurePackageExists(final IProgressMonitor monitor) throws JavaModelException {
        final IPackageFragmentRoot root= getPackageFragmentRoot();
        IPackageFragment pack= getPackageFragment();
        if (pack == null) {
            pack= root.getPackageFragment(""); //$NON-NLS-1$
        }

        if (!pack.exists()) {
            final String packName= pack.getElementName();
            pack= root.createPackageFragment(packName, true, new SubProgressMonitor(monitor, 1));
        }

    }

    protected abstract void extendControl(final Composite container);

    protected abstract String getNameSuffix();

    protected int getNumberOfColumns() {
        return 4;
    }

    @Override
    public String getTypeName() {
        String typeName = super.getTypeName();
        if (typeName.toLowerCase().endsWith(getNameSuffix().toLowerCase())) {
            typeName = typeName.substring(0, typeName.length() - getNameSuffix().length());
        }
        return typeName;
    }

    @Override
    protected void handleFieldChanged(final String fieldName) {
        super.handleFieldChanged(fieldName);
        doStatusUpdate();
    }

    protected final void init(final IStructuredSelection selection) {
        final IJavaElement jelem = getInitialJavaElement(selection);
        initContainerPage(jelem);
        initTypePage(jelem);

        doStatusUpdate();

    }



}
