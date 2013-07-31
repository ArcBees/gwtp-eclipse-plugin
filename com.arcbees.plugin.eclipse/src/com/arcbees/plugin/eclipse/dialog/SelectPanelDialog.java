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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import com.arcbees.plugin.eclipse.domain.SelectedPanel;
import com.arcbees.plugin.eclipse.util.ProgressTaskMonitor;

public class SelectPanelDialog extends Dialog {
    private PresenterConfigModel presenterConfigModel;
    private ProgressBar progressBar;
    private ProgressTaskMonitor fetchMonitor;
    private boolean loading = false;
    
    private Text filterPresenters;
    private Text filterViews;
    
    private Job jobMonitoring;
    private Job jobFindPresenters;
    private Job jobFindViews;
    
    private List listOfPresenters;
    private List listOfViews;
    private List listOfPanels;
    
    private ArrayList<SourceType> listElementsTypePresenters;
    private ArrayList<SourceType> listElementsTypeViews;
    private int selectedIndexPresenter;
    private int selectedIndexView;
    private int selectedIndexPanel;
    private SelectedPanel selectedParentPanelModel;
    
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

        CLabel lblSelectAPresenter = new CLabel(container, SWT.NONE);
        lblSelectAPresenter.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        lblSelectAPresenter.setText("Select a Presenter and a View's panel.");

        CLabel lblSelectPresenter = new CLabel(container, SWT.NONE);
        lblSelectPresenter.setText("Select Presenter");

