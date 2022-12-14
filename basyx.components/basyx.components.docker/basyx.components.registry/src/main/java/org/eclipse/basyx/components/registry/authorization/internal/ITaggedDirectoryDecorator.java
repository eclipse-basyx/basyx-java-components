package org.eclipse.basyx.components.registry.authorization.internal;

import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;

/**
 * Interface for tagged directory decoration
 *
 * @author wege
 */
public interface ITaggedDirectoryDecorator {
	/**
	 * Decorates a tagged directory according to this decorator.
	 *
	 * @param taggedDirectory
	 *            the tagged directory to be decorated.
	 */
	public IAASTaggedDirectory decorate(IAASTaggedDirectory taggedDirectory);
}
