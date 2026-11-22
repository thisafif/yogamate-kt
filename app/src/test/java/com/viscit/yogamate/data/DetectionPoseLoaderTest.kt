package com.viscit.yogamate.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DetectionPoseLoaderTest {
    @Test
    fun metadata_bom_menghasilkan_82_kelas_berurutan_dan_unik() {
        val csv = File("src/main/assets/yoga82_difficulty_labels.csv").readText(Charsets.UTF_8)
        val poses = DetectionPoseLoader.parse(csv)
        assertEquals((0..81).toList(), poses.map { it.classId })
        assertEquals(82, poses.map { it.name }.toSet().size)
        assertTrue(poses.all { it.name.isNotBlank() })
    }
}