        filterPresenters = new Text(container, SWT.BORDER);
        filterPresenters.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                findPresenters(filterPresenters.getText().trim());
            }
        });
        filterPresenters.setText("*Presenter");
        filterPresenters.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        findPresenters(filterPresenters.getText().trim());

        listOfPresenters = new List(container, SWT.BORDER);
        listOfPresenters.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedIndexPresenter = listOfPresenters.getSelectionIndex();

                findViews(filterViews.getText().trim());
            }
        });
        GridData gd_listOfPresenters = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_listOfPresenters.heightHint = 118;
        listOfPresenters.setLayoutData(gd_listOfPresenters);

        CLabel lblSelectAPanel = new CLabel(container, SWT.NONE);
        lblSelectAPanel.setText("Select the Presenter's View:");

        filterViews = new Text(container, SWT.BORDER);
        filterViews.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                findViews(filterViews.getText().trim());
            }
        });
        filterViews.setText("*View");
        filterViews.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        listOfViews = new List(container, SWT.BORDER);
        listOfViews.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedIndexView = listOfPresenters.getSelectionIndex();
                
                findPanels();
            }
        });
        GridData gd_listOfViews = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_listOfViews.heightHint = 127;
        listOfViews.setLayoutData(gd_listOfViews);

        CLabel lblSelectAPanel_1 = new CLabel(container, SWT.NONE);
        lblSelectAPanel_1.setText("Select a Panel from the View:");

        listOfPanels = new List(container, SWT.BORDER);
        listOfPanels.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedIndexPanel = listOfPanels.getSelectionIndex();
                
                canFinishSelection();
            }
        });
        GridData gd_listOfPanels = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_listOfPanels.heightHint = 82;
        listOfPanels.setLayoutData(gd_listOfPanels);

        progressBar = new ProgressBar(container, SWT.NONE);
        GridData gd_progressBar = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_progressBar.heightHint = 21;
        progressBar.setLayoutData(gd_progressBar);
        fetchMonitor = new ProgressTaskMonitor(progressBar);

        return container;
    }

    private void canFinishSelection() {
        selectedParentPanelModel = new SelectedPanel();
        selectedParentPanelModel.setPresenterSourceType(listElementsTypePresenters.get(selectedIndexPresenter));
        selectedParentPanelModel.setViewSourceType(listElementsTypeViews.get(selectedIndexView));
        selectedParentPanelModel.setSelectedIndexPanel(selectedIndexPanel);
        
        // TODO enable OK
    }
    
    public SelectedPanel getSelectedParentPanelModel() {
        return selectedParentPanelModel;
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        button.setGrayed(true);
        button.setSelection(true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 629);
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

        listElementsTypePresenters = new ArrayList<SourceType>();
        final ArrayList<String> listElementsString = new ArrayList<String>();
        SearchRequestor requestor = new SearchRequestor() {
            public void acceptSearchMatch(SearchMatch match) {
                if (match.getElement() instanceof SourceType) {
                    SourceType type = (SourceType) match.getElement();
                    listElementsTypePresenters.add(type);
                    listElementsString.add(type.getElementName());
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

        final String[] listOfElementsStringArray = new String[listElementsTypePresenters.size()];
        listElementsString.toArray(listOfElementsStringArray);

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                loading = false;
                listOfPresenters.setItems(listOfElementsStringArray);
                jobMonitoring.cancel();
            }
        });
    }

    private void findViews(final String filterPattern) {
        if (jobFindViews != null && jobFindViews.getState() != Job.NONE) {
            jobFindViews.cancel();
            loading = false;
        }

        runMonitor();

        jobFindViews = new Job("Find Views") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                findViewsProcess(filterPattern);
                loading = false;
                return Status.OK_STATUS;
            }
        };
        jobFindViews.schedule();
    }

    private void findViewsProcess(String filterPattern) {
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

        listElementsTypeViews = new ArrayList<SourceType>();
        final ArrayList<String> listElementsViewsString = new ArrayList<String>();
        SearchRequestor requestor = new SearchRequestor() {
            public void acceptSearchMatch(SearchMatch match) {
                if (match.getElement() instanceof SourceType) {
                    SourceType type = (SourceType) match.getElement();
                    listElementsTypeViews.add(type);
                    listElementsViewsString.add(type.getElementName());
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

        final String[] listOfElementsStringArray = new String[listElementsTypeViews.size()];
        listElementsViewsString.toArray(listOfElementsStringArray);

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                loading = false;
                if (listOfElementsStringArray.length > 0) {
                    listOfViews.setItems(listOfElementsStringArray);
                } else {
                    listOfViews.removeAll();
                }
                jobMonitoring.cancel();
            }
        });
    }
    
    private void findPanels() {
        if (jobFindViews != null && jobFindViews.getState() != Job.NONE) {
            jobFindViews.cancel();
            loading = false;
        }

        runMonitor();

        jobFindViews = new Job("Find Panels") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                findPanelsProcess();
                loading = false;
                return Status.OK_STATUS;
            }
        };
        jobFindViews.schedule();
    }
    
    private void findPanelsProcess() {
        SourceType sourceTypeForView = listElementsTypeViews.get(selectedIndexView);
        
        IField[] fields = null;
        try {
            fields = sourceTypeForView.getFields();
        } catch (JavaModelException e) {
            // TODO
            e.printStackTrace();
            return;
        }

        // TODO deal with only types that are of widget
        // TODO display zero found
        final String[] fieldStrings = new String[fields.length];
        for (int i=0; i < fields.length; i++) {
            String s = "";
            s += fields[i].getDeclaringType().getElementName() + " ";
            s += fields[i].getElementName() + ";";
            fieldStrings[i] = s;
        }
        
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                loading = false;
                listOfPanels.setItems(fieldStrings);
                jobMonitoring.cancel();
            }
        });
    }

    /**
     * Make the progress bar move.
     */
    private void runMonitor() {
        if (jobMonitoring != null && jobMonitoring.getState() != Job.NONE) {
            jobMonitoring.cancel();
            loading = false;
        }

        jobMonitoring = new Job("Fetching Presenters...") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                final String doing = "Fetching Archetyes...";

                monitor.beginTask(doing, 100);

                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        fetchMonitor.beginTask(doing, 100);
                    }
                });

                loading = true;
                do {
                    try {
                        TimeUnit.MILLISECONDS.sleep(25);

                        monitor.worked(1);

                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                fetchMonitor.worked(1);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        loading = false;
                        return Status.CANCEL_STATUS;
                    }
                } while (loading);

                if (!loading) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            fetchMonitor.reset();
                        }
                    });
                }

                return Status.OK_STATUS;
            }
        };
        jobMonitoring.schedule();
    }
}
