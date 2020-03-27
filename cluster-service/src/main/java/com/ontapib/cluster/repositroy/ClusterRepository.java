package com.ontapib.cluster.repositroy;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ontapib.cluster.model.Cluster;

public interface ClusterRepository extends MongoRepository<Cluster, String> {

}
