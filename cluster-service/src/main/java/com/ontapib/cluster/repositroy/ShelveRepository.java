package com.ontapib.cluster.repositroy;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ontapib.cluster.model.Shelve;

public interface ShelveRepository extends MongoRepository<Shelve, String> {

}
