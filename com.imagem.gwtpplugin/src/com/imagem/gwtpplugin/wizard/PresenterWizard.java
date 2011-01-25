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
import com.imagem.gwtpplugin.projectfile.src.client.core.Presenter;
import com.imagem.gwtpplugin.projectfile.src.client.core.Ui;
import com.imagem.gwtpplugin.projectfile.src.client.core.View;
import com.imagem.gwtpplugin.projectfile.src.client.gin.ClientModule;
import com.imagem.gwtpplugin.projectfile.src.client.gin.Ginjector;
import com.imagem.gwtpplugin.projectfile.src.client.place.Tokens;

public class PresenterWizard extends Wizard implements INewWizard {

	private PresenterWizardPage presenterPage;
	private IStructuredSelection selection;
	private IProject project;
	private IPath basePath;

	public PresenterWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Presenter");
	}
	
	@Override
	public void addPages() {
		presenterPage = new PresenterWizardPage(selection);
		addPage(presenterPage);
	}

	@Override
	public boolean performFinish() {
		String name = presenterPage.getPresenterName();
		String presenterPackage = presenterPage.getPresenterPackage();
		String viewPackage = presenterPackage.replaceAll("presenter", "view");
		String clientPackage = SourceEditor.toPackage(basePath.append("client"));
		String ginPackage = SourceEditor.toPackage(basePath.append("client").append("gin"));
		String placePackage = SourceEditor.toPackage(basePath.append("client").append("place"));
		String resourcePackage = SourceEditor.toPackage(basePath.append("client").append("resource"));
		
		final Presenter presenter = new Presenter(project.getName(), name, presenterPackage, placePackage);
		final View view = new View(name, viewPackage, presenterPackage, resourcePackage);
		final Ui ui = new Ui(name, viewPackage);
		final Tokens tokens = new Tokens(project.getName(), placePackage);
		final ClientModule clientModule = new ClientModule(project.getName(), ginPackage, placePackage, resourcePackage, clientPackage);
		final Ginjector ginjector = new Ginjector(project.getName(), ginPackage, resourcePackage, clientPackage);
		
		presenter.setCodeSplit(presenterPage.isCodeSplit());
		presenter.setPlace(presenterPage.isPlace());
		presenter.setToken(presenterPage.getToken());
		presenter.setWidget(presenterPage.isWidget());
		presenter.setGatekeeper(presenterPage.getGateKeeper());
		presenter.setOnMethods(presenterPage.onMethods());
		
		view.setUiBinder(presenterPage.useUiBinder());
		
		tokens.setToken(presenterPage.getToken());
		
		clientModule.setPresenter(presenter);
		clientModule.setView(view);
		
		ginjector.setPresenter(presenter);
		
		try {
			SourceEditor.createProjectFile(project, presenter, true);
			SourceEditor.createProjectFile(project, view, true);
			if(presenterPage.useUiBinder())
				SourceEditor.createProjectFile(project, ui, true);
			if(!presenterPage.isWidget() && presenterPage.isPlace())
				SourceEditor.updateProjectFile(project, tokens);
			SourceEditor.updateProjectFile(project, clientModule);
			SourceEditor.updateProjectFile(project, ginjector);
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
