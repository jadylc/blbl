package blbl.cat3399.core.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApkUpdaterVersionTest {
    @Test
    fun parseReleaseInfoReadsVersionAndApkUrl() {
        val info =
            ApkUpdater.parseReleaseInfo(
                """
{
  "version": "v0.1.0.104",
  "apk_url": "https://example.com/updates/blbl-0.1.0.104-release.apk",
  "notes": "修复若干问题"
}
                """.trimIndent(),
            )

        assertEquals("0.1.0.104", info.versionName)
        assertEquals("https://example.com/updates/blbl-0.1.0.104-release.apk", info.downloadUrl)
        assertEquals("blbl-0.1.0.104-release.apk", info.assetName)
    }

    @Test(expected = IllegalStateException::class)
    fun parseReleaseInfoRejectsMissingVersion() {
        ApkUpdater.parseReleaseInfo(
            """
{
  "apk_url": "https://example.com/updates/blbl-0.1.0.104-release.apk"
}
            """.trimIndent(),
        )
    }

    @Test(expected = IllegalStateException::class)
    fun parseReleaseInfoRejectsMissingApkUrl() {
        ApkUpdater.parseReleaseInfo(
            """
{
  "version": "0.1.0.104"
}
            """.trimIndent(),
        )
    }

    @Test(expected = IllegalStateException::class)
    fun parseReleaseInfoRejectsInvalidVersion() {
        ApkUpdater.parseReleaseInfo(
            """
{
  "version": "snapshot-main",
  "apk_url": "https://example.com/updates/blbl-snapshot-release.apk"
}
            """.trimIndent(),
        )
    }

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
