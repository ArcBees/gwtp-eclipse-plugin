package com.imagem.gwtpplugin.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.src.shared.model.Model;

public class ModelWizard extends Wizard implements INewWizard {

	private ModelWizardPage modelPage;
	private IStructuredSelection selection;
	private IProject project;
	private IPath basePath;
	
	public ModelWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Model");
	}
	
	@Override
	public void addPages() {
		modelPage = new ModelWizardPage(selection);
		addPage(modelPage);
	}
	
	@Override
	public boolean performFinish() {
		String name = modelPage.getModelName();
		String modelPackage = modelPage.getModelPackage();
		String variables[] = modelPage.getVariables();
		
		final Model model = new Model(name, modelPackage);
		model.setVariables(SourceEditor.getVariables(project, basePath, variables));
		
		try {
			SourceEditor.createProjectFile(project, model, true);
		} 
		catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		if(selection != null && !selection.isEmpty()) {
			if (selection.size() > 1)
				return;
			Object firstElement = selection.getFirstElement();
			IResource resource = null;
			if (firstElement instanceof IResource) {
				// Is it a IResource ?
				resource = (IResource) firstElement;
			}
			else if (firstElement instanceof IAdaptable) {
				// Is it a IResource adaptable ?
				IAdaptable adaptable = (IAdaptable) firstElement;
				resource = (IResource) adaptable.getAdapter(IResource.class);
			}
			project = resource.getProject();
			basePath = SourceEditor.getBasePath(resource.getFullPath());
		}
	}
}
