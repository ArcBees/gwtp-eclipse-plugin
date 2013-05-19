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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CreatePresenterPage extends WizardPage {
    private PresenterConfigModel presenterConfigModel;
    private Text text;
    private Text gateKeeper;
    private Text nameToken;
    private Text overridePopupPanel;
    private Text text_1;

    public CreatePresenterPage(PresenterConfigModel presenterConfigModel) {
        super("wizardPageCreatePresenter");
        
        this.presenterConfigModel = presenterConfigModel;
        
        setTitle("Create Presenter");
        setDescription("Create a presenter for the proejct.");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));
        
        Label lblName = new Label(container, SWT.NONE);
        lblName.setText("Name: 'AppHome'");
        
        text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        Group grpPresenterType = new Group(container, SWT.NONE);
        grpPresenterType.setLayout(null);
        GridData gd_grpPresenterType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpPresenterType.heightHint = 31;
        gd_grpPresenterType.widthHint = 562;
        grpPresenterType.setLayoutData(gd_grpPresenterType);
        grpPresenterType.setText("Presenter Type");
        
        Button btnNestedPresenter = new Button(grpPresenterType, SWT.RADIO);
        btnNestedPresenter.setSelection(true);
        btnNestedPresenter.setBounds(10, 10, 113, 18);
        btnNestedPresenter.setText("Nested Presenter");
        
        Button btnPresenterWidget = new Button(grpPresenterType, SWT.RADIO);
        btnPresenterWidget.setBounds(143, 10, 112, 18);
        btnPresenterWidget.setText("Presenter Widget");
        
        Button btnPopupPresenter = new Button(grpPresenterType, SWT.RADIO);
        btnPopupPresenter.setBounds(273, 10, 109, 18);
        btnPopupPresenter.setText("Popup Presenter");
        
        Group grpNestedPresenterOptions = new Group(container, SWT.NONE);
        grpNestedPresenterOptions.setLayout(null);
        GridData gd_grpNestedPresenterOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_grpNestedPresenterOptions.heightHint = 107;
        gd_grpNestedPresenterOptions.widthHint = 562;
        grpNestedPresenterOptions.setLayoutData(gd_grpNestedPresenterOptions);
        grpNestedPresenterOptions.setText("Nested Presenter Options");
        
        Button btnIsAPlace = new Button(grpNestedPresenterOptions, SWT.CHECK);
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
        
        Button btnRevealcontentevent = new Button(grpNestedPresenterOptions, SWT.RADIO);
        btnRevealcontentevent.setBounds(10, 10, 136, 18);
        btnRevealcontentevent.setText("RevealContentEvent");
        
        Button btnRevealrootcontentevent = new Button(grpNestedPresenterOptions, SWT.RADIO);
        btnRevealrootcontentevent.setBounds(164, 10, 162, 18);
        btnRevealrootcontentevent.setText("RevealRootContentEvent");
        
        Button btnRevealrootlayoutcontentevent = new Button(grpNestedPresenterOptions, SWT.RADIO);
        btnRevealrootlayoutcontentevent.setBounds(332, 10, 200, 18);
        btnRevealrootlayoutcontentevent.setText("RevealRootLayoutContentEvent");
        
        text_1 = new Text(grpNestedPresenterOptions, SWT.BORDER);
        text_1.setBounds(10, 34, 284, 19);
        
        Button btnSelectContentSlot = new Button(grpNestedPresenterOptions, SWT.NONE);
        btnSelectContentSlot.setBounds(300, 30, 152, 28);
        btnSelectContentSlot.setText("Select Content Slot");
        
        Button btnIsCrawlable = new Button(grpNestedPresenterOptions, SWT.CHECK);
        btnIsCrawlable.setBounds(439, 68, 93, 18);
        btnIsCrawlable.setText("Is crawlable");
        
        Group grpPopupPresenter = new Group(container, SWT.NONE);
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
        btnSelectPanel.setBounds(385, 6, 95, 28);
        btnSelectPanel.setText("Select Panel");
        
        Group grpPresenterWidgetOptions = new Group(container, SWT.NONE);
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
    }
}
