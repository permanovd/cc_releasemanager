package com.codingchallenge.releasemanager.data

import com.codingchallenge.releasemanager.domain.OverallVersion
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

interface AppVersionRepository {
    fun findLatest(): Mono<OverallVersion>
    fun findLatest(versions: Map<String, Long>): Mono<OverallVersion>
    fun find(id: Long): Mono<OverallVersion>
    fun save(version: OverallVersion): Mono<OverallVersion>
}

@Component
class MongoTemplateAppVersionRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
    @Value("service.storage.collection") private val collection: String
) : AppVersionRepository {
    override fun findLatest(): Mono<OverallVersion> {
        return mongoTemplate.findOne<OverallVersion>(
            Query().with(Sort.by(Sort.Direction.DESC, "versionId")).limit(1), collection
        )
    }

    override fun findLatest(versions: Map<String, Long>): Mono<OverallVersion> {
        // TODO Escape vars.
        var criteria = Criteria()
        for ((serviceName, verNumber) in versions) {
            criteria = criteria.and("serviceVersions.$serviceName").`is`(verNumber)
        }
        return mongoTemplate.findOne<OverallVersion>(
            query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "versionId"))
                .limit(1),
            collection
        )
    }

    override fun find(id: Long): Mono<OverallVersion> {
        return mongoTemplate.findOne<OverallVersion>(query(Criteria.where("versionId").`is`(id)), collection)
    }

    override fun save(version: OverallVersion): Mono<OverallVersion> {
        return mongoTemplate.insert(version, collection)
    }
}