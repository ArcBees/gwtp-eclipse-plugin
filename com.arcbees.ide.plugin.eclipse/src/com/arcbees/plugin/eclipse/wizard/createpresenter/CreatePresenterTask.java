package com.arcbees.plugin.eclipse.wizard.createpresenter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import com.arcbees.plugin.template.create.presenter.CreateNestedPresenter;
import com.arcbees.plugin.template.domain.presenter.CreatedNestedPresenter;
import com.arcbees.plugin.template.domain.presenter.NestedPresenterOptions;
import com.arcbees.plugin.template.domain.presenter.PresenterOptions;
import com.arcbees.plugin.template.domain.presenter.RenderedTemplate;

public class CreatePresenterTask {
    public static CreatePresenterTask run(PresenterConfigModel presenterConfigModel, IProgressMonitor progressMonitor) {
        CreatePresenterTask createPresenterTask = new CreatePresenterTask(presenterConfigModel, progressMonitor);
        createPresenterTask.run();
        return createPresenterTask;
    }

    private PresenterConfigModel presenterConfigModel;
    private IProgressMonitor progressMonitor;
    private CreatedNestedPresenter createdNestedPresenter;

    private CreatePresenterTask(PresenterConfigModel presenterConfigModel, IProgressMonitor progressMonitor) {
        this.presenterConfigModel = presenterConfigModel;
        this.progressMonitor = progressMonitor;
    }

    private void run() {

        fetchTemplates();
        
        createPresenterPackage();
        createPresenterModule();
        // TODO module link in parent gin
        // TODO presenter
        // TODO presneter into parent slot
        // TODO uihandlers
        // TODO view
        // TODO viewUi binder
        // TODO NameTokens file
        // TODO NameTokens field and method
        
        // TODO
        System.out.println("finished");
    }

    private void fetchTemplates() {
        PresenterOptions presenterOptions = new PresenterOptions();
        presenterOptions.setPackageName(getPackageName());
        presenterOptions.setName(presenterConfigModel.getName());
        // TODO more options...

        if (presenterConfigModel.getNestedPresenter()) {
            fetchNestedTemplate(presenterOptions);
        } else if (presenterConfigModel.getPresenterWidget()) {
            // TODO
        } else if (presenterConfigModel.getPopupPresenter()) {
            // TODO
        }
    }
    
    private void fetchNestedTemplate(PresenterOptions presenterOptions) {
        // TODO translate options
        NestedPresenterOptions nestedPresenterOptions = new NestedPresenterOptions();
        nestedPresenterOptions.setCodeSplit(presenterConfigModel.getCodeSplit());

        createdNestedPresenter = CreateNestedPresenter.run(presenterOptions, nestedPresenterOptions, true);
    }

    private String getPackageName() {
        String packageName = presenterConfigModel.getPath().replace("[/\\]", ".");
        return packageName;
    }

    /**
     * This might not need to be done, if I set the select package to what exists already
     */
    private void createPresenterPackage() {
//        IJavaProject javaProject = presenterConfigModel.getProject();
//        IFolder folder = project.getFolder("src");
//        // folder.create(true, true, null);
//        IPackageFragmentRoot srcFolder = javaProject
//            .getPackageFragmentRoot(folder);
//        IPackageFragment fragment = srcFolder.createPackageFragment(project.getName(), true, null);
    }
    
    private void createPresenterModule() {
        RenderedTemplate renderedModule = createdNestedPresenter.getModule();
        
        String name = "";
        String contents = "";
        boolean force = true;
        
        IPackageFragment selectedPackage = presenterConfigModel.getSelectedPackage();
        try {
            selectedPackage.createCompilationUnit(name, contents, force, progressMonitor);
        } catch (JavaModelException e) {
            // TODO display error
            e.printStackTrace();
        }
    }
}
