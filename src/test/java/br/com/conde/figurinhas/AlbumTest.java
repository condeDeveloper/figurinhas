package br.com.conde.figurinhas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("A conta fechada")
class AlbumTest {

    @Test
    @DisplayName("album de uma figurinha se completa com uma compra")
    void albumDeUma() {
        assertThat(Album.de(1).esperado()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("album de duas custa tres figurinhas em media")
    void albumDeDuas() {
        assertThat(Album.de(2).esperado()).isCloseTo(3.0, within(1e-12));
    }

    @Test
    @DisplayName("o harmonico de n bate com a soma direta")
    void oHarmonico() {
        assertThat(Album.harmonico(1)).isEqualTo(1.0);
        assertThat(Album.harmonico(2)).isCloseTo(1.5, within(1e-12));
        assertThat(Album.harmonico(4)).isCloseTo(1 + 0.5 + 1.0 / 3 + 0.25, within(1e-12));
    }

    @Test
    @DisplayName("a soma dos pedacos e o album inteiro")
    void osPedacosSomamOTodo() {
        for (int n : new int[]{5, 20, 100}) {
            Album album = Album.de(n);
            double somaDosPassos = 0;
            for (int tenho = 0; tenho < n; tenho++) {
                somaDosPassos += album.esperadoParaAProxima(tenho);
            }

            assertThat(somaDosPassos).isCloseTo(album.esperado(), within(1e-9));
        }
    }

    @Test
    @DisplayName("a ultima figurinha custa exatamente n, em qualquer tamanho de album")
    void aUltimaCustaN() {
        for (int n : new int[]{2, 10, 100, 670}) {
            assertThat(Album.de(n).esperadoParaAProxima(n - 1)).isEqualTo(n);
        }
    }

    @Test
    @DisplayName("a primeira figurinha e sempre nova: custa uma compra")
    void aPrimeiraEhGratis() {
        for (int n : new int[]{2, 10, 670}) {
            assertThat(Album.de(n).esperadoParaAProxima(0)).isEqualTo(1.0);
        }
    }

    @Test
    @DisplayName("o custo por figurinha cresce com o tamanho do album, devagar")
    void oCustoPorFigurinhaCresce() {
        double anterior = 0;
        for (int n : new int[]{10, 20, 50, 100, 200, 400, 670}) {
            double porFigurinha = Album.de(n).esperado() / n;

            assertThat(porFigurinha).isGreaterThan(anterior);
            anterior = porFigurinha;
        }

        assertThat(Album.de(670).esperado() / 670).isCloseTo(7.09, within(0.01));
    }

    @Test
    @DisplayName("album vazio ou pedir figurinha que ja se tem nao faz sentido")
    void entradasInvalidas() {
        assertThatThrownBy(() -> Album.de(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Album.de(5).esperadoParaAProxima(5))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
