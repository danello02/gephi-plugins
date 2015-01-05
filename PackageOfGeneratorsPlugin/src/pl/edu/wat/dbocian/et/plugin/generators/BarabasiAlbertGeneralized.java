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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * N > 0
 * m0 > 0 M > 0 0 >= p >= 1 0 >= q >= 1 p + q <= 1
 *
 * sigma2 > 0
 *
 * alfa > 0 beta > 0
 *
 * More info about algorithm:
 * http://en.wikipedia.org/wiki/Barab%C3%A1si%E2%80%93Albert_model
 * http://www.facweb.iitkgp.ernet.in/~niloy/COURSE/Spring2006/CNT/Resource/ba-model-2.pdf
 */
@ServiceProvider(service = Generator.class)
public class BarabasiAlbertGeneralized implements Generator {

    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int N = 100;
    private int m0 = 4;
    private int M = 2;
    private double p = 0.25;
    private double q = 0.25;

    private boolean passingNodes = true;
    
    private double mi = 10;
    private double sigma2 = 5;

    private boolean accelerated = true;
    private double alpha = 0.5;
    private double beta = 0.4;

    @Override
    public void generate(ContainerLoader container) {
        Progress.start(progressTicket, N);
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        // Timestamps
        int t = 1;

        // List of all ever existing nodes
        List<NodeDraft> nodes = new ArrayList<NodeDraft>();

        //List of currently existing nodes
        List<Integer> nodeList = new ArrayList<Integer>();

        List<Integer> degrees = new ArrayList<>();

        // List of nodes age - i.e. iteration left to die
        List<Integer> nodeAges = new ArrayList<Integer>();

        // Map of edges with values meaning:
        // -1 - edge not exist
        // positive value - start time of last time interval
        Map<Integer, Map<Integer, Integer>> edges = new HashMap<>();

        // list of id nodes which degree < n-1
        List<Integer> notFullNodes = new ArrayList<>();

        // Creating m0 isolated nodes
        for (int i = 0; i < m0 && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            nodes.add(i, node);
            nodeList.add(i);
            degrees.add(i, 0);
            nodeAges.add(i, randNodeAge(random));
            System.out.println("Node: " + i + ", age: " + nodeAges.get(i));
            notFullNodes.add(i);

            if (passingNodes) {
                int end = nodeAges.get(i);
                end = end < N ? end : N;
                node.addTimeInterval("0", end + "");
            } else {
                node.addTimeInterval("0", N + "");
            }

            container.addNode(node);
        }

        // Performing N steps of the algorithm
        int n = m0; // the number of currently existing nodes
        int allN = m0; //the nummer of ever existing nodes
        int ec = 0;  // the number of existing edges
        for (int i = 0; i < N && !cancel; ++i, ++t) {
            double r = random.nextDouble();
            n = nodeList.size();

            if (accelerated) {
                Double Mb = Math.pow(n, beta);
                M = Mb.intValue();
                System.out.println("Iteration: " + i + " n: " + n + " M: " + M);
            }

            if (r <= p) { // adding M edges
                System.out.println("\tIteration: " + i + " add M edges");
                for (int m = 0; m < M && !cancel; ++m) {
                    if (ec == n * (n - 1) / 2) {
                        m = M; //end of loop
                    } else {
                        // Randomly choosen source node
                        int a = notFullNodes.get(random.nextInt(notFullNodes.size()));

                        // Get list of nodes available for 'a' and count sum of theirs degree                                        
                        List<Integer> availableNodes = new ArrayList<>();
                        double sum = 0.0;
                        for (int av = 0; av < notFullNodes.size() && !cancel; av++) {
                            int id = notFullNodes.get(av);
                            if (!edgeExists(edges, a, id) && a != id) {
                                availableNodes.add(id);
                                sum += degrees.get(id);
                            }
                        }
                        sum += availableNodes.size();

                        double b = random.nextDouble();
                        double pki = 0.0;

                        // Chose target node with specified probability
                        int avSize = availableNodes.size();
                        for (int j = 0; j < avSize && !cancel; ++j) {
                            int id = availableNodes.get(j);
                            pki += (degrees.get(id) + 1) / sum;
                            if (j == avSize - 1) {   //protection for numerical error 
                                pki = 1.0;
                            }

                            if (b <= pki) {
                                System.out.println("Create edge between: " + a + " and " + id);
                                EdgeDraft edge = getEdge(container, nodes.get(a), nodes.get(id));
                                if (edge == null) {
                                    edge = container.factory().newEdgeDraft();
                                    edge.setSource(nodes.get(a));
                                    edge.setTarget(nodes.get(id));
                                    container.addEdge(edge);
                                }
                                setEdgeStartTime(edges, a, id, t);
                                increaseDegree(degrees, notFullNodes, n, a);
                                increaseDegree(degrees, notFullNodes, n, id);
                                ec++;

                                j = availableNodes.size(); //end of loop
                            }
                        }
                    }
                }
            } else if (r <= p + q) { // rewiring M edges
                System.out.println("\tIteration: " + i + " rewire M edges");
                if (ec == 0 || ec == n * (n - 1) / 2) {
                    continue;
                }
                // Get list of nodes with degree: maxDegree > degree > 0
                List<Integer> notEmptyAndFullNodes = new ArrayList<>();
                for (int nfn = 0; nfn < notFullNodes.size() && !cancel; nfn++) {
                    int id = notFullNodes.get(nfn);
                    if (degrees.get(id) > 0) {
                        notEmptyAndFullNodes.add(id);
                    }
                }

                for (int m = 0; m < M && !cancel; ++m) {
                    // Randomly choosen source node
                    int a = notEmptyAndFullNodes.get(random.nextInt(notEmptyAndFullNodes.size()));

                    // Get list of nodes connected to 'a' 
                    // AND
                    // Get list of nodes available for 'a' and count sum of theirs degree
                    List<Integer> connectedNodes = new ArrayList<>();
                    List<Integer> availableNodes = new ArrayList<>();
                    double sum = 0.0;
                    for (int c = 0; c < allN && !cancel; c++) {
                        if (edgeExists(edges, a, c)) {
                            connectedNodes.add(c);
                        } else if (a != c && nodeList.contains(c)) {
                            availableNodes.add(c);
                            sum += degrees.get(c);
                        }
                    }
                    sum += availableNodes.size();

                    // Randomly choosen edge (target node)
                    int l = connectedNodes.get(random.nextInt(connectedNodes.size()));
                    connectedNodes.clear();

                    // Choose target node with specified probability to rewire edge
                    double b = random.nextDouble();
                    double pki = 0.0;
                    int avSize = availableNodes.size();
                    for (int j = 0; j < avSize && !cancel; ++j) {
                        int id = availableNodes.get(j);
                        pki += (degrees.get(id) + 1) / sum;
                        if (j == avSize - 1) {   //protection for numerical error 
                            pki = 1.0;
                        }

                        if (b <= pki) {
                            System.out.println("Rewire " + a + "->" + l + " to " + a + "->" + id);
                            getEdge(container, nodes.get(a), nodes.get(l)).addTimeInterval(getEdgeStartTime(edges, a, l) + "", t + "");
                            setEdgeStartTime(edges, a, l, -1);
                            decreaseDegree(degrees, notFullNodes, l);

                            EdgeDraft edge = getEdge(container, nodes.get(a), nodes.get(id));
                            if (edge == null) {
                                edge = container.factory().newEdgeDraft();
                                edge.setSource(nodes.get(a));
                                edge.setTarget(nodes.get(id));
                                container.addEdge(edge);
                            }
                            setEdgeStartTime(edges, a, id, t);
                            increaseDegree(degrees, notFullNodes, n, id);

                            // Update notEmptyAndFullNodes list
                            if (degrees.get(l) == 0) {
                                notEmptyAndFullNodes.remove((Integer) l);
                            } else if (!notEmptyAndFullNodes.contains((Integer) l)) {
                                notEmptyAndFullNodes.add(l);
                            }
                            if (degrees.get(id) == n - 1) {
                                notEmptyAndFullNodes.remove((Integer) id);
                                notFullNodes.remove((Integer) id);
                            } else if (!notEmptyAndFullNodes.contains((Integer) id)) {
                                notEmptyAndFullNodes.add(id);
                            }

                            j = availableNodes.size(); //end of loop
                        }
                    }
                }
            } else { // adding a new node with M edges
                System.out.println("\tIteration: " + i + " add node");

                Double Na = 1.0;
                if (accelerated) {
                    Na = Math.pow(n, alpha);
                    System.out.println("Iteration: " + i + " n: " + n + " Na: " + Na.intValue());
                }

                for (int na = 0; na < Na.intValue() && !cancel; na++) {
                    NodeDraft node = container.factory().newNodeDraft();
                    node.setLabel("Node " + allN);
                    nodes.add(allN, node);
                    degrees.add(allN, 0);
                    nodeAges.add(allN, randNodeAge(random));
                    if (nodeList.size() > M) {
                        notFullNodes.add(allN);
                    }

                    if (passingNodes) {
                        int end = nodeAges.get(allN) + t;
                        end = end < N ? end : N;
                        node.addTimeInterval(t + "", end + "");
                    } else {
                        node.addTimeInterval(t + "", N + "");
                    }

                    container.addNode(node);

                    // Adding M edges out of the new node
                    double sum = 0.0;
                    for (int j = 0; j < nodeList.size() && !cancel; ++j) {
                        sum += degrees.get(nodeList.get(j));
                    }
                    sum += nodeList.size();
                    for (int m = 0; m < M && m < nodeList.size() && !cancel; ++m) {
                        r = random.nextDouble();
                        double pki = 0.0;

                        for (int j = 0; j < nodeList.size() && !cancel; ++j) {
                            int id = nodeList.get(j);
                            if (edgeExists(edges, id, allN)) {
                                continue;
                            }
                            pki += (degrees.get(id) + 1) / sum;
                            if (j == nodeList.size() - 1) {   //protection for numerical error 
                                pki = 1.0;
                            }

                            if (r <= pki) {
                                EdgeDraft edge = container.factory().newEdgeDraft();
                                edge.setSource(nodes.get(allN));
                                edge.setTarget(nodes.get(id));
                                setEdgeStartTime(edges, id, allN, t);
                                sum -= (degrees.get(id) + 1);
                                increaseDegree(degrees, notFullNodes, n + 1, allN);
                                increaseDegree(degrees, notFullNodes, n + 1, id);

                                container.addEdge(edge);
                                ec++;

                                j = nodeList.size();
                            }
                        }
                    }

                    nodeList.add(allN);
                    n++;
                    allN++;
                }
            }

            // Node passing
            if (passingNodes) {
                for (int j = 0; j < nodeList.size() && !cancel;) {
                    int id = nodeList.get(j);
                    int a = nodeAges.get(id) - 1;
                    nodeAges.set(id, a);
                    if (a == 0) { // Node will die
                        System.out.println("Node: " + id + " will die in iteration: " + i);
                        n--;
                        nodeList.remove(j);
                        notFullNodes.remove((Integer) id);
                        for (int k = 0; k < nodeList.size() && !cancel; k++) {
                            int idk = nodeList.get(k);
                            if (edgeExists(edges, id, idk)) {
                                getEdge(container, nodes.get(id), nodes.get(idk)).addTimeInterval(getEdgeStartTime(edges, id, idk) + "", t + "");
                                setEdgeStartTime(edges, id, idk, -1);
                                ec--;
                                int d = degrees.get(idk) - 1;
                                degrees.set(idk, d);
                            }
                        }
                    } else {
                        j++;
                    }
                }
            }

            Progress.progress(progressTicket);
        }

        // Update edges time interval
        for (int i = 0; i < n && !cancel; i++) {
            for (int j = i; j < n && !cancel; j++) {
                if (getEdgeStartTime(edges, i, i) > -1) {
                    EdgeDraft edge = getEdge(container, nodes.get(i), nodes.get(j));
                    if (edge != null) {
                        edge.addTimeInterval(getEdgeStartTime(edges, i, i) + "", N + "");
                    }
                }
            }
        }

        Progress.finish(progressTicket);
        progressTicket = null;
    }

