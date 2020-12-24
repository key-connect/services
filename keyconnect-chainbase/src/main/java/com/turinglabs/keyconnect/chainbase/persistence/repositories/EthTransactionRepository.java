package com.turinglabs.keyconnect.chainbase.persistence.repositories;

import com.turinglabs.keyconnect.chainbase.persistence.models.EthTransaction;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EthTransactionRepository extends PagingAndSortingRepository<EthTransaction, String> {

  Optional<EthTransaction> findTopByHash(String hash);
}
