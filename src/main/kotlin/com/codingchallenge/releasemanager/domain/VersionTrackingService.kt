package com.codingchallenge.releasemanager.domain

import com.codingchallenge.releasemanager.data.AppVersionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class VersionTrackingService(private val versionRepository: AppVersionRepository) {

    fun newVersionDeployed(service: String, version: Long): Mono<OverallVersion> {
        return versionRepository
            .findLatest()
            .defaultIfEmpty(OverallVersion(0, mapOf()))
            .flatMap { currentVersion ->
                if (currentVersion.includesVersion(service, version)) Mono.just(currentVersion)
                else {
                    lastVersionAfterDeploy(currentVersion, service, version).switchIfEmpty {
                        versionRepository.save(currentVersion.withIncrement(service, version))
                    }
                }
            }
    }

    fun lastVersionAfterDeploy(currentVersion: OverallVersion, service: String, version: Long): Mono<OverallVersion> {
        val serviceVersions = HashMap(currentVersion.serviceVersions)
        serviceVersions[service] = version
        return versionRepository.findLatest(serviceVersions)
    }

    fun overallVersionByNumber(id: Long): Mono<OverallVersion> {
        return versionRepository.find(id)
    }
}