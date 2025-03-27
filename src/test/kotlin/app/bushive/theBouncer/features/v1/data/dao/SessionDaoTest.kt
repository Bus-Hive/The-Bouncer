import app.bushive.theBouncer.core.result.ResultHandler.isFailure
import app.bushive.theBouncer.core.result.ResultHandler.isSuccess
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.di.configureKoinUnitTest
import app.bushive.theBouncer.features.v1.data.local.dao.session.SessionDao
import app.bushive.theBouncer.features.v1.data.local.dao.user.UserDao
import app.bushive.theBouncer.features.v1.data.local.model.AuthProvider
import app.bushive.theBouncer.features.v1.data.local.model.Session
import app.bushive.theBouncer.features.v1.data.local.model.User
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID

class SessionDaoTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var sessionDao: SessionDao
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        sessionDao = get()
        userDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun getAllSessions_returnsAllSessions() =
        runBlocking {
            val user1 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        email = "u2",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )

            val user2 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        email = "u2",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            val sessions =
                listOf(
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user1.getDataOrNull()?.id,
                        refreshToken = "token1",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user2.getDataOrNull()?.id,
                        refreshToken = "token2",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            sessionDao.addSessions(sessions)
            val result = sessionDao.getAllSessions()
            assertTrue(result.isSuccess())
            assertEquals(sessions.size, result.getDataOrNull()?.size)
        }

    @Test
    fun getSessionById_returnsSession() =
        runBlocking {
            val user1 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )

            var session =
                Session(
                    sessionID = UUID.randomUUID(),
                    userId = user1.getDataOrNull()?.id,
                    refreshToken = "token1",
                    expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            session = sessionDao.addSession(session).getDataOrNull()!!
            val result = sessionDao.getSessionById(session.sessionID!!)
            assertTrue(result.isSuccess())
            assertEquals(session.sessionID, result.getDataOrNull()?.sessionID)
        }

    @Test
    fun getSessionById_returnsNullForNonExistentSession() =
        runBlocking {
            val result = sessionDao.getSessionById(UUID.randomUUID())
            assertTrue(result.isFailure())
            assertNull(result.getDataOrNull())
        }

    @Test
    fun addSession_addsSessionSuccessfully() =
        runBlocking {
            val user1 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )

            val session =
                Session(
                    sessionID = UUID.randomUUID(),
                    userId = user1.getDataOrNull()?.id,
                    refreshToken = "token1",
                    expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val result = sessionDao.addSession(session)
            assertTrue(result.isSuccess())
            val retrievedSession = sessionDao.getSessionById(result.getDataOrNull()!!.sessionID!!)
            assertTrue(retrievedSession.getDataOrNull() != null)
        }

    @Test
    fun addSessions_addsMultipleSessionsSuccessfully() =
        runBlocking {
            val user1 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )

            val user2 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            val sessions =
                listOf(
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user1.getDataOrNull()?.id,
                        refreshToken = "token1",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user2.getDataOrNull()?.id,
                        refreshToken = "token1",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            val result = sessionDao.addSessions(sessions)
            assertTrue(result.isSuccess())
            val retrievedSessions = sessionDao.getAllSessions()
            assertEquals(sessions.size, retrievedSessions.getDataOrNull()?.size)
        }

    @Test
    fun updateSession_updatesSessionSuccessfully() =
        runBlocking {
            val user1 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )

            var session =
                Session(
                    sessionID = UUID.randomUUID(),
                    userId = user1.getDataOrNull()?.id,
                    refreshToken = "token1",
                    expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val sessionID = sessionDao.addSession(session).getDataOrNull()
            session = session.copy(refreshToken = "newToken")
            val result = sessionDao.updateSession(session)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deleteSession_deletesSessionSuccessfully() =
        runBlocking {
            val user1 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            var session =
                Session(
                    sessionID = UUID.randomUUID(),
                    userId = user1.getDataOrNull()?.id,
                    refreshToken = "token1",
                    expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            session = sessionDao.addSession(session).getDataOrNull()!!
            val result = sessionDao.deleteSession(session.sessionID!!)
            assertTrue(result.isSuccess())
            val retrievedSession = sessionDao.getSessionById(session.sessionID!!)
            assertNull(retrievedSession.getDataOrNull())
        }

    @Test
    fun deleteAllSessions_deletesAllSessionsSuccessfully() =
        runBlocking {
            val user1 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )

            val user2 =
                userDao.addUser(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            val sessions =
                listOf(
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user1.getDataOrNull()?.id,
                        refreshToken = "token1",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user1.getDataOrNull()?.id,
                        refreshToken = "token1",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            sessionDao.addSessions(sessions)
            val result = sessionDao.deleteAllSessions()
            assertTrue(result.isSuccess())
            val retrievedSessions = sessionDao.getAllSessions()
            assertTrue(retrievedSessions.getDataOrNull()?.isEmpty() ?: false)
        }
}
