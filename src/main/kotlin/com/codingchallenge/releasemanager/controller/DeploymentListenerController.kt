package com.codingchallenge.releasemanager.controller

import com.codingchallenge.releasemanager.domain.VersionTrackingService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import kotlin.Exception


@RestController
class DeploymentListenerController(private val versionTrackingService: VersionTrackingService) {
    private val log = KotlinLogging.logger {}
    @PostMapping("/deploy")
    fun onDeploy(@RequestBody payload: DeploymentInfo): Mono<OverallVersionDto> {
        log.info { "deploy is notified with payload: $payload" }
        return versionTrackingService
            .newVersionDeployed(payload.serviceName, payload.serviceVersionNumber)
            .map { OverallVersionDto(it.versionId, it.serviceVersions.map { v -> DeployedVersion(v.key, v.value) }) }
    }

    @GetMapping("services")
    fun getVersion(@RequestParam("systemVersion") version: Long): Mono<List<DeployedVersion>> {
        log.info { "Version fetch is called with $version" }
        return versionTrackingService
            .overallVersionByNumber(version)
            .switchIfEmpty(Mono.error(NotFoundException()))
            .map { it.serviceVersions.map { v -> DeployedVersion(v.key, v.value) } }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not found")
    @ExceptionHandler(NotFoundException::class)
    fun notFoundExceptionHandler() {
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to process request")
    @ExceptionHandler(Exception::class)
    fun unexpectedExceptionHandler(e: Exception): String {
        log.warn(e) { "Failed to process request" }
        return "Failed to process request"
    }
}

data class DeployedVersion(val name: String, val version: Long)

data class DeploymentInfo(val serviceName: String, val serviceVersionNumber: Long)

data class OverallVersionDto(val version: Long, val versions: List<DeployedVersion>)