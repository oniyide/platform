package org.hobbit.controller.kubernetes;

//import io.kubernetes.client.ApiException;
//import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.models.V1PodList;

import java.util.List;

public interface K8sClusterManager {

    V1PodList getPodsInfo() throws ApiException;

    long getNumberOfNodes();

    int getNumberOfNodes(String label);

    V1PersistentVolumeClaimList getPVCs(String namespace) throws ApiException;

    //Olu
    public boolean isClusterHealthy();


}
