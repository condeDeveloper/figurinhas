# figurinhas

O colecionador de figurinhas do zero em Java 21: quantas figurinhas para
completar um álbum, quanto custa a última, e por que comprar em pacote fechado
quase não ajuda.

A conta fechada é antiga e curta — quando se tem `k` figurinhas diferentes, a
próxima nova demora `n/(n−k)` compras, e o total é `n` vezes o número harmônico.
Aqui ela é usada como **juiz**: todo o resto sai de uma programação dinâmica
sobre a distribuição inteira, e os dois caminhos têm que concordar.

```
mvn -B -ntp verify
mvn -B -ntp exec:java -Dexec.args="album"   # (ou ultima, sorte)
```

## Quantas figurinhas para completar

| figurinhas | média | mediana | média por figurinha | pela distribuição |
|---:|---:|---:|---:|---:|
| 10 | 29,3 | 27 | 2,93 | 29,3 |
| 50 | 225,0 | 214 | 4,50 | 225,0 |
| 100 | 518,7 | 497 | 5,19 | 518,7 |
| 200 | 1.175,6 | 1.133 | 5,88 | 1.175,6 |
| 400 | 2.628,0 | 2.543 | 6,57 | 2.628,0 |
| **670** | **4.747,1** | **4.605** | **7,09** | 4.747,1 |

A última coluna sai da programação dinâmica, somando `1 − P(completou)` sobre
todas as compras; a primeira sai de `n·H(n)`. São dois caminhos independentes
para o mesmo número, e é assim que um erro de índice na recorrência apareceria.

Um álbum de 670 figurinhas custa, em média, **7,09 figurinhas por espaço**. Se
cada pacote traz 5 e custa R$ 5, são uns R$ 4.750 em figurinhas para um álbum de
R$ 10 — sem contar que a média não é o que acontece com você.

## A última figurinha custa 14% do álbum inteiro

Álbum de 670:

| pedaço | figurinhas | fatia |
|---|---:|---:|
| as primeiras 50% | 463,9 | 9,8% |
| de 50% a 90% | 1.074,3 | 22,6% |
| de 90% a 95% | 469,4 | 9,9% |
| de 95% a 99% | 1.002,3 | 21,1% |
| de 99% até a penúltima | 1.067,2 | 22,5% |
| **só a última** | **670,0** | **14,1%** |

**Metade do álbum custa menos de 10% do esforço.** A primeira figurinha é sempre
nova, a segunda quase sempre, e assim vai: as 335 primeiras saem em 464 compras.
Daí em diante, cada espaço vazio custa cada vez mais, e a última figurinha
sozinha custa exatamente `n` compras — 670 — que são 14,1% de tudo.

| tamanho do álbum | a última custa | fatia do total |
|---:|---:|---:|
| 10 | 10 | 34,1% |
| 50 | 50 | 22,2% |
| 100 | 100 | 19,3% |
| 670 | 670 | 14,1% |

Quanto menor o álbum, pior: num álbum de 10 figurinhas, **um terço de todo o
esforço é a última**.

## Pacote fechado quase não ajuda

Com as figurinhas do mesmo pacote garantidamente diferentes entre si (álbum de
100):

| por pacote | pacotes | figurinhas | uma a uma | economia |
|---:|---:|---:|---:|---:|
| 1 | 518,7 | 518,7 | 518,7 | 0,0% |
| 2 | 258,3 | 516,6 | 518,7 | 0,4% |
| **5** | 102,1 | **510,3** | 518,7 | **1,6%** |
| 10 | 49,9 | 499,4 | 518,7 | 3,7% |
| 20 | 23,9 | 477,0 | 518,7 | 8,0% |

Um pacote de cinco figurinhas distintas economiza **1,6%**. Parece pouco demais,
e é: a garantia só vale no começo, quando quase tudo é novidade. No fim, das
cinco figurinhas do pacote você já tem quatro — e o fato de serem distintas
entre si não muda nada, porque o problema é serem distintas **do que já está no
álbum**.

## Média não é o que acontece com você

Álbum de 100 figurinhas, média 519:

| chance de já ter completado | figurinhas |
|---:|---:|
| 10% | 381 |
| 25% | 430 |
| 50% | **497** |
| 75% | 584 |
| 90% | 683 |
| 99% | 916 |

A mediana (497) fica **abaixo** da média (519), e a cauda é longa: o azarado de
1% paga 916 figurinhas, duas vezes e meia o que o sortudo de 10% pagou. Dizer
"em média são 519" descreve mal a experiência de quase todo mundo.

## O que as medidas me corrigiram

1. **"Comprar em pacote de cinco ajuda bastante."** Economiza 1,6%. A garantia
   de não repetir dentro do pacote vale quase nada perto da repetição contra o
   álbum.
2. **"O álbum é caro porque são muitas figurinhas."** É caro por causa do fim:
   metade dele custa 9,8% do esforço, e só a última custa 14,1%.
3. **"A média resume o problema."** A mediana é menor que a média em todo
   tamanho de álbum, e entre o percentil 10 e o 99 há um fator 2,4.
4. **"A programação dinâmica é monótona."** Matematicamente sim; em ponto
   flutuante ela anda para trás na 15ª casa decimal quando a probabilidade já
   está perto de 1. O teste mede esse desvio em vez de exigir monotonia exata,
   que seria exigir aritmética exata.

## Como está organizado

- `Album`: a conta fechada, com o harmônico somado do menor termo para o maior
  (ao contrário, os termos pequenos somem no arredondamento).
- `Distribuicao`: a programação dinâmica sobre "quantas eu já tenho". A fórmula
  por inclusão e exclusão existe e **não** foi usada: com algumas centenas de
  figurinhas os termos passam de 10^100 e se cancelam até sobrar um número
  pequeno, e o resultado em ponto flutuante vira lixo.
- `Pacote`: a mesma recorrência com a transição hipergeométrica, calculada em
  logaritmo para não estourar.
- `Medidor`: as três tabelas acima, rodando no CI nos três sistemas.

31 testes.
