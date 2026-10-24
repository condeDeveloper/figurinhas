package br.com.conde.figurinhas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * O achado contraintuitivo do repositorio: comprar em pacote fechado, com as
 * figurinhas do pacote garantidamente diferentes entre si, quase nao ajuda.
 */
@DisplayName("Pacotes com varias figurinhas")
class PacoteTest {

    @Test
    @DisplayName("pacote de uma figurinha e o problema original")
    void pacoteDeUmaEhOOriginal() {
        Album album = Album.de(50);

        assertThat(new Pacote(album, 1).pacotesEsperados())
                .isCloseTo(album.esperado(), within(album.esperado() * 1e-4));
    }

    @Test
    @DisplayName("pacote de cinco economiza menos de 2% das figurinhas")
    void oPacoteQuaseNaoAjuda() {
        Album album = Album.de(100);
        double umaAUma = album.esperado();
        double comPacote = new Pacote(album, 5).pacotesEsperados() * 5;

        assertThat(1 - comPacote / umaAUma).isBetween(0.01, 0.02);
    }

    @Test
    @DisplayName("quanto maior o pacote, maior a economia, e ainda assim pouca")
    void aEconomiaCresceDevagar() {
        Album album = Album.de(100);
        double umaAUma = album.esperado();
        double anterior = 0;

        for (int porPacote : new int[]{2, 5, 10, 20}) {
            double economia = 1 - new Pacote(album, porPacote).pacotesEsperados()
                    * porPacote / umaAUma;

            assertThat(economia).describedAs("pacote de %d", porPacote).isGreaterThan(anterior);
            anterior = economia;
        }

        assertThat(anterior).isLessThan(0.10);
    }

    @Test
    @DisplayName("o pacote do tamanho do album completa tudo de uma vez")
    void oPacoteQueEhOAlbum() {
        Album album = Album.de(10);

        assertThat(new Pacote(album, 10).pacotesEsperados()).isCloseTo(1.0, within(1e-9));
        assertThat(new Pacote(album, 10).completoEm(1)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    @DisplayName("a hipergeometrica soma um sobre todos os resultados possiveis")
    void aHipergeometricaFecha() {
        int total = 20;
        int tirados = 5;

        for (int marcados = 0; marcados <= total; marcados++) {
            double soma = 0;
            for (int queremos = 0; queremos <= tirados; queremos++) {
                soma += Pacote.hipergeometrica(total, marcados, tirados, queremos);
            }

            assertThat(soma).describedAs("%d marcados", marcados).isCloseTo(1.0, within(1e-9));
        }
    }

    @Test
    @DisplayName("a hipergeometrica da zero no que e impossivel")
    void oImpossivelDaZero() {
        assertThat(Pacote.hipergeometrica(10, 3, 5, 4)).isZero();
        assertThat(Pacote.hipergeometrica(10, 3, 5, -1)).isZero();
        assertThat(Pacote.hipergeometrica(10, 8, 5, 0)).isZero();
    }

    @Test
    @DisplayName("pacote maior que o album nao existe")
    void pacoteGrandeDemais() {
        assertThatThrownBy(() -> new Pacote(Album.de(5), 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nao cabe");
    }
}
