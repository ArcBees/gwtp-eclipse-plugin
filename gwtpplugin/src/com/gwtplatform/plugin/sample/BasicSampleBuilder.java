package com.gwtplatform.plugin.sample;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.gwtplatform.plugin.SourceWriter;
import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.ProjectClass;
import com.gwtplatform.plugin.projectfile.src.client.core.Presenter;
import com.gwtplatform.plugin.projectfile.src.client.core.View;
import com.gwtplatform.plugin.projectfile.src.client.gin.Ginjector;
import com.gwtplatform.plugin.projectfile.src.client.gin.PresenterModule;
import com.gwtplatform.plugin.projectfile.src.client.place.PlaceAnnotation;
import com.gwtplatform.plugin.projectfile.src.client.place.Tokens;
import com.gwtplatform.plugin.projectfile.src.server.ActionHandler;
import com.gwtplatform.plugin.projectfile.src.server.guice.HandlerModule;
import com.gwtplatform.plugin.projectfile.src.shared.Action;
import com.gwtplatform.plugin.projectfile.src.shared.Result;

public class BasicSampleBuilder {

	private IJavaProject javaProject;
	private IPackageFragmentRoot root;
	private IPackageFragment clientPackage;
	private IPackageFragment serverPackage;
	private IPackageFragment sharedPackage;
	private SourceWriterFactory sourceWriterFactory;

	public BasicSampleBuilder(IPackageFragmentRoot root, IPackageFragment projectPackage, SourceWriterFactory sourceWriterFactory) throws JavaModelException {
		this.root = root;
		this.javaProject = root.getJavaProject();
		this.clientPackage = javaProject.findPackageFragment(projectPackage.getPath().append("client"));
		this.serverPackage = javaProject.findPackageFragment(projectPackage.getPath().append("server"));
		this.sharedPackage = javaProject.findPackageFragment(projectPackage.getPath().append("shared"));
		this.sourceWriterFactory = sourceWriterFactory;
	}

