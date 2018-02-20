/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.languageserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.eclipse.lsp4j.Location;

/** Language service service utilities */
public class LanguageServiceUtils {

  private static final String PROJECTS = "/projects";
  private static final String FILE_PROJECTS = "file:///projects";

  public static String prefixURI(String uri) {
    return uri.startsWith("/") ? FILE_PROJECTS + uri : uri;
  }

  public static String removePrefixUri(String uri) {
    return uri.startsWith(FILE_PROJECTS) ? uri.substring(FILE_PROJECTS.length()) : uri;
  }

  public static List<String> removePrefixUri(List<String> uris) {
    return uris.stream().map(LanguageServiceUtils::removePrefixUri).collect(Collectors.toList());
  }

  public static String removeUriScheme(String uri) {
    return uri.startsWith(FILE_PROJECTS) ? uri.substring("file://".length()) : uri;
  }

  public static boolean truish(Boolean b) {
    return b != null && b;
  }

  public static boolean isProjectUri(String path) {
    return path.startsWith(FILE_PROJECTS);
  }

  public static boolean isStartWithProject(String path) {
    return path.startsWith(PROJECTS);
  }

  public static String prefixProject(String path) {
    return path.startsWith(PROJECTS) ? path : PROJECTS + path;
  }

  public static String extractProjectPath(String fileUri) throws LanguageServerException {
    if (!isProjectUri(fileUri)) {
      throw new LanguageServerException("Not a project URI: " + fileUri);
    }
    Path path = Paths.get(removeUriScheme(fileUri));
    if (path.getNameCount() < 1) {
      throw new LanguageServerException("Not a project URI: " + fileUri);
    }
    return path.subpath(0, 2).toString();
  }

  public static Location fixLocation(Location o) {
    o.setUri(removePrefixUri(o.getUri()));
    return o;
  }

  public static boolean isWorkspaceUri(String uri) {
    return uri.startsWith("/");
  }

  public static String workspaceURIToFileURI(String uri) throws LanguageServerException {
    try {
      return FILE_PROJECTS + new URI(uri).getPath();
    } catch (URISyntaxException e) {
      throw new LanguageServerException("malformed uri", e);
    }
  }

  public static String fixUri(String uri) {
    return isProjectUri(uri) ? removePrefixUri(uri) : uri;
  }
}
