package com.arcbees.plugin.eclipse.wizard.createpresenter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

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
    private boolean forceWriting;

    private CreatePresenterTask(PresenterConfigModel presenterConfigModel, IProgressMonitor progressMonitor) {
        this.presenterConfigModel = presenterConfigModel;
        this.progressMonitor = progressMonitor;
    }

    private void run() {
        fetchTemplates();

        forceWriting = true;
        
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
        // TODO add more options...

        if (presenterConfigModel.getNestedPresenter()) {
            fetchNestedTemplate(presenterOptions);
        } else if (presenterConfigModel.getPresenterWidget()) {
            // TODO
        } else if (presenterConfigModel.getPopupPresenter()) {
            // TODO
        }
    }

    private void fetchNestedTemplate(PresenterOptions presenterOptions) {
        // TODO translate more options
        NestedPresenterOptions nestedPresenterOptions = new NestedPresenterOptions();
        nestedPresenterOptions.setCodeSplit(presenterConfigModel.getCodeSplit());
        createdNestedPresenter = CreateNestedPresenter.run(presenterOptions, nestedPresenterOptions, true);
    }

    /**
     * Create a sub package for the presenter classes
     */
    private String createPresenterPackage() {
        IPackageFragment selectedPackage = presenterConfigModel.getSelectedPackage();
        IPackageFragmentRoot selectedPackageRoot = (IPackageFragmentRoot) selectedPackage.getParent();

        String presenterPackageName = presenterConfigModel.getSelectedPackageAndNameAsSubPackage();
        try {
            presenterCreatedPackage = selectedPackageRoot.createPackageFragment(presenterPackageName, forceWriting,
                    progressMonitor);
        } catch (JavaModelException e) {
            // TODO display error
            e.printStackTrace();
        }
        // TODO logger
        System.out.println("Created Package: " + presenterPackageName);
        return presenterPackageName;
    }

    private void createPresenterModule() {
        RenderedTemplate rendered = createdNestedPresenter.getModule();
        createClass(rendered, forceWriting);
    }

    /**
     * TODO must have better search done next <<<~~~~~~~~~~~~~~~~~
     */
    private void createPresenterModuleLinkForGin() {
        // TODO search out gin module - has to implement AbstractPresenterModule

        // 1. first search parent
        ICompilationUnit unit = findPresenterModuleInParentPackage();
        // 2. TODO walk parent for and look for gin
        // 3. TODO at the client level try client.gin
        // 4. TODO search all filter by GinModule interface, this would be easy (could do this next for ease)

        if (unit != null) {
            try {
                createPresenterGinlink(unit);
            } catch (JavaModelException | MalformedTreeException | BadLocationException e) {
                // TODO display error
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO extract this possibly, but I think I'll wait till I get into slots
     * before I do it see what is common.
     */
    private void createPresenterGinlink(ICompilationUnit unit) throws JavaModelException, MalformedTreeException,
            BadLocationException {
        Document document = new Document(unit.getSource());

        CompilationUnit astRoot = initAstRoot(unit);

        // creation of ASTRewrite
        ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

        // find the configure method
        MethodDeclaration method = findMethod(astRoot, "configure");
        if (method == null) {
            // TODO throw exception
            return;
        }

        // presenter import
        String fileNameForModule = createdNestedPresenter.getModule().getFileName();
        String importName = presenterConfigModel.getSelectedPackageAndNameAsSubPackage() + "." + fileNameForModule;
        String[] presenterPackage = importName.split("\\.");
        ImportDeclaration importDeclaration = astRoot.getAST().newImportDeclaration();
        importDeclaration.setName(astRoot.getAST().newName(presenterPackage));
        ListRewrite lrw = rewrite.getListRewrite(astRoot, CompilationUnit.IMPORTS_PROPERTY);
        lrw.insertLast(importDeclaration, null);

        // presenter configure method install(new Module());
        String moduleName = fileNameForModule + "()";
        String installModuleStatement = "install(new " + moduleName + ");";

        Block block = method.getBody();
        ListRewrite listRewrite = rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
        ASTNode placeHolder = rewrite.createStringPlaceholder(installModuleStatement, ASTNode.EMPTY_STATEMENT);
        listRewrite.insertFirst(placeHolder, null);

        // computation of the text edits
        TextEdit edits = rewrite.rewriteAST(document, unit.getJavaProject().getOptions(true));

        // computation of the new source code
        edits.apply(document);
        String newSource = document.get();

        // update of the compilation unit and save it
        IBuffer buffer = unit.getBuffer();
        buffer.setContents(newSource);
        buffer.save(progressMonitor, forceWriting);

        // TODO logger
        System.out.println("Added presenter gin install into " + unit.getElementName() + " " + installModuleStatement);
    }

    private MethodDeclaration findMethod(CompilationUnit astRoot, String methodName) {
        MethodDeclaration[] methods = ((TypeDeclaration) astRoot.types().get(0)).getMethods();
        if (methods == null) {
            return null;
        }

        for (MethodDeclaration method : methods) {
            if (method.getName().toString().contains(methodName)) {
                return method;
            }
        }
        return null;
    }

    /**
     * Creation of DOM/AST from a ICompilationUnit.
     */
    private CompilationUnit initAstRoot(ICompilationUnit unit) {
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setSource(unit);
        CompilationUnit astRoot = (CompilationUnit) parser.createAST(progressMonitor);
        return astRoot;
    }

    private ICompilationUnit findPresenterModuleInParentPackage() {
        IPackageFragment packageSelected = presenterConfigModel.getSelectedPackage();

        ICompilationUnit[] units = null;
        try {
            units = packageSelected.getCompilationUnits();
        } catch (JavaModelException e) {
            e.printStackTrace();
            // TODO display error
            return null;
        }

        String findUsedInterface = "GinModule";
        for (ICompilationUnit unit : units) {
            boolean found = findInterfaceUseInUnit(unit, findUsedInterface);
            if (found == true) {
                return unit;
            }
        }
        
        // TODO display error
        
        return null;
    }

    private boolean findInterfaceUseInUnit(ICompilationUnit unit, String findUsedInterface) {
        try {
            for (IType type : unit.getTypes()) {
                ITypeHierarchy hierarchy = type.newSupertypeHierarchy(progressMonitor);
                IType[] interfaces = hierarchy.getAllInterfaces();
                for (IType checkInterface : interfaces) {
                    if (checkInterface.getFullyQualifiedName('.').contains(findUsedInterface)) {
                        return true;
                    }
                }
            }
        } catch (JavaModelException e) {
            // TODO display error
            e.printStackTrace();
        }
        return false;
    }

    private void createPresenter() {
        RenderedTemplate rendered = createdNestedPresenter.getPresenter();
        createClass(rendered, forceWriting);
    }

    private void createPresenterLinkInParent() {
        // TODO slots
    }

    private void createPresenterUiHandlers() {
        RenderedTemplate rendered = createdNestedPresenter.getUihandlers();
        createClass(rendered, forceWriting);
    }

    private void createPresenterView() {
        RenderedTemplate rendered = createdNestedPresenter.getView();
        createClass(rendered, forceWriting);
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
            // TODO or throw exception
            e.printStackTrace();
        }
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
