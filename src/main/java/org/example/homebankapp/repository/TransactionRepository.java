package org.example.homebankapp.repository;

import org.example.homebankapp.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    @Query("{ '$or': [ { 'accountId': ?0 }, { 'toAccountId': ?0 } ] }")
    Page<Transaction> findAllByParticipantAccountId(String accountId, Pageable pageable);
}
