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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceField;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.dialogs.PackageSelectionDialog;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.progress.IProgressService;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import com.arcbees.plugin.eclipse.filter.WidgetSelectionExtension;
import com.arcbees.plugin.eclipse.validators.NameTokenValidator;
import com.arcbees.plugin.eclipse.validators.PackageNameValidator;
import com.arcbees.plugin.eclipse.validators.PlaceValidator;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import swing2swt.layout.FlowLayout;

/**
 * All of the UI is generated from Eclipse JFace Editor
 */
public class CreatePresenterPage extends NewTypeWizardPage {
    private DataBindingContext m_bindingContext;
    private PresenterConfigModel presenterConfigModel;

    private Composite parent;
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
    private Button btnCodesplit;
    private Button btnSelectContentSlot;
    private Button btnAddUihandlers;
    private Button btnAddOnbind;
    private Button btnAddOnhide;
    private Button btnAddOnreset;
    private Button btnAddOnunbind;
    private Button btnUseManualReveal;
    private Button btnPrepareFromRequest;
    private Binding bindValueForNameToken;
    private Link link;
    private Composite composite_2;
    private TabItem tbtmPopupPresenter;
    private Composite composite_1;
    private Composite composite_3;
    private Group group;
    private Composite composite_4;
    private Button btnSelectGatekeeper;
    private Label lblSecurity;
    private TabFolder tabFolder;

    public CreatePresenterPage(PresenterConfigModel presenterConfigModel) {
        super(true, "wizardPageCreatePresenter");

        this.presenterConfigModel = presenterConfigModel;
        this.presenterConfigModel.setShell(getShell());

        setTitle("Create Presenter");
        setDescription("Create a presenter for the project.");
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        setPackageNameViaWizardSelectedFocus();
        setDefaults();
    }

    public void createControl(Composite parent) {
        this.parent = parent;

        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        group = new Group(container, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        group.setLayout(new GridLayout(1, false));

        Label lblName = new Label(group, SWT.NONE);
        lblName.setText("Name: 'AppHome'");

        name = new Text(group, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPackage = new Label(group, SWT.NONE);
        lblPackage.setText("Package: 'com.arcbees.project.client'");

        composite_4 = new Composite(group, SWT.NONE);
        GridLayout gl_composite_4 = new GridLayout(2, false);
        gl_composite_4.marginHeight = 0;
        gl_composite_4.marginWidth = 0;
        gl_composite_4.verticalSpacing = 0;
        gl_composite_4.horizontalSpacing = 0;
        composite_4.setLayout(gl_composite_4);
        GridData gd_composite_4 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_composite_4.heightHint = 37;
        composite_4.setLayoutData(gd_composite_4);

        packageName = new Text(composite_4, SWT.BORDER);
        packageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnSelectPackage = new Button(composite_4, SWT.NONE);
        btnSelectPackage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openPackageSelectionDialog();
            }
        });
        btnSelectPackage.setText("Select Package");

