package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.AnnIndexMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnIndexMetadataRepository extends JpaRepository<AnnIndexMetadata, Long> {

    Optional<AnnIndexMetadata> findByIndexNameAndStatus(String indexName, String status);

    List<AnnIndexMetadata> findAllByIndexNameOrderByBuiltAtDesc(String indexName);
}
