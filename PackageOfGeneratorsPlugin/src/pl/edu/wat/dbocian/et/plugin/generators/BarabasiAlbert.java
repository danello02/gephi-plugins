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

import java.awt.Color;
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

/**
 * @author Daniel Bocian
 *
 * based on Cezary Bartosiak implementation:
 * https://github.com/cbartosiak/gephi-plugins/tree/complex-generators
 *
 * More info about algorithm:
 * http://en.wikipedia.org/wiki/Barab%C3%A1si%E2%80%93Albert_model
 *
 */
@ServiceProvider(service = Generator.class)
public class BarabasiAlbert implements Generator {

    private boolean cancel = false;

    private ProgressTicket progressTicket;
    private final Random random;

    private int N = 200;
    private int m0 = 1;
    private int M = 1;
    private int ageingType = 0; //0, 1,2,3

    private boolean randomise = true;
    private double startAge = 0.2;
    private double growingInterval = 0.1;
    private double ageingInterval = 0.1;

    public BarabasiAlbert() {
        random = new Random();
    }

    @Override
    public void generate(ContainerLoader container) {
        if (M > m0) {
            return;
        }

        Progress.start(progressTicket, m0 + (N - m0) * M);
        container.setEdgeDefault(EdgeDefault.UNDIRECTED);

        //double sumDegrees = 0.0; //sum of all nodes degrees
        // Timestamps
        int vt = 1;
        int et = 1;

        //Array of nodes and his degrees
        NodeDraft[] nodes = new NodeDraft[N];
        int[] degrees = new int[N];

        //Array of age:
        //age[N][0] - probability - <0.0, 1>
        //age[N][1] - growing interval - <0.0, 0.1>
        //age[N][2] - ageing interval - <0.0, 0.1> for line ageing, 
        //                              <0.0, inf) for exponential
        double[][] age = new double[N][3];

        //Create initial m0 nodes
        for (int i = 0; i < m0 && !cancel; i++) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            node.setColor(Color.yellow);
            node.addTimeInterval("0", (N - m0) + "");
            nodes[i] = node;
            degrees[i] = 0;

            age[i] = createAgeData();

            container.addNode(node);
            Progress.progress(progressTicket);
        }

        //Add edge between initial nodes to make complete graph
        for (int i = 0; i < m0 && !cancel; i++) {
            for (int j = i + 1; j < m0 && !cancel; j++) {
                EdgeDraft edge = container.factory().newEdgeDraft();
                edge.setSource(nodes[i]);
                edge.setTarget(nodes[j]);
                edge.addTimeInterval("0", (N - m0) + "");
                degrees[i]++;
                degrees[j]++;
                container.addEdge(edge);
            }
        }

