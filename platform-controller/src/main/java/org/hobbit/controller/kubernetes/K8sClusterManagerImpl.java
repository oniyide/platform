package org.hobbit.controller.kubernetes;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.CallGeneratorParams;
import io.kubernetes.client.util.ClientBuilder;
import org.hobbit.controller.docker.ClusterManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class K8sClusterManagerImpl implements K8sClusterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterManagerImpl.class);

    private ApiClient k8sclient;
    private CoreV1Api api;
    private SharedInformerFactory factory;

    private long expectedNumberOfPods = 0;
    private SharedIndexInformer<V1Node> nodeInformer;

    private String K8S_PODS_NUMBER = null;

    private String NAMESPACE = System.getenv("K8S_NAMESPACE");

    public K8sClusterManagerImpl() throws IOException, ApiException {
        k8sclient = ClientBuilder.cluster().build();
        Configuration.setDefaultApiClient(k8sclient);
        api = new CoreV1Api();

        K8S_PODS_NUMBER = System.getenv("K8S_PODS_NUMBER");

        if (NAMESPACE == null || "".equals(NAMESPACE)) {
            NAMESPACE = "default";
        }

        factory = new SharedInformerFactory();

        nodeInformer =
            factory.sharedIndexInformerFor(
                (CallGeneratorParams params) -> {
                    try {
                        return api.listNodeCall(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            params.resourceVersion,
                            params.timeoutSeconds,
                            params.watch,
                            null,
                            null);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                    return null;
                },
                V1Node.class,
                V1NodeList.class);


        expectedNumberOfPods = Integer.parseInt(K8S_PODS_NUMBER);
    }


    @Override
    public V1PodList getPodsInfo() throws ApiException {

        V1PodList list = api.listPodForAllNamespaces(null, null, null, null,
                                                    null, null, null, null, null);
        // k8sclient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        // Parameters are currently commented because I do not know the right values yet

        return list;
    }

    @Override
    public long getNumberOfNodes() {
        // Node informer

        Lister<V1Node> nodeLister = new Lister<V1Node>(nodeInformer.getIndexer());

        return nodeLister.list().size();
    }

    @Override
    public int getNumberOfNodes(String label) {

        Lister<V1Node> nodeLister = new Lister<V1Node>(nodeInformer.getIndexer());
        V1Node node = nodeLister.get(label);

        if (node != null)
            return 1;
        return 0;
    }

    public boolean isClusterHealthy() {
        long numberOfPods = getNumberOfNodes();
        if(numberOfPods >= expectedNumberOfPods) {
            return true;
        }
        LOGGER.debug("Cluster is not healthy ({}/{})",numberOfPods, expectedNumberOfPods);
        return false;
    }

    @Override
    public V1PersistentVolumeClaimList getPVCs(String namespace) throws ApiException {

        V1PersistentVolumeClaimList list = api.listNamespacedPersistentVolumeClaim(namespace, null, null, null, null, null, null, null, null, null);

        return list;
    }
}
