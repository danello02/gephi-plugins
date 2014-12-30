/*
 * Copyright 2008-2010 Gephi
 * Authors : Daniel Bocian
 * Website : http://www.gephi.org
 * 
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.wat.dbocian.et.plugin.generators;

import java.util.Random;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Daniel Bocian
 *
 * based on Cezary Bartosiak implementation
 * https://github.com/cbartosiak/gephi-plugins/tree/complex-generators
 *
 * More info about algorithm:
 * http://www.inf.uni-konstanz.de/algo/publications/bb-eglrn-05.pdf
 */
@ServiceProvider(service = Generator.class)
public class ErdosRenyiGnp implements Generator {

    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int n = 50;
    private double p = 0.05;

    @Override
    public void generate(ContainerLoader container) {
        Double progressCalc = n * (n - 1) * p / 2;
        Progress.start(progressTicket, n + progressCalc.intValue());
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[] nodes = new NodeDraft[n];

        // Creating n nodes
        for (int i = 0; i < n && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
        }

        // Linking every node with each other with probability p (no self-loops)
        int k = -1, v = 1;
        while (v < n && !cancel) {
            double r = random.nextDouble();
            Double d = Math.log10(1 - r) / Math.log10(1 - p);
            //calc next edge
            k = k + 1 + d.intValue();
            while (k >= v && v < n && !cancel) {
                k -= v++;
            }
            if (v < n) {
                EdgeDraft edge = container.factory().newEdgeDraft();
                edge.setSource(nodes[v]);
                edge.setTarget(nodes[k]);
                container.addEdge(edge);
                Progress.progress(progressTicket);
            }
        }

        Progress.finish(progressTicket);
        progressTicket = null;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    @Override
    public String getName() {
        return "Erdos-Renyi G(n, p) model";
    }

    //TODO - zwracanie widoku
    @Override
    public GeneratorUI getUI() {
        return null;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
