package com.codingchallenge.releasemanager.controller

import com.codingchallenge.releasemanager.domain.VersionTrackingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
class DeploymentListenerController(private val versionTrackingService: VersionTrackingService) {
    @PostMapping("/deploy")
    fun onDeploy(@RequestBody payload: DeploymentInfo): Mono<OverallVersionDto> {
        return versionTrackingService
            .newVersionDeployed(payload.serviceName, payload.serviceVersionNumber)
            .map { OverallVersionDto(it.versionId, it.serviceVersions.map { v -> DeployedVersion(v.key, v.value) }) }
    }

    @GetMapping("services")
    fun getVersion(@RequestParam("systemVersion") version: Long): Mono<List<DeployedVersion>> {
        return versionTrackingService
            .overallVersionByNumber(version)
            .switchIfEmpty(Mono.error(NotFoundException()))
            .map { it.serviceVersions.map { v -> DeployedVersion(v.key, v.value) } }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not found")
    @ExceptionHandler(NotFoundException::class)
    fun notFoundExceptionHandler() {
    }

}

data class DeployedVersion(val name: String, val version: Long)

data class DeploymentInfo(val serviceName: String, val serviceVersionNumber: Long)

data class OverallVersionDto(val version: Long, val versions: List<DeployedVersion>)