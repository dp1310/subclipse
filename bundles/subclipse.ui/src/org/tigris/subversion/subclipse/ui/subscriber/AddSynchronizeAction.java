/**
 * ***************************************************************************** Copyright (c) 2005,
 * 2006 Subclipse project and others. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * <p>Contributors: Subclipse project committers - initial API and implementation
 * ****************************************************************************
 */
package org.tigris.subversion.subclipse.ui.subscriber;

import java.util.Iterator;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.team.core.synchronize.FastSyncInfoFilter;
import org.eclipse.team.core.synchronize.SyncInfo;
import org.eclipse.team.ui.synchronize.ISynchronizeModelElement;
import org.eclipse.team.ui.synchronize.ISynchronizePageConfiguration;
import org.eclipse.team.ui.synchronize.SynchronizeModelAction;
import org.eclipse.team.ui.synchronize.SynchronizeModelOperation;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;

public class AddSynchronizeAction extends SynchronizeModelAction {

  public AddSynchronizeAction(String text, ISynchronizePageConfiguration configuration) {
    super(text, configuration);
  }

  protected FastSyncInfoFilter getSyncInfoFilter() {
    return new FastSyncInfoFilter() {
      public boolean select(SyncInfo info) {
        SyncInfoDirectionFilter filter = new SyncInfoDirectionFilter(new int[] {SyncInfo.OUTGOING});
        if (!filter.select(info)) return false;
        IStructuredSelection selection = getStructuredSelection();
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
          ISynchronizeModelElement element = (ISynchronizeModelElement) iter.next();
          IResource resource = element.getResource();
          if (resource == null) return false;
          if (resource.isLinked()) return false;
          ISVNLocalResource svnResource = SVNWorkspaceRoot.getSVNResourceFor(resource);
          try {
            if (svnResource.isManaged()) return false;
          } catch (SVNException e) {
            return false;
          }
        }
        return true;
      }
    };
  }

  protected SynchronizeModelOperation getSubscriberOperation(
      ISynchronizePageConfiguration configuration, IDiffElement[] elements) {
    return new AddSynchronizeOperation(configuration, elements);
  }
}
