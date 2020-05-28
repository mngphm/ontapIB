package com.ontapib.cluster.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ontapib.cluster.model.Sod;
import com.ontapib.cluster.repositroy.SodRepository;

@Service
public class SodService {
	
	@Autowired
	private SodRepository sodRepository;
	
	public Sod createSod(Sod sod) {
		return sodRepository.save(sod);
	}
	
	public List<Sod> getAll() {
		return sodRepository.findAll();
	}

}
