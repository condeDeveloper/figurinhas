package br.com.conde.figurinhas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** Os numeros publicados no README, presos em teste. */
@DisplayName("Os numeros do README")
class MedidasTest {

    @Test
    @DisplayName("tabela do album: a media por tamanho")
    void tabelaDoAlbum() {
        assertThat(Album.de(10).esperado()).isCloseTo(29.3, within(0.05));
        assertThat(Album.de(50).esperado()).isCloseTo(225.0, within(0.05));
        assertThat(Album.de(100).esperado()).isCloseTo(518.7, within(0.05));
        assertThat(Album.de(670).esperado()).isCloseTo(4747.1, within(0.05));
    }

    @Test
    @DisplayName("tabela do album: as medianas")
    void medianas() {
        assertThat(mediana(10)).isEqualTo(27);
        assertThat(mediana(50)).isEqualTo(214);
        assertThat(mediana(100)).isEqualTo(497);
        assertThat(mediana(670)).isEqualTo(4605);
    }

    @Test
    @DisplayName("o album da Copa, com 670 figurinhas, custa 4.747 em media")
    void oAlbumDaCopa() {
        Album copa = Album.de(670);

        assertThat(copa.esperado()).isCloseTo(4747.1, within(0.1));
        assertThat(copa.esperado() / 670).isCloseTo(7.09, within(0.01));
        assertThat(copa.esperadoParaAProxima(669)).isEqualTo(670);
    }

    @Test
    @DisplayName("tabela da ultima: as primeiras 50% custam menos de 10% do album")
    void aPrimeiraMetadeEhBarata() {
        Album copa = Album.de(670);
        double total = copa.esperado();
        double primeiraMetade = total - copa.esperadoDaqui(335);

        assertThat(100 * primeiraMetade / total).isCloseTo(9.8, within(0.1));
    }

    @Test
    @DisplayName("a ultima figurinha sozinha custa 14,1% do album inteiro")
    void oPrecoDaUltima() {
        Album copa = Album.de(670);

        assertThat(100 * copa.esperadoParaAProxima(669) / copa.esperado())
                .isCloseTo(14.1, within(0.1));
        assertThat(100 * 10.0 / Album.de(10).esperado()).isCloseTo(34.1, within(0.1));
    }

    @Test
    @DisplayName("tabela da sorte: de 381 a 916 figurinhas num album de 100")
    void tabelaDaSorte() {
        Distribuicao distribuicao = new Distribuicao(Album.de(100), 3000);

        assertThat(distribuicao.comprasPara(0.10)).isEqualTo(381);
        assertThat(distribuicao.comprasPara(0.25)).isEqualTo(430);
        assertThat(distribuicao.comprasPara(0.50)).isEqualTo(497);
        assertThat(distribuicao.comprasPara(0.75)).isEqualTo(584);
        assertThat(distribuicao.comprasPara(0.90)).isEqualTo(683);
        assertThat(distribuicao.comprasPara(0.99)).isEqualTo(916);
    }

    @Test
    @DisplayName("tabela dos pacotes: a economia de cada tamanho")
    void tabelaDosPacotes() {
        assertThat(economia(2)).isCloseTo(0.4, within(0.1));
        assertThat(economia(5)).isCloseTo(1.6, within(0.1));
        assertThat(economia(10)).isCloseTo(3.7, within(0.1));
        assertThat(economia(20)).isCloseTo(8.0, within(0.1));
    }

    @Test
    @DisplayName("o medidor roda inteiro sem lancar")
    void medidorRoda() {
        Medidor.album();
        Medidor.ultima();
        Medidor.sorte();
    }

    private int mediana(int figurinhas) {
        Album album = Album.de(figurinhas);
        return new Distribuicao(album, (int) (album.esperado() * 5) + 100).mediana();
    }

    private double economia(int porPacote) {
        Album album = Album.de(100);
        double comPacote = new Pacote(album, porPacote).pacotesEsperados() * porPacote;
        return Math.round(1000 * (1 - comPacote / album.esperado())) / 10.0;
    }
}
