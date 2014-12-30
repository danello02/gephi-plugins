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
 */
@ServiceProvider(service = Generator.class)
public class WattsStrogatzBeta implements Generator {

    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int N = 2000;
    private int K = 500;
    private double beta = 0.2;
    private int cp = 0;

    @Override
    public void generate(ContainerLoader container) {
        Double progressCalc = N * K * beta;
        Progress.start(progressTicket, N + N * K / 2 + progressCalc.intValue() / 2);
        System.out.println("Start: " + (N + N * K / 2 + progressCalc.intValue() / 2));
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[] nodes = new NodeDraft[N];

        // Creating a regular ring lattice
        for (int i = 0; i < N && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            node.addTimeInterval(i + "", N + "");
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
            cp++;
        }
        for (int i = 0; i < N && !cancel; ++i) {
            for (int j = 1; j <= K / 2 && !cancel; ++j) {
                EdgeDraft edge = container.factory().newEdgeDraft();
                edge.setSource(nodes[i]);
                edge.setTarget(nodes[(i + j) % N]);
                container.addEdge(edge);
                Progress.progress(progressTicket);
                cp++;
            }
        }
        System.out.println("Progress: " + cp);

        // Rewiring edges
        int s = -1;
        int i = 0, j = 1;
        while (i < N) {
            double r = random.nextDouble();
            Double d = Math.log10(1 - r) / Math.log10(1 - beta);
            //calc next edge to rewire
            s = s + 1 + d.intValue();
            i = s / (K / 2);
            j = 1 + s % (K / 2);

            if (i < N) {
                container.removeEdge(getEdge(container, nodes[i], nodes[(i + j) % N]));

                int k = random.nextInt(N);
                while ((k == i || edgeExists(container, nodes[i], nodes[k])) && !cancel) {
                    k = random.nextInt(N);
                }

                EdgeDraft edge = container.factory().newEdgeDraft();
                edge.setSource(nodes[i]);
                edge.setTarget(nodes[k]);
                container.addEdge(edge);
                cp++;
                Progress.progress(progressTicket);
            }
        }
        System.out.println("Progres count: " + cp);
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

    @Override
    public String getName() {
        return "Watts-Strogatz Small World model Beta";
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
