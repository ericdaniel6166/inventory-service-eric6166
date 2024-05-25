package com.eric6166.inventory.repository;

import com.eric6166.inventory.dto.InventoryDto;
import com.eric6166.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("""
            select new com.eric6166.inventory.dto.InventoryDto(p.id, p.price, i.id, i.quantity) 
            from Product p 
            inner join Inventory i on p.id = i.productId 
            where p.id in :productIdList
            """)
    List<InventoryDto> findAllInventoryByProductIdIn(Iterable<Long> productIdList);
}
