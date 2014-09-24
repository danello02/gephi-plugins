/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.daniel.generator.bamodel.ageing;

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
 * Implementation of this generator base on generalized BA Model.
 * Algorithm was modified with ageing and growing functions which
 * develop orginal preferential attachment of nodes.
 * 
 * @author Daniel Bocian
 */
@ServiceProvider(service = Generator.class)
public class AgeingBAModel implements Generator {

    private boolean cancel = false;

    private ProgressTicket progressTicket;

    private int N = 200;
    private int m0 = 1;
    private int M = 1;
    private int ageingType = 1; //1,2,3

    private boolean randomise = true;
    private double startAge = 0.2;
    private double growingInterval = 0.1;
    private double ageingInterval = 0.1;

    public AgeingBAModel() {
    }

    @Override
    public void generate(ContainerLoader container) {
        if (M > m0) {
            return;
        }

        Progress.start(progressTicket, m0 + (N - m0) * M);
        Random random = new Random();
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

            if (randomise) {
                //Random age parameters for node
                age[i][0] = random.nextDouble();
                age[i][1] = random.nextDouble() / 2;
                age[i][2] = random.nextDouble() / 2;
            } else {
                //Init age data
                age[i][0] = startAge;
                age[i][1] = growingInterval;
                age[i][2] = ageingInterval;
            }

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

            if (randomise) {
                //Random age parameters for node
                age[i][0] = random.nextDouble();
                age[i][1] = random.nextDouble() / 2;
                age[i][2] = random.nextDouble() / 2;
            } else {
                //Init age data for node
                age[i][0] = startAge;
                age[i][1] = growingInterval;
                age[i][2] = ageingInterval;
            }

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
                }
                if (sumDegrees != 0.0) {
                    norm /= sumDegrees;
                }

                double r = random.nextDouble() * norm;
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
                    }
                    break;

                case 3:
                    //Homographic transformations
                    for (int j = 0; j < i && !cancel; j++) {
                        age[j][0] /= i;
                    }
                    break;

                default:
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

            }
        }
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
        return "BA Model with getting old nodes";
    }

    @Override
    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(AgeingBAModelUI.class);
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
