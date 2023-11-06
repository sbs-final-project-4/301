package com.yk.Motivation.domain.order.repository;

import com.yk.Motivation.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}