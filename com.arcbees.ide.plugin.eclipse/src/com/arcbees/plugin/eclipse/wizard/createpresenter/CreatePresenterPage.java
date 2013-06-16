/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.plugin.eclipse.wizard.createpresenter;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import com.arcbees.plugin.eclipse.filter.ContentSlotSelectionExtension;
import com.arcbees.plugin.eclipse.filter.WidgetSelectionExtension;
import com.arcbees.plugin.eclipse.validators.PackageNameValidator;

public class CreatePresenterPage extends NewTypeWizardPage {
    private DataBindingContext m_bindingContext;
    private PresenterConfigModel presenterConfigModel;

    private Composite parent;

    private Group grpNestedPresenterOptions;
    private Group grpPopupPresenter;
    private Group grpPresenterWidgetOptions;
    private Button btnNestedPresenter;
    private Button btnPresenterWidget;
    private Button btnPopupPresenter;

    private Text packageName;
    private Text name;
    private Text nameToken;
    private Text gateKeeper;
    private Text overridePopupPanel;
    private Text contentSlot;

    private Button btnRevealcontentevent;
    private Button btnRevealrootcontentevent;
    private Button btnRevealrootlayoutcontentevent;
    private Button btnIsAPlace;
    private Button btnIsCrawlable;

    public CreatePresenterPage(PresenterConfigModel presenterConfigModel) {
        super(true, "wizardPageCreatePresenter");

        this.presenterConfigModel = presenterConfigModel;

        setTitle("Create Presenter");
        setDescription("Create a presenter for the project.");
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        setDefaults();
        setPackageName();
    }

