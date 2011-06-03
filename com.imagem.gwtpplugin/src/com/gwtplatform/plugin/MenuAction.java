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

package com.gwtplatform.plugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;

import com.gwtplatform.plugin.wizard.MergeLocalesWizard;
import com.gwtplatform.plugin.wizard.NewActionWizard;
import com.gwtplatform.plugin.wizard.NewEventWizard;
import com.gwtplatform.plugin.wizard.NewModelWizard;
import com.gwtplatform.plugin.wizard.NewPresenterWizard;
import com.gwtplatform.plugin.wizard.NewProjectWizard;

/**
 * The entry point for defining the GWTP pull-down menu.
 *
 * @author Michael Renaud
 */
public class MenuAction implements IWorkbenchWindowPulldownDelegate {

  private IWorkbenchWindow window;
  private ISelection selection;
  private SourceWriterFactory sourceWriterFactory;

  @Override
  public Menu getMenu(Control parent) {
    Menu menu = new Menu(parent);

    MenuItem item = new MenuItem(menu, SWT.PUSH);
    item.setText("GWTP Project");
    item.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        INewWizard wizard = new NewProjectWizard(sourceWriterFactory);
        if (selection instanceof IStructuredSelection) {
          wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
        } else {
          wizard.init(window.getWorkbench(), new StructuredSelection());
        }

        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.create();
        dialog.open();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    item = new MenuItem(menu, SWT.SEPARATOR);

    item = new MenuItem(menu, SWT.PUSH);
    item.setText("Action");
    item.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        INewWizard wizard = new NewActionWizard(sourceWriterFactory);
        if (selection instanceof IStructuredSelection) {
          wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
        } else {
          wizard.init(window.getWorkbench(), new StructuredSelection());
        }

        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.create();
        dialog.open();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    item = new MenuItem(menu, SWT.PUSH);
    item.setText("Event");
    item.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        INewWizard wizard = new NewEventWizard(sourceWriterFactory);
        if (selection instanceof IStructuredSelection) {
          wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
        } else {
          wizard.init(window.getWorkbench(), new StructuredSelection());
        }

        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.create();
        dialog.open();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    item = new MenuItem(menu, SWT.PUSH);
    item.setText("Model");
    item.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        INewWizard wizard = new NewModelWizard(sourceWriterFactory);
        if (selection instanceof IStructuredSelection) {
          wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
        } else {
          wizard.init(window.getWorkbench(), new StructuredSelection());
        }

        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.create();
        dialog.open();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    item = new MenuItem(menu, SWT.PUSH);
    item.setText("Presenter");
    item.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        INewWizard wizard = new NewPresenterWizard(sourceWriterFactory);
        if (selection instanceof IStructuredSelection) {
          wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
        } else {
          wizard.init(window.getWorkbench(), new StructuredSelection());
        }

        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.create();
        dialog.open();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    item = new MenuItem(menu, SWT.SEPARATOR);

    item = new MenuItem(menu, SWT.PUSH);
    item.setText("Merge Locales");
    item.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        INewWizard wizard = new MergeLocalesWizard();
        if (selection instanceof IStructuredSelection) {
          wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
        } else {
          wizard.init(window.getWorkbench(), new StructuredSelection());
        }

        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.create();
        dialog.open();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    return menu;
  }

  @Override
  public void init(IWorkbenchWindow window) {
    this.window = window;
    sourceWriterFactory = new SourceWriterFactory();
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
    this.selection = selection;
  }

  @Override
  public void run(IAction action) {
    // TODO Auto-generated method stub
  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub
  }

}
