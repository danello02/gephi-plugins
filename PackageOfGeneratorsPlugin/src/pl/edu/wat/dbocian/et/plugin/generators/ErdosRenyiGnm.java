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
import pl.edu.wat.dbocian.et.plugin.data.BasicEdge;

/**
 * @author Daniel Bocian
 *
 * based on Cezary Bartosiak implementation
 * https://github.com/cbartosiak/gephi-plugins/tree/complex-generators
 */
@ServiceProvider(service = Generator.class)
public class ErdosRenyiGnm implements Generator {

    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int n = 50;
    private int m = 1000;

    @Override
    public void generate(ContainerLoader container) {
        double time = 0.0;
        long startTime = System.nanoTime();
        Progress.start(progressTicket, n + n * n + m);
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        NodeDraft[] nodes = new NodeDraft[n];
        // max count of edges
        int max = n * (n - 1) / 2;

        long nodesTime = System.nanoTime();
        // Creating n nodes
        for (int i = 0; i < n && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            // node.addTimeInterval(vt + "", m + "");
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
        }

        time = (System.nanoTime() - nodesTime) / (Math.pow(10, 9));
        System.out.println("Creating n nodes time: " + (System.nanoTime() - nodesTime) + "ns (" + time + "s)");

        // m <= max/2
        if (m <= max / 2) {
            long edgesTime = System.nanoTime();
            // Creating a list of n^2 edges
            List<BasicEdge> edges = new ArrayList<BasicEdge>();
            for (int i = 0; i < n && !cancel; ++i) {
                for (int j = i + 1; j < n && !cancel; ++j) {
                    BasicEdge edge = new BasicEdge(i, j);
                    edges.add(edge);
                    Progress.progress(progressTicket);
                }
            }
            time = (System.nanoTime() - edgesTime) / (Math.pow(10, 9));
            System.out.println("Creating a list of n^2 edges time: " + (System.nanoTime() - edgesTime) + "ns (" + time + "s)");

            long drawingTime = System.nanoTime();
            // Drawing m edges
            for (int i = 0; i < m; ++i/* , ++et */) {
                BasicEdge be = edges.get(random.nextInt(edges.size()));
                EdgeDraft e = container.factory().newEdgeDraft();
                e.setSource(nodes[be.getSourceId()]);
                e.setTarget(nodes[be.getTargetId()]);
                edges.remove(be);
                container.addEdge(e);
                Progress.progress(progressTicket);
            }
            time = (System.nanoTime() - drawingTime) / (Math.pow(10, 9));
            System.out.println("Drawing edges time: " + (System.nanoTime() - drawingTime) + "ns (" + time + "s)");

        } else { // m > max/2
            long edgesTime = System.nanoTime();
            //Creating a list of n^2 edges
            List<EdgeDraft> edges = new ArrayList<EdgeDraft>();
            for (int i = 0; i < n && !cancel; ++i) {
                for (int j = i + 1; j < n && !cancel; ++j) {
                    EdgeDraft edge = container.factory().newEdgeDraft();
                    edge.setSource(nodes[i]);
                    edge.setTarget(nodes[j]);
                    edges.add(edge);
                    Progress.progress(progressTicket);
                }
            }
            time = (System.nanoTime() - edgesTime) / (Math.pow(10, 9));
            System.out.println("Creating a list of n^2 edges time: " + (System.nanoTime() - edgesTime) + "ns (" + time + "s)");


            long deletingTime = System.nanoTime();
            //Deleting max - m edges
            for (int i = 0; i < max - m; i++) {
                edges.remove(edges.get(random.nextInt(edges.size())));
            }
            time = (System.nanoTime() - deletingTime) / (Math.pow(10, 9));
            System.out.println("Deleting edges time: " + (System.nanoTime() - deletingTime) + "ns (" + time + "s)");
            
            long drawingTime = System.nanoTime();
            //Drawing m edges
            for (EdgeDraft e : edges) {
                container.addEdge(e);
                Progress.progress(progressTicket);
            }
            time = (System.nanoTime() - drawingTime) / (Math.pow(10, 9));
            System.out.println("Drawing edges time: " + (System.nanoTime() - drawingTime) + "ns (" + time + "s)");

        }

        Progress.finish(progressTicket);
        progressTicket = null;
        time = (System.nanoTime() - startTime) / (Math.pow(10, 9));
        System.out.println("Full time: " + (System.nanoTime() - startTime) + "ns (" + time + "s)");
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    @Override
    public String getName() {
        return "Erdos-Renyi G(n, m) model";
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