	public void createSample(Ginjector ginjector, PresenterModule presenterModule, Tokens tokens, PlaceAnnotation defaultPlace, HandlerModule handlerModule) {
		Presenter mainPagePresenter = null;
		Presenter responsePresenter = null;
		View mainPageView = null;
		View responseView = null;
		Action sendTextToServerAction = null;
		Result sendTextToServerResult = null;
		ActionHandler sendTextToServerActionHandler = null;

		try {
			ginjector.becomeWorkingCopy();
			presenterModule.becomeWorkingCopy();
			tokens.becomeWorkingCopy();
			handlerModule.becomeWorkingCopy();

			IType revealEvent = javaProject.findType("com.gwtplatform.mvp.client.proxy.RevealRootContentEvent");
			IPackageFragment corePackage = root.createPackageFragment(clientPackage.getElementName() + ".core", false, null);

			// MainPage
			mainPagePresenter = new Presenter(root, corePackage.getElementName(), "MainPagePresenter", sourceWriterFactory, false);
			IType viewInterface = mainPagePresenter.createViewInterface();
			mainPagePresenter.createProxyPlaceInterface(true, tokens.getType(), "main");
			IMethod constructor = mainPagePresenter.createConstructor();
			mainPagePresenter.createRevealInParentMethod(revealEvent);
			mainPagePresenter.createImport("com.google.gwt.event.dom.client.ClickEvent");
			mainPagePresenter.createImport("com.google.gwt.event.dom.client.ClickHandler");
			mainPagePresenter.createImport("com.google.gwt.event.dom.client.HasClickHandlers");
			mainPagePresenter.createImport("com.google.gwt.user.client.ui.HasValue");
			mainPagePresenter.createImport("com.gwtplatform.mvp.client.proxy.PlaceManager");
			mainPagePresenter.createImport("com.gwtplatform.mvp.client.proxy.PlaceRequest");
			mainPagePresenter.createImport(sharedPackage.getElementName() + ".FieldVerifier");

			viewInterface.createMethod("HasValue<String> getNameValue();", null, false, null);
			viewInterface.createMethod("HasClickHandlers getSendClickHandlers();", null, false, null);
			viewInterface.createMethod("void resetAndFocus();", null, false, null);
			viewInterface.createMethod("void setError(String errorText);", null, false, null);

			mainPagePresenter.getType().createField("private final PlaceManager placeManager;", constructor, false, null);

			SourceWriter sw = sourceWriterFactory.createForMethod(constructor);
			sw.appendParameter("final PlaceManager placeManager");
			sw.writeLine("this.placeManager = placeManager;");
			sw.commit(mainPagePresenter.getBuffer());

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override",
					"protected void onBind() {",
					"  super.onBind();", 
					"  registerHandler(getView().getSendClickHandlers().addClickHandler(new ClickHandler() {", 
					"    @Override", 
					"    public void onClick(ClickEvent event) {", 
					"      sendNameToServer();", 
					"    }", 
					"  }));", 
					"}");
			mainPagePresenter.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override",
					"protected void onReset() {",
					"  super.onReset();", 
					"  getView().resetAndFocus();",
					"}");
			mainPagePresenter.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"/**", 
					" * Send the name from the nameField to the server and wait for a response.", 
					" */", 
					"private void sendNameToServer() {", 
					"  // First, we validate the input.", 
					"  getView().setError(\"\");", 
					"  String textToServer = getView().getNameValue().getValue();", 
					"  if (!FieldVerifier.isValidName(textToServer)) {", 
					"    getView().setError(\"Please enter at least four characters\");", 
					"    return;", 
					"  }", 
					"  ", 
					"  // Then, we transmit it to the ResponsePresenter, which will do the server call", 
					"  placeManager.revealPlace(new PlaceRequest(NameTokens.response).with(ResponsePresenter.textToServerParam, textToServer));", 
					"}");
			mainPagePresenter.createMethod(sw);

			mainPageView = new View(root, corePackage.getElementName(), "MainPageView", sourceWriterFactory, mainPagePresenter.getType(), false);
			mainPageView.createImport("com.google.gwt.event.dom.client.HasClickHandlers");
			mainPageView.createImport("com.google.gwt.user.client.ui.Button");
			mainPageView.createImport("com.google.gwt.user.client.ui.HasValue");
			mainPageView.createImport("com.google.gwt.user.client.ui.HTMLPanel");
			mainPageView.createImport("com.google.gwt.user.client.ui.Label");
			mainPageView.createImport("com.google.gwt.user.client.ui.TextBox");
			mainPageView.createImport("com.google.gwt.user.client.ui.Widget");
			constructor = mainPageView.createConstructor(false);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"private static String html = \"<h1>Web Application Starter Project</h1>\\n\"", 
					"    + \"<table align=\\\"center\\\">\\n\"", 
					"    + \"  <tr>\\n\"", 
					"    + \"    <td colspan=\\\"2\\\" style=\\\"font-weight:bold;\\\">Please enter your name:</td>\\n\"", 
					"    + \"  </tr>\\n\"", 
					"    + \"  <tr>\\n\"", 
					"    + \"    <td id=\\\"nameFieldContainer\\\"></td>\\n\"", 
					"    + \"    <td id=\\\"sendButtonContainer\\\"></td>\\n\"", 
					"    + \"  </tr>\\n\"", 
					"    + \"  <tr>\\n\"", 
					"    + \"    <td colspan=\\\"2\\\" style=\\\"color:red;\\\" id=\\\"errorLabelContainer\\\"></td>\\n\"", 
					"    + \"  </tr>\\n\"", 
					"    + \"</table>\\n\";");
			mainPageView.createField(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLine("private final HTMLPanel panel = new HTMLPanel(html);");
			mainPageView.createField(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLine("private final Label errorLabel;");
			mainPageView.createField(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLine("private final TextBox nameField;");
			mainPageView.createField(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLine("private final Button sendButton;");
			mainPageView.createField(sw);

			sw = sourceWriterFactory.createForMethod(constructor);
			sw.writeLines(
					"sendButton = new Button(\"Send\");", 
					"nameField = new TextBox();", 
					"nameField.setText(\"GWT User\");", 
					"errorLabel = new Label();", 
					"", 
					"// We can add style names to widgets", 
					"sendButton.addStyleName(\"sendButton\");", 
					"", 
					"// Add the nameField and sendButton to the RootPanel", 
					"// Use RootPanel.get() to get the entire body element", 
					"panel.add(nameField, \"nameFieldContainer\");", 
					"panel.add(sendButton, \"sendButtonContainer\");", 
					"panel.add(errorLabel, \"errorLabelContainer\");");
			sw.commit(mainPageView.getBuffer());

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public Widget asWidget() {", 
					"  return panel;", 
					"}");
			mainPageView.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public HasValue<String> getNameValue() {", 
					"  return nameField;", 
					"}");
			mainPageView.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public HasClickHandlers getSendClickHandlers() {", 
					"  return sendButton;", 
					"}");
			mainPageView.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public void resetAndFocus() {", 
					"  // Focus the cursor on the name field when the app loads", 
					"  nameField.setFocus(true);", 
					"  nameField.selectAll();", 
					"}");
			mainPageView.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public void setError(String errorText) {", 
					"  errorLabel.setText(errorText);", 
					"}");
			mainPageView.createMethod(sw);

			IField mainPageToken = tokens.createTokenField("main");
			tokens.createTokenGetter("main");

			ginjector.createProvider(mainPagePresenter.getType());

			presenterModule.createPresenterBinder(mainPagePresenter.getType(), mainPageView.getType());
			presenterModule.createConstantBinder(defaultPlace.getType(), tokens.getType(), mainPageToken);

			// Response
			responsePresenter = new Presenter(root, corePackage.getElementName(), "ResponsePresenter", sourceWriterFactory, false);
			viewInterface = responsePresenter.createViewInterface();
			responsePresenter.createProxyPlaceInterface(true, tokens.getType(), "response");
			constructor = responsePresenter.createConstructor();
			responsePresenter.createRevealInParentMethod(revealEvent);
			responsePresenter.createImport("com.google.gwt.event.dom.client.ClickEvent");
			responsePresenter.createImport("com.google.gwt.event.dom.client.ClickHandler");
			responsePresenter.createImport("com.google.gwt.event.dom.client.HasClickHandlers");
			responsePresenter.createImport("com.google.gwt.user.client.rpc.AsyncCallback");
			responsePresenter.createImport("com.gwtplatform.dispatch.shared.DispatchAsync");
			responsePresenter.createImport("com.gwtplatform.mvp.client.proxy.PlaceManager");
			responsePresenter.createImport("com.gwtplatform.mvp.client.proxy.PlaceRequest");
			responsePresenter.createImport(sharedPackage.getElementName() + ".SendTextToServer");
			responsePresenter.createImport(sharedPackage.getElementName() + ".SendTextToServerResult");

			viewInterface.createMethod("HasClickHandlers getCloseClickHandlers();", null, false, null);
			viewInterface.createMethod("void setServerResponse(String serverResponse);", null, false, null);
			viewInterface.createMethod("void setTextToServer(String textToServer);", null, false, null);

			responsePresenter.getType().createField("public static final String textToServerParam = \"textToServer\";", constructor, false, null);
			responsePresenter.getType().createField("private final DispatchAsync dispatcher;", constructor, false, null);
			responsePresenter.getType().createField("private final PlaceManager placeManager;", constructor, false, null);
			responsePresenter.getType().createField("private String textToServer;", constructor, false, null);

			sw = sourceWriterFactory.createForMethod(constructor);
			sw.appendParameter("final DispatchAsync dispatcher");
			sw.appendParameter("final PlaceManager placeManager");
			sw.writeLine("this.dispatcher = dispatcher;");
			sw.writeLine("this.placeManager = placeManager;");
			sw.commit(responsePresenter.getBuffer());

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override",
					"public void prepareFromRequest(PlaceRequest request) {",
					"  super.prepareFromRequest(request);", 
					"  textToServer = request.getParameter(textToServerParam, null);", 
					"}");
			responsePresenter.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override",
					"protected void onBind() {",
					"  super.onBind();", 
					"  registerHandler(getView().getCloseClickHandlers().addClickHandler(new ClickHandler() {", 
					"    @Override", 
					"    public void onClick(ClickEvent event) {", 
					"      placeManager.revealPlace(new PlaceRequest(NameTokens.main));", 
					"    }", 
					"  }));", 
					"}");
			responsePresenter.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override",
					"protected void onReset() {",
					"  super.onReset();", 
					"  getView().setTextToServer(textToServer);",
					"  getView().setServerResponse(\"Waiting for response...\");",
					"  dispatcher.execute(new SendTextToServer(textToServer), new AsyncCallback<SendTextToServerResult>() {",
					"    @Override",
					"    public void onFailure(Throwable caught) {",
					"      getView().setServerResponse(\"An error occured: \" + caught.getMessage());",
					"    }",
					"  ",
					"    @Override",
					"    public void onSuccess(SendTextToServerResult result) {",
					"      getView().setServerResponse(result.getResponse());",
					"    }",
					"  });",
					"}");
			responsePresenter.createMethod(sw);

			responseView = new View(root, corePackage.getElementName(), "ResponseView", sourceWriterFactory, responsePresenter.getType(), false);
			responseView.createImport("com.google.gwt.event.dom.client.HasClickHandlers");
			responseView.createImport("com.google.gwt.user.client.ui.Button");
			responseView.createImport("com.google.gwt.user.client.ui.HTMLPanel");
			responseView.createImport("com.google.gwt.user.client.ui.HTML");
			responseView.createImport("com.google.gwt.user.client.ui.Label");
			responseView.createImport("com.google.gwt.user.client.ui.Widget");
			constructor = responseView.createConstructor(false);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"private static String html = \"<h1>Remote Procedure Call</h1>\\n\"", 
					"    + \"<table align=\\\"center\\\">\\n\"", 
					"    + \"  <tr>\\n\"", 
					"    + \"    <td style=\\\"font-weight:bold;\\\">Sending name to server:</td>\\n\"", 
					"    + \"  </tr>\\n\"", 
					"    + \"  <tr>\\n\"", 
					"    + \"    <td id=\\\"textToServerContainer\\\"></td>\\n\"", 
					"    + \"  </tr>\\n\"", 
					"    + \"  <tr>\\n\"", 
					"    + \"    <td style=\\\"font-weight:bold;\\\">Server replies:</td>\\n\"", 
					"    + \"  </tr>\\n\"", 
					"    + \"  <tr>\\n\"", 
					"    + \"    <td id=\\\"serverResponseContainer\\\"></td>\\n\"", 
					"    + \"  </tr>\\n\"", 
					"    + \"  <tr>\\n\"", 
					"    + \"    <td id=\\\"closeButton\\\"></td>\\n\"", 
					"    + \"  </tr>\\n\"", 
					"    + \"</table>\\n\";");
			responseView.createField(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLine("private final HTMLPanel panel = new HTMLPanel(html);");
			responseView.createField(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLine("private final Label textToServerLabel;");
			responseView.createField(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLine("private final HTML serverResponseLabel;");
			responseView.createField(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLine("private final Button closeButton;");
			responseView.createField(sw);

			sw = sourceWriterFactory.createForMethod(constructor);
			sw.writeLines(
					"closeButton = new Button(\"Close\");", 
					"// We can set the id of a widget by accessing its Element", 
					"closeButton.getElement().setId(\"closeButton\");", 
					"textToServerLabel = new Label();", 
					"serverResponseLabel = new HTML();", 
					"", 
					"// Add the nameField and sendButton to the RootPanel", 
					"// Use RootPanel.get() to get the entire body element", 
					"panel.add(closeButton, \"closeButton\");", 
					"panel.add(textToServerLabel, \"textToServerContainer\");", 
					"panel.add(serverResponseLabel, \"serverResponseContainer\");");
			sw.commit(responseView.getBuffer());

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public Widget asWidget() {", 
					"  return panel;", 
					"}");
			responseView.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public HasClickHandlers getCloseClickHandlers() {", 
					"  return closeButton;", 
					"}");
			responseView.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public void setServerResponse(String serverResponse) {", 
					"  serverResponseLabel.setHTML(serverResponse);", 
					"}");
			responseView.createMethod(sw);

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override", 
					"public void setTextToServer(String textToServer) {", 
					"  textToServerLabel.setText(textToServer);", 
					"}");
			responseView.createMethod(sw);

			tokens.createTokenField("response");
			tokens.createTokenGetter("response");

			ginjector.createProvider(responsePresenter.getType());

			presenterModule.createPresenterBinder(responsePresenter.getType(), responseView.getType());

			// SendTextToServer
			sendTextToServerResult = new Result(root, sharedPackage.getElementName(), "SendTextToServerResult", sourceWriterFactory);
			sendTextToServerResult.createSerializationConstructor();

			IField responseField = sendTextToServerResult.createField("String", "response");
			sendTextToServerResult.createConstructor(responseField);
			sendTextToServerResult.createGetterMethod(responseField);

			IType actionSuperclass = javaProject.findType("com.gwtplatform.dispatch.shared.UnsecuredActionImpl");

			sendTextToServerAction = new Action(root, sharedPackage.getElementName(), "SendTextToServer", sourceWriterFactory, actionSuperclass, sendTextToServerResult.getType());
			sendTextToServerAction.createSerializationConstructor();

			IField textToServerField = sendTextToServerAction.createField("String", "textToServer");
			sendTextToServerAction.createConstructor(textToServerField);
			sendTextToServerAction.createGetterMethod(textToServerField);

			sendTextToServerActionHandler = new ActionHandler(root, serverPackage.getElementName(), "SendTextToServerActionHandler", sourceWriterFactory, sendTextToServerAction.getType(), sendTextToServerResult.getType());
			constructor = sendTextToServerActionHandler.createConstructor();
			sendTextToServerActionHandler.createImport("javax.servlet.ServletContext");
			sendTextToServerActionHandler.createImport("javax.servlet.http.HttpServletRequest");
			sendTextToServerActionHandler.createImport("com.google.inject.Provider");
			sendTextToServerActionHandler.createImport("com.gwtplatform.dispatch.server.ExecutionContext");
			sendTextToServerActionHandler.createImport("com.gwtplatform.dispatch.shared.ActionException");
			sendTextToServerActionHandler.createImport(sharedPackage.getElementName() + ".FieldVerifier");

			sendTextToServerActionHandler.getType().createField("private final ServletContext servletContext;", constructor, false, null);
			sendTextToServerActionHandler.getType().createField("private final Provider<HttpServletRequest> requestProvider;", constructor, false, null);

			sw = sourceWriterFactory.createForMethod(constructor);
			sw.appendParameter("final ServletContext servletContext");
			sw.appendParameter("final Provider<HttpServletRequest> requestProvider");
			sw.writeLine("this.servletContext = servletContext;");
			sw.writeLine("this.requestProvider = requestProvider;");
			sw.commit(sendTextToServerActionHandler.getBuffer());

			sw = sourceWriterFactory.createForNewClassBodyComponent();
			sw.writeLines(
					"@Override",
					"public SendTextToServerResult execute(SendTextToServer action, ExecutionContext context) throws ActionException {",
					"  String input = action.getTextToServer();", 
					"  ", 
					"  // Verify that the input is valid.", 
					"  if (!FieldVerifier.isValidName(input)) {", 
					"    // If the input is not valid, throw an IllegalArgumentException back to", 
					"    // the client.", 
					"    throw new ActionException(\"Name must be at least 4 characters long\");", 
					"  }", 
					"  ", 
					"  String serverInfo = servletContext.getServerInfo();", 
					"  String userAgent = requestProvider.get().getHeader(\"User-Agent\");", 
					"  return new SendTextToServerResult(\"Hello, \" + input + \"!<br><br>I am running \" + serverInfo + \".<br><br>It looks like you are using:<br>\" + userAgent);", 
					"}");
			sendTextToServerActionHandler.createMethod(sw);

			sendTextToServerActionHandler.createUndoMethod(sendTextToServerAction.getType(), sendTextToServerResult.getType());
			sendTextToServerActionHandler.createActionTypeGetterMethod(sendTextToServerAction.getType());

			handlerModule.createBinder(sendTextToServerAction.getType(), sendTextToServerActionHandler.getType());

			FieldVerifier fieldVerifier = new FieldVerifier(root, sharedPackage.getElementName(), sourceWriterFactory);

			// Commit
			ginjector.commit();
			presenterModule.commit();
			tokens.commit();
			handlerModule.commit();
			
			mainPagePresenter.commit();
			mainPageView.commit();
			responsePresenter.commit();
			responseView.commit();
			sendTextToServerAction.commit();
			sendTextToServerResult.commit();
			sendTextToServerActionHandler.commit();
			fieldVerifier.commit();
		}
		catch (Exception e) {
			try {
				ginjector.discard(false);
				presenterModule.discard(false);
				tokens.discard(false);
				handlerModule.discard(false);
				
				if (mainPagePresenter != null) {
					mainPagePresenter.discard(true);
				}
				if (mainPageView != null) {
					mainPageView.discard(true);
				}
				if (responsePresenter != null) {
					responsePresenter.discard(true);
				}
				if (responseView != null) {
					responseView.discard(true);
				}
				if (sendTextToServerAction != null) {
					sendTextToServerAction.discard(true);
				}
				if (sendTextToServerResult != null) {
					sendTextToServerResult.discard(true);
				}
				if (sendTextToServerActionHandler != null) {
					sendTextToServerActionHandler.discard(true);
				}
			} catch (Exception e1) { 
			}
		}
	}

	class FieldVerifier extends ProjectClass {

		public FieldVerifier(IPackageFragmentRoot root, String packageName, SourceWriterFactory sourceWriterFactory) throws JavaModelException {
			super(root, packageName, "FieldVerifier", sourceWriterFactory);
			init();
		}

		@Override
		protected IType createType() throws JavaModelException {
			SourceWriter sw = sourceWriterFactory.createForNewClass();
			sw.writeLines(
					"/**", 
					" * <p>", 
					" * FieldVerifier validates that the name the user enters is valid.", 
					" * </p>", 
					" * <p>", 
					" * This class is in the <code>shared</code> packing because we use it in both", 
					" * the client code and on the server. On the client, we verify that the name is", 
					" * valid before sending an RPC request so the user doesn't have to wait for a", 
					" * network round trip to get feedback. On the server, we verify that the name is", 
					" * correct to ensure that the input is correct regardless of where the RPC", 
					" * originates.", 
					" * </p>", 
					" * <p>", 
					" * When creating a class that is used on both the client and the server, be sure", 
					" * that all code is translatable and does not use native JavaScript. Code that", 
					" * is note translatable (such as code that interacts with a database or the file", 
					" * system) cannot be compiled into client side JavaScript. Code that uses native", 
					" * JavaScript (such as Widgets) cannot be run on the server.", 
					" * </p>", 
					" */", 
					"public class FieldVerifier {", 
					"", 
					"  /**", 
					"   * Verifies that the specified name is valid for our service.", 
					"   * ", 
					"   * In this example, we only require that the name is at least four characters.", 
					"   * In your application, you can use more complex checks to ensure that", 
					"   * usernames, passwords, email addresses, URLs, and other fields have the", 
					"   * proper syntax.", 
					"   * ", 
					"   * @param name the name to validate", 
					"   * @return true if valid, false if invalid", 
					"   */", 
					"  public static boolean isValidName(String name) {", 
					"    if (name == null) {", 
					"      return false;", 
					"    }", 
					"    return name.length() > 3;", 
					"  }", 
					"}");

			return workingCopy.createType(sw.toString(), null, false, null);
		}

	}

}
