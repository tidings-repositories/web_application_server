package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Report;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends MongoRepository<Report, ObjectId> {
}
