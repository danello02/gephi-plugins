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
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.ErdosRenyiGnpUI;

/**
 * @author Daniel Bocian
 *
 * n > 0
 * 0 <= p <= 1
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

    private int n = 10;
    private double p = 0.01;
    
    private boolean gridAlgorithm = false;
    
    private static final int SPACE_SIZE = 100;
  
    @Override
    public void generate(ContainerLoader container) {
        if (gridAlgorithm) {
            generateGridAlgorithmGnp(container);
        } else {
            generateClassicERGnp(container);
        }                     
    }    
        
    /**
     * Method generate graph using modified Erdos-Renyi G(n, p) algorithm.
     * 
     * Modified algorithm:
     * 1. Create n x n nodes (like in grid)
     * 2. Start from smalest square in center of grid (if n is even square will
     * be 2x2 nodes size, otherwise 1x1).
     * 3. Do 4 and 5 while square ft in grid.
     * 4. Connect evry nodes in square with probability p.
     * 5. Get next, biggest square from the center (size + 2) and go to 4.
     * 
     * Nodes are arranged in grid. Also time interval was added to
     * allow timeline control.
     * 
     * @param container 
     */
    private void generateGridAlgorithmGnp(ContainerLoader container) {
        Progress.start(progressTicket, n * n + countProgress(n));
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[][] nodes = new NodeDraft[n][n];

        Double d = (double) n / 2;
        int delta = -d.intValue() * SPACE_SIZE;
        int maxT = (n + 1) / 2 - 1;

        Progress.setDisplayName(progressTicket, "Creating n x n nodes...");
        for (int i = 0; i < n && !cancel; i++) {
            for (int j = 0; j < n && !cancel; j++) {
                NodeDraft node = container.factory().newNodeDraft();
                node.setLabel("Node " + i + " " + j);
                node.setX(delta + i * SPACE_SIZE);
                node.setY(delta + j * SPACE_SIZE);
                nodes[i][j] = node;
                int timeI = Math.abs(i - n / 2);
                int timeJ = Math.abs(j - n / 2);
                if (n % 2 == 0) {
                    timeI = i < n / 2 ? timeI - 1 : timeI;
                    timeJ = j < n / 2 ? timeJ - 1 : timeJ;
                }
                int time = Math.max(timeI, timeJ);
                
                node.addTimeInterval(time + "", maxT + "");
                container.addNode(node);
                Progress.progress(progressTicket);
            }
        }

        int t = 0;
        for (int s = 1; s <= n / 2 && !cancel; s++) {
            int min = n / 2 - s;
            int max = (n + 1) / 2 + s;
            for (int i = min; i < max && !cancel; i++) {
                for (int j = min; j < max && !cancel; j++) {
                    for (int k = i; k < max && !cancel; k++) {
                        for (int l = k == i ? j + 1 : min; l < max && !cancel; l++) {
                            if (random.nextDouble() < p && !edgeExists(container, nodes[i][j], nodes[k][l])) {
                                EdgeDraft edge = container.factory().newEdgeDraft();
                                edge.setSource(nodes[i][j]);
                                edge.setTarget(nodes[k][l]);
                                edge.addTimeInterval(t + "", maxT + "");
                                container.addEdge(edge);
                            }
                            Progress.progress(progressTicket);
                        }
                    }
                }
            }
            t++;
        }

        Progress.finish(progressTicket);
        progressTicket = null;
    }
    
    /**
     * Method generate graph using classic Erdos-Renyi G(n, p) algorithm with
     * skipping over potenial edges that are not created. Probability of
     * generating the next edge after k trials is: 
     *          p*(1-p)^(k-1)
     * 
     * @param containerLoader
     */
    private void generateClassicERGnp(ContainerLoader container) {
        Progress.start(progressTicket, (int) (n + n * n * p));
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[] nodes = new NodeDraft[n];

        // Creating n nodes
        Progress.setDisplayName(progressTicket, "Creating n nodes...");
        for (int i = 0; i < n && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
        }
        
        // Linking every node with each other with probability p (no self-loops)
        Progress.setDisplayName(progressTicket, "Linking nodes...");
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
    
    private boolean edgeExists(ContainerLoader container, NodeDraft node1, NodeDraft node2) {
        return container.edgeExists(node1, node2) || container.edgeExists(node2, node1);
    }
    
    private int countProgress(int c) {
        if (c > 1) return (c * c) * ( c * c - 1) / 2 + countProgress(c - 2);
        else return 0; 
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

    public boolean isGridAlgorithm() {
        return gridAlgorithm;
    }

    public void setGridAlgorithm(boolean gridAlgorithm) {
        this.gridAlgorithm = gridAlgorithm;
    }

    @Override
    public String getName() {
        return "Erdos-Renyi G(n, p) model";
    }

    @Override
    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(ErdosRenyiGnpUI.class);
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
