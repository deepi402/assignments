package com.serviceplan.price.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.serviceplan.price.entity.ServicePlan;

@Repository
public interface ServicePlanRepository extends CrudRepository<ServicePlan, Integer> {

}
