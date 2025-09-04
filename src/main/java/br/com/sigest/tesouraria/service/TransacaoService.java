package br.com.sigest.tesouraria.service;

import br.com.sigest.tesouraria.domain.entity.Transacao;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import br.com.sigest.tesouraria.dto.TransacaoDto;
import br.com.sigest.tesouraria.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public List<TransacaoDto> findAllTransactions(Integer month, Integer year) {
        List<Transacao> transactions;
        if (month != null && year != null) {
            transactions = transacaoRepository.findByMonthAndYearOrderByDataDesc(month, year);
        } else {
            transactions = transacaoRepository.findAllByOrderByDataDesc();
        }
        return transactions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public Map<Integer, List<Integer>> getAvailableMonthsAndYears() {
        List<Object[]> results = transacaoRepository.findDistinctYearsAndMonths();
        Map<Integer, List<Integer>> availableDates = new TreeMap<>();

        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            availableDates.computeIfAbsent(year, k -> new ArrayList<>()).add(month);
        }
        return availableDates;
    }

    @Transactional
    public List<TransacaoDto> processOfxFile(MultipartFile file) throws IOException {
        List<Transacao> newTransacoes = new ArrayList<>();
        List<Transacao> processedTransacoes = new ArrayList<>(); // To return all processed, including duplicates

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            Transacao transacao = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("<STMTTRN>")) {
                    transacao = new Transacao();
                } else if (line.startsWith("<TRNTYPE>") && transacao != null) {
                    String tipo = line.replace("<TRNTYPE>", "").replace("</TRNTYPE>", "").trim();
                    transacao.setTipo("CREDIT".equalsIgnoreCase(tipo) ? TipoTransacao.CREDITO : TipoTransacao.DEBITO);
                } else if (line.startsWith("<DTPOSTED>") && transacao != null) {
                    String dataStr = line.replace("<DTPOSTED>", "").replace("</DTPOSTED>", "").trim();
                    LocalDate data = LocalDate.parse(dataStr.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
                    transacao.setData(data);
                } else if (line.startsWith("<TRNAMT>") && transacao != null) {
                    BigDecimal valor = new BigDecimal(line.replace("<TRNAMT>", "").replace("</TRNAMT>", "").trim());
                    transacao.setValor(valor);
                } else if (line.startsWith("<MEMO>") && transacao != null) {
                    String memo = line.replace("<MEMO>", "").replace("</MEMO>", "").trim();

                    // Extrair CPF ou CNPJ
                    String documento = extrairDocumento(memo);
                    transacao.setDocumento(documento);

                    // Extrair fornecedor/sócio e descrição
                    String tempMemo = memo;
                    String fornecedorOuSocio = null;
                    String descricao = memo;

                    // Remove documento from memo for easier parsing of fornecedorOuSocio and descricao
                    if (documento != null) {
                        tempMemo = tempMemo.replace(documento, "").trim();
                    }

                    int firstDash = tempMemo.indexOf(" - ");
                    int lastDash = tempMemo.lastIndexOf(" - ");

                    if (firstDash != -1 && lastDash != -1 && firstDash != lastDash) {
                        // Case: "Desc - Fornecedor - Documento"
                        descricao = tempMemo.substring(0, firstDash).trim();
                        fornecedorOuSocio = tempMemo.substring(firstDash + 3, lastDash).trim();
                    } else if (firstDash != -1 && lastDash != -1 && firstDash == lastDash) {
                        // Case: "Desc - Fornecedor" (no document or document already removed)
                        descricao = tempMemo.substring(0, firstDash).trim();
                        fornecedorOuSocio = tempMemo.substring(firstDash + 3).trim();
                    } else {
                        // Case: "Desc" or "Fornecedor" (no dashes or only one part)
                        descricao = tempMemo.trim();
                        fornecedorOuSocio = null; // Or set to memo if no clear distinction
                    }

                    transacao.setFornecedorOuSocio(fornecedorOuSocio);
                    transacao.setDescricao(descricao);
                } else if (line.startsWith("</STMTTRN>") && transacao != null) {
                    // Check for duplicates before adding to the list for saving
                    Transacao existingTransacao = transacaoRepository.findByDataAndTipoAndValorAndFornecedorOuSocioAndDocumentoAndDescricao(
                        transacao.getData(), transacao.getTipo(), transacao.getValor(),
                        transacao.getFornecedorOuSocio(), transacao.getDocumento(), transacao.getDescricao());

                    if (existingTransacao == null) {
                        newTransacoes.add(transacao);
                    }
                    processedTransacoes.add(transacao); // Add to processed list regardless of duplication
                    transacao = null;
                }
            }

            transacaoRepository.saveAll(newTransacoes); // Save only new transactions

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar OFX: " + e.getMessage(), e);
        }

        return processedTransacoes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private String extrairDocumento(String memo) {
        Pattern cpfPattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        Pattern cnpjPattern = Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}");

        Matcher cpfMatcher = cnpjPattern.matcher(memo);
        if (cpfMatcher.find()) {
            return cpfMatcher.group();
        }

        Matcher cnpjMatcher = cpfPattern.matcher(memo);
        if (cnpjMatcher.find()) {
            return cnpjMatcher.group();
        }

        return null;
    }

    private TransacaoDto convertToDto(Transacao transacao) {
        TransacaoDto dto = new TransacaoDto();
        dto.setId(transacao.getId());
        dto.setData(transacao.getData());
        dto.setTipo(transacao.getTipo());
        dto.setValor(transacao.getValor());
        dto.setFornecedorOuSocio(transacao.getFornecedorOuSocio());
        dto.setDocumento(transacao.getDocumento());
        dto.setDescricao(transacao.getDescricao());
        dto.setLancado(transacao.getLancado());
        return dto;
    }
}
