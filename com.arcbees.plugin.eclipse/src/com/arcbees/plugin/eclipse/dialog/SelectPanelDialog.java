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

package com.arcbees.plugin.eclipse.dialog;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import com.arcbees.plugin.eclipse.util.ProgressTaskMonitor;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SelectPanelDialog extends Dialog {
    private Text filter;
    private PresenterConfigModel presenterConfigModel;
    private List listOfPresenters;
    private ArrayList<SourceType> contentSlots;
    private Job jobFindPresenters;
    private ProgressBar progressBar;
    private ProgressTaskMonitor fetchMonitor;
    private boolean loading = false;
    private Job jobMonitoring;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     * @param presenterConfigModel
     */
    public SelectPanelDialog(Shell parentShell, PresenterConfigModel presenterConfigModel) {
        super(parentShell);
        this.presenterConfigModel = presenterConfigModel;
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        CLabel lblFilterSelection = new CLabel(container, SWT.NONE);
        lblFilterSelection.setText("Filter Presenters");

        filter = new Text(container, SWT.BORDER);
        filter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                findPresenters(filter.getText().trim());
            }
        });
        filter.setText("*Presenter");
        filter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        CLabel lblSelectPresenter = new CLabel(container, SWT.NONE);
        lblSelectPresenter.setText("Select Presenter");

        listOfPresenters = new List(container, SWT.BORDER);
        listOfPresenters.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO get panels
            }
        });
        GridData gd_listOfPresenters = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_listOfPresenters.heightHint = 152;
        listOfPresenters.setLayoutData(gd_listOfPresenters);
        
        CLabel lblSelectAPanel = new CLabel(container, SWT.NONE);
        lblSelectAPanel.setText("Select a Panel");
        
        List listOfPanels = new List(container, SWT.BORDER);
        GridData gd_listOfPanels = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_listOfPanels.heightHint = 82;
        listOfPanels.setLayoutData(gd_listOfPanels);
        
        progressBar = new ProgressBar(container, SWT.NONE);
        GridData gd_progressBar = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_progressBar.heightHint = 21;
        progressBar.setLayoutData(gd_progressBar);
        fetchMonitor = new ProgressTaskMonitor(progressBar);
        
        findPresenters(filter.getText().trim());

        return container;
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 458);
    }

    private void findPresenters(final String filterPattern) {
        if (jobFindPresenters != null && jobFindPresenters.getState() != Job.NONE) {
            jobFindPresenters.cancel();
            loading = false;
        }

        runMonitor();
        
        jobFindPresenters = new Job("Find Presenters") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                findPresentersProcess(filterPattern);
                loading = false;
                return Status.OK_STATUS;
            }
        };
        jobFindPresenters.schedule();
    }

    private void findPresentersProcess(String filterPattern) {
        if (filterPattern.length() == 0) {
            filterPattern = "*";
        }

        int searchFor = IJavaSearchConstants.TYPE;
        int limitTo = IJavaSearchConstants.DECLARATIONS;
        int matchRule = SearchPattern.R_PATTERN_MATCH;
        SearchPattern searchPattern = SearchPattern.createPattern(filterPattern, searchFor, limitTo, matchRule);

        IJavaProject project = presenterConfigModel.getJavaProject();
        IJavaElement[] elements = new IJavaElement[] { project };
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        contentSlots = new ArrayList<SourceType>();
        final ArrayList<String> contentSlotsString = new ArrayList<String>();
        SearchRequestor requestor = new SearchRequestor() {
            public void acceptSearchMatch(SearchMatch match) {
                if (match.getElement() instanceof SourceType) {
                    SourceType type = (SourceType) match.getElement();
                    contentSlots.add(type);
                    contentSlotsString.add(type.getElementName());
                }
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

        final String[] contentSlotsStringArray = new String[contentSlots.size()];
        contentSlotsString.toArray(contentSlotsStringArray);

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                loading = false;
                listOfPresenters.setItems(contentSlotsStringArray);
                jobMonitoring.cancel();
            }
        });
    }
    
    private void runMonitor() {
        if (jobMonitoring != null && jobMonitoring.getState() != Job.NONE) {
            jobMonitoring.cancel();
            loading = false;
        }
        
        jobMonitoring = new Job("Fetching Presenters...") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                String doing = "Fetching Archetyes...";

                monitor.beginTask(doing, 100);
                fetchMonitor.beginTask(doing, 100);

                loading = true;
                do {
                    try {
                        TimeUnit.MILLISECONDS.sleep(25);

                        monitor.worked(1);
                        fetchMonitor.worked(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        loading = false;
                        return Status.CANCEL_STATUS;
                    }
                } while (loading);

                if (!loading) {
                    fetchMonitor.reset();
                }

                return Status.OK_STATUS;
            }
        };
        jobMonitoring.schedule();
    }
}
