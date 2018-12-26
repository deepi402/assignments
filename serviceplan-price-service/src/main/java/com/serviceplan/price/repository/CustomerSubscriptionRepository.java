package com.serviceplan.price.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.serviceplan.price.entity.CustomerSubscription;

@Repository
public interface CustomerSubscriptionRepository extends CrudRepository<CustomerSubscription, Integer> {

}
