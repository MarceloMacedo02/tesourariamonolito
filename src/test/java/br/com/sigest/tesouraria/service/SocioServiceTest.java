package br.com.sigest.tesouraria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.domain.repository.GrupoMensalidadeRepository;
import br.com.sigest.tesouraria.domain.repository.RoleRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.domain.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class SocioServiceTest {

    @Mock
    private SocioRepository repository;

    @Mock
    private GrupoMensalidadeRepository grupoMensalidadeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SocioService socioService;

    private Socio socio;
    private SocioDto socioDto;

    @BeforeEach
    void setUp() {
        socio = new Socio();
        socio.setId(1L);
        socio.setNome("Test Socio");
        socio.setCpf("12345678901");
        socio.setGrau("Quadro de Sócio");
        socio.setDataNascimento(LocalDate.of(1990, 1, 1));
        socio.setStatus(StatusSocio.FREQUENTE);

        socioDto = new SocioDto();
        socioDto.setId(1L);
        socioDto.setNome("Test Socio");
        socioDto.setCpf("12345678901");
        socioDto.setGrau("Quadro de Sócio");
        socioDto.setDataNascimento(LocalDate.of(1990, 1, 1));
        socioDto.setStatus(StatusSocio.FREQUENTE.getDescricao());
    }

    @Test
    void testFindSociosByStatus() {
        // Given
        List<Socio> socios = new ArrayList<>();
        socios.add(socio);
        when(repository.findByStatus(StatusSocio.FREQUENTE)).thenReturn(socios);

        // When
        List<Socio> result = socioService.findSociosByStatus(StatusSocio.FREQUENTE);

        // Then
        assertEquals(1, result.size());
        assertEquals(socio, result.get(0));
        verify(repository, times(1)).findByStatus(StatusSocio.FREQUENTE);
    }

    @Test
    void testFindAll() {
        // Given
        List<Socio> socios = new ArrayList<>();
        socios.add(socio);
        when(repository.findAll()).thenReturn(socios);

        // When
        List<Socio> result = socioService.findAll();

        // Then
        assertEquals(1, result.size());
        assertEquals(socio, result.get(0));
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(socio));

        // When
        Socio result = socioService.findById(1L);

        // Then
        assertEquals(socio, result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegraNegocioException.class, () -> socioService.findById(1L));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdAsDto() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(socio));

        // When
        SocioDto result = socioService.findByIdAsDto(1L);

        // Then
        assertEquals(socioDto.getId(), result.getId());
        assertEquals(socioDto.getNome(), result.getNome());
        assertEquals(socioDto.getCpf(), result.getCpf());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindByCpf() {
        // Given
        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(socio));

        // When
        Optional<Socio> result = socioService.findByCpf("12345678901");

        // Then
        assertTrue(result.isPresent());
        assertEquals(socio, result.get());
        verify(repository, times(1)).findByCpf("12345678901");
    }

    @Test
    void testSaveNewSocio() {
        // Given
        socioDto.setId(null); // New socio
        when(repository.save(any(Socio.class))).thenReturn(socio);

        // When
        Socio result = socioService.save(socioDto);

        // Then
        assertEquals(socio, result);
        verify(repository, times(1)).save(any(Socio.class));
    }

    @Test
    void testSaveExistingSocio() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(socio));
        when(repository.save(socio)).thenReturn(socio);

        // When
        Socio result = socioService.save(socioDto);

        // Then
        assertEquals(socio, result);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(socio);
    }

    @Test
    void testSaveSocioWithGrupoMensalidade() {
        // Given
        socioDto.setGrupoMensalidadeId(1L);
        GrupoMensalidade grupo = new GrupoMensalidade();
        grupo.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(socio));
        when(grupoMensalidadeRepository.findById(1L)).thenReturn(Optional.of(grupo));
        when(repository.save(socio)).thenReturn(socio);

        // When
        Socio result = socioService.save(socioDto);

        // Then
        assertEquals(socio, result);
        verify(repository, times(1)).findById(1L);
        verify(grupoMensalidadeRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(socio);
    }

    @Test
    void testSaveSocioWithInvalidGrupoMensalidade() {
        // Given
        socioDto.setGrupoMensalidadeId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(socio));
        when(grupoMensalidadeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegraNegocioException.class, () -> socioService.save(socioDto));
        verify(repository, times(1)).findById(1L);
        verify(grupoMensalidadeRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveSocioWithTitular() {
        // Given
        Socio titular = new Socio();
        titular.setId(2L);
        socioDto.setSocioTitularId(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(socio));
        when(repository.findById(2L)).thenReturn(Optional.of(titular));
        when(repository.save(socio)).thenReturn(socio);

        // When
        Socio result = socioService.save(socioDto);

        // Then
        assertEquals(socio, result);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).findById(2L);
        verify(repository, times(1)).save(socio);
    }

    @Test
    void testSaveSocioWithInvalidTitular() {
        // Given
        socioDto.setSocioTitularId(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(socio));
        when(repository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegraNegocioException.class, () -> socioService.save(socioDto));
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).findById(2L);
    }

    @Test
    void testDelete() {
        // When
        socioService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testToDto() {
        // When
        SocioDto result = socioService.toDto(socio);

        // Then
        assertEquals(socioDto.getId(), result.getId());
        assertEquals(socioDto.getNome(), result.getNome());
        assertEquals(socioDto.getCpf(), result.getCpf());
        assertEquals(socioDto.getGrau(), result.getGrau());
        assertEquals(socioDto.getDataNascimento(), result.getDataNascimento());
        assertEquals(socioDto.getStatus(), result.getStatus());
    }

    @Test
    void testToEntityNewSocio() {
        // Given
        socioDto.setId(null); // New socio
        Socio newSocio = new Socio();
        newSocio.setDataCadastro(LocalDate.now());

        // Mock the behavior of toEntity method by mocking repository.save
        when(repository.save(any(Socio.class))).thenAnswer(invocation -> {
            Socio socioArg = invocation.getArgument(0);
            // Verify that dataCadastro is set for new socios
            assertNotNull(socioArg.getDataCadastro());
            return socioArg;
        });

        // When
        Socio result = socioService.save(socioDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getDataCadastro());
        verify(repository, times(1)).save(any(Socio.class));
    }

    @Test
    void testToEntityExistingSocio() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(socio));
        when(repository.save(socio)).thenReturn(socio);

        // When
        Socio result = socioService.save(socioDto);

        // Then
        assertEquals(socio, result);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(socio);
    }
}