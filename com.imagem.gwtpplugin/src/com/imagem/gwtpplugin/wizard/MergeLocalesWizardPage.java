/**
 * Copyright 2011 IMAGEM Solutions TI santé
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imagem.gwtpplugin.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * @author Michael Renaud
 */
public class MergeLocalesWizardPage extends WizardPage {

  private IProject project;
  private IContainer initialSelection;
  private Text extraDir;
  private IContainer selection;

  protected MergeLocalesWizardPage(IStructuredSelection selection) {
    super("Merge Locales");
    setTitle("Merge Locales");
    setDescription("Merge Locales");

    init(selection);
  }

  private void init(IStructuredSelection selection) {
    if (selection.getFirstElement() instanceof IAdaptable) {
      IAdaptable adaptable = (IAdaptable) selection.getFirstElement();
      IResource resource = (IResource) adaptable.getAdapter(IResource.class);
      if (resource != null) {
        project = resource.getProject();
      }
      IContainer container = (IContainer) resource.getAdapter(IContainer.class);
      if (container == null) {
        container = resource.getParent();
      }
      IFolder folder = project.getFolder(container.getFullPath());
      initialSelection = (IContainer) folder.getAdapter(IContainer.class);
      this.selection = container;
    }
  }

  @Override
  public void createControl(Composite parent) {
    initializeDialogUnits(parent);

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());

    int nColumns = 3;

    GridLayout layout = new GridLayout();
    layout.numColumns = nColumns;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    GridData gd = new GridData();
    gd.horizontalAlignment = GridData.FILL;
    gd.horizontalSpan = nColumns - 2;

    Label label = new Label(composite, SWT.NULL);
    label.setText("Extras directory:");

    extraDir = new Text(composite, SWT.BORDER | SWT.SINGLE);
    extraDir.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Button browse = new Button(composite, SWT.PUSH);
    browse.setText("Browse...");
    browse.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        extraDir.setText(chooseExtraDir().getProjectRelativePath().toOSString());
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        extraDir.setText(chooseExtraDir().getProjectRelativePath().toOSString());
      }
    });

    extraDir.setText(initialSelection.getProjectRelativePath().toOSString());
    setControl(composite);
  }

  protected IContainer chooseExtraDir() {
    ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), initialSelection,
        false, "Message");
    dialog.open();

    IPath path = (IPath) dialog.getResult()[0];
    IFolder folder = project.getFolder(path);
    selection = (IContainer) folder.getAdapter(IContainer.class);

    return selection;
  }

  public IContainer getExtraDir() {
    return selection;
  }

  public IContainer getResourcesDir() {
    return (IContainer) project.findMember("src/com/google/gwt/i18n/client/").getAdapter(
        IContainer.class);
  }

  public IProject getProject() {
    return project;
  }

}
