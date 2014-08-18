package com.arcbees.gwtp.plugin.core.presenter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

public class CreatePresenterPage extends GWTPNewTypeWizardPage {

    private final static Logger logger = Logger.getLogger(CreatePresenterPage.class.getName());

    private Map<String, Boolean> booleanMap = new HashMap<>();
    private Map<String, String> stringMap = new HashMap<>();

    private Composite tabBody;

    private boolean isNested = true;

    private Group revealInGroup;

    public CreatePresenterPage() {
        super("wizardPageCreatePresenter", "Create Presenter", "Create a presenter for the project.");
        stringMap.put("revealType", "RevealType.Root");
    }



    private void addMethodsToNameTokens(final ICompilationUnit unit, final String nameToken, final IProgressMonitor monitor) throws JavaModelException, MalformedTreeException, BadLocationException {
        final Document document = new Document(unit.getSource());
        final CompilationUnit astRoot = initAstRoot(unit, monitor);

        // creation of ASTRewrite
        final ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

        // find existing method
        final MethodDeclaration method = findMethod(astRoot, getNameTokenMethod(nameToken));
        if (method != null) {
            logger.severe("FYI: the method in nameTokens already exists." + method.toString());
            return;
        }


        final List types = astRoot.types();
        final ASTNode rootNode = (ASTNode) types.get(0);
        final ListRewrite listRewrite = rewrite.getListRewrite(rootNode, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

        final ASTNode fieldNode = rewrite.createStringPlaceholder("public final static String " + nameToken + " = \"" + nameToken + "\";", ASTNode.EMPTY_STATEMENT);

        final StringBuilder nameTokenMethod = new StringBuilder();
        nameTokenMethod.append("public static String ").append(getNameTokenMethod(nameToken)).append("() {\n").append("return " + nameToken + ";\n").append("}\n");
        final ASTNode methodNode = rewrite.createStringPlaceholder(nameTokenMethod.toString(), ASTNode.EMPTY_STATEMENT);

        listRewrite.insertFirst(fieldNode, null);
        listRewrite.insertLast(methodNode, null);

        // computation of the text edits
        final TextEdit edits = rewrite.rewriteAST(document, unit.getJavaProject().getOptions(true));

        // computation of the new source code
        edits.apply(document);


        // format code
        final String newSource = new CodeFormattingUtil(getJavaProject(), monitor).formatCodeJavaClass(document);

        // update of the compilation unit and save it
        final IBuffer buffer = unit.getBuffer();
        buffer.setContents(newSource);
        buffer.save(monitor, true);
    }

    private void addNameToken(final Map<String, Object> context, final String nameToken, final IProgressMonitor monitor) throws JavaModelException, MalformedTreeException, BadLocationException {
        if (nameToken != null && !nameToken.isEmpty()) {
            final List<ResolvedSourceType> nameTokenFiles = findClassName("NameTokens");
            if (!nameTokenFiles.isEmpty()) {
                final ResolvedSourceType rst = nameTokenFiles.get(0);
                addMethodsToNameTokens(rst.getCompilationUnit(), nameToken, monitor);
                context.put("nametoken", rst.getElementName() + "." + nameToken);
                context.put("nameTokenImport", "import " + rst.getFullyQualifiedName() + ";");
            }
        }

    }


    private Button createButton(final Composite container, final String text, final int type, final String booleanValueName) {
        final Button button = createButton(container, text, type);

        if (booleanValueName != null) {
            button.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {

                    booleanMap.put(booleanValueName, button.getSelection());

                }

            });
        }
        return button;
    }

    private void createExtraOptions(final Composite container) {
        final Group extraOptionsGroup = createGroup(container, "Extra Options", 8);

        final Group events = createGroup(extraOptionsGroup, "Events", 1);
        createButton(events, "Add UiHandlers", SWT.CHECK, "uihandlers");

        final Group popupGroup = createGroup(extraOptionsGroup, "Popup", 1);
        final Button popupButton = createButton(popupGroup, "Is Popup?", SWT.CHECK, "popup");
        popupButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                revealInGroup.setEnabled(!popupButton.getSelection());
                revealInGroup.setVisible(!popupButton.getSelection());
                stringMap.put("revealType", "RevealType.RootPopup");
            }
        });



        final Group presenterLifecycle = createGroup(extraOptionsGroup, "Presenter Lifecycle Methods", 1);
        createButton(presenterLifecycle, "Add onBind()", SWT.CHECK, "onbind");
        createButton(presenterLifecycle, "Add onHide()", SWT.CHECK, "onhide");
        createButton(presenterLifecycle, "Add onReveal()", SWT.CHECK, "onreveal");
        createButton(presenterLifecycle, "Add onReset()", SWT.CHECK, "onreset");
        createButton(presenterLifecycle, "Add onUnbind()", SWT.CHECK, "onunbind");

    }



    private void createMoreNestedOptions(final Composite container) {
        final Group group = createGroup(container, "More Options", 1);
        createButton(group, "Code Split", SWT.CHECK, "codesplit");
        createButton(group, "Use Manual Reveal", SWT.CHECK, "manualreveal");
        createButton(group, "Use Prepare from Request", SWT.CHECK, "preparefromrequest");

    }

    private void createPlaceControl(final Composite container) {
        final Group group = createGroup(container, "Place", 3);
        final Button isPlaceButton = createButton(group, "Is a Place", SWT.CHECK, "isplace");

        final Label label = new Label(group, SWT.NONE);
        label.setText("NameToken:");

        final Text nameTokenInput = new Text(group, SWT.BORDER);
        nameTokenInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        nameTokenInput.setEnabled(false);

        isPlaceButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                nameTokenInput.setEnabled(isPlaceButton.getSelection());
            }

        });

        nameTokenInput.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                stringMap.put("nametoken", nameTokenInput.getText());

            }
        });

    }

    /**
     * TODO extract this possibly, but I think I'll wait till I get into slots before I do it see what is common.
     */
    private void createPresenterGinlink(final ICompilationUnit unit, final String modulePackageName, final String moduleName, final IProgressMonitor monitor) throws JavaModelException, MalformedTreeException, BadLocationException {
        unit.createImport(modulePackageName + "." + moduleName, null, monitor);
        final Document document = new Document(unit.getSource());

        final CompilationUnit astRoot = initAstRoot(unit, monitor);

        // creation of ASTRewrite
        final ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

        // find the configure method
        final MethodDeclaration method = findMethod(astRoot, "configure");
        if (method == null) {
            logger.severe("createPresenterGinLink() unit did not have configure implementation.");
            return;
        }

        // presenter configure method install(new Module());
        final String installModuleStatement = "install(new " + moduleName + "());";

        final Block block = method.getBody();
        final ListRewrite listRewrite = rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
        final ASTNode placeHolder = rewrite.createStringPlaceholder(installModuleStatement, ASTNode.EMPTY_STATEMENT);
        listRewrite.insertFirst(placeHolder, null);

        // computation of the text edits
        final TextEdit edits = rewrite.rewriteAST(document, unit.getJavaProject().getOptions(true));

        // computation of the new source code
        edits.apply(document);
        final String newSource = document.get();

        // update of the compilation unit and save it
        final IBuffer buffer = unit.getBuffer();
        buffer.setContents(newSource);
        buffer.save(monitor, true);

        logger.info("Added presenter gin install into " + unit.getElementName() + " " + installModuleStatement);
    }

    private void createPresenterTabs(final Composite container, final int nColumns) {
        final Group group = createGroup(container, "Presenter Type", 2);
        final Button nestedRadio = createButton(group, "Nested Presenter", SWT.RADIO);
        nestedRadio.setSelection(true);
        final Button widgetRadio = createButton(group, "Presenter Widget", SWT.RADIO);

        final TabFolder tabFolder = new TabFolder(container, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, nColumns, 1));

        final Composite nestedTabBody = createTab(tabFolder, "Nested Presenter");
        createRevealInControl(nestedTabBody);
        createPlaceControl(nestedTabBody);
        createMoreNestedOptions(nestedTabBody);

        final Composite widgetTabBody = createTab(tabFolder, "Presenter Widget");
        createPresenterWidgeControl(widgetTabBody);

        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                isNested = tabFolder.getSelectionIndex() == 0;
                nestedRadio.setSelection(isNested);
                widgetRadio.setSelection(!isNested);
            }
        });

        nestedRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                tabFolder.setSelection(0);
                isNested = true;
            }
        });

        widgetRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                tabFolder.setSelection(1);
                isNested = false;
            }
        });

    }

    private void createPresenterWidgeControl(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);

        final GridLayout layout = new GridLayout();
        container.setLayout(layout);

        createButton(container, "Bind As Singleton", SWT.CHECK, "singleton");


    }

    private void createRevealInControl(final Composite container) {
        this.revealInGroup = createGroup(container, "Reveal In", 5);

        final Button rootRevealRadio = createButton(revealInGroup, "Root", SWT.RADIO);
        rootRevealRadio.setSelection(true);
        final Button rootLayoutRevealRadio = createButton(revealInGroup, "RootLayout", SWT.RADIO);
        final Button contentSlotRadio = createButton(revealInGroup, "Slot", SWT.RADIO);

        rootRevealRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                stringMap.put("revealType", "RevealType.Root");
            }

        });

        rootLayoutRevealRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                stringMap.put("revealType", "RevealType.RootLayout");
            }

        });

        final Text contentSlot = new Text(revealInGroup, SWT.BORDER);
        contentSlot.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        contentSlot.setEnabled(false);

        contentSlot.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                stringMap.put("revealType", contentSlot.getText());
            }
        });

        final Button selectContentSlotButton = new Button(revealInGroup, SWT.NONE);
        selectContentSlotButton.setEnabled(false);
        contentSlotRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                contentSlot.setEnabled(contentSlotRadio.getSelection());
                selectContentSlotButton.setEnabled(contentSlotRadio.getSelection());

            }
        });
        selectContentSlotButton.setText("Select Slot");

        selectContentSlotButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                selectContentSlot(contentSlot);
            }
        });

    }

    private Composite createTab(final TabFolder tabFolder, final String name) {
        return createTabBody(tabFolder, createTabItem(tabFolder, name));
    }

    private Composite createTabBody(final TabFolder tabFolder, final TabItem tabItem) {
        tabBody = new Composite(tabFolder, SWT.NONE);
        tabBody.setLayout(getTabBodyLayout());
        tabItem.setControl(tabBody);
        return tabBody;
    }

    private TabItem createTabItem(final TabFolder tabFolder, final String name) {
        final TabItem tab = new TabItem(tabFolder, SWT.NONE);
        tab.setText(name);
        return tab;
    }

    @Override
    public void createType(final IProgressMonitor monitor) throws CoreException, InterruptedException {
        StupidVelocityShim.setStripUnknownKeys(true);
        final IPath packagePath = getPackageFragment().getResource().getProjectRelativePath().append(new Path(getTypeName().toLowerCase() + "/"));

        final IProject project = getJavaProject().getProject();
        project.getFolder(packagePath).create(true, true, monitor);

        final String templateFile = "/src/main/resources/templates/presenter/presenter.zip";
        final Map<String, Object> context = new HashMap<>();
        context.put("name", getTypeName());
        context.put("package", getPackageFragment().getElementName() + "." + getTypeName().toLowerCase());
        if (isNested) {
            context.put("nested", true);
        } else {
            context.remove("nested");
            context.remove("manualreveal");
            context.remove("preparefromrequest");
        }

        for (final Entry<String, Boolean> entry : booleanMap.entrySet()) {
            if (entry.getValue()) {
                context.put(entry.getKey(), true);
            }
        }

        for (final Entry<String, String> entry : stringMap.entrySet()) {
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

        try {
            final ZipInputStream projectTemplate = new ZipInputStream(getClass().getResourceAsStream(templateFile));
            ZipEntry entry;
            while ((entry = projectTemplate.getNextEntry()) != null) {
                String fileName = entry.getName();

                if (!fileName.isEmpty()) {
                    fileName = StupidVelocityShim.evaluate(fileName, context);

                    System.out.println(fileName);
                    if (fileName.endsWith("template")) {
                        fileName = fileName.substring(0, fileName.length() - "template.".length());

                        @SuppressWarnings("resource") final Scanner sc = new Scanner(projectTemplate);
                        final StringBuilder sb = new StringBuilder();
                        while (sc.hasNextLine()) {
                            sb.append(sc.nextLine()).append("\n");
                        }
                        projectTemplate.closeEntry();

                        final String template = StupidVelocityShim.evaluate(sb.toString(), context);
                        if (!template.isEmpty()) {
                            final IFile file = project.getFile(packagePath.append(new Path(fileName)));
                            file.create(new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8)), IResource.NONE, null);
                        }

                    }
                }
            }
            projectTemplate.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        final ICompilationUnit ginModule = getGinModule(getPackageFragment(), monitor);
        if (ginModule != null) {
            logger.info("GinModule: " + ginModule.getElementName());
            try {
                createPresenterGinlink(ginModule, (String) context.get("package"), context.get("name") + "Module", monitor);
            } catch (MalformedTreeException | BadLocationException e) {
                logger.severe("Could not install Gin Module: " + e.getMessage());
            }
        }

    }

    @Override
    protected void extendControl(final Composite container) {
        createPresenterTabs(container, getNumberOfColumns());

        createExtraOptions(container);
    }

    private List<ResolvedSourceType> findClassName(final String name) {
        final int searchFor = IJavaSearchConstants.CLASS;
        final int limitTo = IJavaSearchConstants.TYPE;
        final int matchRule = SearchPattern.R_EXACT_MATCH;
        final SearchPattern searchPattern = SearchPattern.createPattern(name, searchFor, limitTo, matchRule);

        final IJavaElement[] elements = new IJavaElement[] { getJavaProject() };
        final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        final List<ResolvedSourceType> found = new ArrayList<ResolvedSourceType>();
        final SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(final SearchMatch match) {
                // TODO
                System.out.println(match);
                final Object element = match.getElement();
                found.add((ResolvedSourceType) element);
            }
        };

        final SearchEngine searchEngine = new SearchEngine();
        final SearchParticipant[] particpant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        try {
            searchEngine.search(searchPattern, particpant, scope, requestor, new NullProgressMonitor());
        } catch (final CoreException e) {
            // TODO
            e.printStackTrace();
        }
        return found;
    }

    private MethodDeclaration findMethod(final CompilationUnit astRoot, final String methodName) {
        final MethodDeclaration[] methods = ((TypeDeclaration) astRoot.types().get(0)).getMethods();
        if (methods == null) {
            return null;
        }

        for (final MethodDeclaration method : methods) {
            if (method.getName().toString().contains(methodName)) {
                return method;
            }
        }
        return null;
    }

    @Override
    public IType getCreatedType() {
        return null;
    }

    private ICompilationUnit getGinModule(final IPackageFragment packageFragment, final IProgressMonitor monitor) throws JavaModelException {

        if (packageFragment.getChildren() != null) {
            for (final IJavaElement element : packageFragment.getChildren()) {
                if (element.getElementType() == IJavaElement.COMPILATION_UNIT) {
                    final ICompilationUnit cUnit = (ICompilationUnit) element;
                    logger.info("Compilation Unit: " + cUnit.getElementName());
                    for (final IType type : cUnit.getTypes()) {
                        final ITypeHierarchy hierarchy = type.newSupertypeHierarchy(monitor);
                        final IType[] interfaces = hierarchy.getAllInterfaces();
                        for (final IType checkInterface : interfaces) {
                            logger.info(checkInterface.getTypeQualifiedName());
                            if (checkInterface.getTypeQualifiedName().equals("GinModule")) {
                                return cUnit;
                            }
                        }
                    }
                }
            }
            ;
        }

        final IJavaElement parent = packageFragment.getParent();
        if (parent != null && parent.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
            return getGinModule((IPackageFragment) packageFragment.getParent(), monitor);
        }

        return null;

    }

    private String getNameTokenMethod(final String nameToken) {
        if (nameToken.length() == 1) {
            return "get" + nameToken.toUpperCase();
        } else {
            return "get" + nameToken.substring(0, 1).toUpperCase() + nameToken.substring(1);
        }
    }

    private Layout getTabBodyLayout() {
        final GridLayout gl_tabBody = new GridLayout(2, false);
        gl_tabBody.marginTop = 10;
        gl_tabBody.marginBottom = 10;
        gl_tabBody.verticalSpacing = 20;
        return gl_tabBody;
    }

    /**
     * Creation of DOM/AST from a ICompilationUnit.
     */
    private CompilationUnit initAstRoot(final ICompilationUnit unit, final IProgressMonitor monitor) {
        final ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(unit);
        final CompilationUnit astRoot = (CompilationUnit) parser.createAST(monitor);
        return astRoot;
    }

    private void selectContentSlot(final Text contentSlot) {
        final List<ResolvedSourceField> contentSlots = new ArrayList<ResolvedSourceField>();

        final String stringPattern = "ContentSlot";
        final int searchFor = IJavaSearchConstants.ANNOTATION_TYPE;
        final int limitTo = IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE;
        final int matchRule = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;
        final SearchPattern searchPattern = SearchPattern.createPattern(stringPattern, searchFor, limitTo, matchRule);

        final IJavaProject project = getJavaProject();
        final IJavaElement[] elements = new IJavaElement[] { project };
        final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        final SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(final SearchMatch match) {
                // TODO
                System.out.println(match);

                final ResolvedSourceField element = (ResolvedSourceField) match.getElement();
                contentSlots.add(element);
            }
        };

        final SearchEngine searchEngine = new SearchEngine();
        final SearchParticipant[] particpant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        try {
            searchEngine.search(searchPattern, particpant, scope, requestor, new NullProgressMonitor());
        } catch (final CoreException e) {
            // TODO
            e.printStackTrace();
        }

        final ResolvedSourceField[] contentListArray = new ResolvedSourceField[contentSlots.size()];
        contentSlots.toArray(contentListArray);

        final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new ILabelProvider() {
            @Override
            public void addListener(final ILabelProviderListener listener) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public Image getImage(final Object element) {
                return null;
            }

            @Override
            public String getText(final Object element) {
                final ResolvedSourceField rsf = (ResolvedSourceField) element;
                final String name = rsf.getElementName();
                final IType type = rsf.getDeclaringType();
                return type.getElementName() + "." + name;
            }

            @Override
            public boolean isLabelProperty(final Object element, final String property) {
                return false;
            }

            @Override
            public void removeListener(final ILabelProviderListener listener) {
            }
        });

        dialog.setElements(contentListArray);
        dialog.setTitle("Choose A Content Slot");

        // User pressed cancel
        if (dialog.open() != Window.OK) {
            contentSlot.setText("");
            return;
        }

        final Object[] result = dialog.getResult();
        if (result == null || result.length < 1) {
            contentSlot.setText("");
        } else {
            final ResolvedSourceField rsf = (ResolvedSourceField) result[0];
            contentSlot.setText(rsf.readableName());
            stringMap.put("contentSlotImport", "import " + rsf.getDeclaringType().getFullyQualifiedName() + ";");
        }
    }

}
