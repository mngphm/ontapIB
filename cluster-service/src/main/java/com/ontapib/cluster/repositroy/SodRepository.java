package com.ontapib.cluster.repositroy;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ontapib.cluster.model.Sod;

public interface SodRepository extends MongoRepository<Sod, String> {
	
}