    private boolean edgeExists(Map<Integer, Map<Integer, Integer>> edges, int id1, int id2) {
        return getEdgeStartTime(edges, id1, id2) > -1;
    }

    private EdgeDraft getEdge(ContainerLoader container, NodeDraft node1, NodeDraft node2) {
        EdgeDraft edge = container.getEdge(node1, node2);
        if (edge == null) {
            edge = container.getEdge(node2, node1);
        }
        return edge;
    }

    private void increaseDegree(List<Integer> degrees, List<Integer> notFullNodes, int numberOfExistingNodes, int id) {
        int d = degrees.get(id) + 1;
        degrees.set(id, d);
        if (d == numberOfExistingNodes - 1) {
            notFullNodes.remove((Integer) id);
        }
    }

    private void decreaseDegree(List<Integer> degrees, List<Integer> notFullNodes, int id) {
        int d = degrees.get(id) - 1;
        degrees.set(id, d);
        if (!notFullNodes.contains((Integer) id)) {
            notFullNodes.add(id);
        }
    }

    private int getEdgeStartTime(Map<Integer, Map<Integer, Integer>> edges, int id1, int id2) {
        int t = -1;
        if (edges.containsKey(id1) && edges.get(id1).containsKey(id2)) {
            t = edges.get(id1).get(id2);
        } else if (edges.containsKey(id2) && edges.get(id2).containsKey(id1)) {
            t = edges.get(id2).get(id1);
        }
        return t;
    }

