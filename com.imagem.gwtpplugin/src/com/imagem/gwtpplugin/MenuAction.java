package com.imagem.gwtpplugin;

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

import com.imagem.gwtpplugin.wizard.ActionWizard;
import com.imagem.gwtpplugin.wizard.EventWizard;
import com.imagem.gwtpplugin.wizard.ModelWizard;
import com.imagem.gwtpplugin.wizard.PresenterWizard;
import com.imagem.gwtpplugin.wizard.ProjectWizard;

public class MenuAction implements IWorkbenchWindowPulldownDelegate {

	private IWorkbenchWindow window;
	private ISelection selection;
	
	@Override
	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("GWTP Project");
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				INewWizard wizard = new ProjectWizard();
				if(selection instanceof IStructuredSelection)
					wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
				else
					wizard.init(window.getWorkbench(), new StructuredSelection());
					
				WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		item = new MenuItem(menu, SWT.SEPARATOR);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Action");
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				INewWizard wizard = new ActionWizard();
				if(selection instanceof IStructuredSelection)
					wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
				else
					wizard.init(window.getWorkbench(), new StructuredSelection());
					
				WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Event");
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				INewWizard wizard = new EventWizard();
				if(selection instanceof IStructuredSelection)
					wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
				else
					wizard.init(window.getWorkbench(), new StructuredSelection());
					
				WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Model");
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				INewWizard wizard = new ModelWizard();
				if(selection instanceof IStructuredSelection)
					wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
				else
					wizard.init(window.getWorkbench(), new StructuredSelection());
					
				WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Presenter");
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				INewWizard wizard = new PresenterWizard();
				if(selection instanceof IStructuredSelection)
					wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
				else
					wizard.init(window.getWorkbench(), new StructuredSelection());
					
				WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		return menu;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
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
