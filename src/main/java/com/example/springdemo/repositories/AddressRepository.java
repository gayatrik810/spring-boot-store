package com.example.springdemo.repositories;

import com.example.springdemo.entities.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}