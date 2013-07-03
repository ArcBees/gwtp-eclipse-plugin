/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.plugin.eclipse.wizard.createpresenter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
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
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import com.arcbees.plugin.eclipse.util.PackageHierarchy;
import com.arcbees.plugin.eclipse.util.PackageHierarchyElement;
import com.arcbees.plugin.template.create.presenter.CreateNestedPresenter;
import com.arcbees.plugin.template.domain.place.CreatedNameTokens;
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
    private PackageHierarchy packageHierarchy;
    private IPackageFragment nameTokensPackage;

    private CreatePresenterTask(PresenterConfigModel presenterConfigModel, IProgressMonitor progressMonitor) {
        this.presenterConfigModel = presenterConfigModel;
        this.progressMonitor = progressMonitor;
    }

    private void run() {
        createPackageHierachyIndex();

        nameTokensPackage = createNameTokensPackage();

        fetchTemplates();

        forceWriting = true;

        createPresenterPackage();
        createPresenterModule();
        createPresenterModuleLinkForGin();
        createPresenter();
        createPresenterUiHandlers();
        createPresenterView();
        createPresenterViewUi();
        createNameTokens();
        createNameTokensToken();

        // TODO format the new source with the project settings
        formatUnits();

        // TODO focus on new presenter and open it up

        // TODO logger
        System.out.println("finished");
    }

    private void formatUnits() {
        // TODO
        // ToolFactory.createCodeFormatter(options)
        // .format(kind, string, offset, length, indentationLevel, lineSeparator);
    }

    private void createPackageHierachyIndex() {
        packageHierarchy = new PackageHierarchy(presenterConfigModel, progressMonitor);
        packageHierarchy.run();
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
        NestedPresenterOptions nestedPresenterOptions = new NestedPresenterOptions();
        nestedPresenterOptions.setPlace(presenterConfigModel.getPlace());
        nestedPresenterOptions.setNameToken(presenterConfigModel.getNameToken());
        nestedPresenterOptions.setNameTokensPackageName(nameTokensPackage.getElementName());
        nestedPresenterOptions.setCrawlable(presenterConfigModel.getCrawlable());
        nestedPresenterOptions.setCodeSplit(presenterConfigModel.getCodeSplit());

        if (presenterConfigModel.getRevealInRoot()) {
            nestedPresenterOptions.setRevealType("Root");
        } else if (presenterConfigModel.getRevealInRootLayout()) {
            nestedPresenterOptions.setRevealType("RootLayout");
        } else if (presenterConfigModel.getPopupPresenter()) {
            nestedPresenterOptions.setRevealType("RootPopup");
        } else if (presenterConfigModel.getRevealInSlot()) {
            nestedPresenterOptions.setRevealType(presenterConfigModel.getContentSlotAsString());
        }
        nestedPresenterOptions.setContentSlotImport(presenterConfigModel.getContentSlotImport());

        createdNestedPresenter = CreateNestedPresenter.run(presenterOptions, nestedPresenterOptions, true);
    }

    /**
     * Create a sub package for the presenter classes
     */
    private String createPresenterPackage() {
        String presenterPackageName = presenterConfigModel.getSelectedPackageAndNameAsSubPackage();
        createPackage(presenterPackageName, forceWriting);
        // TODO logger
        System.out.println("Created Package: " + presenterPackageName);
        return presenterPackageName;
    }

    private IPackageFragment createPackage(String packageName, boolean forceWriting) {
        IPackageFragment selectedPackage = presenterConfigModel.getSelectedPackage();
        IPackageFragmentRoot selectedPackageRoot = (IPackageFragmentRoot) selectedPackage.getParent();

        IPackageFragment created = null;
        try {
            created = presenterCreatedPackage = selectedPackageRoot.createPackageFragment(packageName, forceWriting,
                    progressMonitor);
        } catch (JavaModelException e) {
            // TODO display error
            e.printStackTrace();
        }

        return created;
    }

    private IPackageFragment createNameTokensPackage() {
        IPackageFragment selectedPackage = presenterConfigModel.getSelectedPackage();
        String selectedPackageString = selectedPackage.getElementName();
        PackageHierarchyElement clientPackage = packageHierarchy.findParentClient(selectedPackageString);
        String clientPackageString = clientPackage.getPackageFragment().getElementName();

        // name tokens pacakage ...client.place.NameTokens
        clientPackageString += ".place";

        PackageHierarchyElement nameTokensPackageExists = packageHierarchy.find(clientPackageString);

        IPackageFragment createdNameTokensPackage = null;
        if (nameTokensPackageExists != null && nameTokensPackageExists.getPackageFragment() != null) {
            createdNameTokensPackage = nameTokensPackageExists.getPackageFragment();
        } else {
            createdNameTokensPackage = createPackage(clientPackageString, forceWriting);
        }

        return createdNameTokensPackage;
    }

    private void createPresenterModule() {
        RenderedTemplate rendered = createdNestedPresenter.getModule();
        createClass(rendered, forceWriting);
    }

    /**
     * TODO extraction of functions TODO extract "GinModule" to constant TODO extract "gin" to constant
     */
    private void createPresenterModuleLinkForGin() {
        // 1. first search parent
        ICompilationUnit unit = packageHierarchy.findInterfaceTypeInParentPackage(
                presenterConfigModel.getSelectedPackage(), "GinModule");

        // 2. next check if the parent is client and if so, scan all packages for ginModule
        String selectedPackageElementName = presenterConfigModel.getSelectedPackage().getElementName();
        if (unit == null && packageHierarchy.isParentTheClientPackage(selectedPackageElementName)) {
            // first check for a gin package with GinModule
            PackageHierarchyElement hierarchyElement = packageHierarchy.findParentClientAndAddPackage(
                    selectedPackageElementName, "gin");
            if (hierarchyElement != null) {
                IPackageFragment clienPackage = hierarchyElement.getPackageFragment();
                unit = packageHierarchy.findInterfaceTypeInParentPackage(clienPackage, "GinModule");
            }

            // If no gin package check for any existence of a GinModule
            // TODO could make this smarter in the future, this is a last resort, to install it somewhere.
            if (unit == null) {
                unit = packageHierarchy.findFirstInterfaceType("GinModule");
                // TODO logger
                System.out
                        .println("Warning: This didn't find a ideal place to put the gin install for the new presenter module");
            }
        }

        // 3. walk up next parent for and look for gin module
        if (unit == null) {
            if (selectedPackageElementName.contains("client")) {
                PackageHierarchyElement hierarchyElement = packageHierarchy.findParent(selectedPackageElementName);

                if (hierarchyElement.getPackageFragment() != null) {
                    IPackageFragment parentParentPackage = hierarchyElement.getPackageFragment();
                    unit = packageHierarchy.findInterfaceTypeInParentPackage(parentParentPackage, "GinModule");
                }
            }
        }

        // 4. search all filter by GinModule interface, this would be easy
        // If no gin package check for any existence of a GinModule
        // TODO could make this smarter in the future, this is a last resort, to install it somewhere.
        if (unit == null) {
            unit = packageHierarchy.findFirstInterfaceType("GinModule");
            // TODO logger
            System.out
                    .println("Warning: This didn't find a ideal place to put the gin install for the new presenter module");
        }

        // (could do this next for ease)
        if (unit != null) {
            try {
                createPresenterGinlink(unit);
            } catch (JavaModelException | MalformedTreeException | BadLocationException e) {
                // TODO display error
                e.printStackTrace();
            }
        } else {
            // TODO display error, wasn't able to install gin module
            System.out.println("Error: Wasn't able to install Module");
        }
    }

    /**
     * TODO extract this possibly, but I think I'll wait till I get into slots before I do it see what is common.
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
        String fileNameForModule = createdNestedPresenter.getModule().getNameAndNoExts();
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

    private void createPresenter() {
        RenderedTemplate rendered = createdNestedPresenter.getPresenter();
        createClass(rendered, forceWriting);
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
        List<ResolvedSourceType> foundNameTokens = packageHierarchy.findClassName("NameTokens");
        if (foundNameTokens != null && foundNameTokens.size() > 0) { // exists
            try {
                addMethodsToNameTokens(foundNameTokens);
            } catch (JavaModelException | MalformedTreeException | BadLocationException e) {
                e.printStackTrace();
            }
        } else { // exists
            createNewNameTokens();
        }
    }

    private void addMethodsToNameTokens(List<ResolvedSourceType> foundNameTokens) throws JavaModelException,
            MalformedTreeException, BadLocationException {
        ResolvedSourceType rst = foundNameTokens.get(0);
        ICompilationUnit unit = rst.getCompilationUnit();
        Document document = new Document(unit.getSource());
        CompilationUnit astRoot = initAstRoot(unit);

        // creation of ASTRewrite
        ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

        // find existing method
        MethodDeclaration method = findMethod(astRoot, presenterConfigModel.getNameTokenMethodName());
        if (method != null) {
            // TODO already exists, display warning
            return;
        }
        
        CreatedNameTokens nameTokens = createdNestedPresenter.getNameTokens();
        List<String> fields = nameTokens.getFields();
        List<String> methods = nameTokens.getMethods();
        String fieldSource = fields.get(0);
        String methodSource = methods.get(0);

        List types = astRoot.types();
        ASTNode rootNode = (ASTNode) types.get(0);
        ListRewrite listRewrite = rewrite.getListRewrite(rootNode, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
        
        ASTNode fieldNode = rewrite.createStringPlaceholder(fieldSource, ASTNode.EMPTY_STATEMENT);
        ASTNode methodNode = rewrite.createStringPlaceholder(methodSource, ASTNode.EMPTY_STATEMENT);
        
        listRewrite.insertFirst(fieldNode, null);
        listRewrite.insertLast(methodNode, null);
        
        // computation of the text edits
        TextEdit edits = rewrite.rewriteAST(document, unit.getJavaProject().getOptions(true));

        // computation of the new source code
        edits.apply(document);
        String newSource = document.get();

        // update of the compilation unit and save it
        IBuffer buffer = unit.getBuffer();
        buffer.setContents(newSource);
        buffer.save(progressMonitor, forceWriting);
    }

    private void createNewNameTokens() {
        CreatedNameTokens createdNameTokensTemplate = createdNestedPresenter.getNameTokens();
        RenderedTemplate rendered = createdNameTokensTemplate.getNameTokens();
        String className = rendered.getNameAndNoExt();
        String contents = rendered.getContents();

        try {
            nameTokensPackage.createCompilationUnit(className, contents, forceWriting, progressMonitor);
        } catch (JavaModelException e) {
            // TODO display error
            System.out.println("Couldn't create className: " + className);
            e.printStackTrace();
        }

        // TODO
        System.out.println("test");
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
