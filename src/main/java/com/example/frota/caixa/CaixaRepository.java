package com.example.frota.caixa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {
}
