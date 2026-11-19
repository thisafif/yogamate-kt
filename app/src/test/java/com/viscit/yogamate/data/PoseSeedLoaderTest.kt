package com.viscit.yogamate.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PoseSeedLoaderTest {

    @Test
    fun seed_csv_memiliki_mapping_15_pose_yang_utuh() {
        val csv = File("src/main/assets/yoga_poses_seed.csv").readText(Charsets.UTF_8)
        val poses = PoseSeedLoader.parse(csv)

        assertEquals(15, poses.size)
        assertEquals((0..14).toList(), poses.map { it.labelId })
        assertEquals((0..14).toList(), poses.map { it.classIndex })
        assertEquals(15, poses.map { it.slug }.toSet().size)
        assertTrue(poses.all { it.steps.size >= 3 })
        assertTrue(poses.all { it.classLabel.isNotBlank() && it.description.isNotBlank() })
    }
}
