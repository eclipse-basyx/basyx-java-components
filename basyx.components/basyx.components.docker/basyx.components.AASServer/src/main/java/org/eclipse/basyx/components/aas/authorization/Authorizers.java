package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.extensions.aas.aggregator.authorization.IAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.aas.api.authorization.IAASAPIAuthorizer;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.ISubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.ISubmodelAPIAuthorizer;

public class Authorizers<SubjectInformationType> {
  private final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer;

  public IAASAggregatorAuthorizer<SubjectInformationType> getAasAggregatorAuthorizer() {
    return aasAggregatorAuthorizer;
  }

  private final IAASAPIAuthorizer<SubjectInformationType> aasApiAuthorizer;

  public IAASAPIAuthorizer<SubjectInformationType> getAasApiAuthorizer() {
    return aasApiAuthorizer;
  }

  private final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer;

  public ISubmodelAggregatorAuthorizer<SubjectInformationType> getSubmodelAggregatorAuthorizer() {
    return submodelAggregatorAuthorizer;
  }

  private final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer;

  public ISubmodelAPIAuthorizer<SubjectInformationType> getSubmodelAPIAuthorizer() {
    return submodelAPIAuthorizer;
  }

  private final IFilesAuthorizer<SubjectInformationType> filesAuthorizer;

  public IFilesAuthorizer<SubjectInformationType> getFilesAuthorizer() {
    return filesAuthorizer;
  }

  public Authorizers(
      final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer,
      final IAASAPIAuthorizer<SubjectInformationType> aasApiAuthorizer,
      final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer,
      final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer,
      final IFilesAuthorizer<SubjectInformationType> filesAuthorizer
  ) {
    this.aasAggregatorAuthorizer = aasAggregatorAuthorizer;
    this.aasApiAuthorizer = aasApiAuthorizer;
    this.submodelAggregatorAuthorizer = submodelAggregatorAuthorizer;
    this.submodelAPIAuthorizer = submodelAPIAuthorizer;
    this.filesAuthorizer = filesAuthorizer;
  }
}
