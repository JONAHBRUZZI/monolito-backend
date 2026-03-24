package com.ecommerce.monolith.repository;

import com.ecommerce.monolith.model.CustomerOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Override
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<CustomerOrder> findAll();

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<CustomerOrder> findWithItemsById(Long id);

    @EntityGraph(attributePaths = {"items", "items.product"})
    List<CustomerOrder> findAllByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(String customerEmail);
}
