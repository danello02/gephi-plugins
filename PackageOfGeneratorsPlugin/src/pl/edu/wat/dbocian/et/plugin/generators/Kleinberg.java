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
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Daniel Bocian
 *
 * n >= 2
 * p >= 1
 * p <= 2n - 2
 * q >= 0
 * q <= n^2 - p * (p + 3) / 2 - 1 for p < n
 * q <= (2n - p - 3) * (2n - p) / 2 + 1 for p >= n
 * r >= 0
 * S >= 0
 *
 * based on Cezary Bartosiak implementation:
 * https://github.com/cbartosiak/gephi-plugins/tree/complex-generators
 *
 * More info about algorithm: 
 * http://en.wikipedia.org/wiki/Small-world_routing
 * http://web.cs.ucdavis.edu/~martel/main/podc.talk.ppt
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.117.7097&rep=rep1&type=pdf
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.381&rep=rep1&type=pdf
 */
@ServiceProvider(service = Generator.class)
public class Kleinberg implements Generator {

    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int n = 10;
    private int p = 2;
    private int q = 2;
    private double r = 0;

    private boolean torusBased = false;

    private boolean local = false;
    private int S = 1;

    @Override
    public void generate(ContainerLoader container) {
        if (!local) {
            S = 0;
        }

        Progress.start(progressTicket, n * n + n * n * (2 * p + 1) * (2 * p + 1)
                + (int) Math.pow(n, 4) + n * n * q);
        Random random = new Random();

        NodeDraft[][] nodes = new NodeDraft[n][n];
        boolean[][][][] edges = new boolean[n][n][n][n];

        // Creating lattice n x n       
        for (int i = 0; i < n && !cancel; ++i) {
            for (int j = 0; j < n && !cancel; ++j) {
                NodeDraft node = container.factory().newNodeDraft();
                node.setLabel("Node " + i + " " + j);
                nodes[i][j] = node;
                container.addNode(node);
                Progress.progress(progressTicket);
            }
        }

        // Creating edges from each node to all nodes within distance p
        for (int i = 0; i < n && !cancel; ++i) {
            for (int j = 0; j < n && !cancel; ++j) {
                for (int k = i - p; k <= i + p && !cancel; ++k) {
                    for (int l = j - p; l <= j + p && !cancel; ++l) {
                        int d = 0;
                        if (torusBased) {
                            d = gridDtb(i, j, k, l);
                        } else {
                            d = gridD(i, j, k, l);
                        }
                        if ((torusBased || (!torusBased && k >= 0 && k < n && l >= 0 && l < n)) && d <= p && !(i == k % n && j == l % n)) {
                            EdgeDraft edge = container.factory().newEdgeDraft();
                            edge.setSource(nodes[i][j]);
                            edge.setTarget(nodes[(k + n) % n][(l + n) % n]);
                            edges[i][j][(k + n) % n][(l + n) % n] = true;
                            container.addEdge(edge);
                        }
                        Progress.progress(progressTicket);
                    }
                }
            }
        }

        // Creating edges from each node to q long-range contacts
        // If local create q times one contact for all nodes
        // Else create for all nodes q contacts
        int MLocal = 1;
        int M = q;
        if (local) {
            MLocal = q;
            M = 1;
        }
        for (int mLocal = 0; mLocal < MLocal; mLocal++) {
            for (int i = 0; i < n && !cancel; ++i) {
                for (int j = 0; j < n && !cancel; ++j) {
                    double sum = 0.0;
                    for (int k = 0; k < n && !cancel; ++k) {
                        for (int l = 0; l < n && !cancel; ++l) {
                            int d = d(edges, i, j, k, l);
                            if (!edges[i][j][k][l] && d > 0) {
                                sum += Math.pow(d, -r);
                            }
                            Progress.progress(progressTicket);
                        }
                    }
                    for (int m = 0; m < M && !cancel; ++m) {
                        double b = random.nextDouble();
                        double pki = 0.0;
                        for (int k = 0; k < n && !cancel; ++k) {
                            for (int l = 0; l < n && !cancel; ++l) {
                                int d = d(edges, i, j, k, l);
                                if (!edges[i][j][k][l] && d > 0) {
                                    double currentD = Math.pow(d, -r);
                                    pki += currentD / sum;

                                    if (b <= pki) {
                                        EdgeDraft edge = container.factory().newEdgeDraft();
                                        edge.setSource(nodes[i][j]);
                                        edge.setTarget(nodes[k][l]);
                                        edges[i][j][k][l] = true;
                                        container.addEdge(edge);

                                        sum -= currentD;
                                        // end of loop
                                        k = n;
                                        l = n;
                                    }
                                }
                            }
                        }
                        Progress.progress(progressTicket);
                    }
                }
            }
        }

        Progress.finish(progressTicket);
        progressTicket = null;
    }

    private int d(boolean[][][][] edges, int i, int j, int k, int l) {
        if (i == k && j == l) {
            return 0;
        }
        if (S == 0) {
            if (torusBased) {
                return gridDtb(i, j, k, l);
            } else {
                return gridD(i, j, k, l);
            }
        }
        int d = localD(edges, S, i, j, k, l);
        return d;
    }

    private int gridD(int i, int j, int k, int l) {
        return Math.abs(k - i) + Math.abs(l - j);
    }

    private int gridDtb(int i, int j, int k, int l) {
        return Math.min(Math.abs(k - i), n - Math.abs(k - i)) + Math.min(Math.abs(l - j), n - Math.abs(l - j));
    }

    private int localD(boolean[][][][] edges, int s, int i, int j, int k, int l) {
        if (edges[i][j][k][l]) {
            return 1;
        }
        int d = 1;
        if (torusBased) {
            gridDtb(i, j, k, l);
        } else {
            gridD(i, j, k, l);
        }
        if (s == 0 || d == 1) {
            return d;
        }
        List<List<Integer>> neighbours = getNeighboursForNode(edges, i, j);
        for (int m = 0; m < neighbours.size() && !cancel; m++) {
            List<Integer> node = neighbours.get(m);
            int localR = localD(edges, s - 1, node.get(0), node.get(1), k, l);
            d = localR < d ? localR : d;
        }
        return d;
    }

    private List<List<Integer>> getNeighboursForNode(boolean[][][][] edges, int i, int j) {
        List<List<Integer>> neighbours = new ArrayList<>();
        for (int k = 0; k < n && !cancel; k++) {
            for (int l = 0; l < n && !cancel; l++) {
                if (edges[i][j][k][l]) {
                    List<Integer> node = new ArrayList<>();
                    node.add(k);
                    node.add(l);
                    neighbours.add(node);
                }
            }
        }
        return neighbours;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public boolean isTorusBased() {
        return torusBased;
    }

    public void setTorusBased(boolean torusBased) {
        this.torusBased = torusBased;
    }

    @Override
    public String getName() {
        return "Kleinberg Small World model";
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public int getS() {
        return S;
    }

    public void setS(int S) {
        this.S = S;
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
