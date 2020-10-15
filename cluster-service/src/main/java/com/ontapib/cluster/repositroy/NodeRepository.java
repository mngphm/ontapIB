package com.ontapib.cluster.repositroy;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ontapib.cluster.model.Node;

public interface NodeRepository extends MongoRepository<Node, String> {
	public Node findBySerialNumber(String serialNumber);
}
