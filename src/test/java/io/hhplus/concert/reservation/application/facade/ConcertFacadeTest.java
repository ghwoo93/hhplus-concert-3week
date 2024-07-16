package io.hhplus.concert.reservation.application.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.service.facade.ConcertFacadeImpl;
import io.hhplus.concert.reservation.application.service.interfaces.ConcertFacade;
import io.hhplus.concert.reservation.application.service.interfaces.ConcertService;
import io.hhplus.concert.reservation.application.service.interfaces.QueueService;
import io.hhplus.concert.reservation.application.service.interfaces.TokenService;
import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.domain.model.Token;

public class ConcertFacadeTest {
    @Mock
    private QueueService queueService;

    @Mock
    private TokenService tokenService;

    @Mock
    private ConcertService concertService;

    private ConcertFacade concertFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        concertFacade = new ConcertFacadeImpl(queueService, tokenService, concertService);
    }

    @Test
    void issueToken_WhenUserNotInQueue_ShouldCreateNewQueueAndIssueToken() {
        String userId = "user1";
        Queue newQueue = new Queue(userId);  // 생성자를 사용하여 Queue 객체 생성
        newQueue.setStatus(Queue.QueueStatus.ACTIVE);
        newQueue.setQueuePosition(1);
        newQueue.setExpirationTime(3600); // 1시간 후 만료
    
        Token newToken = new Token();
        newToken.setToken("newToken");
    
        when(queueService.getOrCreateQueueForUser(userId)).thenReturn(newQueue);
        when(tokenService.createToken(userId)).thenReturn(newToken);
    
        TokenDTO result = concertFacade.issueToken(userId);
    
        assertNotNull(result);
        assertEquals("newToken", result.getToken());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(1, result.getQueuePosition());
        assertTrue(result.getRemainingTime() <= 3600 && result.getRemainingTime() > 3590,
                "Remaining time should be between 3590 and 3600 seconds, but was: " + result.getRemainingTime());
    
        verify(queueService).getOrCreateQueueForUser(userId);
        verify(tokenService).createToken(userId);
    }
    
    @Test
    void issueToken_WhenUserQueueExpired_ShouldCreateNewQueueAndIssueToken() {
        String userId = "user3";
        Queue expiredQueue = new Queue(userId);
        expiredQueue.setStatus(Queue.QueueStatus.EXPIRED);
    
        Queue newQueue = new Queue(userId);
        newQueue.setStatus(Queue.QueueStatus.ACTIVE);
        newQueue.setQueuePosition(1);
        newQueue.setExpirationTime(3600); // 1시간 후 만료
    
        Token newToken = new Token();
        newToken.setToken("newToken");
    
        when(queueService.getOrCreateQueueForUser(userId)).thenReturn(expiredQueue);
        when(queueService.createNewQueue(userId)).thenReturn(newQueue);
        when(tokenService.createToken(userId)).thenReturn(newToken);
    
        TokenDTO result = concertFacade.issueToken(userId);
    
        assertNotNull(result);
        assertEquals("newToken", result.getToken());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(1, result.getQueuePosition());
        assertTrue(result.getRemainingTime() <= 3600 && result.getRemainingTime() > 3550,
                "Remaining time should be between 3550 and 3600 seconds, but was: " + result.getRemainingTime());
    
        verify(queueService).getOrCreateQueueForUser(userId);
        verify(queueService).createNewQueue(userId);
        verify(tokenService).createToken(userId);
    }
    
    @Test
    void issueToken_WhenUserInWaitingQueue_ShouldReturnWaitingStatus() {
        String userId = "user2";
        Queue waitingQueue = new Queue(userId);
        waitingQueue.setStatus(Queue.QueueStatus.WAITING);
        waitingQueue.setQueuePosition(10);
        waitingQueue.setLastUpdatedAt(LocalDateTime.now().minusSeconds(1)); // Ensure that the last updated time is set correctly
    
        when(queueService.getOrCreateQueueForUser(userId)).thenReturn(waitingQueue);
    
        TokenDTO result = concertFacade.issueToken(userId);
    
        assertNotNull(result);
        assertNull(result.getToken());
        assertEquals("WAITING", result.getStatus());
        assertEquals(10, result.getQueuePosition());
        assertTrue(result.getRemainingTime() <= 10 && result.getRemainingTime() > 0,
                "Remaining time should be between 0 and 10 seconds, but was: " + result.getRemainingTime());
    
        verify(queueService).getOrCreateQueueForUser(userId);
        verifyNoInteractions(tokenService);
    }

    @Test
    void getAllConcerts_ShouldReturnConcertDTOList() {
        ConcertDTO concert1 = new ConcertDTO("1", "Concert A", "2023-07-14");
        ConcertDTO concert2 = new ConcertDTO("2", "Concert B", "2023-08-20");
        
        when(concertService.getAllConcerts()).thenReturn(Arrays.asList(concert1, concert2));
        
        List<ConcertDTO> result = concertFacade.getAllConcerts();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Concert A", result.get(0).getConcertName());
        assertEquals("Concert B", result.get(1).getConcertName());
        
        verify(concertService, times(1)).getAllConcerts();
    }

    @Test
    void getSeatsByConcertId_ShouldReturnSeatDTOList() {
        String concertId = "1";
        SeatDTO seat1 = new SeatDTO(1, true);
        SeatDTO seat2 = new SeatDTO(2, false);
        
        when(concertService.getSeatsByConcertId(concertId)).thenReturn(Arrays.asList(seat1, seat2));
        
        List<SeatDTO> result = concertFacade.getSeatsByConcertId(concertId);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getSeatNumber());
        assertTrue(result.get(0).isAvailable());
        assertEquals(2, result.get(1).getSeatNumber());
        assertFalse(result.get(1).isAvailable());
        
        verify(concertService, times(1)).getSeatsByConcertId(concertId);
    }
}
