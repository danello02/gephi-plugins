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
 * http://en.wikipedia.org/wiki/Barab%C3%A1si%E2%80%93Albert_model
 * http://www.facweb.iitkgp.ernet.in/~niloy/COURSE/Spring2006/CNT/Resource/ba-model-2.pdf
 */
@ServiceProvider(service = Generator.class)
public class BarabasiAlbertGeneralized implements Generator {

    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int N = 50;
    private int m0 = 4;
    private int M = 2;
    private double p = 0.25;
    private double q = 0.25;

    private boolean passingNodes = true;
    private double mi = 40;
    private double sigma2 = 1;

    @Override
    public void generate(ContainerLoader container) {
        Progress.start(progressTicket, N);
        Random random = new Random();
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        // Timestamps
        int t = 1;

        // Array of all ever existing nodes
        NodeDraft[] nodes = new NodeDraft[N + m0];
        
        //List of currently existing nodes
        List<Integer> nodeList = new ArrayList<Integer>();
        
        int[] degrees = new int[N + m0];
        
        // Array of nodes age - i.e. iteration left to die
        int[] nodeAges = new int[N + m0];

        // Array of edges with values meaning:
        // -1 - edge not exist
        // positive value - start time of last time interval
        int[][] edges = new int[m0 + N][m0 + N];
        for (int i = 0; i < m0 + N; i++) {
            for (int j = 0; j < m0 + N; j++) {
                edges[i][j] = -1;
            }
        }

        // list of id nodes which degree < n-1
        List<Integer> notFullNodes = new ArrayList<>();

        // Creating m0 isolated nodes
        for (int i = 0; i < m0 && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            nodes[i] = node;
            nodeList.add(i);
            degrees[i] = 0;
            nodeAges[i] = randNodeAge(random);
            System.out.println("Node: " + i + ", age: " + nodeAges[i]);
            notFullNodes.add(i);
            
            if (passingNodes) node.addTimeInterval("0", nodeAges[i] + "");
            else node.addTimeInterval("0", N + "");
            
            container.addNode(node);
        }

        // Performing N steps of the algorithm
        int n = m0; // the number of currently existing nodes
        int allN = m0; //the nummer of ever existing nodes
        int ec = 0;  // the number of existing edges
        for (int i = 0; i < N && !cancel; ++i, ++t) {
            double r = random.nextDouble();
            n = nodeList.size();

            if (r <= p) { // adding M edges
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
                                sum += degrees[id];
                            }
                        }
                        sum += availableNodes.size();

                        double b = random.nextDouble();
                        double pki = 0.0;

                        // Chose target node with specified probability
                        int avSize = availableNodes.size();
                        for (int j = 0; j < avSize && !cancel; ++j) {
                            int id = availableNodes.get(j);
                            pki += (degrees[id] + 1) / sum;
                            if (j == avSize - 1) {   //protection for numerical error 
                                pki = 1.0;
                            }

                            if (b <= pki) {
                                EdgeDraft edge = getEdge(container, nodes[a], nodes[id]);
                                if (edge == null) {
                                    edge = container.factory().newEdgeDraft();
                                    edge.setSource(nodes[a]);
                                    edge.setTarget(nodes[id]);
                                    container.addEdge(edge);
                                }
                                edges[a][id] = edges[id][a] = t;
                                increaseDegree(degrees, notFullNodes, n, a);
                                increaseDegree(degrees, notFullNodes, n, id);
                                ec++;

                                j = availableNodes.size(); //end of loop
                            }
                        }
                    }
                }
            } else if (r <= p + q) { // rewiring M edges
                if (ec == 0 || ec == n * (n - 1) / 2) {
                    continue;
                }
                // Get list of nodes with degree: maxDegree > degree > 0
                List<Integer> notEmptyAndFullNodes = new ArrayList<>();
                for (int nfn = 0; nfn < notFullNodes.size() && !cancel; nfn++) {
                    int id = notFullNodes.get(nfn);
                    if (degrees[id] > 0) {
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
                            sum += degrees[c];
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
                        pki += (degrees[id] + 1) / sum;
                        if (j == avSize - 1) {   //protection for numerical error 
                            pki = 1.0;
                        }

                        if (b <= pki) {
                            getEdge(container, nodes[a], nodes[l]).addTimeInterval(edges[a][l] + "", t + "");
                            edges[a][l] = edges[l][a] = -1;
                            decreaseDegree(degrees, notFullNodes, l);

                            EdgeDraft edge = getEdge(container, nodes[a], nodes[id]);
                            if (edge == null) {
                                edge = container.factory().newEdgeDraft();
                                edge.setSource(nodes[a]);
                                edge.setTarget(nodes[id]);
                                container.addEdge(edge);
                            }
                            edges[a][id] = edges[id][a] = t;
                            increaseDegree(degrees, notFullNodes, n, id);

                            // Update notEmptyAndFullNodes list
                            if (degrees[l] == 0) {
                                notEmptyAndFullNodes.remove((Integer) l);
                            } else if (!notEmptyAndFullNodes.contains((Integer) l)) {
                                notEmptyAndFullNodes.add(l);
                            }
                            if (degrees[id] == n - 1) {
                                notEmptyAndFullNodes.remove((Integer) id);
                            } else if (!notEmptyAndFullNodes.contains((Integer) id)) {
                                notEmptyAndFullNodes.add(id);
                            }

                            j = availableNodes.size(); //end of loop
                        }
                    }
                }
            } else { // adding a new node with M edges
                NodeDraft node = container.factory().newNodeDraft();
                node.setLabel("Node " + allN);
                nodes[allN] = node;               
                degrees[allN] = 0;
                nodeAges[allN] = randNodeAge(random);
                System.out.println("Node: " + allN + ", age: " + nodeAges[allN]);
                notFullNodes.add(allN);
                
                if (passingNodes) node.addTimeInterval(t + "", (t + nodeAges[i]) + "");
                else node.addTimeInterval(t + "", N + "");
                
                container.addNode(node);

                // Adding M edges out of the new node
                double sum = 0.0;
                for (int j = 0; j < nodeList.size() && !cancel; ++j) {
                    sum += degrees[nodeList.get(j)];
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
                        pki += (degrees[id] + 1) / sum;
                        if (j == nodeList.size() - 1) {   //protection for numerical error 
                            pki = 1.0;
                        }

                        if (r <= pki) {
                            EdgeDraft edge = container.factory().newEdgeDraft();
                            edge.setSource(nodes[allN]);
                            edge.setTarget(nodes[id]);
                            edges[allN][id] = edges[id][allN] = t;
                            sum -= (degrees[id] + 1);
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
            
            // Node passing
            if (passingNodes) {
                for (int j = 0; j < nodeList.size() && !cancel;) {
                    int id = nodeList.get(j);
                    if (--nodeAges[id] == 0) { // Node will die
                        System.out.println("Node: " + id + "will die in iteration: " + i);
                        n--;
                        nodeList.remove(j);
                        notFullNodes.remove((Integer) id);
                        for (int k = 0; k < nodeList.size() && !cancel; k++) {
                            int idk = nodeList.get(k);
                            if (edgeExists(edges, id, idk)) {
                                getEdge(container, nodes[id], nodes[idk]).addTimeInterval(edges[id][idk] + "", t + "");
                                edges[id][idk] = edges[idk][id] = -1;
                                ec--;
                                decreaseDegree(degrees, notFullNodes, idk);
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
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (edges[i][j] > -1) {
                    EdgeDraft edge = getEdge(container, nodes[i], nodes[j]);
                    if (edge != null) {
                        edge.addTimeInterval(edges[i][j] + "", N + "");
                    }
                }
            }
        }

        Progress.finish(progressTicket);
        progressTicket = null;
    }

    private boolean edgeExists(int[][] edges, int nodeId1, int nodeId2) {
        return edges[nodeId1][nodeId2] > -1;
    }

    private EdgeDraft getEdge(ContainerLoader container, NodeDraft node1, NodeDraft node2) {
        EdgeDraft edge = container.getEdge(node1, node2);
        if (edge == null) {
            edge = container.getEdge(node2, node1);
        }
        return edge;
    }

    private void increaseDegree(int[] degrees, List<Integer> notFullNodes, int numberOfExistingNodes, int id) {
        if (++degrees[id] == numberOfExistingNodes - 1) {
            notFullNodes.remove((Integer) id);
        }
    }

    private void decreaseDegree(int[] degrees, List<Integer> notFullNodes, int id) {
        degrees[id]--;
        if (!notFullNodes.contains((Integer) id)) {
            notFullNodes.add(id);
        }
    }

    /**
     * Method return random 
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
