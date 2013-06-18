    package com.arcbees.plugin.eclipse.wizard.createpresenter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.Document;

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
            presenterCreatedPackage = selectedPackageRoot.createPackageFragment(presenterPackageName, force,
                    progressMonitor);
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
        //TODO search out gin module - has to implement AbstractPresenterModule
        
        // 1. first search parent
        ICompilationUnit unit = findPresenterModuleInParentPackage();
        if (unit != null) {
            createPresenterGinlink(unit);
        }
        
        // 2. TODO search client.gin
        // 3. TODO search all
    }
    
    private void createPresenterGinlink(ICompilationUnit unit) {
        String source = null;
        try {
            source = unit.getSource();
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        
        Document document = new Document(source);
        // TODO modify method
        
        // TODO logger
        System.out.println("Added presenter gin install into " + unit.getElementName());
    }

    private ICompilationUnit findPresenterModuleInParentPackage() {
        IPackageFragment packageSelected = presenterConfigModel.getSelectedPackage();
        
        ICompilationUnit[] units = null;
        try {
            units = packageSelected.getCompilationUnits();
        } catch (JavaModelException e) {
            e.printStackTrace(); 
            // TODO display
            return null;
        }
        
        String findUsedInterface = "GinModule";
        for (ICompilationUnit unit : units) {
            boolean found = findInterfaceUseInUnit(unit, findUsedInterface);
            if (found == true) {
                return unit; 
            }
        }
        return null;
    }

    private boolean findInterfaceUseInUnit(ICompilationUnit unit, String findUsedInterface) {
        try {
            for (IType type : unit.getTypes()) {
                ITypeHierarchy hierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
                IType[] interfaces = hierarchy.getAllInterfaces();
                for (IType inter : interfaces) {
                    System.out.println("interfac=" + inter.getElementName());
                    if (inter.getFullyQualifiedName('.').contains(findUsedInterface)) {
                        return true;
                    }
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return false;
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

        IFolder folder = (IFolder) presenterCreatedPackage.getResource();
        IFile newFile = folder.getFile(rendered.getNameAndNoExt());

        byte[] bytes = rendered.getContents().getBytes();
        InputStream source = new ByteArrayInputStream(bytes);
        try {
            newFile.create(source, IResource.NONE, progressMonitor);
        } catch (CoreException e) {
            // TODO
            e.printStackTrace();
        }

        // TODO logger
        System.out.println("created ui binder");
    }

    /**
     * create name tokens class, if it doesn't exist
     */
    private void createNameTokens() {
        RenderedTemplate rendered = createdNestedPresenter.getNameTokens().getNameTokens();
        // createClass(rendered, false);
        // TODO does class exist already?
        // TODO does the class exist in another package
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
