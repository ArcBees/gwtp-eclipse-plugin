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

package com.gwtplatform.plugin.controls;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.gwtplatform.plugin.projectfile.Field;

/**
 *
 * @author Michael Renaud
 *
 */
public class AddFieldDialog extends TitleAreaDialog {

  private Text type;
  private IJavaProject project;
  // private IWizardPage page;
  private Text name;
  private Field value;
  private String title = "";
  private String message = "";
  private String windowTitle = "";

  public AddFieldDialog(Shell parentShell, IJavaProject project,
      IWizardPage page) {
    super(parentShell);

    this.project = project;
    // this.page = page;
    value = new Field();
  }

  protected Control createContents(Composite parent) {
    Control contents = super.createContents(parent);
    super.setTitle(title);
    super.setMessage(message);
    getShell().setText(windowTitle);
    return contents;
  }

  protected Control createDialogArea(Composite parent) {
    // create composite

    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 3;
    layout.verticalSpacing = 5;
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    // Type
    Label label = new Label(container, SWT.NULL);
    label.setText("Type:");

    type = new Text(container, SWT.BORDER | SWT.SINGLE);
    type.setText("java.lang.String");
    type.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    type.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    Button browse = new Button(container, SWT.PUSH);
    browse.setText("Browse...");
    browse.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
    	  IType selectedType = chooseType();
    	  if (type != null) {
    		  type.setText(selectedType.getFullyQualifiedName());
    	  }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
    	  IType selectedType = chooseType();
    	  if (type != null) {
    		  type.setText(selectedType.getFullyQualifiedName());
    	  }
      }
    });

    // Name
    label = new Label(container, SWT.NULL);
    label.setText("Name:");

    name = new Text(container, SWT.BORDER | SWT.SINGLE);
    name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    name.setFocus();
    name.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    label = new Label(container, SWT.NULL);

    setMessage("Enter a name");
    return container;
  }

  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    getButton(IDialogConstants.OK_ID).setEnabled(false);
  }

  protected void dialogChanged() {
    if (type.getText().isEmpty()) {
      setErrorMessage("Enter a type");
      getButton(IDialogConstants.OK_ID).setEnabled(false);
      return;
    }

    String t = type.getText();
    if (t.equals("char") || t.equals("byte") || t.equals("short")
        || t.equals("int") || t.equals("long") || t.equals("float")
        || t.equals("double") || t.equals("boolean")) {
      value.setPrimitiveType(type.getText());
    } else {
      try {
        IType typeInProject = project.findType(t);
        if (typeInProject == null || !typeInProject.exists()) {
          setErrorMessage(this.type.getText() + " doesn't exist");
          getButton(IDialogConstants.OK_ID).setEnabled(false);
          return;
        } else {
          value.setType(typeInProject);
        }
      } catch (JavaModelException e) {
        setErrorMessage("An unexpected error has happened. Close the wizard and retry.");
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        return;
      }
    }

    // TODO Name validation
    if (name.getText().isEmpty()) {
      setErrorMessage("Enter a name");
      getButton(IDialogConstants.OK_ID).setEnabled(false);
      return;
    }
    value.setName(name.getText());

    setErrorMessage(null);
    getButton(OK).setEnabled(true);
  }

  public Field getValue() {
    return value;
  }

  protected IType chooseType() {
    if (project == null) {
      return null;
    }

    IJavaElement[] elements = new IJavaElement[] { project };
    IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

    FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
        getShell(), false, null, scope,
        IJavaSearchConstants.CLASS_AND_INTERFACE);
    dialog.setTitle("Type Selection");
    dialog.setMessage("Select a type for the field");
    dialog.setInitialPattern("String");

    if (dialog.open() == Window.OK) {
      return (IType) dialog.getFirstResult();
    }
    return null;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setWindowTitle(String windowTitle) {
    this.windowTitle = windowTitle;
  }

}
