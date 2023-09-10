package com.codingchallenge.releasemanager.domain

data class OverallVersion(val versionId: Long, val serviceVersions: Map<String, Long>) {
    fun includesVersion(service: String, version: Long): Boolean {
        return this.serviceVersions.getOrDefault(service, -1) == version
    }

    fun withIncrement(service: String, version: Long): OverallVersion {
        val newVersions = HashMap(serviceVersions)
        newVersions[service] = version
        return copy(versionId = versionId + 1, serviceVersions = newVersions)
    }
}