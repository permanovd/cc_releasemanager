package com.codingchallenge.releasemanager

import com.codingchallenge.releasemanager.domain.VersionTrackingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VersioningServiceTest {
    @Test
    fun `does not increment version if there is one in storage`() {
        val versionTrackingService = VersionTrackingService(InMemoryVersionRepo())

        assertThat(versionTrackingService.newVersionDeployed("service a", 1).block()?.versionId).isEqualTo(1)
        assertThat(versionTrackingService.newVersionDeployed("service b", 1).block()?.versionId).isEqualTo(2)
        assertThat(versionTrackingService.newVersionDeployed("service a", 2).block()?.versionId).isEqualTo(3)
        assertThat(versionTrackingService.newVersionDeployed("service b", 1).block()?.versionId).isEqualTo(3)
    }

    @Test
    fun `does not increment version if there is one in storage after big number of ops`() {
        val versionTrackingService = VersionTrackingService(InMemoryVersionRepo())

        assertThat(versionTrackingService.newVersionDeployed("service a", 1).block()?.versionId).isEqualTo(1)
        assertThat(versionTrackingService.newVersionDeployed("service b", 1).block()?.versionId).isEqualTo(2)
        repeat(200) {
            versionTrackingService.newVersionDeployed("service a", it.toLong()).block()
            versionTrackingService.newVersionDeployed("service b", it.toLong()).block()
        }

        versionTrackingService.newVersionDeployed("service a", 1).block()
        val version = versionTrackingService.newVersionDeployed("service b", 1).block()
        assertThat(version?.versionId).isEqualTo(2)
    }

    @Test
    fun `increments version if services version change`() {
        val versionTrackingService = VersionTrackingService(InMemoryVersionRepo())
        for (i in 1..200) {
            versionTrackingService.newVersionDeployed("service a", i.toLong()).block()
        }
        assertThat(versionTrackingService.newVersionDeployed("service a", 201).block()?.versionId).isEqualTo(201)
        // Back to 5.
        assertThat(versionTrackingService.newVersionDeployed("service a", 5).block()?.versionId).isEqualTo(5)
    }
}