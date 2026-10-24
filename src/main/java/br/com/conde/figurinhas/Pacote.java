package br.com.conde.figurinhas;

/**
 * Figurinhas vendidas em pacotes, com as do mesmo pacote sempre diferentes entre
 * si.
 *
 * <p>Isso muda a conta, e nao e so dividir por cinco. Um pacote de cinco
 * figurinhas distintas traz mais informacao nova no comeco, quando quase tudo e
 * novidade, e no fim ele vale quase tanto quanto uma figurinha so, porque das
 * cinco quase sempre ja se tem quatro.
 *
 * <p>A programacao dinamica aqui e a mesma de {@link Distribuicao}, trocando a
 * transicao: em vez de "ganhei uma nova ou repetida", e "das {@code k} do
 * pacote, quantas eram novas", que sai de uma hipergeometrica.
 */
public final class Pacote {

    private final Album album;
    private final int porPacote;

    public Pacote(Album album, int porPacote) {
        if (porPacote < 1 || porPacote > album.figurinhas()) {
            throw new IllegalArgumentException(
                    "pacote de " + porPacote + " nao cabe num album de " + album.figurinhas());
        }
        this.album = album;
        this.porPacote = porPacote;
    }

    public int porPacote() {
        return porPacote;
    }

    /** Quantos PACOTES, em media, para completar o album. */
    public double pacotesEsperados() {
        int n = album.figurinhas();
        double[] tenho = new double[n + 1];
        tenho[0] = 1;
        double soma = 0;
        for (int pacotes = 0; pacotes < tetoDeSeguranca(); pacotes++) {
            soma += 1 - tenho[n];
            tenho = abrirUmPacote(tenho);
        }
        return soma;
    }

    /** A chance de ter o album completo depois de {@code pacotes} pacotes. */
    public double completoEm(int pacotes) {
        int n = album.figurinhas();
        double[] tenho = new double[n + 1];
        tenho[0] = 1;
        for (int i = 0; i < pacotes; i++) {
            tenho = abrirUmPacote(tenho);
        }
        return tenho[n];
    }

    /**
     * Uma passada da programacao dinamica: dado quantas figurinhas distintas se
     * tem, quantas novas vem no proximo pacote.
     */
    private double[] abrirUmPacote(double[] tenho) {
        int n = album.figurinhas();
        double[] depois = new double[n + 1];
        for (int k = 0; k <= n; k++) {
            if (tenho[k] == 0) {
                continue;
            }
            int faltam = n - k;
            int maximo = Math.min(porPacote, faltam);
            for (int novas = 0; novas <= maximo; novas++) {
                double chance = hipergeometrica(n, faltam, porPacote, novas);
                if (chance > 0) {
                    depois[k + novas] += tenho[k] * chance;
                }
            }
        }
        return depois;
    }

    /**
     * A chance de, tirando {@code tirados} de {@code total} sem repor, virem
     * exatamente {@code queremos} dos {@code marcados} que interessam.
     *
     * <p>A conta e feita em logaritmo para nao estourar: com um album de
     * seiscentas figurinhas os binomiais passam de 10^100.
     */
    static double hipergeometrica(int total, int marcados, int tirados, int queremos) {
        int outros = total - marcados;
        if (queremos > marcados || tirados - queremos > outros || queremos < 0) {
            return 0;
        }
        double log = logBinomial(marcados, queremos)
                + logBinomial(outros, tirados - queremos)
                - logBinomial(total, tirados);
        return Math.exp(log);
    }

    private static double logBinomial(int n, int k) {
        if (k < 0 || k > n) {
            return Double.NEGATIVE_INFINITY;
        }
        double soma = 0;
        for (int i = 1; i <= k; i++) {
            soma += Math.log(n - k + i) - Math.log(i);
        }
        return soma;
    }

    private int tetoDeSeguranca() {
        return (int) Math.ceil(album.esperado() * 6 / porPacote) + 200;
    }
}
