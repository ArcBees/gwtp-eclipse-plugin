package com.arcbees.plugin.eclipse.wizard.createpresenter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import com.arcbees.plugin.template.create.place.CreatedNameTokens;
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
    private IPackageFragment presenterCreatedPackage;

    private CreatePresenterTask(PresenterConfigModel presenterConfigModel, IProgressMonitor progressMonitor) {
        this.presenterConfigModel = presenterConfigModel;
        this.progressMonitor = progressMonitor;
    }

    private void run() {

        fetchTemplates();

        createPresenterPackage();
        createPresenterModule();
        createPresenterModuleLinkForGin();
        createPresenter();
        createPresenterLinkInParent();
        createPresenterUiHandlers();
        createPresenterView();
        createPresenterViewUi();
        createNameTokens();
        createNameTokensToken();

        // TODO
        System.out.println("finished");
    }

    private void fetchTemplates() {
        // Translate options from PresenterConfigModel to PresenterOptions
        PresenterOptions presenterOptions = new PresenterOptions();
        presenterOptions.setPackageName(presenterConfigModel.getSelectedPackageAndNameAsSubPackage());
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

    /**
     * create a sub package for the presenter classes
     */
    private void createPresenterPackage() {
        IPackageFragment selectedPackage = presenterConfigModel.getSelectedPackage();
        IPackageFragmentRoot selectedPackageRoot = (IPackageFragmentRoot) selectedPackage.getParent();
        
        String presenterPackageName = presenterConfigModel.getSelectedPackageAndNameAsSubPackage();
        boolean force = true; // TODO force
        try {
            presenterCreatedPackage = selectedPackageRoot.createPackageFragment(presenterPackageName, force, progressMonitor);
        } catch (JavaModelException e) {
            // TODO
            e.printStackTrace();
        }
        // TODO logger
        System.out.println("Created Package: " + presenterPackageName);
    }

    private void createPresenterModule() {
        RenderedTemplate rendered = createdNestedPresenter.getModule();
        createClass(rendered, true);
    }

    private void createPresenterModuleLinkForGin() {
        // TODO gin install
    }

    private void createPresenter() {
        RenderedTemplate rendered = createdNestedPresenter.getPresenter();
        createClass(rendered, true);
    }

    private void createPresenterLinkInParent() {
        // TODO slots
    }

    private void createPresenterUiHandlers() {
        RenderedTemplate rendered = createdNestedPresenter.getUihandlers();
        createClass(rendered, true);
    }

    private void createPresenterView() {
        RenderedTemplate rendered = createdNestedPresenter.getView();
        createClass(rendered, true);
    }

    private void createPresenterViewUi() {
        RenderedTemplate rendered = createdNestedPresenter.getViewui();
        createClass(rendered, true);
    }

    /**
     * create name tokens class, if it doesn't exist
     */
    private void createNameTokens() {
        RenderedTemplate rendered = createdNestedPresenter.getNameTokens().getNameTokens();
        createClass(rendered, false);
    }

    /**
     * create fields and methods for name tokens
     */
    private void createNameTokensToken() {
        CreatedNameTokens createdNameTokens = createdNestedPresenter.getNameTokens();
        // TODO
    }

    private void createClass(RenderedTemplate rendered, boolean force) {
        String className = rendered.getNameAndNoExt();
        String contents = rendered.getContents();

        try {
            presenterCreatedPackage.createCompilationUnit(className, contents, force, progressMonitor);
        } catch (JavaModelException e) {
            // TODO display error
            System.out.println("Couldn't create className: " + className);
            e.printStackTrace();
        }
    }
}
