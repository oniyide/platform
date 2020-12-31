package org.hobbit.controller.kubernetes;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.hobbit.controller.orchestration.ClusterManager;
import org.hobbit.controller.orchestration.objects.ClusterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class K8sClusterManagerImpl implements ClusterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(K8sClusterManagerImpl.class);

    /**
     * Kubernetes client instance
     */
    private KubernetesClient k8sClient;


    private long expectedNumberOfNodes = 0;
    private String K8S_NODE_NUMBER = null;

    public K8sClusterManagerImpl() {
        this.k8sClient = K8sUtility.getK8sClient();

        K8S_NODE_NUMBER = System.getenv("K8S_NODE_NUMBER");
        if (K8S_NODE_NUMBER == null){
            expectedNumberOfNodes = 1;
        }else{
            expectedNumberOfNodes = Integer.parseInt(K8S_NODE_NUMBER);
        }

    }
    public KubernetesClient getK8sClient() {
        return k8sClient;
    }

    public void setK8sClient(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
    }


    @Override
    public ClusterInfo getClusterInfo() {
        Config config =  k8sClient.getConfiguration();
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setHttpProxy(config.getHttpProxy());
        clusterInfo.setHttpsProxy(config.getHttpsProxy());

        return clusterInfo;
    }

    @Override
    public long getNumberOfNodes() {
        return k8sClient.nodes().list().getItems().size();
    }

    @Override
    public long getNumberOfNodes(String label) {
        String[] key_value = null;
        key_value = label.split("=");
        long numberOfNodes = 0;
        try {
            numberOfNodes = k8sClient.nodes().withLabelIn(key_value[0], key_value[1]).list().getItems().size();
        }catch (Exception e)
        {
            LOGGER.info(e.getMessage());
        }
        return numberOfNodes;
    }

    @Override
    public boolean isClusterHealthy() {
        long numberOfNodes = getNumberOfNodes();
        if (numberOfNodes >= expectedNumberOfNodes){
            return true;
        }
        LOGGER.debug("Cluster is not healthy ({}/{})",numberOfNodes, expectedNumberOfNodes);
        return false;
    }

    @Override
    public long getExpectedNumberOfNodes() {
        return expectedNumberOfNodes;
    }


    public void setTaskHistoryLimit(Integer taskHistoryLimit){

        DeploymentList deployments = k8sClient.apps().deployments().inAnyNamespace().list();
        System.out.println(deployments);
        deployments.getItems().forEach(
            d -> d.getSpec().setRevisionHistoryLimit(taskHistoryLimit)
        );
    }

    public int getTaskHistoryLimit(){
        // revision history limit is set at deployment level not cluster level
        // returning the average revision history across the deployments in the cluster instead
        DeploymentList deployments = k8sClient.apps().deployments().inAnyNamespace().list();
        int taskHistoryLimit = 0;
        for (Deployment d : deployments.getItems()){
            taskHistoryLimit += d.getSpec().getRevisionHistoryLimit();
        }
        taskHistoryLimit = taskHistoryLimit/deployments.getItems().size();
        return taskHistoryLimit;
    }

}