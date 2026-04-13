package blbl.cat3399.core.update

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApkUpdaterVersionTest {
    @Test
    fun autoIncrementedBuildVersionIsDetectedAsNewer() {
        assertTrue(ApkUpdater.isRemoteNewer(remoteVersionName = "0.1.0.102", currentVersionName = "0.1.0.101"))
    }

    @Test
    fun snapshotSuffixDoesNotBreakVersionComparison() {
        assertTrue(
            ApkUpdater.isRemoteNewer(
                remoteVersionName = "0.1.0.103-feature-abcdef0",
                currentVersionName = "0.1.0.102",
            ),
        )
    }

    @Test
    fun sameVersionIsNotNewer() {
        assertFalse(ApkUpdater.isRemoteNewer(remoteVersionName = "0.1.0.103", currentVersionName = "0.1.0.103"))
    }

    @Test
    fun invalidRemoteVersionIsIgnored() {
        assertFalse(ApkUpdater.isRemoteNewer(remoteVersionName = "snapshot-main", currentVersionName = "0.1.0.103"))
    }
}
