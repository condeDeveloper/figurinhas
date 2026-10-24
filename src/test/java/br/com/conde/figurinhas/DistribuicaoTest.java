package br.com.conde.figurinhas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * A distribuicao sai de uma programacao dinamica, e a esperanca dela e conferida
 * contra a formula fechada. Sao dois caminhos independentes para o mesmo numero:
 * se um tiver erro de indice, eles divergem.
 */
@DisplayName("A distribuicao contra a formula")
class DistribuicaoTest {

    @Test
    @DisplayName("a esperanca pela distribuicao bate com n vezes o harmonico")
    void osDoisCaminhosBatem() {
        for (int n : new int[]{2, 5, 10, 30, 100}) {
            Album album = Album.de(n);
            Distribuicao distribuicao = new Distribuicao(album, (int) (album.esperado() * 8) + 200);

            assertThat(distribuicao.esperado())
                    .describedAs("%d figurinhas", n)
                    .isCloseTo(album.esperado(), within(album.esperado() * 1e-4));
        }
    }

    @Test
    @DisplayName("com menos compras que figurinhas, a chance de ter completado e zero")
    void impossivelAntesDaHora() {
        Distribuicao distribuicao = new Distribuicao(Album.de(10), 200);

        for (int compras = 0; compras < 10; compras++) {
            assertThat(distribuicao.completoEm(compras)).isZero();
        }
        assertThat(distribuicao.completoEm(10)).isPositive();
    }

    @Test
    @DisplayName("com exatamente n compras, a chance e n! sobre n elevado a n")
    void aChanceDeAcertarDePrimeira() {
        int n = 6;
        Distribuicao distribuicao = new Distribuicao(Album.de(n), 500);
        double fatorial = 1;
        for (int i = 2; i <= n; i++) {
            fatorial *= i;
        }

        assertThat(distribuicao.completoEm(n))
                .isCloseTo(fatorial / Math.pow(n, n), within(1e-12));
    }

    @Test
    @DisplayName("a chance de ter completado so cresce, a menos do arredondamento")
    void aChanceNuncaCai() {
        Distribuicao distribuicao = new Distribuicao(Album.de(20), 1000);
        double anterior = 0;
        double maiorQueda = 0;

        for (int compras = 0; compras <= 1000; compras++) {
            double agora = distribuicao.completoEm(compras);

            maiorQueda = Math.max(maiorQueda, anterior - agora);
            anterior = agora;
        }

        // A sequencia e matematicamente crescente, mas a conta e em ponto
        // flutuante: ja perto de 1 ela anda para tras na 15a casa decimal.
        // Exigir monotonia exata aqui seria exigir aritmetica exata.
        assertThat(maiorQueda).isLessThan(1e-14);
        assertThat(anterior).isCloseTo(1.0, within(1e-6));
    }

    @Test
    @DisplayName("perto de 1 a conta anda para tras no ultimo bit, e so ali")
    void oDesvioEhDoUltimoBit() {
        Distribuicao distribuicao = new Distribuicao(Album.de(20), 1000);
        int quedas = 0;

        for (int compras = 1; compras <= 1000; compras++) {
            if (distribuicao.completoEm(compras) < distribuicao.completoEm(compras - 1)) {
                quedas++;
                assertThat(distribuicao.completoEm(compras)).isGreaterThan(0.999);
            }
        }

        assertThat(quedas).isPositive();
    }

    @Test
    @DisplayName("a mediana e MENOR que a media: a distribuicao puxa para a direita")
    void aMedianaFicaAbaixoDaMedia() {
        for (int n : new int[]{10, 20, 50, 100}) {
            Album album = Album.de(n);
            Distribuicao distribuicao = new Distribuicao(album, (int) (album.esperado() * 6) + 100);

            assertThat((double) distribuicao.mediana())
                    .describedAs("%d figurinhas", n)
                    .isLessThan(album.esperado());
        }
    }

    @Test
    @DisplayName("a cauda e longa: o azarado paga mais que o dobro do sortudo")
    void aCaudaEhLonga() {
        Distribuicao distribuicao = new Distribuicao(Album.de(100), 3000);

        int sortudo = distribuicao.comprasPara(0.10);
        int azarado = distribuicao.comprasPara(0.99);

        assertThat(sortudo).isEqualTo(381);
        assertThat(azarado).isEqualTo(916);
        assertThat((double) azarado / sortudo).isGreaterThan(2.3);
    }

    @Test
    @DisplayName("pedir distribuicao com menos compras que figurinhas e pedido sem sentido")
    void tetoPequenoDemais() {
        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> new Distribuicao(Album.de(10), 5))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