        Group grpPresenterType = new Group(container, SWT.NONE);
        grpPresenterType.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        GridData gd_grpPresenterType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
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
                    tabFolder.setSelection(0);
                }
            }
        });
        btnNestedPresenter.setSelection(true);
        btnNestedPresenter.setText("Nested Presenter");

        btnPresenterWidget = new Button(grpPresenterType, SWT.RADIO);
        btnPresenterWidget.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = btnPresenterWidget.getSelection();
                if (selected) {
                    tabFolder.setSelection(1);
                }
            }
        });
        btnPresenterWidget.setText("Presenter Widget");

        btnPopupPresenter = new Button(grpPresenterType, SWT.RADIO);
        btnPopupPresenter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = btnPopupPresenter.getSelection();
                if (selected) {
                    tabFolder.setSelection(2);
                }
            }
        });
        btnPopupPresenter.setText("Popup Presenter");

        link = new Link(grpPresenterType, SWT.NONE);
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String surl = "https://github.com/ArcBees/gwtp-eclipse-plugin/wiki/Presenter-Creation";
                gotoUrl(surl);
            }
        });
        link.setToolTipText("Find more help on presenter creation");
        link.setText("<a>Presenter Creation Help</a>");

        tabFolder = new TabFolder(container, SWT.NONE);
        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = tabFolder.getSelectionIndex();
                if (index == 0) {
                    btnNestedPresenter.setSelection(true);
                    btnPresenterWidget.setSelection(false);
                    btnPopupPresenter.setSelection(false);
                } else if (index == 1) {
                    btnNestedPresenter.setSelection(false);
                    btnPresenterWidget.setSelection(true);
                    btnPopupPresenter.setSelection(false);
                } else if (index == 2) {
                    btnNestedPresenter.setSelection(false);
                    btnPresenterWidget.setSelection(false);
                    btnPopupPresenter.setSelection(true);
                }
            }
        });
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        TabItem tbtmNestedPresenter = new TabItem(tabFolder, SWT.NONE);
        tbtmNestedPresenter.setText("Nested Presenter");

        composite_2 = new Composite(tabFolder, SWT.NONE);
        tbtmNestedPresenter.setControl(composite_2);
        composite_2.setLayout(new GridLayout(1, false));

        Group grpReveal = new Group(composite_2, SWT.NONE);
        grpReveal.setLayout(new GridLayout(5, false));
        grpReveal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpReveal.setText("Reveal In");

        btnRevealrootcontentevent = new Button(grpReveal, SWT.RADIO);
        btnRevealrootcontentevent.setSelection(true);
        btnRevealrootcontentevent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnRevealcontentevent.setSelection(false);
                btnRevealrootcontentevent.setSelection(true);
                btnRevealrootlayoutcontentevent.setSelection(false);
                contentSlot.setEnabled(false);
                btnSelectContentSlot.setEnabled(false);

                presenterConfigModel.setRevealInRoot(true);
                presenterConfigModel.setRevealInRootLayout(false);
                presenterConfigModel.setRevealInSlot(false);
            }
        });
        btnRevealrootcontentevent.setText("Root");

        btnRevealrootlayoutcontentevent = new Button(grpReveal, SWT.RADIO);
        btnRevealrootlayoutcontentevent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnRevealcontentevent.setSelection(false);
                btnRevealrootcontentevent.setSelection(false);
                btnRevealrootlayoutcontentevent.setSelection(true);
                contentSlot.setEnabled(false);
                btnSelectContentSlot.setEnabled(false);

                presenterConfigModel.setRevealInRoot(false);
                presenterConfigModel.setRevealInRootLayout(true);
                presenterConfigModel.setRevealInSlot(false);
            }
        });
        btnRevealrootlayoutcontentevent.setText("RootLayout");

        btnRevealcontentevent = new Button(grpReveal, SWT.RADIO);
        btnRevealcontentevent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnRevealcontentevent.setSelection(true);
                btnRevealrootcontentevent.setSelection(false);
                btnRevealrootlayoutcontentevent.setSelection(false);
                contentSlot.setEnabled(true);
                btnSelectContentSlot.setEnabled(true);

                presenterConfigModel.setRevealInRoot(false);
                presenterConfigModel.setRevealInRootLayout(false);
                presenterConfigModel.setRevealInSlot(true);
            }
        });
        btnRevealcontentevent.setText("Slot");

        contentSlot = new Text(grpReveal, SWT.BORDER);
        contentSlot.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        contentSlot.setEnabled(false);

        btnSelectContentSlot = new Button(grpReveal, SWT.NONE);
        btnSelectContentSlot.setEnabled(false);
        btnSelectContentSlot.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectContentSlot();
            }
        });
        btnSelectContentSlot.setText("Select Slot");

        Group grpPlace = new Group(composite_2, SWT.NONE);
        grpPlace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpPlace.setText("Place");
        grpPlace.setLayout(new GridLayout(4, false));

        btnIsAPlace = new Button(grpPlace, SWT.CHECK);
        btnIsAPlace.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = btnIsAPlace.getSelection();
                if (selected) {
                    nameToken.setEnabled(true);
                    btnIsCrawlable.setEnabled(true);
                    nameToken.setFocus();
                } else {
                    nameToken.setEnabled(false);
                    btnIsCrawlable.setEnabled(false);
                    nameToken.setText("");
                }
                bindValueForNameToken.validateTargetToModel();
            }
        });
        btnIsAPlace.setText("Is a Place");

        Label lblPlaceNamenametoken = new Label(grpPlace, SWT.NONE);
        lblPlaceNamenametoken.setToolTipText("Name of the place.");
        lblPlaceNamenametoken.setText("NameToken:");

        nameToken = new Text(grpPlace, SWT.BORDER);
        nameToken.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnIsCrawlable = new Button(grpPlace, SWT.CHECK);
        btnIsCrawlable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnIsCrawlable.setText("Is crawlable");

        Group grpExtra = new Group(composite_2, SWT.NONE);
        grpExtra.setLayout(new GridLayout(1, false));
        grpExtra.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpExtra.setText("More Options");

        btnCodesplit = new Button(grpExtra, SWT.CHECK);
        btnCodesplit.setText("CodeSplit");

        TabItem tbtmPresenterWidget = new TabItem(tabFolder, SWT.NONE);
        tbtmPresenterWidget.setText("Presenter Widget");

        composite_3 = new Composite(tabFolder, SWT.NONE);
        tbtmPresenterWidget.setControl(composite_3);
        composite_3.setLayout(new GridLayout(1, false));

        grpPresenterWidgetOptions = new Group(composite_3, SWT.NONE);
        grpPresenterWidgetOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpPresenterWidgetOptions.setLayout(new FormLayout());
        grpPresenterWidgetOptions.setText("Presenter Widget Options");

        Button btnIsASingleton = new Button(grpPresenterWidgetOptions, SWT.CHECK);
        FormData fd_btnIsASingleton = new FormData();
        fd_btnIsASingleton.top = new FormAttachment(0, 5);
        fd_btnIsASingleton.left = new FormAttachment(0, 5);
        btnIsASingleton.setLayoutData(fd_btnIsASingleton);
        btnIsASingleton.setText("Is a Singleton");

        tbtmPopupPresenter = new TabItem(tabFolder, SWT.NONE);
        tbtmPopupPresenter.setText("Popup Presenter");

        composite_1 = new Composite(tabFolder, SWT.NONE);
        tbtmPopupPresenter.setControl(composite_1);
        composite_1.setLayout(new GridLayout(1, false));

        grpPopupPresenter = new Group(composite_1, SWT.NONE);
        grpPopupPresenter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpPopupPresenter.setLayout(new FormLayout());
        grpPopupPresenter.setText("Popup Presenter Options");

        Button btnOverrideDefaultPopup = new Button(grpPopupPresenter, SWT.CHECK);
        FormData fd_btnOverrideDefaultPopup = new FormData();
        fd_btnOverrideDefaultPopup.right = new FormAttachment(0, 184);
        fd_btnOverrideDefaultPopup.top = new FormAttachment(0, 5);
        fd_btnOverrideDefaultPopup.left = new FormAttachment(0, 5);
        btnOverrideDefaultPopup.setLayoutData(fd_btnOverrideDefaultPopup);
        btnOverrideDefaultPopup.setText("Override default Popup Panel");

        overridePopupPanel = new Text(grpPopupPresenter, SWT.BORDER);
        FormData fd_overridePopupPanel = new FormData();
        fd_overridePopupPanel.right = new FormAttachment(0, 374);
        fd_overridePopupPanel.top = new FormAttachment(0, 5);
        fd_overridePopupPanel.left = new FormAttachment(0, 190);
        overridePopupPanel.setLayoutData(fd_overridePopupPanel);

        Button btnSelectPanel = new Button(grpPopupPresenter, SWT.NONE);
        FormData fd_btnSelectPanel = new FormData();
        fd_btnSelectPanel.top = new FormAttachment(0, 1);
        fd_btnSelectPanel.left = new FormAttachment(0, 380);
        btnSelectPanel.setLayoutData(fd_btnSelectPanel);
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
        btnSelectPanel.setText("Select Panel");

        Group grpConvenienceOptions = new Group(container, SWT.NONE);
        grpConvenienceOptions.setLayout(new FormLayout());
        GridData gd_grpConvenienceOptions = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_grpConvenienceOptions.heightHint = 170;
        gd_grpConvenienceOptions.widthHint = 559;
        grpConvenienceOptions.setLayoutData(gd_grpConvenienceOptions);
        grpConvenienceOptions.setText("Extra Options");

        btnAddUihandlers = new Button(grpConvenienceOptions, SWT.CHECK);
        FormData fd_btnAddUihandlers = new FormData();
        fd_btnAddUihandlers.top = new FormAttachment(0, 25);
        fd_btnAddUihandlers.left = new FormAttachment(0, 5);
        btnAddUihandlers.setLayoutData(fd_btnAddUihandlers);
        btnAddUihandlers.setText("Add UiHandlers");

        Label lblPresenterLifecycleMethods = new Label(grpConvenienceOptions, SWT.NONE);
        FormData fd_lblPresenterLifecycleMethods = new FormData();
        fd_lblPresenterLifecycleMethods.top = new FormAttachment(0, 61);
        fd_lblPresenterLifecycleMethods.left = new FormAttachment(0, 5);
        lblPresenterLifecycleMethods.setLayoutData(fd_lblPresenterLifecycleMethods);
        lblPresenterLifecycleMethods.setText("Presenter Lifecycle Methods");

        btnAddOnbind = new Button(grpConvenienceOptions, SWT.CHECK);
        FormData fd_btnAddOnbind = new FormData();
        fd_btnAddOnbind.top = new FormAttachment(0, 81);
        fd_btnAddOnbind.left = new FormAttachment(0, 5);
        btnAddOnbind.setLayoutData(fd_btnAddOnbind);
        btnAddOnbind.setText("Add onBind()");

        btnAddOnhide = new Button(grpConvenienceOptions, SWT.CHECK);
        FormData fd_btnAddOnhide = new FormData();
        fd_btnAddOnhide.top = new FormAttachment(0, 105);
        fd_btnAddOnhide.left = new FormAttachment(0, 5);
        btnAddOnhide.setLayoutData(fd_btnAddOnhide);
        btnAddOnhide.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnAddOnhide.setText("Add onHide()");

        btnAddOnreset = new Button(grpConvenienceOptions, SWT.CHECK);
        FormData fd_btnAddOnreset = new FormData();
        fd_btnAddOnreset.top = new FormAttachment(0, 129);
        fd_btnAddOnreset.left = new FormAttachment(0, 5);
        btnAddOnreset.setLayoutData(fd_btnAddOnreset);
        btnAddOnreset.setText("Add onReset()");

        btnAddOnunbind = new Button(grpConvenienceOptions, SWT.CHECK);
        FormData fd_btnAddOnunbind = new FormData();
        fd_btnAddOnunbind.top = new FormAttachment(0, 153);
        fd_btnAddOnunbind.left = new FormAttachment(0, 4);
        btnAddOnunbind.setLayoutData(fd_btnAddOnunbind);
        btnAddOnunbind.setText("Add onUnbind()");

        Label lblEvents = new Label(grpConvenienceOptions, SWT.NONE);
        FormData fd_lblEvents = new FormData();
        fd_lblEvents.right = new FormAttachment(0, 64);
        fd_lblEvents.top = new FormAttachment(0, 5);
        fd_lblEvents.left = new FormAttachment(0, 5);
        lblEvents.setLayoutData(fd_lblEvents);
        lblEvents.setText("Events");

        lblSecurity = new Label(grpConvenienceOptions, SWT.NONE);
        FormData fd_lblSecurity = new FormData();
        fd_lblSecurity.right = new FormAttachment(0, 411);
        fd_lblSecurity.top = new FormAttachment(0, 127);
        fd_lblSecurity.left = new FormAttachment(0, 219);
        lblSecurity.setLayoutData(fd_lblSecurity);
        lblSecurity.setText("Gatekeeper Security");

        gateKeeper = new Text(grpConvenienceOptions, SWT.BORDER);
        FormData fd_gateKeeper = new FormData();
        fd_gateKeeper.right = new FormAttachment(0, 411);
        fd_gateKeeper.top = new FormAttachment(0, 146);
        fd_gateKeeper.left = new FormAttachment(0, 219);
        gateKeeper.setLayoutData(fd_gateKeeper);

        btnSelectGatekeeper = new Button(grpConvenienceOptions, SWT.NONE);
        FormData fd_btnSelectGatekeeper = new FormData();
        fd_btnSelectGatekeeper.right = new FormAttachment(0, 539);
        fd_btnSelectGatekeeper.top = new FormAttachment(0, 143);
        fd_btnSelectGatekeeper.left = new FormAttachment(0, 417);
        btnSelectGatekeeper.setLayoutData(fd_btnSelectGatekeeper);
        btnSelectGatekeeper.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO
            }
        });
        btnSelectGatekeeper.setText("Select Gatekeeper");

        btnUseManualReveal = new Button(grpConvenienceOptions, SWT.CHECK);
        FormData fd_btnUseManualReveal = new FormData();
        fd_btnUseManualReveal.right = new FormAttachment(0, 372);
        fd_btnUseManualReveal.top = new FormAttachment(0, 25);
        fd_btnUseManualReveal.left = new FormAttachment(0, 219);
        btnUseManualReveal.setLayoutData(fd_btnUseManualReveal);
        btnUseManualReveal.setText("Use Manual Reveal");

        Label lblOnReveal = new Label(grpConvenienceOptions, SWT.NONE);
        FormData fd_lblOnReveal = new FormData();
        fd_lblOnReveal.right = new FormAttachment(0, 278);
        fd_lblOnReveal.top = new FormAttachment(0, 5);
        fd_lblOnReveal.left = new FormAttachment(0, 219);
        lblOnReveal.setLayoutData(fd_lblOnReveal);
        lblOnReveal.setText("On Reveal");

        Label lblQuerystring = new Label(grpConvenienceOptions, SWT.NONE);
        FormData fd_lblQuerystring = new FormData();
        fd_lblQuerystring.right = new FormAttachment(0, 296);
        fd_lblQuerystring.top = new FormAttachment(0, 61);
        fd_lblQuerystring.left = new FormAttachment(0, 219);
        lblQuerystring.setLayoutData(fd_lblQuerystring);
        lblQuerystring.setText("Querystring");

        btnPrepareFromRequest = new Button(grpConvenienceOptions, SWT.CHECK);
        FormData fd_btnPrepareFromRequest = new FormData();
        fd_btnPrepareFromRequest.right = new FormAttachment(0, 428);
        fd_btnPrepareFromRequest.top = new FormAttachment(0, 81);
        fd_btnPrepareFromRequest.left = new FormAttachment(0, 219);
        btnPrepareFromRequest.setLayoutData(fd_btnPrepareFromRequest);
        btnPrepareFromRequest.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnPrepareFromRequest.setText("Use Prepare from Request");
        m_bindingContext = initDataBindings();

        observeBindingChanges();
    }

    /**
     * Open url in default external browser
     */
    private void gotoUrl(String surl) {
        try {
            URL url = new URL(surl);
            PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
        } catch (PartInitException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * call only once, observes changes in the ui.
     */
    private void observeBindingChanges() {
        IObservableList bindings = m_bindingContext.getValidationStatusProviders();
        for (Object o : bindings) {
            Binding binding = (Binding) o;

            // Add validator feedback control
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
        }

        // All statuses passed, enable next button.
        setPageComplete(success);
    }

    private void openPackageSelectionDialog() {
        IPackageFragment selectedPackage = selectClientPackage();
        if (selectedPackage != null) {
            packageName.setText(selectedPackage.getElementName());
            presenterConfigModel.setPath(selectedPackage.getElementName());
            presenterConfigModel.setSelectedPackage(selectedPackage);
        }
    }

    /**
     * Selected package originates in the wizard on initialization.
     */
    private void setPackageNameViaWizardSelectedFocus() {
        IPackageFragment selectedPackage = presenterConfigModel.getSelectedPackage();

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

    private void setDefaults() {
        nameToken.setEnabled(false);
        btnIsCrawlable.setEnabled(false);
        name.setFocus();

        // TODO future
        lblSecurity.setVisible(false);
        gateKeeper.setVisible(false);
        btnSelectGatekeeper.setVisible(false);
        
        //TODO future presenters
//        btnPresenterWidget.setEnabled(false);
//        btnPopupPresenter.setEnabled(false);
    }

    private void selectContentSlot() {
        final List<ResolvedSourceField> contentSlots = new ArrayList<ResolvedSourceField>();

        String stringPattern = "ContentSlot";
        int searchFor = IJavaSearchConstants.ANNOTATION_TYPE;
        int limitTo = IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE;
        int matchRule = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;
        SearchPattern searchPattern = SearchPattern.createPattern(stringPattern, searchFor, limitTo, matchRule);

        IJavaProject project = presenterConfigModel.getJavaProject();
        IJavaElement[] elements = new IJavaElement[] { project };
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        SearchRequestor requestor = new SearchRequestor() {
            public void acceptSearchMatch(SearchMatch match) {
                // TODO
                System.out.println(match);

                ResolvedSourceField element = (ResolvedSourceField) match.getElement();
                contentSlots.add(element);
            }
        };

        SearchEngine searchEngine = new SearchEngine();
        SearchParticipant[] particpant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        try {
            searchEngine.search(searchPattern, particpant, scope, requestor, new NullProgressMonitor());
        } catch (CoreException e) {
            // TODO
            e.printStackTrace();
        }

        ResolvedSourceField[] contentListArray = new ResolvedSourceField[contentSlots.size()];
        contentSlots.toArray(contentListArray);

        ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new ILabelProvider() {
            @Override
            public void removeListener(ILabelProviderListener listener) {
            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            @Override
            public void dispose() {
            }

            @Override
            public void addListener(ILabelProviderListener listener) {
            }

            @Override
            public String getText(Object element) {
                ResolvedSourceField rsf = (ResolvedSourceField) element;
                String name = rsf.getElementName();
                IType type = rsf.getDeclaringType();
                return type.getElementName() + "." + name;
            }

            @Override
            public Image getImage(Object element) {
                return null;
            }
        });

        dialog.setElements(contentListArray);
        dialog.setTitle("Which operating system are you using");

        // User pressed cancel
        if (dialog.open() != Window.OK) {
            contentSlot.setText("");
            presenterConfigModel.setContentSlot(null);
            return;
        }

        Object[] result = dialog.getResult();
        if (result == null || result.length < 1) {
            contentSlot.setText("");
        } else {
            ResolvedSourceField rsf = (ResolvedSourceField) result[0];
            presenterConfigModel.setContentSlot(rsf);
            contentSlot.setText(presenterConfigModel.getContentSlotAsString());
        }
    }

    private IType selectPopupWidget() {
        IJavaProject project = presenterConfigModel.getJavaProject();
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

    private IPackageFragment selectClientPackage() {
        IJavaElement selectedPackage = presenterConfigModel.getSelectedPackage();
        if (selectedPackage == null) {
            String message = "This can't drill the available selections. To fix it, close the dialog and then "
                    + "focus on the project by selecting any element with in the java project you want to use this tool for.";
            MessageDialog.openError(getShell(), "Error", message);
            return null;
        }
        IJavaElement ipackage = selectedPackage.getParent();
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { ipackage });

        IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
        int flag = PackageSelectionDialog.F_HIDE_EMPTY_INNER;
        PackageSelectionDialog dialog = new PackageSelectionDialog(getShell(), progressService, flag, scope);
        dialog.setFilter("*client*"); //$NON-NLS-1$
        dialog.setIgnoreCase(false);
        dialog.setMultipleSelection(false);
        int status = dialog.open();

        if (Window.OK == status) {
            Object[] result = dialog.getResult();
            if (result != null && result.length == 1) {
                return (IPackageFragment) result[0];
            }
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
        IObservableValue observeSelectionBtnRevealrootcontenteventObserveWidget = WidgetProperties.selection().observe(btnRevealrootcontentevent);
        IObservableValue revealInRootPresenterConfigModelObserveValue = BeanProperties.value("revealInRoot").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnRevealrootcontenteventObserveWidget, revealInRootPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnRevealrootlayoutcontenteventObserveWidget = WidgetProperties.selection().observe(btnRevealrootlayoutcontentevent);
        IObservableValue revealInRootLayoutPresenterConfigModelObserveValue = BeanProperties.value("revealInRootLayout").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnRevealrootlayoutcontenteventObserveWidget, revealInRootLayoutPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnRevealcontenteventObserveWidget = WidgetProperties.selection().observe(btnRevealcontentevent);
        IObservableValue revealInSlotPresenterConfigModelObserveValue = BeanProperties.value("revealInSlot").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnRevealcontenteventObserveWidget, revealInSlotPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeTextContentSlotObserveWidget = WidgetProperties.text(SWT.Modify).observe(contentSlot);
        IObservableValue contentSlotPresenterConfigModelObserveValue = BeanProperties.value("contentSlot").observe(presenterConfigModel);
        bindingContext.bindValue(observeTextContentSlotObserveWidget, contentSlotPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnIsAPlaceObserveWidget = WidgetProperties.selection().observe(btnIsAPlace);
        IObservableValue placePresenterConfigModelObserveValue = BeanProperties.value("place").observe(presenterConfigModel);
        UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
        strategy_3.setBeforeSetValidator(new PlaceValidator());
        bindingContext.bindValue(observeSelectionBtnIsAPlaceObserveWidget, placePresenterConfigModelObserveValue, strategy_3, null);
        //
        IObservableValue observeTextNameTokenObserveWidget = WidgetProperties.text(SWT.Modify).observe(nameToken);
        IObservableValue nameTokenPresenterConfigModelObserveValue = BeanProperties.value("nameToken").observe(presenterConfigModel);
        UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
        strategy_2.setBeforeSetValidator(new NameTokenValidator(btnIsAPlace));
        bindValueForNameToken = bindingContext.bindValue(observeTextNameTokenObserveWidget, nameTokenPresenterConfigModelObserveValue, strategy_2, null);
        //
        IObservableValue observeSelectionBtnIsCrawlableObserveWidget = WidgetProperties.selection().observe(btnIsCrawlable);
        IObservableValue crawlablePresenterConfigModelObserveValue = BeanProperties.value("crawlable").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnIsCrawlableObserveWidget, crawlablePresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnCodesplitObserveWidget = WidgetProperties.selection().observe(btnCodesplit);
        IObservableValue codeSplitPresenterConfigModelObserveValue = BeanProperties.value("codeSplit").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnCodesplitObserveWidget, codeSplitPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnAddOnhideObserveWidget = WidgetProperties.selection().observe(btnAddOnhide);
        IObservableValue onHidePresenterConfigModelObserveValue = BeanProperties.value("onHide").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddOnhideObserveWidget, onHidePresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnAddOnresetObserveWidget = WidgetProperties.selection().observe(btnAddOnreset);
        IObservableValue onResetPresenterConfigModelObserveValue = BeanProperties.value("onReset").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddOnresetObserveWidget, onResetPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnAddOnunbindObserveWidget = WidgetProperties.selection().observe(btnAddOnunbind);
        IObservableValue onUnbindPresenterConfigModelObserveValue = BeanProperties.value("onUnbind").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddOnunbindObserveWidget, onUnbindPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnUseManualRevealObserveWidget = WidgetProperties.selection().observe(btnUseManualReveal);
        IObservableValue useManualRevealPresenterConfigModelObserveValue = BeanProperties.value("useManualReveal").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnUseManualRevealObserveWidget, useManualRevealPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnPrepareFromRequestObserveWidget = WidgetProperties.selection().observe(btnPrepareFromRequest);
        IObservableValue usePrepareFromRequestPresenterConfigModelObserveValue = BeanProperties.value("usePrepareFromRequest").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnPrepareFromRequestObserveWidget, usePrepareFromRequestPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnAddUihandlersObserveWidget = WidgetProperties.selection().observe(btnAddUihandlers);
        IObservableValue useUiHandlersPresenterConfigModelObserveValue = BeanProperties.value("useUiHandlers").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddUihandlersObserveWidget, useUiHandlersPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnAddOnbindObserveWidget = WidgetProperties.selection().observe(btnAddOnbind);
        IObservableValue onBindPresenterConfigModelObserveValue = BeanProperties.value("onBind").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddOnbindObserveWidget, onBindPresenterConfigModelObserveValue, null, null);
        //
        return bindingContext;
    }
}
