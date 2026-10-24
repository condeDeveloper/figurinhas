package br.com.conde.figurinhas;

/**
 * A distribuicao inteira: a chance de ter o album completo depois de cada
 * numero de figurinhas compradas.
 *
 * <p>A conta e uma programacao dinamica sobre uma coisa so: quantas figurinhas
 * DIFERENTES ja se tem. Comprar a de numero 7 ou a de numero 300 da no mesmo, o
 * que importa e se ela e repetida.
 *
 * <p>Ha uma formula fechada para isso, por inclusao e exclusao, com uma soma
 * alternada de binomiais. Ela nao foi usada: com umas poucas centenas de
 * figurinhas os termos passam de 10^100 e se cancelam ate sobrar um numero
 * pequeno, e o resultado em ponto flutuante vira lixo. A programacao dinamica
 * soma apenas numeros positivos entre zero e um, e nao tem esse problema.
 */
public final class Distribuicao {

    private final Album album;
    private final double[] chanceDeCompleto;

    /**
     * @param ate quantas compras simular; o teto precisa ser grande o bastante
     *            para a cauda da distribuicao caber
     */
    public Distribuicao(Album album, int ate) {
        if (ate < album.figurinhas()) {
            throw new IllegalArgumentException("menos compras que figurinhas nunca completa");
        }
        this.album = album;
        int n = album.figurinhas();
        double[] tenho = new double[n + 1];
        tenho[0] = 1;
        chanceDeCompleto = new double[ate + 1];

        for (int compra = 1; compra <= ate; compra++) {
            for (int k = n; k >= 1; k--) {
                double repetida = tenho[k] * k / n;
                double nova = tenho[k - 1] * (n - k + 1.0) / n;
                tenho[k] = repetida + nova;
            }
            tenho[0] = 0;
            chanceDeCompleto[compra] = tenho[n];
        }
    }

    public Album album() {
        return album;
    }

    /** A chance de o album estar completo depois de {@code compras} figurinhas. */
    public double completoEm(int compras) {
        if (compras < 0) {
            throw new IllegalArgumentException("compras negativas");
        }
        return compras >= chanceDeCompleto.length
                ? chanceDeCompleto[chanceDeCompleto.length - 1]
                : chanceDeCompleto[compras];
    }

    /** Quantas compras para ter {@code chance} de chance de ter completado. */
    public int comprasPara(double chance) {
        for (int compras = 0; compras < chanceDeCompleto.length; compras++) {
            if (chanceDeCompleto[compras] >= chance) {
                return compras;
            }
        }
        return -1;
    }

    /** A mediana: o numero de compras em que a chance passa de metade. */
    public int mediana() {
        return comprasPara(0.5);
    }

    /**
     * A esperanca, somada a partir da distribuicao.
     *
     * <p>Serve para conferir a formula fechada: os dois caminhos sao
     * independentes e precisam dar o mesmo numero.
     */
    public double esperado() {
        double soma = 0;
        for (int compras = 0; compras < chanceDeCompleto.length - 1; compras++) {
            soma += 1 - chanceDeCompleto[compras];
        }
        return soma;
    }
}
