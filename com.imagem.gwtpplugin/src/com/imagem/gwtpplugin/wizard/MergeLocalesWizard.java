/**
 * Copyright 2011 IMAGEM Solutions TI santé
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imagem.gwtpplugin.wizard;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Michael Renaud
 */
public class MergeLocalesWizard extends Wizard implements INewWizard {

  private static final String DEPRACATED_COMMENT = "# TODO: DEPRECATED (CONSIDER REMOVING)\n";
  private static final String TRANSLATE_COMMENT = "# TODO: TRANSLATE\n";
  private static final String CONFIRM_COMMENT = "# TODO: CONFIRM TRANSLATION (DESCRIPTION CHANGED)\n";

  private MergeLocalesWizardPage page;
  private IStructuredSelection selection;
  private boolean isDone;

  private boolean deprecatedCommentIssued;
  private boolean translateCommentIssued;
  private boolean confirmCommentIssued;

  public MergeLocalesWizard() {
    super();
    setNeedsProgressMonitor(true);
    setWindowTitle("Merge Locales");
  }

  @Override
  public void addPages() {
    page = new MergeLocalesWizardPage(selection);
    addPage(page);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.selection = selection;
  }

  @Override
  public boolean performFinish() {
    try {
      super.getContainer().run(false, false, new IRunnableWithProgress() {
        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
          isDone = finish(monitor);
        }
      });
    } catch (Exception e) {
      return false;
    }
    return isDone;
  }

  /**
   * Takes every property file in extrasDir and merge them to the default locale
   * file in resourcesDir. Then it merges this resource file with every other
   * non-default local file in the directory.
   *
   * @return true on success, false on exception.
   */
  protected boolean finish(IProgressMonitor desiredMonitor) {
    IProgressMonitor monitor = desiredMonitor;
    if (monitor == null) {
      monitor = new NullProgressMonitor();
    }

    try {
      IContainer extrasDir = page.getExtraDir();
      IContainer resourcesDir = page.getResourcesDir();

      List<IResource> extraResources = enumeratePropertyFiles(extrasDir);
      PropertyCollection incomingProperties = new PropertyCollection();
      for (IResource extraResource : extraResources) {
        File extraFile = new File(extraResource.getLocationURI());
        if (findLocale(extraFile).isEmpty()) {
          PropertyCollection newProperties = new PropertyCollection();
          newProperties.readPropertiesFromFile(extraFile);
          incomingProperties.mergeWith(newProperties, false, false);
        }
      }

      List<IResource> resourceResources = enumeratePropertyFiles(resourcesDir);
      IResource defaultLocalResource = null;
      File defaultLocaleFile = null;
      for (IResource resourceResource : resourceResources) {
        File resourceFile = new File(resourceResource.getLocationURI());
        if (findLocale(resourceFile).isEmpty()) {
          defaultLocalResource = resourceResource;
          defaultLocaleFile = resourceFile;
          break;
        }
      }

      PropertyCollection defaultLocaleProperties = new PropertyCollection();
      defaultLocaleProperties.readPropertiesFromFile(defaultLocaleFile);
      defaultLocaleProperties.mergeWith(incomingProperties, true, false);

      IFile file = (IFile) defaultLocalResource.getAdapter(IFile.class);
      file.setContents(new ByteArrayInputStream(defaultLocaleProperties.toString().getBytes()),
          false, true, null);

      for (IResource resourceResource : resourceResources) {
        File resourceFile = new File(resourceResource.getLocationURI());
        if (!findLocale(resourceFile).isEmpty()) {
          PropertyCollection otherLocaleProperties = new PropertyCollection();
          otherLocaleProperties.readPropertiesFromFile(resourceFile);
          otherLocaleProperties.mergeWith(defaultLocaleProperties, true, true);

          file = (IFile) resourceResource.getAdapter(IFile.class);
          file.setContents(new ByteArrayInputStream(otherLocaleProperties.toString().getBytes()),
              false, true, null);
        }
      }

      // TODO
      /*
       * if deprecatedCommentIssued: print(
       * "Deprecated translations found. Look for: '%s'." %
       * deprecatedComment.strip() ) if confirmCommentIssued: print(
       * "Some translations could require confirmation. Look for: '%s'." %
       * confirmComment.strip() ) if translateCommentIssued: print(
       * "Some properties need to be translated. Look for: '%s'." %
       * translateComment.strip() )
       */
    } catch (Exception e) {
      e.printStackTrace();

      return false;
    }
    return true;
  }

  /**
   * Looks in the specified directory for all property files, that is, files
   * ending in .properties.
   *
   * @param dir
   *          The directory to look in.
   * @return A list of files.
   * @throws CoreException
   */
  protected List<IResource> enumeratePropertyFiles(IContainer dir) throws CoreException {
    /*
     * List<File> propertyFiles = new ArrayList<File>(); IResource[] resources =
     * dir.members(IResource.FILE);
     *
     * for(IResource resource : resources) {
     * if(resource.getName().endsWith(".properties")) propertyFiles.add(new
     * File(resource.getLocationURI())); }
     *
     * return propertyFiles;
     */

    List<IResource> propertyResources = new ArrayList<IResource>();
    IResource[] resources = dir.members(IResource.FILE);

    for (IResource resource : resources) {
      if (resource.getName().endsWith(".properties")) {
        propertyResources.add(resource);
      }
    }

    return propertyResources;
  }

  /**
   * Identifies the locale given the filename of a property file. For example
   * file_fr.properties will return fr. If there is no locale in the filename,
   * returns the empty string.
   *
   * @param file
   *          The file from which to extract the locale.
   * @return The locale or the empty string for the default locale.
   */
  protected String findLocale(File file) {
    String name[] = file.getName().split("_");
    if (name.length == 1) {
      return ""; // Default locale when none is found
    }
    String local[] = name[name.length - 1].split("\\.");
    if (local[0].equals("default")) {
      return "";
    }
    return local[0];
  }

  /**
   * A property is a single element of translation.
   */
  public class Property {
    private String comments;
    private String key;
    private String value;

    public Property() {
      comments = null;
      key = null;
      value = null;
    }

    /**
     * Get the next property from a file. Throws a InvalidProperty exception if
     * the property is not correctly formatted. This method will replace the
     * comments, key and value of this Property object.
     *
     * @param reader
     *          The file object to read from.
     * @return true on success, false on EOF.
     */
    public boolean getFromFile(BufferedReader reader) {
      try {
        // Skip blank lines. Return false if EOF is reached
        String line = reader.readLine();
        if (line == null) {
          return false;
        }
        while (line.isEmpty()) {
          line = reader.readLine();
          if (line == null) {
            return false;
          }
        }

        // Read the comment block
        comments = "";
        while (line.startsWith("#")) {
          comments += line + "\n";
          line = reader.readLine();
        }

        // Read the key/value
        int index = line.indexOf("=");
        if (index < 0) {
          // Comment-only property
          return true;
        }
        key = line.substring(0, index);
        value = line.substring(index + 1);
        while (value.endsWith("\\\n")) {
          value += reader.readLine();
        }

        return true;
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }

    /**
     * Check if this property is marked as deprecated.
     *
     * @return true if it is marked as deprecated, false otherwise.
     */
    public boolean isDeprecated() {
      return comments.contains(DEPRACATED_COMMENT);
    }

    /**
     * Ensures that this property is not marked as deprecated, by removing any
     * such comment.
     */
    public void unsetDeprecated() {
      if (isDeprecated()) {
        comments.replaceAll(DEPRACATED_COMMENT, "");
      }
    }

    /**
     * Ensures that this property is marked as deprecated, by including an
     * appropriate comment.
     */
    public void setDeprecated() {
      if (!comments.contains(DEPRACATED_COMMENT)) {
        comments += DEPRACATED_COMMENT;
      }
      deprecatedCommentIssued = true;
    }

    /**
     * Ensures that this property indicates that it requires translation, by
     * including an appropriate comment.
     */
    public void setTranslationNeeded() {
      if (!comments.contains(TRANSLATE_COMMENT)) {
        comments += TRANSLATE_COMMENT;
      }
      translateCommentIssued = true;
    }

    /**
     * Ensures that this property indicates that its translation should be
     * confirmed, by including an appropriate comment.
     */
    public void setConfirmTranslation() {
      if (!comments.contains(CONFIRM_COMMENT)) {
        comments += CONFIRM_COMMENT;
      }
      confirmCommentIssued = true;
    }

    /**
     * Check that the comment matches between both properties. The comments are
     * considered to match if they are exactly the same when the "# TODO:"
     * comments are removed.
     *
     * @param otherProperty
     *          The other property with which to compare.
     * @return true if the comment matches, False otherwise.
     */
    public boolean commentMatchs(Property otherProperty) {
      return stripToDoComments().equals(otherProperty.stripToDoComments());
    }

    /**
     * Returns a copy of the comments without the TODO comments.
     *
     * @return The stripped result.
     */
    public String stripToDoComments() {
      String currentComments = this.comments;

      String[] commentsToStrip = { DEPRACATED_COMMENT, TRANSLATE_COMMENT, CONFIRM_COMMENT };

      for (String commentToStrip : commentsToStrip) {
        int index = currentComments.indexOf(commentToStrip);
        while (index != -1) {
          currentComments = currentComments.substring(0, index)
              + currentComments.substring(index + commentToStrip.length());
          index = currentComments.indexOf(commentToStrip);
        }
      }

      return currentComments;
    }

    /**
     * Clone this object.
     *
     * @return The cloned object
     */
    public Property clone() {
      Property clone = new Property();
      clone.setComments(comments);
      clone.setKey(key);
      clone.setValue(value);
      return clone;
    }

    @Override
    public String toString() {
      String str = comments;
      if (key != null) {
        str += key + "=" + value + "\n\n";
      }
      return str;
    }

    public void setComments(String comments) {
      this.comments = comments;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public String getComments() {
      return comments;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }
  }

  /**
   * A collection of Property object that can be merged or written to files.
   */
  public class PropertyCollection {
    public Map<String, Property> properties;
    public List<Property> orderedProperties;

    public PropertyCollection() {
      properties = new HashMap<String, Property>();
      orderedProperties = new ArrayList<Property>();
    }

    /**
     * Adds an object of type Property to the collection.
     *
     * @param property
     *          The Property object to add
     */
    public void add(Property property) {
      properties.put(property.getKey(), property);
      orderedProperties.add(property);
    }

    public Property get(String key) {
      return properties.get(key);
    }

    /**
     * Read all the properties from a given file.
     *
     * @param file
     *          The file to read properties from.
     */
    public void readPropertiesFromFile(File file) {
      try {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        while (true) {
          Property property = new Property();
          if (!property.getFromFile(reader)) {
            break;
          }
          property.unsetDeprecated();
          add(property);
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    /**
     * Merge this collection with another one. Any key that is found in the
     * other collection but not in this one will be added. If the markDeprecated
     * parameter is true, any key that is found in this collection but not in
     * the other one will be marked as deprecated. If the markTranslation
     * parameter is true, comments will be added to indicate translations that
     * need to be performed.
     *
     * @param otherCollection
     *          The collection to merge into this one.
     * @param markDeprecated
     *          true if deprecated translations should be indicated, false
     *          otherwise.
     * @param markTranslation
     *          true means that "# TODO: TRANSLATE" and
     *          "# TODO: CONFIRM TRANSLATION" comments will be added when
     *          needed.
     */
    public void mergeWith(PropertyCollection otherCollection, boolean markDeprecated,
        boolean markTranslation) {
      // Bring properties over
      for (Property property : otherCollection.getProperties()) {
        if (property.getKey() == null) {
          continue; // Don't merge comment-only properties
        }
        if (!containsKey(property.getKey())) {
          Property propertyCopy = property.clone();
          add(propertyCopy);
          if (markTranslation) {
            propertyCopy.setTranslationNeeded();
          }
        } else if (!property.getComments().isEmpty()) {
          // Empty comments mean non-UIBinder translations OR deprecated
          // translations, skip them.
          // Non-empty comments are copied over, with a confirmation comment if
          // requested.
          Property currentProperty = get(property.getKey());
          if (!currentProperty.commentMatchs(property)) {
            currentProperty.setComments(property.getComments());
            if (markTranslation) {
              currentProperty.setConfirmTranslation();
            }
          }
        }
      }

      // Mark deprecated properties
      if (markDeprecated) {
        for (Property property : getProperties()) {
          if (property.getKey() == null) {
            continue; // Don't consider comment-only properties
          }
          if (!otherCollection.containsKey(property.getKey())) {
            property.setDeprecated();
          }
        }
      }
    }

    private boolean containsKey(String key) {
      return properties.containsKey(key);
    }

    public Collection<Property> getProperties() {
      // return properties.values();
      return orderedProperties;
    }

    @Override
    public String toString() {
      String str = "";
      for (Property property : getProperties()) {
        str += property.toString();
      }
      return str;
    }
  }

}
