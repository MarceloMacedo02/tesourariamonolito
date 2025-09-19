package br.com.sigest.tesouraria.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;

public interface SocioRepository extends JpaRepository<Socio, Long> {

    @Query("SELECT DISTINCT s FROM Socio s JOIN Cobranca c ON c.socio.id = s.id WHERE c.status = 'VENCIDA'")
    List<Socio> findSociosInadimplentes();

    Optional<Socio> findByCpf(String cpf);

    Optional<Socio> findTopByNomeContainingIgnoreCase(String nome);

    /**
     * Busca todos os sócios com um status específico.
     * 
     * @param status O status do sócio (ex: StatusSocio.FREQUENTE).
     * @return Uma lista de sócios com o status especificado.
     */
    List<Socio> findByStatus(StatusSocio status);

    @Query("SELECT s FROM Socio s LEFT JOIN FETCH s.dependentes WHERE s.id = :id")
    Optional<Socio> findByIdWithDependentes(@Param("id") Long id);
}