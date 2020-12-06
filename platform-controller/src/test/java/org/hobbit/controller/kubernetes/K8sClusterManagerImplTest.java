package org.hobbit.controller.kubernetes;

import io.fabric8.kubernetes.api.model.NodeListBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.hobbit.controller.orchestration.objects.ClusterInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

class K8sClusterManagerImplTest {

    @Rule
    KubernetesServer server = new KubernetesServer();

    @Rule
    K8sClusterManagerImpl clusterManager = new K8sClusterManagerImpl();

    @Before
    public void setUp() throws Exception {
        server.before();
        server.expect().withPath("/api/v1/nodes").andReturn(200, new NodeListBuilder().addNewItem().and().build()).once();
        server.expect().withPath("/api/v1/nodes").andReturn(200, new NodeListBuilder().addNewItem().and().build()).once();
        server.expect().withPath("/api/v1/nodes").andReturn(200, new NodeListBuilder().addNewItem().and().build()).once();

    }


    @Test
    void getClusterInfo() {
        final ClusterInfo info = clusterManager.getClusterInfo();
        assertNotNull(info);
    }

    @Test
    void getNumberOfNodes() {
        long numberOfNodes = clusterManager.getNumberOfNodes();
        assertEquals(3, numberOfNodes);
    }

    @Test
    void testGetNumberOfNodes() {
        long numberOfNodes = clusterManager.getNumberOfNodes("org.hobbit.workergroup=system");
        assertEquals(0, numberOfNodes);
    }

    @Test
    void isClusterHealthy() {
        boolean isHealthy = clusterManager.isClusterHealthy();
        assertTrue(isHealthy);
    }

    @Test
    void setTaskHistoryLimit() {
        clusterManager.setTaskHistoryLimit(0);
        Integer taskHistoryLimit = clusterManager.getTaskHistoryLimit();
        assertEquals(0, (long) taskHistoryLimit);
        //set back to default
        clusterManager.setTaskHistoryLimit(5);
    }

}
