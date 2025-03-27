import app.bushive.theBouncer.core.result.ResultHandler.isFailure
import app.bushive.theBouncer.core.result.ResultHandler.isSuccess
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.di.configureKoinUnitTest
import app.bushive.theBouncer.features.v1.data.local.dao.session.SessionDao
import app.bushive.theBouncer.features.v1.data.local.dao.user.UserDao
import app.bushive.theBouncer.features.v1.data.local.model.AuthProvider
import app.bushive.theBouncer.features.v1.data.local.model.Session
import app.bushive.theBouncer.features.v1.data.local.model.User
import app.bushive.theBouncer.features.v1.domain.repository.session.SessionRepository
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionRepositoryTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var sessionRepository: SessionRepository
    private lateinit var sessionDao: SessionDao
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        sessionDao = get()
        userDao = get()
        sessionRepository = get()
        databaseFactory = get()
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
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            email = "u2",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()!!
            val sessions =
                listOf(
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user1.id,
                        refreshToken = "token1",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user1.id,
                        refreshToken = "token2",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            sessionDao.addSessions(sessions)
            val result = sessionRepository.getAll()
            assertTrue(result.isSuccess())
            assertEquals(sessions.size, result.getDataOrNull()?.size)
        }

    @Test
    fun getSessionById_withValidId_returnsSession() =
        runBlocking {
            val user1 =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            email = "u2",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()!!
            val session =
                Session(
                    sessionID = UUID.randomUUID(),
                    userId = user1.id,
                    refreshToken = "token1",
                    expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            sessionDao.addSession(session)
            val result = sessionRepository.getById(session.sessionID!!)
            assertTrue(result.isSuccess())
            assertEquals(session.sessionID, result.getDataOrNull()?.sessionID)
        }

    @Test
    fun getSessionById_withInvalidId_returnsError() =
        runBlocking {
            val result = sessionRepository.getById(UUID.randomUUID())
            assertTrue(result.isFailure())
        }

    @Test
    fun addSession_addsSessionSuccessfully() =
        runBlocking {
            val user1 =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            email = "u2",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()!!

            val session =
                Session(
                    sessionID = UUID.randomUUID(),
                    userId = user1.id,
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
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            email = "u2",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()!!
            val sessions =
                listOf(
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user1.id,
                        refreshToken = "token1",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = user1.id,
                        refreshToken = "token2",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            val result = sessionRepository.add(sessions)
            assertTrue(result.isSuccess())
            val retrievedSessions = sessionDao.getAllSessions()
            assertEquals(sessions.size, retrievedSessions.getDataOrNull()?.size)
        }

    @Test
    fun updateSession_updatesSessionSuccessfully() =
        runBlocking {
            val user1 =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            email = "u2",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()!!
            var session =
                Session(
                    sessionID = UUID.randomUUID(),
                    userId = user1.id,
                    refreshToken = "token1",
                    expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            sessionDao.addSession(session)
            session = session.copy(refreshToken = "newToken")
            val result = sessionRepository.update(session)
            assertTrue(result.isSuccess())
            val retrievedSession = sessionDao.getSessionById(session.sessionID!!)
            assertEquals("newToken", retrievedSession.getDataOrNull()?.refreshToken)
        }

    @Test
    fun deleteSession_withValidId_deletesSuccessfully() =
        runBlocking {
            val user1 =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            email = "u2",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()!!
            val session =
                Session(
                    sessionID = UUID.randomUUID(),
                    userId = user1.id,
                    refreshToken = "token1",
                    expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            sessionDao.addSession(session)
            val result = sessionRepository.deleteById(session.sessionID!!)
            assertTrue(result.isSuccess())
            val retrievedSession = sessionDao.getSessionById(session.sessionID!!)
            assertTrue(retrievedSession.isFailure())
        }

    @Test
    fun deleteSession_withInvalidId_returnsError() =
        runBlocking {
            val result = sessionRepository.deleteById(UUID.randomUUID())
            assertTrue(result.isFailure())
        }

    @Test
    fun deleteAllSessions_deletesAllSessionsSuccessfully() =
        runBlocking {
            val sessions =
                listOf(
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = UUID.randomUUID(),
                        refreshToken = "token1",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    Session(
                        sessionID = UUID.randomUUID(),
                        userId = UUID.randomUUID(),
                        refreshToken = "token2",
                        expiresAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            sessionDao.addSessions(sessions)
            val result = sessionRepository.deleteAll()
            assertTrue(result.isSuccess())
            val retrievedSessions = sessionDao.getAllSessions()
            assertTrue(retrievedSessions.getDataOrNull()?.isEmpty() ?: false)
        }
}
