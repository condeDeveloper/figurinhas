package br.com.conde.figurinhas;

import java.util.Locale;

/**
 * As tabelas do README. Uso:
 * {@code mvn exec:java -Dexec.args="album"} (ou {@code ultima}, {@code sorte}).
 */
public final class Medidor {

    private Medidor() {
    }

    public static void main(String[] args) {
        String tabela = args.length > 0 ? args[0] : "tudo";
        boolean tudo = tabela.equals("tudo");
        if (tudo || tabela.equals("album")) {
            album();
        }
        if (tudo || tabela.equals("ultima")) {
            ultima();
        }
        if (tudo || tabela.equals("sorte")) {
            sorte();
        }
    }

    /** Quantas figurinhas para completar, por tamanho de album. */
    public static void album() {
        System.out.println();
        System.out.println("ALBUM  (quantas figurinhas para completar)");
        System.out.printf(Locale.ROOT, "%-10s %12s %12s %12s %12s%n",
                "figurinhas", "media", "mediana", "media/n", "pela DP");
        for (int n : new int[]{10, 20, 50, 100, 200, 400, 670}) {
            Album album = Album.de(n);
            int teto = (int) Math.ceil(album.esperado() * 5) + 100;
            Distribuicao distribuicao = new Distribuicao(album, teto);
            System.out.printf(Locale.ROOT, "%-10d %12.1f %12d %12.2f %12.1f%n",
                    n, album.esperado(), distribuicao.mediana(),
                    album.esperado() / n, distribuicao.esperado());
        }
        System.out.println();
        System.out.println("a media e a conta fechada n*H(n); a ultima coluna sai da");
        System.out.println("distribuicao inteira, por caminho independente. Elas batem.");
        System.out.println();
    }

    /** O preco de cada pedaco do album, e o da ultima figurinha. */
    public static void ultima() {
        System.out.println();
        System.out.println("A ULTIMA  (quanto custa cada pedaco do album, com 670 figurinhas)");
        Album album = Album.de(670);
        System.out.printf(Locale.ROOT, "%-20s %12s %12s%n", "pedaco", "figurinhas", "fatia");
        int[] marcos = {0, 335, 603, 637, 663, 669};
        String[] nomes = {"as primeiras 50%", "de 50% a 90%", "de 90% a 95%",
                "de 95% a 99%", "de 99% a penultima", "SO A ULTIMA"};
        int[] fins = {335, 603, 637, 663, 669, 670};
        double total = album.esperado();
        for (int i = 0; i < marcos.length; i++) {
            double custo = album.esperadoDaqui(marcos[i]) - album.esperadoDaqui(fins[i]);
            System.out.printf(Locale.ROOT, "%-20s %12.1f %11.1f%%%n",
                    nomes[i], custo, 100 * custo / total);
        }
        System.out.println();
        System.out.printf(Locale.ROOT, "  total: %.1f figurinhas%n", total);
        System.out.println();
        System.out.println("por tamanho de album, o peso da ultima figurinha sozinha:");
        for (int n : new int[]{10, 50, 100, 670}) {
            Album outro = Album.de(n);
            System.out.printf(Locale.ROOT, "  %4d figurinhas: a ultima custa %5.0f (%4.1f%% do total)%n",
                    n, outro.esperadoParaAProxima(n - 1),
                    100 * outro.esperadoParaAProxima(n - 1) / outro.esperado());
        }
        System.out.println();
    }

    /** O que muda com pacote de varias figurinhas, e quanta sorte faz diferenca. */
    public static void sorte() {
        System.out.println();
        System.out.println("PACOTES  (figurinhas distintas dentro do mesmo pacote)");
        System.out.printf(Locale.ROOT, "%-10s %12s %14s %14s %12s%n",
                "por pacote", "pacotes", "figurinhas", "uma a uma", "economia");
        Album album = Album.de(100);
        double umaAUma = album.esperado();
        for (int porPacote : new int[]{1, 2, 5, 10, 20}) {
            double pacotes = new Pacote(album, porPacote).pacotesEsperados();
            double figurinhas = pacotes * porPacote;
            System.out.printf(Locale.ROOT, "%-10d %12.1f %14.1f %14.1f %11.1f%%%n",
                    porPacote, pacotes, figurinhas, umaAUma,
                    100 * (1 - figurinhas / umaAUma));
        }
        System.out.println();
        System.out.println("SORTE  (album de 100, quantas figurinhas por nivel de confianca)");
        Distribuicao distribuicao = new Distribuicao(album, 3000);
        for (double chance : new double[]{0.10, 0.25, 0.50, 0.75, 0.90, 0.99}) {
            System.out.printf(Locale.ROOT, "  %4.0f%% de chance: %5d figurinhas%n",
                    100 * chance, distribuicao.comprasPara(chance));
        }
        System.out.printf(Locale.ROOT, "  a media fica em %.0f%n", album.esperado());
        System.out.println();
    }
}
