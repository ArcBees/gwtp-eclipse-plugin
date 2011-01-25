package com.imagem.gwtpplugin.wizard;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.Activator;
import com.imagem.gwtpplugin.project.ProjectCreator;

public class ProjectWizard extends Wizard implements INewWizard {

	private ProjectWizardPage page;

	public ProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New GWTP Project");

		try {
			URL url = new URL(Activator.getDefault().getBundle().getEntry("/"), "icons/logo.png");
			setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(url));
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void addPages() {
		page = new ProjectWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		ProjectCreator.createProject(page.getProjectName(), page.getProjectPackage(), page.getProjectLocation());
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {}

}
