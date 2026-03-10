package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 31: ANN 인덱스 라이프사이클 메타데이터 — ANN Index Builder HNSW/FAISS (DOC_31)
 */
@Entity
@Table(name = "ann_index_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnnIndexMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 인덱스 식별자 (예: simclusters_v2, twhin_user, twhin_post)
    @Column(name = "index_name", length = 100, nullable = false)
    private String indexName;

    // 인덱스 버전 (빌드 타임스탬프 기반)
    @Column(name = "version", length = 50, nullable = false)
    private String version;

    // HNSW / FAISS / SCANN
    @Column(name = "algorithm_type", length = 20)
    private String algorithmType;

    // 벡터 차원 수
    @Column(name = "dimension")
    private Integer dimension;

    // 인덱싱된 벡터 수
    @Column(name = "vector_count")
    private Long vectorCount;

    // BUILDING / READY / DEPRECATED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "BUILDING";

    @Column(name = "built_at")
    private LocalDateTime builtAt;

    @Column(name = "deployed_at")
    private LocalDateTime deployedAt;

    @Column(name = "deprecated_at")
    private LocalDateTime deprecatedAt;
}