        //Add other nodes with M edges for each one
        for (int i = m0; i < N && !cancel; i++, vt++, et++) {
            //Add node 
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            node.addTimeInterval(vt + "", (N - m0) + "");
            nodes[i] = node;
            degrees[i] = 0;

            age[i] = createAgeData();

            container.addNode(node);

            //Add M edge out of the new node
            for (int m = 0; m < M && !cancel;) {

                double norm = 0.0;

                double sumDegrees = 0.0;
                for (int j = 0; j < i && !cancel; j++) {
                    if (container.edgeExists(nodes[i], nodes[j]) || container.edgeExists(nodes[j], nodes[i])) {
                        continue;
                    }
                    sumDegrees += degrees[j];
                    norm += degrees[j] * age[j][0];
                    if (Double.isInfinite(norm)) {
                        norm = Double.MIN_VALUE;
                    }
                }

                if (sumDegrees != 0.0) {
                    norm /= sumDegrees;
                    if (Double.isInfinite(norm)) {
                        norm = Double.MIN_VALUE;
                    }
                }

                double r = random.nextDouble() * norm;
                if (Double.isInfinite(r)) {
                    r = Double.MIN_VALUE;
                }
                double p = 0.0;

                //choose node
                for (int j = 0; j < i && !cancel; j++) {
                    if (container.edgeExists(nodes[i], nodes[j]) || container.edgeExists(nodes[j], nodes[i])) {
                        continue;
                    }

                    if (i == 1) {
                        p = 1.0;
                    } else {
                        p += degrees[j] / sumDegrees * age[j][0];
                        if (Double.isInfinite(p)) {
                            p = Double.MIN_VALUE;
                        }
                    }

                    if (r <= p) {
                        EdgeDraft edge = container.factory().newEdgeDraft();
                        edge.setSource(nodes[i]);
                        edge.setTarget(nodes[j]);
                        edge.addTimeInterval(et + "", (N - m0) + "");
                        degrees[i]++;
                        degrees[j]++;
                        container.addEdge(edge);
                        Progress.progress(progressTicket);

                        j = i;  //break loop
                        m++;    //go to next edge
                    }
                }
            }
            Progress.progress(progressTicket);

            //Change age of all existing nodes beyond just added 
            //(ageingType == 0 mean none ageing)
            if (ageingType != 0) {
                switch (ageingType) {
                    case 1:
                        //Line groiwng and ageing
                        for (int j = 0; j < i && !cancel; j++) {
                            if (age[j][1] >= 0.0) {
                                if (age[j][0] + age[j][1] > 1.0) {
                                    age[j][0] = 1.0;
                                    age[j][1] = -1.0;
                                } else {
                                    age[j][0] += age[j][1];
                                }
                            } else if (age[j][2] >= 0.0) {
                                if (age[j][0] - age[j][2] < 0.2) {
                                    age[j][0] = 0.2;
                                    age[j][2] = -1.0;
                                } else {
                                    age[j][0] -= age[j][2];
                                }
                            }
                        }
                        break;

                    case 2:
                        //Exponential function
                        for (int j = 0; j < i && !cancel; j++) {
                            age[j][0] *= age[j][2];
                            if (Double.isInfinite(age[j][0])) {
                                age[j][0] = Double.MIN_VALUE;
                            }
                        }
                        break;

                    case 3:
                        //Homographic transformations
                        for (int j = 0; j < i && !cancel; j++) {
                            age[j][0] = age[j][1] / ++age[j][2];
                            if (Double.isInfinite(age[j][0])) {
                                age[j][0] = Double.MIN_VALUE;
                            }
                        }
                        break;

                    default:
                        ; //None ageing
                        break;

                }
            }
        }
    }

    private double[] createAgeData() {
        double[] age = new double[3];
        if (randomise) {
            //Random age parameters for node
            age[0] = random.nextDouble();
            age[1] = random.nextDouble();
            age[2] = ageingType == 2 ? random.nextDouble() * random.nextInt(Integer.MAX_VALUE) : random.nextDouble();
        } else {
            //Init age data for node
            age[0] = startAge;
            age[1] = growingInterval;
            age[2] = ageingInterval;
        }

        //Homographic function use age table in diffrent way.
        //age[i][0] - acctually age - [0,1]
        //age[i][1] - start age - [0,1]
        //age[i][2] - iteration count - int
        if (ageingType == 3) {
            age[1] = age[0];
            age[2] = 1.0;
        }
        return age;
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

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public int getAgeingType() {
        return ageingType;
    }

    public void setAgeingType(int ageingType) {
        this.ageingType = ageingType;
    }

    public boolean isRandomise() {
        return randomise;
    }

    public void setRandomise(boolean randomise) {
        this.randomise = randomise;
    }

    public double getStartAge() {
        return startAge;
    }

    public void setStartAge(double startAge) {
        this.startAge = startAge;
    }

    public double getGrowingInterval() {
        return growingInterval;
    }

    public void setGrowingInterval(double growingInterval) {
        this.growingInterval = growingInterval;
    }

    public double getAgeingInterval() {
        return ageingInterval;
    }

    public void setAgeingInterval(double ageingInterval) {
        this.ageingInterval = ageingInterval;
    }

    @Override
    public String getName() {
        return "Barabasi-Albert Scale Free model";
    }

    //TODO - zwracanie widoku
    @Override
    public GeneratorUI getUI() {
        //return Lookup.getDefault().lookup(AgeingBAModelUI.class);
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
