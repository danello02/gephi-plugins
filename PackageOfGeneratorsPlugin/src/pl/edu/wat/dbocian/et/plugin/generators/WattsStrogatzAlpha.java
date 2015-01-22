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
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import pl.edu.wat.dbocian.et.plugin.data.BasicEdge;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.WattsStrogatzAlphaUI;

/**
 * @author Daniel Bocian
 *
 * n > k > 0
 * 0 <= alpha
 * 
 * 0 <= p <= 1
 * m >= 0
 * m <= n * k / 2
 * 
 * based on Cezary Bartosiak implementation:
 * https://github.com/cbartosiak/gephi-plugins/tree/complex-generators
 * 
 * More info about algorithm:
 * http://www.lucamoroni.it/cdf-simulations/duncan-watts-alpha-model/
 */
@ServiceProvider(service = Generator.class)
public class WattsStrogatzAlpha implements Generator {
    public static final int RING = 0;
    public static final int GNP = 1;
    public static final int GNM = 2;
    
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int n = 20;
    private int k = 4;
    private double alpha = 3.5;
    private final double pr = Math.pow(10, -10);
    
    // initial topology:
    // 0 - regular ring (default)
    // 1 - G(n, p)
    // 2 - G(n, m)
    private int topology = 0;
    private double p = 0.05;
    private int m = 20;

    @Override
    public void generate(ContainerLoader container) {
        Progress.start(progressTicket, n + n + n * k / 2);
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[] nodes = new NodeDraft[n];
        int ec;

        // Create nodes with specified topology
        switch (topology) {
            case GNP: 
                Double progressCalc = n * (n - 1) * p / 2;
                Progress.start(progressTicket, n + progressCalc.intValue() + n * k / 2);
                ec = generateGnp(container, nodes);
            break;
               
            case GNM: 
                Progress.start(progressTicket, n + n * n / 2 + m + n * k / 2);
                ec = generateGnm(container, nodes);
            break;
                
            default:
                Progress.start(progressTicket, n + n + n * k / 2);
                ec = generateRing(container, nodes);
        }
        

        // Creating n * k / 2 edges
        List<Integer> ids = new ArrayList<>();
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
            return pr;
        }
        return Math.pow(mij / k, alpha) * (1 - pr) + pr;
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
    
    private int generateRing(ContainerLoader container, NodeDraft[] nodes) {
        int ec = 0;
        // Creating a regular ring lattice
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
                return ec;
    }
    
    private int generateGnp(ContainerLoader container, NodeDraft[] nodes) {
        int ec = 0;
        Random random = new Random();
        // Creating n nodes
                for (int i = 0; i < n && !cancel; ++i) {
                    NodeDraft node = container.factory().newNodeDraft();
                    node.setLabel("Node " + i);
                    nodes[i] = node;
                    container.addNode(node);
                    Progress.progress(progressTicket);
                }

                // Linking every node with each other with probability p (no self-loops)
                int s = -1, v = 1;
                while (v < n && !cancel) {
                    double r = random.nextDouble();
                    Double d = Math.log10(1 - r) / Math.log10(1 - p);
                    //calc next edge
                    s = s + 1 + d.intValue();
                    while (s >= v && v < n && !cancel) {
                        s -= v++;
                    }
                    if (v < n) {
                        ec++;
                        EdgeDraft edge = container.factory().newEdgeDraft();
                        edge.setSource(nodes[v]);
                        edge.setTarget(nodes[s]);
                        container.addEdge(edge);
                        Progress.progress(progressTicket);
                    }
                }
                return ec;
    }
    
    private int generateGnm(ContainerLoader container, NodeDraft[] nodes) {
        int ec = 0;
        Random random = new Random();
        // max count of edges
        int max = n * (n - 1) / 2;

        // Creating n nodes
        for (int i = 0; i < n && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            // node.addTimeInterval(vt + "", m + "");
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
        }

        if (m <= max / 2) {
            // Creating a list of n^2 edges
            List<BasicEdge> edges = new ArrayList<>();
            for (int i = 0; i < n && !cancel; ++i) {
                for (int j = i + 1; j < n && !cancel; ++j) {
                    BasicEdge edge = new BasicEdge(i, j);
                    edges.add(edge);
                    Progress.progress(progressTicket);
                }
            }

            // Drawing m edges
            for (int i = 0; i < m && !cancel; ++i) {
                BasicEdge be = edges.get(random.nextInt(edges.size()));
                EdgeDraft e = container.factory().newEdgeDraft();
                e.setSource(nodes[be.getSourceId()]);
                e.setTarget(nodes[be.getTargetId()]);
                edges.remove(be);
                ec++;
                container.addEdge(e);
                Progress.progress(progressTicket);
            }

        } else {
            //Creating a list of n^2 edges
            List<EdgeDraft> edges = new ArrayList<>();
            for (int i = 0; i < n && !cancel; ++i) {
                for (int j = i + 1; j < n && !cancel; ++j) {
                    EdgeDraft edge = container.factory().newEdgeDraft();
                    edge.setSource(nodes[i]);
                    edge.setTarget(nodes[j]);
                    edges.add(edge);
                    Progress.progress(progressTicket);
                }
            }

            //Deleting max - m edges
            for (int i = 0; i < max - m && !cancel; i++) {
                edges.remove(edges.get(random.nextInt(edges.size())));
            }

            //Drawing m edges
            ec = edges.size();
            for (EdgeDraft e : edges) {
                container.addEdge(e);
                Progress.progress(progressTicket);
            }
        }
        return ec;
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

    public int getTopology() {
        return topology;
    }

    public void setTopology(int topology) {
        this.topology = topology;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }
        
    @Override
    public String getName() {
        return "Watts-Strogatz Small World model Alpha";
    }

    @Override
    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(WattsStrogatzAlphaUI.class);
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
