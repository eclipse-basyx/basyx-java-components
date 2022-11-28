/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.aas.authorization;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.servlets.DefaultServlet;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.vab.coder.json.metaprotocol.Result;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.protocol.http.server.ExceptionToHTTPCodeMapper;

/**
 *
 * A decorated variant of the {@link DefaultServlet} that checks authorization
 * when downloading files from the Tomcat server.
 *
 * @author wege
 *
 */
public class AuthorizedDefaultServlet<SubjectInformationType> extends DefaultServlet {
  protected final IFilesAuthorizer<SubjectInformationType> filesAuthorizer;
  protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

  public AuthorizedDefaultServlet(
      final IFilesAuthorizer<SubjectInformationType> filesAuthorizer,
      final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
  ) {
    this.filesAuthorizer = filesAuthorizer;
    this.subjectInformationProvider = subjectInformationProvider;
  }

  public AuthorizedDefaultServlet(
      final AuthorizedDefaultServletParams<SubjectInformationType> params
  ) {
    this(
      params.getFilesAuthorizer(),
      params.getSubjectInformationProvider()
    );
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    Path path = Paths.get(getRelativePath(request, true));

    try {
      authorizeDoGet(path);

      super.doGet(request, response);
    } catch (final NotAuthorized e) {
      int httpCode = ExceptionToHTTPCodeMapper.mapFromException(e);
      response.setStatus(httpCode);
      sendException(response.getOutputStream(), e);
    }
  }

  protected void authorizeDoGet(final Path path) {
    try {
      filesAuthorizer.authorizeDownloadFile(subjectInformationProvider.get(), path);
    } catch (final InhibitException e) {
      throw new NotAuthorized(e);
    }
  }

  private void sendException(OutputStream outputStream, Exception e) throws IOException {
    final String eString = new GSONTools(new DefaultTypeFactory()).serialize(new Result(e));
    outputStream.write(eString.getBytes(StandardCharsets.UTF_8));
  }
}
