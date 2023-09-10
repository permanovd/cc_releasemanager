package com.codingchallenge.releasemanager

import com.codingchallenge.releasemanager.data.AppVersionRepository
import com.codingchallenge.releasemanager.domain.OverallVersion
import reactor.core.publisher.Mono

class InMemoryVersionRepo : AppVersionRepository {
    private val storage = sortedMapOf<Long, OverallVersion>(Long::compareTo)

    init {
        storage[0] = OverallVersion(0, mapOf())
    }

    override fun findLatest(): Mono<OverallVersion> {
        return Mono.just(storage[storage.lastKey()]!!)
    }

    override fun findLatest(versions: Map<String, Long>): Mono<OverallVersion> {
        val value = storage.filterValues { v ->
            versions.all { v.includesVersion(it.key, it.value) }
        }.entries.firstOrNull()?.value
        return Mono.justOrEmpty(value)
    }

    override fun find(id: Long): Mono<OverallVersion> {
        return Mono.justOrEmpty(storage[id])
    }

    override fun save(version: OverallVersion): Mono<OverallVersion> {
        storage[version.versionId] = version
        return Mono.just(version)
    }
}