    private void setEdgeStartTime(Map<Integer, Map<Integer, Integer>> edges, int id1, int id2, int value) {
        if (edges.containsKey(id1) && edges.get(id1).containsKey(id2)) {
            edges.get(id1).put(id2, value);
        } else if (edges.containsKey(id2) && edges.get(id2).containsKey(id1)) {
            edges.get(id2).put(id1, value);
        } else if (edges.containsKey(id1)) {
            edges.get(id1).put(id2, value);
        } else if (edges.containsKey(id2)) {
            edges.get(id2).put(id1, value);
        } else {
            Map<Integer, Integer> map = new HashMap<>();
            map.put(id2, value);
            edges.put(id1, map);
        }
    }

    /**
     * Method return random integer value with Normal distribution N(mi, sigma2).
     * If value is < 1 then return 1.
     *
     * @return age
     */
    private int randNodeAge(Random rand) {
        Double r = mi + sigma2 * rand.nextGaussian();
        int age = r.intValue();
        return age < 1 ? 1 : age;
    }

    public int getN() {
        return N;
    }

    public void setN(int N) {
        this.N = N;
    }

    public int getM0() {
        return m0;
    }

    public void setM0(int m0) {
        this.m0 = m0;
    }

    public int getM() {
        return M;
    }

    public void setM(int M) {
        this.M = M;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    @Override
    public String getName() {
        return "Generalized Barabasi-Albert Scale Free model";
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
    public void setProgressTicket(ProgressTicket pt) {
        this.progressTicket = pt;
    }

}
