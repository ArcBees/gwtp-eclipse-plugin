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

/**
 * All of the UI is generated from Eclipse JFace Editor
 */
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

        Label lblName = new Label(container, SWT.NONE);
        lblName.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        lblName.setText("Name: 'AppHome'");

        name = new Text(container, SWT.BORDER);
        name.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        GridData gd_name = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gd_name.widthHint = 563;
        name.setLayoutData(gd_name);

        Label lblPackage = new Label(container, SWT.NONE);
        lblPackage.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        lblPackage.setText("Package: 'com.arcbees.project.client'");

        Composite composite = new Composite(container, SWT.NONE);
        GridData gd_composite = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
        gd_composite.heightHint = 28;
        gd_composite.widthHint = 571;
        composite.setLayoutData(gd_composite);

        packageName = new Text(composite, SWT.NONE);
        packageName.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        packageName.setBounds(0, 5, 422, 16);

        Button btnSelectPackage = new Button(composite, SWT.NONE);
        btnSelectPackage.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        btnSelectPackage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openPackageSelectionDialog();
            }
        });
        btnSelectPackage.setBounds(438, -1, 120, 28);
        btnSelectPackage.setText("Select Package");

        Group grpPresenterType = new Group(container, SWT.NONE);
        grpPresenterType.setLayout(new FormLayout());
        GridData gd_grpPresenterType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpPresenterType.heightHint = 31;
        gd_grpPresenterType.widthHint = 562;
        grpPresenterType.setLayoutData(gd_grpPresenterType);
        grpPresenterType.setText("Presenter Type");

        btnNestedPresenter = new Button(grpPresenterType, SWT.RADIO);
        btnNestedPresenter.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnNestedPresenter = new FormData();
        fd_btnNestedPresenter.top = new FormAttachment(0, 5);
        fd_btnNestedPresenter.left = new FormAttachment(0, 5);
        btnNestedPresenter.setLayoutData(fd_btnNestedPresenter);
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
        btnNestedPresenter.setText("Nested Presenter");

        btnPresenterWidget = new Button(grpPresenterType, SWT.RADIO);
        btnPresenterWidget.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnPresenterWidget = new FormData();
        fd_btnPresenterWidget.top = new FormAttachment(0, 5);
        fd_btnPresenterWidget.left = new FormAttachment(0, 138);
        btnPresenterWidget.setLayoutData(fd_btnPresenterWidget);
        btnPresenterWidget.setEnabled(false);
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
        btnPresenterWidget.setText("Presenter Widget");

        btnPopupPresenter = new Button(grpPresenterType, SWT.RADIO);
        btnPopupPresenter.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnPopupPresenter = new FormData();
        fd_btnPopupPresenter.top = new FormAttachment(0, 5);
        fd_btnPopupPresenter.left = new FormAttachment(0, 268);
        btnPopupPresenter.setLayoutData(fd_btnPopupPresenter);
        btnPopupPresenter.setEnabled(false);
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
        btnPopupPresenter.setText("Popup Presenter");

        link = new Link(grpPresenterType, SWT.NONE);
        link.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_link = new FormData();
        fd_link.right = new FormAttachment(0, 557);
        fd_link.top = new FormAttachment(0, 7);
        fd_link.left = new FormAttachment(0, 398);
        link.setLayoutData(fd_link);
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String surl = "https://github.com/ArcBees/gwtp-eclipse-plugin/wiki/Presenter-Creation";
                gotoUrl(surl);
            }
        });
        link.setToolTipText("Find more help on presenter creation");
        link.setText("<a>Presenter Creation Help</a>");

        grpNestedPresenterOptions = new Group(container, SWT.NONE);
        grpNestedPresenterOptions.setLayout(new GridLayout(1, false));
        GridData gd_grpNestedPresenterOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpNestedPresenterOptions.heightHint = 165;
        gd_grpNestedPresenterOptions.widthHint = 562;
        grpNestedPresenterOptions.setLayoutData(gd_grpNestedPresenterOptions);
        grpNestedPresenterOptions.setText("Nested Presenter Options");

        Group grpReveal = new Group(grpNestedPresenterOptions, SWT.NONE);
        grpReveal.setText("Reveal In");
        grpReveal.setLayout(new FormLayout());
        GridData gd_grpReveal = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpReveal.heightHint = 31;
        gd_grpReveal.widthHint = 538;
        grpReveal.setLayoutData(gd_grpReveal);

        btnRevealrootcontentevent = new Button(grpReveal, SWT.RADIO);
        btnRevealrootcontentevent.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnRevealrootcontentevent = new FormData();
        fd_btnRevealrootcontentevent.top = new FormAttachment(0, 5);
        fd_btnRevealrootcontentevent.left = new FormAttachment(0, 5);
        btnRevealrootcontentevent.setLayoutData(fd_btnRevealrootcontentevent);
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
        btnRevealrootlayoutcontentevent.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnRevealrootlayoutcontentevent = new FormData();
        fd_btnRevealrootlayoutcontentevent.top = new FormAttachment(0, 5);
        fd_btnRevealrootlayoutcontentevent.left = new FormAttachment(0, 57);
        btnRevealrootlayoutcontentevent.setLayoutData(fd_btnRevealrootlayoutcontentevent);
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
        btnRevealcontentevent.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnRevealcontentevent = new FormData();
        fd_btnRevealcontentevent.top = new FormAttachment(0, 5);
        fd_btnRevealcontentevent.left = new FormAttachment(0, 144);
        btnRevealcontentevent.setLayoutData(fd_btnRevealcontentevent);
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

        btnSelectContentSlot = new Button(grpReveal, SWT.NONE);
        btnSelectContentSlot.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnSelectContentSlot = new FormData();
        fd_btnSelectContentSlot.top = new FormAttachment(0, 1);
        fd_btnSelectContentSlot.left = new FormAttachment(0, 436);
        btnSelectContentSlot.setLayoutData(fd_btnSelectContentSlot);
        btnSelectContentSlot.setEnabled(false);
        btnSelectContentSlot.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectContentSlot();
            }
        });
        btnSelectContentSlot.setText("Select Slot");

        contentSlot = new Text(grpReveal, SWT.BORDER);
        contentSlot.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_contentSlot = new FormData();
        fd_contentSlot.right = new FormAttachment(0, 430);
        fd_contentSlot.top = new FormAttachment(0, 5);
        fd_contentSlot.left = new FormAttachment(0, 193);
        contentSlot.setLayoutData(fd_contentSlot);
        contentSlot.setEnabled(false);

        Composite composite_1 = new Composite(grpNestedPresenterOptions, SWT.NONE);
        composite_1.setLayout(new FormLayout());
        GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_composite_1.widthHint = 551;
        composite_1.setLayoutData(gd_composite_1);

        Group grpPlace = new Group(composite_1, SWT.NONE);
        FormData fd_grpPlace = new FormData();
        fd_grpPlace.bottom = new FormAttachment(0, 70);
        fd_grpPlace.right = new FormAttachment(0, 449);
        fd_grpPlace.top = new FormAttachment(0, 10);
        fd_grpPlace.left = new FormAttachment(0);
        grpPlace.setLayoutData(fd_grpPlace);
        grpPlace.setText("Place");
        grpPlace.setLayout(new FormLayout());

        btnIsAPlace = new Button(grpPlace, SWT.CHECK);
        btnIsAPlace.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnIsAPlace = new FormData();
        fd_btnIsAPlace.top = new FormAttachment(0, 6);
        fd_btnIsAPlace.left = new FormAttachment(0, 5);
        btnIsAPlace.setLayoutData(fd_btnIsAPlace);
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
        lblPlaceNamenametoken.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_lblPlaceNamenametoken = new FormData();
        fd_lblPlaceNamenametoken.top = new FormAttachment(0, 8);
        fd_lblPlaceNamenametoken.left = new FormAttachment(0, 82);
        lblPlaceNamenametoken.setLayoutData(fd_lblPlaceNamenametoken);
        lblPlaceNamenametoken.setToolTipText("Name of the place.");
        lblPlaceNamenametoken.setText("NameToken:");

        nameToken = new Text(grpPlace, SWT.BORDER);
        nameToken.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_nameToken = new FormData();
        fd_nameToken.right = new FormAttachment(0, 321);
        fd_nameToken.top = new FormAttachment(0, 5);
        fd_nameToken.left = new FormAttachment(0, 160);
        nameToken.setLayoutData(fd_nameToken);

        btnIsCrawlable = new Button(grpPlace, SWT.CHECK);
        btnIsCrawlable.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnIsCrawlable = new FormData();
        fd_btnIsCrawlable.top = new FormAttachment(0, 6);
        fd_btnIsCrawlable.left = new FormAttachment(0, 333);
        btnIsCrawlable.setLayoutData(fd_btnIsCrawlable);
        btnIsCrawlable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnIsCrawlable.setText("Is crawlable");

        Group grpExtra = new Group(composite_1, SWT.NONE);
        FormData fd_grpExtra = new FormData();
        fd_grpExtra.bottom = new FormAttachment(0, 71);
        fd_grpExtra.right = new FormAttachment(0, 551);
        fd_grpExtra.top = new FormAttachment(0, 11);
        fd_grpExtra.left = new FormAttachment(0, 455);
        grpExtra.setLayoutData(fd_grpExtra);
        grpExtra.setText("More Options");
        grpExtra.setLayout(new FormLayout());

        btnCodesplit = new Button(grpExtra, SWT.CHECK);
        btnCodesplit.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnCodesplit = new FormData();
        fd_btnCodesplit.top = new FormAttachment(0, 5);
        fd_btnCodesplit.left = new FormAttachment(0, 5);
        btnCodesplit.setLayoutData(fd_btnCodesplit);
        btnCodesplit.setText("CodeSplit");

        grpPopupPresenter = new Group(container, SWT.NONE);
        grpPopupPresenter.setLayout(new FormLayout());
        GridData gd_grpPopupPresenter = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpPopupPresenter.widthHint = 562;
        grpPopupPresenter.setLayoutData(gd_grpPopupPresenter);
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

        grpPresenterWidgetOptions = new Group(container, SWT.NONE);
        grpPresenterWidgetOptions.setLayout(new FormLayout());
        GridData gd_grpPresenterWidgetOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpPresenterWidgetOptions.heightHint = 25;
        gd_grpPresenterWidgetOptions.widthHint = 562;
        grpPresenterWidgetOptions.setLayoutData(gd_grpPresenterWidgetOptions);
        grpPresenterWidgetOptions.setText("Presenter Widget Options");

        Button btnIsASingleton = new Button(grpPresenterWidgetOptions, SWT.CHECK);
        FormData fd_btnIsASingleton = new FormData();
        fd_btnIsASingleton.top = new FormAttachment(0, 5);
        fd_btnIsASingleton.left = new FormAttachment(0, 5);
        btnIsASingleton.setLayoutData(fd_btnIsASingleton);
        btnIsASingleton.setText("Is a Singleton");

        Group grpConvenienceOptions = new Group(container, SWT.NONE);
        grpConvenienceOptions.setLayout(new FormLayout());
        GridData gd_grpConvenienceOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpConvenienceOptions.heightHint = 170;
        gd_grpConvenienceOptions.widthHint = 559;
        grpConvenienceOptions.setLayoutData(gd_grpConvenienceOptions);
        grpConvenienceOptions.setText("Extra Options");

        btnAddUihandlers = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddUihandlers.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnAddUihandlers = new FormData();
        fd_btnAddUihandlers.top = new FormAttachment(0, 25);
        fd_btnAddUihandlers.left = new FormAttachment(0, 5);
        btnAddUihandlers.setLayoutData(fd_btnAddUihandlers);
        btnAddUihandlers.setText("Add UiHandlers");

        Label lblPresenterLifecycleMethods = new Label(grpConvenienceOptions, SWT.NONE);
        lblPresenterLifecycleMethods.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_lblPresenterLifecycleMethods = new FormData();
        fd_lblPresenterLifecycleMethods.top = new FormAttachment(0, 61);
        fd_lblPresenterLifecycleMethods.left = new FormAttachment(0, 5);
        lblPresenterLifecycleMethods.setLayoutData(fd_lblPresenterLifecycleMethods);
        lblPresenterLifecycleMethods.setText("Presenter Lifecycle Methods");

        btnAddOnbind = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddOnbind.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnAddOnbind = new FormData();
        fd_btnAddOnbind.top = new FormAttachment(0, 81);
        fd_btnAddOnbind.left = new FormAttachment(0, 5);
        btnAddOnbind.setLayoutData(fd_btnAddOnbind);
        btnAddOnbind.setText("Add onBind()");

        btnAddOnhide = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddOnhide.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
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
        btnAddOnreset.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnAddOnreset = new FormData();
        fd_btnAddOnreset.top = new FormAttachment(0, 129);
        fd_btnAddOnreset.left = new FormAttachment(0, 5);
        btnAddOnreset.setLayoutData(fd_btnAddOnreset);
        btnAddOnreset.setText("Add onReset()");

        btnAddOnunbind = new Button(grpConvenienceOptions, SWT.CHECK);
        btnAddOnunbind.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnAddOnunbind = new FormData();
        fd_btnAddOnunbind.top = new FormAttachment(0, 153);
        fd_btnAddOnunbind.left = new FormAttachment(0, 4);
        btnAddOnunbind.setLayoutData(fd_btnAddOnunbind);
        btnAddOnunbind.setText("Add onUnbind()");

        Label lblEvents = new Label(grpConvenienceOptions, SWT.NONE);
        lblEvents.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_lblEvents = new FormData();
        fd_lblEvents.right = new FormAttachment(0, 64);
        fd_lblEvents.top = new FormAttachment(0, 5);
        fd_lblEvents.left = new FormAttachment(0, 5);
        lblEvents.setLayoutData(fd_lblEvents);
        lblEvents.setText("Events");

        Label lblSecurity = new Label(grpConvenienceOptions, SWT.NONE);
        FormData fd_lblSecurity = new FormData();
        fd_lblSecurity.right = new FormAttachment(0, 411);
        fd_lblSecurity.top = new FormAttachment(0, 127);
        fd_lblSecurity.left = new FormAttachment(0, 219);
        lblSecurity.setLayoutData(fd_lblSecurity);
        lblSecurity.setVisible(false);
        lblSecurity.setText("Gatekeeper Security");

        gateKeeper = new Text(grpConvenienceOptions, SWT.BORDER);
        FormData fd_gateKeeper = new FormData();
        fd_gateKeeper.right = new FormAttachment(0, 411);
        fd_gateKeeper.top = new FormAttachment(0, 146);
        fd_gateKeeper.left = new FormAttachment(0, 219);
        gateKeeper.setLayoutData(fd_gateKeeper);
        gateKeeper.setVisible(false);

        Button btnSelectGatekeeper = new Button(grpConvenienceOptions, SWT.NONE);
        FormData fd_btnSelectGatekeeper = new FormData();
        fd_btnSelectGatekeeper.right = new FormAttachment(0, 539);
        fd_btnSelectGatekeeper.top = new FormAttachment(0, 143);
        fd_btnSelectGatekeeper.left = new FormAttachment(0, 417);
        btnSelectGatekeeper.setLayoutData(fd_btnSelectGatekeeper);
        btnSelectGatekeeper.setVisible(false);
        btnSelectGatekeeper.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnSelectGatekeeper.setText("Select Gatekeeper");

        btnUseManualReveal = new Button(grpConvenienceOptions, SWT.CHECK);
        btnUseManualReveal.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_btnUseManualReveal = new FormData();
        fd_btnUseManualReveal.right = new FormAttachment(0, 372);
        fd_btnUseManualReveal.top = new FormAttachment(0, 25);
        fd_btnUseManualReveal.left = new FormAttachment(0, 219);
        btnUseManualReveal.setLayoutData(fd_btnUseManualReveal);
        btnUseManualReveal.setText("Use Manual Reveal");

        Label lblOnReveal = new Label(grpConvenienceOptions, SWT.NONE);
        lblOnReveal.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_lblOnReveal = new FormData();
        fd_lblOnReveal.right = new FormAttachment(0, 278);
        fd_lblOnReveal.top = new FormAttachment(0, 5);
        fd_lblOnReveal.left = new FormAttachment(0, 219);
        lblOnReveal.setLayoutData(fd_lblOnReveal);
        lblOnReveal.setText("On Reveal");

        Label lblQuerystring = new Label(grpConvenienceOptions, SWT.NONE);
        lblQuerystring.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
        FormData fd_lblQuerystring = new FormData();
        fd_lblQuerystring.right = new FormAttachment(0, 296);
        fd_lblQuerystring.top = new FormAttachment(0, 61);
        fd_lblQuerystring.left = new FormAttachment(0, 219);
        lblQuerystring.setLayoutData(fd_lblQuerystring);
        lblQuerystring.setText("Querystring");

        btnPrepareFromRequest = new Button(grpConvenienceOptions, SWT.CHECK);
        btnPrepareFromRequest.setFont(SWTResourceManager.getFont("Lucida Grande", 10, SWT.NORMAL));
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
        grpNestedPresenterOptions.setVisible(true);
        grpPopupPresenter.setVisible(false);
        grpPresenterWidgetOptions.setVisible(false);
        nameToken.setEnabled(false);
        btnIsCrawlable.setEnabled(false);
        name.setFocus();
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
        IObservableValue bytesPresenterConfigModelgetPathObserveValue = PojoProperties.value("bytes").observe(
                presenterConfigModel.getPath());
        UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
        strategy_1.setBeforeSetValidator(new PackageNameValidator());
        bindingContext.bindValue(observeTextPackageNameObserveWidget, bytesPresenterConfigModelgetPathObserveValue,
                strategy_1, null);
        //
        IObservableValue observeSelectionBtnNestedPresenterObserveWidget = WidgetProperties.selection().observe(
                btnNestedPresenter);
        IObservableValue nestedPresenterPresenterConfigModelObserveValue = BeanProperties.value("nestedPresenter")
                .observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnNestedPresenterObserveWidget,
                nestedPresenterPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnPresenterWidgetObserveWidget = WidgetProperties.selection().observe(
                btnPresenterWidget);
        IObservableValue presenterWidgetPresenterConfigModelObserveValue = BeanProperties.value("presenterWidget")
                .observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnPresenterWidgetObserveWidget,
                presenterWidgetPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnPopupPresenterObserveWidget = WidgetProperties.selection().observe(
                btnPopupPresenter);
        IObservableValue popupPresenterPresenterConfigModelObserveValue = BeanProperties.value("popupPresenter")
                .observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnPopupPresenterObserveWidget,
                popupPresenterPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeTextNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(name);
        IObservableValue namePresenterConfigModelObserveValue = BeanProperties.value("name").observe(
                presenterConfigModel);
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new PackageNameValidator());
        bindingContext.bindValue(observeTextNameObserveWidget, namePresenterConfigModelObserveValue, strategy, null);
        //
        IObservableValue observeSelectionBtnRevealrootcontenteventObserveWidget = WidgetProperties.selection().observe(
                btnRevealrootcontentevent);
        IObservableValue revealInRootPresenterConfigModelObserveValue = BeanProperties.value("revealInRoot").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnRevealrootcontenteventObserveWidget,
                revealInRootPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnRevealrootlayoutcontenteventObserveWidget = WidgetProperties.selection()
                .observe(btnRevealrootlayoutcontentevent);
        IObservableValue revealInRootLayoutPresenterConfigModelObserveValue = BeanProperties
                .value("revealInRootLayout").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnRevealrootlayoutcontenteventObserveWidget,
                revealInRootLayoutPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnRevealcontenteventObserveWidget = WidgetProperties.selection().observe(
                btnRevealcontentevent);
        IObservableValue revealInSlotPresenterConfigModelObserveValue = BeanProperties.value("revealInSlot").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnRevealcontenteventObserveWidget,
                revealInSlotPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeTextContentSlotObserveWidget = WidgetProperties.text(SWT.Modify).observe(contentSlot);
        IObservableValue contentSlotPresenterConfigModelObserveValue = BeanProperties.value("contentSlot").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeTextContentSlotObserveWidget, contentSlotPresenterConfigModelObserveValue,
                null, null);
        //
        IObservableValue observeSelectionBtnIsAPlaceObserveWidget = WidgetProperties.selection().observe(btnIsAPlace);
        IObservableValue placePresenterConfigModelObserveValue = BeanProperties.value("place").observe(
                presenterConfigModel);
        UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
        strategy_3.setBeforeSetValidator(new PlaceValidator());
        bindingContext.bindValue(observeSelectionBtnIsAPlaceObserveWidget, placePresenterConfigModelObserveValue,
                strategy_3, null);
        //
        IObservableValue observeTextNameTokenObserveWidget = WidgetProperties.text(SWT.Modify).observe(nameToken);
        IObservableValue nameTokenPresenterConfigModelObserveValue = BeanProperties.value("nameToken").observe(
                presenterConfigModel);
        UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
        strategy_2.setBeforeSetValidator(new NameTokenValidator(btnIsAPlace));
        bindValueForNameToken = bindingContext.bindValue(observeTextNameTokenObserveWidget,
                nameTokenPresenterConfigModelObserveValue, strategy_2, null);
        //
        IObservableValue observeSelectionBtnIsCrawlableObserveWidget = WidgetProperties.selection().observe(
                btnIsCrawlable);
        IObservableValue crawlablePresenterConfigModelObserveValue = BeanProperties.value("crawlable").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnIsCrawlableObserveWidget,
                crawlablePresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnCodesplitObserveWidget = WidgetProperties.selection().observe(btnCodesplit);
        IObservableValue codeSplitPresenterConfigModelObserveValue = BeanProperties.value("codeSplit").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnCodesplitObserveWidget, codeSplitPresenterConfigModelObserveValue,
                null, null);
        //
        IObservableValue observeSelectionBtnAddOnbindObserveWidget = WidgetProperties.selection().observe(btnAddOnbind);
        IObservableValue javaProjectPresenterConfigModelObserveValue = BeanProperties.value("javaProject").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddOnbindObserveWidget,
                javaProjectPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnAddOnhideObserveWidget = WidgetProperties.selection().observe(btnAddOnhide);
        IObservableValue onHidePresenterConfigModelObserveValue = BeanProperties.value("onHide").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddOnhideObserveWidget, onHidePresenterConfigModelObserveValue,
                null, null);
        //
        IObservableValue observeSelectionBtnAddOnresetObserveWidget = WidgetProperties.selection().observe(
                btnAddOnreset);
        IObservableValue onResetPresenterConfigModelObserveValue = BeanProperties.value("onReset").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddOnresetObserveWidget, onResetPresenterConfigModelObserveValue,
                null, null);
        //
        IObservableValue observeSelectionBtnAddOnunbindObserveWidget = WidgetProperties.selection().observe(
                btnAddOnunbind);
        IObservableValue onUnbindPresenterConfigModelObserveValue = BeanProperties.value("onUnbind").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddOnunbindObserveWidget, onUnbindPresenterConfigModelObserveValue,
                null, null);
        //
        IObservableValue observeSelectionBtnUseManualRevealObserveWidget = WidgetProperties.selection().observe(
                btnUseManualReveal);
        IObservableValue useManualRevealPresenterConfigModelObserveValue = BeanProperties.value("useManualReveal")
                .observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnUseManualRevealObserveWidget,
                useManualRevealPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnPrepareFromRequestObserveWidget = WidgetProperties.selection().observe(
                btnPrepareFromRequest);
        IObservableValue usePrepareFromRequestPresenterConfigModelObserveValue = BeanProperties.value(
                "usePrepareFromRequest").observe(presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnPrepareFromRequestObserveWidget,
                usePrepareFromRequestPresenterConfigModelObserveValue, null, null);
        //
        IObservableValue observeSelectionBtnAddUihandlersObserveWidget = WidgetProperties.selection().observe(
                btnAddUihandlers);
        IObservableValue useUiHandlersPresenterConfigModelObserveValue = BeanProperties.value("useUiHandlers").observe(
                presenterConfigModel);
        bindingContext.bindValue(observeSelectionBtnAddUihandlersObserveWidget,
                useUiHandlersPresenterConfigModelObserveValue, null, null);
        //
        return bindingContext;
    }
}
