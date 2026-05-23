package com.litert.coach.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.litert.coach.data.db.AppDatabase
import com.litert.coach.data.db.entity.AiContextSummaryEntity
import com.litert.coach.data.db.entity.ChatMessageEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatDaoTest {
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() = db.close()

    private fun makeMessage(role: String, content: String) = ChatMessageEntity(
        role = role, content = content
    )

    @Test
    fun insert_and_getUnsummarized_returns_all_by_default() = runTest {
        val dao = db.chatDao()
        dao.insert(makeMessage("user", "Hello"))
        dao.insert(makeMessage("assistant", "Hi there!"))
        val unsummarized = dao.getUnsummarized()
        assertEquals(2, unsummarized.size)
    }

    @Test
    fun markSummarized_hides_messages_from_getUnsummarized() = runTest {
        val dao = db.chatDao()
        val id1 = dao.insert(makeMessage("user", "Message 1")).toInt()
        val id2 = dao.insert(makeMessage("assistant", "Reply 1")).toInt()
        dao.insert(makeMessage("user", "Message 2"))

        dao.markSummarized(id2) // mark first two as summarized

        val unsummarized = dao.getUnsummarized()
        assertEquals(1, unsummarized.size)
        assertEquals("Message 2", unsummarized[0].content)
    }

    @Test
    fun insertSummary_and_getLatestSummary() = runTest {
        val dao = db.chatDao()
        dao.insert(makeMessage("user", "test"))
        val msgId = dao.insert(makeMessage("assistant", "reply")).toInt()

        dao.insertSummary(AiContextSummaryEntity(
            summaryText = "User asked about training",
            coversToMessageId = msgId
        ))

        val latest = dao.getLatestSummary()
        assertNotNull(latest)
        assertEquals("User asked about training", latest!!.summaryText)
        assertEquals(msgId, latest.coversToMessageId)
    }

    @Test
    fun clearAll_removes_all_messages() = runTest {
        val dao = db.chatDao()
        dao.insert(makeMessage("user", "Hello"))
        dao.insert(makeMessage("assistant", "Hi"))
        dao.clearAll()
        assertEquals(0, dao.getAll().size)
    }
}
