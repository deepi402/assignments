package com.serviceplan.price.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.serviceplan.price.entity.CustomerAccount;

@Repository
public interface CustomerAccountRepository extends CrudRepository<CustomerAccount, Integer> {

}