    public void createControl(Composite parent) {
        this.parent = parent;

        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        Label lblName = new Label(container, SWT.NONE);
        lblName.setText("Name: 'AppHome'");

        name = new Text(container, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPackage = new Label(container, SWT.NONE);
        lblPackage.setText("Package: 'com.arcbees.project.client'");

        Composite composite = new Composite(container, SWT.NONE);
        GridData gd_composite = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gd_composite.heightHint = 28;
        gd_composite.widthHint = 568;
        composite.setLayoutData(gd_composite);

        packageName = new Text(composite, SWT.NONE);
        packageName.setBounds(0, 4, 422, 19);

        Button btnSelectPackage = new Button(composite, SWT.NONE);
        btnSelectPackage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openPackageSelectionDialog();
            }
        });
        btnSelectPackage.setBounds(438, 0, 120, 28);
        btnSelectPackage.setText("Select Package");

        Group grpPresenterType = new Group(container, SWT.NONE);
        grpPresenterType.setLayout(null);
        GridData gd_grpPresenterType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpPresenterType.heightHint = 31;
        gd_grpPresenterType.widthHint = 562;
        grpPresenterType.setLayoutData(gd_grpPresenterType);
        grpPresenterType.setText("Presenter Type");

        btnNestedPresenter = new Button(grpPresenterType, SWT.RADIO);
        btnNestedPresenter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = btnNestedPresenter.getSelection();
                if (selected) {
                    grpNestedPresenterOptions.setVisible(true);
                    grpPopupPresenter.setVisible(false);
                    grpPresenterWidgetOptions.setVisible(false);
                }
            }
        });
        btnNestedPresenter.setSelection(true);
        btnNestedPresenter.setBounds(10, 10, 113, 18);
        btnNestedPresenter.setText("Nested Presenter");

        btnPresenterWidget = new Button(grpPresenterType, SWT.RADIO);
        btnPresenterWidget.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = btnPresenterWidget.getSelection();
                if (selected) {
                    grpNestedPresenterOptions.setVisible(false);
                    grpPopupPresenter.setVisible(true);
                    grpPresenterWidgetOptions.setVisible(false);
                }
            }
        });
        btnPresenterWidget.setBounds(143, 10, 112, 18);
        btnPresenterWidget.setText("Presenter Widget");

        btnPopupPresenter = new Button(grpPresenterType, SWT.RADIO);
        btnPopupPresenter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = btnPopupPresenter.getSelection();
                if (selected) {
                    grpNestedPresenterOptions.setVisible(false);
                    grpPopupPresenter.setVisible(false);
                    grpPresenterWidgetOptions.setVisible(true);
                }
            }
        });
        btnPopupPresenter.setBounds(273, 10, 109, 18);
        btnPopupPresenter.setText("Popup Presenter");

        grpNestedPresenterOptions = new Group(container, SWT.NONE);
        grpNestedPresenterOptions.setLayout(null);
        GridData gd_grpNestedPresenterOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpNestedPresenterOptions.heightHint = 107;
        gd_grpNestedPresenterOptions.widthHint = 562;
        grpNestedPresenterOptions.setLayoutData(gd_grpNestedPresenterOptions);
        grpNestedPresenterOptions.setText("Nested Presenter Options");

        btnIsAPlace = new Button(grpNestedPresenterOptions, SWT.CHECK);
        btnIsAPlace.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = btnIsAPlace.getSelection();
                if (selected) {
                    nameToken.setEnabled(true);
                    btnIsCrawlable.setEnabled(true);
                } else {
                    nameToken.setEnabled(false);
                    btnIsCrawlable.setEnabled(false);
                }
            }
        });
        btnIsAPlace.setBounds(10, 68, 71, 18);
        btnIsAPlace.setText("Is a Place");

        nameToken = new Text(grpNestedPresenterOptions, SWT.BORDER);
        nameToken.setBounds(164, 68, 269, 19);

        Label lblPlaceNamenametoken = new Label(grpNestedPresenterOptions, SWT.NONE);
        lblPlaceNamenametoken.setBounds(87, 70, 72, 14);
        lblPlaceNamenametoken.setToolTipText("Name of the place.");
        lblPlaceNamenametoken.setText("NameToken:");

        Button btnCodesplit = new Button(grpNestedPresenterOptions, SWT.CHECK);
        btnCodesplit.setBounds(10, 93, 93, 18);
        btnCodesplit.setText("CodeSplit");

        btnRevealcontentevent = new Button(grpNestedPresenterOptions, SWT.RADIO);
        btnRevealcontentevent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnRevealcontentevent.setSelection(true);
                btnRevealrootcontentevent.setSelection(false);
                btnRevealrootlayoutcontentevent.setSelection(false);
            }
        });
        btnRevealcontentevent.setSelection(true);
        btnRevealcontentevent.setBounds(10, 10, 136, 18);
        btnRevealcontentevent.setText("RevealContentEvent");

        btnRevealrootcontentevent = new Button(grpNestedPresenterOptions, SWT.RADIO);
        btnRevealrootcontentevent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnRevealcontentevent.setSelection(false);
                btnRevealrootcontentevent.setSelection(true);
                btnRevealrootlayoutcontentevent.setSelection(false);
            }
        });
        btnRevealrootcontentevent.setBounds(164, 10, 162, 18);
        btnRevealrootcontentevent.setText("RevealRootContentEvent");

        btnRevealrootlayoutcontentevent = new Button(grpNestedPresenterOptions, SWT.RADIO);
        btnRevealrootlayoutcontentevent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnRevealcontentevent.setSelection(false);
                btnRevealrootcontentevent.setSelection(false);
                btnRevealrootlayoutcontentevent.setSelection(true);
            }
        });
        btnRevealrootlayoutcontentevent.setBounds(332, 10, 200, 18);
        btnRevealrootlayoutcontentevent.setText("RevealRootLayoutContentEvent");

        contentSlot = new Text(grpNestedPresenterOptions, SWT.BORDER);
        contentSlot.setBounds(10, 39, 349, 19);

        Button btnSelectContentSlot = new Button(grpNestedPresenterOptions, SWT.NONE);
        btnSelectContentSlot.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IType contentSlotType = selectContentSlot();
                if (contentSlotType != null) {
                    String slot = contentSlotType.getFullyQualifiedName('.');
                    contentSlot.setText(slot);
                }
            }
        });
        btnSelectContentSlot.setBounds(365, 34, 152, 28);
        btnSelectContentSlot.setText("Select Content Slot");

        btnIsCrawlable = new Button(grpNestedPresenterOptions, SWT.CHECK);
        btnIsCrawlable.setBounds(439, 68, 93, 18);
        btnIsCrawlable.setText("Is crawlable");

        grpPopupPresenter = new Group(container, SWT.NONE);
        grpPopupPresenter.setLayout(null);
        GridData gd_grpPopupPresenter = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpPopupPresenter.widthHint = 562;
        grpPopupPresenter.setLayoutData(gd_grpPopupPresenter);
        grpPopupPresenter.setText("Popup Presenter Options");

        Button btnOverrideDefaultPopup = new Button(grpPopupPresenter, SWT.CHECK);
        btnOverrideDefaultPopup.setBounds(10, 10, 179, 18);
        btnOverrideDefaultPopup.setText("Override default Popup Panel");

        overridePopupPanel = new Text(grpPopupPresenter, SWT.BORDER);
        overridePopupPanel.setBounds(195, 10, 184, 19);

        Button btnSelectPanel = new Button(grpPopupPresenter, SWT.NONE);
        btnSelectPanel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IType popupType = selectPopupWidget();
                if (popupType != null) {
                    String widget = popupType.getFullyQualifiedName('.');
                    overridePopupPanel.setText(widget);
                }
            }
        });
        btnSelectPanel.setBounds(385, 6, 95, 28);
        btnSelectPanel.setText("Select Panel");

        grpPresenterWidgetOptions = new Group(container, SWT.NONE);
        GridData gd_grpPresenterWidgetOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpPresenterWidgetOptions.heightHint = 25;
        gd_grpPresenterWidgetOptions.widthHint = 562;
        grpPresenterWidgetOptions.setLayoutData(gd_grpPresenterWidgetOptions);
        grpPresenterWidgetOptions.setText("Presenter Widget Options");
        grpPresenterWidgetOptions.setLayout(null);

        Button btnIsASingleton = new Button(grpPresenterWidgetOptions, SWT.CHECK);
        btnIsASingleton.setBounds(10, 10, 94, 18);
        btnIsASingleton.setText("Is a Singleton");

        Group grpConvenienceOptions = new Group(container, SWT.NONE);
        grpConvenienceOptions.setLayout(null);
        GridData gd_grpConvenienceOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpConvenienceOptions.heightHint = 170;
        gd_grpConvenienceOptions.widthHint = 562;
        grpConvenienceOptions.setLayoutData(gd_grpConvenienceOptions);
        grpConvenienceOptions.setText("Extra Options");

        Button btnAddUihandlers = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddUihandlers.setBounds(10, 30, 105, 18);
        btnAddUihandlers.setText("Add UiHandlers");

        Label lblPresenterLifecycleMethods = new Label(grpConvenienceOptions, SWT.NONE);
        lblPresenterLifecycleMethods.setBounds(10, 66, 153, 14);
        lblPresenterLifecycleMethods.setText("Presenter Lifecycle Methods");

        Button btnAddOnbind = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddOnbind.setBounds(10, 86, 91, 18);
        btnAddOnbind.setText("Add onBind()");

        Button btnAddOnhide = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddOnhide.setBounds(10, 110, 92, 18);
        btnAddOnhide.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnAddOnhide.setText("Add onHide()");

        Button btnAddOnreset = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddOnreset.setBounds(10, 134, 97, 18);
        btnAddOnreset.setText("Add onReset()");

        Button btnAddOnunbind = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddOnunbind.setBounds(9, 158, 106, 18);
        btnAddOnunbind.setText("Add onUnbind()");

        Label lblEvents = new Label(grpConvenienceOptions, SWT.NONE);
        lblEvents.setBounds(10, 10, 59, 14);
        lblEvents.setText("Events");

        Label lblSecurity = new Label(grpConvenienceOptions, SWT.NONE);
        lblSecurity.setBounds(224, 132, 192, 14);
        lblSecurity.setText("Gatekeeper Security");

        gateKeeper = new Text(grpConvenienceOptions, SWT.BORDER);
        gateKeeper.setBounds(224, 151, 192, 19);

        Button btnNewButton = new Button(grpConvenienceOptions, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnNewButton.setBounds(422, 148, 122, 28);
        btnNewButton.setText("Select Gatekeeper");

        Button btnUseManualReveal = new Button(grpConvenienceOptions, SWT.CHECK);
        btnUseManualReveal.setBounds(224, 30, 153, 18);
        btnUseManualReveal.setText("Use Manual Reveal");

        Label lblOnReveal = new Label(grpConvenienceOptions, SWT.NONE);
        lblOnReveal.setBounds(224, 10, 59, 14);
        lblOnReveal.setText("On Reveal");

        Label lblQuerystring = new Label(grpConvenienceOptions, SWT.NONE);
        lblQuerystring.setBounds(224, 66, 77, 14);
        lblQuerystring.setText("Querystring");

        Button btnPrepareFromRequest = new Button(grpConvenienceOptions, SWT.CHECK);
        btnPrepareFromRequest.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnPrepareFromRequest.setBounds(224, 86, 209, 18);
        btnPrepareFromRequest.setText("Use Prepare from Request");
        m_bindingContext = initDataBindings();
        
        observeBindingChanges();
    }
    
    private void observeBindingChanges() {
        IObservableList bindings = m_bindingContext.getValidationStatusProviders();
        for (Object o : bindings) {
            Binding binding = (Binding) o;
            
            // Validator feedback control
            ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
            
            binding.getTarget().addChangeListener(new IChangeListener() {
                @Override
                public void handleChange(ChangeEvent event) {
                    checkBindingValidationStatus();
                }
            });
        }
    }
    
    /**
     * Check all the bindings validators for OK status.
     */
    private void checkBindingValidationStatus() {
        IObservableList bindings = m_bindingContext.getValidationStatusProviders();

        boolean success = true;
        for (Object o : bindings) {
            Binding b = (Binding) o;
            IObservableValue status = b.getValidationStatus();
            IStatus istatus = (IStatus) status.getValue();
            if (!istatus.isOK()) {
                success = false;
            }
            // TODO
            //System.out.println("istatus=" + istatus.getMessage() + " ... " + istatus.isOK() + " " + presenterConfigModel);
        }

        // All statuses passed, enable next button.
        setPageComplete(success);
    }

    // TODO when the dialog opens up, focus on the package directory first, if
    // its selected
    private void openPackageSelectionDialog() {
        DirectoryDialog dirDialog = new DirectoryDialog(parent.getShell());
        dirDialog.setText("Select a directory to put the presenter in.");
        String selectedDir = dirDialog.open();
        if (selectedDir != null) {
            packageName.setText(selectedDir);
            presenterConfigModel.setPath(selectedDir);
        }
    }

    private void setPackageName() {
        IPackageFragment selectedPackage = getPackageSelection();
        String name = "";
        if (selectedPackage != null) {
            name = selectedPackage.getElementName();
        }
        
        if (selectedPackage != null && name.contains(".client")) {
            presenterConfigModel.setPath(name);
            presenterConfigModel.setSelectedPackage(selectedPackage);
            packageName.setText(name);
        } else if (name != null) {
            setMessage("The package '" + name + " is not a client side package.", IMessageProvider.ERROR);
        } else {
            setMessage("Select a project in the navigator with a client side package before creating the presenter.",
                    IMessageProvider.ERROR);
        }
    }
    
    private IPackageFragment getPackageSelection() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        ISelectionService selectionservice = window.getSelectionService();
        if (selectionservice == null) {
            return null;
        }

        TreeSelection selection = (TreeSelection) selectionservice.getSelection();
        if (selection == null) {
            return null;
        }

        IPackageFragment selectedPackage = null;
        try {
            selectedPackage = (IPackageFragment) selection.getFirstElement();
        } catch (Exception e) {
        }
        return selectedPackage;
    }

    private void setDefaults() {
        grpNestedPresenterOptions.setVisible(true);
        grpPopupPresenter.setVisible(false);
        grpPresenterWidgetOptions.setVisible(false);

        nameToken.setEnabled(false);
        btnIsCrawlable.setEnabled(false);
    }

    private IType selectContentSlot() {
        IJavaProject project = presenterConfigModel.getProject();
        if (project == null) {
            // TODO notify the user that a project is not selected.
            return null;
        }

        IJavaElement[] elements = new IJavaElement[] { project };
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard()
                .getContainer(), scope, IJavaSearchConstants.CLASS, new ContentSlotSelectionExtension(
                presenterConfigModel));
        dialog.setTitle("ContentSlot Selection");
        dialog.setMessage("Select the Presenter's parent. Parent must implement 'HasSlots'.");
        dialog.setInitialPattern("*Presenter");

        if (dialog.open() == Window.OK) {
            return (IType) dialog.getFirstResult();
        }
        return null;
    }

    private IType selectPopupWidget() {
        IJavaProject project = presenterConfigModel.getProject();
        if (project == null) {
            // TODO notify the user that a project is not selected.
            return null;
        }

        IJavaElement[] elements = new IJavaElement[] { project };
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard()
                .getContainer(), scope, IJavaSearchConstants.CLASS, new WidgetSelectionExtension(presenterConfigModel));
        dialog.setTitle("Select a Widget");
        dialog.setMessage("Select a widget to override the default popup panel.");
        dialog.setInitialPattern("*");

        if (dialog.open() == Window.OK) {
            return (IType) dialog.getFirstResult();
        }
        return null;
    }
    protected DataBindingContext initDataBindings() {
        DataBindingContext bindingContext = new DataBindingContext();
        //
        IObservableValue observeTextPackageNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(packageName);
        IObservableValue bytesPresenterConfigModelgetPathObserveValue = PojoProperties.value("bytes").observe(presenterConfigModel.getPath());
        UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
        strategy_1.setBeforeSetValidator(new PackageNameValidator());
        bindingContext.bindValue(observeTextPackageNameObserveWidget, bytesPresenterConfigModelgetPathObserveValue, strategy_1, null);
        //
        IObservableValue observeSelectionBtnNestedPresenterObserveWidget = WidgetProperties.selection().observe(btnNestedPresenter);
        IObservableValue nestedPresenterPresenterConfigModelObserveValue = BeanProperties.value("nestedPresenter").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnNestedPresenterObserveWidget, nestedPresenterPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnPresenterWidgetObserveWidget = WidgetProperties.selection().observe(btnPresenterWidget);
        IObservableValue presenterWidgetPresenterConfigModelObserveValue = BeanProperties.value("presenterWidget").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnPresenterWidgetObserveWidget, presenterWidgetPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnPopupPresenterObserveWidget = WidgetProperties.selection().observe(btnPopupPresenter);
        IObservableValue popupPresenterPresenterConfigModelObserveValue = BeanProperties.value("popupPresenter").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnPopupPresenterObserveWidget, popupPresenterPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeTextNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(name);
        IObservableValue namePresenterConfigModelObserveValue = BeanProperties.value("name").observe(presenterConfigModel);
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new PackageNameValidator());
        bindingContext.bindValue(observeTextNameObserveWidget, namePresenterConfigModelObserveValue, strategy, null);
        //
        return bindingContext;
    }
}
