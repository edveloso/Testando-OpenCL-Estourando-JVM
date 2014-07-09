package Control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Carlos / Andre
 */
public class InteracaoJava {

    private final int interacao;
    private final long tempoInicial;
    private long tempoFinal;

    public InteracaoJava(int interacao) {
        this.tempoInicial = System.currentTimeMillis();
        this.interacao = interacao;
        executaArray();
    }

    public long retornaTempo() {
        return (this.tempoFinal - this.tempoInicial);
    }

    private void executaArray() {
        int n = this.interacao;
        float srcArrayA[] = new float[n];
        float srcArrayB[] = new float[n];
        float dstArray[] = new float[n];

        // atribuir valor para as posições
        for (int i = 0; i < n; i++) {
            srcArrayA[i] = i;
            srcArrayB[i] = i;
        }

        for (int i = 0; i < n; i++) {
            dstArray[i] = srcArrayA[i] * srcArrayB[i];
        }
        this.tempoFinal = System.currentTimeMillis();
        System.out.println(retornaTempo() + " milissegundos Java ");
        dstArray = null;
        srcArrayA = null;
        srcArrayB = null;
        File arquivo = new File("c:\\AppsPrjFinal\\ESTOUROJVMJAVA.txt");
        try (FileWriter fw = new FileWriter(arquivo)) {
            fw.write(Long.toString(retornaTempo()));
            fw.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
