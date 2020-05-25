package com.ontapib.cluster.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ontapib.cluster.model.Node;
import com.ontapib.cluster.service.NodeService;

public class NodeController {
	@Autowired
	private NodeService nodeService;
	
	@RequestMapping("/node/nodes")
	public List<Node> getAllNodes() {
		return nodeService.getAllNodes();
	}
	
	public NodeController() {
		// TODO Auto-generated constructor stub
	}

}
