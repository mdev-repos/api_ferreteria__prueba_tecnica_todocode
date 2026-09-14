package com.mdev.ferreteria.repository;

import com.mdev.ferreteria.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IToolRepository extends JpaRepository<Tool, Long> {
}
