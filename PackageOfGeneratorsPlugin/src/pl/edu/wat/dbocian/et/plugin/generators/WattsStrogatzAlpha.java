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

import java.util.ArrayList;
import java.util.List;
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
 * based on Cezary Bartosiak implementation:
 * https://github.com/cbartosiak/gephi-plugins/tree/complex-generators
 * 
 * More info about algorithm:
 * http://www.lucamoroni.net/cdf-simulations/duncan-watts-alpha-model/
 */
@ServiceProvider(service = Generator.class)
public class WattsStrogatzAlpha implements Generator {

    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int n = 20;
    private int k = 4;
    private double alpha = 3.5;
    private double p = Math.pow(10, -10);

    @Override
    public void generate(ContainerLoader container) {
        Progress.start(progressTicket, n + n + n * k / 2);
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[] nodes = new NodeDraft[n];

        // Creating a regular ring lattice
        int ec = 0;
        for (int i = 0; i < n && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
        }
        for (int i = 0; i < n && !cancel; ++i) {
            EdgeDraft edge = container.factory().newEdgeDraft();
            edge.setSource(nodes[i]);
            edge.setTarget(nodes[(i + 1) % n]);
            container.addEdge(edge);
            ec++;
            Progress.progress(progressTicket);
        }

        // Creating n * k / 2 edges
        List<Integer> ids = new ArrayList<Integer>();
        while (ec < n * k / 2 && !cancel) {
            for (int i = 0; i < n && !cancel; ++i) {
                ids.add(i);
            }
            while (ec < n * k / 2 && ids.size() > 0 && !cancel) {
                Integer i = ids.remove(random.nextInt(ids.size()));
                double[] Rij = new double[n];
                double sumRij = 0.0;
                for (int j = 0; j < n && !cancel; ++j) {
                    Rij[j] = calculateRij(container, nodes, i, j, ec);
                    sumRij += Rij[j];
                }
                double r = random.nextDouble();
                double pij = 0.0;
                for (int j = 0; j < n && !cancel; ++j) {
                    if (Rij[j] > 0.0) {
                        pij += Rij[j] / sumRij;
                        if (r <= pij) {
                            EdgeDraft edge = container.factory().newEdgeDraft();
                            edge.setSource(nodes[i]);
                            edge.setTarget(nodes[j]);
                            container.addEdge(edge);
                            ec++;

                            Progress.progress(progressTicket);
                            j = n;
                        }
                    }
                }
            }
        }

        Progress.finish(progressTicket);
        progressTicket = null;
    }

    public double calculateRij(ContainerLoader container, NodeDraft[] nodes, int i, int j, int ec) {
        if (i == j || edgeExists(container, nodes[i], nodes[j])) {
            return 0.0;
        }
        int mij = calculatemij(container, nodes, i, j);
        int kt = 2 * ec / n; //average degree of network at time t-1
        if (mij >= kt) {
            return 1.0;
        }
        if (mij == 0) {
            return p;
        }
        return Math.pow(mij / k, alpha) * (1 - p) + p;
    }

    public int calculatemij(ContainerLoader container, NodeDraft[] nodes, int i, int j) {
        int mij = 0;
        for (int l = 0; l < n && !cancel; ++l) {
            if (l != i && l != j
                    && edgeExists(container, nodes[i], nodes[l])
                    && edgeExists(container, nodes[j], nodes[l])) {
                mij++;
            }
        }
        return mij;
    }

    private boolean edgeExists(ContainerLoader container, NodeDraft node1, NodeDraft node2) {
        return container.edgeExists(node1, node2) || container.edgeExists(node2, node1);
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    @Override
    public String getName() {
        return "Watts-Strogatz Small World model Alpha";
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
