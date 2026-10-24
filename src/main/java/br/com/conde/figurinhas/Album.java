package br.com.conde.figurinhas;

/**
 * Um album de {@code figurinhas} figurinhas diferentes, compradas uma a uma, com
 * todas igualmente provaveis.
 *
 * <p>A conta fechada deste problema e antiga e curta: quando se tem {@code k}
 * figurinhas diferentes, a chance de a proxima ser nova e {@code (n-k)/n}, entao
 * ela demora, em media, {@code n/(n-k)} compras. Somando sobre todos os
 * {@code k}, o total e {@code n} vezes o numero harmonico.
 *
 * <p>Essa formula e usada aqui como <b>juiz</b>, nao como resposta: tudo o mais
 * no repositorio sai de uma programacao dinamica sobre a distribuicao inteira, e
 * os dois tem que concordar.
 */
public record Album(int figurinhas) {

    public Album {
        if (figurinhas < 1) {
            throw new IllegalArgumentException("album sem figurinha nao e album");
        }
    }

    public static Album de(int figurinhas) {
        return new Album(figurinhas);
    }

    /**
     * O numero harmonico {@code 1 + 1/2 + ... + 1/n}, somado do menor termo para o
     * maior.
     *
     * <p>A ordem importa: somando do maior para o menor, os termos pequenos somem
     * no arredondamento quando n passa de alguns milhares.
     */
    public static double harmonico(int n) {
        double soma = 0;
        for (int i = n; i >= 1; i--) {
            soma += 1.0 / i;
        }
        return soma;
    }

    /** Quantas figurinhas, em media, para completar o album. */
    public double esperado() {
        return figurinhas * harmonico(figurinhas);
    }

    /**
     * Quantas figurinhas, em media, para sair de {@code tenho} diferentes para
     * {@code tenho + 1}.
     */
    public double esperadoParaAProxima(int tenho) {
        if (tenho < 0 || tenho >= figurinhas) {
            throw new IllegalArgumentException("ja tenho " + tenho + " de " + figurinhas);
        }
        return (double) figurinhas / (figurinhas - tenho);
    }

    /** Quantas figurinhas, em media, para ir de {@code tenho} ate o album cheio. */
    public double esperadoDaqui(int tenho) {
        double soma = 0;
        for (int k = tenho; k < figurinhas; k++) {
            soma += esperadoParaAProxima(k);
        }
        return soma;
    }
}
