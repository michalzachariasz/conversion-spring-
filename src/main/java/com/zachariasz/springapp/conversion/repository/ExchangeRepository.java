package com.zachariasz.springapp.conversion.repository;

import com.zachariasz.springapp.conversion.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ExchangeRepository extends JpaRepository<Exchange,String> {
    Exchange findByIdIgnoreCase(String id);
}
