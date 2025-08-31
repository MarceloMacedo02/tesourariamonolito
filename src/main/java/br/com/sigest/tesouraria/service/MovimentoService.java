import br.com.sigest.tesouraria.dto.MovimentoDto;
import br.com.sigest.tesouraria.dto.ExtratoFiltroDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.repository.MovimentoRepository;
import br.com.sigest.tesouraria.repository.RubricaRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimentoService {

    @Autowired
    private MovimentoRepository movimentoRepository;

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;

    @Autowired
    private RubricaRepository rubricaRepository;

    public List<Movimento> findAll() {
        return movimentoRepository.findAll();
    }

    public Movimento findById(Long id) {
        return movimentoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Movimento não encontrado."));
    }

    @Transactional
    public Movimento registrarMovimento(MovimentoDto dto) {
        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(dto.getContaFinanceiraId())
                .orElseThrow(() -> new RegraNegocioException("Conta financeira não encontrada."));

        Rubrica rubrica = rubricaRepository.findById(dto.getRubricaId())
                .orElseThrow(() -> new RegraNegocioException("Rubrica não encontrada."));

        // Atualiza o saldo da conta financeira
        if (dto.getTipo() == TipoMovimento.CREDITO) {
            contaFinanceira.setSaldoAtual(contaFinanceira.getSaldoAtual() + dto.getValor());
        } else if (dto.getTipo() == TipoMovimento.DEBITO) {
            contaFinanceira.setSaldoAtual(contaFinanceira.getSaldoAtual() - dto.getValor());
        }
        contaFinanceiraRepository.save(contaFinanceira);

        // Cria o registro de movimento
        Movimento movimento = new Movimento();
        movimento.setTipo(dto.getTipo());
        movimento.setValor(dto.getValor());
        movimento.setContaFinanceira(contaFinanceira);
        movimento.setRubrica(rubrica);
        movimento.setCentroCusto(rubrica.getCentroCusto()); // Centro de custo da rubrica
        movimento.setDataHora(dto.getData().atStartOfDay()); // Usa a data do DTO
        movimento.setOrigemDestino(dto.getOrigemDestino());

        return movimentoRepository.save(movimento);
    }

    public List<Movimento> filtrarMovimentos(ExtratoFiltroDto filtro) {
        List<Movimento> movimentos = movimentoRepository.findAll(); // Get all for in-memory filtering

        return movimentos.stream()
                .filter(movimento -> {
                    boolean match = true;

                    if (filtro.getDataInicio() != null) {
                        match = match && !movimento.getDataHora().toLocalDate().isBefore(filtro.getDataInicio());
                    }
                    if (filtro.getDataFim() != null) {
                        match = match && !movimento.getDataHora().toLocalDate().isAfter(filtro.getDataFim());
                    }
                    if (filtro.getContaFinanceiraId() != null) {
                        match = match && movimento.getContaFinanceira().getId().equals(filtro.getContaFinanceiraId());
                    }
                    if (filtro.getTipoMovimento() != null) {
                        match = match && movimento.getTipo() == filtro.getTipoMovimento();
                    }
                    if (filtro.getRubricaId() != null) {
                        match = match && movimento.getRubrica().getId().equals(filtro.getRubricaId());
                    }
                    return match;
                })
                .collect(Collectors.toList());
    }

}
