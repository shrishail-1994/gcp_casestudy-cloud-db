package com.example.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


public interface ImageRepository extends CrudRepository<Picture, Integer> {
	//Optional<Picture> findByName(String name);
}
