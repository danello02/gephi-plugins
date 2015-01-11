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
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.WattsStrogatzBetaUI;

/**
 * @author Daniel Bocian
 *
 * based on Cezary Bartosiak implementation
 * https://github.com/cbartosiak/gephi-plugins/tree/complex-generators
 *
 * N > K >= ln(N) >= 1
 * K % 2 == 0
 * 0 <= beta <= 1
 * 
 * More info about algorithm:
 * http://en.wikipedia.org/wiki/Watts_and_Strogatz_model
 * http://www.inf.uni-konstanz.de/algo/publications/bb-eglrn-05.pdf
 */
@ServiceProvider(service = Generator.class)
public class WattsStrogatzBeta implements Generator {

    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int N = 20;
    private int K = 4;
    private double beta = 0.2;

    // Will probabilty depend on degree
    private boolean dependentProbability = false;
    private double r = -50;

    @Override
    public void generate(ContainerLoader container) {
        if (dependentProbability) {
            generateModifiedWSBeta(container);
        } else {
            generateClassicWSBeta(container);
        }

    }

    /**
     * Method generate graph using modified Watts-Strogratz Beta algorithm.
     * Modification add preferential attachment of rewired edges.
     * Probability of attachment to node is equal:
     *          (ki^r) / (SUMj(kj^r))
     * where:
     *  - ki - degree of node i,
     *  - r - real parameter.
     * 
     * Thanks to the fact:
     *  - for r > 0 nodes with higher degree are more attractive.
     *  - for r < 0 nodes with lower degree are more attractive.
     * 
     * @param loader 
     */
    private void generateModifiedWSBeta(ContainerLoader container) {
        Progress.start(progressTicket, N + N * K / 2 + N * K);
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[] nodes = new NodeDraft[N];
        int[] degrees = new int[N];

        // Creating a regular ring lattice
        Progress.setDisplayName(progressTicket, "Generating N nodes...");
        for (int i = 0; i < N && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();    
            node.setLabel("Node " + i);
            node.addTimeInterval(i + "", N + "");
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
        }
        Progress.setDisplayName(progressTicket, "Generating N*K/2 edges...");
        for (int i = 0; i < N && !cancel; ++i) {
            degrees[i] = K;
            for (int j = 1; j <= K / 2 && !cancel; ++j) {
                EdgeDraft edge = container.factory().newEdgeDraft();
                edge.setSource(nodes[i]);
                edge.setTarget(nodes[(i + j) % N]);
                container.addEdge(edge);
                Progress.progress(progressTicket);
            }
        }

        // Rewiring edges
        Progress.setDisplayName(progressTicket, "Rewiring edges...");
        for (int i = 0; i < N && !cancel; ++i) {
            // Get list of nodes not connected to i
            double sum = 0.0;
            List<Integer> allowed = new ArrayList<>();
            for (int n = 0; n < N && !cancel; n++) {
                if (n != i && !edgeExists(container, nodes[i], nodes[n])) {
                    sum += Math.pow(degrees[n], r);
                    allowed.add(n);
                }
            }
            
            for (int j = 1; j <= K / 2 && !cancel; ++j) {                
                if (random.nextDouble() <= beta) {
                    double rand = random.nextDouble() * sum;
                    double p = 0.0;
                    for (int n = 0; n < allowed.size() && !cancel; n++) {
                        p += Math.pow(degrees[allowed.get(n)], r);
                        
                        // If find node or this is last node (protection for numerical error)
                        if (rand <= p || n == allowed.size() - 1) {
                            int nodeId = allowed.get(n);
                            EdgeDraft edge = container.factory().newEdgeDraft();
                            edge.setSource(nodes[i]);
                            edge.setTarget(nodes[nodeId]);
                            sum -= Math.pow(degrees[nodeId], r);
                            degrees[nodeId]++;
                            allowed.remove(n);
                            
                            container.addEdge(edge);
                            
                            // End of loop
                            n = allowed.size();
                        }
                    }
                    
                    int nodeId = (i + j) % N;
                    container.removeEdge(getEdge(container, nodes[i], nodes[nodeId]));
                    degrees[nodeId]--;
                    allowed.add(nodeId);
                    
                    Progress.progress(progressTicket);
                }
            }
        }
    }
    
    /**
     * Method generate graph using classic Watts-Strogratz Beta algorithm with
     * skipping over potenial edges that are not created. Probability of
     * generating the next edge after k trials is: 
     *          p*(1-p)^(k-1)
     * @param containerLoader
     */
    private void generateClassicWSBeta(ContainerLoader container) {
        Double progressCalc = N * K * beta;
        Progress.start(progressTicket, N + N * K / 2 + progressCalc.intValue() / 2);
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[] nodes = new NodeDraft[N];

        // Creating a regular ring lattice
        Progress.setDisplayName(progressTicket, "Generating N nodes...");
        for (int i = 0; i < N && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            node.addTimeInterval(i + "", N + "");
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
        }
        Progress.setDisplayName(progressTicket, "Generating N*K/2 edges...");
        for (int i = 0; i < N && !cancel; ++i) {
            for (int j = 1; j <= K / 2 && !cancel; ++j) {
                EdgeDraft edge = container.factory().newEdgeDraft();
                edge.setSource(nodes[i]);
                edge.setTarget(nodes[(i + j) % N]);
                container.addEdge(edge);
                Progress.progress(progressTicket);
            }
        }

        // Rewiring edges
        Progress.setDisplayName(progressTicket, "Rewiring edges...");
        int s = -1;
        int i = 0, j;
        while (i < N && !cancel) {
            double r = random.nextDouble();
            Double d = Math.log10(1 - r) / Math.log10(1 - beta);
            //calc next edge to rewire
            s = s + 1 + d.intValue();
            i = s / (K / 2);
            j = 1 + s % (K / 2);

            if (i < N) {                
                int k = random.nextInt(N);
                while ((k == i || edgeExists(container, nodes[i], nodes[k])) && !cancel) {
                    k = random.nextInt(N);
                }
                
                container.removeEdge(getEdge(container, nodes[i], nodes[(i + j) % N]));

                EdgeDraft edge = container.factory().newEdgeDraft();
                edge.setSource(nodes[i]);
                edge.setTarget(nodes[k]);
                container.addEdge(edge);
                Progress.progress(progressTicket);
            }
        }
        Progress.finish(progressTicket);
        progressTicket = null;
    }

    private boolean edgeExists(ContainerLoader container, NodeDraft node1, NodeDraft node2) {
        return container.edgeExists(node1, node2) || container.edgeExists(node2, node1);
    }

    private EdgeDraft getEdge(ContainerLoader container, NodeDraft node1, NodeDraft node2) {
        EdgeDraft edge = container.getEdge(node1, node2);
        if (edge == null) {
            edge = container.getEdge(node2, node1);
        }
        return edge;
    }

    public int getN() {
        return N;
    }

    public void setN(int N) {
        this.N = N;
    }

    public int getK() {
        return K;
    }

    public void setK(int K) {
        this.K = K;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public boolean isDependentProbability() {
        return dependentProbability;
    }

    public void setDependentProbability(boolean dependentProbability) {
        this.dependentProbability = dependentProbability;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
    
    @Override
    public String getName() {
        return "Watts-Strogatz Small World model Beta";
    }

    @Override
    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(WattsStrogatzBetaUI.class);
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
