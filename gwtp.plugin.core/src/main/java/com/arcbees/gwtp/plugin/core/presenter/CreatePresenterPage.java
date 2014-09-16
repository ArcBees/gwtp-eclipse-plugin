/**
 * Copyright 2014 ArcBees Inc.
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

package com.arcbees.gwtp.plugin.core.presenter;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceField;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.arcbees.gwtp.plugin.core.StupidVelocityShim;
import com.arcbees.gwtp.plugin.core.common.GWTPNewTypeWizardPage;
import com.arcbees.gwtp.plugin.core.util.CodeFormattingUtil;
import com.arcbees.gwtp.plugin.core.util.zip.TemplateZipItem;
import com.arcbees.gwtp.plugin.core.util.zip.ZipTemplateIterator;

public class CreatePresenterPage extends GWTPNewTypeWizardPage {
    private static final Logger logger = Logger.getLogger(CreatePresenterPage.class.getName());

    private Map<String, Boolean> booleanMap = new HashMap<>();
    private Map<String, String> stringMap = new HashMap<>();

    private Composite tabBody;

    private boolean isNested = true;

    private Group revealInGroup;

    public CreatePresenterPage() {
        super("wizardPageCreatePresenter", "Create Presenter", "Create a presenter for the project.");
        stringMap.put("revealType", "RevealType.Root");
    }

    @Override
    public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {
        ensurePackageExists(monitor);
        StupidVelocityShim.setStripUnknownKeys(true);
        IPath packagePath = getPackageFragment().getResource().getProjectRelativePath()
                .append(new Path(getTypeName().toLowerCase() + "/"));

        IProject project = getJavaProject().getProject();
        project.getFolder(packagePath).create(true, true, monitor);

        Map<String, Object> context = new HashMap<>();
        context.put("name", getTypeName());
        context.put("package", getPackageFragment().getElementName() + "." + getTypeName().toLowerCase());
        if (isNested) {
            context.put("nested", true);
        } else {
            context.remove("nested");
            context.remove("manualreveal");
            context.remove("preparefromrequest");
        }

        for (Entry<String, Boolean> entry : booleanMap.entrySet()) {
            if (entry.getValue()) {
                context.put(entry.getKey(), true);
            }
        }

        for (Entry<String, String> entry : stringMap.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }

        if (stringMap.get("revealType").startsWith("RevealType.Root")) {
            context.remove("contentSlotImport");
        }

        if (booleanMap.containsKey("isplace") && booleanMap.get("isplace")) {
            try {
                addNameToken(context, stringMap.get("nametoken"), monitor);
            } catch (MalformedTreeException | BadLocationException e) {
                logger.severe("Could not save NameToken: " + e.getMessage());
            }
        }

        ZipTemplateIterator zipTemplateIterator = new ZipTemplateIterator(
                "/src/main/resources/templates/presenter/presenter.zip");
        for (TemplateZipItem item : zipTemplateIterator) {
            if (!item.isFolder()) {
                String fileText = StupidVelocityShim.evaluate(item.getText(), context);
                if (!fileText.trim().isEmpty()) {
                    String fileName = StupidVelocityShim.evaluate(item.getName(), context);
                    IFile file = project.getFile(packagePath.append(new Path(fileName)));
                    file.create(new ByteArrayInputStream(fileText.getBytes(StandardCharsets.UTF_8)),
                            IResource.NONE, null);
                }
            }
        }
        zipTemplateIterator.closeCurrentStream();

        ICompilationUnit ginModule = getGinModule(getPackageFragment(), monitor);
        if (ginModule != null) {
            logger.info("GinModule: " + ginModule.getElementName());
            try {
                createPresenterGinlink(ginModule, (String) context.get("package"), context.get("name") + "Module",
                        monitor);
            } catch (MalformedTreeException | BadLocationException e) {
                logger.severe("Could not install Gin Module: " + e.getMessage());
            }
        }
    }

    @Override
    protected void extendControl(Composite container) {
        createPresenterTabs(container, getNumberOfColumns());
        createExtraOptions(container);
    }

    @Override
    protected String getNameSuffix() {
        return "Presenter";
    }

    private void addMethodsToNameTokens(ICompilationUnit unit, String nameToken,
            IProgressMonitor monitor) throws JavaModelException, MalformedTreeException, BadLocationException {
        Document document = new Document(unit.getSource());
        CompilationUnit astRoot = initAstRoot(unit, monitor);

        // creation of ASTRewrite
        ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

        // find existing method
        MethodDeclaration method = findMethod(astRoot, getNameTokenMethod(nameToken));
        if (method != null) {
            logger.severe("FYI: the method in nameTokens already exists." + method.toString());
            return;
        }

        List types = astRoot.types();
        ASTNode rootNode = (ASTNode) types.get(0);
        ListRewrite listRewrite = rewrite.getListRewrite(rootNode, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

        ASTNode fieldNode = rewrite.createStringPlaceholder("public statuc String " + nameToken + " = \""
                + nameToken + "\";", ASTNode.EMPTY_STATEMENT);

        StringBuilder nameTokenMethod = new StringBuilder();
        nameTokenMethod.append("public static String ").append(getNameTokenMethod(nameToken)).append("() {\n")
        .append("return " + nameToken + ";\n").append("}\n");
        ASTNode methodNode = rewrite.createStringPlaceholder(nameTokenMethod.toString(), ASTNode.EMPTY_STATEMENT);

        listRewrite.insertFirst(fieldNode, null);
        listRewrite.insertLast(methodNode, null);

        // computation of the text edits
        TextEdit edits = rewrite.rewriteAST(document, unit.getJavaProject().getOptions(true));

        // computation of the new source code
        edits.apply(document);

        // format code
        String newSource = new CodeFormattingUtil(getJavaProject(), monitor).formatCodeJavaClass(document);

        // update of the compilation unit and save it
        IBuffer buffer = unit.getBuffer();
        buffer.setContents(newSource);
        buffer.save(monitor, true);
    }

    private void addNameToken(Map<String, Object> context, String nameToken, IProgressMonitor monitor)
            throws JavaModelException, MalformedTreeException, BadLocationException {
        if (nameToken != null && !nameToken.isEmpty()) {
            List<ResolvedSourceType> nameTokenFiles = findClassName("NameTokens");
            if (!nameTokenFiles.isEmpty()) {
                ResolvedSourceType rst = nameTokenFiles.get(0);
                addMethodsToNameTokens(rst.getCompilationUnit(), nameToken, monitor);
                context.put("nametoken", rst.getElementName() + "." + nameToken);
                context.put("nameTokenImport", "import " + rst.getFullyQualifiedName() + ";");
            }
        }
    }

    private Button createButton(Composite container, String text, int type,
            final String booleanValueName) {
        final Button button = createButton(container, text, type);

        if (booleanValueName != null) {
            button.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    booleanMap.put(booleanValueName, button.getSelection());
                }
            });
        }
        return button;
    }

    private void createExtraOptions(Composite container) {
        Group extraOptionsGroup = createGroup(container, "Extra Options", 8);

        Group events = createGroup(extraOptionsGroup, "Events", 1);
        createButton(events, "Add UiHandlers", SWT.CHECK, "uihandlers");

        Group popupGroup = createGroup(extraOptionsGroup, "Popup", 1);
        final Button popupButton = createButton(popupGroup, "Is Popup?", SWT.CHECK, "popup");
        popupButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                revealInGroup.setEnabled(!popupButton.getSelection());
                revealInGroup.setVisible(!popupButton.getSelection());
                stringMap.put("revealType", "RevealType.RootPopup");
            }
        });

        Group presenterLifecycle = createGroup(extraOptionsGroup, "Presenter Lifecycle Methods", 1);
        createButton(presenterLifecycle, "Add onBind()", SWT.CHECK, "onbind");
        createButton(presenterLifecycle, "Add onHide()", SWT.CHECK, "onhide");
        createButton(presenterLifecycle, "Add onReveal()", SWT.CHECK, "onreveal");
        createButton(presenterLifecycle, "Add onReset()", SWT.CHECK, "onreset");
        createButton(presenterLifecycle, "Add onUnbind()", SWT.CHECK, "onunbind");
    }

    private void createMoreNestedOptions(Composite container) {
        Group group = createGroup(container, "More Options", 1);
        createButton(group, "Code Split", SWT.CHECK, "codesplit");
        createButton(group, "Use Manual Reveal", SWT.CHECK, "manualreveal");
        createButton(group, "Use Prepare from Request", SWT.CHECK, "preparefromrequest");
    }

    private void createPlaceControl(Composite container) {
        Group group = createGroup(container, "Place", 3);
        final Button isPlaceButton = createButton(group, "Is a Place", SWT.CHECK, "isplace");

        Label label = new Label(group, SWT.NONE);
        label.setText("NameToken:");

        final Text nameTokenInput = new Text(group, SWT.BORDER);
        nameTokenInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        nameTokenInput.setEnabled(false);

        isPlaceButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                nameTokenInput.setEnabled(isPlaceButton.getSelection());
            }
        });

        nameTokenInput.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                stringMap.put("nametoken", nameTokenInput.getText());
            }
        });
    }

    /**
     * TODO extract this possibly, but I think I'll wait till I get into slots before I do it see what is common.
     */
    private void createPresenterGinlink(ICompilationUnit unit, String modulePackageName,
            String moduleName, IProgressMonitor monitor) throws JavaModelException, MalformedTreeException,
            BadLocationException {
        unit.createImport(modulePackageName + "." + moduleName, null, monitor);
        Document document = new Document(unit.getSource());

        CompilationUnit astRoot = initAstRoot(unit, monitor);

        // creation of ASTRewrite
        ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

        // find the configure method
        MethodDeclaration method = findMethod(astRoot, "configure");
        if (method == null) {
            logger.severe("createPresenterGinLink() unit did not have configure implementation.");
            return;
        }

        // presenter configure method install(new Module());
        String installModuleStatement = "install(new " + moduleName + "());";

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
        buffer.save(monitor, true);
    }

    private void createPresenterTabs(Composite container, int nColumns) {
        Group group = createGroup(container, "Presenter Type", 2);
        final Button nestedRadio = createButton(group, "Nested Presenter", SWT.RADIO);
        nestedRadio.setSelection(true);
        final Button widgetRadio = createButton(group, "Presenter Widget", SWT.RADIO);

        final TabFolder tabFolder = new TabFolder(container, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, nColumns, 1));

        Composite nestedTabBody = createTab(tabFolder, "Nested Presenter");
        createRevealInControl(nestedTabBody);
        createPlaceControl(nestedTabBody);
        createMoreNestedOptions(nestedTabBody);

        Composite widgetTabBody = createTab(tabFolder, "Presenter Widget");
        createPresenterWidgeControl(widgetTabBody);

        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isNested = tabFolder.getSelectionIndex() == 0;
                nestedRadio.setSelection(isNested);
                widgetRadio.setSelection(!isNested);
            }
        });

        nestedRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tabFolder.setSelection(0);
                isNested = true;
            }
        });

        widgetRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tabFolder.setSelection(1);
                isNested = false;
            }
        });
    }

    private void createPresenterWidgeControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        createButton(container, "Bind As Singleton", SWT.CHECK, "singleton");
    }

    private void createRevealInControl(Composite container) {
        this.revealInGroup = createGroup(container, "Reveal In", 5);

        Button rootRevealRadio = createButton(revealInGroup, "Root", SWT.RADIO);
        rootRevealRadio.setSelection(true);
        Button rootLayoutRevealRadio = createButton(revealInGroup, "RootLayout", SWT.RADIO);
        final Button contentSlotRadio = createButton(revealInGroup, "Slot", SWT.RADIO);

        rootRevealRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stringMap.put("revealType", "RevealType.Root");
            }
        });

       rootLayoutRevealRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stringMap.put("revealType", "RevealType.RootLayout");
            }
        });

        final Text contentSlot = new Text(revealInGroup, SWT.BORDER);
        contentSlot.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        contentSlot.setEnabled(false);

        contentSlot.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                stringMap.put("revealType", contentSlot.getText());
            }
        });

        final Button selectContentSlotButton = new Button(revealInGroup, SWT.NONE);
        selectContentSlotButton.setEnabled(false);
        contentSlotRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                contentSlot.setEnabled(contentSlotRadio.getSelection());
                selectContentSlotButton.setEnabled(contentSlotRadio.getSelection());
            }
        });
        selectContentSlotButton.setText("Select Slot");

        selectContentSlotButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectContentSlot(contentSlot);
            }
        });
    }

    private Composite createTab(TabFolder tabFolder, String name) {
        return createTabBody(tabFolder, createTabItem(tabFolder, name));
    }

    private Composite createTabBody(TabFolder tabFolder, TabItem tabItem) {
        tabBody = new Composite(tabFolder, SWT.NONE);
        tabBody.setLayout(getTabBodyLayout());
        tabItem.setControl(tabBody);
        return tabBody;
    }

    private TabItem createTabItem(TabFolder tabFolder, String name) {
        TabItem tab = new TabItem(tabFolder, SWT.NONE);
        tab.setText(name);
        return tab;
    }

    private List<ResolvedSourceType> findClassName(String name) {
        int searchFor = IJavaSearchConstants.CLASS;
        int limitTo = IJavaSearchConstants.TYPE;
        int matchRule = SearchPattern.R_EXACT_MATCH;
        SearchPattern searchPattern = SearchPattern.createPattern(name, searchFor, limitTo, matchRule);

        IJavaElement[] elements = new IJavaElement[] { getJavaProject() };
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        final List<ResolvedSourceType> found = new ArrayList<ResolvedSourceType>();
        SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch match) {
                // TODO
                System.out.println(match);
                Object element = match.getElement();
                found.add((ResolvedSourceType) element);
            }
        };

        SearchEngine searchEngine = new SearchEngine();
        SearchParticipant[] particpant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        try {
            searchEngine.search(searchPattern, particpant, scope, requestor, new NullProgressMonitor());
        } catch (CoreException e) {
            // TODO
            e.printStackTrace();
        }
        return found;
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

    private ICompilationUnit getGinModule(IPackageFragment packageFragment, IProgressMonitor monitor)
            throws JavaModelException {
        if (packageFragment.getChildren() != null) {
            for (IJavaElement element : packageFragment.getChildren()) {
                if (element.getElementType() == IJavaElement.COMPILATION_UNIT) {
                    ICompilationUnit cUnit = (ICompilationUnit) element;
                    logger.info("Compilation Unit: " + cUnit.getElementName());
                    for (IType type : cUnit.getTypes()) {
                        ITypeHierarchy hierarchy = type.newSupertypeHierarchy(monitor);
                        IType[] interfaces = hierarchy.getAllInterfaces();
                        for (IType checkInterface : interfaces) {
                            logger.info(checkInterface.getTypeQualifiedName());
                            if (checkInterface.getTypeQualifiedName().equals("GinModule")) {
                                return cUnit;
                            }
                        }
                    }
                }
            }
        }

        String packageName = packageFragment.getElementName();
        if (packageName.contains(".")) {
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
            return getGinModule(getPackageFragmentRoot().getPackageFragment(packageName), monitor);
        }

        return null;
    }

    private String getNameTokenMethod(String nameToken) {
        if (nameToken.length() == 1) {
            return "get" + nameToken.toUpperCase();
        } else {
            return "get" + nameToken.substring(0, 1).toUpperCase() + nameToken.substring(1);
        }
    }

    private Layout getTabBodyLayout() {
        GridLayout tabBodyLayout = new GridLayout(2, false);
        tabBodyLayout.marginTop = 10;
        tabBodyLayout.marginBottom = 10;
        tabBodyLayout.verticalSpacing = 20;
        return tabBodyLayout;
    }

    /**
     * Creation of DOM/AST from a ICompilationUnit.
     */
    private CompilationUnit initAstRoot(ICompilationUnit unit, IProgressMonitor monitor) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(unit);
        CompilationUnit astRoot = (CompilationUnit) parser.createAST(monitor);
        return astRoot;
    }

    private void selectContentSlot(Text contentSlot) {
        final List<ResolvedSourceField> contentSlots = new ArrayList<ResolvedSourceField>();

        String stringPattern = "ContentSlot";
        int searchFor = IJavaSearchConstants.ANNOTATION_TYPE;
        int limitTo = IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE;
        int matchRule = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;
        SearchPattern searchPattern = SearchPattern.createPattern(stringPattern, searchFor, limitTo, matchRule);

        IJavaProject project = getJavaProject();
        IJavaElement[] elements = new IJavaElement[] { project };
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch match) {
                // TODO
                System.out.println(match);

                ResolvedSourceField element = (ResolvedSourceField) match.getElement();
                contentSlots.add(element);
            }
        };

        SearchEngine searchEngine = new SearchEngine();
        SearchParticipant[] particpant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        try {
            searchEngine.search(searchPattern, particpant, scope, requestor, new NullProgressMonitor());
        } catch (CoreException e) {
            // TODO
            e.printStackTrace();
        }

        ResolvedSourceField[] contentListArray = new ResolvedSourceField[contentSlots.size()];
        contentSlots.toArray(contentListArray);

        ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new ILabelProvider() {
            @Override
            public void addListener(ILabelProviderListener listener) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public Image getImage(Object element) {
                return null;
            }

            @Override
            public String getText(Object element) {
                ResolvedSourceField rsf = (ResolvedSourceField) element;
                String name = rsf.getElementName();
                IType type = rsf.getDeclaringType();
                return type.getElementName() + "." + name;
            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener listener) {
            }
        });

        dialog.setElements(contentListArray);
        dialog.setTitle("Choose A Content Slot");

        // User pressed cancel
        if (dialog.open() != Window.OK) {
            contentSlot.setText("");
            return;
        }

        Object[] result = dialog.getResult();
        if (result == null || result.length < 1) {
            contentSlot.setText("");
        } else {
            ResolvedSourceField rsf = (ResolvedSourceField) result[0];
            contentSlot.setText(rsf.readableName());
            stringMap.put("contentSlotImport", "import " + rsf.getDeclaringType().getFullyQualifiedName() + ";");
        }
    }
}
