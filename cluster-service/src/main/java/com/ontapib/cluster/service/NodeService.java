package com.ontapib.cluster.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ontapib.cluster.model.Node;
import com.ontapib.cluster.model.Shelve;
import com.ontapib.cluster.repositroy.NodeRepository;

@Service
public class NodeService {
	
	@Autowired
	private NodeRepository nodeRepository;
	
	// Create Node
	public Node createNode(String nodeName, String version, String serialnumber, String model, 
			Date warrantyEndDate, List<Shelve> shelves) {
		return nodeRepository.save(new Node(nodeName, version, serialnumber, model, warrantyEndDate, shelves));
	}
	
	public Node createNode(Node node) {
		return nodeRepository.save(node);
	}
	
	// Get all Nodes
	public List<Node> getAllNodes() {
		return nodeRepository.findAll();
	}
	
}
