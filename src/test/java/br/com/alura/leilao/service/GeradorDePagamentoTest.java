package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeradorDePagamentoTest {

    private GeradorDePagamento gerador;
    @Mock
    private PagamentoDao pagamentoDao;

    @Mock
    private Clock clock;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @BeforeEach
    public void beforeEach(){
        MockitoAnnotations.initMocks(this);
        this.gerador = new GeradorDePagamento(pagamentoDao, clock);
    }
    @Test
    public void deveriaCriarPagamentoParaVencedorDoLeilaoNaSextaComVencimentoNaSegundaFeira(){
        Leilao leilao = leilao();
        Lance vencedor = leilao.getLanceVencedor();


        LocalDate data = LocalDate.of(2022, 5,27);

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        gerador.gerarPagamento(vencedor);

        //captura o objeto q esta somente na classe a ser testada
        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();
        Assert.assertEquals(LocalDate.now(clock).plusDays(3), pagamento.getVencimento());
        Assert.assertEquals(vencedor.getValor(), pagamento.getValor());
        Assert.assertFalse(pagamento.getPago());
        Assert.assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
        Assert.assertEquals(leilao, pagamento.getLeilao());

    }

    private Leilao leilao() {

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));

        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);

        return leilao;

    }

}