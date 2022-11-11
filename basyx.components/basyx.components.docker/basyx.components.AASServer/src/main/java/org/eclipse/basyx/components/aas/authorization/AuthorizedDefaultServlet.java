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
      enforceDoGet(path);

      super.doGet(request, response);
    } catch (final NotAuthorized e) {
      int httpCode = ExceptionToHTTPCodeMapper.mapFromException(e);
      response.setStatus(httpCode);
      sendException(response.getOutputStream(), e);
    }
  }

  protected void enforceDoGet(final Path path) {
    try {
      filesAuthorizer.enforceDownloadFile(subjectInformationProvider.get(), path);
    } catch (final InhibitException e) {
      throw new NotAuthorized(e);
    }
  }

  private void sendException(OutputStream outputStream, Exception e) throws IOException {
    final String eString = new GSONTools(new DefaultTypeFactory()).serialize(new Result(e));
    outputStream.write(eString.getBytes(StandardCharsets.UTF_8));
  }
}
