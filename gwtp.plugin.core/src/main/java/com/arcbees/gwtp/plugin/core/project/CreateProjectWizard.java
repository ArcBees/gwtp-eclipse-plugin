package com.arcbees.gwtp.plugin.core.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.arcbees.gwtp.plugin.core.Activator;
import com.arcbees.gwtp.plugin.core.StupidVelocityShim;

public class CreateProjectWizard extends Wizard implements INewWizard {
    private IWorkbench fWorkbench;
    private Object fSelection;

    public CreateProjectWizard() {
        setWindowTitle("Create GWTP Project");
        setHelpAvailable(false);
    }

    @Override
    public void addPages() {
        addPage(NameProjectPage.get());
        addPage(AddFeaturesPage.get());
        addPage(FeatureConfigPage.get());
    }


    private void createProject(final IProgressMonitor monitor) throws Exception {
        StupidVelocityShim.setStripUnknownKeys(false);
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(NameProjectPage.get().getProjectName());
        project.create(monitor);
        project.open(monitor);
        Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, Status.OK, "Creating Project 1", null));
        if (!project.getLocation().equals(NameProjectPage.get().getLocation())) {
            final IProjectDescription description = project.getDescription();
            description.setLocation(NameProjectPage.get().getLocation());
            project.setDescription(description, monitor);
        }

        final Map<String, Object> context = new HashMap<>();
        context.put("mavenGroup", NameProjectPage.get().getMavenGroup());
        context.put("mavenArtifactId", NameProjectPage.get().getMavenArtifactId());
        context.put("projectName", NameProjectPage.get().getProjectName());
        context.put("packageName", NameProjectPage.get().getPackageName());
        context.put("packageFolder", NameProjectPage.get().getPackageName().replace(".", "/"));

        AddFeaturesPage.get().fillContext(context);
        FeatureConfigPage.get().fillContext(context);


        try {
            final ZipInputStream projectTemplate = new ZipInputStream(getClass().getResourceAsStream("/src/main/resources/templates/project/project.zip"));
            ZipEntry entry;
            String lastFolderPath = "";
            while ((entry = projectTemplate.getNextEntry()) != null) {
                String path = entry.getName();

                if (!path.isEmpty()) {
                    path = StupidVelocityShim.evaluate(path, context);

                    System.out.println(path);
                    if (path.endsWith("template")) {
                        path = path.substring(0, path.length() - "template.".length());

                        @SuppressWarnings("resource") final Scanner sc = new Scanner(projectTemplate);
                        final StringBuilder sb = new StringBuilder();
                        while (sc.hasNextLine()) {
                            sb.append(sc.nextLine()).append("\n");
                        }
                        projectTemplate.closeEntry();

                        final String template = StupidVelocityShim.evaluate(sb.toString(), context);
                        if (!template.isEmpty()) {
                            final IFile file = project.getFile(new Path(path));
                            file.create(new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8)), IResource.NONE, null);
                        }


                    } else {
                        final String[] last = lastFolderPath.split("/");
                        final String[] current = path.split("/");
                        if (current.length > last.length) {
                            final StringBuilder pathBuilder = new StringBuilder();
                            for (int i = 0; i < current.length; i++) {
                                pathBuilder.append(current[i]).append("/");
                                if (last.length <= i) {
                                    project.getFolder(new Path(pathBuilder.toString())).create(true, true, monitor);
                                }

                            }
                        } else {
                            project.getFolder(new Path(path)).create(true, true, monitor);
                        }

                        lastFolderPath = path;
                    }
                }
            }
            projectTemplate.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        final IProjectConfigurationManager mavenConfig = MavenPlugin.getProjectConfigurationManager();
        final ResolverConfiguration resolverConfiguration = mavenConfig.getResolverConfiguration(project);
        mavenConfig.enableMavenNature(project, resolverConfiguration, monitor);
    }

    private void generate(final IProgressMonitor monitor) {
        try {

            createProject(monitor);
        } catch (final Exception e) {
            warn("Could not create gwtp project. Error: " + e.toString());
            e.printStackTrace();
        }

    }

    public IWorkbench getWorkbench() {
        return fWorkbench;
    }

    @Override
    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
        fWorkbench= workbench;
        fSelection= selection;

    }

    @Override
    public boolean performFinish() {
        runGenerate();
        return true;
    }

    public void runGenerate() {
        final Job job = new Job("Generate Project") {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                generate(monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    protected void selectAndReveal(final IResource newResource) {
        BasicNewResourceWizard.selectAndReveal(newResource, fWorkbench.getActiveWorkbenchWindow());
    }

    private void warn(final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openWarning(getShell(), "Warning", message);
            }
        });
    }

}
