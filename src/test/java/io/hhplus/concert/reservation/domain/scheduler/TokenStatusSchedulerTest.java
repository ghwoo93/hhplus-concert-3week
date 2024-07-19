package io.hhplus.concert.reservation.domain.scheduler;

import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TokenStatusSchedulerTest {

    @Mock
    private TokenRepository tokenRepository;

    private TokenStatusScheduler tokenStatusScheduler;

    @Captor
    private ArgumentCaptor<TokenStatus> statusCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenStatusScheduler = new TokenStatusScheduler(tokenRepository);
    }

    @Test
    void testUpdateTokenPositions() {
        // Given
        TokenEntity token1 = new TokenEntity();
        token1.setStatus(TokenStatus.WAITING);
        token1.setCreatedAt(LocalDateTime.now().minusMinutes(3));

        TokenEntity token2 = new TokenEntity();
        token2.setStatus(TokenStatus.WAITING);
        token2.setCreatedAt(LocalDateTime.now().minusMinutes(2));

        TokenEntity token3 = new TokenEntity();
        token3.setStatus(TokenStatus.WAITING);
        token3.setCreatedAt(LocalDateTime.now().minusMinutes(1));

        List<TokenEntity> waitingTokens = Arrays.asList(token1, token2, token3);

        // When
        when(tokenRepository.findByStatusOrderByCreatedAt(any(TokenStatus.class))).thenReturn(waitingTokens);

        tokenStatusScheduler.updateTokenPositions();

        // Then
        verify(tokenRepository).findByStatusOrderByCreatedAt(statusCaptor.capture());
        TokenStatus capturedStatus = statusCaptor.getValue();
        assertNotNull(capturedStatus);
        assertEquals(TokenStatus.WAITING, capturedStatus);
        
        verify(tokenRepository, times(3)).save(any(TokenEntity.class));

        assertEquals(1, token1.getQueuePosition());
        assertEquals(2, token2.getQueuePosition());
        assertEquals(3, token3.getQueuePosition());
    }
}
