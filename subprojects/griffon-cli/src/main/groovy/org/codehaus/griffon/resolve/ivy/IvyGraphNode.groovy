/* Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.resolve.ivy

import groovy.transform.CompileStatic
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.report.ResolveReport
import org.apache.ivy.core.resolve.IvyNode
import org.codehaus.griffon.resolve.Dependency
import org.codehaus.griffon.resolve.reporting.GraphNode

/**
 * Adapts an Ivy graph into a Grails one
 *
 * @author Graeme Rocher (Grails 2.3)
 * @since 1.3.0
 */
@CompileStatic
class IvyGraphNode extends GraphNode {
    IvyGraphNode(ResolveReport report) {
        super(new Dependency("org.codehaus.griffon.internal", "root", "1.0")) // version numbers not relevant for root node / dummy object
        createGraph(this, report.getDependencies(), report.getConfigurations())
    }

    void createGraph(GraphNode current, Collection<IvyNode> nodes, String[] confs) {
        for (IvyNode node in nodes) {
            if (node.isLoaded()) {

                final ModuleRevisionId id = node.id
                def graphNode = new GraphNode(new Dependency(id.organisation, id.name, id.revision))
                current.children << graphNode

                final Collection<IvyNode> dependencies = node.getDependencies(confs[0], confs)
                if (dependencies) {
                    createGraph(graphNode, dependencies, confs)
                }
            }
        }
    }
}
